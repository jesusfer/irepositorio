package server;

import java.util.Properties;

import middleware.JavaORB;
import middleware.Middleware;
import repositorio.IRepositorio;

public class IRepositorioThread extends Thread {
	String cfgNombre;
	String cfgPadre;
	String cfgRaiz;

	public IRepositorioThread(String cfgNombre, String cfgPadre, String cfgRaiz) {
		super("RepoThread");
		this.cfgNombre = cfgNombre;
		this.cfgPadre = cfgPadre;
		this.cfgRaiz = cfgRaiz;
		// start();
	}

	@Override
	public void run() {
		// /////////////////////////////////
		// Inicializar middleware
		// /////////////////////////////////

		System.out.println("Inicializando Middleware");
		Properties props = new Properties();
		JavaORB mdlw = new JavaORB();
		// mdlw.opcionesLC = args;
		mdlw.opcionesProp = props;
		mdlw.nombreSN = "OrgRepos";
		Middleware.inicializar(mdlw);

		// Creación del sirviente y registrarlo
		System.out.println("Creando y registrando el repositorio");

		IRepositorioServerImpl sirviente = new IRepositorioServerImpl();
		IRepositorio obj = (IRepositorio) Middleware.registrar(sirviente, "repositorio.IRepositorio");
		// Configurar el sirviente
		sirviente.nombre(cfgNombre);

		// Antes de crear el sirviente hace falta:
		// - Referencia al padre (si no soy el raiz)
		// - Referencia al raiz

		boolean soyRaiz = cfgRaiz.equals(cfgNombre);

		if (!soyRaiz) {
			System.out.println("Este no es el repositorio raiz");
			// He de buscar el repositorio raiz
			IRepositorio repoRaiz = (IRepositorio) Middleware.localizar(cfgRaiz, "repositorio.IRepositorio");
			if (repoRaiz == null) {
				MainRepositorio.errorFatal("No se ha podido localizar el repositorio raiz!");
			}
			System.out.println("Raiz encontrada: " + repoRaiz.nombre());

			// Y también a mi padre
			// TODO Esto se puede obviar si el padre y el raiz son el mismo
			IRepositorio repoPadre = (IRepositorio) Middleware
					.localizar(cfgPadre, "repositorio.IRepositorio");
			if (repoPadre == null) {
				MainRepositorio.errorFatal("No se ha podido localizar el repositorio padre!");
			}
			System.out.println("Padre encontrado: " + repoPadre.nombre());
		}
		
		// Registrarme como hijo si es necesario

		Middleware.nombrar(obj, cfgNombre);

		System.out.println("Repositorio activo...");
		Middleware.esperar();
	}
}
