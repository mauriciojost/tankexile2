package paquete;
import java.awt.*;
import java.io.IOException;

// Clase que contiene en sí la información sobre los elementos que componen el circuito de juego.
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import presentacion.Conexion;
import presentacion.PrePartida;

public class Circuito implements CircuitoControlable, Serializable, Imitable{
	private transient static Circuito circuito;
	private transient CargadorCircuitoTXT cargadorTXT; // Permite la conversión del archivo txt a circuito.
	private transient ArrayList<Bloque> bloques = new ArrayList<Bloque>();
	private transient ArrayList<Bola> bolas = new ArrayList<Bola>();
	private transient ArrayList<Bloque> elementosChocados = new ArrayList<Bloque>(); // Colección usada para mantener los elementos chocados, sin perder algún evento.
	private transient Meta metas[] = new Meta[2];
	private transient Tanque tanqueLocal;
	private transient Tanque tanqueOponente;
	private String nickPropio;
	private ArrayList<int[]> choquesPendientesCircuitoRemoto = new ArrayList<int[]>();
	
	
	// Constructor de la clase.
	public Circuito(String nombreCircuitoTXT){
		circuito = this;
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
					if ((i==Finals.BLOQUES_NUM/2)&&(j==Finals.BLOQUES_NUM/2)&&(!(bloque instanceof Meta))){
							bloque = null;
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
	
	// Método que realiza la llamada de pintado de cada elemento constitutivo del circuito (bloques).
	public void pintar(Graphics2D g){
		Iterator iterador = bloques.iterator();
		while(iterador.hasNext()){
			((Bloque)iterador.next()).pintar(g);
		}
	}
	
	// Método llamado remotamente para indicar que el jugador remoto ya ha llegado a su meta.
	public void oponenteLlego() throws RemoteException{
		(new Thread(
			new Runnable(){
				public void run(){
					String mensaje = "Fin del juego. Usted ha perdido.";
					for (int i=0; i<10;i++){
						PrePartida.getPrePartida().setEstado("", Font.BOLD);
						try{Thread.sleep(300);}catch(Exception e){e.printStackTrace();}
						PrePartida.getPrePartida().setEstado(mensaje, Font.BOLD);
						try{Thread.sleep(300);}catch(Exception e){e.printStackTrace();}				
					}
				}
			}
		)).start();
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
		for(int i=0; i<bloques.size();i++){
			Bloque bloque = (Bloque)bloques.get(i);
			if (tanqueLocal.getBounds().intersects(bloque.getBounds())){
				elementosChocados.add(bloque);
			}	
		}
		
		Iterator iterador = elementosChocados.iterator();
		while(iterador.hasNext()){
			Bloque bloque = (Bloque)iterador.next();
			bloque.eventoChoque(tanqueLocal);
			tanqueLocal.eventoChoque(bloque);
			iterador.remove();
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
			if (tanqueLocal.getBounds().intersects(bola.getBounds())){
				tanqueLocal.eventoChoque(bola);
				bola.eventoChoque(tanqueLocal);
			}	
		}
		
		if (tanqueLocal.getBounds().intersects(tanqueOponente.getBounds())){
			tanqueLocal.eventoChoque(tanqueOponente);
			tanqueOponente.eventoChoque(tanqueLocal);
		}
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

	public void setNickOponente(String n){
		nickPropio = n;
	}
	public String getNickOponente() throws RemoteException {
		return nickPropio;
	}
	
	public ArrayList<int []> getChoquesPendientes(){
		ArrayList<int[]> retorno = choquesPendientesCircuitoRemoto;
		choquesPendientesCircuitoRemoto = new ArrayList<int[]>();
		return retorno;
	}
	
	public void imitar(Imitable circuito){
		
		Iterator<int[]> iterador = null;
		try{
			Object[] array = (Object[])circuito.getParametros();
			ArrayList<int[]> unElem = (ArrayList<int[]>) array[0];
			iterador = unElem.iterator();
			//iterador = ((ArrayList<int[]>)(((Object[])circuito.getParametros())[0])).iterator();
			//iterador = circuitoAImitar.getChoquesPendientes().iterator();
		}catch(Exception e){e.printStackTrace();}
		
		try {
			while(iterador.hasNext()){
				this.informarChoque(iterador.next());
			}
			
		} catch (RemoteException ex) {
			System.out.println("Error al imitar al circuito remoto. El oponente ha finalizado la sesión.");
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión.");
			System.exit(0);
		}
	}
	
	public void choqueNuevoCircuitoLocal(int indice, int magnitudDelChoque){
		int choque[] = {indice,magnitudDelChoque};
		this.choquesPendientesCircuitoRemoto.add(choque);
	}
	
	public void manejarCircuitoRemoto(){
		
	}
	
	public static Circuito getCircuito(){
		return circuito;
	}

	public Object[] getParametros() throws RemoteException {
		Object[] arreglo = {this.getChoquesPendientes()};
		return arreglo;
	}
	
}	