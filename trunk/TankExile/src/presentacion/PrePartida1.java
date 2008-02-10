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

public class PrePartida1 extends JFrame implements MouseListener, VentanaControlable{
	private final String NOMBRE_CIRCUITO_TEMPORAL = "temporal.tmp";
	private static Presentacion1 presentacion;

	private static VentanaControlable ventanaRemota;
	private static Conexion conexion;

	private static PrePartida1 prePartida;

	private Escenografia escenografia;
	private Configurador1 configurador;
	private JTextField estado;
	//private String imageFilePath = "C:\\tank.jpg";
	//private ImageIcon ii = new ImageIcon(imageFilePath);
	private ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("res/tank.JPG"));
	private ImageIcon esp = new ImageIcon(getClass().getClassLoader().getResource("res/EsperaOponente.jpg"));
	private JFrame ventanaEspera = new JFrame("Tank Exile - Esperando Oponente");

	private JButton b_inicio;
	private File circuitoSeleccionado;

    private String nick_propio = "";
	private boolean sonido_prepartida = false;

    private JButton b_elegir_circuito;
	
	
	public static PrePartida1 getPrePartida(Presentacion1 presentacion, Conexion conexion){
		if (prePartida != null){
			PrePartida1.conexion = conexion;
			PrePartida1.presentacion = presentacion;
			return prePartida;
		}else{
			
			PrePartida1.conexion = conexion;
			PrePartida1.presentacion = presentacion;
			prePartida = new PrePartida1(presentacion);
			return prePartida;
		}
	}
	
	
	
	public void setVentanaRemota(VentanaControlable ventanaRemota){
		PrePartida1.ventanaRemota = ventanaRemota;
	}
	

	// Constructor de la clase.
	
	private PrePartida1(Presentacion1 presentacion){
		super("Tank Exile - Pre Partida");

		this.presentacion=presentacion;
		setBounds(presentacion.getX(),presentacion.getY(),Finals.ANCHO_VENTANA-250,375);
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,375));//Finals.ALTO_VENTANA-300
		panel.setLayout(new FlowLayout());
		panel.setBackground(Color.LIGHT_GRAY);
		
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		Creador creador = new Creador();
		
		JPanel panel_icono = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,150), new FlowLayout());
		
		//Creo una Etiqueta para cargar icono q contiene a la imagen
		JLabel iM = new JLabel();
		iM.setIcon(ii);
		iM.setOpaque(false);
		iM.setSize(200, 200);
		panel_icono.add(iM);
		
		b_inicio = creador.crearBoton("Inicio", "Comienza la partida", this);
		b_inicio.setEnabled(false);
		
		JButton b_cambia_ip = creador.crearBoton("Cambiar IP", "Permite realizar nueva conexion", this);
				
		b_elegir_circuito = creador.crearBoton("Elegir Circuito", "Permite seleccion de circuito", this);
		b_elegir_circuito.setEnabled(conexion.getID()==1);
		
		JButton b_opciones = creador.crearBoton("Opciones", "Permite configuracion", this);
		
		JButton b_salida = creador.crearBoton("Salida", "Cerrar", this);
		
		estado = creador.crearCampo("Estado ", false, Finals.colorGris);
		estado.setPreferredSize(new Dimension(80,30));
		
		JPanel panel_botones = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,200), new GridLayout(6,1));
		
		panel_botones.add(b_inicio);
		panel_botones.add(b_cambia_ip);
		panel_botones.add(b_elegir_circuito);
		panel_botones.add(b_opciones);
		panel_botones.add(b_salida);
		panel_botones.add(estado);
		
		panel.add(panel_icono);
		panel.add(panel_botones);
		setVisible(true);
		
		this.conexion = conexion;

		this.prePartida=this;

		
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
		//coneccionOponente es un campo q indica si el oponente ya presiono en jugar(true) o todavia no(false)
       //en cada caso respectivamente comenzara el juego o se mantendra esperando el inicio en un Frame    
        if (!b_inicio.isEnabled()) return;
           
        //MOstrar pantalla de esperar oponente mientras Oponente.coneccionOponente sea Falso
        //while(!Oponente.coneccionOponente){}
        this.dispose();
        /*try{
			
                             
			ventanaEspera.setBounds(100,50,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370);
			setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
			JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
			panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370));
			//panel.setLayout(new GridLayout(2,1));
			panel.setBackground(Color.LIGHT_GRAY);
		
			addWindowListener(
				new WindowAdapter(){
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				}
			); // Se define un objeto que escucha los eventos sobre la ventana.
		 
            JLabel iM = new JLabel();
            iM.setIcon(ii);
            iM.setOpaque(false);
            iM.setSize(200, 200);
            //panel.add(iM);
                
            //ventanaEspera.setVisible(true);
			 * 
		}catch (Exception ex){ex.printStackTrace();}
		*/
        try { 
			if(this.getNickPropio().equals("")){
				this.setNickPropio(" PLAYER"+PrePartida1.conexion.getID());
			}
			Partida partida = new Partida(this.circuitoSeleccionado.getPath(), conexion, this);
			partida.jugar();
			this.dispose();
		} catch (Exception ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
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
		try{ventanaRemota.setInicioHabilitado(true);}catch(Exception e){e.printStackTrace();}
		
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
		this.configurador = new Configurador1(prePartida);
		
	}
	
	// Método que responde al evento sobre el boton Cambiar IP
	public void responderCambia(){
		this.dispose();
		presentacion.setLocation(this.getX(), this.getY());
		presentacion.setVisible(true);
	}
	
    public void responderSalida(){
		System.exit(0);
	}
	
	public static PrePartida1 getPrePartida() {
		return prePartida;

	}
        
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			nombre = nombre.substring(0, 6);
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {	
			ex.printStackTrace();
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }

	public void metodoDeControl() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void setInicioHabilitado(boolean habilitada) throws RemoteException {
		this.b_inicio.setEnabled(habilitada);
		this.circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);	
	}
}
