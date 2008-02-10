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
    private PrePartida1 prePartida;
    private JTextField estado = new JTextField("Estado: ");
	private JButton b_seleccion;
	private JButton b_cancelar;
	private File ultimoArchivoElegido;
	
    public Escenografia(PrePartida1 prepartida) {

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
		
		b_seleccion = creador.crearBoton("Seleccionar", "", this);
		
		/*
		b_seleccion.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					System.out.println("1 - " + lista.getSelectedValue());
					System.out.println("2 - "+ lista.getSelectedIndex());
					// LINEA REEE IMPORTANTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					prePartida.setCircuitoSeleccionado((File)lista.getSelectedValue());
					// LINEA REEE IMPORTANTE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					Escenografia.getEsc().dispose();
					
					PrePartida1.getPrePartida1().setVisible(true);
				}
			}
		);
		*/
		// cancelar
		b_cancelar = creador.crearBoton("Cancelar", "Cede la seleccion al oponente", this);
		/*
		b_cancelar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Escenografia.getEsc().dispose();

				PrePartida1.getPrePartida1().setLocation(Escenografia.getEsc().getX(), Escenografia.getEsc().getY());

				PrePartida1.getPrePartida1().setVisible(true);

				PrePartida1.getPrePartida1().setEstado("Estado: Se ha seleccionado un circuito.");
            //hay que implementar la vuelta atras a la pagina que la precede
			}
		});
		*/
		JPanel panel_boton = creador.crearPanel(new Dimension(Finals.ANCHO_VENTANA-200, 115), new FlowLayout(FlowLayout.CENTER));
		panel_boton.add(b_seleccion);
		panel_boton.add(b_cancelar);
		
		/*
		MouseListener mouseListener = new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() instanceof JButton){
					try {
						String nombre = new String(((JButton)e.getSource()).getText());
						nombre = nombre.substring(0, 6);
						System.out.println("Nombre: "+nombre);
						this.getClass().getMethod("responder"+nombre, (Class[])null).invoke(this, (Object[])null);
					} catch (Exception ex) {	
						ex.printStackTrace();
					} 
				}else{
					if (e.getClickCount() == 2) {
						indeX = lista.locationToIndex(e.getPoint());
						System.out.println("Double clicked on Item " + indeX);
						lista.setSelectedIndex(indeX);
					}
				}
			}
		};
		lista.addMouseListener(mouseListener);
		*/
		//lista.addMouseListener(this);
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
		//frame.add(escenografia.getEsc());
		//frame.setVisible(true);
                
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
	
	public void responderSelecc(){
		File archivo = (File)lista.getSelectedValue();
		if (paquete.CargadorCircuitoTXT.validarCircuito(archivo.getPath())){
			//////////////////MAURICIO//////////////////////////////////
			// Esta parte ha de ser modificada. Donde el true se ha de especificar si el archivo es propio o remoto (ver con qué criterio).
			prePartida.setCircuitoSeleccionado(archivo,true); // <<<----
			ultimoArchivoElegido = archivo;
			this.dispose();
			prePartida.setLocation(this.getX(), this.getY());
			prePartida.setVisible(true);
			System.out.println("Circuito validado correctamente.");
			this.prePartida.setEstado("Circuito: '"+ultimoArchivoElegido+"'.");
		}else{
			JOptionPane.showMessageDialog(null, "Circuito no válido.");

			System.out.println("Circuito no válido.");
		}
		
	}
	
	public void responderCancel(){
		this.dispose();
		prePartida.setLocation(this.getX(), this.getY());
		prePartida.setVisible(true);
		if (ultimoArchivoElegido!=null){
			this.prePartida.setEstado("Circuito: '"+ultimoArchivoElegido+"'.");
		}
		// ACA SE DEBE ASIGNAR LA RESPONSABILIDAD DE ELEGIR CIRCUITO AL OPONENTE!!!!
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

