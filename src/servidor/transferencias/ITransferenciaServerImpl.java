package servidor.transferencias;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import repositorio.ArchivoDetalles;
import repositorio.ArchivoNoEncontradoException;
import repositorio.ITransferenciaPOA;

public class ITransferenciaServerImpl extends ITransferenciaPOA {
	private ArchivoDetalles detalles;
	private int bloque = 1024 * 4;
	FileInputStream fis;
	TransferenciaThread hilo;

	public ITransferenciaServerImpl(ArchivoDetalles detalles, TransferenciaThread hilo) {
		this.detalles = detalles;
		this.hilo = hilo;

		File file = new File(detalles.directorio, detalles.archivo);
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}
	}

	public byte[] solicitarBloque() throws ArchivoNoEncontradoException {
		if (fis == null) {
			// Ha habido alg�n error abriendo el archivo.
			hilo.transferenciaFinalizada();
			throw new ArchivoNoEncontradoException("El archivo no se ha encontrado en el servidor");
		}

		byte[] resultado = new byte[bloque];
		try {
			int leidos = fis.read(resultado);
			if (leidos == -1) {
				fis.close();
				// Hemos terminado de leer el archivo
				hilo.transferenciaFinalizada();
				return new byte[0];
			}
			// Para el caso del �ltimo bloque, ajustar el array al tama�o le�do
			// real ya que no podemos indicar hasta donde llegar al enviarlo
			if (leidos < bloque) {
				byte[] temp = resultado.clone();
				resultado = new byte[leidos];
				for (int i = 0; i < leidos; i++) {
					resultado[i] = temp[i];
				}
			}
		} catch (Exception e) {
			System.err.println("Error leyendo del archivo para xfer: " + detalles.archivo);
			try {
				fis.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			// Por si queremos simular lentitud
			Thread.sleep(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return resultado;
	}
}
