
package presentacion;

import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrePartida1 extends JFrame {
	
	// Constructor de la clase.
	public PrePartida1(int x, int y){
		super("Tank Exile - Pre Partida");
		setBounds(x,y,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-500); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-500));
		panel.setLayout(new GridLayout(2,1));
		panel.setBackground(Color.LIGHT_GRAY);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		Boton1 b1 = new Boton1("    JUGAR   ", this);
		b1.addMouseListener(b1);
		
		JPanel jp4 = new JPanel();
		jp4.setLayout(new FlowLayout());
		jp4.setBackground(Color.LIGHT_GRAY);
		jp4.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/2));
		jp4.add(b1);
		
		Boton1 b2 = new Boton1("OPCIONES",this);
		b2.addMouseListener(b2);
		
		JPanel jp5 = new JPanel();
		jp5.setLayout(new FlowLayout());
		jp5.setBackground(Color.LIGHT_GRAY);
		jp5.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/2));
		jp5.add(b2);
		
		
		panel.add(jp4);
		panel.add(jp5);
		setVisible(true);
		System.out.println(" PASE POR CONSTRUCTOR DE PRE PARTIDA");
	}
}
