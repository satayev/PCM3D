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
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class UIMockupController implements Initializable {

  public Button csReset;
  public Button csAdd;
  public ShapePane csPane;
  public FlowPane shapePicker;
  public AnchorPane shapeCanvas;
  public TextField csEdgeCount;
  private CSNode head;

  public Tab simulationTab;
  public Button animationButton;
  public Text simulationResults;
  public TextField zenithField, azimuthField, latitudeField, longitudeField;
  public CheckBox toEquatorCheckBox;

  public Slider degreesSlider, photonsSlider, modelSizeSlider;
  public Label degreesLabel, photonsLabel, modelSizeLabel;

  @Override
  public void initialize(URL url, ResourceBundle rb) {

    /*
     * Panel tab controls
     */
    assert csReset != null : "fx:id=\"csReset\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert csAdd != null : "fx:id=\"csAdd\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert csPane != null : "fx:id=\"csPane\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert shapePicker != null : "fx:id=\"shapePicker\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert shapeCanvas != null : "fx:id=\"shapeCanvas\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert csEdgeCount != null : "fx:id=\"csEdgeCount\" was not injected: check your FXML file 'UIMockup.fxml'.";

    csReset.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent t) {
        csPane.clear();

        int count;
        try {
          count = Integer.parseInt(csEdgeCount.getText());
        } catch (NumberFormatException e) {
          System.out.println(e);
          return;
        }

        CSNode[] nodes = new CSNode[count];
        Line[] edges = new Line[count];

        double thetaDelta = 2 * Math.PI / count;

        for (int i = 0; i < count; i++) {
          nodes[i] = new CSNode(null, null);

          double theta = thetaDelta * i;
          nodes[i].layoutXProperty().set(100 + 90 * Math.cos(theta));
          nodes[i].layoutYProperty().set(100 + 90 * Math.sin(theta));
        }
        for (int i = 0; i < count; i++) {
          int j = i + 1 == count ? 0 : i + 1;
          nodes[i].prev = i == 0 ? nodes[count - 1] : nodes[i - 1];
          nodes[i].next = nodes[j];

          edges[i] = new Line();
          edges[i].startXProperty().bind(nodes[i].layoutXProperty());
          edges[i].startYProperty().bind(nodes[i].layoutYProperty());
          edges[i].endXProperty().bind(nodes[j].layoutXProperty());
          edges[i].endYProperty().bind(nodes[j].layoutYProperty());
        }

        head = nodes[0];

        csPane.getChildren().addAll(edges);
        csPane.getChildren().addAll(nodes);
      }
    });
    csAdd.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent t) {
        if (head == null || head.next == head)
          return;

        CSNode currNode = head;
        do {
          currNode.setLayoutX(currNode.getLayoutX() * 0.5d);
          currNode.setLayoutY(currNode.getLayoutY() * 0.5d);
          currNode = currNode.next;
        } while (currNode != head);

        shapePicker.getChildren().add(new ShapePane(head, 0.0d, true));

        do {
          currNode.setLayoutX(currNode.getLayoutX() * 2.0d);
          currNode.setLayoutY(currNode.getLayoutY() * 2.0d);
          currNode = currNode.next;
        } while (currNode != head);
      }
    });
    shapeCanvas.setOnDragOver(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent t) {
        t.acceptTransferModes(TransferMode.ANY);
        ShapePane.dragPreview.layoutXProperty().set(t.getSceneX() - 100.0d);
        ShapePane.dragPreview.layoutYProperty().set(t.getSceneY() - 148.0d);
        t.consume();
      }
    });
    shapeCanvas.setOnDragEntered(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent t) {
        shapeCanvas.getChildren().add(ShapePane.dragPreview);
        t.consume();
      }
    });
    shapeCanvas.setOnDragDropped(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent t) {
        t.setDropCompleted(true);
        t.consume();
      }
    });

    /*
     * Simulation tab controls using AppletInterfacer
     */
    assert simulationTab != null : "fx:id=\"simulationTab\" was not injected: check your FXML file 'UIMockup.fxml'.";
    simulationTab.setOnSelectionChanged(new EventHandler<Event>() {
      @Override
      public void handle(Event t) {
        if (simulationTab.isSelected()) {
          AppletInterfacer.open();
        }
        else {
          AppletInterfacer.standBy();
        }
      }
    });

    // Turning on or off animation
    assert animationButton != null : "fx:id=\"animationButton\" was not injected: check your FXML file 'UIMockup.fxml'.";
    animationButton.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent e) {
        //AppletInterfacer.updateModel((int)modelSizeSlider.getValue());
        //AppletInterfacer.toggleAnim();
        //AppletModel.setParams(degreesSlider.getValue(), (int)photonsSlider.getValue(), (int)modelSizeSlider.getValue());

        // User inputed values updated in model on animationButton press
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()),  Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());

        AppletInterfacer.update(Double.parseDouble(zenithField.getText()), Double.parseDouble(azimuthField.getText()), Double.parseDouble(latitudeField.getText()),
            Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
        
        if (AppletInterfacer.getAnimState()) {
          animationButton.setText("Pause Simulation");
        }
        else {
          animationButton.setText("Play Simulation");
        }

      }
    });

    // Retrieving AppletModel's simulation stat results for viewing
    assert simulationResults != null : "fx:id=\"simulationResults\" was not injected: check your FXML file 'UIMockup.fxml'.";
    Timeline fiveSecondsWonder = new Timeline(new KeyFrame(Duration.seconds(.1), new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent event) {
        simulationResults.setText(AppletInterfacer.modelStats());
        
      }
    }));
    fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
    fiveSecondsWonder.play();

    assert zenithField != null : "fx:id=\"zenithField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert azimuthField != null : "fx:id=\"azimuthField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert latitudeField != null : "fx:id=\"latitudeField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert longitudeField != null : "fx:id=\"longitudeField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert toEquatorCheckBox != null : "fx:id=\"toEquatorCheckBox\" was not injected: check your FXML file 'UIMockup.fxml'.";

    zenithField.setText(String.valueOf(AppletInterfacer.getZenith()));
    azimuthField.setText(String.valueOf(AppletInterfacer.getAzimuth()));
    
    // TODO - Add ability for user to choose switch to radians?
    // TODO - Accept only valid values from text fields, prompt user when invalid
    zenithField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateModel(Double.parseDouble(zenithField.getText()), Double.parseDouble(azimuthField.getText()));
        AppletInterfacer.changed = true; animationButton.setText("Reset");
      }
    });
    azimuthField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateModel(Double.parseDouble(zenithField.getText()), Double.parseDouble(azimuthField.getText()));
        AppletInterfacer.changed = true;animationButton.setText("Reset");
      }
    });
    latitudeField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()), Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
        AppletInterfacer.changed = true;animationButton.setText("Reset");
      }
    });
    longitudeField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()), Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
        AppletInterfacer.changed = true;animationButton.setText("Reset");
      }
    });
    toEquatorCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()),    Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
       AppletInterfacer.changed = true;animationButton.setText("Reset");
      }
    });

    /*
     * Various other controls that may be kept or phased out
     */
    //        assert degreesSlider != null : "fx:id=\"degreesSlider\" was not injected: check your FXML file 'UIMockup.fxml'.";
    //        assert photonsSlider != null : "fx:id=\"photonsSlider\" was not injected: check your FXML file 'UIMockup.fxml'.";
    //        assert modelSizeSlider != null : "fx:id=\"modelSizeSlider\" was not injected: check your FXML file 'UIMockup.fxml'.";
    //        assert degreesLabel != null : "fx:id=\"degreesLabel\" was not injected: check your FXML file 'UIMockup.fxml'.";
    //        assert photonsLabel != null : "fx:id=\"photonsLabel\" was not injected: check your FXML file 'UIMockup.fxml'.";
    //        assert modelSizeLabel != null : "fx:id=\"modelSizeLabel\" was not injected: check your FXML file 'UIMockup.fxml'.";
    //
    //        degreesLabel.setText(Double.toString(degreesSlider.getValue()));
    //        degreesSlider.valueProperty().addListener(new ChangeListener<Number>() {
    //            public void changed(ObservableValue<? extends Number> ov,
    //                Number old_val, Number new_val) {
    //        		AppletInterfacer.changed();
    //        		degreesLabel.setText(Double.toString(degreesSlider.getValue()));
    //            }
    //        });
    //        photonsLabel.setText(Double.toString(photonsSlider.getValue()));
    //        photonsSlider.valueProperty().addListener(new ChangeListener<Number>() {
    //            public void changed(ObservableValue<? extends Number> ov,
    //                Number old_val, Number new_val) {
    //        		AppletInterfacer.changed();
    //            	photonsLabel.setText(String.valueOf((int)photonsSlider.getValue()));
    //            }
    //        });
    //        modelSizeLabel.setText(Double.toString(modelSizeSlider.getValue()));
    //        modelSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
    //            public void changed(ObservableValue<? extends Number> ov,
    //                Number old_val, Number new_val) {
    //        		AppletInterfacer.changed();
    //            	modelSizeLabel.setText(String.valueOf(Math.pow((int)modelSizeSlider.getValue(), 2)));
    //            }
    //        });

  }
}
