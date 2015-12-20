package presentacion;

import java.awt.Font;
import java.awt.Toolkit;
import java.io.*;
import java.nio.channels.FileChannel;
import java.rmi.RemoteException;
import paquete.Partida;
import presentacion.Conexion;
import presentacion.Copiador;
import presentacion.Escenografia;
import presentacion.VentanaControlable;

public class PrePartida extends javax.swing.JFrame implements VentanaControlable {
	private static PrePartida prePartida;
	private static VentanaControlable ventanaRemota;
	private boolean sonidoPrePartida = false;
	private String nickPropio = "";
	private final String NOMBRE_CIRCUITO_TEMPORAL = "temporal.tmp";
	private File circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);
	
	public static PrePartida getPrePartida(){
		if (prePartida == null){
			prePartida = new PrePartida(); // En caso de no existir instancia, la crea.
		}
		return prePartida;
	}

	public void setVentanaRemota(VentanaControlable ventanaRemota){
		PrePartida.ventanaRemota = ventanaRemota;
	}
		
	public void setSonidoHabilitado(boolean s){
		sonidoPrePartida = s;
	}
	
	public boolean getSonidoHabilitado(){
		return sonidoPrePartida;		
	}
	
	public void setNickPropio(String nick){
		nickPropio = nick;
	}
	
	public String getNickPropio(){
		return nickPropio;
	}
	
	// Método que muestra mensaje de estado de la aplicación.
	public void setEstado(String noticia, int estilo){
		etiquetaEstado.setFont(new Font("Arial",estilo,12));
		etiquetaEstado.setText(noticia); 
	}
	
	private PrePartida() {
		initComponents();
		circuitoSeleccionado.deleteOnExit();
		this.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width-this.getSize().width)/2, (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height)/2);
		this.setVisible(true);
		botonElegir.setEnabled(Conexion.getConexion().getID()==1);
	}
	
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        imagenTankExile = new javax.swing.JLabel();
        separadorEstado = new javax.swing.JSeparator();
        etiquetaEstado = new javax.swing.JLabel();
        botonInicio = new javax.swing.JButton();
        botonElegir = new javax.swing.JButton();
        botonOpciones = new javax.swing.JButton();
        botonSalida = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TankExile - PrePartida");
        setResizable(false);

        imagenTankExile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        imagenTankExile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/tank.JPG"))); // NOI18N
        imagenTankExile.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        imagenTankExile.setFocusable(false);

        etiquetaEstado.setText("Etiqueta de estado");

        botonInicio.setText("Inicio");
        botonInicio.setEnabled(false);
        botonInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonInicioActionPerformed(evt);
            }
        });

        botonElegir.setText("Elegir circuito");
        botonElegir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonElegirActionPerformed(evt);
            }
        });

        botonOpciones.setText("Opciones");
        botonOpciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonOpcionesActionPerformed(evt);
            }
        });

        botonSalida.setText("Salir");
        botonSalida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSalidaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imagenTankExile, javax.swing.GroupLayout.Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
                    .addComponent(botonInicio, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(botonElegir, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(botonOpciones, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(botonSalida, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE)
                    .addComponent(etiquetaEstado, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(separadorEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imagenTankExile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonElegir, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonOpciones, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonSalida, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separadorEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etiquetaEstado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void botonElegirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonElegirActionPerformed
		Escenografia.getEscenografia().setVisible(true);
		this.dispose();
}//GEN-LAST:event_botonElegirActionPerformed

	private void botonInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonInicioActionPerformed
		//if (!botonInicio.isEnabled()) return;
		// Quien tiene el ID = 1, es quien selecciona circuito y por tanto, quien indica cuando se habilita el botón Inicio en el host remoto.
		if (Conexion.getConexion().getID()==1)
			try{ventanaRemota.setInicioHabilitado(true);}catch(Exception e){e.printStackTrace();}
		this.dispose();
		Partida partida = new Partida(this.circuitoSeleccionado.getPath(), this);
		partida.jugar();
		this.dispose();
	}//GEN-LAST:event_botonInicioActionPerformed

	private void botonOpcionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonOpcionesActionPerformed
		//configurar sonido y nombre del jugador
		this.dispose();
		//Configurador.getConfigurador().setVisible(true);
		presentacion.Configuracion.getConfiguracion().setVisible(true);
	}//GEN-LAST:event_botonOpcionesActionPerformed

	private void botonSalidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSalidaActionPerformed
		System.exit(0);
	}//GEN-LAST:event_botonSalidaActionPerformed
	
	// Método invocado cuando se ha validado el circuito seleccionado.
	public void setCircuitoSeleccionado(File circuitoSeleccionado){
		
		try {
			Copiador.getCopiador().enviarAHostRemoto(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);				
			try {copiarArchivo(circuitoSeleccionado.getPath(), NOMBRE_CIRCUITO_TEMPORAL);}catch(Exception e){e.printStackTrace();}
		} catch (IOException ex) {
			System.err.println("Error al intentar copiar en el método de Conexion copiarDeHostRemoto.");
		}	
			
		botonInicio.setEnabled(true); // Se habilita botón Inicio luego de haber seleccionado circuito. Se puede iniciar el juego.
				
		this.circuitoSeleccionado = new File(NOMBRE_CIRCUITO_TEMPORAL);	
		this.circuitoSeleccionado.deleteOnExit();
	}
	
	private void copiarArchivo(String origen, String destino) throws Exception{
		FileChannel ic = new FileInputStream(origen).getChannel();
		FileChannel oc = new FileOutputStream(destino).getChannel();
		ic.transferTo(0, ic.size(), oc);
		ic.close();
		oc.close();
	}
	
	public void setInicioHabilitado(boolean habilitada) throws RemoteException {
		this.botonInicio.setEnabled(habilitada);
	}
	
	public void titilar(){
		(new Thread(
			new Runnable(){
				String auxiliar[] = {etiquetaEstado.getText(),""};
				public void run(){
					for (int i=0;i<13;i++){
						setEstado(auxiliar[i%2],1);
						try{Thread.sleep(1000);}catch(Exception e){e.printStackTrace();}
					}
				}
			}
		)).start();
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonElegir;
    private javax.swing.JButton botonInicio;
    private javax.swing.JButton botonOpciones;
    private javax.swing.JButton botonSalida;
    private javax.swing.JLabel etiquetaEstado;
    private javax.swing.JLabel imagenTankExile;
    private javax.swing.JSeparator separadorEstado;
    // End of variables declaration//GEN-END:variables
}
