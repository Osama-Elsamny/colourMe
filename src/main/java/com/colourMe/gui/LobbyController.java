package com.colourMe.gui;

import com.colourMe.common.gameState.*;
import com.colourMe.common.messages.Message;
import com.colourMe.networking.ClockSynchronization.Clock;
import com.colourMe.networking.client.GameClient;
import com.colourMe.networking.server.GameServer;
import com.google.gson.*;
import javafx.animation.AnimationTimer;
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

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class LobbyController {
    private final int COORDINATE_BUFFER_MAX_SIZE = 6;
    private final int COORDINATE_COUNTER_LIMIT = 3;

    // Counts the number of coordinates handled by ON_DRAG and sends every COORDINATE_BUFFER_LIMIT th Coordinate
    private String playerID;
    private String playerIP;
    private String serverAddress;
    private GameAPI gameAPI;
    private int coordinateCounter = 0;
    private int expectedPlayers = 3;
    private LinkedList<Coordinate> coordinateBuffer = new LinkedList<>();
    private Scene scene;
    Color userColor;
    long userColorCode;
    private GameServer gameServer;
    private GameClient gameClient;
    private Clock serverClock;
    private Clock clientClock;

    public static PriorityBlockingQueue<Message> receiveQueue;
    public static PriorityBlockingQueue<Message> sendQueue;


    private void createQueues() {
        // Create Queues
        Comparator<Message> messageComparator = (m1, m2) -> (int) (m1.getTimestamp() - m2.getTimestamp());

        receiveQueue = new PriorityBlockingQueue<>(100, messageComparator);
        sendQueue = new PriorityBlockingQueue<>(100, messageComparator);
    }

    public void startServer(Clock serverClock){
        gameServer = new GameServer(serverClock);
        gameServer.start();
        while (!gameServer.isRunning()){
            // Wait for server to start
        }
    }

    public void initServerMachine(GameConfig gameConfig, String networkIP, String playerID) {
        serverClock = new Clock();
        serverClock.start();
        startServer(serverClock);
        gameServer.initGameService(gameConfig);
        String serverAddress = String.format("ws://%s:8080/connect/%s", networkIP, playerID);
        initClientMachine(serverAddress, playerID, networkIP);
    }

    public void initClientMachine(String serverAddress, String playerID, String playerIP) {
        System.out.println("Player IP: " + playerIP);
        this.playerID = playerID;
        this.playerIP = playerIP;
        createQueues();

        clientClock = new Clock();
        clientClock.start();

        gameClient = new GameClient(receiveQueue, sendQueue, serverAddress, playerID, clientClock);
        gameClient.start();

        setGameAPI(sendQueue, receiveQueue);
        JsonObject data = new JsonObject();
        gameAPI.sendConnectRequest(playerID, playerIP);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    public void startNextServer() {
        try {
            GameService gameService = gameAPI.getGameService();
            GameService serverGameService = gameService.clone();

            serverClock = new Clock();
            serverClock.start();
            startServer(serverClock);
            this.gameServer.initGameService(serverGameService);

            String serverAddress = String.format("ws://%s:8080/connect/%s", "127.0.0.1", playerID);
            gameClient = new GameClient(receiveQueue, sendQueue, serverAddress, playerID, clientClock);
            gameClient.start();
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void waitForNextServer(String nextIP) {
        try {Thread.sleep(5000);} catch (Exception ex) {} // Wait for next server to be started
        String serverAddress = String.format("ws://%s:8080/connect/%s", nextIP, playerID);
        System.out.println("Next IP: " + serverAddress);
        this.gameClient = new GameClient(receiveQueue, sendQueue, serverAddress, playerID, clientClock);
        this.gameClient.start();
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
        Label playersNameLabel[] = new Label[4];
        Label playersScoreLabel[] = new Label[4];
        AnchorPane playersAnchorPane[] = new AnchorPane[4];
        inilizeAnchorPaneArray(playersAnchorPane);
        inilizeLabelArrays(playersNameLabel);
        inilizeLabelArrays(playersScoreLabel);
        displayPlayerNamesAndScores(playersNameLabel, playersScoreLabel);
        VBox vbox = new VBox();
        Stage primaryStage = MainPageController.getPrimaryStage();
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
        addToCssFile(welcomeLabel, leftAnchorPane, topAnchorPane, vbox, playersAnchorPane);
        setComponentHeightAndWidth(welcomeLabel, leftAnchorPane, topAnchorPane, vbox, playersAnchorPane);
        //adding Label in the AnchorPanes so we can display the scores and the player names
        setAnchorPaneChilderen(playersAnchorPane, playersNameLabel, playersScoreLabel);
        setXandYforLabels(playersNameLabel, playersScoreLabel);
        // adding AnchorPanes to Vbox
        setVerticalBoxChildren(vbox, playersAnchorPane);
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

    private void setVerticalBoxChildren(VBox vBox, AnchorPane playersAnchorPane[]) {
        for (int i = 0; i < 4; i++) {
            vBox.getChildren().add(playersAnchorPane[i]);
        }
    }

    private void setAnchorPaneChilderen(AnchorPane playersAnchorPane[], Label playersNameLabel[], Label playersScoreLabel[]) {
        for (int i = 0; i < 4; i++) {
            playersAnchorPane[i].getChildren().add(playersNameLabel[i]);
            playersAnchorPane[i].getChildren().add(playersScoreLabel[i]);
        }
    }

    private void inilizeAnchorPaneArray(AnchorPane arr[]) {
        for (int i = 0; i < 4; i++) {
            arr[i] = new AnchorPane();
        }
    }

    private void inilizeLabelArrays(Label arr[]) {
        for (int i = 0; i < 4; i++) {
            arr[i] = new Label();
        }
    }

    private void displayPlayerNamesAndScores(Label playersNameLabel[], Label playersScoreLabel[]) {
        int size = gameAPI.getPlayerIds().size();
        for (int i = 0; i < size; i++) {
            String playerID = gameAPI.getPlayerIds().get(i);
            playersNameLabel[i].setText(playerID);
            playersScoreLabel[i].setText("0");
            playersScoreLabel[i].setId("score-" + playerID);
        }
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
                handleConnect(data, response.getPlayerID(), response.getTimestamp());
                break;
            case GetCellResponse:
                break;
            case CellUpdateResponse:
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
            case ReconnectResponse:
                handleReconnect(data);
                break;
            case ClockSyncResponse:
                handleClockSync(data);
                break;
            case DefaultType:
                // TODO: Handle errors if you have any
            default:
        }
    }

    private void handleConnect(JsonObject data, String userID, long TimeStamp) {

        Boolean successful = data.get("successful").getAsBoolean();
        int numPlayers = gameAPI.getNumOfPlayers();

        if (!successful && (userID.equals(this.playerID))) {
            // Resend connection request.
            gameAPI.sendConnectRequest(this.playerID, this.playerIP);
        } else if (successful && (numPlayers == expectedPlayers)) {
            clientClock.setTime(TimeStamp);
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
            List<Coordinate> coordinates =
                    Arrays.asList(gson.fromJson(data.get("coordinates"), Coordinate[].class));
            GraphicsContext cellGraphicsContext = getGraphicsContext("canvas-" + row + "-" + col);
            coordinates.forEach(x -> renderStroke(cellGraphicsContext, x, userID, row, col));
        }
    }

    private void handleCellRelease(JsonObject data, String userID) {
        // Color the cell or make it empty based on hasColoured property for the cell.
        boolean success = data.get("successful").getAsBoolean();
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
        updatePlayersScore(userID);
    }

    private void handleDisconnect(JsonObject data) {
        try {
            PopUpWindow window = new PopUpWindow();
            window.display("ColourMe",
                    Arrays.asList("Connecting to the server, please wait ..."), true);
            boolean startServer = data.get("startServer").getAsBoolean();
            String nextIP = data.get("nextIP").getAsString();
            if (startServer) {
                startNextServer();
            } else {
                waitForNextServer(nextIP);
            }
        } catch(Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void handleReconnect(JsonObject data) {
        if (data.get("successful").getAsBoolean()) {
            Gson gson = gameAPI.getGameService().gson;
            gameAPI.setGameService(gson.fromJson(data, GameService.class));
            restoreCellStates();
            updateScores();
        }
    }

    private void restoreCellStates() {
        Cell[][] cells = gameAPI.getGameService().getCells();
        for (int r=0; r < cells.length; r++) {
            for (int c=0; c < cells.length; c++) {
                if (cells[r][c].getState().equals(CellState.AVAILABLE)) {
                    GraphicsContext graphicsContext = getGraphicsContext(String.format("canvas-%s-%s", r, c));
                    clearCell(graphicsContext);
                }
            }
        }
    }

    private void updateScores() {
        gameAPI.getPlayerIds().forEach(this::updatePlayersScore);
    }

    private void handleClientDisconnect(JsonObject data) {
        // Show disconnected label on the gui
    }

    private void handleClockSync(JsonObject data) {
        long serverTime = data.get("TimeStamp").getAsLong();
        clientClock.setTime(serverTime);
    }

    private void addToCssFile(Label welcomeLabel, AnchorPane leftAnchorPane, AnchorPane topAnchorPane, VBox vbox, AnchorPane playersAnchorPane[]) {
        //add id for the css file
        vbox.getStyleClass().add("vbox");
        welcomeLabel.getStyleClass().add("welcomeLabel");
        leftAnchorPane.getStyleClass().add("LeftAnchorPane");
        topAnchorPane.getStyleClass().add("topAnchorPane");
    }

    private void setComponentHeightAndWidth(Label welcomeLabel, AnchorPane leftAnchorPane, AnchorPane topAnchorPane,VBox vbox, AnchorPane playersAnchorPane[]) {
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
        for (int i = 0; i < 4; i++) {
            playersAnchorPane[i].setPrefHeight(200);
            playersAnchorPane[i].setPrefWidth(200);
        }
    }

    private Node lookup(String id) {
        return scene.lookup("#" + id);
    }

    private GraphicsContext getGraphicsContext(String canvasID){
        Canvas canvas = (Canvas) lookup(canvasID);
        return canvas.getGraphicsContext2D();
    }

    private void setXandYforLabels(Label playersNameLabel[], Label playersScoreLabel[]) {
        for (int i = 0; i < 4; i++) {
            playersNameLabel[i].setLayoutX(14);
            playersNameLabel[i].setLayoutY(24);
            playersScoreLabel[i].setLayoutX(56);
            playersScoreLabel[i].setLayoutY(24);
        }
    }

    private void updatePlayersScore(String userID) {
        int score = gameAPI.getPlayerScore(userID);
        Label scoreToUpdate = (Label) lookup("score-" + userID);
        scoreToUpdate.setText(Integer.toString(score));
    }
}
