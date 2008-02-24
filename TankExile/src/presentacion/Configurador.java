package presentacion;

import java.awt.event.MouseEvent;
import paquete.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Configurador extends JFrame implements MouseListener {
	
	private JTextField campo_nick_propio;
	private JButton evento;
	private static Configurador configurador = null;
	// Constructor de la clase.
	private Configurador(){
		super("TankExile - Opciones");
		setBounds(PrePartida.getPrePartida().getX(),PrePartida.getPrePartida().getY(),Finals.ANCHO_VENTANA-250,200); // Reajusta tamaño de la ventana, sin modificar su posición.
		configurador = this;
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,150));
		panel.setLayout(new FlowLayout());
		panel.setBackground(Finals.colorGris);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		// Se crea instancia que sabe crear paneles, botones, areas y campos de texto.
		Creador creador = Creador.getCreador();
		JTextArea titulo_nick_propio = creador.crearArea("Nick: ", false, Finals.colorGris);
		
		campo_nick_propio = creador.crearCampo("", true, Finals.colorBlanco);
		campo_nick_propio.setColumns(15);
		
		JButton sonido = creador.crearBoton("Sonido", "Habilita sonido", this);
		
		JButton volver = creador.crearBoton("Aceptar", "Almacena configuracion", this);
		JPanel panel_nick_propio = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-200,80), new FlowLayout());
		panel_nick_propio.add(titulo_nick_propio); 
		panel_nick_propio.add(campo_nick_propio);
		panel_nick_propio.add(sonido);
		panel.add(panel_nick_propio);
		panel.add(volver);
		setVisible(true);
		//campo_nick_propio.setFocusable(true);
		//campo_nick_propio.requestFocus();
		campo_nick_propio.requestFocusInWindow();
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-this.getSize().width)/2, (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height)/2);
	}
	
	public static Configurador getConfigurador(){
		if (configurador==null){
			configurador = new Configurador();
		}
		return configurador;
	}
	
	// Método invocado cuando se hace click sobre el botón Sonido.
	public void responderSonido(){
		evento.setText("Mudo");
		evento.setToolTipText("Deshabilita el sonido");
		PrePartida.getPrePartida().setSonidoHabilitado(true);
	}
	// Método invocado cuando se hace click sobre el botón Mudo.
	public void responderMudo(){
		evento.setText("Sonido");
		evento.setToolTipText("Habilita el sonido");
		PrePartida.getPrePartida().setSonidoHabilitado(false);
	}
	// Método invocado cuando se hace click sobre el botón Aceptar.
	public void responderAceptar(){
		this.dispose();
		PrePartida.getPrePartida().setLocation(this.getX(), this.getY());
		if (PrePartida.getPrePartida().getSonidoHabilitado()) 
			PrePartida.getPrePartida().setEstado("Sonido habilitado.   Nick: "+campo_nick_propio.getText(),Font.PLAIN);
		else 
			PrePartida.getPrePartida().setEstado("Sonido deshabilitado.   Nick: "+campo_nick_propio.getText(),Font.PLAIN);
		
		PrePartida.getPrePartida().setVisible(true);
	}
	// Método que responder al evento click sobre la ventana de la instancia de Configurador.
	public void mouseClicked(MouseEvent e) {
		PrePartida.getPrePartida().setNickPropio(campo_nick_propio.getText()); 
		try { 
			evento = ((JButton)e.getSource());
			String nombre = new String(((JButton)e.getSource()).getText());
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {
			Logger.getLogger(PrePartida.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}
