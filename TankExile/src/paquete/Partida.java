package paquete;

import java.rmi.RemoteException;
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
public class Partida extends Canvas implements Runnable{
    private BufferStrategy estrategia; // Atributo que permite establecer el Doble Buffering para pintar la pantalla.
    private Circuito circuito; // Circuito a ser creado para correr la partida.

    private Tanque tanquePropio; // Tanque del jugador en el host.
    private Tanque tanqueLocalLigadoOponente; // Tanque del oponente en el host.
    private Jugador jugador; // Jugador, atributo encargado de hacer el listening del teclado para comandar al tanque propio.
    private Bola bolaBuena;
    private Bola bolaMala;

    private int yoID; // Representa el ID del jugador en este host.
    private int otroID; // Representa el ID del jugador oponente.
    private static Conexion conexion; // Objeto utilizado para todo lo relacionado a la comunicación entre ambos hosts.
    private static String nombreCircuitoTXT; // Atributo que representa el nombre del archivo del circuito.
	private static PrePartida prePartida;
	private Thread hiloTanqueRemoto;
	private Thread hiloBolasRemotas;
	private Thread hiloCircuitoRemoto;
	private boolean correrHilos = true;
	private static Partida instanciaPartida;
	private JFrame ventana;
	private String nickPropio;
	private String nickOponente;
	
