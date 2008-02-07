
package presentacion;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import paquete.Finals;

public class Creador {
	
	public Creador(){
		
	}
	
	public JButton crearBoton(String nombre, String aviso, MouseListener presentacion){
		JButton boton = new JButton(nombre);
		boton.setPreferredSize(new Dimension(110,30));
		boton.addMouseListener(presentacion);
		boton.setToolTipText(aviso);
		return boton;
	}
	
	public JPanel crearPanel(Dimension d, LayoutManager lm){
		JPanel panel = new JPanel();
		panel.setPreferredSize(d);
		panel.setLayout(lm);
		panel.setBackground(Finals.colorFondo);
		return panel;
	}
	
	public JTextField crearCampo(){
		JTextField campo = new JTextField();
		return campo;
	
	}
	
	public JTextArea crearArea(Dimension d, String nombre){
		JTextArea area = new JTextArea(nombre);
		return area;
	}

}
