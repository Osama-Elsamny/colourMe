package com.colourMe.gui;

import com.colourMe.common.gameState.GameConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;

public class ServerGameConfigController {
    @FXML
    private Label welcomeStatment;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTF;
    @FXML
    private Label penSizeLabel;
    @FXML
    private TextField penSizeTF;
    @FXML
    private Label boardSizeLabel;
    @FXML
    private TextField boardSizeTF;
    @FXML
    private TextField coverageTF;
    @FXML
    private Label coverageLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;

    @FXML
    void getGameConfigInput(ActionEvent event) throws IOException {
        String serverIP = InetAddress.getLocalHost().getHostAddress().trim();
        String playerID = getPlayerID().trim();
        int thickness = getThickness();
        int boardSize = getBoardSize();
        float ratio = getRatio();
        GameConfig gameConfig = new GameConfig(boardSize, ratio, thickness);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
        Parent root = (Parent) loader.load();
        LobbyController controller = loader.getController();
        controller.initServerMachine(gameConfig, serverIP, playerID);
        startScene(event, "lobby");
    }

    @FXML
    void goToMainScreen(ActionEvent event) throws IOException {
        startScene(event, "mainPage");
    }
    private String getPlayerID() {
        return nameTF.getText();
    }
    private int getThickness() {
        return Integer.parseInt(penSizeTF.getText());
    }
    private int getBoardSize() {
        return Integer.parseInt(boardSizeTF.getText());
    }
    private float getRatio() {
        return Float.parseFloat(coverageTF.getText());
    }
    private void startScene(ActionEvent event, String fileName) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fileName + ".fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/" + fileName + ".css").toExternalForm());
        Stage primaryStage = MainPageController.getPrimaryStage();
        //primaryStage.hide(); might be needed
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
