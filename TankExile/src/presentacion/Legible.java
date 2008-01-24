
package presentacion;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Interfaz utilizada por la Clase Conexion para hacer uso de los métodos provistos para la manipulación de archivos de circuito.
public interface Legible extends Remote {
	// Método de lectura, indicado el nombre completo del archivo (sin dirección, relativa de dónde está el jar) retorna un String con su contenido.
	public String leer(String archivo) throws RemoteException, IOException;
	// Método que realiza la copia de un archivo remoto hacia el host local.
	public void copiarDeHostRemoto(String archivoOrigenRemoto, String archivoDestinoLocal) throws IOException; 
}
