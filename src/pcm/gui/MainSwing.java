package pcm.gui;

import pcm.gui.graphics.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import processing.core.PApplet;


/**
 * Instantiates JavaFX inside a GUI. Places Processing applet inside the GUI.
 * TODO: cut out empty spaces from placing unusual contents inside jpanels to be added to the jframe, borderlayout maybe?
 * 
 * @author Susan
 */
public class MainSwing extends JFrame {

  private JLayeredPane layeredPane;

  public static int appletWidth = 750, appletHeight = 650;
  public static AppletModel appletModel = new AppletModel();
  public static PApplet applet = new Applet(appletWidth, appletHeight, appletModel);
  
  public MainSwing() {
    super("Test");

    setBounds(100, 50, 1000, 700);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);

    JComponent contentPane = new JPanel();
    layeredPane = new JLayeredPane();
    layeredPane.setPreferredSize(new Dimension(1000, 700));
    //contentPane.setSize(new Dimension(1000,700));
    layeredPane.setLayout(null);

    init();

    int offsetx = 0, offsety = 45;

    //AppletModel model = new AppletModel();

    JPanel mainPanel = new JPanel();
    // Angled view
    mainPanel.setOpaque(true);
    mainPanel.setBounds(offsetx, offsety, appletWidth, appletHeight);
    //mainPanel.setBounds(offsetx,offsety+height/2,width,height/2);
    mainPanel.add(applet);
    layeredPane.add(mainPanel, new Integer(5));
    applet.init();


    contentPane.add(layeredPane);
    contentPane.setOpaque(true);
    setContentPane(contentPane);

    pack();
    setVisible(true);
  }

  public void init() {

    setLayout(new BorderLayout());
    final JFXPanel fxPanel = new JFXPanel();

    Platform.setImplicitExit(false);
    // create JavaFX scene
    Platform.runLater(new Runnable() {
      public void run() {
        try {

          String fxml = "UIMockup.fxml";
          FXMLLoader loader = new FXMLLoader();
          InputStream in = MainSwing.class.getResourceAsStream(fxml);
          loader.setBuilderFactory(new JavaFXBuilderFactory());
          loader.setLocation(MainSwing.class.getResource(fxml));
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

    JPanel p = new JPanel();
    //p.setPreferredSize(new Dimension(1000, 700));
    p.setOpaque(true);
    p.setBounds(-10, -10, 1000, 700);
    p.add(fxPanel);
    layeredPane.add(p, new Integer(0));
    //add(fxPanel, BorderLayout.CENTER);
  }

  public static void main(String[] args) {

    new MainSwing();

  }
}