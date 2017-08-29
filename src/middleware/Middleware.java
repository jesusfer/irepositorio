package middleware;

/**
 * Clase que encapsula la funcionalidad de un middleware. Crea un objeto
 * IMiddleware que implementa la funcionalidad de un middleware concreto, y
 * delega todas sus operaciones sobre este objeto.
 * 
 * @author jperez
 */
public abstract class Middleware {
	// Referencia al objeto en el que se delegan las operaciones de esta clase:
	private static IMiddleware middleware;

	/**
	 * Establece el objeto middleware que se pasa como parámetro para delegar en
	 * él el resto de las operaciones, e inicializa dicho objeto mediante el
	 * método inicializar de su interfaz.
	 * 
	 * @param middlew
	 *            Objeto middleware delegado.
	 */
	public static void inicializar(IMiddleware middlew) {
		middleware = middlew;
		middleware.inicializar();
	}

	public static void detener() {
		middleware.detener();
	}

	public static void esperar() {
		middleware.esperar();
	}

	public static Object registrar(Object sirviente, String nombre_interfaz) {
		return middleware.registrar(sirviente, nombre_interfaz);
	}

	public static void desregistrar(Object objeto) {
		middleware.desregistrar(objeto);
	}

	public static void nombrarObjeto(Object objeto, String nombre) {
		middleware.nombrarObjeto(objeto, nombre);
	}

	public static void nombrarObjeto(Object objeto, String[] nombres) {
		middleware.nombrarObjeto(objeto, nombres);
	}

	public static void desnombrarObjeto(String[] nombres){
		middleware.desnombrarObjeto(nombres);
	}

	public static void nombrarContexto(String[] nombres) {
		middleware.nombrarContexto(nombres);
	}

	public static Object localizar(String nombre_objeto, String nombre_clase) {
		return middleware.localizar(nombre_objeto, nombre_clase);
	}

	public static Object localizar(String[] nombres, String nombre_clase) {
		return middleware.localizar(nombres, nombre_clase);
	}

	public static Object[] localizarHijos(String nombre_objeto, String nombre_clase_hijos) {
		return middleware.localizarHijos(nombre_objeto, nombre_clase_hijos);
	}
}
