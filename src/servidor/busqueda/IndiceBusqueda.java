package servidor.busqueda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import repositorio.ArchivoDetalles;
import repositorio.ArchivoNoEncontradoException;
import repositorio.Coincidencia;
import repositorio.IRepositorio;
import servidor.Constantes;
import servidor.Main;
import utils.Strings;

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
		palabra = palabra.trim().toLowerCase();
		ArrayList<Coincidencia> results = new ArrayList<Coincidencia>();

		for (ArchivoDetalles det : indice.values()) {
			for (int i = 0; i < det.palabrasClave.length; i++) {
				if (det.palabrasClave[i].trim().toLowerCase().equals(palabra)) {
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

	public void guardarEnDisco() {
		String directorio = Main.getDirectorioConfiguracion().getAbsolutePath();
		File archivoLista = new File(directorio, Constantes.ArchivoIndice);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(archivoLista));
			for (ArchivoDetalles detalles : indice.values()) {
				String convertida = DetallesToString(detalles);
				bw.write(convertida);
				bw.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cada linea debe tener el siguiente formato
	 * nombre,archivo,directorio,palabrasClave,comentario
	 * */
	private String DetallesToString(ArchivoDetalles detalles) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(URLEncoder.encode(detalles.nombre, "UTF-8"));
			sb.append(',');
			sb.append(URLEncoder.encode(detalles.archivo, "UTF-8"));
			sb.append(',');
			sb.append(URLEncoder.encode(detalles.directorio, "UTF-8"));
			sb.append(',');
			sb.append(URLEncoder.encode(Strings.join("::", detalles.palabrasClave), "UTF-8"));
			sb.append(',');
			sb.append(URLEncoder.encode(detalles.comentario, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
