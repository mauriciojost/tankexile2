package paquete;

import java.awt.Rectangle;

public interface ElementoDeJuego {
	public void eventoChoque(ElementoDeJuego contraQuien);
	public Rectangle getBounds();
}
