package com.colourMe.gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.LinkedList;

public class lobbyController {
    @FXML
    private Label welcomeStatment;

    @FXML
    private Button startGame;

    private final int COORDINATE_BUFFER_MAX_SIZE = 8;
    private final int COORDINATE_COUNTER_LIMIT = 4;
    // Counts the number of coordinates handled by ON_DRAG and sends every COORDINATE_BUFFER_LIMIT th Coordinate
    private int coordinateCounter = 0;
    private int requestCounter = 0; // Counts number of requests made per cell colouring
    private LinkedList<Coordinate> coordinateBuffer = new LinkedList<>();

    Color userColor = Color.BLUE;
    long userColorCode = -16776961;

    private StackPane createCell(BooleanProperty cellSwitch) {
        StackPane cell = new StackPane();
        Canvas cellCanvas = new Canvas();
        // could use the following 2 lines to set it to the parent width
        cellCanvas.widthProperty().bind(cell.widthProperty());
        cellCanvas.heightProperty().bind(cell.heightProperty());
        final GraphicsContext graphicsContext = cellCanvas.getGraphicsContext2D();
        initDraw(graphicsContext);
        cellCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                initCounters();
                graphicsContext.beginPath();
                graphicsContext.moveTo(event.getX(), event.getY());
                coordinateBuffer.add(new Coordinate(event.getX(), event.getY()));
//                graphicsContext.stroke();
            }
        });

        cellCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                graphicsContext.lineTo(event.getX(), event.getY());
                addCoordinateToQueue(event, graphicsContext);
                graphicsContext.stroke();
//              graphicsContext.closePath();
//              graphicsContext.beginPath();
//              graphicsContext.moveTo(event.getX(), event.getY());
            }
        });

        cellCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                double canvasWidth = graphicsContext.getCanvas().getWidth();
                double canvasHeight = graphicsContext.getCanvas().getHeight();
                double totalPixels = 0;
                double colorCount = 0;
                graphicsContext.lineTo(event.getX(), event.getY());
                graphicsContext.stroke();
                graphicsContext.closePath();
                WritableImage snap = graphicsContext.getCanvas().snapshot(null, null);
                for(int i = 0; i < canvasWidth; i++){
                    for(int j = 0; j < canvasHeight; j++){
                        //System.out.println(snap.getPixelReader().getArgb(i,j));
                        if(snap.getPixelReader().getArgb(i,j) == userColorCode){
                            colorCount++;
                        }
                    }
                }
                totalPixels = canvasHeight * canvasWidth;
                System.out.println(totalPixels);
                System.out.println(colorCount);
                if(colorCount/totalPixels > 0.95){
                    graphicsContext.setFill(userColor);
                    graphicsContext.fillRect(0,0, canvasWidth, canvasHeight);
                }else{
                    graphicsContext.clearRect(0,0, canvasWidth, canvasHeight);
                    initDraw(graphicsContext);
                }
            }
        });
        cell.getChildren().add(cellCanvas);
        cell.getStyleClass().add("cell");
        return cell;
    }

    private GridPane createGrid(BooleanProperty[][] switches) {

        int numCols = switches.length ;
        int numRows = switches[0].length ;

        GridPane grid = new GridPane();

        for (int x = 0 ; x < numCols ; x++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(cc);
        }

        for (int y = 0 ; y < numRows ; y++) {
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }

        for (int x = 0 ; x < numCols ; x++) {
            for (int y = 0 ; y < numRows ; y++) {
                grid.add(createCell(switches[x][y]), x, y);
            }
        }

        grid.getStyleClass().add("grid");
        return grid;
    }

    private void initDraw(GraphicsContext gc){
//        double canvasWidth = gc.getCanvas().getWidth();
//        double canvasHeight = gc.getCanvas().getHeight();
//        gc.setStroke(Color.BLACK);
//        gc.setLineWidth(5);
//        gc.strokeRect(0, 0, canvasWidth, canvasHeight);
        gc.setStroke(userColor);
        gc.setLineWidth(10);
    }

    // Called in Mouse OnClick
    private void initCounters(){
        requestCounter = 0;
        coordinateCounter = 0;
    }

    // Called in Mouse OnDrag
    private void addCoordinateToQueue(MouseEvent event, GraphicsContext gc){
        coordinateCounter++;
        if (coordinateBuffer.size() <= COORDINATE_BUFFER_MAX_SIZE
            && coordinateCounter > COORDINATE_COUNTER_LIMIT
            && event.getX() > 0 && event.getX() < gc.getCanvas().getWidth()
            && event.getY() > 0 && event.getY() < gc.getCanvas().getHeight()){

            coordinateCounter = 0;
            coordinateBuffer.add(new Coordinate(event.getX(), event.getY()));
        }
        if (coordinateBuffer.size() > COORDINATE_BUFFER_MAX_SIZE){
            while(! coordinateBuffer.isEmpty()) {
                coordinateCounter = 0;
                requestCounter++;
                coordinateBuffer.remove();
                System.out.println("Coordinate buffer emptied");
                System.out.println("Request #: " + requestCounter);
            }
            // TODO: Add coordinates to send buffer
        }
    }

    @FXML
    void displayBoard(ActionEvent event) throws IOException {
        int numCols = 5 ;
        int numRows = 5 ;
        BorderPane root = new BorderPane();
        Label welcomeLabel = new Label("ColourMe");
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        BooleanProperty[][] switches = new BooleanProperty[numCols][numRows];
        for (int x = 0 ; x < numCols ; x++) {
            for (int y = 0 ; y < numRows ; y++) {
                switches[x][y] = new SimpleBooleanProperty();
            }
        }
        GridPane grid = createGrid(switches);
        AnchorPane leftAnchorPane = new AnchorPane();
        AnchorPane topAnchorPane = new AnchorPane();
        //Setting the anchor
        AnchorPane.setTopAnchor(welcomeLabel, 0.0);
        AnchorPane.setLeftAnchor(welcomeLabel, 0.0);
        AnchorPane.setRightAnchor(welcomeLabel, 0.0);
        AnchorPane.setBottomAnchor(welcomeLabel, 0.0);
        //add id for the css file
        welcomeLabel.getStyleClass().add("welcomeLabel");
        leftAnchorPane.getStyleClass().add("LeftAnchorPane");
        leftAnchorPane.getStyleClass().add("topAnchorPane");
        //Height and width for label
        welcomeLabel.setPrefHeight(55);
        welcomeLabel.setPrefWidth(602);
        //Height and width for anchor
        leftAnchorPane.setPrefWidth(112);
        leftAnchorPane.setPrefHeight(345);
        //Height and width for anchor
        topAnchorPane.setPrefWidth(600);
        topAnchorPane.setPrefHeight(64);
        topAnchorPane.getChildren().add(welcomeLabel);
        root.setTop(topAnchorPane);
        root.setCenter(grid);
        root.setRight(leftAnchorPane);
        BorderPane.setAlignment(topAnchorPane, Pos.CENTER);
        BorderPane.setAlignment(leftAnchorPane, Pos.CENTER);
        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("grid-with-borders.css").toExternalForm());
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
