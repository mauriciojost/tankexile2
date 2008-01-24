
package presentacion;

import paquete.*;
import presentacion.FileChooser;
import presentacion.PrePartida1;
import presentacion.Presentacion;
import java.awt.Button;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

public class Boton1 extends Button implements MouseListener{
    
	private JFrame ventana;
    // Constructor de la clase.
	public Boton1(String s, JFrame ventana){
	super(s);
	this.ventana = ventana;
    }
    // Método que responde al evento sobre el boton Conexion.
    public void responderConexion(MouseEvent e) {
		//((Boton1)e.getComponent()).ventana.getContentPane().removeAll();
		//this.ventana.dispose();
		System.out.println("POR CONSTRUIR PRE PARTIDA");
		this.ventana.dispose();
		new PrePartida1();
		
    }
    // Metodo que responde al evento sobre el boton Jugar.
	public Partida responderJugar(MouseEvent e) {
		//((Boton1)e.getComponent()).ventana.getContentPane().removeAll();
		//this.ventana.setEnabled(false);
		//this.ventana.setVisible(false);
		System.out.println("POR CONSTRUIR PARTIDA");
		//return new Partida(this.ventana);
		this.ventana.dispose();
		return new Partida(0,"circuito2.txt",null);
		
		
	}
    // Método que responder al evento sobre el boton Opciones.
    public void responderOpciones(MouseEvent e) {
		//((Boton1)e.getComponent()).ventana.getContentPane().removeAll();
		System.out.println("POR CONSTRUIR CONFIGURADOR");
		//new Configurador1();
		//new FileChooser();
                /*JFrame frame = new JFrame("Seleccion de Circuito");
                SplitPane splitPane = new SplitPane();
                frame.getContentPane().add(splitPane.getSplitPane());
                frame.pack();
                frame.setVisible(true);*/
                new SplitPane();
                this.ventana.dispose();
    }
	
    public void responderReiniciar(MouseEvent e) {
		//((Boton1)e.getComponent()).ventana.getContentPane().removeAll();
		this.ventana.dispose();
		new PrePartida1();
		
    }
	
    public void responderNuevaConexion() {
    }
	
    public void responderSeleccionarEscenario() {
    }

    public void mouseClicked(MouseEvent e) {
		String nombre = ((Boton1)e.getComponent()).getLabel();
		
		if(nombre.contentEquals("CONECTAR")) responderConexion(e);
		
		if(nombre.contentEquals("JUGAR")){
			Partida p = responderJugar(e);
			p.jugar();
		}
		
		if(nombre.contentEquals("OPCIONES")) responderOpciones(e);
	
		if(nombre.contentEquals("Reiniciar")){
		}
		if(nombre.contentEquals("Nueva Conexion")){
			((Boton1)e.getComponent()).ventana.getContentPane().removeAll();
			new Presentacion(this.ventana);
		}
		if(nombre.contentEquals("Seleccionar Escenario")){
		}
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
