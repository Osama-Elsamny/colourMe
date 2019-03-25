package com.colourMe.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class serverGameConfigController {
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
        getUserName();
        getPenSize();
        getBoardSize();
        getCoverage();
        startScene(event, "lobby");
    }
    @FXML
    void goToMainScreen(ActionEvent event) throws IOException {
        startScene(event, "mainPage");
    }
    private void getUserName() {
        System.out.println(nameTF.getText());
    }
    private void getPenSize() {
        System.out.println(penSizeTF.getText());
    }
    private void getBoardSize() {
        System.out.println(boardSizeTF.getText());
    }
    private void getCoverage() {
        System.out.println(coverageTF.getText());
    }
    private void startScene(ActionEvent event, String fileName) throws  IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fileName + ".fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/" + fileName + ".css").toExternalForm());
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //primaryStage.hide(); might be needed
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
