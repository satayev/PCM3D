package pcm.gui;

import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class ShapePane extends AnchorPane {
    public CSNode head;
    private Rectangle background;
    public static ShapePane dragPreview;
    public double rotation = 0.0d, scale = 100.0d, height = 200.0d;
    
    public ShapePane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ShapePane.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        background = (Rectangle) getChildren().get(0);
        background.widthProperty().bind(widthProperty());
        background.heightProperty().bind(heightProperty());
    }
    
    public ShapePane(CSNode firstNode, double backgroundOpacity, boolean draggable) {
        this();
        setShape(firstNode);
        setBackgroundOpacity(backgroundOpacity);
        
        if (draggable) {
            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
                    Dragboard db = startDragAndDrop(TransferMode.ANY);
                    ClipboardContent content = new ClipboardContent();
                    content.putString("");
                    db.setContent(content);
                    dragPreview = copy();
                    t.consume();
                }
            });
        }
    }
    
    public void scale(double s) {
        double oldScale = scale;
        scale = s;
        s /= oldScale;
        oldScale *= 0.5d;
        double halfScale = scale * 0.5d;
        
        CSNode curr = head;
        do {
            double x = curr.getLayoutX() - oldScale;
            double y = curr.getLayoutY() - oldScale;
            
            curr.setLayoutX(x * s + halfScale);
            curr.setLayoutY(y * s + halfScale);
            
            curr = curr.next;
        } while (curr != head);
        
        resetShape();
    }
    
    public void rotate(double theta) {
        double oldRotation = rotation;
        theta *= Math.PI / 180.0d;
        rotation = theta;
        theta -= oldRotation;
        
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        
        double halfScale = scale * 0.5d;
        
        CSNode curr = head;
        do {
            double x = curr.getLayoutX() - halfScale;
            double y = curr.getLayoutY() - halfScale;
            
            curr.setLayoutX(x * cos + y * sin + halfScale);
            curr.setLayoutY(-x * sin + y * cos + halfScale);
            
            minX = Math.min(minX, curr.getLayoutX());
            minY = Math.min(minY, curr.getLayoutY());
            maxX = Math.max(maxX, curr.getLayoutX());
            maxY = Math.max(maxY, curr.getLayoutY());
            
            curr = curr.next;
        } while (curr != head);
        
        double realScale = scale;
        scale = Math.max(maxX - minX, maxY - minY);
        scale(realScale);
        
        resetShape();
    }

    public ShapePane copy() {
        ArrayList<CSNode> origNodes = new ArrayList<CSNode>();
        
        CSNode curr = head;
        do {
            origNodes.add(curr);
            curr = curr.next;
        } while (curr != head);
        
        CSNode[] nodes = new CSNode[origNodes.size()];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new CSNode(null, null);
            nodes[i].setLayoutX(origNodes.get(i).getLayoutX());
            nodes[i].setLayoutY(origNodes.get(i).getLayoutY());
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].prev = i == 0 ? nodes[nodes.length - 1] : nodes[i - 1];
            nodes[i].next = i == nodes.length - 1 ? nodes[0] : nodes[i + 1];
        }
        
        ShapePane copy = new ShapePane(nodes[0], 0.0d, false);
//        copy.background.setFill(Color.BLUE);
//        copy.background.setOpacity(0.1d);
        return copy;
    }
    
    private void resetShape() {
        clear();
        setShape(head);
    }
    
    private void setShape(CSNode firstNode) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        head = firstNode;
        CSNode curr = firstNode;
        do {
            getChildren().add(new Line(curr.getLayoutX(), curr.getLayoutY(),
                    curr.next.getLayoutX(), curr.next.getLayoutY()));
            
            minX = Math.min(minX, curr.getLayoutX());
            minY = Math.min(minY, curr.getLayoutY());
            maxX = Math.max(maxX, curr.getLayoutX());
            maxY = Math.max(maxY, curr.getLayoutY());
            
            curr = curr.next;
        } while (curr != firstNode);
        
        curr = head;
        do {
            curr.setLayoutX(curr.getLayoutX() - minX);
            curr.setLayoutY(curr.getLayoutY() - minY);
            setWidth(maxX - minX);
            setHeight(maxY - minY);
            
            curr = curr.next;
        } while (curr != firstNode);
    }
    
    public void clear() {
        getChildren().clear();
        getChildren().add(background);
    }

    private void setBackgroundOpacity(double opacity) {
        background.setOpacity(opacity);
    }
    public void setFill(Paint fill) {
        background.setFill(fill);
    }
    public Paint getFill() {
        return background.getFill();
    }
    public ObjectProperty<Paint> fill() {
        return background.fillProperty();
    }
}
