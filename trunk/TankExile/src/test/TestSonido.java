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
import paquete.Bloque;
import paquete.Jugador;
import paquete.Tanque;
import paquete.Finals;
import paquete.Muro;
import presentacion.Conexion;

/*
 El test de sonido, se completa al test de movimiento del tanque, en conjunto con el manejo del tanque remoto ligado al tanque local y el choque
 contra muro.
*/

public class TestSonido  extends Canvas{
	private BufferStrategy estrategia;
	private JFrame ventana;
	private Tanque tanque;
	private Tanque otroTanque;
	private Jugador jugador;
	private Muro muro;
	
	public TestSonido(){
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
		tanque.setX(200); tanque.setY(200);
		tanque.setSonidoHabilitado(true); 
		//La linea siguiente permite modificar la velocidad para ver los efectos de un choque a maxima velocidad, o un choque a minima velocidad
		tanque.setVelocidad(tanque.MAX_VELOCIDAD); // Comentar esta linea para ver efectos a minima velocidad.
		jugador = new Jugador(tanque);
		this.addKeyListener(jugador);
		
		muro = new Muro(15,15);
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
		
	}
		
	public void pintar(){
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA+6);
		tanque.pintar(g);
		otroTanque.setX(70);
		otroTanque.pintar(g);
		muro.pintar(g);
		estrategia.show();
	}
	public void actuar(){
		if(((Bloque)muro).getBounds().intersects(tanque.getBounds())){
			tanque.choque(false);
			tanque.setX(400);
			//muro.deterioro(tanque.getVelocidad());
		}
		tanque.actuar();
		
		//contador++;
		//tanque.setTodo(300,0,(contador/10)%4, contador%16,0,true);
		//tanque.choque(false);

	}
	public static void main(String args[]){
		TestSonido test = new TestSonido();
		test.testear();
	}
}
