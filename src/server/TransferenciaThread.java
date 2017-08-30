package server;

import middleware.Middleware;
import repositorio.ArchivoDetalles;
import repositorio.ITransferencia;

public class TransferenciaThread extends Thread {
	private ArchivoDetalles detalles;
	private ITransferencia transferencia;
	private ITransferenciaServerImpl transferServant;
	private boolean transferenciaEnProgreso;

	public TransferenciaThread(ArchivoDetalles detalles) {
		setName("TransferThread");
		this.detalles = detalles;
		transferServant = new ITransferenciaServerImpl(detalles, this);
		transferencia = (ITransferencia) Middleware.registrar(transferServant, "repositorio.ITransferencia");
		transferenciaEnProgreso = true;
		start();
	}

	public ITransferencia getTransferencia() {
		return transferencia;
	}

	public void transferenciaFinalizada() {
		transferenciaEnProgreso = false;
	}

	@Override
	public void run() {
		try {
			while (transferenciaEnProgreso) {
				Thread.sleep(1);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
