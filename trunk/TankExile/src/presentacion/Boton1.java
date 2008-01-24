
package presentacion;

import paquete.*;
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
		// Acciones relativas a la conexión. Seguramente se necesita de un objeto de la clase Conexion.
		// Necesito obtener el IP del oponente tipeado en el cuadro de texto.
		System.out.println("POR CONSTRUIR PRE PARTIDA");
		ventana.dispose();
		new PrePartida1(ventana.getX(),ventana.getY());
		
    }
    // Metodo que responde al evento sobre el boton Jugar.
	public Partida responderJugar(MouseEvent e) {
		
		System.out.println("POR CONSTRUIR PARTIDA");
		ventana.dispose();
		return new Partida(0,"circuito2.txt",null);
		
		
	}
    // Método que responder al evento sobre el boton Opciones.
    public void responderOpciones(MouseEvent e) {
		//((Boton1)e.getComponent()).ventana.getContentPane().removeAll();
		System.out.println("POR CONSTRUIR CONFIGURADOR 1");
		//new Configurador1();

		//ventana.dispose();
		//new FileChooser(ventana.getX(),ventana.getY());
		

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
		ventana.dispose();
		new PrePartida1(ventana.getX(),ventana.getY());
		
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
