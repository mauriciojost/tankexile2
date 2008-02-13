package paquete;


import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface relacionada al control de un tanque remotamente.
public interface Controlable extends Remote {
	// Conjunto de métodos que se exige tenga un objeto controlable, sea por teclado o por comando remoto.
	public void setTodo(int x, int y, int direccion, int movimientoDeTrama, int tramaChoque, boolean moviendose)throws RemoteException;
	public String getNickOponente()throws RemoteException;	
	public void setMoviendose(boolean estaMoviendose) throws RemoteException;
	public void choqueResumido() throws RemoteException;
}
