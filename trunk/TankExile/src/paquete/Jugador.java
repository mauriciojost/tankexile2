package paquete;
import java.applet.AudioClip;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

// Clase cuyo objeto tiene la responsabilidad de atender al teclado y con ello comandar el tanque propio (el 'no remoto').
public class Jugador implements KeyListener{
	private Controlable tanque;
	private URL url = null;
	public AudioClip clip; 
	

	public Jugador(Controlable tanque){
		this.tanque = tanque;  
	}

	public URL getUrl(){
		return url;
	}
	public void keyReleased(KeyEvent e) {
		try { 
			switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN: //clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrAbajo();
					break;
				case KeyEvent.VK_UP: //clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrArriba();
					break;
				case KeyEvent.VK_LEFT: //clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrIzquierda();
					break;
				case KeyEvent.VK_RIGHT: //clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrDerecha();
					break;
				//case KeyEvent.VK_SHIFT:tanque.noAcelerar();break;
			}
		} catch (RemoteException ex) {
			Logger.getLogger(Jugador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
				
	public void keyPressed(KeyEvent e) {			
		try { 
			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: //if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irArriba();
					break;
				case KeyEvent.VK_LEFT: //if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irIzquierda();
					break;
				case KeyEvent.VK_RIGHT: //if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irDerecha();
					break;
				case KeyEvent.VK_DOWN: //if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irAbajo();
					break;
				//case KeyEvent.VK_SHIFT:tanque.acelerar();break;
			}
		} catch (RemoteException ex) {
			Logger.getLogger(Jugador.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
		
	public void keyTyped(KeyEvent e){}
	
}
