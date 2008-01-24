/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package presentacion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Medina
 */
class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
            //hay que implementar la vuelta atras a la pagina que la precede
    }

}
