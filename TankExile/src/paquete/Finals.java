package paquete;

import java.awt.Color;

public interface Finals{	

	public static final int PERIODO =			4;
	public static final int PERIODO_BOLA =		4;
	public static final int PERIODO_DE_TURNO =	3000;
	public static final int BLOQUES_NUM =		32;
	public static final int BLOQUE_LADO_LONG =	20;
	public static final int CANTIDAD_DE_INTENTOS_DE_CONEXION = 3;
	public static final int ANCHO_VENTANA = BLOQUES_NUM * BLOQUE_LADO_LONG;// +4;
	public static final int ALTO_VENTANA = BLOQUES_NUM * BLOQUE_LADO_LONG;// +32;
	
	public static final int ABAJO =				0;
	public static final int IZQUIERDA =			1;
	public static final int DERECHA =			2;
	public static final int ARRIBA =			3;
	
	public static final int ESPERA_CONEXION =	100;
	
	
	public static final Color colorGris = Color.lightGray;
	public static final Color colorBlanco = Color.white;
	public static final Color COLOR_LEYENDAS_PARTIDA = Color.black;
}
