package graphics;

import java.awt.BorderLayout;
import javax.swing.*;

import pcm.geom.Vector;
import processing.core.PApplet;

public class GUI extends JFrame {

  public GUI() {
    super("Demo");

    int width = 700, height = 800;
    setBounds(100, 50, width, height);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(null);

    AppletModel model = new AppletModel();

    // Can add PApplet to this frame or JApplet

    //setLayout(new BorderLayout());
    //PApplet applet = new Applet();

    JPanel mainPanel = new JPanel();
    // Angled view
    PApplet applet0 = new Applet();
    
//    width, height / 2, 3, 0,
//        new Vector(37.44635891391954, 39.81545548373833, 108.67396104335785),
//        new Vector(432.7288153558058, 411.7856923688496, 363.53423328608915),
//        new Vector(0.0, 0.0, -1.0),
//        new Vector(0.6853057146072388, -0.7282554507255554, 0.0),
//        new Vector(0.3095265328884125, 0.2912718653678894, -0.9051817655563354),
//        new Vector(0.6592035293579102, 0.6203262209892273, 0.4250246584415436), model);
    mainPanel.setBounds(0, 0, width, height);
    mainPanel.add(applet0);
    applet0.init();

//    JPanel leftPanel = new JPanel();
//    // Above view
//    PApplet applet1 = new Applet(width / 2, height / 2, 3, 1,
//        new Vector(123.71290140940556, 87.71537992937016, 571.9623483370927),
//        new Vector(123.99429138051346, 39.75564116612077, -29.049701690673828),
//        new Vector(0.0, 0.0, -1.0),
//        new Vector(0.9999827742576599, 0.005867215804755688, 0.0),
//        new Vector(-0.005848623346537352, 0.996813952922821, -0.07954679429531097),
//        new Vector(-4.6671819291077554E-4, 0.07954542338848114, 0.9968311190605164),
//        model);
//    leftPanel.setBounds(0, 0, width / 2, height / 2);
//    leftPanel.add(applet1);
//    applet1.init();
//
//    JPanel rightPanel = new JPanel();
//    // Front view
//    PApplet applet2 = new Applet(width / 2, height / 2, 3, 2,
//        new Vector(125.82053857659048, -61.8114633099176, 134.2598032951355),
//        new Vector(133.00827005071065, 668.1692323275802, 101.37971584149304),
//        new Vector(0.0, 0.0, -1.0),
//        new Vector(0.9999515414237976, -0.00984598696231842, 0.0),
//        new Vector(-4.430162371136248E-4, -0.04499242082238197, -0.9989872574806213),
//        new Vector(0.009836014360189438, 0.9989387392997742, -0.044994596391916275),
//        model);
//    rightPanel.setBounds(width / 2, 0, width / 2, height / 2);
//    rightPanel.add(applet2);
//    applet2.init();
//
    add(mainPanel);
//    add(leftPanel);
//    add(rightPanel);
    setVisible(true);
  }

  public static void main(String[] args) {

    new GUI();

  }

}