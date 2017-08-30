package servidor.repositorio;

import java.util.Properties;

import middleware.JavaORB;
import middleware.Middleware;
import repositorio.IRepositorio;
import servidor.Main;
import servidor.busqueda.IndiceBusqueda;

/**
 * Este hilo se encarga de crear el servidor para responder a llamadas remotas
 * para este Repositorio
 * */
public class RepositorioThread extends Thread {
	private String cfgNombre;
	private String cfgPadre;
	private String cfgRaiz;

	private IndiceBusqueda indice;

	private IRepositorioServerImpl sirviente;

	private IRepositorio repositorio;
	private IRepositorio repositorioRaiz;
	private IRepositorio repositorioPadre;

	public RepositorioThread(String cfgNombre, String cfgPadre, String cfgRaiz, IndiceBusqueda indice) {
		super("RepoThread");
		this.cfgNombre = cfgNombre;
		this.cfgPadre = cfgPadre;
		this.cfgRaiz = cfgRaiz;
		this.indice = indice;
		// start();
	}

	@Override
	public void run() {
		Main.loadLock.lock();
		// /////////////////////////////////
		// Inicializar middleware
		// /////////////////////////////////

		// System.out.println("Inicializando Middleware");
		Properties props = new Properties();
		JavaORB mdlw = new JavaORB();
		// mdlw.opcionesLC = args;
		mdlw.opcionesProp = props;
		mdlw.nombreSN = "OrgRepos";
		Middleware.inicializar(mdlw);

		// Creación del sirviente y registrarlo
		System.out.println("Creando y registrando el repositorio");
		sirviente = new IRepositorioServerImpl(indice);
		repositorio = (IRepositorio) Middleware.registrar(sirviente, IRepositorio.CLASE);

		indice.setRepositorio(repositorio);
		// Configurar el sirviente
		repositorio.nombre(cfgNombre);

		// Me registro en el raiz para que puedan localizarme por nombre
		// rápidamente
		// Registro un contexto con el nombre del repositorio
		// Y dentro del contexto un objeto que apunta al IRepositorio
		// Así puedo colgar referencias también a mis hijos del contexto
		String[] nombresContexto = { cfgNombre };
		String[] nombresRepositorio = { cfgNombre, IRepositorio.CLAVE };
		IRepositorio x = (IRepositorio) Middleware.localizar(nombresRepositorio, IRepositorio.CLASE);
		if (x != null) {
			boolean continuar = false;
			try {
				x.nombre();
			} catch (Exception ex) {
				continuar = true;
			}
			if (!continuar) {
				Main.errorFatal("Ya hay un repositorio registrado con nombre: " + cfgNombre);

			}
		}

		Middleware.nombrarContexto(nombresContexto);
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
				Main.errorFatal("No se ha podido localizar el repositorio raiz!");
			}
			// System.out.println("Raiz encontrada: " +
			// repositorioRaiz.nombre());

			// Y también a mi padre
			if (cfgRaiz.equals(cfgPadre)) {
				repositorioPadre = repositorioRaiz;
			} else {
				repositorioPadre = localizarRepositorio(cfgPadre);
				if (repositorioPadre == null) {
					Main.errorFatal("No se ha podido localizar el repositorio padre!");
				}
			}
			// System.out.println("Padre encontrado: " +
			// repositorioPadre.nombre());
			repositorio.padre(repositorioPadre);
			// Registrarme como hijo ya que no soy el raiz
			try {
				repositorio.padre().registrar(repositorio);
			} catch (Exception ex) {
				Main.errorFatal("Error en el registro como subordinado");
			}
		}

		// Hemos terminado la inicialización
		Main.loadLock.unlock();
		System.out.println("Repositorio activo...");
		Middleware.esperar();
		System.out.println("Repositorio terminando...");
	}

	public void detener() {
		if (!cfgNombre.equals(cfgPadre)) {
			try {
				repositorioPadre.baja(cfgNombre);
			} catch (Exception e) {
//				e.printStackTrace();
				System.err.println("Error dando de baja. Quizá el padre se ha muerto?");
			}
		}
		// Middleware.desregistrar(sirviente);
	}

	private IRepositorio localizarRepositorio(String nombre) {
		IRepositorio repo;
		// En realidad, el nombre es el nombre del contexto y dentro del
		// contexto
		// encontraré el repositorio usando la clave
		String[] ruta = { nombre, IRepositorio.CLAVE };
		repo = (IRepositorio) Middleware.localizar(ruta, IRepositorio.CLASE);

		return repo;
	}

	public IRepositorio getRepositorio() {
		return repositorio;
	}

	public IRepositorio getRepositorioRaiz() {
		return repositorioRaiz;
	}

	public IndiceBusqueda getIndiceBusqueda() {
		return indice;
	}
}
