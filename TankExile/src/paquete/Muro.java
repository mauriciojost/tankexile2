package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

// Clase que representa a los muros en el circuito. 
import presentacion.Conexion;
public class Muro extends Bloque{
	public static final int UNIDAD_DE_MAGNITUD = Tanque.U_VELOCIDAD;
	private static final int NUMERO_DE_IMAGENES=5;
	private static BufferedImage imagenes[] = new BufferedImage[NUMERO_DE_IMAGENES]; // Conjunto de imágenes asociadas a la clase Muro.
	private int tramaActual = 0; // Atributo que representa al número de imagen x (en imagenes[x]) que se ha de representar para este bloque al pintarlo en la pantalla.
	
	public Muro(int x, int y){
		super(x,y); // Constructor del bloque.
		//System.out.println(this.getClass().getSimpleName());
		try {
			// Carga de las imágenes en la Clase (puesto que son las mismas para cada objeto de la misma).
			for (int i = 0; i < NUMERO_DE_IMAGENES; i++){
				if (imagenes[i]==null) imagenes[i] = ImageIO.read(getClass().getClassLoader().getResource("res/murito"+i+".gif"));
			}
		} catch (Exception e) {
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Muro, " + e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		}
	}
	
	// Método de dibujo del muro.
	public void pintar(Graphics2D g) {
		g.drawImage(imagenes[tramaActual], super.getX(), super.getY(), null);
	}
	
	// Método que realiza una variación en la imagen a mostrar, dando un efecto de deterioro en el bloque.
	public void deterioro(int magnitud){
		tramaActual = ( tramaActual + ( magnitud ) / Muro.UNIDAD_DE_MAGNITUD ); // Se toma la trama 1 del arreglo de imágenes.
		if (tramaActual > (imagenes.length - 1)){
			tramaActual = imagenes.length -1;
		}
	}

	public void eventoChoqueConTanque(Tanque tanque){
		deterioro(tanque.getVelocidad()); // Se provoca en el muro indicado un deterioro.
		Circuito.getCircuito().choqueNuevoCircuitoLocal(this.getIndice(), tanque.getVelocidad());
	}
	
	public void eventoChoqueConBola(Bola bola){
		//System.out.println(this.getNombre() + ": eventoChoqueConBola(...)");
	}
}
