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

public class initializeGameController {

    @FXML
    private Label welcomeStatment;

    @FXML
    private Label nameLabel;

    @FXML
    private TextField nameTF;

    @FXML
    private Button nextButton;

    @FXML
    void getUserName(ActionEvent event) throws IOException {
        System.out.println(nameTF.getText());
        Parent root = FXMLLoader.load(getClass().getResource("lobby.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("lobby.css").toExternalForm());
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //primaryStage.hide(); might be needed
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
