package com.colourMe.gui;

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

public class clientGameConfigController {

    @FXML
    private Label welcomeStatment;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTF;
    @FXML
    private Label ipAddressLabel;
    @FXML
    private TextField ipAddressTF;
    @FXML
    private Button nextButton;
    @FXML
    private Button backButton;

    @FXML
    void getGameConfigInput(ActionEvent event) throws IOException {
        String playerID = getPlayerID();
        String serverIP = String.format("ws://%s:8080/connect/%s", getIPAddress(), playerID);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/lobby.fxml"));
        Parent root = (Parent) loader.load();
        lobbyController controller = loader.getController();
        controller.initClientMachine(serverIP, playerID, "127.0.0.1");
        startScene(event, "lobby");
    }
    @FXML
    void goToMainScreen(ActionEvent event) throws IOException{
        startScene(event, "mainPage");
    }
    private String getPlayerID() {
        return nameTF.getText();
    }
    private String getIPAddress() {
        return ipAddressTF.getText();
    }
    private void startScene(ActionEvent event, String fileName) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fileName + ".fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/" + fileName + ".css").toExternalForm());
        Stage primaryStage = mainPageController.getPrimaryStage();
        //primaryStage.hide(); might be needed
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

