package paquete;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

// Clase utilizada para la reproducción de sonido durante la partida.
public class Audio{
	private URL url = null; 
	private AudioClip clip;
	private boolean yaReproducioneLoop = false;
	
	public Audio(String ubicacion){
		try{
			url = getClass().getClassLoader().getResource(ubicacion);
		}catch (Exception e) { 
			System.out.println("Error en la carga de archivos de audio.");
			e.printStackTrace();
		}
		this.clip = Applet.newAudioClip(url);
		this.detener();
		
	}
	
	public void reproduccionLoop(){ // Intento de que si el oponente se mueve, se reproduzca sonido para tal movimiento.
		if (!this.yaReproducioneLoop){
			clip.loop();
			
			this.yaReproducioneLoop = true;
		}
	}
	
	public void reproduccionSimple(){
		clip.play();
	}
	
	public void detener(){
		clip.stop();
		this.yaReproducioneLoop = false;
	}
	public void detenerTotal(){
		(new Thread(
			new Runnable(){
				public void run(){
					try{Thread.sleep(2000);}catch(Exception e){}
					clip.stop();
				}
			}
		)).start();
		clip.stop();
		this.yaReproducioneLoop = true;
	}
}
