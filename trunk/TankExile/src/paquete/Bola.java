package paquete;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.Random;
import javax.imageio.ImageIO;

// Clase que representa a cada bola del juego.

public class Bola extends Thread implements Imitable, ElementoDeJuego{
	private transient static int RANGO_VELOCIDAD = 2;
	private transient static int MIN_VELOCIDAD = 1;
	private transient static final int MARGEN = 1;
	private transient static BufferedImage imagen[] = new BufferedImage[2];

	private transient boolean correrHilos = true; // Indicador de detención del hilo.
	private transient boolean buena; // Indicador de bola buena o mala.
	private transient Point velocidad = new Point(0,0);
	private Rectangle bounds = new Rectangle((Finals.BLOQUES_NUM/2) * 20+5,(Finals.BLOQUES_NUM/2) * 20+5,Finals.BLOQUE_LADO_LONG/2,Finals.BLOQUE_LADO_LONG/2);
	private transient int currentFrame; // Indicador de la imagen a mostrar.
	private transient Random rnd = new Random(); // Generador de números pseudo aleatorios usados en el rebote de las bolas.
	
	// Contructor.
	public Bola(boolean buena) {
		this.setName(buena?"Hilo bola buena":"Hilo bola mala");
		this.buena = buena; //this.tanquePropio = tanquePropio;
		currentFrame = (buena?0:1);
		try {
			if (imagen[0]== null) imagen[0] = ImageIO.read(getClass().getClassLoader().getResource("res/bolaBuena.gif"));
			if (imagen[1]== null) imagen[1] = ImageIO.read(getClass().getClassLoader().getResource("res/bolaMala.gif"));	
		} catch (Exception e) {
			System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Bola, "+e.getClass().getName()+" "+e.getMessage());
			System.exit(0);
		}
	}

	public void pintar(Graphics2D g){
		g.drawImage( imagen[currentFrame], bounds.x,bounds.y, null);
	}

	public void stopHilo(){
		correrHilos = false;
	}

	@Override
	public void run(){
		velocidad.setLocation((rnd.nextBoolean()?1:-1), (rnd.nextBoolean()?1:-1));
		try {Thread.sleep(4000);} catch (InterruptedException ex) {ex.printStackTrace();}
		while(correrHilos){
			actuar(); 
			try {Thread.sleep(Finals.PERIODO_SINCRONIZACION_BOLAS);} catch (InterruptedException ex) {ex.printStackTrace();}
		}
	}
	
	public void actuar() {
		bounds.x+=velocidad.x;
		bounds.y+=velocidad.y;
	}

	public boolean getBuena(){
		return buena;
	}

	public void eventoChoque(ElementoDeJuego contraQuien) {
		Class[] arregloDeClases = {contraQuien.getClass()};
		Object[] arrayArgumentos = {contraQuien};
		try {	
			this.getClass().getMethod("eventoChoqueCon" + contraQuien.getClass().getSimpleName(), (Class[]) arregloDeClases).invoke(this, arrayArgumentos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void eventoChoqueConTanque(Tanque tanque){
		rebotar(tanque);
	}
	public void eventoChoqueConMeta(Meta meta){}
	
	private void rebotar(ElementoDeJuego objeto){
		Point arriba = new Point(bounds.x+bounds.width/2,bounds.y+MARGEN);
		Point abajo = new Point(bounds.x+bounds.width/2,bounds.y+bounds.height-MARGEN);
		Point derecha = new Point(bounds.x+bounds.width-MARGEN,bounds.y+bounds.height/2);
		Point izquierda = new Point(bounds.x+MARGEN,bounds.y+bounds.height/2);
		
		if (objeto.getBounds().contains(arriba)){	
			velocidad.y = (Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		}
		else if (objeto.getBounds().contains(abajo)){ // Subiendo y el muro está más arriba.
			velocidad.y =-(Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		}
		if (objeto.getBounds().contains(izquierda)){ // A la derecha y el muro está más a la derecha.
			velocidad.x =(Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		}
		else if (objeto.getBounds().contains(derecha)){ // A la izquierda y el muro está más a la izquierda.
			velocidad.x =-(Bola.MIN_VELOCIDAD + rnd.nextInt(Bola.RANGO_VELOCIDAD));
		}
	}
	
	public void eventoChoqueConMuro(Muro muro){
		rebotar(muro);
	}

	public Rectangle getBounds(){
		return bounds;
	}

	public void imitar(Imitable objetoAImitar) throws RemoteException {
		Bola bola = (Bola)objetoAImitar;
		bounds.x = bola.getBounds().x;
		bounds.y = bola.getBounds().y;
	}

	public Object[] getParametros() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
