package paquete;

import java.awt.image.ImageObserver;

public interface Finals  extends ImageObserver{	
	public static final int PERIODO=10;
	public static final int PERIODO_DE_TURNO=3000;
	public static final int BLOQUES_NUM=32;
	public static final int BLOQUE_LADO_LONG = 20;
	public static final int ANCHO_VENTANA=BLOQUES_NUM*BLOQUE_LADO_LONG+6;
	public static final int ALTO_VENTANA=BLOQUES_NUM*BLOQUE_LADO_LONG+32;
	
	public static final int ABAJO =		0;
	public static final int IZQUIERDA =	1;
	public static final int DERECHA =	2;
	public static final int ARRIBA =	3;
}
