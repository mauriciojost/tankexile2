package paquete;

import presentacion.*;
import presentacion.Conexion;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

// Clase que contiene en sí el hilo principal del juego. Genera el circuito y los tanques, los hace actuar, y se encarga de pintarlos.
public class Partida extends Canvas implements Finals, Runnable{
    private BufferStrategy estrategia; // Atributo que permite establecer el Doble Buffering para pintar la pantalla.
    private Circuito circuito; // Circuito a ser creado para correr la partida.

    private Tanque tanquePropio; // Tanque del jugador en el host.
    private Tanque tanqueLocalLigadoOponente; // Tanque del oponente en el host.
    private Jugador jugador; // Jugador, atributo encargado de hacer el listening del teclado para comandar al tanque propio.
    private Bola bolaBuena;
    private Bola bolaMala;

    private int yoID; // Representa el ID del jugador en este host.
    private int otroID; // Representa el ID del jugador oponente.
    //private String iPOponente; // Representa la dirección IP en la red del host del jugador oponente.
    private static Conexion conexion; // Objeto utilizado para todo lo relacionado a la comunicación entre ambos hosts.
    private static String nombreCircuitoTXT; // Atributo que representa el nombre del archivo del circuito.
	private static PrePartida1 prePartida;
	private Thread hiloTanqueRemoto;
	private Thread hiloBolasRemotas;
	private Thread hiloCircuitoRemoto;
	private boolean correrHilos = true;
	private static Partida instanciaPartida;
	private JFrame ventana;
	
