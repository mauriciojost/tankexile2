package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

// Clase que representa a los muros en el circuito. 
public class Muro extends Bloque{
	private static BufferedImage imagenes[] = new BufferedImage[5]; // Conjunto de imágenes asociadas a la clase Muro.
	private int tramaActual = 0; // Atributo que representa al número de imagen x (en imagenes[x]) que se ha de representar para este bloque al pintarlo en la pantalla.
	
	public Muro(int x, int y){
		super(x,y); // Constructor del bloque.
		try {
			// Carga de las imágenes en la Clase (puesto que son las mismas para cada objeto de la misma).
			if (imagenes[0]==null) imagenes[0] = ImageIO.read(getClass().getClassLoader().getResource("res/muro0.gif"));
			if (imagenes[1]==null) imagenes[1] = ImageIO.read(getClass().getClassLoader().getResource("res/muro1.gif"));
			if (imagenes[2]==null) imagenes[2] = ImageIO.read(getClass().getClassLoader().getResource("res/muro2.gif"));
			if (imagenes[3]==null) imagenes[3] = ImageIO.read(getClass().getClassLoader().getResource("res/muro3.gif"));
			if (imagenes[4]==null) imagenes[4] = ImageIO.read(getClass().getClassLoader().getResource("res/muro4.gif"));
		} catch (Exception e) {
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Muro, " + e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		}
	}
	
	// Método de dibujo del muro.
	public void paint(Graphics2D g) {
		g.drawImage(imagenes[tramaActual], super.getX(), super.getY(), null);
	}
	
	// Método que realiza una variación en la imagen a mostrar, dando un efecto de deterioro en el bloque.
	public void deterioro(int magnitud){
		tramaActual=(tramaActual+magnitud); // Se toma la trama 1 del arreglo de imágenes.
		if (tramaActual > imagenes.length-1){
			tramaActual = imagenes.length -1;
		}
	}
}
