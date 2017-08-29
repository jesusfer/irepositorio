package middleware;

import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

import java.lang.reflect.*;
import java.util.*;

public class JavaORB implements IMiddleware {
	public String opcionesLC[]; // Opciones a pasar al ORB recibidas por la
	// línea de comandos
	public Properties opcionesProp; // Opciones a pasar al ORB como lista de
	// propiedades
	public String nombreSN = ""; // Nombre que se usa como raíz en Servicio de
	// Nombres
	public String kindSN = ""; // Tipo (extensión) de dicho nombre

	private org.omg.CORBA.ORB orb; // Referencia a ORB
	private org.omg.PortableServer.POA poa; // Referencia a POA
	private String _nombreSN, _kindSN; // Valores de nombreSN y kindSN hechos
	// fijos tras inicialización
	private NamingContext raizAplicacion; // Referencia a contexto raíz del

	// Naming Service usado por
	// aplicación

	/**
	 * inicializar_servicio_nombres
	 * 
	 * Inicializa el servicio de nombres, en caso de no estarlo. Crea si hace
	 * falta el contexto raíz de la aplicación.
	 */
	private void inicializar_servicio_nombres() {

		NamingContext raizSN = null;
		NameComponent contexto, nc[] = new NameComponent[1];

		try {
			org.omg.CORBA.Object servnombres = orb.resolve_initial_references("NameService");
			raizSN = NamingContextHelper.narrow(servnombres);
			contexto = new NameComponent(_nombreSN, _kindSN);
			nc[0] = contexto;
			org.omg.CORBA.Object objctx = raizSN.resolve(nc);
			// Si ya está creado el contexto raiz de la aplicacion, no se
			// producirá la excepción NotFound:
			raizAplicacion = NamingContextHelper.narrow(objctx);
		} catch (NotFound e) // Si no está creado el contexto raíz de la
		// aplicación, se produce esta excepción
		{
			raizAplicacion = raizSN.new_context();
			try {
				raizSN.rebind_context(nc, raizAplicacion);
			} catch (CannotProceed ex) {
				ex.printStackTrace();
			} catch (NotFound ex) {
				e.printStackTrace();
			} catch (org.omg.CosNaming.NamingContextPackage.InvalidName ex) {
				ex.printStackTrace();
			}
		} catch (org.omg.CORBA.ORBPackage.InvalidName e) {
			e.printStackTrace();
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		}

	}

	private org.omg.CORBA.Object narrow(org.omg.CORBA.Object objeto, String nombre_interfaz) {
		org.omg.CORBA.Object resultado = null;

		try {
			String nombre_clase = nombre_interfaz + "Helper"; // Construimos el
			// nombre de la
			// clase Helper
			Class helper = Class.forName(nombre_clase); // Y construimos la
			// llamada al método
			// narrow
			Class[] tipos_parametros = { org.omg.CORBA.Object.class };
			Method narrow = helper.getMethod("narrow", tipos_parametros);
			org.omg.CORBA.Object args[] = { objeto };
			resultado = (org.omg.CORBA.Object) narrow.invoke(null, (Object[]) args);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return resultado;
	}

	public void inicializar() {
		// Inicializar ORB usando las opciones.
		orb = org.omg.CORBA.ORB.init(opcionesLC, opcionesProp);
		_nombreSN = nombreSN;
		_kindSN = kindSN;

		// Inicializar POA
		try {
			poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			poa.the_POAManager().activate();
		} catch (AdapterInactive e) {
			e.printStackTrace();
		} catch (org.omg.CORBA.ORBPackage.InvalidName e) {
			e.printStackTrace();
		}
	}

	public void detener() {
		orb.shutdown(true);
		orb.destroy();
	}

	public void esperar() {
		orb.run();
	}

	public Object registrar(java.lang.Object sirviente, String nombre_interfaz) {
		org.omg.CORBA.Object resultado = null;

		try {
			org.omg.CORBA.Object obj = poa.servant_to_reference((Servant) sirviente);
			resultado = (org.omg.CORBA.Object) narrow(obj, nombre_interfaz);
		} catch (ServantNotActive e) {
			e.printStackTrace();
		} catch (WrongPolicy e) {
			e.printStackTrace();
		}

		return resultado;
	}

	public void desregistrar(java.lang.Object objeto) {
		try {
			poa.deactivate_object(((Servant) objeto)._object_id());
		} catch (ObjectNotActive e) {
			e.printStackTrace();
		} catch (WrongPolicy e) {
			e.printStackTrace();
		}
	}

	public void nombrar(java.lang.Object objeto, String nombre) {
		// Si no se ha obtenido aún la referencia al contexto raíz de la
		// aplicación, obténgase ahora:
		if (raizAplicacion == null) {
			inicializar_servicio_nombres();
		}

		// En cualquier caso raizAplicación ya apunta al contexto de la
		// aplicación. Creamos en este
		// el nombre que se pasa como argumento:
		try {
			NameComponent nuevo_nombre = new NameComponent(nombre, "");
			NameComponent[] nn = { nuevo_nombre };
			raizAplicacion.rebind(nn, (org.omg.CORBA.Object) objeto);
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		}
	}

	public java.lang.Object localizar(String nombre_objeto, String nombre_clase) {
		org.omg.CORBA.Object resultado = null;

		if (raizAplicacion == null) // Si no se ha inicializado el servicio de
			// nombres...
			inicializar_servicio_nombres();

		try {
			NameComponent nombre = new NameComponent(nombre_objeto, "");
			NameComponent[] nn = { nombre };
			org.omg.CORBA.Object obj = raizAplicacion.resolve(nn);

			if (obj != null)
				resultado = narrow(obj, nombre_clase);

		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NotFound e) {
			e.printStackTrace();
		}

		return resultado;
	}

}
