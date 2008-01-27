
package presentacion;

import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
		
		JPanel panel = (JPanel) getContentPane(); // Panel donde se grafican la interface de usuario.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500)); // Establece tamaño para el panel.
		panel.setLayout(new GridLayout(1,1)); // Establece manager layout para el panel.
		panel.setBackground(Color.LIGHT_GRAY); // Establece el color de fondo del panel.
		
		ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("res/tankLoading.jpg"));
		JLabel iM = new JLabel();
		iM.setIcon(ii);
		iM.setOpaque(false);
		iM.setSize(Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500);
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500)); // Establece tamaño para el panel.
		panel.add(iM);
				
		setVisible(true); // Se hace visible la ventana.
		
		try {
			Thread.sleep(2500);
		} catch (InterruptedException ex) {
			Logger.getLogger(Presentacion1.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		panel.removeAll(); // Se remueven los objetos que se encontrataban contenidos en el panel.
		panel.setLayout(new GridLayout(3,1)); // Establece manager layout para el panel.
		
		// Creacion de los componentes que conforman la interface de usuario.
		JTextArea jta2 = new JTextArea("IP propio: ");
		jta2.setEditable(false);
		jta2.setBackground(Color.LIGHT_GRAY);
		
		JTextArea ip_propio = new JTextArea("192.168.0.2");
		ip_propio.setEditable(false);
		ip_propio.setBackground(Color.LIGHT_GRAY);
		
		JPanel jp1 = new JPanel();
		jp1.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/3));
		jp1.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp1.setBackground(Color.LIGHT_GRAY);
		jp1.add(jta2);
		jp1.add(ip_propio);
		
		
		JTextArea jta3 = new JTextArea("Ingrese IP de oponente:");
		jta3.setEditable(false);
		jta3.setBackground(Color.LIGHT_GRAY);
				
		JTextField ip_oponente = new JTextField();
		ip_oponente.setColumns(15);
		ip_oponente.setToolTipText(" ");
		ip_oponente.setFocusAccelerator('\132'); // Estabcle combinacion Alt+z para obtener focus.
		
		this.area_ip = ip_oponente;
				
		JPanel jp2 = new JPanel();
		jp2.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250, (Finals.ALTO_VENTANA-500)/3));
		jp2.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp2.setBackground(Color.LIGHT_GRAY);
		jp2.add(jta3);
		jp2.add(ip_oponente);
		
		JButton b_conectar = new JButton("Conectar");
		b_conectar.setPreferredSize(new Dimension(110,30));
		b_conectar.addMouseListener(this);
		b_conectar.setToolTipText("Intenta conexion con IP ingresado");
		
		JButton b_salida = new JButton("Salida");
		b_salida.setPreferredSize(new Dimension(110,30));
		b_salida.addMouseListener(this);
		b_salida.setToolTipText("Cerrar");
		
		JPanel jp3 = new JPanel();
		jp3.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/3));
		jp3.setLayout(new FlowLayout(FlowLayout.TRAILING));
		jp3.setBackground(Color.LIGHT_GRAY);
		jp3.add(b_conectar);
		jp3.add(b_salida);
		
		panel.add(jp1);
		panel.add(jp2);
		panel.add(jp3);
		
		setVisible(true);
		presentacion1 = this;
	}
	
	Presentacion1(JFrame ventana) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public static Presentacion1 getPresentacion1() {
		return presentacion1;
	}
	
	public void responderConect(){
		// Acciones relativas a la conexión. Seguramente se necesita de un objeto de la clase Conexion.
		System.out.println(this.area_ip.getText()); // Se obtiene el IP del oponente tipeado en el cuadro de texto.
		System.out.println("POR CONSTRUIR PRE PARTIDA");
		this.dispose();
		prePartida1 = new PrePartida1(this.getX(),this.getY(),presentacion1);
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
}
