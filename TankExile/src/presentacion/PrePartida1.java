
package presentacion;

import java.awt.Button;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class PrePartida1 extends JFrame implements MouseListener{
	private Presentacion1 presentacion1;
        private static PrePartida1 prepartida1;
        private Escenografia escenografia;
	// Constructor de la clase.
	public PrePartida1(int x, int y,Presentacion1 presentacion1){
		super("Tank Exile - Pre Partida");
                this.presentacion1=presentacion1;
		setBounds(x,y,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-500); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-500));
		panel.setLayout(new GridLayout(2,1));
		panel.setBackground(Color.LIGHT_GRAY);
		addWindowListener(null);
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		JButton b1 = new JButton("Jugar");
		b1.setPreferredSize(new Dimension(110,30));
		b1.addMouseListener(this);
		
		JPanel jp4 = new JPanel();
		jp4.setLayout(new FlowLayout());
		jp4.setBackground(Color.LIGHT_GRAY);
		jp4.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/2));
		jp4.add(b1);
		
		JButton b2 = new JButton("Opciones"); 
		b2.setPreferredSize(new Dimension(110,30));
		b2.addMouseListener(this);
		
		JPanel jp5 = new JPanel();
		jp5.setLayout(new FlowLayout());
		jp5.setBackground(Color.LIGHT_GRAY);
		jp5.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-500)/2));
		jp5.add(b2);
		
		panel.add(jp4);
		panel.add(jp5);
		setVisible(true);
                prepartida1=this;
		System.out.println(" PASE POR CONSTRUCTOR DE PRE PARTIDA");
	}
	
	
		// Metodo que responde al evento sobre el boton Jugar.
	public void Jugar() {
		this.dispose();
		System.out.println("POR CONSTRUIR PARTIDA");


		String ipaca = new String("192.168.0.7"); 
		String ipalla = new String("192.168.0.101"); //con 1
		String circuito = new String("circuito2.txt");
		Conexion conexion = new Conexion(null);

		//conexion.bindearMisArchivos();
		/*
		do{
			try{
				conexion.ponerADisposicionArchivosRemotos();
			}catch(Exception e){
				System.out.println("Intento fallido para obtener archivos remotos. Intentando de nuevo...");
				try {Thread.sleep(1000);} catch (InterruptedException ex) {Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);}
			}
		}while(!conexion.archivosListo());
		*/
		/*
		try {
			conexion.copiarDeHostRemoto("circuiton.txt", "copio.txt");
			conexion.enviarAHostRemoto("circuiton.txt", "copio.txt");
		} catch (IOException ex) {
			System.err.println("Error al intentar copiar en el método de Conexion copiarDeHostRemoto.");
			Logger.getLogger(Partida.class.getName()).log(Level.SEVERE, null, ex);
		}
		*/
		Partida tank_exile = new Partida(0, circuito, conexion);
		tank_exile.jugar();

	}
    // Método que responder al evento sobre el boton Opciones.
	public void Opciones() {
		
		System.out.println("POR CONSTRUIR ESCENOGRAFIA");
		escenografia =new Escenografia(prepartida1);
		this.dispose();
	}
        
	static public PrePartida1 getPrePartida1() {
		return prepartida1;
	}
        
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			this.getClass().getMethod(nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (IllegalAccessException ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IllegalArgumentException ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchMethodException ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} catch (SecurityException ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}
