
package presentacion;

import java.awt.event.MouseEvent;
import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Presentacion1 extends JFrame implements MouseListener {
	
	// Constructor de la clase.
	public Presentacion1(int x, int y){
		super("TankExile - Presentacion");
		setBounds(x,y, Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false);
		
		JPanel panel = (JPanel) getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250, Finals.ALTO_VENTANA-500));
		panel.setLayout(new GridLayout(3,1));
		panel.setBackground(Color.LIGHT_GRAY);
		
		JTextArea jta1 = new JTextArea("Cargando..."); 
		jta1.setEditable(false); 
		jta1.setBackground(Color.LIGHT_GRAY);
		panel.add(jta1);
		
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		setVisible(true);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Logger.getLogger(Presentacion1.class.getName()).log(Level.SEVERE, null, ex);
		}
		panel.removeAll();
		
		JTextArea jta2 = new JTextArea("IP propio:");
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
				
		JTextArea ip_oponente = new JTextArea();
		ip_oponente.setRows(1); ip_oponente.setColumns(15);
		panel.add(ip_oponente);
		
		JPanel jp2 = new JPanel();
		jp2.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250, (Finals.ALTO_VENTANA-500)/3));
		jp2.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp2.setBackground(Color.LIGHT_GRAY);
		jp2.add(jta3);
		jp2.add(ip_oponente);
		
		JButton b1 = new JButton("CONECTAR");
		b1.addMouseListener(this);
		
		JPanel jp3 = new JPanel();
		jp3.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/3));
		jp3.setLayout(new FlowLayout(FlowLayout.CENTER));
		jp3.setBackground(Color.LIGHT_GRAY);
		jp3.add(b1);
		
		panel.add(jp1);
		panel.add(jp2);
		panel.add(jp3);
		
		setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		// Acciones relativas a la conexión. Seguramente se necesita de un objeto de la clase Conexion.
		// Necesito obtener el IP del oponente tipeado en el cuadro de texto.
		System.out.println("POR CONSTRUIR PRE PARTIDA");
		this.dispose();
		new PrePartida1(this.getX(),this.getY());
	}

	public void mousePressed(MouseEvent e) { }

	public void mouseReleased(MouseEvent e) { }

	public void mouseEntered(MouseEvent e) { }

	public void mouseExited(MouseEvent e) { }
}
