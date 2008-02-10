package paquete;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class Audio {
	private URL url = null; 
	public AudioClip clip;
	public Audio(String ubicacion){
		try{
			//url = new URL("file:///" + System.getProperty("user.dir") + ubicacion);
			url = getClass().getClassLoader().getResource(ubicacion);
			System.out.println("Sonido URL:"+url);
		}catch (Exception e) { 
			System.out.println("Error en la carga de archivos de audio.");
			e.printStackTrace();
		}
		
		this.clip = Applet.newAudioClip(getUrl());//getURL("/src/res/SOUNDER.WAV")
	}
	
	public URL getUrl(){
		return url;
	}
	
	public void reproduccionLoop(){ // Intento que si el oponente se mueve, yo reproduzca sonido para tal movimiento
		clip.loop();
	}
	
	public void reproduccionSimple(){
		clip.play();
	}
	
	public void detener(){
		clip.stop();
	}

}
