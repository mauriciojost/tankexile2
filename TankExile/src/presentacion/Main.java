package presentacion;

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
    }
}
