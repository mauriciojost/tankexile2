package paquete;
import java.awt.Graphics2D;

// Clase cuyos objetos son las unidades de conformación del circuito de juego.
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
public abstract class Bloque implements ElementoDeJuego, Imitable{	
	private int indice=-1;
	private transient Rectangle bounds = new Rectangle(0,0,Finals.BLOQUE_LADO_LONG,Finals.BLOQUE_LADO_LONG);
	
	public Bloque(int bx, int by) {
		bounds.setLocation(bx * Finals.BLOQUE_LADO_LONG, by * Finals.BLOQUE_LADO_LONG);
	}
	
	public void eventoChoque(ElementoDeJuego contraQuien) {
		try {
			Class[] arregloDeClases = {contraQuien.getClass()};
			Object[] arrayArgumentos = {contraQuien};
			this.getClass().getMethod("eventoChoqueCon" + contraQuien.getClass().getSimpleName(), (Class[]) arregloDeClases).invoke(this, arrayArgumentos);
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}
	
	// Método que brinda la imagen a mostrar para este bloque. Abstracto.
	public abstract void pintar(Graphics2D g);
	
	public int getIndice(){
		return indice;
	}
	public void setIndice(int indice){
		this.indice = indice;
	}
	
	// Métodos que permiten obtener la posición real (en pixeles desde la esquina superior izquierda) del bloque.
	public int getX(){return bounds.x;}
	public int getY(){return bounds.y;}
	public Rectangle getBounds(){
		return bounds;
	}
	
}
