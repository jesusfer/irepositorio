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
	String nombre;
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

	public String[] subordinados() {
		// TODO Auto-generated method stub
		return null;
	}

	public void subordinados(String[] newSubordinados) {
		// TODO Auto-generated method stub

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
	}

	public Coincidencia[] buscar(String palabraClave) {
		// TODO Auto-generated method stub
		return null;
	}

	public void iniciarDescarga(String nombre) {
		// TODO Auto-generated method stub

	}

	public String registrar(IRepositorio referencia) {
		// TODO Auto-generated method stub
		return null;
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
		return registrado.nombre();
	}

	public byte[] solicitarBloque(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}
}
