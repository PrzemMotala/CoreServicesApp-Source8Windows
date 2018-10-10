package com.przemekm.coreservicesapp;

import com.przemekm.coreservicesapp.database.H2Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class contains methods which are used to start a JavaFX application.
 *
 * @author Przemysław Motała
 * @version 1.0
 * @see Application
 */
public final class Main extends Application {
    /**
     * This parameter defines the initial width of the created application.
     *
     * @see Scene
     */
    private static final int SCENE_WIDTH = 750;

    /**
     * This parameter defines the initial height of the created application.
     *
     * @see Scene
     */
    private static final int SCENE_HEIGHT = 800;

    @Override
    public void start(final Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass()
                .getResource("/mainWindow.fxml"));
        primaryStage.setTitle("Orders Manager Application");
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show();
    }

    /**
     * This is a main method. It is used to launch the application
     * with use of inherited {@link #launch(String...)} method.
     *
     * @param args arguments passed to the application.
     * @see #launch(String...)
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }

    /**
     * This method is called when the application is being closed.
     * <p>
     * The connection with the database is closed with use
     * of {@link H2Database#closeConnection()} method.
     *
     * @see H2Database#closeConnection()
     */
    @Override
    public void stop() {
        H2Database.getInstance().closeConnection();
    }
}
