package presentacion;

import java.rmi.RemoteException;
import paquete.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.channels.FileChannel;

import javax.swing.*;
//import test.VentanaPresentacion;
import test.VentanaPresentacion;


public class PrePartida extends JFrame implements MouseListener, VentanaControlable{
	private final String NOMBRE_CIRCUITO_TEMPORAL = "temporal.tmp";
	private static VentanaControlable ventanaRemota;
	private static PrePartida prePartida;
	private JTextField estado;
	private ImageIcon imagenPresentacion = new ImageIcon(getClass().getClassLoader().getResource("res/tank.GIF"));
	private JButton b_inicio;
	private File circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);
    private String nick_propio = null;
	private boolean sonido_prepartida = false;
    private JButton b_elegir_circuito;
	
	// Método que permite tener referencias, a la instancia de Presentacion y de Conexión, por parte de la instancia de PrePartida.
	public static PrePartida getPrePartida(){
		if (prePartida == null){
			prePartida = new PrePartida(); // En caso de no existir instancia, la crea.
		}
		return prePartida;
	}
	
	public void setVentanaRemota(VentanaControlable ventanaRemota){
		PrePartida.ventanaRemota = ventanaRemota;
	}
	
	// Constructor de la clase.
	private PrePartida(){
		super("Tank Exile - Pre Partida");
		
		this.setBounds(VentanaPresentacion.getPresentacion().getX(),VentanaPresentacion.getPresentacion().getY(),400,350); // Establece posición y tamaño de la ventana.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		//panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,385));//Finals.ALTO_VENTANA-300
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-this.getSize().width)/2, (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height)/2);
		panel.setLayout(new FlowLayout());
		
		
		addWindowListener(
			new WindowAdapter() {	
				public void windowClosing(WindowEvent e) {System.exit(0);}
			}
		); // Se define un objeto que escucha los eventos sobre la ventana.
		
		Creador creador = Creador.getCreador(); // Objeto que puede crear paneles, botones, areas y campos de texto.
		
		JPanel panel_icono = creador.crearPanel(new Dimension(400,120), new FlowLayout());
		
		// Crea una etiqueta para cargar icono que contiene a la imagen.
		JLabel imagen = new JLabel();
		imagen.setIcon(imagenPresentacion);
		imagen.setOpaque(false);
		imagen.setSize(200, 200);
		panel_icono.add(imagen);
		panel_icono.setBounds(imagen.getBounds());
		panel_icono.setLocation(0, 0);
		
		b_inicio = creador.crearBoton("Inicio", "Comienza la partida", this); // (texto, tool tip text, listener).
		b_inicio.setEnabled(false);
		
				
		b_elegir_circuito = creador.crearBoton("Elegir Circuito", "Permite seleccion de circuito", this);
		b_elegir_circuito.setEnabled(Conexion.getConexion().getID()==1); // Quien tenga ID = 1 será quien pueda seleccionar circuito.
		
		JButton b_opciones = creador.crearBoton("Opciones", "Permite configuracion", this);
		
		JButton b_salida = creador.crearBoton("Salida", "Cerrar", this);
		
		estado = creador.crearCampo("Estado ", false);
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
		if (Conexion.getConexion().getID()==1)
			try{ventanaRemota.setInicioHabilitado(true);}catch(Exception e){e.printStackTrace();}
        
        this.dispose();
        
        
		//Partida partida = new Partida(this.circuitoSeleccionado.getPath(), this);
		//partida.jugar();
		this.dispose();
		
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
		
		try {
			Conexion.getConexion().enviarAHostRemoto(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);				
			try {copiarArchivo(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);}catch(Exception e){e.printStackTrace();}
		} catch (IOException ex) {
			System.err.println("Error al intentar copiar en el método de Conexion copiarDeHostRemoto.");
		}	
			
		b_inicio.setEnabled(true); // Se habilita botón Inicio luego de haber seleccionado circuito. Se puede iniciar el juego.
				
		this.circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);	
		this.circuitoSeleccionado.deleteOnExit();
	}
    // Método invocado cuando se hace click en el botón Elegir Circuito.
	public void responderElegirCircuito() {
		if (this.b_elegir_circuito.isEnabled()){
			Escenografia.getEscenografia().setVisible(true);
			this.dispose();
		}
	}
	// Método invocado cuando se hace click en el botón Opciones.
	public void responderOpciones(){
		//configurar sonido y nombre del jugador
		this.dispose();
		//Configurador.getConfigurador().setVisible(true);
		test.Configuracion.getConfiguracion().setVisible(true);
	}
	// Método invocado cuando se hace click en el botón Salida.
    public void responderSalida(){
		System.exit(0);
	}
	
	// Método que responder al evento click sobre la ventana de la instancia de PrePartida.
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText()); 
			nombre = nombre.replaceAll(" ", ""); // Quita los espacios del texto obtenido del botón clickeado.
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {	
			ex.printStackTrace();
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
