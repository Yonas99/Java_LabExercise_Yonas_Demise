package com.poker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.poker.controller.PokerTableController;
import com.poker.model.Player;
import com.poker.model.BotPlayer;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game_table.fxml"));
            Parent root = loader.load();

            PokerTableController controller = loader.getController();

            List<Player> players = new ArrayList<>();
            players.add(new Player("Human", 1000, false));
            players.add(new BotPlayer("Bot 1", 1000));
            players.add(new BotPlayer("Bot 2", 1000));
            players.add(new BotPlayer("Bot 3", 1000));

            controller.initializePlayers(players);
            
            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/css/poker_style.css").toExternalForm());
            
            primaryStage.setTitle("Poker Game");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
            System.out.println("Poker Game started successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading game table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}