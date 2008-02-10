package paquete;
import java.awt.Graphics2D;
import java.io.IOException;

// Clase que contiene en sí la información sobre los elementos que componen el circuito de juego.
import java.rmi.RemoteException;
import javax.swing.JOptionPane;
import presentacion.Conexion;
public class Circuito implements CircuitoControlable {
	private CargadorCircuitoTXT cargadorTXT; // Permite la conversión del archivo txt a circuito.
	private Bloque matrizDeBloques[][] = new Bloque [Finals.BLOQUES_NUM][Finals.BLOQUES_NUM]; // Matriz que contiene los elementos Bloque que son mapeados en la pantalla.
	private Meta metas[] = new Meta[2];
	private Conexion conexion;
	
	// Constructor de la clase.
	public Circuito(String nombreCircuitoTXT){
		cargadorTXT = new CargadorCircuitoTXT(nombreCircuitoTXT);
		Bloque bloque;
		// Es recorrida secuencialmente cada posición del circuito para determinar según el archivo txt, si crear o no un muro o una meta, allí.
		for (int j = 0; j < Finals.BLOQUES_NUM; j++) {
			for (int i = 0; i < Finals.BLOQUES_NUM; i++) {
				// En caso de estar en un borde, es creado automáticamente un muro.
				if ((j==0)||(i==0)||(i==Finals.BLOQUES_NUM-1)||(j==Finals.BLOQUES_NUM-1)){
					this.agregarBloque(i, j, new Muro(i,j));
				}else{
				// En caso de no estar en un borde, se procede a leer el archivo para obtener el elemento a ligar al circuito.
					try {
						bloque = cargadorTXT.getBloqueLeido(i,j); // El elemento a crear es leído para ser agregado al circuito.
						this.agregarBloque(i, j, bloque); // Es agregado el bloque leído al circuito.
						if (bloque instanceof Meta){ // Es verificada la posibilidad de que este bloque sea una meta, para vincularla con alguno de los atributos.
							metas[Math.abs(((Meta)bloque).getNumero()%2)] = (Meta)bloque ;
						}
					
					} catch (IOException ex) {
						System.out.println("Error de IO al leer el circuito.");
						ex.printStackTrace();
					}
				}
		
			}
		}
		try{
		if (!cargadorTXT.circuitoCorrecto()) // Es verificada la existencia de las dos metas, en caso de no estar se arroja una excepción.
			throw new Exception("Error: archivo de circuito incorrecto, faltan las metas número 1 y/o 2 en el mismo.");
		}catch(Exception e){
			e.printStackTrace();
		}
		cargadorTXT.cerrarArchivo();
	}
	
	// Método privado que añade un bloque dado al circuito (tanto a la matriz como al grupo de objetos a representar).
	private void agregarBloque(int i,int j, Bloque bloque){
		this.setBloqueEnMatriz(i, j, bloque); // Es agregado a la matriz el bloque dado (o nada en caso de ser un null).
	}
	
	public void setConexion(Conexion conexion){
		this.conexion = conexion;
	}
	
	public void pintar(Graphics2D g){
		for (int i = 0; i < Finals.BLOQUES_NUM; i++) {
			for (int j = 0; j < Finals.BLOQUES_NUM; j++) {
				if (matrizDeBloques[i][j]!=null){
					matrizDeBloques[i][j].pintar(g);
				}
			}
		}
	}
	
	// Método que brinda el bloque correspondiente a las coordenadas dadas.
	public Muro getMuro (int bloqueX, int bloqueY){
		if (this.getBloqueEnMatriz(bloqueX, bloqueY) instanceof Muro){
			return (Muro)this.getBloqueEnMatriz(bloqueX, bloqueY);
		}else{
			return (Muro)null;
		}
	}
	
	// Metodo que indica si el tanque dado ha llegado a su objetivo (meta del oponente).
	public boolean llegueAMiMeta(Tanque tanque){
		Meta meta = metas[Math.abs((tanque.getID()+1)%2)];
		if (((tanque.getX() > (meta.getX() - 20))&&(tanque.getX() < (meta.getX() + 20)))&&((tanque.getY() > (meta.getY() - 20))&&(tanque.getY() < (meta.getY() + 20)))){
			//JOptionPane
			
			Runnable hilitoMensajePropio = new Runnable(){
				public void run(){
					Partida.getPartida().finalizar();
					JOptionPane.showMessageDialog(null, "Fin del juego. Usted ha ganado!!!");
				}
			};
			(new Thread(hilitoMensajePropio, "Hilo de mensaje de llegada a la meta (propio)")).start();
			
			conexion.partidaPerdida();
			
			return true;
		}
		return false;
	}
	
	
	public void oponenteLlego() throws RemoteException{
		Partida.getPartida().finalizar();
		JOptionPane.showMessageDialog(null, "Fin del juego. Usted ha perdido...");
	}
	
