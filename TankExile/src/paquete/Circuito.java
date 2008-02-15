package paquete;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;

// Clase que contiene en sí la información sobre los elementos que componen el circuito de juego.
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import presentacion.Conexion;
import presentacion.PrePartida;
public class Circuito implements CircuitoControlable {
	private CargadorCircuitoTXT cargadorTXT; // Permite la conversión del archivo txt a circuito.
	//private Bloque matrizDeBloques[][] = new Bloque [Finals.BLOQUES_NUM][Finals.BLOQUES_NUM]; // Matriz que contiene los elementos Bloque que son mapeados en la pantalla.
	private ArrayList<Bloque> bloques = new ArrayList<Bloque>();
	private ArrayList<Bola> bolas = new ArrayList<Bola>();
	
	private Meta metas[] = new Meta[2];
	private Conexion conexion;
	private Tanque tanqueLocal;
	private Tanque tanqueOponente;
	
	// Constructor de la clase.
	public Circuito(String nombreCircuitoTXT){
		
		cargadorTXT = new CargadorCircuitoTXT(nombreCircuitoTXT);
		Bloque bloque=null;
		// Es recorrida secuencialmente cada posición del circuito para determinar según el archivo txt, si crear o no un muro o una meta, allí.
		for (int j = 0; j < Finals.BLOQUES_NUM; j++) {
			for (int i = 0; i < Finals.BLOQUES_NUM; i++) {
				// En caso de estar en un borde, es creado automáticamente un muro.
				if ((j==0)||(i==0)||(i==Finals.BLOQUES_NUM-1)||(j==Finals.BLOQUES_NUM-1)){
					bloque = (new Muro(i,j));
				}else{
					// En caso de no estar en un borde, se procede a leer el archivo para obtener el elemento a ligar al circuito.
					try {
						bloque = cargadorTXT.getBloqueLeido(i,j); // El elemento a crear es leído para ser agregado al circuito.
						
					} catch (IOException ex) {
						System.out.println("Error de IO al leer el circuito.");
						ex.printStackTrace();
						System.exit(-1);
					}
				}
				this.agregarBloque(bloque); // Es agregado el bloque leído al circuito.
				
			}
		}
		
		if (!cargadorTXT.circuitoCorrecto()){ // Es verificada la existencia de las dos metas, en caso de no estar se arroja una excepción.
			System.out.println("Error: archivo de circuito incorrecto, faltan las metas número 1 y/o 2 en el mismo.");
			System.exit(-1);
		}
		
		cargadorTXT.cerrarArchivo();
	}
	
	public void agregarBola(Bola bola){
		bolas.add(bola);
	}
	public void setTanques(Tanque tanqueLocal, Tanque tanqueOponente){
		this.tanqueLocal = tanqueLocal;
		this.tanqueOponente = tanqueOponente;
		
	}
	
	
	// Método privado que añade un bloque dado al circuito (tanto a la matriz como al grupo de objetos a representar).
	private void agregarBloque(Bloque bloque){
		if (bloque!=null){
			bloques.add(bloque);
			if (bloque instanceof Meta){ // Es verificada la posibilidad de que este bloque sea una meta, para vincularla con alguno de los atributos.
				metas[Math.abs(((Meta)bloque).getNumero()%2)] = (Meta)bloque ;
			}
			bloque.setIndice(bloques.indexOf(bloque));
		}
	}
	
	public void setConexion(Conexion conexion){
		this.conexion = conexion;
	}
	public Conexion getConexion(){
		return conexion;
	}
	
	// Método que realiza la llamada de pintado de cada elemento constitutivo del circuito (bloques).
	public void pintar(Graphics2D g){
		Iterator iterador = bloques.iterator();
		while(iterador.hasNext()){
			((Bloque)iterador.next()).pintar(g);
		}
	}
	
	// Método llamado remotamente para indicar que el jugador remoto ya ha llegado a su meta.
	public void oponenteLlego() throws RemoteException{
		PrePartida.getPrePartida().setEstado(" Fin del juego. Usted ha perdido.", Font.BOLD);
		PrePartida.getPrePartida().setVisible(true);
		Partida.getPartida().finalizar();
		//JOptionPane.showMessageDialog(null, "Fin del juego. Usted ha perdido...");
	}
	
