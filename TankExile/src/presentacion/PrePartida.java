package presentacion;


import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PrePartida extends JFrame implements MouseListener, VentanaControlable{
	private final String NOMBRE_CIRCUITO_TEMPORAL = "temporal.tmp";
	private static Presentacion presentacion;

	private static VentanaControlable ventanaRemota;
	private static Conexion conexion;

	private static PrePartida prePartida;

	private Escenografia escenografia;
	private Configurador configurador;
	private JTextField estado;
	
	private ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("res/tank.GIF"));

	private JButton b_inicio;
	private File circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);

    private String nick_propio = null;
	private boolean sonido_prepartida = false;

    private JButton b_elegir_circuito;
	
	// Método que permite tener referencias, a la instancia de Presentacion y de Conexión, por parte de la instancia de PrePartida.
	public static PrePartida getPrePartida(Presentacion presentacion, Conexion conexion){
		PrePartida.conexion = conexion;
		PrePartida.presentacion = presentacion;
		if (prePartida == null){
			prePartida = new PrePartida(presentacion); // En caso de no existir instancia, la crea.
		}
		return prePartida;
	}
	
	public void setVentanaRemota(VentanaControlable ventanaRemota){
		PrePartida.ventanaRemota = ventanaRemota;
	}
	
	// Constructor de la clase.
	private PrePartida(Presentacion presentacion){
		super("Tank Exile - Pre Partida");
		
	
		setBounds(presentacion.getX(),presentacion.getY(),Finals.ANCHO_VENTANA-250,385); // Establece posición y tamaño de la ventana.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,385));//Finals.ALTO_VENTANA-300
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.LIGHT_GRAY);
		
		addWindowListener(
			new WindowAdapter() {	
				public void windowClosing(WindowEvent e) {System.exit(0);}
			}
		); // Se define un objeto que escucha los eventos sobre la ventana.
		
		Creador creador = new Creador(); // Objeto que puede crear paneles, botones, areas y campos de texto.
		
		JPanel panel_icono = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,150), new FlowLayout());
		
		// Crea una etiqueta para cargar icono que contiene a la imagen.
		JLabel iM = new JLabel();
		iM.setIcon(ii);
		iM.setOpaque(false);
		iM.setSize(200, 200);
		panel_icono.add(iM);
		
		b_inicio = creador.crearBoton("Inicio", "Comienza la partida", this); // (texto, tool tip text, listener).
		b_inicio.setEnabled(false);
		
				
		b_elegir_circuito = creador.crearBoton("Elegir Circuito", "Permite seleccion de circuito", this);
		b_elegir_circuito.setEnabled(conexion.getID()==1); // Quien tenga ID = 1 será quien pueda seleccionar circuito.
		
		JButton b_opciones = creador.crearBoton("Opciones", "Permite configuracion", this);
		
		JButton b_salida = creador.crearBoton("Salida", "Cerrar", this);
		
		estado = creador.crearCampo("Estado ", false, Finals.colorGris);
		estado.setPreferredSize(new Dimension(80,30));
		
		JPanel panel_botones = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,200), new GridLayout(6,1));
		
		panel_botones.add(b_inicio);
		panel_botones.add(b_elegir_circuito);
		panel_botones.add(b_opciones);
		panel_botones.add(b_salida);
		panel_botones.add(estado);
		panel.add(panel_icono);
		panel.add(panel_botones);
		
		setVisible(true); // Se hace visible la ventana.
		
//		PrePartida.conexion = conexion;
//		PrePartida.prePartida = this;
//		PrePartida.presentacion = presentacion;
		
		setEstado(" Conexión establecida con éxito.", Font.PLAIN); // Crear instancia de PrePartida implica que se ha establecido la conexión.
	}
	
	public void setSonidoHabilitado(boolean s){
		sonido_prepartida = s;
	}
	
	public boolean getSonidoHabilitado(){
		return sonido_prepartida;
	}
	
	public void setNickPropio(String nick){
		nick_propio = nick;
	}
	
	public String getNickPropio(){
		return nick_propio;
	}
	// Método que muestra mensaje de estado de la aplicación.
	public void setEstado(String noticia, int estilo){
		estado.setFont(new Font("Arial",estilo,12));
		estado.setText(noticia); 
		
	}
	
	// Metodo que responde al evento sobre el boton Inicio.
	public void responderInicio() {
        if (!b_inicio.isEnabled()) return;
        
		// Quien tiene el ID = 1, es quien selecciona circuito y por tanto, quien indica cuando se habilita el botón Inicio en el host remoto.
		if (conexion.getID()==1)
			try{ventanaRemota.setInicioHabilitado(true);}catch(Exception e){e.printStackTrace();}
        
        this.dispose();
        
        try { 
			Partida partida = new Partida(this.circuitoSeleccionado.getPath(), conexion, this);
			partida.jugar();
			this.dispose();
		} catch (Exception ex) {
			Logger.getLogger(PrePartida.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
        
	
	private void copiarArchivo(String origen, String destino) throws Exception{
		FileChannel ic = new FileInputStream(origen).getChannel();
		FileChannel oc = new FileOutputStream(destino).getChannel();
		ic.transferTo(0, ic.size(), oc);
		ic.close();
		oc.close();
	}
	
	// Método invocado cuando se ha validado el circuito seleccionado.
	public void setCircuitoSeleccionado(File circuitoSeleccionado){
		// SE PUEDE EVITAR EL PROPIO PORQUE SIEMPRE SELECCIONO UN CIRCUITO PROPIO.
		try {
			conexion.enviarAHostRemoto(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);				
			try {copiarArchivo(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);}catch(Exception e){e.printStackTrace();}
		} catch (IOException ex) {
			System.err.println("Error al intentar copiar en el método de Conexion copiarDeHostRemoto.");
			Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
		}	
			
		b_inicio.setEnabled(true); // Se habilita botón Inicio luego de haber seleccionado circuito. Se puede iniciar el juego.
				
		this.circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);	
		this.circuitoSeleccionado.deleteOnExit();
	}
    // Método invocado cuando se hace click en el botón Elegir Circuito.
	public void responderElegirCircuito() {
		if (this.b_elegir_circuito.isEnabled()){
			this.escenografia = new Escenografia(prePartida);
			this.dispose();
		}
	}
	// Método invocado cuando se hace click en el botón Opciones.
	public void responderOpciones(){
		//configurar sonido y nombre del jugador
		this.dispose();
		this.configurador = new Configurador(prePartida);
		
	}
	// Método invocado cuando se hace click en el botón Salida.
    public void responderSalida(){
		System.exit(0);
	}
	
	public static PrePartida getPrePartida() {
		return prePartida;

	}
	// Método que responder al evento click sobre la ventana de la instancia de PrePartida.
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText()); 
			nombre = nombre.replaceAll(" ", ""); // Quita los espacios del texto obtenido del botón clickeado.
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {	
			ex.printStackTrace();
			Logger.getLogger(PrePartida.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	public void setInicioHabilitado(boolean habilitada) throws RemoteException {
		this.b_inicio.setEnabled(habilitada);
			
	}
}
