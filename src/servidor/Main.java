package servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import middleware.Middleware;
import repositorio.ArchivoDetalles;
import repositorio.ArchivoNoEncontradoException;
import repositorio.Coincidencia;
import repositorio.ITransferencia;
import servidor.busqueda.IndiceBusqueda;
import servidor.repositorio.RepositorioThread;
import utils.Strings;

public class Main {
	private static RepositorioThread repoThread;
	public static Lock loadLock = new ReentrantLock();

	private static File directorioConfiguracion;
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

		directorioConfiguracion = new File(args.length == 1 ? args[0] : "");
		if (!directorioConfiguracion.exists()) {
			errorFatal("Config dir doesn't exist! (" + directorioConfiguracion.getAbsolutePath() + ")");
		}

		// p("Config dir: " +
		// directorioConfiguracion.getAbsolutePath());

		// Leer archivos de configuracion sencillos
		String cfgPadre = leeCadenaConfiguracion(Constantes.RepositorioPadre, directorioConfiguracion);
		String cfgRaiz = leeCadenaConfiguracion(Constantes.RepositorioRaiz, directorioConfiguracion);
		String cfgNombre = leeCadenaConfiguracion(Constantes.RepositorioNombre, directorioConfiguracion);

		// p("Repo raiz: " + cfgRaiz);
		p("Repo nombre: " + cfgNombre);
		// p("Repo padre: " + cfgPadre);

		IndiceBusqueda indice = cargarIndice(Constantes.ArchivoIndice, directorioConfiguracion);

		repoThread = new RepositorioThread(cfgNombre, cfgPadre, cfgRaiz, indice);
		repoThread.start();

		// Esperar a que el hilo inicialice...
		// Ñapa para esperar hasta que el hilo servidor haya terminado de cargar
		Thread.sleep(1);
		Main.loadLock.lock();
		Main.loadLock.unlock();

