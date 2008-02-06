package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

// Clase cuyos objetos son parte del circuito, cada uno es partida para un tanque y llegada para el otro.
public class Meta extends Bloque{
	private static BufferedImage imagenes[] = new BufferedImage[2]; // Conjunto de imágenes asociadas a la clase Meta.
	private int numeroDeMeta; // Atributo que representa el número de meta.
	
	// Constructor de la clase.
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
	
	// Método de dibujo.
	public void paint(Graphics2D g) {
		g.drawImage(imagenes[numeroDeMeta], super.getX(), super.getY(), null);
	}
	
	// Método que retorna el número de meta de esta instancia.
	public int getNumero(){
		return numeroDeMeta;
	}
}
