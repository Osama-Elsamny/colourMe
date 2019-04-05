package com.colourMe.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {

    private static Stage primaryStage;
    @FXML
    public void goToServerGameConfig(ActionEvent event) throws IOException{
        startScene(event, "serverGameConfig");
    }
    @FXML
    void goToClientGameConfig(ActionEvent event) throws IOException {
        startScene(event, "clientGameConfig");
    }
    @FXML
    void exitGame(ActionEvent event) throws IOException {
        System.exit(0);
    }
    private void startScene(ActionEvent event, String fileName) throws  IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/" + fileName + ".fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/" + fileName + ".css").toExternalForm());
        primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        //primaryStage.hide(); might be needed
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
