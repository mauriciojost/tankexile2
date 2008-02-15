
package paquete;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

// Clase que representa a cada bola del juego.
public class Bola extends Thread implements BolaControlable, ElementoDeJuego{
	private static int RANGO_VELOCIDAD = 4;
	private static int MIN_VELOCIDAD = 1;
	private static BufferedImage imagen[] = new BufferedImage[2];
	private int x,y; // Posición.
	
	private boolean correrHilos; // Indicador de detención del hilo.
	private Tanque tanquePropio;
	private boolean buena; // Indicador de bola buena o mala.
	private int vx = 2; // Velocidad de la bola.
	private int vy = 2;
	private int currentFrame; // Indicador de la imagen a mostrar.
	private boolean soyLocal = false; // Indicador de que la bola actual es controlada localmente, y no de forma remota.
	private Random rnd = new Random(); // Generador de números pseudo aleatorios usados en el rebote de las bolas.
	
	// Contructor.
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
		correrHilos = true; // Se permite la ejecución de los hilos.
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
				actuar(); // En caso de ser local, la bola actua teniendo en cuenta rebotes. No así cuando es controlada de forma remota.
				try {Thread.sleep(Finals.PERIODO_BOLA);} catch (InterruptedException ex) {Logger.getLogger(Bola.class.getName()).log(Level.SEVERE, null, ex);}
			}
		}else{
			while(correrHilos){
				actuarResumido();
				try {Thread.sleep(Finals.PERIODO_BOLA);} catch (InterruptedException ex) {Logger.getLogger(Bola.class.getName()).log(Level.SEVERE, null, ex);}
			}
		}
	}
	
	public void actuar() {
		x+=vx;
		y+=vy;
			
		// Es verificada la condición de contacto con el tanque local.
		if((tanquePropio.getX() > x-20)&&(tanquePropio.getX() < x+20)&&(tanquePropio.getY() > y-20)&&(tanquePropio.getY() < y+20))
			if(buena)
				tanquePropio.setVelocidad(Tanque.MAX_VELOCIDAD); // La bola buena aumenta la velocidad.
			else
				tanquePropio.choque(true); // La bola mala produce el efecto de choque y establece la velocidad estandar.
			
		// Es verificada la condición de rebote.
		if (x < 0)
			vx = (Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		if (x > Finals.ANCHO_VENTANA-Finals.BLOQUE_LADO_LONG)
		  vx = -(Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		if (y < 0)
			vy = (Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		if ( y > Finals.ALTO_VENTANA-Finals.BLOQUE_LADO_LONG) 
			vy = -(Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
	}
	
	public void actuarResumido(){
		// Es sólo verificada la condición de choque con el tanque local.
		
		Rectangle bolaRec = new Rectangle(x,y,Finals.BLOQUE_LADO_LONG,Finals.BLOQUE_LADO_LONG);
		if (bolaRec.intersects(tanquePropio.getBounds()))
			if (buena)
				tanquePropio.setVelocidad(Tanque.MAX_VELOCIDAD); // La bola buena aumenta la velocidad.
			else
				tanquePropio.choque(true); // La bola mala produce el efecto de choque y establece la velocidad estandar.
	}

	public int getVx() { return vx; }
	public void setVx(int i) {vx = i;}

	public void setTodo(int x, int y) throws RemoteException {
		this.x = x;
		this.y = y;
	}

	public void eventoChoque(ElementoDeJuego contraQuien) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getNombre() {
		return "Bola";
	}
	public Rectangle getBounds(){
		return new Rectangle(x,y,Finals.BLOQUE_LADO_LONG, Finals.BLOQUE_LADO_LONG);
	}
}