	// Método que mantiene la coherencia entre el circuito y su tanque local.
	// También ejecuta: el efecto de deterioro del muro correspondiente (en caso de colisión) y la corrección de la posición del tanque.
	// Además indica al circuito remoto la existencia de choques.
	// Indica además la llegada a la meta.
	// Método que se encarga de mantener la coherencia entre el circuito y su tanque local.
	public void actuar(){
		//boolean hayChoque = false; // Variable booleana que indica la existencia o no de choque con al menos un muro.
		//Meta miMeta = metas[Math.abs((tanqueLocal.getID()+1)%2)];
		Rectangle tanqueRec = tanqueLocal.getBounds();
		Rectangle tanqueRec2 = this.tanqueOponente.getBounds();
		
		for(int i=0; i<bloques.size();i++){
			Bloque bloque = (Bloque)bloques.get(i);
			if (tanqueRec.intersects(bloque.getBounds())){
				tanqueLocal.eventoChoque(bloque);
				bloque.eventoChoque(tanqueLocal);
			}	
		}
		for (int j=0; j<bolas.size();j++){
			Bola bola = (Bola)bolas.get(j);
			for(int i=0; i<bloques.size();i++){
				Bloque bloque = (Bloque)bloques.get(i);
				if (bola.getBounds().intersects(bloque.getBounds())){
					bola.eventoChoque(bloque);
					bloque.eventoChoque(bola);
				}	
			}
		}
		
		for(int i=0; i<bolas.size();i++){
			Bola bola = (Bola)bolas.get(i);
			if (tanqueRec.intersects(bola.getBounds())){
				tanqueLocal.eventoChoque(bola);
				bola.eventoChoque(tanqueLocal);
			}	
		}
		if (tanqueRec.intersects(tanqueRec2)){
			tanqueLocal.eventoChoque(tanqueOponente);
			tanqueOponente.eventoChoque(tanqueLocal);
		}
		
		/* ANTES DE POLIMORFISMO
		for(int i=0; i<bloques.size();i++){
			Bloque bloque = (Bloque)bloques.get(i);
			if (tanqueRec.intersects(bloque.getBounds())){
				if(bloque instanceof Muro){
					((Muro)bloque).deterioro(tanque.getVelocidad()); // Se provoca en el muro indicado un deterioro.
					conexion.choqueNuevoCircuitoLocal(i, tanque.getVelocidad());
					hayChoque = true;
				}
				else if (bloque instanceof Meta){
					if (miMeta.equals(bloque)){
						PrePartida.getPrePartida().setEstado("Fin del juego. Usted ha ganado...", Font.BOLD);
						PrePartida.getPrePartida().setVisible(true);
						conexion.partidaPerdida();
						Partida.getPartida().finalizar();
					}
				}
			}
		}
		*/
		/*
		if (hayChoque){
			// Corrección de la posición del tanque involucrado.
			switch (tanqueLocal.getDireccion()){
				// Según la dirección del tanque, este es llevado hacia atrás hasta la condición de no solapamiento.
				case Finals.ABAJO:		while(this.solapamiento(tanqueLocal)){tanqueLocal.setY(tanqueLocal.getY()-Tanque.U_VELOCIDAD);}
				case Finals.ARRIBA:		while(this.solapamiento(tanqueLocal)){tanqueLocal.setY(tanqueLocal.getY()+Tanque.U_VELOCIDAD);}
				case Finals.IZQUIERDA:	while(this.solapamiento(tanqueLocal)){tanqueLocal.setX(tanqueLocal.getX()+Tanque.U_VELOCIDAD);}
				case Finals.DERECHA:	while(this.solapamiento(tanqueLocal)){tanqueLocal.setX(tanqueLocal.getX()-Tanque.U_VELOCIDAD);}
			}
			
			tanqueLocal.choque(false);
			
		}*/
	}
	
	// Método que indica mediante un booleano si ha existido un solapamiento con los muros del circuito, por parte del tanque indicado como parámetro.
	private boolean solapamiento(Tanque tanque){
		Iterator iterador = bloques.iterator();
		Rectangle tanqueRec = tanque.getBounds();
		while(iterador.hasNext()){
			if (tanqueRec.intersects(((Bloque)iterador.next()).getBounds())){
				return true;
			}
		}
		return false;
	}
	
	// Método que retorna el objeto de la clase Meta solicitado.
	public Meta getMeta(int numero){
		return metas[Math.abs(numero%2)];
	}
	
	// Método llamado remotamente, que indica un nuevo choque para representar en los muros, con sus parámetros como un array.
	public void informarChoque(int parametrosDelChoque[]) throws RemoteException {
		// El parámetro contiene en su primer elemento el índice relacionado al muro chocado. En su segundo elemento, la magnitud del choque.
		((Muro)bloques.get((int)parametrosDelChoque[0])).deterioro((int)parametrosDelChoque[1]);
	}
}	