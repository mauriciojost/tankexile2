package presentacion;

import paquete.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import javax.swing.*;
import javax.swing.event.*;
import java.rmi.RemoteException;

import java.util.*;

//SplitPaneDemo itself is not a visible component.
import java.util.logging.Level;
import java.util.logging.Logger;

public class Escenografia extends JFrame implements MouseListener, ListSelectionListener {
    private Vector<File> fileNames = new Vector<File>();
    private static JList lista;
    private JSplitPane splitPane;
    private static Escenografia escenografia;
    private File dir = new File("Circuitos");
    private  File file;
    private String names;
    private int indeX;
    final static String tec = ".tec";
    private File[] vector_de_archivos;//* = dir.listFiles();*/
    private PrePartida prePartida;
    private JTextField estado = new JTextField("Estado: ");
	private JButton b_seleccion;
	private JButton b_cancelar;
	private File ultimoArchivoElegido;
	
    public Escenografia(PrePartida prepartida) {

		super("TankExile - Seleccionar Escenario");
		//setBounds(cx,cy,Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300);
		//escenografia=new Escenografia();
		this.prePartida=prepartida;
        setBounds(prepartida.getX(), prepartida.getY(), Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300);
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300));
		//getContentPane().setLayout(new FlowLayout());
		getContentPane().setLayout(new FlowLayout());
		getContentPane().setBackground(Finals.colorGris);
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		// Filtrado de archivos del tipo circuito.
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory() || (!tec.equals(f.getName().substring((f.getName().length())-4)))) {
					return false;
				} else {
					return true;
				} 
			}
		};
		
		vector_de_archivos = dir.listFiles(fileFilter); // Se aplica el filtro para que solo sean visibles los archivos .tec de la carpeta Circuitos.
		lista = new JList(vector_de_archivos);
		//lista.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-210,Finals.ALTO_VENTANA-300-110));
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setSelectedIndex(0); // Determina en que posicion de la lista se establece inicialmente el foco.
		lista.addListSelectionListener(this);
		lista.setSelectedIndex(0);
		
		JScrollPane jsp = new JScrollPane(lista);
		jsp.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-210, Finals.ALTO_VENTANA-400));
		jsp.setBackground(Finals.colorGris);
		
		// Creamos Botones.
		
		Creador creador = new Creador();
		
		b_seleccion = creador.crearBoton("Seleccionar", "Utilizar circuito seleccionado", this);
		
		b_cancelar = creador.crearBoton("Cancelar", "", this);
		
		JPanel panel_boton = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-200, 115), new FlowLayout(FlowLayout.CENTER));
		panel_boton.add(b_seleccion);
		panel_boton.add(b_cancelar);
		
		estado.setText("Estado:                     ");
		estado.setEditable(false);
		estado.setBackground(Finals.colorGris);
		
		JPanel panel_estado = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-200,100), new GridLayout(1,1));
		panel_estado.add(estado);
		
		getContentPane().add(jsp);
		getContentPane().add(panel_boton);
		getContentPane().add(panel_estado);
		
		setVisible(true);
		escenografia=this;
		        
	}
	
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	static public Escenografia getEsc() {
		return escenografia;
	}
	public static Object getSelectedNameFile() {
		return lista.getSelectedValue();
	}
	
	public int getSelectedindexFile() {
		return indeX;
	}
	
	public void valueChanged(ListSelectionEvent e) {
		/* if (e.getValueIsAdjusting())
			return;

        JList theList = (JList)e.getSource();
        if (theList.isSelectionEmpty()) {
             System.out.println("seleccion vacia");
        } else {
            int index = theList.getSelectedIndex();
            indeX = theList.getSelectedIndex();
            //names= (String) theList.getSelectedValue();
            names = (String) list.getSelectedValue();
        }*/
    }
	
	public void metodoDeControl() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	public void responderSeleccionar(){
		File archivo = (File)lista.getSelectedValue();
		if (paquete.CargadorCircuitoTXT.validarCircuito(archivo.getPath())){ // Se selecciona circuito sujeto a la condición de que este sea validado.
			//////////////////MAURICIO//////////////////////////////////
			// Esta parte ha de ser modificada. Donde el true se ha de especificar si el archivo es propio o remoto (ver con qué criterio).
			prePartida.setCircuitoSeleccionado(archivo); // <<<----
			ultimoArchivoElegido = archivo;
			this.dispose();
			prePartida.setLocation(this.getX(), this.getY());
			prePartida.setVisible(true);
			System.out.println("Circuito validado correctamente.");
			this.prePartida.setEstado("Circuito: '"+ultimoArchivoElegido+"'.", Font.PLAIN);
		}else{
			JOptionPane.showMessageDialog(null, "Circuito no válido.");

			System.out.println("Circuito no válido.");
		}
		
	}
	
	public void responderCancelar(){
		this.dispose();
		prePartida.setLocation(this.getX(), this.getY());
		prePartida.setVisible(true);
		if (ultimoArchivoElegido!=null){
			this.prePartida.setEstado("Circuito: '"+ultimoArchivoElegido+"'.", Font.PLAIN);
		}
		
	}
	
	public void mouseClicked(MouseEvent e) {
		try {
			String nombre = new String(((JButton)e.getSource()).getText());
			this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
		} catch (Exception ex) {	
			ex.printStackTrace();
			Logger.getLogger(PrePartida.class.getName()).log(Level.SEVERE, null, ex);
		}		
	}

	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}

