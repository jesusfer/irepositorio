package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Properties;
import java.util.Scanner;

import repositorio.IRepositorio;

import middleware.JavaORB;
import middleware.Middleware;

public class MainRepositorio {
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

		System.out.println("Config dir: " + directorioConfiguracion.getAbsolutePath());

		// Leer archivos de configuracion sencillos
		String cfgPadre = leeCadenaConfiguracion("RepositorioPadre.cfg", directorioConfiguracion);
		String cfgRaiz = leeCadenaConfiguracion("RepositorioRaiz.cfg", directorioConfiguracion);
		String cfgNombre = leeCadenaConfiguracion("NombreRepositorio.cfg", directorioConfiguracion);

		System.out.println("Repo raiz: " + cfgRaiz);
		System.out.println("Repo nombre: " + cfgNombre);
		System.out.println("Repo padre: " + cfgPadre);

		cargarIndice("ListaArchivos.cfg", directorioConfiguracion);

		IRepositorioThread repoThread = new IRepositorioThread(cfgNombre, cfgPadre, cfgRaiz);
		repoThread.start();

		// Esperar a que el hilo inicialice...
		Thread.sleep(2000);

		while (true) {
			System.out.println("Escribe \"exit\" para salir");
			System.out.print("> ");

			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String entradaUsuario = br.readLine();

			if (entradaUsuario != null && entradaUsuario.equals("exit")) {
				Middleware.detener();
//				repoThread.interrupt();
				System.exit(0);
			}
		}
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

	private static void cargarIndice(String nombre, File directorio) {
		File archivo = new File(directorio, nombre);
		if (!archivo.exists()) {
			errorFatal(nombre + " no encontrado!");
		}
	}

	public static void errorFatal(String msg) {
		System.err.println(msg);
		System.err.println("Exiting...");
		System.exit(1);
	}

	private static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}

			return stringBuilder.toString();
		} finally {
			reader.close();
		}
	}
}
