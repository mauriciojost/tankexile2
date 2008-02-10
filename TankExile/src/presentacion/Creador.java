
package presentacion;

import java.awt.Color;
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
		panel.setBackground(Finals.colorGris);
		return panel;
	}
	
	public JTextField crearCampo(String nombre, boolean editable, Color bg){
		JTextField campo = new JTextField(nombre);
		campo.setEditable(editable);
		campo.setBackground(bg);
		return campo;
	}
	
	public JTextArea crearArea(String nombre, boolean editable, Color bg){
		JTextArea area = new JTextArea(nombre);
		area.setEditable(editable);
		area.setBackground(bg);
		return area;
	}
}
