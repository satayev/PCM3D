package pcm.gui;

import dev.simple.FixedPhoton;
import dev.simple.SimpleFixedModel;
import dev.simple.Tower;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;

import pcm.gui.graphics.*;

import javafx.scene.Node;

import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import pcm.model.geom.Vector;

public class UIMockupController implements Initializable {

  public static TabPane GUIPane;
  public static AnchorPane shapeCanvas, dataAnchorPane;
  public Button csReset, csAdd;
  public ShapePane csPane;
  public FlowPane shapePicker;
  public TextField csEdgeCount, shapeRotation, shapeScale;//, shapeHeight; 
  private CSNode head;
  public Tab simulationTab, dataTab;
  public Button animationButton;
  public Text simulationResults;
  public TextField zenithField, azimuthField, latitudeField, longitudeField;
  public CheckBox orbitCheckBox, toEquatorCheckBox;
  public Slider degreesSlider, photonsSlider, modelSizeSlider;
  public Label degreesLabel, photonsLabel, modelSizeLabel;
  public Accordion simulationAccordion1, simulationAccordion2, simulationAccordion3;
  public MenuBar dataMenuBar;
  public Menu clearButton; 
  public GridPane dataOptionsGrid, dataGraphGrid;
  public TitledPane userEarthOptions;

  /** These variables are set according to the menu options selected */
  List<Double> x;
  List<Double> y;
  String title;
  String xLabel;
  String yLabel;
  LineChartGraph lcg = new LineChartGraph();
  ListView<String> fileList = new ListView<String>(), modeList = new ListView<String>(), xAxisList = new ListView<String>(), yAxisList = new ListView<String>();
  ObservableList<String> items;
  
  @Override
  public void initialize(URL url, ResourceBundle rb) {

    assert GUIPane != null : "fx:id=\"GUIPane\" was not injected: check your FXML file 'UIMockup.fxml'.";
    GUIPane.setPrefSize(Main.width, Main.height);

    initializePatternTab();
    initializeSimulationTab();
    initializeDataTab();
  }

