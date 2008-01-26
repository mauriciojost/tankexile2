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
class ipBackListener  implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PrePartida1.getPrePartida1().dispose();
            Presentacion1.getPresentacion1().show();
            
    }

}
