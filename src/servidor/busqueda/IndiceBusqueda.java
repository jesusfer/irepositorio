package servidor.busqueda;

import java.util.ArrayList;
import java.util.HashMap;

import repositorio.ArchivoDetalles;
import repositorio.ArchivoNoEncontradoException;
import repositorio.Coincidencia;
import repositorio.IRepositorio;

public class IndiceBusqueda {
	private HashMap<String, ArchivoDetalles> indice;
	private IRepositorio repositorio;

	public IndiceBusqueda() {
		indice = new HashMap<String, ArchivoDetalles>();
	}

	public void setRepositorio(IRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	public void nuevoDocumento(ArchivoDetalles detalles) {
		indice.put(detalles.nombre, detalles);
		// Consola.Mensaje("Indice::nuevoDocumento: " + detalles.nombre);
	}

	public int size() {
		return indice.size();
	}

	/**
	 * Devolver todos los archivos del indice.
	 * */
	public Coincidencia[] buscar() {
		ArrayList<Coincidencia> results = new ArrayList<Coincidencia>();

		for (ArchivoDetalles det : indice.values()) {
			results.add(new Coincidencia(repositorio.nombre(), repositorio, det.nombre, det.archivo,
					det.palabrasClave, det.comentario));
		}

		return results.toArray(new Coincidencia[0]);
	}

	/**
	 * Buscar si una palabra clave está en el índice.
	 * */
	public Coincidencia[] buscar(String palabra) {
		ArrayList<Coincidencia> results = new ArrayList<Coincidencia>();

		for (ArchivoDetalles det : indice.values()) {
			for (int i = 0; i < det.palabrasClave.length; i++) {
				if (det.palabrasClave[i].equals(palabra)) {
					results.add(new Coincidencia(repositorio.nombre(), repositorio, det.nombre, det.archivo,
							det.palabrasClave, det.comentario));
				}
			}
		}

		return results.toArray(new Coincidencia[0]);
	}

	public ArchivoDetalles detallesArchivo(String nombre) throws ArchivoNoEncontradoException {
		if (!indice.containsKey(nombre)) {
			throw new ArchivoNoEncontradoException("Archivo no encontrado");
		}
		return indice.get(nombre);
	}
}
