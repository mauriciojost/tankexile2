package paquete;

import java.awt.Graphics2D;
// Clase cuyos objetos son las unidades de conformación del circuito de juego.
public abstract class Bloque{
	
	private int x, y; //Atributos que representan las coordenadas reales (píxeles) del bloque.
		
	public Bloque(int bx, int by) {

		x = bx * Finals.BLOQUE_LADO_LONG; // Cálculo de las coordenadas reales del bloque.
		y = by * Finals.BLOQUE_LADO_LONG;
	}
	
	// Método que brinda la imagen a mostrar para este bloque. Abstracto.
	public abstract void paint(Graphics2D g);
	
	// Métodos que permiten obtener la posición real (en pixeles desde la esquina superior izquierda) del bloque.
	public int getX(){return x;}
	public int getY(){return y;}

	
}
