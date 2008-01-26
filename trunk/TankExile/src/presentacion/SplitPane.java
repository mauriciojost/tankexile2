package presentacion;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileFilter;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;

//SplitPaneDemo itself is not a visible component.
public class SplitPane implements ListSelectionListener {
    private Vector<File> fileNames = new Vector<File>();
    private JList list;
    private JSplitPane splitPane;
    private File dir = new File("Circuitos");
    private  File files;
    private String names;
    private int indeX;
    final static String tec = "tec";
    private File[] file = dir.listFiles();
    

    public SplitPane() {
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
		list = new JList(fileNames);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0); // Modifica cual es el primer marcado ??
		list.addListSelectionListener(this);
		
		// Creamos Etiqueta.
		JLabel selCircuit = new JLabel("   Seleccion de Circuito    ");
		JLabel newLine =    new JLabel("                                              ");
		// Creamos Botones.
		// abrir
		JButton openButton = new JButton("Seleccionar"/*, new ImageIcon("images/open.gif")*/);
		openButton.addActionListener(new OpenListener(list));
		
		// cancelar
		JButton cancelButton = new JButton("Cancelar"/*, new ImageIcon("images/cancel.gif")*/);
		cancelButton.addActionListener(new CancelListener());
		
		JPanel buttonPanel = new JPanel();
		JPanel labelPanel = new JPanel();
		JPanel bigPanel = new JPanel();
		
		buttonPanel.setLayout(new FlowLayout(50,2,50));
		labelPanel.add(selCircuit); // Etiqueta de Seleccion de Circuito.
		// buttonPanel.add(newLine);// Espacio.
		buttonPanel.add(openButton); // boton de abrir
		buttonPanel.add(cancelButton); // boton de cancelar
		
		bigPanel.add(labelPanel);
		bigPanel.add(buttonPanel);
		
		JScrollPane listScrollPane = new JScrollPane(list);
		
		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(listScrollPane);
		splitPane.setRightComponent(bigPanel);
		splitPane.setOneTouchExpandable(true);
		
		// Provide minimum sizes for the two components in the split pane.
		// Dimension minimumSize = new Dimension(100, 50);
		//listScrollPane.setMinimumSize(minimumSize);
		
		// Set the initial location and size of the divider.
		splitPane.setDividerLocation(200);
		splitPane.setDividerSize(10);
		
		// Provide a preferred size for the split pane.
		splitPane.setPreferredSize(new Dimension(350, 200));
		
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					indeX = list.locationToIndex(e.getPoint());
					System.out.println("Double clicked on Item " + indeX);
					list.setSelectedIndex(indeX);
				}
			}
		};
		list.addMouseListener(mouseListener);
		JFrame jf = new JFrame("ANDA ");
		jf.setPreferredSize(new Dimension(400,400));
		//jf.setResizable(false);
		jf.getContentPane().setPreferredSize(new Dimension(400,400));
		jf.getContentPane().add(splitPane);
		jf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {System.exit(0);}
		}); // Se define un objeto que escucha los eventos sobre la ventana.
		jf.setVisible(true);
	}
	
	public JSplitPane getSplitPane() {
		return splitPane;
	}
	
	public Object  getSelectedNameFile() {
		return  list.getSelectedValue();
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
}
