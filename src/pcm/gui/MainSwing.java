package pcm.gui;

import graphics.Applet;
import graphics.AppletModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

import javax.swing.*;

import pcm.geom.Vector;
import processing.core.PApplet;

public class MainSwing extends JFrame {

  private JLayeredPane layeredPane;

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

    //    JPanel panel = new JPanel();
    //    panel.setOpaque(true);
    //    panel.setBounds(100, 100, 600, 600);
    //    //panel.setMaximumSize(new Dimension(600,600));
    //    PApplet applet = new Applet();
    //    panel.add(applet);// BorderLayout.CENTER);
    //    //panel.setBackground(Color.green);
    //    layeredPane.add(panel, new Integer(5));
    //    applet.init();

    int offsetx = 0, offsety = 45, width = 750, height = 650;

    AppletModel model = new AppletModel(3);

    JPanel mainPanel = new JPanel();
    // Angled view
    PApplet applet0 = new Applet(width, height, 3, 0,
        new Vector(38.15295579507074, 45.93018709355965, 98.72728729248047),
        new Vector(409.32583601053545, 398.7817868740755, 269.13170308753797),
        new Vector(0.0, 0.0, -1.0),
        new Vector(0.679308295249939, -0.7338531017303467, 0.0),
        new Vector(0.32537463307380676, 0.3011906147003174, -0.8963345289230347),
        new Vector(0.6577777862548828, 0.6088873744010925, 0.44337838888168335),
        model);
    mainPanel.setOpaque(true);
    mainPanel.setBounds(offsetx, offsety, width, height);
    //mainPanel.setBounds(offsetx,offsety+height/2,width,height/2);
    mainPanel.add(applet0);
    layeredPane.add(mainPanel, new Integer(5));
    applet0.init();

    //    JPanel leftPanel = new JPanel();
    //    // Above view
    //    PApplet applet1 = new Applet(width/2, height/2, 3,1,
    //        new Vector(123.71290140940556, 87.71537992937016, 571.9623483370927),
    //        new Vector(123.99429138051346, 39.75564116612077, -29.049701690673828),
    //        new Vector(0.0, 0.0, -1.0),
    //        new Vector(0.9999827742576599, 0.005867215804755688, 0.0),
    //        new Vector(-0.005848623346537352, 0.996813952922821, -0.07954679429531097),
    //        new Vector(-4.6671819291077554E-4, 0.07954542338848114, 0.9968311190605164),
    //        model);
    //    leftPanel.setOpaque(true);
    //    leftPanel.setBounds(offsetx,offsety+height/2,width/2,height/2);
    //    leftPanel.add(applet1);
    //    layeredPane.add(leftPanel, new Integer(4));
    //    applet1.init();    
    //    
    //    
    //    JPanel rightPanel = new JPanel();
    //    // Front view
    //    PApplet applet2 = new Applet(width/2, height/2, 3,2,
    //        new Vector(125.82053857659048, -61.8114633099176, 134.2598032951355),
    //        new Vector(133.00827005071065, 668.1692323275802, 101.37971584149304),
    //        new Vector(0.0, 0.0, -1.0),
    //        new Vector(0.9999515414237976, -0.00984598696231842, 0.0),
    //        new Vector(-4.430162371136248E-4, -0.04499242082238197, -0.9989872574806213),
    //        new Vector(0.009836014360189438, 0.9989387392997742, -0.044994596391916275),
    //        model);
    //    rightPanel.setOpaque(true);
    //    rightPanel.setBounds(offsetx+width/2,offsety+height/2,width/2,height/2);
    //    rightPanel.add(applet2);
    //    layeredPane.add(rightPanel, new Integer(3));
    //    applet2.init();

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