/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package paquete;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author pc
 */
public class Audio {
	private URL url2 = null; 
	public AudioClip clip2;
	public Audio(){
		try{
			url2 = new URL("file:///" + System.getProperty("user.dir") + "/src/res/waterrun.WAV");
		}catch (MalformedURLException e) { 
				e.printStackTrace();
		}
		this.clip2 = Applet.newAudioClip(getUrl());//getURL("/src/res/SOUNDER.WAV")
	}
	
	public URL getUrl(){
		return url2;
	}
	
	public void reproduccion(){ // Intento que si el oponente se mueve, yo reproduzca sonido para tal movimiento
		clip2.loop();
	}
	
	public void detener(){
		clip2.stop();
	}
	

}
