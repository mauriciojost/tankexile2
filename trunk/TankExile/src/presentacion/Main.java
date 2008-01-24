package presentacion;
import presentacion.Presentacion1;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RMISecurityManager;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.JFrame;



public class Main {
	private Main(){}
	
	public static void main(String[] args) {
		//
		/*
		String host = (args.length < 1) ? null : args[0];
		try {
			Registry registry = LocateRegistry.getRegistry(4050);
			
			Controlable stub = (Controlable) registry.lookup("Clave de búsqueda");
			
			System.out.println("Esperando respuesta...");
			
			stub.irIzquierda();
			
		} catch (Exception e) {
			System.err.println("Excepción de cliente: " + e.toString());
			e.printStackTrace();
		}
		//*/
		///////////////////////////////////////////////////////
		
		//
		new Presentacion1(350,70);
		
		 ////////////////////////////////////////////////////////
		//
		/*FileChooser frame = new FileChooser();		

        frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {System.exit(0);}
			});

        frame.pack();
        frame.setVisible(true);
		//*/

    }
}
