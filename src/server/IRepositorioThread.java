package server;

import java.util.Properties;

import middleware.JavaORB;
import middleware.Middleware;
import repositorio.IRepositorio;

/**
 * Este hilo se encarga de crear el servidor para responder a llamadas remotas
 * para este Repositorio
 * */
public class IRepositorioThread extends Thread {
	private String cfgNombre;
	private String cfgPadre;
	private String cfgRaiz;

	IRepositorioServerImpl sirviente;

	private IRepositorio repositorio;
	private IRepositorio repositorioRaiz;
	private IRepositorio repositorioPadre;

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
		sirviente = new IRepositorioServerImpl();
		repositorio = (IRepositorio) Middleware.registrar(sirviente, IRepositorio.CLASE);

		// Configurar el sirviente
		repositorio.nombre(cfgNombre);

		// Me registro en el raiz para que puedan localizarme por nombre
		// rápidamente
		// Registro un contexto con el nombre del repositorio
		// Y dentro del contexto un objeto que apunta al IRepositorio
		// Así puedo colgar referencias también a mis hijos del contexto
		String[] nombresContexto = { cfgNombre };
		Middleware.nombrarContexto(nombresContexto);

		String[] nombresRepositorio = { cfgNombre, IRepositorio.CLAVE };
		Middleware.nombrarObjeto(repositorio, nombresRepositorio);

		// Ahora busco el raiz y mi padre para tener sus referencias
		// Si tengo padre, me registro como hijo suyo
		boolean soyRaiz = cfgRaiz.equals(cfgNombre);

		if (soyRaiz) {
			System.out.println("Este repositorio es el raiz del sistema");
			repositorioRaiz = repositorio;
			repositorioPadre = repositorio;
		} else {
			System.out.println("Este no es el repositorio raiz");

			// He de buscar el repositorio raiz
			repositorioRaiz = localizarRepositorio(cfgRaiz);
			if (repositorioRaiz == null) {
				MainRepositorio.errorFatal("No se ha podido localizar el repositorio raiz!");
			}
			System.out.println("Raiz encontrada: " + repositorioRaiz.nombre());

			// Y también a mi padre
			if (cfgRaiz.equals(cfgPadre)) {
				repositorioPadre = repositorioRaiz;
			} else {
				repositorioPadre = localizarRepositorio(cfgPadre);
				if (repositorioPadre == null) {
					MainRepositorio.errorFatal("No se ha podido localizar el repositorio padre!");
				}
			}
			System.out.println("Padre encontrado: " + repositorioPadre.nombre());
			repositorio.padre(repositorioPadre);
			// Registrarme como hijo ya que no soy el raiz
			repositorio.padre().registrar(repositorio);
		}

		// Hemos terminado la inicialización
		System.out.println("Repositorio activo...");
		Middleware.esperar();
		System.out.println("RepoThread terminando...");
	}

	public void detener() {
		if (!cfgNombre.equals(cfgPadre)) {
			repositorioPadre.baja(cfgNombre);
		}
		// Middleware.desregistrar(sirviente);
	}

	IRepositorio localizarRepositorio(String nombre) {
		IRepositorio repo;
		// En realidad, el nombre es el nombre del contexto y dentro del
		// contexto
		// encontraré el repositorio usando la clave
		String[] ruta = { nombre, IRepositorio.CLAVE };
		repo = (IRepositorio) Middleware.localizar(ruta, IRepositorio.CLASE);

		return repo;
	}
}
