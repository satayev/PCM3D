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
    protected Rectangle background;
    private CSNode head;
    public static ShapePane dragPreview;
    
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
            nodes[i].layoutXProperty().set(origNodes.get(i).layoutXProperty().get());
            nodes[i].layoutYProperty().set(origNodes.get(i).layoutYProperty().get());
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].prev = i == 0 ? nodes[nodes.length - 1] : nodes[i - 1];
            nodes[i].next = i == nodes.length - 1 ? nodes[0] : nodes[i + 1];
        }
        
        ShapePane copy = new ShapePane(nodes[0], 0.0d, false);
        copy.rotateProperty().set(rotateProperty().doubleValue());
        copy.setFill(Color.BLUE);
        return copy;
    }
    
    private void setShape(CSNode firstNode) {
        head = firstNode;
        CSNode curr = firstNode;
        do {
            getChildren().add(new Line(curr.getLayoutX(), curr.getLayoutY(),
                    curr.next.getLayoutX(), curr.next.getLayoutY()));
            curr = curr.next;
        } while (curr != firstNode);
    }
    
    private void setBackgroundOpacity(double opacity) {
        background.opacityProperty().set(opacity);
    }
    
    public void clear() {
        getChildren().clear();
        getChildren().add(background);
    }

    public void setFill(Paint fill) {
        background.fillProperty().set(fill);
    }
    public Paint getFill() {
        return background.fillProperty().get();
    }
    public ObjectProperty<Paint> fill() {
        return background.fillProperty();
    }
}
