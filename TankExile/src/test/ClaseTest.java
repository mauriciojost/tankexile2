
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
	private Tanque otroTanque;
	private Muro muro;
	private int contador = 0;
	public ClaseTest(){
		ventana = new JFrame("TankExile"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		panel.setBounds(0, 0, Finals.ANCHO_VENTANA,Finals.ALTO_VENTANA);
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).
		ventana.setBounds(0,0,Finals.ANCHO_VENTANA+6,Finals.ALTO_VENTANA+30); // Establecimiento de las dimensiones de la ventana.
		ventana.setVisible(true); // Ventana visible.
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
	public void testearX(){
		
		tanque = new Tanque(1);
		otroTanque = new Tanque(0);
		
		muro = new Muro(1,0);
		try{Conexion.getConexion(null).conectar();}catch(Exception e){e.printStackTrace();}
		Conexion.getConexion().setTanquePropio(tanque);
		Conexion.getConexion().setTanqueLocalOponente(otroTanque);
		Conexion.getConexion().bindearTanqueLocalOponente();
		try{Conexion.getConexion().ponerADisposicionTanqueRemoto();}catch(Exception e){e.printStackTrace();}
		(new Thread(Conexion.getConexion().getHiloManejadorDeTanqueRemoto())).start();
		
		
		Thread hilo = new Thread(
			new Runnable(){
				public void run(){
					while(true){
						actuar();
						pintar();
						try{Thread.sleep(100);}catch(Exception e){}
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
		tanque.setY(20);
		otroTanque.pintar(g);
		muro.pintar(g);
		estrategia.show();
	}
	public void actuar(){
		contador++;
		tanque.setTodo(0,0,(contador/10)%4, contador%16,0,true);
		muro.deterioro(1*Muro.UNIDAD_DE_MAGNITUD);
		tanque.choque(false);
	}
	public static void main(String args[]){
		ClaseTest testeo = new ClaseTest();
		testeo.testearX();
	}
}
