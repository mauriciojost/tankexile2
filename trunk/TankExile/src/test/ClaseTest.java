
package test;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;
import paquete.*;
import presentacion.*;

// Clase de testeo de diversos objetos.
public class ClaseTest extends Canvas{
	private BufferStrategy estrategia; // Estrategia utilizada para la representación gráfica de los objetos.
	private JFrame ventana; // Ventana relacionada a la representación gráfica.
	private Tanque tanque; // Tanque principal de testeo.
	private Tanque otroTanque; // Tanque secundario (o controlado) de testeo.
	private Muro muro; // Muro a testear.
	private int contador = 0; // Contador auxiliar.
	
	// Constructor de la clase.
	public ClaseTest(){
		ventana = new JFrame("Test TankExile"); // Armado de la ventana.
		JPanel panel = (JPanel)ventana.getContentPane(); // Obtención de su JPanel.
		panel.setBounds(0, 0, 100,100);
		panel.add(this); // El panel pintará este canvas (definido por esta instancia de Partida).
		ventana.setBounds(0,0,100,100); // Establecimiento de las dimensiones de la ventana.
		ventana.addWindowListener(
			new WindowAdapter(){
				@Override
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			}
		);
		
		ventana.setVisible(true); // Ventana visible.
		this.createBufferStrategy(2); // Creación de una estrategia de doble buffer.
		estrategia = this.getBufferStrategy(); // Obtención de esta estrategia de dibujo.
	}
	
	public void mostrar(String a){
		System.out.println(a);
	}
	
	public void testearX(){	
		tanque = new Tanque(1); // Creación de un tanque principal.
		tanque.setSonidoHabilitado(true);
		otroTanque = new Tanque(0); // Creación de un tanque secundario (para ser controlado mediante RMI).
		mostrar("Tanques creados con éxito.");
		muro = new Muro(1,0); // Creación de un muro.
		mostrar("Muro creado con éxito.");
		

		tanque = new Tanque(1);
		otroTanque = new Tanque(0);
				
		muro = new Muro(1,0);

		mostrar("Estableciendo comunicación...");

		try{Conexion.getConexion(null).conectar();}catch(Exception e){e.printStackTrace();}
		Conexion.getConexion().setTanquePropio(tanque);
		Conexion.getConexion().setTanqueLocalOponente(otroTanque);
		Conexion.getConexion().bindearTanqueLocalOponente();
		try{Conexion.getConexion().ponerADisposicionTanqueRemoto();}catch(Exception e){e.printStackTrace();}
		mostrar("Comunicación establecida con éxito.");
		
		(new Thread(Conexion.getConexion().getHiloManejadorDeTanqueRemoto())).start();
		mostrar("Hilo manejador del tanque remoto creado e iniciado.");
		
		Thread hilo = new Thread(
			new Runnable(){
				public void run(){
					while(true){
						actuar();
						pintar();
						try{Thread.sleep(10);}catch(Exception e){}
					}
					
				}
			}
		);
		ventana.addKeyListener(new Jugador(tanque));
		
		hilo.start();
		mostrar("Hilo principal del juego iniciado.");
	}
//	public void testearSonido(){
//		Thread hilo1 = new Thread({
//			new Runnable(){
//				public void run(){
//					
//				}
//			}
//		}
//	}
	
	// Método que realiza el nuevo dibujo de los objetos indicados.
	public void pintar(){
		Graphics2D g = (Graphics2D)estrategia.getDrawGraphics();
		
		g.setColor(Color.WHITE); // Pintado de un rectángulo fondo, color blanco.
		g.fillRect(0, 0, 100, 100);
		
		tanque.pintar(g); 
		
		otroTanque.setY(20); // Al tanque controlado se lo fija en Y para lograr visualizar la diferencia de comportamientos.
		otroTanque.pintar(g);
		muro.pintar(g);
		
		estrategia.show();
	}
	
	// Método que da lugar al comportamiento de los objetos.
	public void actuar(){
		contador++;
		tanque.actuar();
		
		tanque.setX(0); // Se fija la posición del tanque principal para poder verlo.
		tanque.setY(0);
		otroTanque.actuarResumido();
		muro.deterioro(1*Muro.UNIDAD_DE_MAGNITUD); // El muro es deteriorado.
		//tanque.choque(false); // Es indicado al tanque que se ha producido un choque, para visualizar su comportamiento.

	}
	public static void main(String args[]){
		ClaseTest testeo = new ClaseTest();
		testeo.testearX();
	}
}
