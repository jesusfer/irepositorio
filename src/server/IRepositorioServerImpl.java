package server;

import middleware.Middleware;
import repositorio.Coincidencia;
import repositorio.IRepositorio;

/**
 * This class is the implementation object for your IDL interface.
 * 
 * Let the Eclipse complete operations code by choosing 'Add unimplemented
 * methods'.
 */
public class IRepositorioServerImpl extends repositorio.IRepositorioPOA {
	String nombre = null;
	IRepositorio padre = null;

	public IRepositorioServerImpl() {
		super();
	}

	public String nombre() {
		return nombre;
	}

	public void nombre(String newNombre) {
		nombre = newNombre;
	}

	public IRepositorio padre() {
		if (padre == null)
			return (IRepositorio) this;
		return padre;
	}

	public void padre(IRepositorio newPadre) {
		padre = newPadre;
	}

	/**
	 * Obtiene la lista de repositorios que están subordinados de este.
	 * 
	 * La lista se obtiene del Name Server.
	 * */
	public IRepositorio[] subordinados() {
		Object[] hijos = Middleware.localizarHijos(nombre(), IRepositorio.CLASE);
		IRepositorio[] hijosRep = new IRepositorio[hijos.length - 1];
		for (int i = 0, j = 0; i < hijos.length; i++) {
			IRepositorio r = (IRepositorio) hijos[i];
			if (!r.nombre().equals(this.nombre())) {
				hijosRep[j] = r;
				j++;
			}
		}
		return hijosRep;
	}

	/**
	 * Da de baja un repositorio subordinado.
	 * 
	 * Solo quita la referencia del subordinado como hijo de este repositorio.
	 * No quita referencias en ORB o quita el nombre raiz del repositorio.
	 * */
	public void baja(String nombre) {
		String[] nombres = { nombre(), nombre };
		Middleware.desnombrarObjeto(nombres);
		Consola.Mensaje("Baja de subordinado: " + nombre);
		Consola.Mensaje("Subordinados cuenta: " + subordinados().length);
	}

	public Coincidencia[] buscar(String palabraClave) {
		// TODO Auto-generated method stub
		return null;
	}

	public void iniciarDescarga(String nombre) {
		// TODO Auto-generated method stub

	}

	/**
	 * Metodo que usará un repositorio subordinado para registrarse como hijo.
	 * 
	 * TODO Se podría también mantener la lista de hijos según los que se
	 * registren usando este método.
	 * */
	public String registrar(IRepositorio referencia) {
		return registrarConNombre(referencia, referencia.nombre());
	}

	/**
	 * Metodo que usará un repositorio subordinado para registrarse como hijo.
	 * 
	 * TODO Se podría también mantener la lista de hijos según los que se
	 * registren usando este método.
	 * */
	public String registrarConNombre(IRepositorio referencia, String nombre) {
		String[] ruta = { nombre(), nombre };
		Middleware.nombrarObjeto(referencia, ruta);
		IRepositorio registrado = (IRepositorio) Middleware.localizar(ruta, IRepositorio.CLASE);
		Consola.Mensaje("Nuevo subordinado: " + registrado.nombre());
		Consola.Mensaje("Subordinados cuenta: " + subordinados().length);
		return registrado.nombre();
	}

	public byte[] solicitarBloque(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}
}
