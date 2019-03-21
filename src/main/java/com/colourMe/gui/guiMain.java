package com.colourMe.gui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class guiMain extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("mainPage.css").toExternalForm());
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}