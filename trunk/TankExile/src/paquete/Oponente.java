

package paquete;

import java.rmi.RemoteException;

public class Oponente implements Controlable {
	public static boolean coneccionOponente= false;//Este campo es verdadero cuando el oponente ha clikeado en jugar
        public Oponente(){
	}
	

	public void setX(int x) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setY(int y) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void irArriba() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void irAbajo() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void irIzquierda() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void irDerecha() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void noIrArriba() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void noIrAbajo() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void noIrIzquierda() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void noIrDerecha() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setDireccion(int direccion) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void acelerar() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void noAcelerar() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void imitar(Tanque tanque) throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
