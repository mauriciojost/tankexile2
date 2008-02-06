package paquete;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Tanque implements Controlable{
	private static final int TRAMAS_CHOQUE = 2; // Cantidad de imágenes del tanque.
	private static final int PERIODO_CHOQUE = 50; // Duración de los efectos de un choque.
	private static final int TRAMAS_MOVIMIENTO = 16; // Cantidad de imágenes del tanque.
	private static final int PERIODO_MOVIMENTO = 1;
	private int TRANCO_TANQUE = 1; // Tamaño del tranco de avance del tanque.
	private int vX; // Velocidad horizontal del tanque.
	private int vY; // Velocidad vertical del tanque.
	private boolean teclasHabilitadas = true;
	
	private int temporizadorMovimento = 0;
	
	private int movimientoTrama = 0;
	
	
	private boolean arriba,abajo,izquierda,derecha; // Booleanos representativas de la directiva de la dirección a adoptar.
	private int direccion = Finals.ARRIBA; // Atributo representativo de la dirección actual del tanque.
	
	private boolean choque = false; // Indicador del estado de choque.
	private int temporizadorChoque = 0; // Temporizador que permite limitar en tiempo los efectos del choque.
	private int choqueTrama = 0; // Índice auxiliar del arreglo las imágenes del tanque.
	
    private int X, Y; // Coordenadas (en píxeles) del tanque.
    private static HashMap<Integer, BufferedImage> imagenes = new HashMap<Integer, BufferedImage>(); // Conjunto de imágenes del tanque, asociadas a una clave cada una mediante un HashMap.
    private static Circuito circuito; // Circuito en el cual está el tanque.
	private int id; // Identificador del tanque.

	
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
		return TRANCO_TANQUE;
	}
	
	// Método que pinta en pantalla la imagen que corresponde al tanque y a su estado.
    public void pintar(Graphics2D g){
		g.drawImage((BufferedImage)imagenes.get(new Integer(direccion * 10000 + choqueTrama * 100+ movimientoTrama)), X,Y, null);
    }
    
	// Método que retorna la dirección actual del tanque.
	public int getDireccion(){
		return direccion;
    }
    
	private String convertirDireccionAString(int direccion){
		switch (direccion){
			
			/*
				ABAJO		0
				IZQUIERDA	1
				DERECHA		2
				ARRIBA		3
			 */
			case 0: return "abajo";
			case 1: return "izquierda";
			case 2: return "derecha";
			case 3: return "arriba";
			default: return "";
		}
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
					imagenes.put(new Integer(30000+i), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_arriba"+i+".gif")));
					imagenes.put(new Integer(30100+i), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_arribac"+i+".gif")));
				}
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					imagenes.put(new Integer(Finals.ABAJO    *10000+ i), this.rotarImagen(imagenes.get(30000+ i),180));
					imagenes.put(new Integer(Finals.ABAJO    *10000 + 100+ i), this.rotarImagen(imagenes.get(30100+ i),180));
				}
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					imagenes.put(new Integer(Finals.DERECHA  *10000+ i), this.rotarImagen(imagenes.get(30000+ i), 90));
					imagenes.put(new Integer(Finals.DERECHA  *10000 + 100 + i), this.rotarImagen(imagenes.get(30100+ i), 90));
				}
				for (int i = 0; i < this.TRAMAS_MOVIMIENTO;i++){
					imagenes.put(new Integer(Finals.IZQUIERDA*10000+ i), this.rotarImagen(imagenes.get(30000+ i),-90));
					imagenes.put(new Integer(Finals.IZQUIERDA*10000 + 100 + i), this.rotarImagen(imagenes.get(30100+ i),-90));
				}
			}catch(Exception e){
				System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Tanque, "+e.getClass().getName()+" "+e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
	}
	
	private BufferedImage rotarImagen(BufferedImage img, int grados){
		AffineTransform rotation = new AffineTransform();
		AffineTransformOp rotator;
		rotation.rotate(Math.toRadians(grados), img.getWidth() / 2,
		img.getHeight() / 2);
		rotator = new AffineTransformOp(rotation,AffineTransformOp.TYPE_BILINEAR);
		return rotator.filter(img, null);
	}
	
	// Método de actuación del tanque.
	public void actuar() {
		
		this.deshabilitarTeclas();
		
		X+=vX; // Actualización de la posición.
		Y+=vY;
		
		
		if(circuito.hayColision(this)){ // Detección de colisiones. Responsabilidades del circuito.
			this.choque();// En caso de haberla, sufrir efectos del mismo.
		}
		
		//if(circuito.llegueAMiMeta(this)){ // Detección de llegada a la meta.
		//	this.choque();
		//}
		
		
		this.habilitarTeclas();
					
		// Efectos del estado de choque.
		if (choque){
			choqueTrama = (choqueTrama + 1) % TRAMAS_CHOQUE;
			temporizadorChoque++;
			teclasHabilitadas = false;
			if (temporizadorChoque == PERIODO_CHOQUE){
				choque = false;
				teclasHabilitadas = true;
				choqueTrama = 0;
			}
		}
		
		if (arriba || abajo || derecha || izquierda){
			temporizadorMovimento++;
			if (temporizadorMovimento==PERIODO_MOVIMENTO){
				temporizadorMovimento = 0;
				movimientoTrama=(movimientoTrama+TRANCO_TANQUE)%TRAMAS_MOVIMIENTO;
			}
		}
		
    }
    
	public void deshabilitarTeclas(){
		teclasHabilitadas = false;
	}
	
	public void habilitarTeclas(){
		teclasHabilitadas = true;
	}
	
	public int getMovimientoTrama(){
		return movimientoTrama;
	}
	
	// Método que actualiza las velocidades según se tengan o no ciertas teclas presionadas.
	private void actualizarVelocidades(){
		vX=0; vY=0;
		if (abajo)		vY = +TRANCO_TANQUE;
		if (arriba)		vY = -TRANCO_TANQUE;
		if (izquierda)	vX = -TRANCO_TANQUE;
		if (derecha)	vX = +TRANCO_TANQUE;
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
	
	public void irAbajo(){
		forzarTeclasSueltas();
		if (teclasHabilitadas){
			abajo  = true;
			direccion = Finals.ABAJO;
		}
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
	
	public void irDerecha(){
		forzarTeclasSueltas();
		if (teclasHabilitadas){
			derecha = true;
			direccion = Finals.DERECHA;
		}
		actualizarVelocidades();
	}
	
	public void acelerar(){
		TRANCO_TANQUE = 2;
		actualizarVelocidades();
	}
	public void noAcelerar(){
		TRANCO_TANQUE = 1;
		actualizarVelocidades();
	}
	
	public void noIrArriba(){
		arriba=false;
		actualizarVelocidades();
	}
	public void noIrAbajo(){
		abajo=false;
		actualizarVelocidades();
	}
	public void noIrIzquierda(){
		izquierda=false;
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
	public void choque(){
		teclasHabilitadas = false;
		choque = true;
		forzarTeclasSueltas();
		actualizarVelocidades();
		temporizadorChoque=0;
	}
	public void setTodo(int x, int y, int direccion, int movimientoTrama, int choqueTrama){
		this.X = x;
		this.Y = y;
		this.direccion = direccion;
		this.movimientoTrama = movimientoTrama;
		this.choqueTrama = choqueTrama;
	}
	public int getChoqueTrama(){
		return choqueTrama;
	}
}	
