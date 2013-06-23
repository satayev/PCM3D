package pcm.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import graphics.Applet;
import javafx.scene.Node;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import processing.core.PApplet;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class UIMockupController implements Initializable {

    @FXML
    public Button csReset;
    public AnchorPane csCanvas;
    public TextField csEdgeCount;
    public Tab simulationTab;
    public AnchorPane simulationAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert simulationTab != null : "fx:id=\"simulationTab\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert simulationAnchorPane != null : "fx:id=\"simulationAnchorPane\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert csReset != null : "fx:id=\"csReset\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert csCanvas != null : "fx:id=\"csCanvas\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert csEdgeCount != null : "fx:id=\"csEdgeCount\" was not injected: check your FXML file 'UIMockup.fxml'.";

        csReset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                csCanvas.getChildren().clear();
                try {
                    int count = Integer.parseInt(csEdgeCount.getText());
                    CSNode[] nodes = new CSNode[count];
                    Line[] edges = new Line[count];
                    
                    double thetaDelta = 2 * Math.PI / count;
                    for (int i = 0; i < count; i++) {
                        double theta = thetaDelta * i;
                        nodes[i] = new CSNode();
                        AnchorPane.setLeftAnchor(nodes[i], 96.5 + 90 * Math.cos(theta));
                        AnchorPane.setTopAnchor(nodes[i], 96.5 + 90 * Math.sin(theta));
                    }
                    for (int i = 0; i < count; i++) {
                        int j = i + 1 == count ? 0 : i + 1;
                        edges[i] = new Line();
                        edges[i].startXProperty().bind(nodes[i].layoutXProperty());
                        edges[i].startYProperty().bind(nodes[i].layoutYProperty());
                        edges[i].endXProperty().bind(nodes[j].layoutXProperty());
                        edges[i].endYProperty().bind(nodes[j].layoutYProperty());
                    }
                    csCanvas.getChildren().addAll(edges);
                    csCanvas.getChildren().addAll(nodes);
                } catch (NumberFormatException e) {
                    System.out.println(e);
                }
            }
        });

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
