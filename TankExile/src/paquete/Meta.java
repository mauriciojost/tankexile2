package paquete;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import javax.imageio.ImageIO;

// Clase cuyos objetos son parte del circuito, cada uno es partida para un tanque y llegada para el otro.
import presentacion.PrePartida;
public class Meta extends Bloque{
	private transient static BufferedImage imagenes[] = new BufferedImage[2]; // Conjunto de imágenes asociadas a la clase Meta.
	private transient int numeroDeMeta; // Atributo que representa el número de meta.
	private boolean teGanaron = false;
	
	// Constructor de transientla clase.
	public Meta(int x, int y, int numeroDeMeta){
		super(x,y); // Constructor del bloque.
		this.numeroDeMeta = numeroDeMeta;
		try {
			// Carga de las imágenes en la Clase (puesto que son las mismas para cada objeto de la misma).
			if (imagenes[0]==null) imagenes[0] = ImageIO.read(getClass().getClassLoader().getResource("res/meta0.gif"));
			if (imagenes[1]==null) imagenes[1] = ImageIO.read(getClass().getClassLoader().getResource("res/meta1.gif"));
		} catch (Exception e) {
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Meta, " + e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		}
	}
	
	public boolean getTeGanaron(){
		return teGanaron;
	}
	
	// Método de dibujo.
	public void pintar(Graphics2D g) {
		g.drawImage(imagenes[numeroDeMeta], super.getX(), super.getY(), null);
	}
	
	// Método que retorna el número de meta de esta instancia.
	public int getNumero(){
		return numeroDeMeta;
	}
	
	public void eventoChoqueConTanque(Tanque tanque){
		if (this.numeroDeMeta == otroID(tanque.getID())){
			this.teGanaron = true;
			this.finPartida(true);
		}
	}
	private int otroID(int id){
		return ((id+1)%2);
	}
	
	public void eventoChoqueConBola(Bola bola){
		//System.out.println(this.getNombre() + ": eventoChoqueConBola(...)");
	}

	public void imitar(Imitable objetoAImitar){
		Meta imitada = (Meta) objetoAImitar;	
		if (imitada.getTeGanaron()){
			this.finPartida(false);
		}
	}

	public Object[] getParametros() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	private void finPartida(boolean gane){
		if(gane)
			PrePartida.getPrePartida().setEstado("Fin del juego. Usted ha ganado...", Font.BOLD);
		else
			PrePartida.getPrePartida().setEstado("Fin del juego. Usted ha perdido...", Font.BOLD);
		PrePartida.getPrePartida().setVisible(true);
		Partida.getPartida().finalizar();
	}
	

}
