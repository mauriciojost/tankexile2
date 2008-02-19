/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

/**
 *
 * @author pc
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import javax.swing.JPanel;
import paquete.Jugador;
import paquete.Tanque;
import paquete.Finals;
import paquete.Bola;
import presentacion.Conexion;

/*
 El test de la bola, se completa al test de movimiento del tanque, en conjunto con el manejo del tanque remoto ligado al tanque local.
 Para crear una bola "BUENA"(colo azul, establece el valor maximo de velocidad del tanque), se debe pasar true, como primer argumento a su 
 constructor. Caso contrario, pasando false como primer argumento se crea una bola "MALA"(color rojo, provoca el efecto de choque contra un muro
 y estable el valor minimo de velocidad del tanque).
*/

public class TestBola  extends Canvas{
	private BufferStrategy estrategia;
	private JFrame ventana;
	private Tanque tanque;
	private Tanque otroTanque;
	private Bola bola;
	private Jugador jugador;
		
	public TestBola(){
		ventana = new JFrame("TankExile - Test Bola"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		panel.setBounds(0, 0, Finals.ANCHO_VENTANA, Finals.ALTO_VENTANA);
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).
		ventana.setBounds(0,0,Finals.ANCHO_VENTANA+6,Finals.ALTO_VENTANA+30); // Establecimiento de las dimensiones de la ventana.
		ventana.setResizable(false);
		ventana.setVisible(true); // Ventana visible.
		this.requestFocus();
		ventana.addWindowListener(
			new WindowAdapter(){
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		
		this.createBufferStrategy(2);
		estrategia = this.getBufferStrategy();
	}
	public void testear(){
		
		tanque = new Tanque(1);
		tanque.setX(100); tanque.setY(100);
		jugador = new Jugador(tanque);
		this.addKeyListener(jugador);
		bola = new Bola(true,tanque,true);
		
		otroTanque = new Tanque(0);
		try{Conexion.getConexion().conectar(null);}catch(Exception e){e.printStackTrace();}
		Conexion.getConexion().setTanquePropio(tanque);
		//Conexion.getConexion().setTanqueLocalOponente();
		Conexion.getConexion().bindearTanqueLocalOponente(otroTanque);
		try{Conexion.getConexion().ponerADisposicionTanqueRemoto();}catch(Exception e){e.printStackTrace();}
		(new Thread(Conexion.getConexion().getHiloManejadorDeTanqueRemoto())).start();
		
		
		Thread hilo = new Thread(
			new Runnable(){
				public void run(){
					while(true){
						actuar();
						pintar();
						try{Thread.sleep(Finals.PERIODO);}catch(Exception e){}
					}
					
				}
			}
		);
		hilo.start();
		bola.start();
	}
		
	public void pintar(){
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA+6);
		tanque.pintar(g);
		otroTanque.setX(50);
		otroTanque.pintar(g);
		bola.pintar(g);
		
		estrategia.show();
	}
	public void actuar(){
		tanque.actuar();
		
		//contador++;
		//tanque.setTodo(300,0,(contador/10)%4, contador%16,0,true);
		//tanque.choque(false);

	}
	public static void main(String args[]){
		TestBola test = new TestBola();
		test.testear();
	}
}