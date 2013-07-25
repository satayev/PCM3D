package pcm.gui;

import pcm.gui.graphics.*;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;

import javax.swing.*;

import processing.core.PApplet;

/**
 * Instantiates JavaFX and Processing applet inside JFrame GUI (required for showing PApplet).
 * 
 * @author Susan
 */
public class Main extends JFrame implements ComponentListener {

  private JLayeredPane layeredPane;

  // width and height equivalent to UIMockup.fxml's values, but are altered on running Main
  public static int width = 1000, height = 700,
      //region of window where PApplet sits   
      offsetTop = 53, offsetRight = 240; 
  
  public static JFrame GUI;

  public Main() {
    super("PCM3D");
    
    int offsetTop = 53, offsetRight = 240; // region of window where PApplet sits
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//    height = gd.getDisplayMode().getHeight() - 75;
//    width = height - offsetTop + offsetRight;
    
    setBounds(100, 0, width, height);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    // Creating a LayeredPane for PApplet to appear over JavaFX
    JComponent contentPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(new Dimension(width, height));
    layeredPane.setLayout(null);

    // Adding JavaFX to JPanel and LayeredPane
    initJavaFX();

    // Using AppletInterfacer to add PApplet to JPanel and LayeredPane
    
    AppletInterfacer.panel.setOpaque(true);
    AppletInterfacer.create(0, offsetTop, width - offsetRight, height - offsetTop);
    layeredPane.add(AppletInterfacer.panel, new Integer(5));

    contentPane.add(layeredPane);
    contentPane.setOpaque(true);
    setContentPane(contentPane);

    pack();
    setVisible(true);

    // For resizing listener
    addComponentListener(this);
    
    GUI = this;
  }

  /*
   * Loading JavaFX fxml file into AWT Swing component
   */
  public void initJavaFX() {

    setLayout(new BorderLayout());
    final JFXPanel fxPanel = new JFXPanel();

    Platform.setImplicitExit(false);
    // Create JavaFX scene
    Platform.runLater(new Runnable() {
      public void run() {
        try {

          String fxml = "UIMockup.fxml";
          FXMLLoader loader = new FXMLLoader();
          InputStream in = Main.class.getResourceAsStream(fxml);
          loader.setBuilderFactory(new JavaFXBuilderFactory());
          loader.setLocation(Main.class.getResource(fxml));
          TabPane page;
          try {
            page = (TabPane) loader.load(in);
          } finally {
            in.close();
          }
          Scene scene = new Scene(page);
          fxPanel.setScene(scene);

        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });

    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    //p.setPreferredSize(new Dimension(1000, 700));
    p.setOpaque(true);
    p.setBounds(0, 0, getWidth(), getHeight());
    p.add(fxPanel);
    layeredPane.add(p, new Integer(0));
  }

  @Override
  public void componentHidden(ComponentEvent arg0) {
  }

  @Override
  public void componentMoved(ComponentEvent arg0) {
  }

  /*
   * //TODO
   * Applet resizing enabled by adding using JFrame's listener and recreating applet
   * 
   * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
   */
  @Override
  public void componentResized(ComponentEvent arg0) {
    //System.out.println(" --- Resized ");

    // code adding listener to applet doesn't work / memory leak - 
    //appletInterfacer.applet.frame = this;
    //appletInterfacer.applet.setupFrameResizeListener();
  }

  @Override
  public void componentShown(ComponentEvent arg0) {

  }

  public static void main(String[] args) {

    new Main();

  }

}