package paquete;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;


// Clase utilizada para la reproducción de sonido durante la partida.
public class Audio {
	private URL url = null; 
	public AudioClip clip;
	public Audio(String ubicacion){
		try{
			url = getClass().getClassLoader().getResource(ubicacion);
		}catch (Exception e) { 
			System.out.println("Error en la carga de archivos de audio.");
			e.printStackTrace();
		}
		this.clip = Applet.newAudioClip(getUrl());
	}
	
	public URL getUrl(){
		return url;
	}
	
	public void reproduccionLoop(){ // Intento de que si el oponente se mueve, se reproduzca sonido para tal movimiento.
		clip.loop();
	}
	
	public void reproduccionSimple(){
		clip.play();
	}
	
	public void detener(){
		clip.stop();
	}

}
