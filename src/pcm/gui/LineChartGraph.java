package pcm.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/*
 * This class is a tad redundant, perhaps can be elaborated upon in the future.
 */
public class LineChartGraph {
  public List<Double> x = new ArrayList<Double>(), y = new ArrayList<Double>();
  public String title = "", xLabel = "", yLabel = "";
  
  final NumberAxis xAxis = new NumberAxis();
  final NumberAxis yAxis = new NumberAxis();
  final LineChart<Number,Number> lineChart =  new LineChart<Number,Number>(xAxis,yAxis);

  /**
   * Creates a graph with the list x and y, which should be the same size
   * @param x
   * @param y
   * @param title Title of the graph
   * @param xLabel Label for x axis
   * @param yLabel Label for y axis
   */
  public void update(List<Double> x, List<Double> y, String title, String xLabel, String yLabel) {
    this.x = x;
    this.y = y;
    this.title = title;
    this.xLabel = xLabel;
    this.yLabel = yLabel;
    make();
  }
  
  public void make() {
      xAxis.setLabel(xLabel);
      yAxis.setLabel(yLabel);
      
      lineChart.setTitle(title);
      //defining a series
      XYChart.Series series = new XYChart.Series();
      //populating the series with data
      for (int i = 0; i < x.size(); i++) 
        series.getData().add(new XYChart.Data(x.get(i), y.get(i)));
      
      lineChart.getData().clear();
      lineChart.getData().add(series);
     
  }

}
