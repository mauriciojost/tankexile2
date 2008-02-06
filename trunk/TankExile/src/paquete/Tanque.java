package paquete;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;


public class Tanque implements Controlable{
	private static final int TRAMAS_CHOQUE = 2; // Cantidad de imágenes del tanque.
	private static final int PERIODO_CHOQUE = 50; // Duración de los efectos de un choque.
	private int TRANCO_TANQUE = 1; // Tamaño del tranco de avance del tanque.
	private int vX; // Velocidad horizontal del tanque.
	private int vY; // Velocidad vertical del tanque.
	
	private boolean arriba,abajo,izquierda,derecha; // Booleanos representativas de la directiva de la dirección a adoptar.
	private int direccion = Finals.ARRIBA; // Atributo representativo de la dirección actual del tanque.
	
	private boolean choque = false; // Indicador del estado de choque.
	private int temporizadorChoque = 0; // Temporizador que permite limitar en tiempo los efectos del choque.
	private int choqueTrama = 0; // Índice auxiliar del arreglo las imágenes del tanque.
	
    private static Finals constantes; // Interfaz con constantes útiles.
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
		g.drawImage((BufferedImage)imagenes.get(new Integer(direccion * 10+ choqueTrama) ), X,Y, constantes);			
    }
    
	// Método que retorna la dirección actual del tanque.
	public int getDireccion(){
		return direccion;
    }
    
	// Constructor.
	public Tanque(Finals constantes, Circuito circuito, int id) {
		this.id = id;
		this.circuito = circuito;	
		this.constantes = constantes;
		
		// Carga de las imágenes estáticamente.
		if (imagenes.isEmpty())
			try{
				imagenes.put(new Integer(30), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_up0.gif")));
				imagenes.put(new Integer(31), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_up1.gif")));
				imagenes.put(new Integer(00), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_down0.gif")));
				imagenes.put(new Integer(01), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_down1.gif")));
				imagenes.put(new Integer(20), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_right0.gif")));
				imagenes.put(new Integer(21), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_right1.gif")));
				imagenes.put(new Integer(10), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_left0.gif")));
				imagenes.put(new Integer(11), ImageIO.read(getClass().getClassLoader().getResource("res/Tanque_left1.gif")));
			}catch(Exception e){
				System.out.println("Error: no se ha podido realizar la carga de imágenes de la clase Tanque, "+e.getClass().getName()+" "+e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}
	}
	
	// Método de actuación del tanque.
	public void actuar() {
		
		if(circuito.hayColision(this)){ // Detección de colisiones. Responsabilidades del circuito.
			this.choque(); // En caso de haberla, sufrir efectos del mismo.
		}
		
		if(circuito.llegueAMiMeta(this)){ // Detección de llegada a la meta.
			this.choque();
		}
		
		X+=vX; // Actualización de la posición.
		Y+=vY;
					
		// Efectos del estado de choque.
		if (choque){
			choqueTrama=(choqueTrama+1)%TRAMAS_CHOQUE;
			temporizadorChoque++;
			if (temporizadorChoque==PERIODO_CHOQUE){
				choque=false;
			}
		}else{
			choqueTrama=0;
		}
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
		if (!choque){
			arriba = true;
			direccion = Finals.ARRIBA;
		}
		actualizarVelocidades();
	}
	
	public void irAbajo(){
		forzarTeclasSueltas();
		if (!choque){
			abajo  = true;
			direccion = Finals.ABAJO;
		}
		actualizarVelocidades();
	}
	
	public void irIzquierda(){
		forzarTeclasSueltas();
		if (!choque){
			izquierda  = true;
			direccion = Finals.IZQUIERDA;
		}
		actualizarVelocidades();
	}
	
	public void irDerecha(){
		forzarTeclasSueltas();
		if (!choque){
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
		choque = true;
		forzarTeclasSueltas();
		actualizarVelocidades();
		temporizadorChoque=0;
	}
	
}	
