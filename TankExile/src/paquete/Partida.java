package paquete;

import java.awt.Image;
import presentacion.*;
import presentacion.Conexion;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Clase que contiene en sí el hilo principal del juego. Genera el circuito y los tanques, los hace actuar, y se encarga de pintarlos.
public class Partida extends Canvas implements Finals, Runnable{
	private BufferStrategy estrategia; // Atributo que permite establecer el Doble Buffering para pintar la pantalla.
	private Circuito circuito; // Circuito a ser creado para correr la partida.
	private ArrayList<Bloque> bloquesAPintar; // Conjunto de bloques pertenecientes al circuito a ser pintados.
	
	private Tanque tanquePropio; // Tanque del jugador en el host.
	private Tanque tanqueLocalLigadoOponente; // Tanque del oponente en el host.
	private Jugador jugador; // Jugador, atributo encargado de hacer el listening del teclado para comandar al tanque propio.

	private int yoID; // Representa el ID del jugador en este host.
	private int otroID; // Representa el ID del jugador oponente.
	//private String iPOponente; // Representa la dirección IP en la red del host del jugador oponente.
	private Conexion conexion; // Objeto utilizado para todo lo relacionado a la comunicación entre ambos hosts.
	private String nombreCircuitoTXT; // Atributo que representa el nombre del archivo del circuito.
	//private JFrame ventana;
	
	// Contstructor. Genera los elementos básicos de una aplicación del tipo juego.
	public Partida(String nombreCircuitoTXT, Conexion conexion) {
		JFrame ventana = new JFrame("TankExile"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		this.setBounds(0,0,Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA); // Establecimiento de las dimensiones de este objeto Partida.
		
                
		//panel.setBounds(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA);
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA)); // Establecimiento de las dimensiones del panel.
		panel.setLayout(new GridLayout());
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).
		
		ventana.setBounds(0,0,Finals.ANCHO_VENTANA+3,Finals.ALTO_VENTANA); // Establecimiento de las dimensiones de la ventana.
		ventana.setVisible(true); // Ventana visible.
		
		
		ventana.addWindowListener(new WindowAdapter() { // Ventana tiene escucha, una clase anónima, para el cierre.
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		ventana.setResizable(false); // La ventana no es de tamaño ajustable.
		this.createBufferStrategy(2); // Es creado sobre este canvas una estrategia de buffering, de dos buffers.
		estrategia = getBufferStrategy(); // Sobre este objeto se aplicará el método de paint. Este realizará por sí mismo el doble buffering.
		this.requestFocus(); // El foco es tomado.
		
		this.conexion = conexion;
		this.yoID = conexion.getID()%2; // Son asignados los valores de ID e IP del oponente.
		this.otroID = (conexion.getID()+1)%2;
		//this.iPOponente = iPOponente;
		this.nombreCircuitoTXT = nombreCircuitoTXT; // Asignación del nombre del archivo del circuito.
	}
	// Método que arranca la escena de la partida. Involucra la inicialización de los elementos principales del juego en sí.
	public void iniciarEscena() {
		bloquesAPintar = new ArrayList<Bloque>(); // Conjunto de bloques del circuito que serán pintados en cada ciclo de actualización.

		circuito = new Circuito(bloquesAPintar, nombreCircuitoTXT); // Creación del circuito de juego.
		
		tanquePropio = new Tanque(this, circuito, yoID); // Creación del tanque comandado por el jugador en este host.
		tanqueLocalLigadoOponente = new Tanque(this, circuito, otroID); // Creación del tanque que será ligado al registro de RMI para ser comandado por el host remoto.
		conexion.servirTanqueLocalOponente(tanqueLocalLigadoOponente); // El tanque anterior es puesto a disposición del host remoto.
		conexion.setTanquePropio(tanquePropio); // La conexión esta lista para ser establecida, el hilo conexión observará al tanque y con sus parámetros comandará al tanque remoto puesto en el registro de RMI.
		
		jugador = new Jugador(tanquePropio); // Un jugador es creado para comandar el tanque propio del host.
		this.addKeyListener(jugador); // El jugador comienza a escuchar el teclado.
		this.addMouseListener(null);
		Thread hiloTanqueRemoto = new Thread(conexion.getHiloTanqueRemoto());
		
		do{
			try{
				conexion.establecerComunicacionTanqueRemoto(); // La conexión es establecida.
			}catch(Exception e){
				System.out.println("Intento fallido para obtener tanque remoto. Intentando de nuevo...");
				try {Thread.sleep(1000);} catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
			}
		}while(!conexion.tanqueListo());
		
		
		// Ambos tanques son ubicados en sus metas correspondientes.
		tanquePropio.setX(circuito.getMeta(yoID).getX());
		tanquePropio.setY(circuito.getMeta(yoID).getY());
		tanqueLocalLigadoOponente.setX(circuito.getMeta(otroID).getX());
		tanqueLocalLigadoOponente.setY(circuito.getMeta(otroID).getY());
		
		
		
		hiloTanqueRemoto.start();
				
				// El hilo conexion arranca a comandar al tanque remoto observando el comportamiento del tanque propio.
		
	}
	
	// Método que llama a la actuación de cada tanque.
	public void actualizarEscena(){
		tanquePropio.actuar();
		tanqueLocalLigadoOponente.actuar();
		
	}
	
	// Método encargado de brindar la imagen correcta (representativa del estado del tanque) para que esta sea pintada en pantalla.
	public void pintarEscena() {
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();
		g.setColor(Color.LIGHT_GRAY);
            
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		for (int i = 0; i < bloquesAPintar.size(); i++) {
			((Bloque)bloquesAPintar.get(i)).paint(g);
		}
		tanquePropio.pintar(g);
		tanqueLocalLigadoOponente.pintar(g);
		
		estrategia.show();
	}
	
	// Método que contiene el bucle de ejecución principal del juego.
	public void  jugar(){
		iniciarEscena();
		Thread hilo = new Thread(this);
		hilo.start();

	}

		
	/*
	public static void main(String[] args) {
		
		String ipaca = new String("192.168.0.7"); 
		String ipalla = new String("192.168.0.101"); //con 1
		String circuito = new String("circuito2.txt");
		Conexion conexion = new Conexion(null);

		conexion.bindearMisArchivos();
		
		do{
			try{
				conexion.ponerADisposicionArchivosRemotos();
			}catch(Exception e){
				System.out.println("Intento fallido para obtener archivos remotos. Intentando de nuevo...");
				try {Thread.sleep(1000);} catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
			}
		}while(!conexion.archivosListo());
		
		try {
			conexion.copiarDeHostRemoto("circuiton.txt", "copio.txt");
			conexion.enviarAHostRemoto("circuiton.txt", "copio.txt");
		} catch (IOException ex) {
			System.err.println("Error al intentar copiar en el método de Conexion copiarDeHostRemoto.");
			Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		Partida tank_exile = new Partida(0, circuito, conexion);
		tank_exile.jugar();
		
	}
	*/
	public void run() {
		while(isVisible()){
			actualizarEscena();
			pintarEscena();
			try{ 
				Thread.sleep(Finals.PERIODO);
			}catch(InterruptedException e){e.printStackTrace();}
		}
	}
	
}
