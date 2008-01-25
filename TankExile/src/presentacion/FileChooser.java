
package presentacion;

import paquete.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FileChooser extends JFrame implements ActionListener {

    private JTextArea log;
    //private JFileChooser fc = new JFileChooser();

    private String newline = System.getProperty("line.separator");

    public FileChooser(int x, int y) {
		super("Tank Exile - Seleccion de Circuito");
		setBounds(x,y,Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-510);
		setResizable(false);
		
		getContentPane().setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,Finals.ALTO_VENTANA-510));
		getContentPane().setLayout(new GridLayout(2,1));
		getContentPane().setBackground(Color.LIGHT_GRAY);
		
		
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        
		JButton abrir = new JButton("Explorar");
		abrir.addActionListener(this);
		
		JPanel jp6 = new JPanel();
		jp6.setLayout(new FlowLayout());
		jp6.setBackground(Color.LIGHT_GRAY);
		jp6.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-510)/2));
		jp6.add(abrir);
		
		log = new JTextArea(1,25);
		log.setEditable(false);
		log.setMargin(new Insets(2,2,2,2));
		log.setBackground(Color.LIGHT_GRAY);
		
		
		JPanel jp7 = new JPanel();
		jp7.setLayout(new FlowLayout(FlowLayout.LEFT));
		jp7.setBackground(Color.LIGHT_GRAY);
		jp7.setPreferredSize(new Dimension(Finals.ANCHO_VENTANA-250,(Finals.ALTO_VENTANA-510)/2));
		jp7.add(log);
		
		getContentPane().add(jp6);
		getContentPane().add(jp7);
		setResizable(false);
		pack();
		setVisible(true);
    }
	
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser fc = new JFileChooser();
		// Adhiere un objeto que actua como filtro de manera tal que sean visibles archivos con extensión txt.
		fc.addChoosableFileFilter(new TextFilter());
		int returnVal = fc.showOpenDialog(this); // Muestra ventana para abrir archivos.
		
		// Verifica si se seleciona Abrir (opción abrir aceptada).
		if (returnVal == JFileChooser.APPROVE_OPTION) { 
			log.setText("Abriendo: " + fc.getSelectedFile().getName() + ".");
			// Aca se hace la apertura del archivo seleccionado
			// Luego, se debe crear un objeto
		}else{
			log.setText("Selección cancelada.");
		}
	}
}
