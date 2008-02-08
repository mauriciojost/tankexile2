/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package presentacion;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Mauricio
 */
public interface VentanaControlable extends Remote {
	public void metodoDeControl() throws RemoteException;
	public void setSeleccionHabilitada(boolean habilitada) throws RemoteException;
}
