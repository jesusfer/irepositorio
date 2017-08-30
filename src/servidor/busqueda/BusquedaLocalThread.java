package servidor.busqueda;

import repositorio.Coincidencia;

public class BusquedaLocalThread extends Thread implements IBusquedaThread {
	private IndiceBusqueda indice;
	private String palabra;
	private Coincidencia[] resultados;

	public BusquedaLocalThread(IndiceBusqueda origen, String palabra) {
		this.indice = origen;
		this.palabra = palabra;
		start();
	}

	@Override
	public void run() {
		resultados = this.indice.buscar(palabra);
	}

	public Coincidencia[] getResultados() {
		return resultados;
	}
}
