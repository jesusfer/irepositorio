package servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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

	public static File getDirectorioConfiguracion() {
		return directorioConfiguracion;
	}

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
			errorFatal("El directorio de configuración no existe! (" + directorioConfiguracion.getAbsolutePath() + ")");
		}

		// p("Config dir: " +
		// directorioConfiguracion.getAbsolutePath());

		// Leer archivos de configuracion sencillos
		String cfgPadre = leeCadenaConfiguracion(Constantes.REPO_PADRE, directorioConfiguracion);
		String cfgRaiz = leeCadenaConfiguracion(Constantes.REPO_RAIZ, directorioConfiguracion);
		String cfgNombre = leeCadenaConfiguracion(Constantes.REPO_NOMBRE, directorioConfiguracion);

		// p("Repo raiz: " + cfgRaiz);
		p("Repo nombre: " + cfgNombre);
		// p("Repo padre: " + cfgPadre);

		IndiceBusqueda indice = cargarIndice(Constantes.ARCHIVO_INDICE, directorioConfiguracion);

		repoThread = new RepositorioThread(cfgNombre, cfgPadre, cfgRaiz, indice);
		repoThread.start();
		
		// Esperar a que el hilo inicialice...
		// Ñapa para esperar hasta que el hilo servidor pille el Lock antes que este
		Thread.sleep(100);
		Main.loadLock.lock();
		Main.loadLock.unlock();

		try {
			menuPrincipal();
		} catch (Exception ex) {
			errorFatal("Ha ocurrido un error: " + ex.getMessage());
		}
	}

	private static String leeCadenaConfiguracion(String nombre, File directorio) {
		String contents = null;
		File archivo = null;
		Scanner scanner = null;
		try {
			archivo = new File(directorio.getAbsolutePath(), nombre);
			if (!archivo.exists()) {
				errorFatal(nombre + " no encontrado!");
			}
			scanner = new Scanner(archivo, "UTF-8");
			contents = scanner.nextLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			errorFatal(archivo.getName() + " no se puede encontrar?!");
		} finally {
			scanner.close();
		}

		return contents;
	}

	// TODO Probablemente este método debería ir en la clase IndiceBusqueda
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
			p("9. Salir");
			f("> ");

			// No se puede cerrar entrada, porque si no se cierra System.in para toda la aplicación
			@SuppressWarnings("resource")
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
					System.exit(0);
					break;
				}
			} catch (java.util.InputMismatchException ex) {
			}
		}
	}

	private static void menuBusquedaLocal() {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		f("Palabra? ");
		String palabra = in.nextLine();
		Coincidencia[] cx = repoThread.getRepositorio().buscar(palabra);
		ultimaBusqueda = cx;
		menuImprimirCoincidencias(cx);
	}

	private static void menuBusquedaGlobal() {
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		f("Palabra? ");
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
			p("No se ha hecho una búsqueda aún o la última búsqueda no tuvo resultados.");
			return;
		}
		// Que el usuario elija un resultado de búsqueda...
		p("Ultima búsqueda realizada:");
		menuImprimirCoincidencias(ultimaBusqueda);

		@SuppressWarnings("resource")
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
		File salida = null;
		boolean borrar = false;
		int totalRecibidos = 0;
		try {
			// Iniciar la descarga
			ITransferencia transferencia = resultado.repositorio.iniciarDescarga(resultado.nombre);
			// Crear el archivo local e ir rellenúndolo
			File dir = new File("");
			salida = new File(dir.getAbsolutePath(), resultado.archivo);
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
			// Borrar el archivo que hemos creado para guardar. Si no se queda
			// con 0 bytes
			borrar = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			p();
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (borrar) {
				try {
					salida.delete();
				} catch (Exception ex) {
				}
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
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		int index = 0;
		for (String mensaje : preguntas) {
			String respuesta = "";
			// Nos aseguramos que el usuario escribe algo
			while (respuesta.trim().length() == 0) {
				f("%-20s? ", mensaje);
				respuesta = in.nextLine();
			}
			respuestas[index++] = respuesta;
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

		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		p();
		f("# a borrar? ");
		int indice = in.nextInt() - 1;
		if (indice > todas.length) {
			p("No es un resultado vúlido...");
		}
		// Para consumir el salto de linea despuús del número?
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
		@SuppressWarnings("resource")
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
				f(format, i + 1, c.repositorio.nombre(), c.nombre, c.archivo, Strings.join(",", c.palabrasClave),
						c.comentario);
			}
		}
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