    // Contstructor. Genera los elementos básicos de una aplicación del tipo juego.
    public Partida(String nombreCircuitoTXT, Conexion conexion, PrePartida prePartida) {
		instanciaPartida = this;
		Partida.prePartida = prePartida;
		ventana = new JFrame("TankExile"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		//this.setBounds(0,0,Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA); // Establecimiento de las dimensiones de este objeto Partida.
		ventana.setLayout(new GridLayout());
		panel.setBounds(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA);
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA)); // Establecimiento de las dimensiones del panel.
		panel.setLayout(new GridLayout());
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).
	

		ventana.setBounds(0,0,Finals.ANCHO_VENTANA+6,Finals.ALTO_VENTANA+30); // Establecimiento de las dimensiones de la ventana.
		ventana.setVisible(true); // Ventana visible.

		ventana.addWindowListener(
			new WindowAdapter() { // Ventana tiene escucha, una clase anónima, para el cierre.
				public void windowClosing(WindowEvent e) {
					//getPrePartida().setEstado("Partida abortada.");
					//getPrePartida().setVisible(true);
					//finalizar();
					System.exit(0);
				}
			}
		);


		ventana.setResizable(false); // La ventana no es de tamaño ajustable.
		this.createBufferStrategy(2); // Es creado sobre este canvas una estrategia de buffering, de dos buffers.
		estrategia = this.getBufferStrategy(); // Sobre este objeto se aplicará el método de paint. Este realizará por sí mismo el doble buffering.
		
		Partida.conexion = conexion;
		this.yoID = conexion.getID()%2; // Son asignados los valores de ID e IP del oponente.
		this.otroID = (conexion.getID()+1)%2;
		//this.iPOponente = iPOponente;
		Partida.nombreCircuitoTXT = nombreCircuitoTXT; // Asignación del nombre del archivo del circuito.
		
    }
    // Método que arranca la escena de la partida. Involucra la inicialización de los elementos principales del juego en sí.
    public void iniciarEscena() {
		ventana.setVisible(true); // Ventana visible.
		this.requestFocus(); // El foco es tomado.
		
		circuito = new Circuito(nombreCircuitoTXT); // Creación del circuito de juego.
		circuito.setConexion(conexion);
		tanquePropio = new Tanque(yoID); // Creación del tanque comandado por el jugador en este host.
		
		tanqueLocalLigadoOponente = new Tanque(otroID); // Creación del tanque que será ligado al registro de RMI para ser comandado por el host remoto.
		circuito.setTanques(tanquePropio, tanqueLocalLigadoOponente);
		conexion.setTanqueLocalOponente(tanqueLocalLigadoOponente); 
		conexion.bindearTanqueLocalOponente(); // El tanque anterior es puesto a disposición del host remoto.
		conexion.setTanquePropio(tanquePropio); // La conexión esta lista para ser establecida, el hilo conexión observará al tanque y con sus parámetros comandará al tanque remoto puesto en el registro de RMI.
		this.tanqueLocalLigadoOponente.setNickOponente(prePartida.getNickPropio()); // Nick que será leido por el host remoto.
		
		tanquePropio.setSonidoHabilitado(prePartida.getSonidoHabilitado());
		
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

		hiloTanqueRemoto = new Thread(conexion.getHiloManejadorDeTanqueRemoto(), "Hilo manejador del tanque remoto");
		// Ambos tanques son ubicados en sus metas correspondientes.
		tanquePropio.setX(circuito.getMeta(yoID).getX());
		tanquePropio.setY(circuito.getMeta(yoID).getY());
		
		tanqueLocalLigadoOponente.setX(circuito.getMeta(otroID).getX());
		tanqueLocalLigadoOponente.setY(circuito.getMeta(otroID).getY());
		
		this.nickOponente = conexion.getNickTanqueOponente();
		this.nickPropio = (prePartida.getNickPropio());
		
		hiloTanqueRemoto.start(); // El hilo que controla al tanque remoto es iniciado.
		
		// Creación y bindeo de las bolas.
		bolaBuena = new Bola(true,tanquePropio);
		bolaMala = new Bola(false,tanquePropio);
		circuito.agregarBola(bolaBuena);
		circuito.agregarBola(bolaMala);
		
		conexion.setBolasLocales(bolaBuena, bolaMala);
		conexion.bindearBolasLocales();
		System.out.println("Servidor de bolas listo.");
		
		
		if (this.yoID == 1){
			// Se establece la responsabilidad de las bolas según la localidad o no del host.
			bolaBuena.setLocal();
			bolaMala.setLocal();
			
			do{
				try{
					conexion.ponerADisposicionBolasRemotas(); // Se llama a las remotas en caso de hacer control de bolas localmente, para controlarlas.
				}catch(Exception e){
					try { Thread.sleep(Finals.ESPERA_CONEXION); } catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
					System.out.println("Intento fallido por obtener bolas remotas... Reintentando...");
				}
			}while(!conexion.bolasListo());
			System.out.println("Bolas remotas a disposición.");
			
			// Es creado e iniciado el hilo que manejará a las bolas remotas.
			hiloBolasRemotas = new Thread(conexion.getHiloManejadorDeBolas(), "Hilo manejador de bolas remotas");
			hiloBolasRemotas.start();
		}
		
		// Las bolas comienzan a actuar por sí mismas.
		bolaBuena.start();
		bolaMala.start();
		
		// El circiuto local es bindeado.
		conexion.setCircuitoPropio(circuito);
		conexion.bindearCircuitoLocal();
		
		do{
			try{
				conexion.ponerADisposicionCircuitoRemoto(); // El circuito remoto es puesto a disposición del host local.
			}catch(Exception e){
				try { Thread.sleep(Finals.ESPERA_CONEXION); } catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
				System.out.println("Intento fallido por obtener circuito remoto... Reintentando...");
			}
		}while(!conexion.circuitoListo());
		
		System.out.println("Circuito remoto a disposición.");
		
		// Se comienza la sincronización de los dos circuitos.
		hiloCircuitoRemoto = new Thread(conexion.getHiloManejadorDeCircuitoRemoto(), "Hilo manejador de circuito remoto");
		hiloCircuitoRemoto.start();
    }

    // Método que llama a la actuación de cada tanque.
    public void actualizarEscena(){
		tanquePropio.actuar();
		tanqueLocalLigadoOponente.actuarResumido();
		circuito.actuar();

    }
	
	
	// Método que retorna la ventana de prePartida, y según sea la localidad o no del host, habilita el botón de inicio.
	public PrePartida getPrePartida(){
		if (conexion.getID()==0){
			try {prePartida.setInicioHabilitado(false);} catch (RemoteException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
		}
		return prePartida;
	}
	
    // Método encargado de realizar el dibujo de cada elemento de la partida en pantalla.
    public void pintarEscena() {
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();

		// Establecimiento del fondo.
		g.setColor(Color.WHITE);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		
		// Pintado del circuito.
		circuito.pintar(g);
		
		// Pintado de las leyendas.
		g.setColor(Finals.COLOR_LEYENDAS_PARTIDA);
		
		int metaPX  = circuito.getMeta(conexion.getID()).getX() + 25;
		int metaPY  = circuito.getMeta(conexion.getID()).getY() + 10;
				
		int metaOX  = circuito.getMeta(conexion.getOtroID()).getX() + 25;
		int metaOY  = circuito.getMeta(conexion.getOtroID()).getY() + 10;
		
		g.drawString((((nickPropio!=null)&&(!nickPropio.equals("")))?nickPropio:("Jugador local " + (conexion.getID()+1))), metaPX, metaPY); // Grafica el nick propio
		g.drawString((((nickOponente!=null)&&(!nickOponente.equals("")))?nickOponente:("Jugador oponente " + (int)(((conexion.getID()+1)%2)+1))), metaOX, metaOY); // Grafica el nick oponente
			
		// Pintado de los tanques y las bolas.
		tanqueLocalLigadoOponente.pintar(g);
		tanquePropio.pintar(g);
		bolaBuena.pintar(g);
		bolaMala.pintar(g);
		
		// Se muestra lo pintado.
		estrategia.show();
    }

    // Método que contiene el bucle de ejecución principal del juego.
    public void jugar(){
		iniciarEscena();
		Thread hiloJuego = new Thread(this, "Hilo principal del juego");
		hiloJuego.start();
    }

	public void stopHilos(){
		correrHilos = false;
	}
	
	// Bucle principal del juego.
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
	
	// Método que realiza la detención de todos los hilos de la partida.
	public void finalizar(){
		try {		
			tanquePropio.detenerReproduccion(); // Es detenida la reproducción de todos los sonidos.
			this.tanqueLocalLigadoOponente.detenerReproduccion();
			this.getPrePartida().setVisible(true); // Se hace visible la ventana de prePartida.
			this.stopHilos(); // Se detienen los hilos del juego.
			conexion.stopHilos(); // Se detienen los hilos que controlan remotamente.
			this.bolaBuena.stopHilo(); // Se detienen los hilos de cada bola.
			this.bolaMala.stopHilo();
			ventana.dispose(); // Se da de baja esta ventana.
		} catch (Throwable ex) {
			Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
		}
		conexion.desbindearTodo(false); // Se hace unbind de todo aquello bindeado.
	}
}
