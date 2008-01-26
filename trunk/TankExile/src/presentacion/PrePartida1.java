
package presentacion;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedCharacterIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import paquete.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrePartida1 extends JFrame implements MouseListener{
	private Presentacion1 presentacion1;
        private static PrePartida1 prepartida1;
        private Escenografia escenografia;
        private String imageFilePath = "C:\\tank.jpg";
        private ImageIcon ii = new ImageIcon(imageFilePath);
       
	// Constructor de la clase.
	public PrePartida1(int x, int y,Presentacion1 presentacion1){
		super("Tank Exile - Pre Partida");
                this.presentacion1=presentacion1;
		setBounds(x,y,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-400); // Reajusta tamaño de la ventana, sin modificar su posición.
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-500));
		panel.setLayout(new GridLayout(2,3));
                //panel.setLayout(new BorderLayout());
		panel.setBackground(Color.LIGHT_GRAY);
		addWindowListener(null);
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
               
                
               
		JButton b1 = new JButton("Jugar");
		b1.setPreferredSize(new Dimension(110,30));
		b1.addMouseListener(this);
                
                JButton ipButton = new JButton("Cambiar ip");
                ipButton.setPreferredSize(new Dimension(110,30));
                ipButton.addActionListener(new ipBackListener());
			
		JButton b2 = new JButton("Opciones"); 
		b2.setPreferredSize(new Dimension(110,30));
		b2.addMouseListener(this);
                
                JButton exitButton = new JButton("Salir");
                exitButton.setPreferredSize(new Dimension(110,30));
                exitButton.addActionListener(new exitListener());
                
                JButton exit2Button = new JButton("Salir2");
                exit2Button.setPreferredSize(new Dimension(110,30));
                exit2Button.addActionListener(new exitListener());
                
                JPanel jp4 = new JPanel();
		jp4.setLayout(new GridLayout(4,1));
		jp4.setBackground(Color.LIGHT_GRAY);
		//jp4.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-500,(Finals.ALTO_VENTANA-1000)/2));
		jp4.setPreferredSize(new Dimension(110,30));
                jp4.setBounds(0, 0, 110, 30);
                jp4.setSize(new Dimension(110,30));
                
                jp4.add(b1,0);
                jp4.add(ipButton,1);
		jp4.add(b2,2);
                jp4.add(exitButton,3);
                
                
		JPanel jp5 = new JPanel();
		jp5.setLayout(new FlowLayout());
		jp5.setBackground(Color.LIGHT_GRAY);
		
		jp5.setPreferredSize(new Dimension(400,300));
            
               //Creo icono para cargar imagen
                JLabel iM = new JLabel();
                iM.setIcon(ii);
                iM.setOpaque(false);
                iM.setSize(200, 200);
                
                
                jp5.add(iM);
		
                 
		panel.add(jp5,0);
		panel.add(BorderLayout.CENTER,jp4);
		setVisible(true);
                
                prepartida1=this;
		System.out.println(" PASE POR CONSTRUCTOR DE PRE PARTIDA");
	}
	
	
		// Metodo que responde al evento sobre el boton Jugar.
	public void Jugar() {
		try {
			this.dispose();
			System.out.println("POR CONSTRUIR PARTIDA");


			String ipaca = new String("192.168.0.7");
			String ipalla = new String("192.168.0.101"); //con 1
			String circuito = new String("circuito2.txt");
			Conexion conexion = new Conexion(null);
			do{
				conexion.conectar();
			}while(!conexion.conexionLista());
			conexion.start();
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
		} catch (Exception ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		}

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
		} catch (Exception ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} 
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}
