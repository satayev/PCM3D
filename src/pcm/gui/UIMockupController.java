/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pcm.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import graphics.Applet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import processing.core.PApplet;

import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * FXML Controller class
 *
 * @author beeRoc
 */
public class UIMockupController implements Initializable {

  

    @FXML 
    public Tab simulationTab;
    public AnchorPane simulationAnchorPane; // Value injected by FXMLLoader

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
      
      assert simulationTab != null : "fx:id=\"simulationTab\" was not injected: check your FXML file 'UIMockup.fxml'.";
      assert simulationAnchorPane != null : "fx:id=\"simulationAnchorPane\" was not injected: check your FXML file 'UIMockup.fxml'.";

//      simulationTab.setOnSelectionChanged(new EventHandler<Event>() {
//        @Override
//        public void handle(Event t) {
//            if (simulationTab.isSelected()) {
//                
//            }
//        }
//    });
      

/*      Node applet...
        Node n = new Node();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
         JPanel panel = new JPanel();
         PApplet applet = new Applet();
         panel.add(applet);
         applet.init();
         simulationAnchorPane.
      simulationAnchorPane.setTopAnchor(panel, 24.0);
      simulationAnchorPane.setLeftAnchor(panel, 0.0);
      simulationAnchorPane.setBottomAnchor(panel, 0.0);
      simulationAnchorPane.setRightAnchor(panel, 240.0); // or prefWidth="1040.0"
      simulationAnchorPane.getChildren().addAll(panel);
      
      }
     }*/
      
    }    
}
