package presentacion;


import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
	
	
	public static PrePartida getPrePartida(Presentacion presentacion, Conexion conexion){
		if (prePartida != null){
			PrePartida.conexion = conexion;
			PrePartida.presentacion = presentacion;
			return prePartida;
		}else{
			
			PrePartida.conexion = conexion;
			PrePartida.presentacion = presentacion;
			prePartida = new PrePartida(presentacion);
			return prePartida;
		}
	}
	
	
	
	public void setVentanaRemota(VentanaControlable ventanaRemota){
		PrePartida.ventanaRemota = ventanaRemota;
	}
	

	// Constructor de la clase.
	
	private PrePartida(Presentacion presentacion){
		super("Tank Exile - Pre Partida");
		
	
		setBounds(presentacion.getX(),presentacion.getY(),Finals.ANCHO_VENTANA-250,385);
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
		
		Creador creador = new Creador();
		
		JPanel panel_icono = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,150), new FlowLayout());
		
		// Crea una etiqueta para cargar icono que contiene a la imagen.
		JLabel iM = new JLabel();
		iM.setIcon(ii);
		iM.setOpaque(false);
		iM.setSize(200, 200);
		panel_icono.add(iM);
		
		b_inicio = creador.crearBoton("Inicio", "Comienza la partida", this);
		b_inicio.setEnabled(false);
		
				
		b_elegir_circuito = creador.crearBoton("Elegir Circuito", "Permite seleccion de circuito", this);
		b_elegir_circuito.setEnabled(conexion.getID()==1);
		
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
		
		setVisible(true);
		
		PrePartida.conexion = conexion;
		PrePartida.prePartida = this;
		PrePartida.presentacion = presentacion;
		
		setEstado("Conexión establecida con éxito.");
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

	public void setEstado(String noticia){
		estado.setText(noticia);
	}
	
	// Metodo que responde al evento sobre el boton Jugar.
	public void responderInicio() {
        if (!b_inicio.isEnabled()) return;
        
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
	public void setCircuitoSeleccionado(File circuitoSeleccionado, boolean propio){
		
		try {
			if (propio) {
				conexion.enviarAHostRemoto(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);				
				try {copiarArchivo(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);}catch(Exception e){e.printStackTrace();}
			} else {
				conexion.copiarDeHostRemoto(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);
			}
		} catch (IOException ex) {
			System.err.println("Error al intentar copiar en el método de Conexion copiarDeHostRemoto.");
			Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
		}	
			
		b_inicio.setEnabled(true);
		
		
		this.circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);	
		this.circuitoSeleccionado.deleteOnExit();
	}
    // Método que responde al evento sobre el boton Elegir Circuito.
	public void responderElegir() {
		if (this.b_elegir_circuito.isEnabled()){
			this.escenografia = new Escenografia(prePartida);
			this.dispose();
		}
	}

	public void responderOpcion(){
		//configurar sonido y nombre del jugador
		this.dispose();
		this.configurador = new Configurador(prePartida);
		
	}
	
    public void responderSalida(){
		System.exit(0);
	}
	
	public static PrePartida getPrePartida() {
		return prePartida;

	}
        
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			nombre = nombre.substring(0, 6);
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
