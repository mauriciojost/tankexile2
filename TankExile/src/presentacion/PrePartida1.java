
package presentacion;


import java.awt.event.MouseEvent;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PrePartida1 extends JFrame implements MouseListener{
	private Presentacion1 presentacion1;
	private static PrePartida1 prepartida1;
	private Escenografia escenografia;
	//private String imageFilePath = "C:\\tank.jpg";
	//private ImageIcon ii = new ImageIcon(imageFilePath);
	private ImageIcon ii = new ImageIcon(getClass().getClassLoader().getResource("res/tank.jpg"));
	private ImageIcon esp = new ImageIcon(getClass().getClassLoader().getResource("res/EsperaOponente.jpg"));
	private JFrame ventanaEspera = new JFrame("Tank Exile - Esperando Oponente");
        // Constructor de la clase.
    @SuppressWarnings("static-access")
	public PrePartida1(int x, int y,Presentacion1 presentacion1){
		super("Tank Exile - Pre Partida");
		this.presentacion1=presentacion1;
		setBounds(x,y,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370);
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370));
		panel.setLayout(new GridLayout(2,1));
		panel.setBackground(Color.LIGHT_GRAY);
		
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		JButton b_inicio = new JButton("Inicio");
		b_inicio.setPreferredSize(new Dimension(110,30));
		b_inicio.addMouseListener(this);
		
		JButton b_cambia_ip = new JButton("Cambiar IP");
		b_cambia_ip.setPreferredSize(new Dimension(110,30));
		b_cambia_ip.addMouseListener(this);
		
		JButton b_opciones = new JButton("Opciones");
		b_opciones.setPreferredSize(new Dimension(110,30));
		b_opciones.addMouseListener(this);
		
		JButton b_salida = new JButton("Salida");
		b_salida.setPreferredSize(new Dimension(110,30));
		b_salida.addMouseListener(this);
		
		JButton exit2Button = new JButton("Salir2");
		exit2Button.setPreferredSize(new Dimension(110,30));
		exit2Button.addMouseListener(this);
		
		JPanel jp4 = new JPanel();
		jp4.setLayout(new GridLayout(4,1));
		jp4.setBackground(Color.LIGHT_GRAY);
		jp4.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-200));
		jp4.add(b_inicio);
		jp4.add(b_cambia_ip);
		jp4.add(b_opciones);
		jp4.add(b_salida);
		
		JPanel jp5 = new JPanel();
		jp5.setLayout(new FlowLayout());
		jp5.setBackground(Color.LIGHT_GRAY);
		jp5.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-445));
		

		jp5.setPreferredSize(new Dimension(400,300));
            
               //Creo una Etiqueta para cargar icono q contiene a la imagen
                JLabel iM = new JLabel();
                iM.setIcon(ii);
                iM.setOpaque(false);
                iM.setSize(200, 200);
                
                
                jp5.add(iM);

		
		panel.add(jp5);
		panel.add(jp4);
		setVisible(true);
		
		this.prepartida1=this;
		System.out.println(" PASE POR CONSTRUCTOR DE PRE PARTIDA");
	}
	
	// Metodo que responde al evento sobre el boton Jugar.
	public void responderInicio() {
        //coneccionOponente es un campo q indica si el oponente ya presiono en jugar(true) o todavia no(false)
       //en cada caso respectivamente comenzara el juego o se mantendra esperando el inicio en un Frame    
        System.out.println(" PASE POR responder inicio");
           
        //MOstrar pantalla de esperar oponente mientras Oponente.coneccionOponente sea Falso
        //while(!Oponente.coneccionOponente){}
        
             try{this.dispose();
             
                
		ventanaEspera.setBounds(100,50,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370);
		setResizable(false); // No se permite dar nuevo tamaño a la ventana.
		
		JPanel panel = (JPanel)this.getContentPane(); // Panel donde se grafican los objetos (bloques)que componen el escenario y los tanques que representan a cada jugador.
		panel.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-370));
		//panel.setLayout(new GridLayout(2,1));
		panel.setBackground(Color.LIGHT_GRAY);
		
		addWindowListener(new WindowAdapter() {
                @Override
            public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		 
                JLabel iM = new JLabel();
                iM.setIcon(ii);
                iM.setOpaque(false);
                iM.setSize(200, 200);
                panel.add(iM);
                
                ventanaEspera.setVisible(true);
             }
             catch (Exception ex2){
             System.out.println("exception en responder inicio");
             }
         
         /*while(!Oponente.coneccionOponente){
            
                                                          } */
            try {
			ventanaEspera.dispose();
                        //this.dispose();
			System.out.println("POR CONSTRUIR PARTIDA");
			String ipaca = new String("192.168.0.7");
			String ipalla = new String("192.168.0.101"); //con 1
			//String circuito = new String("circuito2.txt");
                        String circuito = Escenografia.getSelectedNameFile().toString();
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
	public void responderOpcion() {
		System.out.println("POR CONSTRUIR ESCENOGRAFIA");
		escenografia = new Escenografia(prepartida1);
		this.dispose();
	}
	
	//
	public void responderCambia(){
		System.out.println("responde Cambia");
		this.dispose();
		presentacion1.setVisible(true);
	}
	
    public void responderSalida(){
		System.exit(0);
	}
	
	static public PrePartida1 getPrePartida1() {
		return prepartida1;
	}
        
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			nombre = nombre.substring(0, 6);
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {
			Logger.getLogger(PrePartida1.class.getName()).log(Level.SEVERE, null, ex);
		} 
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}
