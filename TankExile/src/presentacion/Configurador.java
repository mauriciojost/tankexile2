package presentacion;

import java.awt.Color;
import java.awt.event.MouseEvent;
import paquete.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Object;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Configurador extends JFrame implements MouseListener {
	private PrePartida prePartida;
	private JTextField campo_nick_propio;
	private JButton evento;
	// Constructor de la clase.
	public Configurador(PrePartida prePartida){
		super("TankExile - Opciones");
		this.prePartida = prePartida;
		setBounds(prePartida.getX(),prePartida.getY(),Finals.ANCHO_VENTANA-250,150); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,150));
		panel.setLayout(new FlowLayout());
		panel.setBackground(Finals.colorGris);
		/*Boton b1 = new Boton("SONIDO", ventana);
		b1.addMouseListener(b1);
		panel.add(b1);*/
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		Creador creador = new Creador();
		
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
		System.out.println(" PASE POR CONSTRUCTOR DE CONFIGURADOR 1");
	}
	
	public void responderSonido(){
		evento.setText("Mudo");
		evento.setToolTipText("Deshabilita sonido");
		prePartida.setSonidoHabilitado(true);
	}
	
	public void responderMudo(){
		evento.setText("Sonido");
		evento.setToolTipText("Habilita sonido");
		prePartida.setSonidoHabilitado(false);
	}
			
	public void responderAceptar(){
		this.dispose();
		prePartida.setLocation(this.getX(), this.getY());
		prePartida.setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		prePartida.setNickPropio(campo_nick_propio.getText()); 
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
