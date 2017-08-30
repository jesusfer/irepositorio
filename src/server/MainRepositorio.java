package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import repositorio.ArchivoDetalles;
import repositorio.ArchivoNoEncontradoException;
import repositorio.Coincidencia;
import repositorio.IRepositorio;
import repositorio.ITransferencia;

import middleware.JavaORB;
import middleware.Middleware;

public class MainRepositorio {
	private static RepositorioThread repoThread;
	protected static Lock loadLock = new ReentrantLock();

	private static Coincidencia[] ultimaBusqueda;

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
		// Ñapa para esperar hasta que el hilo servidor haya terminado de cargar
		Thread.sleep(1);
		MainRepositorio.loadLock.lock();
		MainRepositorio.loadLock.unlock();

		verMenuPrincipal();
	}

	private static String leeCadenaConfiguracion(String nombre, File directorio) {
		String contents = null;
		File archivo = null;
		try {
			archivo = new File(directorio.getAbsolutePath(), nombre);
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

	private static void verMenuPrincipal() {
		while (true) {
			System.out.println();
			System.out.format("%nMenú del repositorio%n%n");
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
				case 3:
					descargarDocumento();
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

	private static void descargarDocumento() {
		if (ultimaBusqueda == null || ultimaBusqueda.length == 0) {
			System.out.println("No se ha hecho una búsqueda o la última búsqueda no tuvo resultados.");
			return;
		}
		// Que el usuario elija un resultado de búsqueda...
		System.out.println("Ultima búsqueda realizada:");
		imprimirCoincidencias(ultimaBusqueda);

		Scanner in = new Scanner(System.in);
		System.out.println();
		System.out.print("# de resultado? ");
		int indice = in.nextInt() - 1;
		if (indice > ultimaBusqueda.length) {
			System.out.println("No es un resultado válido...");
		}
		Coincidencia resultado = ultimaBusqueda[indice];
		System.out.format("Iniciando descarga de '%s' desde '%s'%n", resultado.nombre, resultado.repositorio
				.nombre());

		// Pues nada, iniciar la descarga en el directorio actual

		FileOutputStream fos = null;
		int totalRecibidos = 0;
		try {
			// Iniciar la descarga
			ITransferencia transferencia = resultado.repositorio.iniciarDescarga(resultado.nombre);
			// Crear el archivo local e ir rellenándolo
			File dir = new File("");
			File salida = new File(dir.getAbsolutePath(), resultado.archivo);
			System.out.println("Descargando en: " + salida.getAbsolutePath());
			fos = new FileOutputStream(salida);
			byte[] leidos = transferencia.solicitarBloque();
			while (leidos.length > 0) {
				fos.write(leidos);
				totalRecibidos += leidos.length;
				leidos = transferencia.solicitarBloque();
				System.out.format("%-10s %12d\r", "Recibidos:", totalRecibidos);
				System.out.flush();
			}
		} catch (ArchivoNoEncontradoException e) {
			System.out.println("Error: " + e.message);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
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
			ultimaBusqueda = cx;
			imprimirCoincidencias(cx);
		} catch (Exception ex) {
			System.err.format("Error buscando globalmente");
		}
		volverAlMenu();
	}

	private static void hacerBusqueda() {
		Scanner in = new Scanner(System.in);
		System.out.print("Palabra? ");
		String palabra = in.nextLine();
		Coincidencia[] cx = repoThread.getRepositorio().buscar(palabra);
		ultimaBusqueda = cx;
		imprimirCoincidencias(cx);
		volverAlMenu();
	}

	private static void volverAlMenu() {
		System.out.println();
		System.out.println("Pulsa Enter para volver al menú...");
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}

	private static void imprimirCoincidencias(Coincidencia[] coincidencias) {
		String format = "%-3s%-20s%-20s%-16s%-25s%-25s%n";
		System.out.format("%nArchivos encontrados (%d)%n%n", coincidencias.length);
		if (coincidencias.length > 0) {
			System.out
					.format(format, "#", "Repositorio", "Nombre", "Archivo", "Palabras clave", "Comentario");
			System.out
					.format(format, "-", "-----------", "------", "-------", "--------------", "----------");
			for (int i = 0; i < coincidencias.length; i++) {
				Coincidencia c = coincidencias[i];
				System.out.format(format, i + 1, c.repositorio.nombre(), c.nombre, c.archivo, join(",",
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
