package middleware;

/**
 * Interfaz proporcionada por la capa de abstracción de middleware. Proporciona
 * las operaciones habituales soportada por un middleware.
 * 
 * @author jperez
 */
public interface IMiddleware {
	/**
	 * Inicializa el middleware. Debe ser invocada antes que cualquier otra
	 * operación. Previamente a su invocación se debe haber configurado (si
	 * procede) el objeto que implementa la funcionalidad del middleware.
	 */
	public void inicializar();

	/**
	 * Detiene la capa de abstracción del middleware. No se debería llamar a
	 * ninguna otra operación de la interfaz tras haber detenido su
	 * funcionamiento.
	 */
	public void detener();

	/**
	 * Entra al hilo llamante en un bucle infinito de espera. Esta operación
	 * resulta útil para evitar la terminación de un servidor cuando tras crear
	 * y hacer accesibles los sirvientes no tiene nada más que hacer.
	 */
	public void esperar();

	/**
	 * Registra en el middleware un objeto sirviente devolviendo una referencia
	 * al objeto accesible de manera remota que implementa. Dependiendo del
	 * middleware empleado, dicho objeto podrá ser el sirviente u otro objeto
	 * distinto. Dado que el resultado se devuelve como Object, deberá
	 * realizarse una conversión de tipo al tipo destino.
	 * 
	 * @param sirviente
	 *            Objeto que se desea hacer accesible a través de la red
	 * @param nombre_interfaz
	 *            Nombre de la interfaz que pretende implementar dicho objeto
	 * 
	 * @return El objeto accesible a través de la red construido por el
	 *         middleware. Puede coincidir o no con el sirviente dependiendo del
	 *         middleware.
	 */
	public Object registrar(Object sirviente, String nombre_interfaz);

	/**
	 * Da de baja un objeto previamente registrado con {@link registrar} para
	 * que deje de ser accesible a través de la red. La referencia a objeto que
	 * se debe pasar debe ser la producida por la operación registrar.
	 * 
	 * @param
	 */
	public void desregistrar(Object objeto);

	/**
	 * Asocia un nombre a un objeto. Dicho objeto debe ser el resultado de haber
	 * registrado un sirviendo con {@link registrar}.
	 * 
	 * @param objeto
	 *            Objeto al que se le asocia el nombre
	 * @param nombre
	 *            Nombre (cadena de texto) que se le asocia
	 */
	public void nombrar(Object objeto, String nombre);

	/**
	 * Localiza un objeto a partir de su nombre asociado. En caso de no existir
	 * ningún objeto asociado a la cadena de texto que se suministra como
	 * nombre, se obtiene null
	 * 
	 * @param nombre_objeto
	 *            Nombre asociado al objeto que se busca
	 * @param nombre_clase
	 *            Nombre de la clase a la que pertenece
	 * @return Referencia al objeto asociado al nombre. En caso de no existir
	 *         ningún objeto asociado a dicho nombre, se obtiene null.
	 */
	public Object localizar(String nombre_objeto, String nombre_clase);
}
