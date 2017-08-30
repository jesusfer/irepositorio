package utils;

public class Strings {
	/**
	 * Une un array de cadenas usando el separador.
	 * */
	public static String join(String separador, String[] cadenas) {
		StringBuilder sb = new StringBuilder();
		if (cadenas.length > 0) {
			sb.append(cadenas[0]);
			for (int i = 1; i < cadenas.length; i++) {
				sb.append(separador);
				sb.append(cadenas[i]);
			}
		}
		return sb.toString();
	}
}
