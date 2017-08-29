package server;

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
		return null;
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

	public void baja(String nombre) {
		// TODO Auto-generated method stub

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

	public String registrarConNombre(IRepositorio referencia, String nombre) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] solicitarBloque(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}
}
