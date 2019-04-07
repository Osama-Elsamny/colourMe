package com.colourMe.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.util.List;

public class PopUpWindow {
    public Stage display(String title, List<String> message, boolean canExit) {
        Stage window = new Stage();
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
//        window.showAndWait();

        Platform.setImplicitExit(false);

        window.setOnCloseRequest(event -> {
            if (!canExit) {
                event.consume();
            }
        });

        window.show();
        return window;
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
