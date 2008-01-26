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

public class Escenografia extends JFrame implements VentanaControlable, ListSelectionListener {
    private Vector<File> fileNames = new Vector<File>();
    private JList lista;
    private JSplitPane splitPane;
    private static Escenografia escenografia;
    private File dir = new File("Circuitos");
    private  File files;
    private String names;
    private int indeX;
    final static String tec = "tec";
    private File[] file = dir.listFiles();
    private PrePartida1 prepartida1;
    
    public Escenografia(PrePartida1 prepartida1) {

		super("TankExile - Seleccionar Escenario");
		//setBounds(cx,cy,Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300);
		//escenografia=new Escenografia();
                this.prepartida1=prepartida1;
                setBounds(prepartida1.getX(), prepartida1.getY(), Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300);
                setResizable(false);
		getContentPane().setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200,Finals.ALTO_VENTANA-300));
		getContentPane().setLayout(new GridLayout(3,1));
		addWindowListener(new WindowAdapter() {
            @Override
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		
		// Filtrado de archivos del tipo circuito.
		FileFilter fileFilter = new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String s = f.getName();
				int i = s.lastIndexOf('.');
			
				if (i > 0 &&  i < s.length() - 1) {
					String extension = s.substring(i+1).toLowerCase();
					if (tec.equals(extension)) {
						return true;
					} else {
						return false;
					}
				}
				return false;
		    }
		};
		file = dir.listFiles(fileFilter);
		//String[] children = dir.list();
		//String[] children;
		if (file == null) {
			// Either dir does not exist or is not a directory.
			System.out.println("no tiene nada"); 
		} else {
			for (int i=0; i<file.length; i++) {
				// Get filename of file or directory
				fileNames.add(i, file[i]);
				// PAra borrarr
				//System.out.println(file[i]+"texto");
			}
		}
		// Create the list of images and put it in a scroll pane.
		lista = new JList(fileNames);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lista.setSelectedIndex(0); // Determina en que posicion de la lista se establece inicialmente el foco.
		lista.addListSelectionListener(this);
		
		JScrollPane jsp = new JScrollPane(lista);
		jsp.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200, Finals.ALTO_VENTANA-300-100));
		// Creamos Etiqueta.
		JLabel jl1 = new JLabel("   Seleccion de Circuito    ");
		jl1.setBackground(Color.LIGHT_GRAY);
		
		JPanel jp_titulo = new JPanel();
		jp_titulo.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200, Finals.ALTO_VENTANA-300-322));
		jp_titulo.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp_titulo.setBackground(Color.LIGHT_GRAY);
		jp_titulo.add(jl1);
		
		// Creamos Botones.
		// abrir
		JButton abrir = new JButton("Seleccionar"/*, new ImageIcon("images/open.gif")*/);
		abrir.setPreferredSize(new Dimension(110,30));
		abrir.addActionListener(new OpenListener(lista));
		
		// cancelar
		JButton cancelar = new JButton("Cancelar"/*, new ImageIcon("images/cancel.gif")*/);
		cancelar.setPreferredSize(new Dimension(110,30));
		cancelar.addActionListener(new CancelListener());
		
		JPanel jp_boton = new JPanel();
		jp_boton.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-200, Finals.ANCHO_VENTANA-300-322));
		jp_boton.setLayout(new FlowLayout(FlowLayout.CENTER));
		jp_boton.setBackground(Color.LIGHT_GRAY);
		jp_boton.add(abrir);
		jp_boton.add(cancelar);
		
		MouseListener mouseListener = new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					indeX = lista.locationToIndex(e.getPoint());
					System.out.println("Double clicked on Item " + indeX);
					lista.setSelectedIndex(indeX);
				}
			}
		};
		lista.addMouseListener(mouseListener);
		
		getContentPane().add(jp_titulo);
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
	static public Escenografia getEsc() {
		return escenografia;
	}
	public Object  getSelectedNameFile() {
		return  lista.getSelectedValue();
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

