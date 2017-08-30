package server;

import java.util.ArrayList;

import middleware.Middleware;
import repositorio.ArchivoDetalles;
import repositorio.ArchivoNoEncontradoException;
import repositorio.Coincidencia;
import repositorio.IRepositorio;
import repositorio.ITransferencia;

/**
 * This class is the implementation object for your IDL interface.
 * 
 * Let the Eclipse complete operations code by choosing 'Add unimplemented
 * methods'.
 */
public class IRepositorioServerImpl extends repositorio.IRepositorioPOA {
	String nombre = null;
	IRepositorio padre = null;
	IndiceBusqueda indice;

	public IRepositorioServerImpl(IndiceBusqueda indice) {
		super();
		this.indice = indice;
	}

	public String nombre() {
		return nombre;
	}

	public void nombre(String newNombre) {
		nombre = newNombre;
	}

	public IRepositorio padre() {
		if (padre == null)
			return (IRepositorio) this;
		return padre;
	}

	public void padre(IRepositorio newPadre) {
		padre = newPadre;
	}

	/**
	 * Obtiene la lista de repositorios que están subordinados de este.
	 * 
	 * La lista se obtiene del Name Server.
	 * */
	public IRepositorio[] subordinados() {
		Object[] hijos = Middleware.localizarHijos(nombre(), IRepositorio.CLASE);
		// Hay que quitar uno, que es este mismo repositorio
		IRepositorio[] hijosRep = new IRepositorio[hijos.length - 1];
		for (int i = 0, j = 0; i < hijos.length; i++) {
			IRepositorio r = (IRepositorio) hijos[i];
			if (!r.nombre().equals(this.nombre())) {
				hijosRep[j] = r;
				j++;
			}
		}
		return hijosRep;
	}

	/**
	 * Da de baja un repositorio subordinado.
	 * 
	 * Solo quita la referencia del subordinado como hijo de este repositorio.
	 * No quita referencias en ORB o quita el nombre raiz del repositorio.
	 * */
	public void baja(String nombre) {
		String[] nombres = { nombre(), nombre };
		Middleware.desnombrarObjeto(nombres);
		// Consola.Mensaje("Baja de subordinado: " + nombre);
		// Consola.Mensaje("Subordinados cuenta: " + subordinados().length);
	}

	/**
	 * Buscar implica buscar en este repositorio y recursivamente en los
	 * repositorios subordinados.
	 */
	public Coincidencia[] buscar(String palabraClave) {
		// 1. Hacemos una lista con los repositorios en los que hay que buscar
		// (incluyendo el local)
		// 2. Por cada repositorio hacemos un Thread y buscamos
		// 3. Join
		// 4. Unir resultados
		ArrayList<Coincidencia> resultados = new ArrayList<Coincidencia>();

		// for (Coincidencia c : this.indice.buscar(palabraClave)) {
		// resultados.add(c);
		// }

		ArrayList<IRepositorio> repos = new ArrayList<IRepositorio>();
		// Si añado el local, provocamos recursividad infinita
		// repos.add(indice.getRepositorio());
		for (IRepositorio sub : subordinados()) {
			repos.add(sub);
		}

		ArrayList<Thread> threads = new ArrayList<Thread>();
		threads.add(new BusquedaLocalThread(indice, palabraClave));
		for (IRepositorio repo : repos) {
			threads.add(new BusquedaThread(repo, palabraClave));
		}
		for (Thread t : threads) {
			try {
				t.join();
				for (Coincidencia c : ((IBusquedaThread) t).getResultados()) {
					resultados.add(c);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Aqui todos los hilos han terminado y tenemos todos los resultados
		// unidos

		return resultados.toArray(new Coincidencia[0]);
	}

	/**
	 * Este método debe comportarse como una factoría de ITransferencias.
	 * @throws ArchivoNoEncontradoException 
	 * */
	public ITransferencia iniciarDescarga(String nombre) throws ArchivoNoEncontradoException {
		// Se solicita la descarga de un archivo
		// Se busca el archivo en el índice y se pasan los detalles a la nueva
		// transferencia
		ArchivoDetalles detalles = indice.detallesArchivo(nombre);
		TransferenciaThread hilo = new TransferenciaThread(detalles);
		return hilo.getTransferencia();
	}

	/**
	 * Metodo que usará un repositorio subordinado para registrarse como hijo.
	 * 
	 * TODO Se podría también mantener la lista de hijos según los que se
	 * registren usando este método.
	 * */
	public String registrar(IRepositorio referencia) {
		return registrarConNombre(referencia, referencia.nombre());
	}

	/**
	 * Metodo que usará un repositorio subordinado para registrarse como hijo.
	 * 
	 * TODO Se podría también mantener la lista de hijos según los que se
	 * registren usando este método.
	 * */
	public String registrarConNombre(IRepositorio referencia, String nombre) {
		String[] ruta = { nombre(), nombre };
		Middleware.nombrarObjeto(referencia, ruta);
		IRepositorio registrado = (IRepositorio) Middleware.localizar(ruta, IRepositorio.CLASE);
		// Consola.Mensaje("Nuevo subordinado: " + registrado.nombre());
		// Consola.Mensaje("Subordinados cuenta: " + subordinados().length);
		return registrado.nombre();
	}

	public byte[] solicitarBloque(String nombre) {
		// TODO Auto-generated method stub
		return null;
	}
}
