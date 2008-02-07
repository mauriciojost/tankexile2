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

public class OldEscenografia extends JFrame implements VentanaControlable, ListSelectionListener {
    private Vector<File> fileNames = new Vector<File>();
    private static JList lista;
    private JSplitPane splitPane;
    private static OldEscenografia escenografia;
    private File dir = new File("Circuitos");
    private  File file;
    private String names;
    private int indeX;
    final static String tec = ".tec";
    private File[] vector_de_archivos;//* = dir.listFiles();*/
    private PrePartida1 prePartida;
    
	private JButton b_abrir;
	private JButton b_cancelar;
	
	
    public OldEscenografia(PrePartida1 prepartida) {

		super("TankExile - Seleccionar Escenario");
		//setBounds(cx,cy,Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300);
		//escenografia=new Escenografia();
		this.prePartida=prepartida;
        setBounds(prepartida.getX(), prepartida.getY(), Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300);
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300));
		getContentPane().setLayout(new FlowLayout());
		getContentPane().setBackground(Color.LIGHT_GRAY);
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		// Filtrado de archivos del tipo circuito.
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return false;
				} 
				//int i = f.getName().lastIndexOf('.');
				//if (i > 0 &&  i < f.getName().length() - 1) {
				//	String extension = s.substring(i+1).toLowerCase();
				if (tec.equals(f.getName().substring((f.getName().length())-4))) {
						return true;
					} else {
						return false;
					}
				}
				//return false;
		    //}
		};
		
		vector_de_archivos = dir.listFiles(fileFilter); // Se aplica el filtro para que solo sean visibles los archivos .tec de la carpeta Circuitos.
		//String[] children = dir.list();
		//String[] children;
		if (vector_de_archivos == null) {
			// Either dir does not exist or is not a directory.
			//System.out.println("no tiene nada"); 
		} else {
			for (int i=0; i<vector_de_archivos.length; i++) {
				// Get filename of file or directory
				fileNames.add(i, vector_de_archivos[i]);
				
				// PAra borrarr
				//System.out.println(file[i]+"texto");
			}
		}
		// Create the list of images and put it in a scroll pane.
		//lista = new JList(fileNames);
		lista = new JList(vector_de_archivos);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setSelectedIndex(0); // Determina en que posicion de la lista se establece inicialmente el foco.
		lista.addListSelectionListener(this);
		
		JScrollPane jsp = new JScrollPane(lista);
		jsp.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-210, Finals.ALTO_VENTANA-300-85));
		jsp.setBackground(Color.LIGHT_GRAY);
		// Creamos Botones.
		// abrir
		b_abrir = new JButton("Seleccionar"/*, new ImageIcon("images/open.gif")*/);
		b_abrir.setPreferredSize(new Dimension(110,30));
		/*b_abrir.addActionListener(new OpenListener(lista));*/ //REVISAR: yo comenté, Mauricio
		b_abrir.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					System.out.println(lista.getSelectedValue());
					System.out.println(lista.getSelectedIndex());
					
					prePartida.setCircuitoSeleccionado((File)lista.getSelectedValue());
					Escenografia.getEsc().dispose();
					PrePartida1.getPrePartida1().setVisible(true);
				}
			}
		);
		
		// cancelar
		b_cancelar = new JButton("Cancelar"/*, new ImageIcon("images/cancel.gif")*/);
		b_cancelar.setPreferredSize(new Dimension(110,30));
		b_cancelar.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Escenografia.getEsc().dispose();/*
<<<<<<< .mine
				PrePartida1.getPrePartida1().setLocation(Escenografia.getEsc().getX(), Escenografia.getEsc().getY());
				PrePartida1.getPrePartida1().show();
=======
				PrePartida1.getPrePartida1().setVisible(true);
>>>>>>> .r27*/
				PrePartida1.getPrePartida1().setEstado("Estado: Se ha seleccionado un circuito.");
            //hay que implementar la vuelta atras a la pagina que la precede
			}
		});
		
		JPanel jp_boton = new JPanel();
		jp_boton.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200, Finals.ANCHO_VENTANA-300-290));
		jp_boton.setLayout(new FlowLayout(FlowLayout.CENTER));
		jp_boton.setBackground(Color.LIGHT_GRAY);
		jp_boton.add(b_abrir);
		jp_boton.add(b_cancelar);
		
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
		
		getContentPane().add(jsp);
		getContentPane().add(jp_boton);
                
		
		setVisible(true);
		escenografia=this;
		//frame.add(escenografia.getEsc());
		//frame.setVisible(true);
                
	}
	
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	static public OldEscenografia getEsc() {
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
	
	protected static Vector parseList(String theStringList) {
		Vector<String> v = new Vector<String>(10);
		StringTokenizer tokenizer = new StringTokenizer(theStringList, " ");
		while (tokenizer.hasMoreTokens()) {
			String image = tokenizer.nextToken();
			v.addElement(image);
		}
		return v;
	}
	public void metodoDeControl() throws RemoteException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}

