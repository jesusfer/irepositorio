package utils;

public class Arrays {
	public static <T> boolean Any(T[] array, Condicion cond) {
		boolean resultado = false;
		for (T t : array) {
			resultado |= cond.test(t);
			if (resultado)
				break;
		}
		return resultado;
	}
}