package com.colourMe;
import com.colourMe.networking.server.GameServerEndpoint;
import org.glassfish.tyrus.server.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("/mainPage.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/mainPage.css").toExternalForm());
        primaryStage.setTitle("ColourMe");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
        Server server = new Server("localhost", 8080, "",
                null, GameServerEndpoint.class);
        try{
            server.start();
            System.out.println("Server has started!");
            Thread.sleep(60000);
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            server.stop();
        }
    }
}