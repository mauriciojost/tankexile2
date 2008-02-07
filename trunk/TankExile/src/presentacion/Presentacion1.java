
package presentacion;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import paquete.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionListener;

public class Presentacion1 extends JFrame implements MouseListener {
	private PrePartida1 prePartida1;
        
    private static  Presentacion1 presentacion1;
	private JTextField area_ip;
	
	// Constructor de la clase.
	public Presentacion1(int x, int y){
		super("TankExile - Presentación");
		setBounds(x,y, Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false); // Impide modificar tamaño de la ventana.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}); // Se define un objeto que escucha y resonde a los eventos sobre la ventana.
		
		JPanel panel = (JPanel) getContentPane(); // Panel donde se grafica la interface de usuario.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500)); // Establece tamaño para el panel.
		panel.setLayout(new GridLayout(1,1)); // Establece manager layout para el panel.
		panel.setBackground(Finals.colorFondo); // Establece el color de fondo del panel.

		ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("res/tankLoading.jpg"));
		JLabel iM = new JLabel();
		iM.setIcon(ii);
		iM.setOpaque(false);
		iM.setSize(Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500);
		panel.add(iM);
				
		setVisible(true); // Se hace visible la ventana.

		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(Presentacion1.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		panel.removeAll(); // Se remueven los objetos que se encontrataban contenidos en el panel.
		panel.setLayout(new GridLayout(4,1)); // Establece manager layout para el panel.
		
		// Creacion de los componentes que conforman la interface de usuario.
		Creador creador = new Creador();
		
		JTextField titulo_ip_propio = new JTextField("IP propio: ");//Cambie esta linea
		titulo_ip_propio.setEditable(false);
		titulo_ip_propio.setBackground(Finals.colorFondo);
		titulo_ip_propio.setFont(new Font("Arial",Font.BOLD,15));
		
		JPanel panel_ip_propio = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/3),new FlowLayout(FlowLayout.LEFT));
		panel_ip_propio.add(titulo_ip_propio);
		
		try {
			JTextArea ip_propio = new JTextArea(InetAddress.getLocalHost().getHostAddress());
			ip_propio.setEditable(false);
			ip_propio.setBackground(Finals.colorFondo);
			panel_ip_propio.add(ip_propio);
		} catch (UnknownHostException ex) {
			Logger.getLogger(Presentacion1.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		///////////////////////////////////////////////////////
		
		JTextArea titulo_ip_oponente = new JTextArea("Ingrese IP de oponente:");
		titulo_ip_oponente.setEditable(false);
		titulo_ip_oponente.setFont(new Font("Arial",Font.BOLD,15));
		titulo_ip_oponente.setBackground(Finals.colorFondo);
		
		JTextField ip_oponente = new JTextField();
		ip_oponente.setColumns(15);
		ip_oponente.setToolTipText(" ");
		ip_oponente.setFocusAccelerator('\132'); // Establece combinacion Alt+z para obtener focus.

		this.area_ip = ip_oponente;
				
		JPanel panel_ip_oponente = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250, (Finals.ALTO_VENTANA-500)/3),new FlowLayout(FlowLayout.LEFT));
		panel_ip_oponente.add(titulo_ip_oponente);
		panel_ip_oponente.add(ip_oponente);
		
		JPanel panel_botones = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/3), new FlowLayout(FlowLayout.TRAILING));
		panel_botones.add(creador.crearBoton("Conectar", "Intenta conexion con IP ingresado", this));
		panel_botones.add(creador.crearBoton("Salida", "Cerrar", this));
		
		
		JTextField estado = new JTextField("Estado: ");
		estado.setEditable(false);
		estado.setBackground(Finals.colorFondo);
		
		panel.add(panel_ip_propio);
		panel.add(panel_ip_oponente);
		panel.add(panel_botones);
		panel.add(estado);
		
		setVisible(true);
		presentacion1 = this;
	}

	public static Presentacion1 getPresentacion1() {
		return presentacion1;
	}
	
	public void responderConect(){

		// Acciones relativas a la conexión. Seguramente se necesita de un objeto de la clase Conexion.
		System.out.println(this.area_ip.getText()); // Se obtiene el IP del oponente tipeado en el cuadro de texto.
		
		Conexion conexion = new Conexion(area_ip.getText());
		do{
			try{
				conexion.conectar();
			}catch(Exception ex){
				System.out.println("Fallo en el intento. Intentando conexión nuevamente...");
				try{Thread.sleep(Finals.ESPERA_CONEXION);}catch(InterruptedException r){}
			}
		}while(!conexion.conexionLista());
		this.dispose();
		conexion.start();
		prePartida1 = new PrePartida1(presentacion1, conexion);
		prePartida1.setEstado("Estado: Conexion establecida exitosamente");
	}
	
	public void responderSalida(){
		System.exit(0);
	}
	
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			nombre = nombre.substring(0, 6); // Utiliza los 6 primeros caracteres del texto obtenido del boton.
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} 
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	
	public static void main(String args[]){
		new Presentacion1(350,70);
	}
}