  private void initializeDataTab() {
    // Adds the graph to Data tab
    assert dataGraphGrid != null : "fx:id=\"dataGraphGrid\" was not injected: check your FXML file 'UIMockup.fxml'.";
    lcg.make();
    lcg.lineChart.setPrefSize(800, 500);
    dataGraphGrid.add(lcg.lineChart, 0, 0);

    /* Data tab */
    assert dataTab != null : "fx:id=\"dataTab\" was not injected: check your FXML file 'UIMockup.fxml'.";
    dataTab.setOnSelectionChanged(new EventHandler<Event>() {
      @Override
      public void handle(Event t) {
        if (dataTab.isSelected()) {
          // Retrieve data from AppletModel/Interfacer
          // TODO - John initialize vars
          x = new ArrayList<Double>(Arrays.asList(new Double(0), new Double(3), new Double(9)));
          y = new ArrayList<Double>(Arrays.asList(new Double(-1), new Double(8), new Double(9)));
          title = "Test";
          xLabel = "x-axis";
          yLabel = "y-axis";

          /* Default initial graph on data tab */
          modeList.getSelectionModel().select(0);
          xAxisList.getSelectionModel().select(0);
          yAxisList.getSelectionModel().select(1);
          
        }
      }
    });
    
    /* Data tab options */
    assert dataOptionsGrid != null : "fx:id=\"dataOptionsGrid\" was not injected: check your FXML file 'UIMockup.fxml'.";
    dataOptionsGrid.setHgap(5);
    dataOptionsGrid.setVgap(5);
    dataOptionsGrid.setPadding(new Insets(5, 5, 5, 5));

    final String[] options = new String[] { "File", "Mode", "X-Axis", "Y-Axis" };
    Text category;
    for (int i = 0; i < options.length; i++) {
      category = new Text(options[i]);
      category.setFont(Font.font("Verdana", 11));
      category.setFill(Color.WHITE);
      dataOptionsGrid.add(category, i, 0);
    }

    final String[] fileOptions = new String[] { "Save graph as .csv" };
    final String[] modeOptions = new String[] { "Wavelength", "Frequency" };
    final String[] axisOptions = new String[] { "..", "..." };

    /* Creating lists */
    items = FXCollections.observableArrayList(fileOptions);
    fileList.setItems(items);
    dataOptionsGrid.add(fileList, 0, 1);
    items = FXCollections.observableArrayList(modeOptions);
    modeList.setItems(items);
    dataOptionsGrid.add(modeList, 1, 1);
    items = FXCollections.observableArrayList(axisOptions);
    xAxisList.setItems(items);
    dataOptionsGrid.add(xAxisList, 2, 1);
    items = FXCollections.observableArrayList(axisOptions);
    yAxisList.setItems(items);
    dataOptionsGrid.add(yAxisList, 3, 1);
    
    HBox hbox = new HBox(); // space holder for layout
    hbox.setPrefWidth(400);
    dataOptionsGrid.add(hbox, 4, 1);
    
    /* User option listeners */
    fileList.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> ov,
              String old_val, String new_val) {
            if (new_val.equals(fileOptions[0])) {
              // Save .cvs
            }
          }
        });
    // TODO - John for the next three listeners
    modeList.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> ov,
              String old_val, String new_val) {
            if (new_val.equals(modeOptions[0])) {
              // Wavelength
              //change affected vars
              lcg.update(x, y, title, xLabel, yLabel);
            }
            else if (new_val.equals(modeOptions[1])) {
              // Frequency
              //change affected vars
              lcg.update(x, y, title, xLabel, yLabel);
            }
          }
        });

    xAxisList.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> ov,
              String old_val, String new_val) {
            if (new_val.equals(axisOptions[0])) {
              //x = ....;
              xLabel = axisOptions[0];
              title = yLabel + " vs " + xLabel;
              lcg.update(x, y, title, xLabel, yLabel);
            }
            else if (new_val.equals(axisOptions[1])) {
              //x = ....;
              xLabel = axisOptions[1];
              title = yLabel + " vs " + xLabel;
              lcg.update(x, y, title, xLabel, yLabel);
            }
          }
        });

    yAxisList.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<String>() {
          public void changed(ObservableValue<? extends String> ov,
              String old_val, String new_val) {
            if (new_val.equals(axisOptions[0])) {
              y = new ArrayList<Double>(Arrays.asList(new Double(8), new Double(3), new Double(0)));;
              yLabel = axisOptions[0];
              title = yLabel + " vs " + xLabel;
              lcg.update(x, y, title, xLabel, yLabel);
            }
            else if (new_val.equals(axisOptions[1])) {
              //y = ....;
              yLabel = axisOptions[1];
              title = yLabel + " vs " + xLabel;
              lcg.update(x, y, title, xLabel, yLabel);
            }
          }
        });



  }

  private void initializeSimulationTab() {
    /*
     * Simulation tab controls using AppletInterfacer
     */
    assert simulationTab != null : "fx:id=\"simulationTab\" was not injected: check your FXML file 'UIMockup.fxml'.";
    simulationTab.setOnSelectionChanged(new EventHandler<Event>() {
      @Override
      public void handle(Event t) {
        if (simulationTab.isSelected()) {
//          if (shapeCanvas.getChildren().size() > 0)
            AppletInterfacer.setModel(1, 1, 1, makeEdgeLists());
          AppletInterfacer.open();
        } else {
          AppletInterfacer.standBy();
        }
      }
    });

    assert simulationAccordion1 != null : "fx:id=\"simulationAccordion1\" was not injected: check your FXML file 'UIMockup.fxml'.";
    simulationAccordion1.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
      @Override
      public void changed(ObservableValue<? extends TitledPane> property, final TitledPane oldPane, final TitledPane newPane) {
        if (simulationAccordion1.maxHeightProperty().getValue() == 0)
          simulationAccordion1.setMaxHeight(500);
        else
          simulationAccordion1.setMaxHeight(0);
      }
    });
    assert simulationAccordion2 != null : "fx:id=\"simulationAccordion2\" was not injected: check your FXML file 'UIMockup.fxml'.";
    simulationAccordion2.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
      @Override
      public void changed(ObservableValue<? extends TitledPane> property, final TitledPane oldPane, final TitledPane newPane) {
        if (simulationAccordion2.maxHeightProperty().getValue() == 0)
          simulationAccordion2.setMaxHeight(500);
        else
          simulationAccordion2.setMaxHeight(0);
      }
    });
    assert simulationAccordion3 != null : "fx:id=\"simulationAccordion3\" was not injected: check your FXML file 'UIMockup.fxml'.";
    simulationAccordion3.expandedPaneProperty().addListener(new ChangeListener<TitledPane>() {
      @Override
      public void changed(ObservableValue<? extends TitledPane> property, final TitledPane oldPane, final TitledPane newPane) {
        if (simulationAccordion3.maxHeightProperty().getValue() == 0)
          simulationAccordion3.setMaxHeight(500);
        else
          simulationAccordion3.setMaxHeight(0);
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

        AppletInterfacer.update(Double.parseDouble(zenithField.getText()), Double.parseDouble(azimuthField.getText()),
            Double.parseDouble(latitudeField.getText()),
            Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());

        if (AppletInterfacer.getAnimState()) {
          animationButton.setText("Pause Simulation");
        } else {
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

    assert userEarthOptions != null : "fx:id=\"userEarthOptions\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert orbitCheckBox != null : "fx:id=\"orbitCheckBox\" was not injected: check your FXML file 'UIMockup.fxml'.";
    // Whether to use preset ISS orbit or not
    orbitCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (newValue) {
          userEarthOptions.setPrefHeight(45);
          //TODO - John
        } else {
          userEarthOptions.setPrefHeight(90);
          //TODO - John
        }
      }
    });
    

    assert zenithField != null : "fx:id=\"zenithField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert azimuthField != null : "fx:id=\"azimuthField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert latitudeField != null : "fx:id=\"latitudeField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert longitudeField != null : "fx:id=\"longitudeField\" was not injected: check your FXML file 'UIMockup.fxml'.";
    assert toEquatorCheckBox != null : "fx:id=\"toEquatorCheckBox\" was not injected: check your FXML file 'UIMockup.fxml'.";

    zenithField.setText(String.valueOf(AppletInterfacer.getZenith()));
    azimuthField.setText(String.valueOf(AppletInterfacer.getAzimuth()));

    // TODO - Accept only valid values from text fields, prompt user when invalid
    zenithField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateModel(Double.parseDouble(zenithField.getText()), Double.parseDouble(azimuthField.getText()));
        AppletInterfacer.changed = true;
        animationButton.setText("Reset");
      }
    });
    azimuthField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateModel(Double.parseDouble(zenithField.getText()), Double.parseDouble(azimuthField.getText()));
        AppletInterfacer.changed = true;
        animationButton.setText("Reset");
      }
    });
    latitudeField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()), Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
        AppletInterfacer.changed = true;
        animationButton.setText("Reset");
      }
    });
    longitudeField.setOnKeyReleased(new EventHandler<KeyEvent>() {
      public void handle(KeyEvent ke) {
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()), Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
        AppletInterfacer.changed = true;
        animationButton.setText("Reset");
      }
    });
    toEquatorCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        //AppletInterfacer.updateEarth(Double.parseDouble(latitudeField.getText()),    Double.parseDouble(longitudeField.getText()), (toEquatorCheckBox.selectedProperty()).getValue());
        AppletInterfacer.changed = true;
        animationButton.setText("Reset");
      }
    });

  }

  private void initializePatternTab() {
    clearButton.showingProperty().addListener(new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
            shapeCanvas.getChildren().clear();
            clearButton.hide();
        }
    }); 
    shapeRotation.textProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> ov, String t, String t1) {
        double rotation;
        try {
          rotation = Double.parseDouble(t1);
        } catch (NumberFormatException e) {
          System.out.println(e);
          return;
        }

        for (Node shape : shapePicker.getChildren())
          ((ShapePane) shape).rotate(rotation);
      }
    });
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
          nodes[i].setLayoutX(100 + 100 * Math.cos(theta));
          nodes[i].setLayoutY(100 + 100 * Math.sin(theta));
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

        t.consume();
      }
    });
    csAdd.setOnAction(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent t) {
        if (head == null)
          return;

        CSNode currNode = head;
        do {
          currNode.setLayoutX(currNode.getLayoutX() * 0.5d);
          currNode.setLayoutY(currNode.getLayoutY() * 0.5d);
          currNode = currNode.next;
        } while (currNode != head);

        ShapePane newShape = new ShapePane(head, 0.0d, true);
        try {
          newShape.rotate(Double.parseDouble(shapeRotation.getText()));
        } catch (NumberFormatException e) {
          System.out.println(e);
        }

        shapePicker.getChildren().add(newShape);

        csPane.clear();
        head = null;

        t.consume();
      }
    });
    shapeCanvas.setOnDragOver(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent t) {
        t.acceptTransferModes(TransferMode.ANY);

        double radius = ShapePane.dragPreview.scale * 0.5d;

        ShapePane.dragPreview.setLayoutX(Math.max(0, Math.min(
            t.getX() - radius, shapeCanvas.getWidth() - ShapePane.dragPreview.scale)));
        ShapePane.dragPreview.setLayoutY(Math.max(0, Math.min(
            t.getY() - radius, shapeCanvas.getHeight() - ShapePane.dragPreview.scale)));

        t.consume();
      }
    });
    shapeCanvas.setOnDragEntered(new EventHandler<DragEvent>() {
      @Override
      public void handle(DragEvent t) {
        if (!shapeCanvas.getChildren().contains(ShapePane.dragPreview)) {
          double scale = 0.1d;
//          double height = 1.0d; 
          try {
            scale = Double.parseDouble(shapeScale.getText()) * 600.0d;
          } catch (NumberFormatException e) {
            System.out.println(e);
          }
//          try {
//            height = Double.parseDouble(shapeHeight.getText());
//          } catch (NumberFormatException e) {
//            System.out.println(e);
//          } 
          
          ShapePane.dragPreview.scale(scale);
          shapeCanvas.getChildren().add(ShapePane.dragPreview);
        }
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
  }

  private SimpleFixedModel makeSFM() {
    ObservableList<Node> shapes = shapeCanvas.getChildren();
    List<Tower> towers = new ArrayList<Tower>();
    double x = 1, y = 1, z = 1, theta = 0;

    for (int i = 0; i < shapes.size(); i++) {
      ShapePane shape = (ShapePane) shapes.get(i);
      List<Double> lx = new ArrayList<Double>();
      List<Double> ly = new ArrayList<Double>();
      CSNode first = shape.head;
      CSNode curr = first;

      do {
        lx.add((shape.getLayoutX() + curr.getLayoutX()) / 600.0d);
        ly.add((shape.getLayoutY() + curr.getLayoutY()) / 600.0d);
        curr = curr.next;
      } while (curr != first);

      towers.add(new Tower(lx, ly));
    }

    return new SimpleFixedModel(x, y, z, theta, towers, new FixedPhoton(new Vector(0, 0, -1)));
  }

  private List<List<Vector>> makeEdgeLists() {
    ObservableList<Node> shapes = shapeCanvas.getChildren();
    List<List<Vector>> edgelists = new ArrayList<List<Vector>>();

    for (int i = 0; i < shapes.size(); i++) {
      ShapePane shape = (ShapePane) shapes.get(i);
      List<Vector> edgelist = new ArrayList<Vector>();
      CSNode first = shape.head;
      CSNode curr = first;

      do {
        double x = (shape.getLayoutX() + curr.getLayoutX()) / 600.0d;
        double y = (shape.getLayoutY() + curr.getLayoutY()) / 600.0d;
        Vector edge = new Vector(x, y, 0);
        edgelist.add(edge);
        curr = curr.next;
      } while (curr != first);

      edgelists.add(edgelist);
    }

    return edgelists;
  }
}
