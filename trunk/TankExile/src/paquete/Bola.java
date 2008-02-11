
package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bola extends Thread implements BolaControlable{
	private static BufferedImage imagen[] = new BufferedImage[2];
	private int x,y;
	private final int largo = 20;
	private boolean correrHilos;
	private Tanque tanquePropio;
	private boolean buena;
	private int vx = 2;
	private int vy = 2;
	private int periodoEfecto = 0;
	private int currentFrame;
	private boolean soyLocal = false;
	Random rnd = new Random();
	
	public Bola(boolean buena, Tanque tanquePropio) {
		this.setName(buena?"Hilo bola buena":"Hilo bola mala");
		this.buena = buena; this.tanquePropio = tanquePropio;
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
	public void setLocal(){
		soyLocal = true;
	}
	@Override
	public void run(){
		if (soyLocal){
			
			this.setX(rnd.nextInt(Finals.BLOQUES_NUM) * Finals.BLOQUE_LADO_LONG);
			this.setY(rnd.nextInt(Finals.BLOQUES_NUM) * Finals.BLOQUE_LADO_LONG);
			this.vx = (rnd.nextBoolean()?1:-1) * vx;
			this.vy = (rnd.nextBoolean()?1:-1) * vy;
			
			while(correrHilos){
				actuar();
				try {
					Thread.sleep(Finals.PERIODO_BOLA);
				} catch (InterruptedException ex) {
					Logger.getLogger(Bola.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}else{
			while(correrHilos){
				actuarResumido();
				try {
					Thread.sleep(Finals.PERIODO_BOLA);
				} catch (InterruptedException ex) {
					Logger.getLogger(Bola.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}
	
	public void actuar() {
		x+=vx;
		y+=vy;
//		(Tx > (Bx-20)) 
//		(Tx < (Bx+20))
//		(Ty > (By-20)) 
//		(Ty < (By+20))


		if((tanquePropio.getX() > x-20)&&(tanquePropio.getX() < x+20)&&(tanquePropio.getY() > y-20)&&(tanquePropio.getY() < y+20))
			if(buena)
				tanquePropio.setVelocidad(2); // La bola buena aumenta la velocidad.
			else
				tanquePropio.choque(true); // La bola mala produce el efecto de choque y establece la velocidad estandar.
		
		if (x < 0)
			vx = (rnd.nextInt(2) + 4);
		if (x > Finals.ANCHO_VENTANA-this.largo)
		  vx = -(rnd.nextInt(2) + 4);
		if (y < 0)
			vy = (rnd.nextInt(2) + 4);
		if ( y > Finals.ALTO_VENTANA-this.largo) 
			vy = -(rnd.nextInt(2) + 4);
	}
	
	public void actuarResumido(){
		if((tanquePropio.getX() > x-20)&&(tanquePropio.getX() < x+20)&&(tanquePropio.getY() > y-20)&&(tanquePropio.getY() < y+20))
			if (buena)
				tanquePropio.setVelocidad(2); // La bola buena aumenta la velocidad.
			else
				tanquePropio.choque(true); // La bola mala produce el efecto de choque y establece la velocidad estandar.
	}

	public int getVx() { return vx; }
	public void setVx(int i) {vx = i;	}

	public void setTodo(int x, int y) throws RemoteException {
		this.x = x;
		this.y = y;
	}
}
