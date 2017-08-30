package middleware;

import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.omg.PortableServer.Servant;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.logging.CORBALogDomains;

import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class JavaORB implements IMiddleware {
	public String opcionesLC[]; // Opciones a pasar al ORB recibidas por la
	// l�nea de comandos
	public Properties opcionesProp; // Opciones a pasar al ORB como lista de
	// propiedades
	public String nombreSN = ""; // Nombre que se usa como ra�z en Servicio de
	// Nombres
	public String kindSN = ""; // Tipo (extensi�n) de dicho nombre

	private org.omg.CORBA.ORB orb; // Referencia a ORB
	private org.omg.PortableServer.POA poa; // Referencia a POA
	private String _nombreSN, _kindSN; // Valores de nombreSN y kindSN hechos
	// fijos tras inicializaci�n
	private NamingContext raizAplicacion; // Referencia a contexto ra�z del

	// Naming Service usado por
	// aplicaci�n

	/**
	 * inicializar_servicio_nombres
	 * 
	 * Inicializa el servicio de nombres, en caso de no estarlo. Crea si hace
	 * falta el contexto ra�z de la aplicaci�n.
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
			// Si ya est� creado el contexto raiz de la aplicacion, no se
			// producir� la excepci�n NotFound:
			raizAplicacion = NamingContextHelper.narrow(objctx);
		} catch (NotFound e) // Si no est� creado el contexto ra�z de la
		// aplicaci�n, se produce esta excepci�n
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
			// llamada al m�todo
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

		// Para evitar que ORB escriba constantemente mensajes de error en la consola
		LogManager lm = LogManager.getLogManager();
		Logger l = lm.getLogger("javax.enterprise.resource.corba._DEFAULT_.rpc.transport");
		l.setLevel(Level.SEVERE);
		
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

	public void nombrarObjeto(java.lang.Object objeto, String nombre) {
		// Si no se ha obtenido a�n la referencia al contexto ra�z de la
		// aplicaci�n, obt�ngase ahora:
		if (raizAplicacion == null) {
			inicializar_servicio_nombres();
		}

		// En cualquier caso raizAplicaci�n ya apunta al contexto de la
		// aplicaci�n. Creamos en este
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

	public void nombrarObjeto(java.lang.Object objeto, String[] nombres) {
		// Si no se ha obtenido a�n la referencia al contexto ra�z de la
		// aplicaci�n, obt�ngase ahora:
		if (raizAplicacion == null) {
			inicializar_servicio_nombres();
		}

		// En cualquier caso raizAplicaci�n ya apunta al contexto de la
		// aplicaci�n. Creamos en este
		// el nombre que se pasa como argumento:
		try {
			ArrayList<NameComponent> cx = new ArrayList<NameComponent>();
			for (String nombre : nombres) {
				cx.add(new NameComponent(nombre, ""));
			}
			NameComponent[] nn = new NameComponent[cx.size()];
			cx.toArray(nn);
			raizAplicacion.rebind(nn, (org.omg.CORBA.Object) objeto);
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		}
	}

	public void desnombrarObjeto(String[] nombres) {
		// Si no se ha obtenido a�n la referencia al contexto ra�z de la
		// aplicaci�n, obt�ngase ahora:
		if (raizAplicacion == null) {
			inicializar_servicio_nombres();
		}

		// En cualquier caso raizAplicaci�n ya apunta al contexto de la
		// aplicaci�n. Creamos en este
		// el nombre que se pasa como argumento:
		try {
			ArrayList<NameComponent> cx = new ArrayList<NameComponent>();
			for (String nombre : nombres) {
				cx.add(new NameComponent(nombre, ""));
			}
			NameComponent[] nn = new NameComponent[cx.size()];
			cx.toArray(nn);
			raizAplicacion.unbind(nn);
		} catch (InvalidName e) {
			e.printStackTrace();
		} catch (NotFound e) {
			e.printStackTrace();
		} catch (CannotProceed e) {
			e.printStackTrace();
		}
	}

	public void nombrarContexto(String[] nombres) {
		// Si no se ha obtenido a�n la referencia al contexto ra�z de la
		// aplicaci�n, obt�ngase ahora:
		if (raizAplicacion == null) {
			inicializar_servicio_nombres();
		}

		// En cualquier caso raizAplicaci�n ya apunta al contexto de la
		// aplicaci�n. Creamos en este
		// el nombre que se pasa como argumento:
		try {
			ArrayList<NameComponent> cx = new ArrayList<NameComponent>();
			for (String nombre : nombres) {
				cx.add(new NameComponent(nombre, ""));
			}

			NameComponent[] nn = new NameComponent[cx.size()];
			cx.toArray(nn);
			NamingContext nuevo_ctx = raizAplicacion.new_context();
			raizAplicacion.rebind_context(nn, nuevo_ctx);
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
//			e.printStackTrace();
		}

		return resultado;
	}

	public Object localizar(String[] nombres, String nombre_clase) {
		org.omg.CORBA.Object resultado = null;

		if (raizAplicacion == null) // Si no se ha inicializado el servicio de
			// nombres...
			inicializar_servicio_nombres();

		try {
			ArrayList<NameComponent> cx = new ArrayList<NameComponent>();
			for (String nombre : nombres) {
				cx.add(new NameComponent(nombre, ""));
			}

			NameComponent[] nn = new NameComponent[cx.size()];
			cx.toArray(nn);
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
//			e.printStackTrace();
		}

		return resultado;
	}

	/**
	 * Devuelve una lista de los objetos de una clase que est�n en el contexto
	 * nombre_objeto.
	 * 
	 * nombre_objeto ser� el nombre del contexto de donde sacar los hijos. Para
	 * cada hijo, si es un objeto, se har� el casting a nombre_clase_hijos.
	 * */
	public Object[] localizarHijos(String nombre_objeto, String nombre_clase_hijos) {
		org.omg.CORBA.Object[] resultado = null;

		ArrayList<org.omg.CORBA.Object> _lista = new ArrayList<org.omg.CORBA.Object>();
		
		if (raizAplicacion == null) // Si no se ha inicializado el servicio de
			// nombres...
			inicializar_servicio_nombres();

		try {
			NameComponent nombre = new NameComponent(nombre_objeto, "");
			NameComponent[] nn = { nombre };
			org.omg.CORBA.Object obj = raizAplicacion.resolve(nn);
			NamingContext ctx = NamingContextHelper.narrow(obj);

			BindingListHolder bl = new BindingListHolder();
			BindingIteratorHolder bit = new BindingIteratorHolder();

			ctx.list(0, bl, bit);
			BindingHolder bh = new BindingHolder();
			while (bit.value.next_one(bh)) {
				Binding b = bh.value;
				if (b.binding_type.equals(BindingType.nobject)) {
					org.omg.CORBA.Object hijoCorba = ctx.resolve(b.binding_name);
					_lista.add(narrow(hijoCorba, nombre_clase_hijos));
				}
			}

			resultado = new org.omg.CORBA.Object[_lista.size()];
			_lista.toArray(resultado);
			
			// resultado = narrow(obj, nombre_clase);

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
