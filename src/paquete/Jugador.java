package paquete;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.HashMap;

// Clase cuyo objeto tiene la responsabilidad de atender al teclado y con ello comandar el tanque propio (el 'no remoto').
public class Jugador implements KeyListener{
	private Tanque tanque;
	private URL url = null;
	private HashMap<Integer,String> reglas = new HashMap<Integer,String>();

	public Jugador(Tanque tanque){
		this.tanque = tanque;
		reglas.put(KeyEvent.VK_DOWN, "Abajo");
		reglas.put(KeyEvent.VK_UP, "Arriba");
		reglas.put(KeyEvent.VK_LEFT, "Izquierda");
		reglas.put(KeyEvent.VK_RIGHT, "Derecha");
	}

	public URL getUrl(){
		return url;
	}
	public void keyReleased(KeyEvent e) {
		try {
			String metodo = "noIr" + reglas.get(e.getKeyCode());
			tanque.getClass().getMethod(metodo, (Class[]) null).invoke(tanque, (Object[]) null);
		} catch (Exception ex) {
			System.out.println("Tecla no válida.");
		}
	}
				
	public void keyPressed(KeyEvent e) {			
		try {
			String metodo = "ir" + reglas.get(e.getKeyCode());
			tanque.getClass().getMethod(metodo, (Class[]) null).invoke(tanque, (Object[]) null);
		} catch (Exception ex) {
			System.out.println("Tecla no válida.");
		} 
	}
	public void keyTyped(KeyEvent e){}
}
