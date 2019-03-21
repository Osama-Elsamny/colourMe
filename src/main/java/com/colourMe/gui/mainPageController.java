package com.colourMe.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class mainPageController{
    @FXML
    public void goToLobby(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("initializeGame.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("initializeGame.css").toExternalForm());
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //primaryStage.hide(); might be needed
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
