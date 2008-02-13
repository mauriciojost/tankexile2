
package test;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;
import paquete.*;
import presentacion.*;


public class ClaseTest extends Canvas{
	private BufferStrategy estrategia;
	private JFrame ventana;
	private Tanque tanque;
	private int contador = 0;
	public ClaseTest(){
		ventana = new JFrame("TankExile"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		//this.setBounds(0,0,Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA); // Establecimiento de las dimensiones de este objeto Partida.
		panel.setBounds(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA);
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).
		ventana.setBounds(0,0,Finals.ANCHO_VENTANA+6,Finals.ALTO_VENTANA+30); // Establecimiento de las dimensiones de la ventana.
		ventana.setVisible(true); // Ventana visible.
		ventana.addWindowListener(
			new WindowAdapter(){
				@Override
				public void windowClosing(WindowEvent e) {
					//getPrePartida().setEstado("Partida abortada.");
					//getPrePartida().setVisible(true);
					//finalizar();
					System.exit(0);
				}
			}
		);
		
		this.createBufferStrategy(2);
		estrategia = this.getBufferStrategy();
	}
	public void testearX(){
		tanque = new Tanque(1);
		
		Thread hilo = new Thread(
			new Runnable(){
				public void run(){
					while(true){
						actuar();
						pintar();
						try{Thread.sleep(50);}catch(Exception e){}
					}
					
				}
			}
		);
		hilo.start();
	}
	
	public void pintar(){
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();
		g.setColor(Color.white);
		g.drawRect(0, 0, 100, 100);
		tanque.pintar(g);
		estrategia.show();
	}
	public void actuar(){
		contador=(contador+1)%16;
		tanque.setTodo(0,0,Finals.ARRIBA, contador,0,true);
	}
	public static void main(String args[]){
		ClaseTest testeo = new ClaseTest();
		testeo.testearX();
	}
}
