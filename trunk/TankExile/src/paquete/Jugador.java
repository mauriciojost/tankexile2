package paquete;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Clase cuyo objeto tiene la responsabilidad de atender al teclado y con ello comandar el tanque propio (el 'no remoto').
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Jugador implements KeyListener{
	private Controlable tanque;
	public Jugador(Controlable tanque){
		this.tanque = tanque;
	}
	
	public void keyReleased(KeyEvent e) {
		try {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					tanque.noIrAbajo();
					break;
				case KeyEvent.VK_UP:
					tanque.noIrArriba();
					break;
				case KeyEvent.VK_LEFT:
					tanque.noIrIzquierda();
					break;
				case KeyEvent.VK_RIGHT:
					tanque.noIrDerecha();
					break;
				case KeyEvent.VK_SHIFT:
					tanque.noAcelerar();
					break;
			}
		} catch (RemoteException ex) {
			Logger.getLogger(Jugador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
				
	public void keyPressed(KeyEvent e) {			
		try {
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					tanque.irArriba();
					break;
				case KeyEvent.VK_LEFT:
					tanque.irIzquierda();
					break;
				case KeyEvent.VK_RIGHT:
					tanque.irDerecha();
					break;
				case KeyEvent.VK_DOWN:
					tanque.irAbajo();
					break;
				case KeyEvent.VK_SHIFT:
					tanque.acelerar();
					break;
			}
		} catch (RemoteException ex) {
			Logger.getLogger(Jugador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
		
	public void keyTyped(KeyEvent e){}
}
