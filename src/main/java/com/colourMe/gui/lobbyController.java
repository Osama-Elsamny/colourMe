package com.colourMe.gui;

import com.colourMe.common.gameState.Coordinate;
import com.colourMe.common.gameState.GameConfig;
import com.colourMe.common.messages.Message;
import com.colourMe.common.messages.MessageType;
import com.colourMe.networking.client.GameClient;
import com.colourMe.networking.server.GameServer;
import com.google.gson.*;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.BooleanExpression;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Pair;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class lobbyController {
    @FXML
    private Label welcomeStatment;
    @FXML
    private Button startGame;
    @FXML
    private Label promptLabel;
    @FXML
    private Label player1Label;
    @FXML
    private Label player2Label;
    @FXML
    private Label player3Label;
    @FXML
    private Label player4Label;

    private final int COORDINATE_BUFFER_MAX_SIZE = 6;
    private final int COORDINATE_COUNTER_LIMIT = 3;
    // Counts the number of coordinates handled by ON_DRAG and sends every COORDINATE_BUFFER_LIMIT th Coordinate
    private String playerID;
    private String playerIP;
    private String serverAddress;
    private GameAPI gameAPI;
    private int coordinateCounter = 0;
    private int expectedPlayers = 2;
    private LinkedList<Coordinate> coordinateBuffer = new LinkedList<>();
    private Scene scene;
    Color userColor;
    long userColorCode;
    private GameServer gameServer;
    private GameClient gameClient;

    public static PriorityBlockingQueue<Message> receiveQueue;
    public static PriorityBlockingQueue<Message> sendQueue;


    private void createQueues() {
        // Create Queues
        Comparator<Message> messageComparator = (m1, m2) -> (int) (m1.getTimestamp() - m2.getTimestamp());

        receiveQueue = new PriorityBlockingQueue<>(100, messageComparator);
        sendQueue = new PriorityBlockingQueue<>(100, messageComparator);
    }

    public void initServerMachine(GameConfig gameConfig, String networkIP, String playerID) {
        // Start Server
        gameServer = new GameServer();
        gameServer.start();
        while (!gameServer.isRunning()){
            // Wait for server to start
        }
        gameServer.initGameService(gameConfig);
        String serverAddress = String.format("ws://%s:8080/connect/%s", networkIP, playerID);
        // Start Client
        initClientMachine(serverAddress, playerID, networkIP);
    }

    public void initClientMachine(String serverAddress, String playerID, String playerIP) {
        this.playerID = playerID;
        this.playerIP = playerIP;
        createQueues();
        gameClient = new GameClient(receiveQueue, sendQueue, serverAddress, playerID);
        gameClient.start();
        setGameAPI(sendQueue, receiveQueue);
        JsonObject data = new JsonObject();
        data.addProperty("playerIP", playerIP);

        Message connectMessage = new Message(MessageType.ConnectRequest, data ,playerID);
        sendQueue.put(connectMessage);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    private void setGameAPI(PriorityBlockingQueue<Message> sendQueue,
                            PriorityBlockingQueue<Message> receivedQueue) {
        this.gameAPI = new GameAPI(sendQueue, receivedQueue);
    }

    private StackPane createCell(BooleanProperty cellSwitch, int colNum, int rowNum) {
        StackPane cell = new StackPane();
        Canvas cellCanvas = new Canvas();
        cellCanvas.setId("canvas-" + rowNum + "-" + colNum);
        // could use the following 2 lines to set it to the parent width
        cellCanvas.widthProperty().bind(cell.widthProperty());
        cellCanvas.heightProperty().bind(cell.heightProperty());
        final GraphicsContext graphicsContext = cellCanvas.getGraphicsContext2D();
        initDraw(graphicsContext, this.playerID);
        cellCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                onClick(event, rowNum, colNum);
            }
        });
        cellCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                onDrag(graphicsContext, event, rowNum, colNum);
            }
        });
        cellCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                onRelease(graphicsContext, event, rowNum, colNum);
            }
        });
        cell.getChildren().add(cellCanvas);
        cell.getStyleClass().add("cell");
        return cell;
    }
    private void onClick(MouseEvent event, int row, int col){
        initCounters();
        Coordinate coordinate = new Coordinate(event.getX(), event.getY());
        gameAPI.sendGetCellRequest(playerID, row, col, coordinate);
    }

    private void onDrag(GraphicsContext graphicsContext, MouseEvent event, int row, int col){
        Coordinate coordinate = new Coordinate(event.getX(), event.getY());
        renderStroke(graphicsContext, coordinate, playerID, row, col);
        addCoordinateToQueue(event, graphicsContext, row, col);
    }

    private void onRelease(GraphicsContext graphicsContext, MouseEvent event, int row, int col){
        Coordinate coordinate = new Coordinate(event.getX(), event.getY());
        renderStroke(graphicsContext, coordinate, playerID, row, col);
        if(gameAPI.isCellLocked(row, col)) {
            boolean hasColoured = colourCellIfConquered(graphicsContext);
            gameAPI.sendReleaseCellRequest(playerID, row, col, hasColoured);
        }
    }

    private boolean colourCellIfConquered(GraphicsContext graphicsContext) {
        double totalPixels;
        double colorCount;
        float ratio = gameAPI.getRatio();
        double canvasWidth = graphicsContext.getCanvas().getWidth();
        double canvasHeight = graphicsContext.getCanvas().getHeight();

        totalPixels = canvasHeight * canvasWidth;
        colorCount = computePixelsColoured(graphicsContext);

        boolean hasColoured = colorCount/totalPixels > ratio;
        if (hasColoured) {
            colourCell(graphicsContext, userColor);
        } else {
            clearCell(graphicsContext);
        }
        return hasColoured;
    }

    private int computePixelsColoured (GraphicsContext graphicsContext) {
        int colorCount = 0;
        double canvasWidth = graphicsContext.getCanvas().getWidth();
        double canvasHeight = graphicsContext.getCanvas().getHeight();

        WritableImage snap = graphicsContext.getCanvas().snapshot(null, null);
        for (int i = 0; i < canvasWidth; i++) {
            for (int j = 0; j < canvasHeight; j++) {
                if (snap.getPixelReader().getArgb(i,j) == userColorCode){
                    colorCount++;
                }
            }
        }
        return colorCount;
    }

    private void colourCell(GraphicsContext graphicsContext, Color color){
        double height = graphicsContext.getCanvas().getHeight();
        double width = graphicsContext.getCanvas().getWidth();
        graphicsContext.setFill(color);
        graphicsContext.fillRect(0,0, width, height);
    }

    private void clearCell(GraphicsContext graphicsContext){
        double height = graphicsContext.getCanvas().getHeight();
        double width = graphicsContext.getCanvas().getWidth();
        graphicsContext.clearRect(0,0, width, height);
        initDraw(graphicsContext, playerID);
    }

    private void renderStroke(GraphicsContext graphicsContext, Coordinate coordinate,
                               String playerID, int row, int col) {
        if (gameAPI.playerOwnsCell(row, col, playerID)) {
            initDraw(graphicsContext, playerID);
            graphicsContext.beginPath();
            graphicsContext.moveTo(coordinate.x, coordinate.y);
            graphicsContext.lineTo(coordinate.x, coordinate.y);
            graphicsContext.stroke();
            graphicsContext.closePath();
        }
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
                grid.add(createCell(switches[x][y], x , y), x, y);
            }
        }

        grid.getStyleClass().add("grid");
        return grid;
    }

    private void initDraw(GraphicsContext gc, String playerID){
        int thickness = gameAPI.getThickness();
        gc.setStroke(gameAPI.getPlayerColour(playerID));
        gc.setLineWidth(thickness);
    }

    // Called in Mouse OnClick
    private void initCounters(){
        coordinateCounter = 0;
    }

    // Called in Mouse OnDrag
    private void addCoordinateToQueue(MouseEvent event, GraphicsContext gc, int row, int col){
        coordinateCounter++;
        if (coordinateBuffer.size() <= COORDINATE_BUFFER_MAX_SIZE
            && coordinateCounter > COORDINATE_COUNTER_LIMIT
            && event.getX() > 0 && event.getX() < gc.getCanvas().getWidth()
            && event.getY() > 0 && event.getY() < gc.getCanvas().getHeight()){

            coordinateCounter = 0;
            coordinateBuffer.add(new Coordinate(event.getX(), event.getY()));
        }
        if (coordinateBuffer.size() > COORDINATE_BUFFER_MAX_SIZE) {
            gameAPI.sendCellUpdateRequest(playerID, row, col, coordinateBuffer);
            while(! coordinateBuffer.isEmpty()) {
                coordinateCounter = 0;
                coordinateBuffer.remove();
            }
            coordinateCounter++;
        }
    }

    @FXML
    private void displayBoard() {
        this.userColor = gameAPI.getPlayerColour(playerID);
        this.userColorCode = gameAPI.getPlayerColourCode(playerID);

        int numCols = gameAPI.getBoardSize();
        int numRows = gameAPI.getBoardSize();
        BorderPane root = new BorderPane();
        Label welcomeLabel = new Label("ColourMe");
        AnchorPane player1AnchorPane = new AnchorPane();
        Label player1NameLabel = new Label("Player1");
        Label player1ScoreLabel = new Label("score");
        AnchorPane player2AnchorPane = new AnchorPane();
        Label player2NameLabel = new Label("Player2");
        Label player2ScoreLabel = new Label("score2");
        AnchorPane player3AnchorPane = new AnchorPane();
        Label player3NameLabel = new Label("Player3");
        Label player3ScoreLabel = new Label("score3");
        AnchorPane player4AnchorPane = new AnchorPane();
        Label player4NameLabel = new Label("Player4");
        Label player4ScoreLabel = new Label("score4");
        VBox vbox = new VBox();

        Stage primaryStage = mainPageController.getPrimaryStage();
        BooleanProperty[][] switches = new BooleanProperty[numCols][numRows];
        for (int x = 0 ; x < numCols ; x++) {
            for (int y = 0 ; y < numRows ; y++) {
                switches[x][y] = new SimpleBooleanProperty();
            }
        }
        GridPane grid = createGrid(switches);
        AnchorPane leftAnchorPane = new AnchorPane();
        AnchorPane topAnchorPane = new AnchorPane();
        //Setting the top anchor (welcomeLabel)
        AnchorPane.setTopAnchor(welcomeLabel, 0.0);
        AnchorPane.setLeftAnchor(welcomeLabel, 0.0);
        AnchorPane.setRightAnchor(welcomeLabel, 0.0);
        AnchorPane.setBottomAnchor(welcomeLabel, 0.0);
        //Setting the top anchor (welcomeLabel)
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
        addToCssFile(welcomeLabel, leftAnchorPane, topAnchorPane, vbox, player1AnchorPane, player2AnchorPane, player3AnchorPane, player4AnchorPane);
        setComponentHeightAndWidth(welcomeLabel, leftAnchorPane, topAnchorPane, vbox, player1AnchorPane, player2AnchorPane, player3AnchorPane, player4AnchorPane);
        //adding Label in the AnchorPanes so we can display the scores and the player names
        player1AnchorPane.getChildren().add(player1NameLabel);
        player2AnchorPane.getChildren().add(player2NameLabel);
        player3AnchorPane.getChildren().add(player3NameLabel);
        player4AnchorPane.getChildren().add(player4NameLabel);
        player1AnchorPane.getChildren().add(player1ScoreLabel);
        player2AnchorPane.getChildren().add(player2ScoreLabel);
        player3AnchorPane.getChildren().add(player3ScoreLabel);
        player4AnchorPane.getChildren().add(player4ScoreLabel);
        setXandYforLabels(player1NameLabel, player1ScoreLabel, player2NameLabel, player2ScoreLabel, player3NameLabel, player3ScoreLabel, player4NameLabel, player4ScoreLabel);
        // adding AnchorPanes to Vbox
        vbox.getChildren().add(player1AnchorPane);
        vbox.getChildren().add(player2AnchorPane);
        vbox.getChildren().add(player3AnchorPane);
        vbox.getChildren().add(player4AnchorPane);
        //adding labels to AnchorPane
        topAnchorPane.getChildren().add(welcomeLabel);
        leftAnchorPane.getChildren().add(vbox);
        //Alignment for the boarder Pane
        root.setTop(topAnchorPane);
        root.setCenter(grid);
        root.setRight(leftAnchorPane);
        BorderPane.setAlignment(topAnchorPane, Pos.CENTER);
        BorderPane.setAlignment(leftAnchorPane, Pos.CENTER);
        scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(getClass().getResource("/grid.css").toExternalForm());
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void update(){
        if(gameAPI.hasResponse()) {
            // processResponse()
            Message response = gameAPI.processResponse();

            // updateGUI()
            renderResponse(response);
        }
    }

    private void renderResponse(Message response) {
        JsonObject data = response.getData().getAsJsonObject();
        switch(response.getMessageType()) {
            case ConnectResponse:
                handleConnect(data, response.getPlayerID());
                break;
            case GetCellResponse:
                break;
            case CellUpdate:
                handleCellUpdate(data, response.getPlayerID());
                break;
            case ReleaseCellResponse:
                handleCellRelease(data, response.getPlayerID());
                break;
            case ClientDisconnectResponse:
                handleClientDisconnect(data);
                break;
            case Disconnect:
                handleDisconnect(data);
                break;
            case DefaultType:
                // TODO: Handle errors if you have any
            default:
        }
    }

    private void handleConnect(JsonObject data, String userID) {

        Boolean successful = data.get("successful").getAsBoolean();
        int numPlayers = gameAPI.getNumOfPlayers();

        if (!successful && (userID.equals(this.playerID))) {
            // Resend connection request.
            gameAPI.sendConnectRequest(this.playerID, this.playerIP);
        } else if (successful && (numPlayers == expectedPlayers)) {
            displayBoard();
        } else {
            // Not enough players to begin.
            // Do Nothing.
        }
    }


    private void handleCellUpdate(JsonObject data, String userID) {
        // Render other clients' coordinates
        Gson gson = new Gson();
        Boolean success = data.get("successful").getAsBoolean();

        if (success && (! userID.equals(this.playerID))){
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            JsonParser parser = new JsonParser();
            String string = data.get("coordinates").toString();
            string = string.replaceAll("\\\\\"(\\w+)\\\\\"(:)", "$1$2");
            string = string.substring(1, string.length()-1);
            List<Coordinate> coordinates = Arrays.asList(gson.fromJson(string, Coordinate[].class));
            GraphicsContext cellGraphicsContext = getGraphicsContext("canvas-" + row + "-" + col);
            coordinates.forEach(x -> renderStroke(cellGraphicsContext, x, userID, row, col));
        }
    }

    private void handleCellRelease(JsonObject data, String userID) {
        // Color the cell or make it empty based on hasColoured property for the cell.
        Boolean success = data.get("successful").getAsBoolean();

        if (success && (! userID.equals(playerID))){
            int row = data.get("row").getAsInt();
            int col = data.get("col").getAsInt();
            boolean hasColoured = data.get("hasColoured").getAsBoolean();
            Color otherUserColor = gameAPI.getPlayerColour(userID);

            GraphicsContext cellGraphicsContext = getGraphicsContext("canvas-" + row + "-" + col);

            if (hasColoured) {
                colourCell(cellGraphicsContext, otherUserColor);
            } else {
                clearCell(cellGraphicsContext);
            }
        }
    }

    private void handleDisconnect(JsonObject data) {
        // Show reconnecting
    }

    private void handleClientDisconnect(JsonObject data) {
        // Show disconnected label on the gui
    }

    void setPlayer1LabelAsJoined(String name){
        player1Label.setText(name + " joined");
    }
    void setPlayer2LabelAsJoined(String name){
        player2Label.setText(name + " joined");
    }
    void setPlayer3LabelAsJoined(String name){
        player3Label.setText(name + " joined");
    }
    void setPlayer4LabelAsJoined(String name){
        player4Label.setText(name + " joined");
    }
    private void addToCssFile(Label welcomeLabel, AnchorPane leftAnchorPane, AnchorPane topAnchorPane, VBox vbox, AnchorPane player1AnchorPane, AnchorPane player2AnchorPane, AnchorPane player3AnchorPane, AnchorPane player4AnchorPane) {
        //add id for the css file
        vbox.getStyleClass().add("vbox");
        player1AnchorPane.getStyleClass().add("player1AnchorPane");
        player2AnchorPane.getStyleClass().add("player2AnchorPane");
        player3AnchorPane.getStyleClass().add("player3AnchorPane");
        player4AnchorPane.getStyleClass().add("player4AnchorPane");
        welcomeLabel.getStyleClass().add("welcomeLabel");
        leftAnchorPane.getStyleClass().add("LeftAnchorPane");
        topAnchorPane.getStyleClass().add("topAnchorPane");
    }
    private void setComponentHeightAndWidth(Label welcomeLabel, AnchorPane leftAnchorPane, AnchorPane topAnchorPane,VBox vbox, AnchorPane player1AnchorPane, AnchorPane player2AnchorPane, AnchorPane player3AnchorPane, AnchorPane player4AnchorPane) {
        //Height and width for welcome label
        welcomeLabel.setPrefHeight(55);
        welcomeLabel.setPrefWidth(602);
        //Height and width for left anchor
        leftAnchorPane.setPrefWidth(112);
        leftAnchorPane.setPrefHeight(345);
        //Height and width for top anchor
        topAnchorPane.setPrefWidth(600);
        topAnchorPane.setPrefHeight(64);
        //Height and width for each player Anchor Pane
        player1AnchorPane.setPrefWidth(200);
        player1AnchorPane.setPrefHeight(200);
        player2AnchorPane.setPrefWidth(200);
        player2AnchorPane.setPrefHeight(200);
        player3AnchorPane.setPrefWidth(200);
        player3AnchorPane.setPrefHeight(200);
        player4AnchorPane.setPrefWidth(200);
        player4AnchorPane.setPrefHeight(200);
    }
    private Node lookup(String id){
        return scene.lookup("#" + id);
    }

    private GraphicsContext getGraphicsContext(String canvasID){
        Canvas canvas = (Canvas) lookup(canvasID);
        return canvas.getGraphicsContext2D();
    }
    private void setXandYforLabels(Label player1NameLabel, Label player1ScoreLabel, Label player2NameLabel, Label player2ScoreLabel, Label player3NameLabel, Label player3ScoreLabel, Label player4NameLabel, Label player4ScoreLabel) {
        player1NameLabel.setLayoutX(14);
        player1NameLabel.setLayoutY(24);
        player1ScoreLabel.setLayoutX(56);
        player1ScoreLabel.setLayoutY(24);
        player2NameLabel.setLayoutX(14);
        player2NameLabel.setLayoutY(24);
        player2ScoreLabel.setLayoutX(56);
        player2ScoreLabel.setLayoutY(24);
        player3NameLabel.setLayoutX(14);
        player3NameLabel.setLayoutY(24);
        player3ScoreLabel.setLayoutX(56);
        player3ScoreLabel.setLayoutY(24);
        player4NameLabel.setLayoutX(14);
        player4NameLabel.setLayoutY(24);
        player4ScoreLabel.setLayoutX(56);
        player4ScoreLabel.setLayoutY(24);
    }
}
