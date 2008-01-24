
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

    public FileChooser() {
        super("Seleccion de Circuito");
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        //JButton openButton = new JButton("Abrir"/*, new ImageIcon("images/open.gif")*/);
        //openButton.addActionListener(new OpenListener());
		Boton1 openButton = new Boton1("Abrir",this);
		openButton.addActionListener(this);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(openButton); // boton de abrir
     
		log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        JScrollPane logScrollPane = new JScrollPane(log);

        Container contentPane = getContentPane();
        contentPane.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(logScrollPane, BorderLayout.CENTER);
		setResizable(false);
		pack();
		setVisible(true);
    }
	/*
	private class OpenListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			JFileChooser fc = new JFileChooser();
			fc.addChoosableFileFilter(new TextFilter());
		
			int returnVal = fc.showOpenDialog(FileChooser.this);
		
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//Aca se hace la apertura del archivo seleccionado
				log.append("Abriendo: " + file.getName() + "." + newline);
			}else{
				log.append("Comando cancelado por usuarior." + newline);
			}
        }
	}
	*/
	public void actionPerformed(ActionEvent e) {
		
		JFileChooser fc = new JFileChooser();
		fc.setBounds(new Rectangle(this.getX(),this.getY()));
		fc.addChoosableFileFilter(new TextFilter());
	
		int returnVal = fc.showOpenDialog(FileChooser.this);
	
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			//Aca se hace la apertura del archivo seleccionado
			log.append("Abriendo: " + file.getName() + "." + newline);
		}else{
			log.append("Comando cancelado por usuarior." + newline);
		}
	}
}