	// Método que indica mediante un booleano si ha existido una colisión con los muros del circuito, por parte del tanque indicado como parámetro.
	// También ejecuta: el efecto de deterioro del muro correspondiente (en caso de colisión) y la corrección de la posición del tanque.
	public boolean hayColision(Tanque tanque){
		boolean hayChoque = false; // Variable booleana que indica la existencia o no de choque con al menos un muro.
		int bloqueX = tanque.getX()/Finals.BLOQUE_LADO_LONG; // Cálculo de la coordenada (en matriz) del bloque superior izquierdo a analizar (coordenada X).
		int bloqueY = tanque.getY() / Finals.BLOQUE_LADO_LONG; // Cálculo de la coordenada del bloque superior izquierdo a analizar (coordenada Y).
		boolean bloqueXrebase = (tanque.getX()%Finals.BLOQUE_LADO_LONG)>0; // Variable que indica posibilidad de múltiple colisión (coordenada X).
		boolean bloqueYrebase = (tanque.getY() % Finals.BLOQUE_LADO_LONG)>0; // Ídem (coordenada Y). Será true si el tanque estará entre dos cuadrantes horizontalmente.
								
		if (hayMuro(bloqueX, bloqueY)){
			hayChoque=true; // En caso de haber muro solapado en la parte superior izquierda del tanque, hay choque.
			this.getMuro(bloqueX, bloqueY).deterioro(tanque.getVelocidad()); // Se provoca en el muro indicado un deterioro.
			conexion.choqueNuevoCircuitoLocal(bloqueX, bloqueY, tanque.getVelocidad());
		}
		
		if (bloqueXrebase && hayMuro(bloqueX+1,bloqueY)){
			hayChoque = true; // Si hay rebase en X (tanque más a la derecha de un "encaje en bloque horizontal") hay choque.
			this.getMuro(bloqueX+1, bloqueY).deterioro(tanque.getVelocidad());
			conexion.choqueNuevoCircuitoLocal(bloqueX+1, bloqueY, tanque.getVelocidad());
		}
		if (bloqueYrebase && hayMuro(bloqueX,bloqueY+1)){
			hayChoque = true; // Si hay un rebase en Y (tanque más abajo de un "encaje en bloque vertical") hay choque.
			this.getMuro(bloqueX, bloqueY+1).deterioro(tanque.getVelocidad()); // También hay deterioro, pero en este muro.
			conexion.choqueNuevoCircuitoLocal(bloqueX, bloqueY+1, tanque.getVelocidad());
		}
		if (bloqueYrebase && bloqueXrebase && hayMuro(bloqueX+1,bloqueY+1)){
			hayChoque = true; // Si hay ambos tipos de rebase, y se tiene un muro en el cuadrante inferior derecho al del tanque, hay choque.
			this.getMuro(bloqueX+1, bloqueY+1).deterioro(tanque.getVelocidad()); // Se deteriora el muro.
			conexion.choqueNuevoCircuitoLocal(bloqueX+1, bloqueY+1, tanque.getVelocidad());
		}
		
		// Corrección de la posición del tanque involucrado.
		switch (tanque.getDireccion()){
			// Según la dirección del tanque, este es llevado hacia atrás hasta la condición de no solapamiento.
			case Finals.ABAJO:		while(this.solapamiento(tanque)){tanque.setY(tanque.getY()-1);}
			case Finals.ARRIBA:		while(this.solapamiento(tanque)){tanque.setY(tanque.getY()+1);}
			case Finals.IZQUIERDA:	while(this.solapamiento(tanque)){tanque.setX(tanque.getX()+1);}
			case Finals.DERECHA:	while(this.solapamiento(tanque)){tanque.setX(tanque.getX()-1);}												
		}			
		return hayChoque; // Es retornado un booleano que indica la existencia o no de un choque.
	}
	
	
	// Método que indica mediante un booleano si ha existido un solapamiento con los muros del circuito, por parte del tanque indicado como parámetro.
	private boolean solapamiento(Tanque tanque){
		boolean hayChoque = false; // Variable booleana que indica la existencia o no de choque con al menos un muro.
		int bloqueX = tanque.getX()/Finals.BLOQUE_LADO_LONG; // Cálculo de la coordenada (en matriz) del bloque superior izquierdo a analizar (coordenada X).
		int bloqueY = tanque.getY() / Finals.BLOQUE_LADO_LONG; // Cálculo de la coordenada del bloque superior izquierdo a analizar (coordenada Y).
		boolean bloqueXrebase = (tanque.getX()%Finals.BLOQUE_LADO_LONG)>0; // Variable que indica posibilidad de múltiple colisión (coordenada X).
		boolean bloqueYrebase = (tanque.getY() % Finals.BLOQUE_LADO_LONG)>0; // Ídem (coordenada Y). Será true si el tanque estará entre dos cuadrantes horizontalmente.
								
		if ((hayMuro(bloqueX, bloqueY))||(bloqueXrebase && hayMuro(bloqueX+1,bloqueY))|| (bloqueYrebase && hayMuro(bloqueX,bloqueY+1))||(bloqueYrebase && bloqueXrebase && hayMuro(bloqueX+1,bloqueY+1))){
			hayChoque=true; // En caso de haber muro solapado en la parte superior izquierda del tanque, hay choque.
		}	
		return hayChoque; // Es retornado un booleano que indica la existencia o no de un choque.
	}
	
	// Método que indica la existencia o no de un muro en las coordenadas indicadas del circuito.
	private boolean hayMuro(int x, int y){
		if(this.getBloqueEnMatriz(x, y) instanceof Muro){
			return true;
		}else{
			return false;
		}
	}
	
	// Método que retorna el objeto de la clase Meta solicitado.
	public Meta getMeta(int numero){
		return metas[Math.abs(numero%2)];
	}
	
	// Método que retorna el bloque indicado por sus coordenadas.
	private Bloque getBloqueEnMatriz(int bloqueX, int bloqueY){
		return matrizDeBloques[Math.abs(bloqueX%matrizDeBloques.length)][Math.abs(bloqueY%matrizDeBloques.length)];
	}
	
	// Método que asocia la matriz con un bloque.
	private void setBloqueEnMatriz(int bloqueX, int bloqueY, Bloque bloque){
		matrizDeBloques[Math.abs(bloqueX%matrizDeBloques.length)][Math.abs(bloqueY%matrizDeBloques.length)] = bloque;
	}

	public void informarChoque(int parametrosDelChoque[]) throws RemoteException {
		this.getMuro(parametrosDelChoque[0], parametrosDelChoque[1]).deterioro(parametrosDelChoque[2]);
	}
}	