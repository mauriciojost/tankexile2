package paquete;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;

// Clase que contiene en sí la información sobre los elementos que componen el circuito de juego.
public class Circuito implements CircuitoControlable, Serializable, Imitable{
	private transient static Circuito circuito;
	private transient CargadorCircuitoTXT cargadorTXT; // Permite la conversión del archivo txt a circuito.
	private transient ArrayList<Bloque> bloques = new ArrayList<Bloque>();
	private transient ArrayList<Bola> bolas = new ArrayList<Bola>();
	private transient ArrayList<Bloque> elementosChocados = new ArrayList<Bloque>(); // Colección usada para mantener los elementos chocados, sin perder algún evento.
	private transient Meta metas[] = new Meta[2];
	private transient Tanque tanqueLocal;
	private transient Tanque tanqueOponente;
	
	private ArrayList<Imitable> elementosAImitar = new ArrayList<Imitable>();
	
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
						System.out.println("Error de IO al leer el circuito."); ex.printStackTrace(); System.exit(-1);
					}
					if ((i==Finals.BLOQUES_NUM/2)&&(j==Finals.BLOQUES_NUM/2)&&(!(bloque instanceof Meta))){bloque = null;}
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
			//System.out.println(bloque.getClass().getSimpleName() +":"+ bloque.getIndice());
		}
	}
	
	// Método que realiza la llamada de pintado de cada elemento constitutivo del circuito (bloques).
	public void pintar(Graphics2D g){
		Iterator iterador = bloques.iterator();
		while(iterador.hasNext()){
			((Bloque)iterador.next()).pintar(g);
		}
	}
	
	public void actuar(){
		for(int i=0; i<bloques.size();i++){
			Bloque bloque = (Bloque)bloques.get(i);
			if (tanqueLocal.getBounds().intersects(bloque.getBounds()))
				elementosChocados.add(bloque);
		}
		
		Iterator iterador = elementosChocados.iterator();
		while(iterador.hasNext()){
			Bloque bloque = (Bloque)iterador.next();
			bloque.eventoChoque(tanqueLocal);
			tanqueLocal.eventoChoque(bloque);
			this.elementosAImitar.add(bloque);
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
		
		for(int i=0; i<bolas.size();i++){
			Bola bola = (Bola)bolas.get(i);
			if (tanqueOponente.getBounds().intersects(bola.getBounds())){
				tanqueOponente.eventoChoque(bola);
				bola.eventoChoque(tanqueOponente);
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
	
	public void imitar(Imitable circuito){
		Object[] array = null;
		try{
			array = (Object[])circuito.getParametros();
		}catch(Exception e){e.printStackTrace();}
		
		ArrayList<Imitable> listaImitables = (ArrayList<Imitable>) array[0];
		Iterator<Imitable> iterador = listaImitables.iterator();
		
		try {
			while(iterador.hasNext()){
				Bloque bloque = (Bloque)iterador.next();
				this.bloques.get(bloque.getIndice()).imitar(bloque);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "El oponente abandono conexión.");
			System.exit(0);
		}
	}
	
	public static Circuito getCircuito(){
		return circuito;
	}

	public Object[] getParametros() throws RemoteException {
		Object[] arreglo = {this.elementosAImitar};
		elementosAImitar = new ArrayList<Imitable>();
		return arreglo;
	}
}	