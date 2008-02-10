
package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bola extends Thread implements BolaControlable{
	private static BufferedImage imagen[] = new BufferedImage[2];
	private int x,y;
	private final int largo = 20;
	private boolean correrHilos;

	private boolean buena;
	private int vx = 2;
	private int vy = 2;

	private int currentFrame;
	public Bola(boolean buena) {
		this.setName(buena?"Hilo bola buena":"Hilo bola mala");
		this.buena = buena;
		if (buena)
			x = x + 35;
		currentFrame = (buena?0:1);
		try {
			if (imagen[0]== null) imagen[0] = ImageIO.read(getClass().getClassLoader().getResource("res/bolaBuena.gif"));
			if (imagen[1]== null) imagen[1] = ImageIO.read(getClass().getClassLoader().getResource("res/bolaMala.gif"));
		} catch (Exception e) {
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Bola, "+e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		}
		correrHilos = true;
	}

	public void pintar(Graphics2D g){
		g.drawImage( imagen[currentFrame], x,y, null);
	}

	public int getX ( ) { return x; }
	public int getY ( ) { return y; }
	public void setX (int i) { x = i; }
	public void setY (int i) { y = i; }
	
	public void stopHilo(){
		correrHilos = false;
	}
	
	@Override
	public void run(){
		while(correrHilos){
			actuar();
			try {
				Thread.sleep(Finals.PERIODO*2);
			} catch (InterruptedException ex) {
				Logger.getLogger(Bola.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	public void actuar() {
		x+=vx;
		y+=vy;
		
		if (x < 0 || x > Finals.ANCHO_VENTANA-this.largo)
		  vx = -vx;
		if (y < 0 || y > Finals.ALTO_VENTANA-this.largo)
		  vy = -vy;
	}

	public int getVx() { return vx; }
	public void setVx(int i) {vx = i;	}

	public void setTodo(int x, int y) throws RemoteException {
		this.x = x;
		this.y = y;
	}
}
