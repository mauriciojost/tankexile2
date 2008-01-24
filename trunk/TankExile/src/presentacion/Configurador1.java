package presentacion;

import paquete.*;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Configurador1 extends JFrame {
		
	// Constructor de la clase.
	public Configurador1(){
		
		JPanel panel = (JPanel)getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA));
		panel.setLayout(new FlowLayout());
		/*Boton b1 = new Boton("SONIDO", ventana);
		b1.addMouseListener(b1);
		panel.add(b1);*/
		addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
				   			System.exit(0);
						}
					}); // Se define un objeto que escucha los eventos sobre la ventana.
		setResizable(true); // Se permite dar nuevo tamaño a la ventana.
		setBounds(getX(),getY(),Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA-450); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		setVisible(true);
		System.out.println(" PASE POR CONSTRUCTOR DE CONFIGURADOR 1");
	}

}
