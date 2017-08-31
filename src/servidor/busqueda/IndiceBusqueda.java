package servidor.busqueda;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

	public int size() {
		return indice.size();
	}

	/**
	 * Devolver todos los archivos del indice.
	 */
	public Coincidencia[] buscar() {
		ArrayList<Coincidencia> resultados = new ArrayList<Coincidencia>();

		for (ArchivoDetalles det : indice.values()) {
			resultados.add(new Coincidencia(repositorio.nombre(), repositorio, det.nombre, det.archivo,
					det.palabrasClave, det.comentario));
		}

		return resultados.toArray(new Coincidencia[0]);
	}

	/**
	 * Divide la consulta en varias palabras y hace una búsqueda por cada una.
	 */
	public Coincidencia[] buscar(String consulta) {
		// En realidad si se ponen varias palabras deberíamos tener resultados
		// de todas
		ArrayList<String> palabras = new ArrayList<String>();
		if (consulta.indexOf(" ") != -1) {
			palabras.add(consulta);
		}
		for (String p : consulta.split(" ")) {
			palabras.add(p.trim().toLowerCase());
		}
		ArrayList<Coincidencia> resultados = new ArrayList<Coincidencia>();

		for (String p : palabras) {
			ArrayList<Coincidencia> temp = buscarPalabra(p);
			for (Coincidencia c : temp) {
				if (!existeCoincidencia(resultados, c)) {
					resultados.add(c);
				}
			}
		}

		return resultados.toArray(new Coincidencia[0]);
	}

	/**
	 * Buscar si una palabra clave está en el índice.
	 */
	private ArrayList<Coincidencia> buscarPalabra(String palabra) {
		ArrayList<Coincidencia> resultados = new ArrayList<Coincidencia>();

		for (ArchivoDetalles det : indice.values()) {
			for (int i = 0; i < det.palabrasClave.length; i++) {
				if (det.palabrasClave[i].trim().toLowerCase().equals(palabra)) {
					resultados.add(new Coincidencia(repositorio.nombre(), repositorio, det.nombre, det.archivo,
							det.palabrasClave, det.comentario));
				}
			}
		}
		return resultados;
	}

	/**
	 * ¿Existe la coincidencia en la lista?
	 */
	private boolean existeCoincidencia(ArrayList<Coincidencia> resultados, Coincidencia c) {
		boolean resultado = false;
		for (Coincidencia r : resultados) {
			if (r.nombre.equals(c.nombre)) {
				resultado = true;
				break;
			}
		}
		return resultado;
	}

	/**
	 * Devuelve el objeto ArchivoDetalles del documento con nombre nombre
	 */
	public ArchivoDetalles buscarDetalles(String nombre) throws ArchivoNoEncontradoException {
		if (!indice.containsKey(nombre)) {
			throw new ArchivoNoEncontradoException("Archivo no encontrado");
		}
		return indice.get(nombre);
	}

	public void nuevoDocumento(ArchivoDetalles detalles) {
		indice.put(detalles.nombre, detalles);
		// Consola.Mensaje("Indice::nuevoDocumento: " + detalles.nombre);
	}

	public void borrarDocumento(String nombre) {
		indice.remove(nombre);
	}

	public void guardarEnDisco() {
		String directorio = Main.getDirectorioConfiguracion().getAbsolutePath();
		File archivoLista = new File(directorio, Constantes.ARCHIVO_INDICE);
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
	 * Cada línea debe tener el siguiente formato
	 * nombre,archivo,directorio,palabrasClave,comentario
	 */
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

	/**
	 * El formato de la línea debe ser cadenas quoteadas separadas por comas:
	 * 
	 * valor1,valor2,valor3
	 * 
	 * Cada linea debe tener el siguiente formato
	 * nombre,archivo,directorio,palabrasClave,comentario
	 * 
	 * - nombre debe ser unico para cada archivo - Las palabras clave deben estar
	 * separadas por ::
	 * 
	 * Los valores deben estar URLEncoded en UTF-8.
	 */
	public static ArchivoDetalles StringToDetalles(String linea) {
		ArchivoDetalles detalles = null;
		String[] parts = linea.split(",");
		// Pueden ser 4 si no hay comentario, o 5 si lo hay
		if (parts.length != 4 && parts.length != 5) {
			Main.errorFatal("Linea de indice incorrecta");
		}
		try {
			String nombre = URLDecoder.decode(parts[0], "UTF-8");
			String archivo = URLDecoder.decode(parts[1], "UTF-8");
			String directorio = URLDecoder.decode(parts[2], "UTF-8");
			String[] palabrasClave = URLDecoder.decode(parts[3], "UTF-8").split("::");
			String comentario;
			comentario = URLDecoder.decode(parts[4], "UTF-8");
			detalles = new ArchivoDetalles(nombre, archivo, directorio, palabrasClave, comentario);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return detalles;
	}
}
