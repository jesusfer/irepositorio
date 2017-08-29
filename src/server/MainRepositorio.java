package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Scanner;

import repositorio.ArchivoDetalles;
import repositorio.Coincidencia;
import repositorio.IRepositorio;

import middleware.JavaORB;
import middleware.Middleware;

public class MainRepositorio {
	static RepositorioThread repoThread;

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		// /////////////////////////////////
		// Inicializar el repositorio
		// /////////////////////////////////

		File directorioConfiguracion = new File(args.length == 1 ? args[0] : "");
		if (!directorioConfiguracion.exists()) {
			errorFatal("Config dir doesn't exist! (" + directorioConfiguracion.getAbsolutePath() + ")");
		}

		// System.out.println("Config dir: " +
		// directorioConfiguracion.getAbsolutePath());

		// Leer archivos de configuracion sencillos
		String cfgPadre = leeCadenaConfiguracion("RepositorioPadre.cfg", directorioConfiguracion);
		String cfgRaiz = leeCadenaConfiguracion("RepositorioRaiz.cfg", directorioConfiguracion);
		String cfgNombre = leeCadenaConfiguracion("NombreRepositorio.cfg", directorioConfiguracion);

		// System.out.println("Repo raiz: " + cfgRaiz);
		System.out.println("Repo nombre: " + cfgNombre);
		// System.out.println("Repo padre: " + cfgPadre);

		IndiceBusqueda indice = cargarIndice("ListaArchivos.cfg", directorioConfiguracion);

		repoThread = new RepositorioThread(cfgNombre, cfgPadre, cfgRaiz, indice);
		repoThread.start();

		// Esperar a que el hilo inicialice...
		Thread.sleep(1000);
		verMenuPrincipal();
	}

	private static String leeCadenaConfiguracion(String nombre, File directorio) {
		String contents = null;
		File archivo = null;
		try {
			archivo = new File(directorio, nombre);
			if (!archivo.exists()) {
				errorFatal(nombre + " no encontrado!");
			}
			contents = new Scanner(archivo, "UTF-8").nextLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			errorFatal(archivo.getName() + " no se puede encontrar?!");
		}
		return contents;
	}

	private static IndiceBusqueda cargarIndice(String nombre, File directorio) {
		IndiceBusqueda indice = new IndiceBusqueda();
		File archivo = new File(directorio, nombre);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(archivo));
			String line = null;
			String ls = System.getProperty("line.separator");
			try {
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.length() == 0 || line.startsWith("#"))
						continue;
					indice.nuevoDocumento(procesarLineaIndice(line));
				}
			} finally {
				reader.close();
			}

		} catch (FileNotFoundException e1) {
			errorFatal(nombre + " no encontrado!");
		} catch (IOException e) {
			e.printStackTrace();
			errorFatal(e.getMessage());
		}
		// Consola.Mensaje("Cargados " + indice.size() +
		// " documentos en el indice");
		return indice;
	}

	public static void verMenuPrincipal() {
		while (true) {
			System.out.println();
			System.out.println("1. Buscar documentos en este repositorio (y subordinados)");
			System.out.println("2. Buscar documentos globalmente");
			System.out.println("3. Descargar documento");
			System.out.println("4. Listar documentos en el repositorio local");
			System.out.println("5. Insertar documento en el repositorio local");
			System.out.println("6. Eliminar documento en el repositorio local");
			System.out.println("9. Exit");
			System.out.print("> ");

			Scanner entrada = new Scanner(System.in);
			try {

				switch (entrada.nextInt()) {
				case 1:
					hacerBusqueda();
					break;
				case 2:
					hacerBusquedaGlobal();
					break;
				case 4:
					listarRepositorioLocal();
					break;
				case 9:
					repoThread.detener();
					Middleware.detener();
					// repoThread.interrupt();
					System.exit(0);
					break;
				}
			} catch (java.util.InputMismatchException ex) {
			}
		}
	}

	private static void listarRepositorioLocal() {
		imprimirCoincidencias(repoThread.getIndiceBusqueda().buscar());
	}

	private static void hacerBusquedaGlobal() {
		Scanner in = new Scanner(System.in);
		System.out.print("Palabra? ");
		String palabra = in.nextLine();
		try {
			Coincidencia[] cx = repoThread.getRepositorioRaiz().buscar(palabra);
			imprimirCoincidencias(cx);
		} catch (Exception ex) {
			System.err.format("Error buscando globalmente");
		}
	}

	private static void hacerBusqueda() {
		Scanner in = new Scanner(System.in);
		System.out.print("Palabra? ");
		String palabra = in.nextLine();
		Coincidencia[] cx = repoThread.getRepositorio().buscar(palabra);
		imprimirCoincidencias(cx);
	}

	private static void imprimirCoincidencias(Coincidencia[] coincidencias) {
		String format = "%-20s%-20s%-16s%-25s%-25s%n";
		System.out.format("%nArchivos encontrados (%d)%n%n", coincidencias.length);
		if (coincidencias.length > 0) {
			System.out.format(format, "Repositorio", "Nombre", "Archivo", "Palabras clave", "Comentario");
			System.out.format(format, "-----------", "------", "-------", "--------------", "----------");
			for (Coincidencia c : coincidencias) {
				System.out.format(format, c.repositorio.nombre(), c.nombre, c.archivo, join(",",
						c.palabrasClave), c.comentario);
			}
		}
	}

	private static String join(String caracter, String[] cadenas) {
		StringBuilder sb = new StringBuilder();
		if (cadenas.length > 0) {
			sb.append(cadenas[0]);
			for (int i = 1; i < cadenas.length; i++) {
				sb.append(caracter);
				sb.append(cadenas[i]);
			}
		}
		return sb.toString();
	}

	/**
	 * El formato de la linea debe ser cadenas quoteadas separadas por comas:
	 * 
	 * valor1,valor2,valor3
	 * 
	 * Los valores deben estar URLEncoded en UTF-8.
	 * */
	private static ArchivoDetalles procesarLineaIndice(String line) {
		ArchivoDetalles detalles = null;
		String[] parts = line.split(",");
		if (parts.length != 5) {
			errorFatal("Linea de indice incorrecta");
		}
		try {
			String _nombre = URLDecoder.decode(parts[0], "UTF-8");
			String _archivo = URLDecoder.decode(parts[1], "UTF-8");
			String _directorio = URLDecoder.decode(parts[2], "UTF-8");
			String[] _palabrasClave = URLDecoder.decode(parts[3], "UTF-8").split("::");
			String _comentario = URLDecoder.decode(parts[4], "UTF-8");
			detalles = new ArchivoDetalles(_nombre, _archivo, _directorio, _palabrasClave, _comentario);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return detalles;
	}

	public static void errorFatal(String msg) {
		System.err.println(msg);
		System.err.println("Exiting...");
		System.exit(1);
	}
}
