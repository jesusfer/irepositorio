package utils;

import java.util.Iterator;

public class Arrays {
	public static <T> Iterable<T> iterate(T[] array) {
		return new ArrayIterable<T>(array);
	}
}

class ArrayIterable<T> implements Iterable<T> {
	private T[] _array;

	public ArrayIterable(T[] array) {
		this._array = array;
	}

	public Iterator<T> iterator() {
		return new ArrayIterator<T>();
	}

	class ArrayIterator<T> implements Iterator<T> {
		private int posicion;

		public ArrayIterator() {
			posicion = 0;
		}

		public boolean hasNext() {
			boolean result = false;
			if (posicion < _array.length) {
				result = true;
			}
			return false;
		}

		public T next() {
			return (T) _array[posicion++];
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}