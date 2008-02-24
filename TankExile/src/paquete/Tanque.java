package paquete;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Tanque implements ElementoDeJuego, Serializable, Imitable{

	public transient static final int U_VELOCIDAD = 2; // Parámetro utilizado para determinar la gravedad de un choque según su velocidad.
	public transient static final int MAX_VELOCIDAD = U_VELOCIDAD * 2; // Velocidad máxima.
	public transient static final int MIN_VELOCIDAD = U_VELOCIDAD; // Velocidad mínima.
	private transient static final int TRAMAS_CHOQUE = 2; // Cantidad de imágenes del tanque respecto de la situación de choque.
	private transient static final int PERIODO_CHOQUE_CHICO = 50; // Duración de los efectos de un choque.
	private transient static final int PERIODO_CHOQUE_GRANDE = 150; // Duración de los efectos de un choque.
	private transient static final int TRAMAS_MOVIMIENTO = 18; // Cantidad de imágenes del tanque.
	private transient static final int PERIODO_MOVIMENTO = 1;
	private transient final int SUB_PERIODO_CHOQUE = 5; // Duración de un sub período de choque.
	
	private int contadorSubTramaChoque; // Contador auxiliar para la situación de choque.
	private Rectangle bounds = new Rectangle(0,0,Finals.BLOQUE_LADO_LONG, Finals.BLOQUE_LADO_LONG);
	private Point velocidad = new Point(0,0);
	private int trancoTanque = Tanque.MIN_VELOCIDAD; // Tamaño del tranco de avance del tanque (unidad de avance).
	private transient int temporizadorMovimento = 0; 

	private int movimientoTrama = 0;
	private transient boolean teclasHabilitadas = true; // Indicador de habilitación o no de teclado para comandar el tanque.
	private transient boolean choqueGrande = false; // Indicador de magnitud del último choque.
	
	private boolean arriba,abajo,izquierda,derecha; // Booleanos representativas de la directiva de la dirección a adoptar.
	private int direccion = Finals.ARRIBA; // Atributo representativo de la dirección actual del tanque.
	
	private boolean choque = false; // Indicador del estado de choque.
	private int temporizadorChoque = PERIODO_CHOQUE_GRANDE; // Temporizador que permite limitar en tiempo los efectos del choque.
	private int choqueTrama = 0; // Índice auxiliar del arreglo las imágenes del tanque.
	
    private transient static HashMap<Integer, BufferedImage> imagenes = new HashMap<Integer, BufferedImage>(); // Conjunto de imágenes del tanque, asociadas a una clave cada una mediante un HashMap.
	private int id; // Identificador del tanque.
	
	private transient static boolean sonido_habilitado = false;
	private transient Audio audio_movimiento;
	private transient Audio audio_choque;
	//private String nickOponente; 
	private boolean moviendose;
	private transient boolean moviendose_remoto=false;

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
		g.drawImage((BufferedImage)imagenes.get(new Integer(direccion * 10000 + choqueTrama * 100+ movimientoTrama)), bounds.x,bounds.y, null);
    }
    
	// Método que retorna la dirección actual del tanque.
	public int getDireccion(){
		return direccion;
    }
    
	// Constructor.
	public Tanque(int id) {
		this.id = id;
		// Carga de las imágenes estáticamente.
		if (imagenes.isEmpty())
			try{
				for (int i = 0; i < Tanque.TRAMAS_MOVIMIENTO;i++){
					String nombre = "res/Tanque_arriba"+(TRAMAS_MOVIMIENTO - i-1)+".gif";
					imagenes.put(new Integer(30000+i), ImageIO.read(getClass().getClassLoader().getResource(nombre)));
					imagenes.put(new Integer(Finals.ABAJO    *10000+ i), this.rotarImagen(imagenes.get(30000+ i),180));
					imagenes.put(new Integer(Finals.DERECHA  *10000+ i), this.rotarImagen(imagenes.get(30000+ i), 90));
					imagenes.put(new Integer(Finals.IZQUIERDA*10000+ i), this.rotarImagen(imagenes.get(30000+ i),-90));
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
	
	public void setMoviendose(boolean estaMoviendose){moviendose = estaMoviendose;}
	public boolean getMoviendose(){return moviendose;}
	
	// Método de actuación del tanque.
	public void actuar() {
		bounds.x+=velocidad.x; // Actualización de la posición.
		bounds.y+=velocidad.y;
		
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
			if (temporizadorMovimento==PERIODO_MOVIMENTO){
				temporizadorMovimento = 0;
				movimientoTrama=(movimientoTrama+trancoTanque)%TRAMAS_MOVIMIENTO;
			}
		}else{moviendose = false;}
		
		if (moviendose || moviendose_remoto) {
			if(sonido_habilitado){
				audio_movimiento.reproduccionLoop();
			}
		} else {
			audio_movimiento.detener();
		}
		
    }
    
	public int getMovimientoTrama(){return movimientoTrama;}
	// Método que actualiza las velocidades según se tengan o no ciertas teclas presionadas.
	private void actualizarVelocidades(){
		velocidad.x=0; velocidad.y=0; 
		if (abajo)		velocidad.y = +trancoTanque;
		if (arriba)		velocidad.y = -trancoTanque;
		if (izquierda)	velocidad.x = -trancoTanque;
		if (derecha)	velocidad.x = +trancoTanque;
	}
	// Métodos básicos de posicionamiento.
	public Point getPos(){return bounds.getLocation();}
	public void setX(int i){bounds.x=i;}
	public void setY(int i){bounds.y=i;}
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
		choque = true;
		if(sonido_habilitado){
			audio_choque.reproduccionSimple();
		} // Reproduce sonido para choque local.
		forzarTeclasSueltas();
		actualizarVelocidades();
		temporizadorChoque=0;
		choqueGrande = (this.trancoTanque==Tanque.MAX_VELOCIDAD)  || agravante; // Cuando un choque se quiere forzar a ser grande, se utiliza 'agravante' en true.
		trancoTanque = Tanque.MIN_VELOCIDAD; // Modifica velocidad despues de comprobar el tipo de choque.
		contadorSubTramaChoque=0;
	}
	
	public int getChoqueTrama(){
		return choqueTrama;
	}
	
	public void detenerReproduccion(){
		audio_movimiento.detenerTotal();
		this.audio_choque.detenerTotal();
	}
	
	public Rectangle getBounds(){
		return bounds;
	}

	public void eventoChoque(ElementoDeJuego contraQuien) {
		try {
			Class[] arregloDeClases = {contraQuien.getClass()};
			Object[] arrayArgumentos = {contraQuien};
			this.getClass().getMethod("eventoChoqueCon" + contraQuien.getClass().getSimpleName(), (Class[]) arregloDeClases).invoke(this, arrayArgumentos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	public void eventoChoqueConTanque(Tanque tanque){
		corregirPosicion(tanque);
		choque(false);
	}
	
	public void eventoChoqueConMuro(Muro muro){
		corregirPosicion(muro);
		choque(false);
	}
	
	public void eventoChoqueConMeta(Meta meta){}
	
	public void eventoChoqueConBola(Bola bola){
		if(bola.getBuena())
			setVelocidad(Tanque.MAX_VELOCIDAD);
		else
			choque(true);
	}

	private void corregirPosicion(ElementoDeJuego objeto){
		switch (this.getDireccion()){
			// Según la dirección del tanque, este es llevado hacia atrás hasta la condición de no solapamiento.
			case Finals.ABAJO:		while(this.getBounds().intersects(objeto.getBounds())){bounds.y = (bounds.y-Tanque.U_VELOCIDAD);}
			case Finals.ARRIBA:		while(this.getBounds().intersects(objeto.getBounds())){bounds.y = (bounds.y+Tanque.U_VELOCIDAD);}
			case Finals.IZQUIERDA:	while(this.getBounds().intersects(objeto.getBounds())){bounds.x= (bounds.x+Tanque.U_VELOCIDAD);}
			case Finals.DERECHA:	while(this.getBounds().intersects(objeto.getBounds())){bounds.x= (bounds.x-Tanque.U_VELOCIDAD);}
		}
	}
	
	public void imitar(Imitable objetoAImitar) throws RemoteException {
		Tanque tanqueAImitar = (Tanque)objetoAImitar;
		this.bounds.setLocation(tanqueAImitar.getPos().x, tanqueAImitar.getPos().y);
		//this.bounds.setLocation(tanqueAImitar.getPos()); // USAR ESTO
		this.moviendose_remoto = tanqueAImitar.getMoviendose();
		this.direccion = tanqueAImitar.getDireccion();
		this.movimientoTrama = tanqueAImitar.getMovimientoTrama();
		this.choqueTrama = tanqueAImitar.getChoqueTrama();
		if (tanqueAImitar.getChoque()) this.choque(false);
	}

	public boolean getChoque(){
		return (this.temporizadorChoque<2);
	}
	public Object[] getParametros() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}	
