package pcm.gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import pcm.gui.graphics.*;

import javafx.scene.Node;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import processing.core.PApplet;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class UIMockupController implements Initializable {

    boolean runAnim = false, appletParamsChanged = false;
    
    @FXML
    public Button csReset;
    public AnchorPane csCanvas;
    public TextField csEdgeCount;
    public Tab simulationTab;
    public AnchorPane simulationAnchorPane;
    
    public Button animationButton;
    public Text simulationResults;
    
    public Slider degreesSlider, photonsSlider, modelSizeSlider;
    public Label degreesLabel, photonsLabel, modelSizeLabel;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert simulationTab != null : "fx:id=\"simulationTab\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert simulationAnchorPane != null : "fx:id=\"simulationAnchorPane\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert csReset != null : "fx:id=\"csReset\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert csCanvas != null : "fx:id=\"csCanvas\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert csEdgeCount != null : "fx:id=\"csEdgeCount\" was not injected: check your FXML file 'UIMockup.fxml'.";
        
        
        assert degreesSlider != null : "fx:id=\"degreesSlider\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert photonsSlider != null : "fx:id=\"photonsSlider\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert modelSizeSlider != null : "fx:id=\"modelSizeSlider\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert degreesLabel != null : "fx:id=\"degreesLabel\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert photonsLabel != null : "fx:id=\"photonsLabel\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert modelSizeLabel != null : "fx:id=\"modelSizeLabel\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert animationButton != null : "fx:id=\"animationButton\" was not injected: check your FXML file 'UIMockup.fxml'.";
        assert simulationResults != null : "fx:id=\"simulationResults\" was not injected: check your FXML file 'UIMockup.fxml'.";
        
        
        degreesLabel.setText(Double.toString(degreesSlider.getValue()));
        degreesSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            		appletParamsChanged = true;
            		degreesLabel.setText(Double.toString(degreesSlider.getValue()));
            }
        });
        photonsLabel.setText(Double.toString(photonsSlider.getValue()));
        photonsSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            		appletParamsChanged = true;
            		photonsLabel.setText(String.valueOf((int)photonsSlider.getValue()));
            }
        });
        modelSizeLabel.setText(Double.toString(modelSizeSlider.getValue()));
        modelSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {
            		appletParamsChanged = true;
            		modelSizeLabel.setText(String.valueOf(Math.pow((int)modelSizeSlider.getValue(), 2)));
            }
        });
        
        
        animationButton.setOnAction(new EventHandler<ActionEvent>() {
          @Override 
          public void handle(ActionEvent e) {
	          runAnim = !runAnim;
	          MainSwing.appletModel.runAnim = runAnim; 
            if (runAnim) {
//            	if (appletParamsChanged) {
//            		System.out.println("changed params");
//            		MainSwing.appletModel = new AppletModel(degreesSlider.getValue(), (int)photonsSlider.getValue(), (int)modelSizeSlider.getValue());
//            		//MainSwing.appletModel.setParams(degreesSlider.getValue(), (int)photonsSlider.getValue(), (int)modelSizeSlider.getValue());
//            	}
//          	  	appletParamsChanged = false;
            	animationButton.setText("Pause Simulation");
            }
            else {
            	if (appletParamsChanged) {
            		//System.out.println("changed params");
            		//MainSwing.appletModel = new AppletModel(degreesSlider.getValue(), (int)photonsSlider.getValue(), (int)modelSizeSlider.getValue());
            		MainSwing.appletModel.setParams(degreesSlider.getValue(), (int)photonsSlider.getValue(), (int)modelSizeSlider.getValue());
            	}
            	appletParamsChanged = false;
            	animationButton.setText("Play Simulation");
            }
           
          }
        });
        
        Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(.1), new EventHandler<ActionEvent>() {

          @Override
          public void handle(ActionEvent event) {

        	  if (!appletParamsChanged)
        		  degreesSlider.setValue(MainSwing.appletModel.degrees);
        	  simulationResults.setText(MainSwing.appletModel.printOutput);
            
            if (!MainSwing.appletModel.runAnim)
            	animationButton.setText("Play Simulation");

            
          }
        }));
        fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
        fiveSecondsWonder.play();
          
        
        

        
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

    }
}
