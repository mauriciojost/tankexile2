
package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
// MODIFICACION DE FEDE

// AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
// AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
public class Bola{
	protected int vx;
	private BufferedImage imagen[] = new BufferedImage[2];
	protected int x,y;
	protected int width, heigth;
	private int contador=0;

	private int periodoDeTrama=50;

	private int currentFrame=0;
	public Bola() {
		//super(stage);
		URL url=null;
		 try {
				url = getClass().getClassLoader().getResource("res/bicho0.gif");
			   imagen[0] = ImageIO.read(url);
						url = getClass().getClassLoader().getResource("res/bicho1.gif");
			   imagen[1] = ImageIO.read(url);
			} catch (Exception e) {
			
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Bola, "+e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		
	  }
		
		setFrameSpeed(35);
	}

	public void paint(Graphics2D g){
		g.drawImage( imagen[currentFrame], x,y, null);
	}

	public int getFrameSpeed() {return periodoDeTrama;	}
	public void setFrameSpeed(int i) {periodoDeTrama = i;	}
	public int getX()  { return x; }
	public void setX(int i) {	x = i; }

	public int getY() {	return y; }
	public void setY(int i) { y = i; }
	
	
	public void actuar() {
		//super.act();
		//acá esta mi comentario (Mauricio)
					contador ++;
		if (contador == periodoDeTrama){
						contador =0;
						currentFrame = (currentFrame+1) % imagen.length;
	}
					
					
		x+=vx;
		if (x < 0 || x > Finals.ANCHO_VENTANA)
		  vx = -vx;
	}

	public int getVx() { return vx; }
	public void setVx(int i) {vx = i;	}
}