		menuPrincipal();
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
			String linea = null;
			try {
				while ((linea = reader.readLine()) != null) {
					linea = linea.trim();
					if (linea.length() == 0 || linea.startsWith("#"))
						continue;
					indice.nuevoDocumento(IndiceBusqueda.StringToDetalles(linea));
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

	private static void menuPrincipal() {
		while (true) {
			p();
			p("Menú del repositorio");
			p();
			p("1. Buscar documentos en este repositorio (y subordinados)");
			p("2. Buscar documentos globalmente");
			p("3. Descargar documento");
			p("4. Listar documentos en el repositorio local");
			p("5. Insertar documento en el repositorio local");
			p("6. Eliminar documento en el repositorio local");
			p("9. Exit");
			System.out.print("> ");

			Scanner entrada = new Scanner(System.in);
			try {

				switch (entrada.nextInt()) {
				case 1:
					menuBusquedaLocal();
					menuEsperarEntradaUsuario();
					break;
				case 2:
					menuBusquedaGlobal();
					menuEsperarEntradaUsuario();
					break;
				case 3:
					menuDescargarDocumento();
					break;
				case 4:
					menuListarRepositorioLocal();
					menuEsperarEntradaUsuario();
					break;
				case 5:
					menuInsertarDocumento();
					break;
				case 6:
					menuEliminarDocumento();
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

	private static void menuBusquedaLocal() {
		Scanner in = new Scanner(System.in);
		System.out.print("Palabra? ");
		String palabra = in.nextLine();
		Coincidencia[] cx = repoThread.getRepositorio().buscar(palabra);
		ultimaBusqueda = cx;
		menuImprimirCoincidencias(cx);
	}

	private static void menuBusquedaGlobal() {
		Scanner in = new Scanner(System.in);
		System.out.print("Palabra? ");
		String palabra = in.nextLine();
		try {
			Coincidencia[] cx = repoThread.getRepositorioRaiz().buscar(palabra);
			ultimaBusqueda = cx;
			menuImprimirCoincidencias(cx);
		} catch (Exception ex) {
			System.err.println("Error buscando globalmente");
		}
	}

	private static void menuDescargarDocumento() {
		if (ultimaBusqueda == null || ultimaBusqueda.length == 0) {
			p("No se ha hecho una búsqueda o la última búsqueda no tuvo resultados.");
			return;
		}
		// Que el usuario elija un resultado de búsqueda...
		p("Ultima búsqueda realizada:");
		menuImprimirCoincidencias(ultimaBusqueda);

		Scanner in = new Scanner(System.in);
		p();
		f("# de resultado? ");
		int indice = in.nextInt() - 1;
		if (indice > ultimaBusqueda.length) {
			p("No es un resultado válido...");
		}
		Coincidencia resultado = ultimaBusqueda[indice];
		f("Iniciando descarga de '%s' desde '%s'%n", resultado.nombre, resultado.repositorio.nombre());

		// Pues nada, iniciar la descarga en el directorio actual

		FileOutputStream fos = null;
		int totalRecibidos = 0;
		try {
			// Iniciar la descarga
			ITransferencia transferencia = resultado.repositorio.iniciarDescarga(resultado.nombre);
			// Crear el archivo local e ir rellenándolo
			File dir = new File("");
			File salida = new File(dir.getAbsolutePath(), resultado.archivo);
			p("Descargando en: " + salida.getAbsolutePath());

			fos = new FileOutputStream(salida);
			byte[] leidos = transferencia.solicitarBloque();
			while (leidos.length > 0) {
				fos.write(leidos);
				totalRecibidos += leidos.length;
				leidos = transferencia.solicitarBloque();
				f("%-10s %12d\r", "Recibidos:", totalRecibidos);
				System.out.flush();
			}
		} catch (ArchivoNoEncontradoException e) {
			p("Error: " + e.message);
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

	private static void menuListarRepositorioLocal() {
		Coincidencia[] todas = repoThread.getIndiceBusqueda().buscar();
		menuImprimirCoincidencias(todas);
	}

	private static void menuInsertarDocumento() {
		String[] preguntas = { "Nombre documento", "Nombre archivo", "Directorio", "Palabras clave (::)",
				"Comentario" };
		// Obtener detalles del nuevo archivo
		String[] respuestas = new String[preguntas.length];
		Scanner in = new Scanner(System.in);
		int index = 0;
		for (String mensaje : preguntas) {
			f("%-20s? ", mensaje);
			respuestas[index++] = in.nextLine();
		}
		// Revisar que todo esté bien y pedir confirmación
		// p("Nuevo documento:");
		// index = 0;
		// for (String mensaje : preguntas) {
		// f("%20s: %s", mensaje, respuestas[index++]);
		// }
		p();
		f("Guardar? (s/n)");
		boolean guardar = in.nextLine().equals("s");
		if (guardar) {
			ArchivoDetalles nuevo = new ArchivoDetalles( //
					respuestas[0], //
					respuestas[1], //
					respuestas[2], //
					respuestas[3].split("::"), //
					respuestas[4]);
			repoThread.getIndiceBusqueda().nuevoDocumento(nuevo);
			repoThread.getIndiceBusqueda().guardarEnDisco();
			f("Guardado (%s)", nuevo.nombre);
			p();
		} else {
			p("No se ha guardado");
		}
	}

	private static void menuEliminarDocumento() {
		Coincidencia[] todas = repoThread.getIndiceBusqueda().buscar();
		menuImprimirCoincidencias(todas);

		Scanner in = new Scanner(System.in);
		p();
		f("# a borrar? ");
		int indice = in.nextInt() - 1;
		if (indice > todas.length) {
			p("No es un resultado válido...");
		}
		// Para consumir el salto de linea después del número?
		in.nextLine();
		f("Seguro? (s/n)");
		boolean seguro = in.nextLine().equals("s");
		if (seguro) {
			Coincidencia resultado = todas[indice];
			repoThread.getIndiceBusqueda().borrarDocumento(resultado.nombre);
			repoThread.getIndiceBusqueda().guardarEnDisco();
			p();
			f("Borrado (%s)", resultado.nombre);
			p();
		} else {
			p("No se ha borrado nada.");
		}
	}

	private static void menuEsperarEntradaUsuario() {
		p();
		p("Pulsa Enter para volver al menú...");
		Scanner in = new Scanner(System.in);
		in.nextLine();
	}

	private static void menuImprimirCoincidencias(Coincidencia[] coincidencias) {
		String format = "%-3s%-20s%-20s%-16s%-25s%-25s%n";
		f("%nArchivos encontrados (%d)%n%n", coincidencias.length);
		if (coincidencias.length > 0) {
			f(format, "#", "Repositorio", "Nombre", "Archivo", "Palabras clave", "Comentario");
			f(format, "-", "-----------", "------", "-------", "--------------", "----------");
			for (int i = 0; i < coincidencias.length; i++) {
				Coincidencia c = coincidencias[i];
				f(format, i + 1, c.repositorio.nombre(), c.nombre, c.archivo, Strings.join(",",
						c.palabrasClave), c.comentario);
			}
		}
	}

	public static File getDirectorioConfiguracion() {
		return directorioConfiguracion;
	}

	public static void errorFatal(String msg) {
		System.err.println(msg);
		System.err.println("Terminando...");
		System.exit(1);
	}

	public static void p() {
		System.out.println();
	}

	public static void p(String s) {
		System.out.println(s);
	}

	public static void f(String format, Object... args) {
		System.out.format(format, args);
	}
}
