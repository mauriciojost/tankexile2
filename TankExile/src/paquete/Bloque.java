package paquete;
import java.awt.Graphics2D;

// Clase cuyos objetos son las unidades de conformación del circuito de juego.
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
public abstract class Bloque implements ElementoDeJuego{	
	private int x, y; //Atributos que representan las coordenadas reales (píxeles) del bloque.
		
	public Bloque(int bx, int by) {
		x = bx * Finals.BLOQUE_LADO_LONG; // Cálculo de las coordenadas reales del bloque.
		y = by * Finals.BLOQUE_LADO_LONG;
	}
	
	public void eventoChoque(ElementoDeJuego contraQuien) {
		try {
			Class[] arregloDeClases = {contraQuien.getClass()};
			Object[] arrayArgumentos = {contraQuien};
			this.getClass().getMethod("eventoChoqueCon" + contraQuien.getNombre(), (Class[]) arregloDeClases).invoke(this, arrayArgumentos);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(Bloque.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(Bloque.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
			Logger.getLogger(Bloque.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(Bloque.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(Bloque.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	/*

	public void eventoChoqueConTanque(Tanque tanque){
		System.out.println("eventoChoqueConTanque(...)");
	}
	
	public void eventoChoqueConTanque(Bola bola){
		System.out.println("eventoChoqueConBola(...)");
	}
	*/

	// Método que brinda la imagen a mostrar para este bloque. Abstracto.
	public abstract void pintar(Graphics2D g);
	
	// Métodos que permiten obtener la posición real (en pixeles desde la esquina superior izquierda) del bloque.
	public int getX(){return x;}
	public int getY(){return y;}
	public Rectangle getBounds(){
		return new Rectangle(x, y, Finals.BLOQUE_LADO_LONG, Finals.BLOQUE_LADO_LONG);
	}
}
