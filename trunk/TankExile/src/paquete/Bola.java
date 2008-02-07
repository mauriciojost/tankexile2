
package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bola extends Thread implements BolaControlable{
	private int vx = 1;
	private int vy = 1;
	private BufferedImage imagen[] = new BufferedImage[2];
	protected int x,y;
	protected int width = 20, heigth = 20;
	
	private boolean buena;
	

	private int currentFrame;
	public Bola(boolean buena) {
		this.buena = buena;
		if (buena)
			x = x + 20;
		currentFrame = (buena?0:1);
		try {
			imagen[0] = ImageIO.read(getClass().getClassLoader().getResource("res/bolaBuena.gif"));
			imagen[1] = ImageIO.read(getClass().getClassLoader().getResource("res/bolaMala.gif"));
		} catch (Exception e) {
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Bola, "+e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		}
	}

	public void pintar(Graphics2D g){
		g.drawImage( imagen[currentFrame], x,y, null);
	}

	public int getX ( ) { return x; }
	public int getY ( ) { return y; }
	public void setX (int i) { x = i; }
	public void setY (int i) { y = i; }
	
	public void run(){
		while(true){
			actuar();
			try {
				this.sleep(Finals.PERIODO);
			} catch (InterruptedException ex) {
				Logger.getLogger(Bola.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public void actuar() {
		x+=vx;
		y+=vy;
		
					
		if (x < 0 || x > Finals.ANCHO_VENTANA-width)
		  vx = -vx;
		if (y < 0 || y > Finals.ALTO_VENTANA-heigth)
		  vy = -vy;
	}

	public int getVx() { return vx; }
	public void setVx(int i) {vx = i;	}

	public void setTodo(int x, int y) throws RemoteException {
		this.x = x;
		this.y = y;
	}
}
