package servidor.busqueda;

import repositorio.Coincidencia;
import repositorio.IRepositorio;

public class BusquedaThread extends Thread implements IBusquedaThread {
	private IRepositorio repositorio;
	private String palabra;
	private Coincidencia[] resultados;

	public BusquedaThread(IRepositorio origen, String palabra) {
		this.repositorio = origen;
		this.palabra = palabra;
		start();
	}

	public Coincidencia[] getResultados() {
		return resultados;
	}

	@Override
	public void run() {
		// Consola.Mensaje("Buscando en " + repositorio.nombre());
		resultados = this.repositorio.buscar(palabra);
	}

}
