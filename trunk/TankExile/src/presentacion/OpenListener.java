/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package presentacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JList;
import javax.swing.JSplitPane;


/**
 *
 * @author Medina
 */
class OpenListener implements ActionListener {
  private JList list;
  
    OpenListener(JList list) {
        this.list=list;
    }
        public void actionPerformed(ActionEvent e) {
                
            //Retornamos el nombre del archivo seleccionado
                System.out.println(list.getSelectedValue());
                System.out.println(list.getSelectedIndex());
                //Aca se hace la apertura del nombre y del indice
                // del archivo seleccionado
          
        }
}