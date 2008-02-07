
package presentacion;


import java.awt.BorderLayout;

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

import java.io.File;
import java.lang.reflect.Method;


import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class PrePartida1 extends JFrame implements MouseListener{
	private Presentacion1 presentacion1;


	private Conexion conexion;

	private static PrePartida1 prepartida1;
	private Escenografia escenografia;
	private JTextField estado = new JTextField("Estado: ");
	//private String imageFilePath = "C:\\tank.jpg";
	//private ImageIcon ii = new ImageIcon(imageFilePath);
	private ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("res/tank.JPG"));
	private ImageIcon esp = new ImageIcon(getClass().getClassLoader().getResource("res/EsperaOponente.jpg"));
	private JFrame ventanaEspera = new JFrame("Tank Exile - Esperando Oponente");

	private JButton b_inicio;
	private File circuitoSeleccionado;
    
	// Constructor de la clase.
    @SuppressWarnings("static-access")
	public PrePartida1(Presentacion1 presentacion1, Conexion conexion){
		super("Tank Exile - Pre Partida");
		this.presentacion1=presentacion1;
		setBounds(presentacion1.getX(),presentacion1.getY(),Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370);
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370));
		panel.setLayout(new GridLayout(2,1));
		panel.setBackground(Color.LIGHT_GRAY);
		
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		b_inicio = new JButton("Inicio");
		b_inicio.setPreferredSize(new Dimension(110,30));
		b_inicio.addMouseListener(this);
		b_inicio.setEnabled(false);
		
		JButton b_cambia_ip = new JButton("Cambiar IP");
		b_cambia_ip.setPreferredSize(new Dimension(110,30));
		b_cambia_ip.addMouseListener(this);
		
		JButton b_elegir_circuito = new JButton("Elegir Circuito");
		b_elegir_circuito.setPreferredSize(new Dimension(110,30));
		b_elegir_circuito.addMouseListener(this);
				
		JButton b_salida = new JButton("Salida");
		b_salida.setPreferredSize(new Dimension(110,30));
		b_salida.addMouseListener(this);
		
		estado.setEditable(false);
		estado.setBackground(Finals.colorFondo);
		
		JPanel panel_botones = new JPanel();
		panel_botones.setLayout(new GridLayout(5,1));
		panel_botones.setBackground(Finals.colorFondo);
		panel_botones.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-200));
		panel_botones.add(b_inicio);
		panel_botones.add(b_cambia_ip);
		panel_botones.add(b_elegir_circuito);
		panel_botones.add(b_salida);
		panel_botones.add(estado);
		
		JPanel panel_icono = new JPanel();
		panel_icono.setLayout(new FlowLayout());
		panel_icono.setBackground(Finals.colorFondo);
		panel_icono.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-445));
		

		//Creo una Etiqueta para cargar icono q contiene a la imagen
		JLabel iM = new JLabel();
		iM.setIcon(ii);
		iM.setOpaque(false);
		iM.setSize(200, 200);
		panel_icono.add(iM);

		
		panel.add(panel_icono);
		panel.add(panel_botones);
		setVisible(true);
		
		this.prepartida1=this;
		System.out.println(" PASE POR CONSTRUCTOR DE PRE PARTIDA");
		this.conexion = conexion;
		
		//Mauricio
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
		//Mauricio
	}
	

	public void setEstado(String noticia){
		estado.setText(noticia);
	}
	
	/*	
	// Metodo que responde al evento sobre el boton Jugar.
	public void Jugar() {
		this.dispose();
		
		// Mauricio dice: donde dice circuito se ha de entregar el nombre del txt (en el directorio del jar).
		// En caso de no estar en el lugar del jar, indicar de forma relativa a ese directorio que contiene al jar.
		String circuito = new String("circuito2.txt");
		Partida tank_exile = new Partida(circuito, conexion);
		tank_exile.jugar();
	}
	*/
	

	// Metodo que responde al evento sobre el boton Jugar.
	public void responderInicio() {
        //coneccionOponente es un campo q indica si el oponente ya presiono en jugar(true) o todavia no(false)
       //en cada caso respectivamente comenzara el juego o se mantendra esperando el inicio en un Frame    
        if (!b_inicio.isEnabled()) return;
           
        //MOstrar pantalla de esperar oponente mientras Oponente.coneccionOponente sea Falso
        //while(!Oponente.coneccionOponente){}
        
        try{
			this.dispose();
             
                
			ventanaEspera.setBounds(100,50,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370);
			setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
			JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
			panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370));
			//panel.setLayout(new GridLayout(2,1));
			panel.setBackground(Color.LIGHT_GRAY);
		
			addWindowListener(
				new WindowAdapter(){
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				}
			); // Se define un objeto que escucha los eventos sobre la ventana.
		 
            JLabel iM = new JLabel();
            iM.setIcon(ii);
            iM.setOpaque(false);
            iM.setSize(200, 200);
            panel.add(iM);
                
            ventanaEspera.setVisible(true);
		}catch (Exception ex){ex.printStackTrace();}
         
        /*while(!Oponente.coneccionOponente){
            
                                                          } */

        try {
			

			//Conexion conexion = new Conexion("0.0.0.0");
			/*do{
				conexion.conectar();
			}while(!conexion.conexionLista());
			conexion.start();*/
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
			Partida partida = new Partida(this.circuitoSeleccionado.getPath(), conexion);
			ventanaEspera.dispose();
			partida.jugar();
			
		} catch (Exception ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
        
	public void setCircuitoSeleccionado(File circuitoSeleccionado){
		this.circuitoSeleccionado = circuitoSeleccionado;
	}
    // Método que responder al evento sobre el boton Opciones.
	public void responderElegir() {
		System.out.println("POR CONSTRUIR ESCENOGRAFIA");
		this.escenografia = new Escenografia(prepartida1);
		
		this.dispose();
		b_inicio.setEnabled(true);
	}
	
	//
	public void responderCambia(){
		System.out.println("responde Cambia");
		this.dispose();
		presentacion1.setLocation(this.getX(), this.getY());
		presentacion1.setVisible(true);
	}
	
    public void responderSalida(){
		System.exit(0);
	}
	
	public static PrePartida1 getPrePartida1() {
		return prepartida1;
	}
        
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			nombre = nombre.substring(0, 6);
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {	
			ex.printStackTrace();
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}
