package com.colourMe.gui;

import com.colourMe.common.util.Log;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.Callable;

public class PopUpWindow {
    private Stage window = null;
    private String title;
    private List<String> message;
    private boolean canExit;

    public PopUpWindow(String title, List<String> message, boolean canExit) {
        this.window = new Stage();
        this.title = title;
        this.message = message;
        this.canExit = canExit;
        build();
    }

    public void build() {
        int size = message.size();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);
        window.setMinHeight(250);

        Label label[] = new Label[size];
        setLabelText(label, message);
        VBox vbox = new VBox(10);
        addChildrenToVbox(label, vbox, size);
        vbox.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(vbox);
        window.setScene(scene);

        Platform.setImplicitExit(false);

        window.setOnCloseRequest(event -> {
            if (!canExit) {
                event.consume();
            }
        });
    }

    public void display() {
        Log.get(this).info("Showing PopUp Window");
        window.show();
    }

    public void displayAndWait() {
        window.showAndWait();
    }
    public void close(){
        Log.get(this).info("Closing PopUp Window");
        if (window != null) {
            this.window.close();
            this.window = null;
        }
    }

    private void setLabelText(Label arr[], List<String> message) {
        int size = message.size();
        for (int i = 0; i < size; i++) {
            arr[i] = new Label(message.get(i));
        }
    }

    private void addChildrenToVbox(Label arr[], VBox vBox, int size) {
        for (int i = 0; i < size; i++) {
            vBox.getChildren().add(arr[i]);
        }
    }
}
