package paquete;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Tanque implements Controlable{
	public static final int MAX_VELOCIDAD = 4; // Velocidad máxima.
	public static final int MIN_VELOCIDAD = 2; // Velocidad mínima.
	public static final int U_VELOCIDAD = 2; // Parámetro utilizado para determinar la gravedad de un choque según su velocidad.
	private static final int TRAMAS_CHOQUE = 2; // Cantidad de imágenes del tanque respecto de la situación de choque.
	private static final int PERIODO_CHOQUE_CHICO = 50; // Duración de los efectos de un choque.
	private static final int PERIODO_CHOQUE_GRANDE = 150; // Duración de los efectos de un choque.
	private static final int TRAMAS_MOVIMIENTO = 18; // Cantidad de imágenes del tanque.
	private static final int PERIODO_MOVIMENTO = 1;
	private final int SUB_PERIODO_CHOQUE = 5; // Duración de un sub período de choque.
	private int contadorSubTramaChoque; // Contador auxiliar para la situación de choque.
	private int vX; // Velocidad horizontal del tanque.
	private int vY; // Velocidad vertical del tanque.
	private int trancoTanque = Tanque.MIN_VELOCIDAD; // Tamaño del tranco de avance del tanque (unidad de avance).
	private int temporizadorMovimento = 0; 
	private int movimientoTrama = 0;
	private boolean teclasHabilitadas = true; // Indicador de habilitación o no de teclado para comandar el tanque.
	private boolean choqueGrande = false; // Indicador de magnitud del último choque.
	
	
	private boolean arriba,abajo,izquierda,derecha; // Booleanos representativas de la directiva de la dirección a adoptar.
	private int direccion = Finals.ARRIBA; // Atributo representativo de la dirección actual del tanque.
	
	private boolean choque = false; // Indicador del estado de choque.
	private int temporizadorChoque = 0; // Temporizador que permite limitar en tiempo los efectos del choque.
	private int choqueTrama = 0; // Índice auxiliar del arreglo las imágenes del tanque.
	
    private int X, Y; // Coordenadas (en píxeles) del tanque.
    private static HashMap<Integer, BufferedImage> imagenes = new HashMap<Integer, BufferedImage>(); // Conjunto de imágenes del tanque, asociadas a una clave cada una mediante un HashMap.
    private static Circuito circuito; // Circuito en el cual está el tanque.
	private int id; // Identificador del tanque.

	private boolean ayuda_sonido = false;
	private static boolean sonido_habilitado = false;
	private Audio audio_movimiento;
	private Audio audio_choque;
	private String nickOponente; 
	private boolean moviendose; 

	public boolean getSonidoHabilitado(){
		return sonido_habilitado;
	}
	
	public void setSonidoHabilitado(boolean sh){
		sonido_habilitado = sh;
	}

	public int getID(){
		return id;
	}
	
	// Método que forza a las teclas a ser soltadas. 
    private void forzarTeclasSueltas(){ 
		arriba=false;
		abajo=false;
		izquierda=false;
		derecha=false;
    }
	
	public int getVelocidad(){
		return trancoTanque;
	}

	public void setVelocidad(int i){ trancoTanque = i;}
	
	// Método que pinta en pantalla la imagen que corresponde al tanque y a su estado.
    public void pintar(Graphics2D g){
		g.drawImage((BufferedImage)imagenes.get(new Integer(direccion * 10000 + choqueTrama * 100+ movimientoTrama)), X,Y, null);
    }
    
	// Método que retorna la dirección actual del tanque.
	public int getDireccion(){
		return direccion;
    }
    
	// Constructor.
	@SuppressWarnings("static-access")
	public Tanque(Circuito circuito, int id) {
		this.id = id;
		this.circuito = circuito;	
		
		// Carga de las imágenes estáticamente.
		if (imagenes.isEmpty())
			try{
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					String nombre = "res/Tanque_arriba"+(TRAMAS_MOVIMIENTO - i-1)+".gif";
					//String nombrec = "res/Tanque_arribac"+(TRAMAS_MOVIMIENTO - i-1)+".gif";
					imagenes.put(new Integer(30000+i), ImageIO.read(getClass().getClassLoader().getResource(nombre)));
					//imagenes.put(new Integer(30100+i), ImageIO.read(getClass().getClassLoader().getResource(nombrec)));
				}
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					imagenes.put(new Integer(Finals.ABAJO    *10000+ i), this.rotarImagen(imagenes.get(30000+ i),180));
					//imagenes.put(new Integer(Finals.ABAJO    *10000 + 100+ i), this.rotarImagen(imagenes.get(30100+ i),180));
				}
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					imagenes.put(new Integer(Finals.DERECHA  *10000+ i), this.rotarImagen(imagenes.get(30000+ i), 90));
					//imagenes.put(new Integer(Finals.DERECHA  *10000 + 100 + i), this.rotarImagen(imagenes.get(30100+ i), 90));
				}
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					imagenes.put(new Integer(Finals.IZQUIERDA*10000+ i), this.rotarImagen(imagenes.get(30000+ i),-90));
					//imagenes.put(new Integer(Finals.IZQUIERDA*10000 + 100 + i), this.rotarImagen(imagenes.get(30100+ i),-90));
				}
			}catch(Exception e){
				System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Tanque, "+e.getClass().getName()+" "+e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
		
		
		audio_movimiento = new Audio("res/waterrun.wav");
		audio_choque = new Audio("res/click.wav");
		
	}
	
	// Método que rota una imagen.
	private BufferedImage rotarImagen(BufferedImage img, int grados){
		AffineTransform rotation = new AffineTransform();
		AffineTransformOp rotator;
		rotation.rotate(Math.toRadians(grados), img.getWidth() / 2,
		img.getHeight() / 2);
		rotator = new AffineTransformOp(rotation,AffineTransformOp.TYPE_BILINEAR);
		return rotator.filter(img, null);
	}
	
	// Método de actuación resumida del tanque, que sólo reproduce o nó un sonido según la situación o no de movimiento. Usado por el tanque oponente.
	public void actuarResumido() {
		if (moviendose){
			if(sonido_habilitado && !ayuda_sonido){
				audio_movimiento.reproduccionLoop();
				ayuda_sonido = true;
			}
		} else {
			audio_movimiento.detener();
			ayuda_sonido = false;
		}
    }
    
	public void setMoviendose(boolean estaMoviendose){
		moviendose = estaMoviendose;
	}
	
	public boolean getMoviendose(){
		return moviendose;
	}
	
	public void choqueResumido(){
		if(sonido_habilitado){ audio_choque.reproduccionSimple();}
	}
	
	// Método de actuación del tanque.
	public void actuar() {
		X+=vX; // Actualización de la posición.
		Y+=vY;
		
		if(circuito.hayColision(this)){ // Detección de colisiones. Responsabilidades del circuito.
			this.choque(false);// En caso de haberla, sufrir efectos del mismo.
		}
		
		if(circuito.llegueAMiMeta(this)){ // Detección de llegada a la meta.
			this.choque(false);
		}
					
		// Efectos del estado de choque.
		if (choque){
			contadorSubTramaChoque++;
			if (contadorSubTramaChoque==SUB_PERIODO_CHOQUE){
				choqueTrama = (choqueTrama + 1) % TRAMAS_CHOQUE;
				contadorSubTramaChoque=0;
			}
			
			temporizadorChoque++;
			teclasHabilitadas = false;
			
			if ((temporizadorChoque == (choqueGrande?PERIODO_CHOQUE_GRANDE:PERIODO_CHOQUE_CHICO))){
				choque = false;
				teclasHabilitadas = true;
				choqueTrama = 0;
			}
		}
		
		// Efectos de animación y reproducción de sonido.
		if (arriba || abajo || derecha || izquierda){
			moviendose = true;
			temporizadorMovimento++;			
			if(sonido_habilitado && !ayuda_sonido){
				audio_movimiento.reproduccionLoop();
				ayuda_sonido = true;
			}
			if (temporizadorMovimento==PERIODO_MOVIMENTO){
				temporizadorMovimento = 0;
				movimientoTrama=(movimientoTrama+trancoTanque)%TRAMAS_MOVIMIENTO;
			}
		}else{ 
			moviendose = false;
			audio_movimiento.detener();
			ayuda_sonido = false;
		}
		
    }
    
	public int getMovimientoTrama(){
		return movimientoTrama;
	}
	
	// Método que actualiza las velocidades según se tengan o no ciertas teclas presionadas.
	private void actualizarVelocidades(){
		vX=0; vY=0; 
		if (abajo)		vY = +trancoTanque;
		if (arriba)		vY = -trancoTanque;
		if (izquierda)	vX = -trancoTanque;
		if (derecha)	vX = +trancoTanque;
	}
  
	// Métodos básicos de posicionamiento.
	public int getX(){return X;}
	public int getY(){return Y;}
	public void setX(int i){X=i;}
	public void setY(int i){Y=i;}
		
	// Conjunto de métodos de respuesta al teclado.
	public void irArriba(){
		forzarTeclasSueltas();
		if (teclasHabilitadas){ 
			arriba = true;
			direccion = Finals.ARRIBA;
		}
		actualizarVelocidades();
	}
	
	public void noIrArriba(){ 
		arriba=false;
		actualizarVelocidades();
	}
	
	
	public void irAbajo(){
		forzarTeclasSueltas();
		if (teclasHabilitadas){ 
			abajo  = true;
			direccion = Finals.ABAJO;
		}
		actualizarVelocidades();
	}

	public void noIrAbajo(){ 
		
		abajo=false;
		actualizarVelocidades();
	}
	public void irIzquierda(){
		forzarTeclasSueltas();
		if (teclasHabilitadas){ 
			izquierda  = true;
			direccion = Finals.IZQUIERDA;
		}
		actualizarVelocidades();
	}
	public void noIrIzquierda(){ 
		izquierda=false;
		actualizarVelocidades();
	}
	public void irDerecha(){ 
		forzarTeclasSueltas();
		if (teclasHabilitadas){
			derecha = true;
			direccion = Finals.DERECHA;
		}
		actualizarVelocidades();	
	}
	public void noIrDerecha(){
		derecha=false;
		actualizarVelocidades();
	}
	
	

	
	public void setDireccion(int direccion){
		this.direccion = direccion;
	}
	
	// Método que genera los efectos de un choque en el tanque.
	public void choque(boolean agravante){ 
		teclasHabilitadas = false;
		choque = true;              if(sonido_habilitado){ audio_choque.reproduccionSimple();} // Reproduce sonido para choque local.
		forzarTeclasSueltas();
		actualizarVelocidades();
		temporizadorChoque=0;
		choqueGrande = (this.trancoTanque==Tanque.MAX_VELOCIDAD)  || agravante; 
		trancoTanque=Tanque.MIN_VELOCIDAD; // Modifica velocidad despues de comprobar el tipo de choque.
		contadorSubTramaChoque=0;
		
	}
	

	public void setTodo(int x, int y, int direccion, int movimientoTrama, int choqueTrama, boolean moviendose){
		this.X = x;
		this.Y = y;
		this.moviendose = moviendose;
		this.direccion = direccion;
		this.movimientoTrama = movimientoTrama;
		this.choqueTrama = choqueTrama;
	}
	public int getChoqueTrama(){
		return choqueTrama;
	}
	
	public String getNickOponente(){
		System.out.println("Tanque.getNickOponente():" + nickOponente);
		return nickOponente;
	}
	public void setNickOponente(String nickOponente){
		this.nickOponente = nickOponente;
	}
	public void detenerReproduccion(){
		audio_movimiento.detener();
	}
}	
