package com.colourMe;

import com.colourMe.common.util.Log;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException{
        Log.initLogging();
        Log.setDefaultLevel(Level.INFO);
        Parent root = FXMLLoader.load(getClass().getResource("/mainPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/mainPage.css").toExternalForm());
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}