    // Contstructor. Genera los elementos básicos de una aplicación del tipo juego.
    public Partida(String nombreCircuitoTXT, Conexion conexion, PrePartida1 prePartida) {
		instanciaPartida = this;
		this.prePartida = prePartida;
		ventana = new JFrame("TankExile"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		//this.setBounds(0,0,Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA); // Establecimiento de las dimensiones de este objeto Partida.
		ventana.setLayout(new GridLayout());
		panel.setBounds(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA);
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA)); // Establecimiento de las dimensiones del panel.
		panel.setLayout(new GridLayout());
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).

		ventana.setBounds(0,0,Finals.ANCHO_VENTANA+6,Finals.ALTO_VENTANA+50); // Establecimiento de las dimensiones de la ventana.
		ventana.setVisible(true); // Ventana visible.

		ventana.addWindowListener(
			new WindowAdapter() { // Ventana tiene escucha, una clase anónima, para el cierre.
				public void windowClosing(WindowEvent e) {
					finalizar();
				}
			}
		);


		ventana.setResizable(false); // La ventana no es de tamaño ajustable.
		this.createBufferStrategy(2); // Es creado sobre este canvas una estrategia de buffering, de dos buffers.
		estrategia = getBufferStrategy(); // Sobre este objeto se aplicará el método de paint. Este realizará por sí mismo el doble buffering.
		

		this.conexion = conexion;
		this.yoID = conexion.getID()%2; // Son asignados los valores de ID e IP del oponente.
		this.otroID = (conexion.getID()+1)%2;
		//this.iPOponente = iPOponente;
		this.nombreCircuitoTXT = nombreCircuitoTXT; // Asignación del nombre del archivo del circuito.
		
    }
    // Método que arranca la escena de la partida. Involucra la inicialización de los elementos principales del juego en sí.
    public void iniciarEscena() {
		ventana.setVisible(true);
		this.requestFocus(); // El foco es tomado.
		
		circuito = new Circuito(nombreCircuitoTXT); // Creación del circuito de juego.
		circuito.setConexion(conexion);
		tanquePropio = new Tanque(circuito, yoID); // Creación del tanque comandado por el jugador en este host.
		tanqueLocalLigadoOponente = new Tanque(circuito, otroID); // Creación del tanque que será ligado al registro de RMI para ser comandado por el host remoto.
		conexion.bindearTanqueLocalOponente(tanqueLocalLigadoOponente); // El tanque anterior es puesto a disposición del host remoto.
		conexion.setTanquePropio(tanquePropio); // La conexión esta lista para ser establecida, el hilo conexión observará al tanque y con sus parámetros comandará al tanque remoto puesto en el registro de RMI.
		
		jugador = new Jugador(tanquePropio); // Un jugador es creado para comandar el tanque propio del host.
		this.addKeyListener(jugador); // El jugador comienza a escuchar el teclado.

		do{
			try{
			conexion.ponerADisposicionTanqueRemoto(); // La conexión es establecida.
			}catch(Exception e){
				System.out.println("Intento fallido para obtener tanque remoto. Intentando de nuevo...");
				try {Thread.sleep(Finals.ESPERA_CONEXION);} catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
			}
		}while(!conexion.tanqueListo());

		hiloTanqueRemoto = new Thread(conexion.getHiloManejadorDeTanqueRemoto());
		// Ambos tanques son ubicados en sus metas correspondientes.
		tanquePropio.setX(circuito.getMeta(yoID).getX());
		tanquePropio.setY(circuito.getMeta(yoID).getY());
		tanqueLocalLigadoOponente.setX(circuito.getMeta(otroID).getX());
		tanqueLocalLigadoOponente.setY(circuito.getMeta(otroID).getY());

		hiloTanqueRemoto.start();

		bolaBuena = new Bola(true);
		bolaMala = new Bola(false);
		conexion.setBolasLocales(bolaBuena, bolaMala);
		conexion.bindearBolasLocales();
		System.out.println("Servidor de bolas listo.");
		if (this.yoID == 1){
			bolaBuena.start();
			bolaMala.start();
			do{
				try{
					conexion.ponerADisposicionBolasRemotas();
				}catch(Exception e){
					try { Thread.sleep(Finals.ESPERA_CONEXION); } catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
					System.out.println("Intento fallido por obtener bolas remotas... Reintentando...");
				}
			}while(!conexion.bolasListo());
			System.out.println("Bolas remotas a disposición.");
			hiloBolasRemotas = new Thread(conexion.getHiloManejadorDeBolas());
			hiloBolasRemotas.start();
		}
		
		conexion.setCircuitoPropio(circuito);
		conexion.bindearCircuitoLocal();
		
		do{
				try{
					conexion.ponerADisposicionCircuitoRemoto();
				}catch(Exception e){
					try { Thread.sleep(Finals.ESPERA_CONEXION); } catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
					System.out.println("Intento fallido por obtener circuito remoto... Reintentando...");
				}
		}while(!conexion.circuitoListo());
		System.out.println("Circuito remoto a disposición.");
		hiloCircuitoRemoto = new Thread(conexion.getHiloManejadorDeCircuitoRemoto());
		hiloCircuitoRemoto.start();
    }

    // Método que llama a la actuación de cada tanque.
    public void actualizarEscena(){
		tanquePropio.actuar();
		//tanqueLocalLigadoOponente.actuar(); // Mauricio: ¿Realmente hace falta?.
    }
	
	public PrePartida1 getPrePartida(){
		return	prePartida;
	}
    // Método encargado de brindar la imagen correcta (representativa del estado del tanque) para que esta sea pintada en pantalla.
    public void pintarEscena() {
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();
		g.setColor(Color.WHITE);

		g.fillRect(0,0,this.getWidth(),this.getHeight());
		circuito.pintar(g);

		tanquePropio.pintar(g);
		tanqueLocalLigadoOponente.pintar(g);
		bolaBuena.pintar(g);
		bolaMala.pintar(g);
		estrategia.show();
    }

    // Método que contiene el bucle de ejecución principal del juego.
    public void jugar(){
		iniciarEscena();
		Thread hiloJuego = new Thread(this);
		hiloJuego.start();
    }

	public void stopHilos(){
		correrHilos = false;
	}
	
    public void run() {
		while(correrHilos){
			actualizarEscena();
			pintarEscena();
			try{ 
				Thread.sleep(Finals.PERIODO);
			}catch(InterruptedException e){e.printStackTrace();}
		}
    }
	public static Partida getPartida(){
		return instanciaPartida;
	}
	public void finalizar(){
		try {		
			this.getPrePartida().setVisible(true);
			this.stopHilos();
			conexion.stopHilos();
			this.bolaBuena.stopHilo();
			this.bolaMala.stopHilo();
			ventana.dispose();
		} catch (Throwable ex) {
			Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
		}
		conexion.desbindearTodo(false);
	}
}
