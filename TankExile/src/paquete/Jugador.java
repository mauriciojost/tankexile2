package paquete;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// Clase cuyo objeto tiene la responsabilidad de atender al teclado y con ello comandar el tanque propio (el 'no remoto').
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Jugador implements KeyListener{
	private Controlable tanque;
	private URL url = null;
	public AudioClip clip; 
	private boolean auxiliar_sonido_jugador = false;
	private boolean sonido_habilitado_jugador = false;
	
	public URL getUrl(){
		return url;
	}
	public Jugador(Controlable tanque){
		this.tanque = tanque;
		try{
			this.sonido_habilitado_jugador = tanque.getSonidoHabilitado();
		} catch (RemoteException ex) {
			System.out.println("Error en getSonidoHabilitado de Jugador.");
			Logger.getLogger(Jugador.class.getName()).log(Level.SEVERE, null, ex);
			//JOptionPane.showMessageDialog(null, "El oponente abandono conexión");
			System.exit(0);
		}
				
		try{
			
			url = new URL("file:///" + System.getProperty("user.dir") + "/src/res/waterrun.WAV");
		}catch (MalformedURLException e) { 
				e.printStackTrace();
		}
		this.clip = Applet.newAudioClip(getUrl());//getURL("/src/res/SOUNDER.WAV")
	}
	
	public void keyReleased(KeyEvent e) {
		try { 
			switch (e.getKeyCode()) {
				case KeyEvent.VK_DOWN: clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrAbajo();
					break;
				case KeyEvent.VK_UP: clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrArriba();
					break;
				case KeyEvent.VK_LEFT: clip.stop(); auxiliar_sonido_jugador = false;
					tanque.noIrIzquierda();
					break;
				case KeyEvent.VK_RIGHT: clip.stop(); auxiliar_sonido_jugador = false;
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
				case KeyEvent.VK_UP: if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irArriba();
					break;
				case KeyEvent.VK_LEFT: if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irIzquierda();
					break;
				case KeyEvent.VK_RIGHT: if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
					tanque.irDerecha();
					break;
				case KeyEvent.VK_DOWN: if(!auxiliar_sonido_jugador && sonido_habilitado_jugador){ clip.loop(); auxiliar_sonido_jugador = true;}
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
	
//	public void setSonido(boolean sh){
//		sonido_habilitado = sh;
//	}
	
}
