
package presentacion;

import paquete.*;
import presentacion.PrePartida;
import presentacion.Configurador;
import presentacion.Presentacion;
import java.awt.Button;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

public class Boton extends Button implements MouseListener{
    
	private JFrame ventana;
    // Constructor de la clase.
	public Boton(String s, JFrame ventana){
	super(s);
	this.ventana = ventana;
    }
    // Método que responde al evento sobre el boton Conexion.
    public void responderConexion(MouseEvent e) {
		((Boton)e.getComponent()).ventana.getContentPane().removeAll();
		//this.ventana.dispose();
		System.out.println("POR CONSTRUIR PRE PARTIDA");
		new PrePartida(this.ventana);
    }
    // Metodo que responde al evento sobre el boton Jugar.
	public Partida responderJugar(MouseEvent e) {
		((Boton)e.getComponent()).ventana.getContentPane().removeAll();
		//this.ventana.setEnabled(false);
		//this.ventana.setVisible(false);
		System.out.println("POR CONSTRUIR PARTIDA");
		//return new Partida(this.ventana);
		return new Partida(0, "circuito2.txt", null);
		
	}
    // Método que responder al evento sobre el boton Opciones.
    public void responderOpciones(MouseEvent e) {
		((Boton)e.getComponent()).ventana.getContentPane().removeAll();
		System.out.println("POR CONSTRUIR CONFIGURADOR");
		new Configurador(this.ventana);
    }
	
    public void responderReiniciar(MouseEvent e) {
		((Boton)e.getComponent()).ventana.getContentPane().removeAll();
		new Configurador(this.ventana);
    }
	
    public void responderNuevaConexion() {
    }
	
    public void responderSeleccionarEscenario() {
    }

    public void mouseClicked(MouseEvent e) {
		String nombre = ((Boton)e.getComponent()).getLabel();
		if(nombre.contentEquals("CONECTAR")) responderConexion(e);
		
		if(nombre.contentEquals("JUGAR")){
			Partida p = responderJugar(e);
			p.jugar();
		}
		
		if(nombre.contentEquals("OPCIONES")) responderOpciones(e);
	
		if(nombre.contentEquals("Reiniciar")){
		}
		if(nombre.contentEquals("Nueva Conexion")){
			((Boton)e.getComponent()).ventana.getContentPane().removeAll();
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
