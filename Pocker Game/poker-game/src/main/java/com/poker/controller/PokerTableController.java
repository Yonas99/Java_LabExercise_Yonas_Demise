package com.poker.controller;

import com.poker.game.GameManager;
import com.poker.model.BotPlayer;
import com.poker.model.Card;
import com.poker.model.Player;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class PokerTableController {
    
    @FXML
    private Pane tablePane;
    
    @FXML
    private Label potLabel;
    
    @FXML
    private Label phaseLabel;
    
    @FXML
    private Label currentTurnLabel;
    
    @FXML
    private Button foldButton;
    
    @FXML
    private Button checkButton;
    
    @FXML
    private Button callButton;
    
    @FXML
    private Button raiseButton;
    
    @FXML
    private Button allInButton;
    
    @FXML
    private Button menuButton;
    
    @FXML
    private Button pauseButton;
    
    @FXML
    private StackPane communityCardsContainer;
    
    private GameManager gameManager;
    private List<Player> players;
    private List<PlayerUI> playerUIs;
    private boolean isProcessingAction;
    
    @FXML
    private void initialize() {
        playerUIs = new ArrayList<>();
        isProcessingAction = false;

        foldButton.setOnAction(e -> handleFold());
        checkButton.setOnAction(e -> handleCheck());
        callButton.setOnAction(e -> handleCall());
        raiseButton.setOnAction(e -> handleRaise());
        allInButton.setOnAction(e -> handleAllIn());
        menuButton.setOnAction(e -> handleMenu());
        pauseButton.setOnAction(e -> handlePause());

        styleActionButtons();
    }

    public void initializePlayers(List<Player> players) {
        this.players = players;

        this.gameManager = new GameManager(players, 10, 20);

        createPlayerUIs();

        startNewHand();
    }

    private void createPlayerUIs() {
        tablePane.getChildren().clear();
        playerUIs.clear();

        double centerX = 640;
        double centerY = 360;
        double radius = 250;

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);

            double angle = (2 * Math.PI * i) / players.size() - Math.PI / 2;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            PlayerUI playerUI = new PlayerUI(player, x, y);
            playerUIs.add(playerUI);

            tablePane.getChildren().addAll(playerUI.getAllNodes());
        }

        tablePane.getChildren().add(communityCardsContainer);
        communityCardsContainer.setLayoutX(centerX - 150);
        communityCardsContainer.setLayoutY(centerY - 50);
    }

    private void startNewHand() {
        gameManager.startNewHand();
        updateUI();

        if (getCurrentPlayer().isBot()) {
            handleBotTurn();
        }
    }

    private Player getCurrentPlayer() {
        return gameManager.getCurrentPlayer();
    }

    private void updateUI() {
        potLabel.setText("Pot: $" + gameManager.getPotSize());
        phaseLabel.setText("Phase: " + gameManager.getCurrentPhase());

        Player currentPlayer = getCurrentPlayer();
        currentTurnLabel.setText(currentPlayer.getName() + "'s turn");

        for (PlayerUI playerUI : playerUIs) {
            playerUI.update();
        }

        updateCommunityCards();

        updateActionButtons();

        if (gameManager.isHandComplete()) {
            handleHandComplete();
        }
    }

    private void updateCommunityCards() {
        communityCardsContainer.getChildren().clear();

        List<Card> communityCards = gameManager.getCommunityCards();
        for (int i = 0; i < 5; i++) {
            StackPane cardPane = new StackPane();
            cardPane.setPrefSize(60, 90);

            if (i < communityCards.size()) {
                Card card = communityCards.get(i);
                Rectangle cardRect = new Rectangle(60, 90);
                cardRect.setStyle("-fx-fill: white; -fx-stroke: #333; -fx-stroke-width: 2;");

                String cardText = card.getRank().getSymbol() + card.getSuit().getSymbol();
                Text cardLabel = new Text(cardText);
                cardLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

                if (card.getSuit() == Card.Suit.HEARTS || card.getSuit() == Card.Suit.DIAMONDS) {
                    cardLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #e74c3c;");
                } else {
                    cardLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: #2c3e50;");
                }

                cardPane.getChildren().addAll(cardRect, cardLabel);

                FadeTransition ft = new FadeTransition(Duration.millis(300), cardPane);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            } else {
                Rectangle cardRect = new Rectangle(60, 90);
                cardRect.setStyle("-fx-fill: #2c3e50; -fx-stroke: #95a5a6; -fx-stroke-width: 2;");
                cardPane.getChildren().add(cardRect);
            }
            
            cardPane.setLayoutX(i * 65);
            communityCardsContainer.getChildren().add(cardPane);
        }
    }

    private void updateActionButtons() {
        Player currentPlayer = getCurrentPlayer();
        boolean isBot = currentPlayer.isBot();

        foldButton.setDisable(isBot);
        checkButton.setDisable(isBot);
        callButton.setDisable(isBot);
        raiseButton.setDisable(isBot);
        allInButton.setDisable(isBot);

        if (!isBot) {
            checkButton.setDisable(!gameManager.getBettingManager().canCheck(currentPlayer));
            callButton.setDisable(gameManager.getBettingManager().getCallAmount(currentPlayer) == 0);
        }
    }

    private void handleBotTurn() {
        Player botPlayer = getCurrentPlayer();
        if (!(botPlayer instanceof BotPlayer)) {
            return;
        }

        new Thread(() -> {
            try {
                Thread.sleep(1000 + (long)(Math.random() * 2000));

                Platform.runLater(() -> {
                    BotPlayer bot = (BotPlayer) botPlayer;
                    int callAmount = gameManager.getBettingManager().getCallAmount(botPlayer);
                    int potSize = gameManager.getPotSize();

                    BotPlayer.BettingDecision decision = bot.makeDecision(callAmount, potSize, botPlayer.getCurrentBet());

                    switch (decision.getAction()) {
                        case FOLD:
                            gameManager.processAction(Player.Action.FOLD, 0);
                            break;
                        case CHECK:
                            gameManager.processAction(Player.Action.CHECK, 0);
                            break;
                        case CALL:
                            gameManager.processAction(Player.Action.CALL, 0);
                            break;
                        case RAISE:
                            gameManager.processAction(Player.Action.RAISE, decision.getAmount());
                            break;
                    }
                    
                    updateUI();

                    if (getCurrentPlayer().isBot() && !gameManager.isHandComplete()) {
                        handleBotTurn();
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @FXML
    private void handleFold() {
        if (isProcessingAction) return;
        isProcessingAction = true;
        
        gameManager.processAction(Player.Action.FOLD, 0);
        updateUI();
        
        isProcessingAction = false;

        if (getCurrentPlayer().isBot() && !gameManager.isHandComplete()) {
            handleBotTurn();
        }
    }

    @FXML
    private void handleCheck() {
        if (isProcessingAction) return;
        isProcessingAction = true;
        
        gameManager.processAction(Player.Action.CHECK, 0);
        updateUI();
        
        isProcessingAction = false;

        if (getCurrentPlayer().isBot() && !gameManager.isHandComplete()) {
            handleBotTurn();
        }
    }

    @FXML
    private void handleCall() {
        if (isProcessingAction) return;
        isProcessingAction = true;
        
        gameManager.processAction(Player.Action.CALL, 0);
        updateUI();
        
        isProcessingAction = false;

        if (getCurrentPlayer().isBot() && !gameManager.isHandComplete()) {
            handleBotTurn();
        }
    }

    @FXML
    private void handleRaise() {
        if (isProcessingAction) return;
        isProcessingAction = true;
        
        int raiseAmount = gameManager.getBettingManager().getMinRaise();
        gameManager.processAction(Player.Action.RAISE, raiseAmount);
        updateUI();
        
        isProcessingAction = false;

        if (getCurrentPlayer().isBot() && !gameManager.isHandComplete()) {
            handleBotTurn();
        }
    }

    @FXML
    private void handleAllIn() {
        if (isProcessingAction) return;
        isProcessingAction = true;
        
        gameManager.processAction(Player.Action.ALL_IN, 0);
        updateUI();
        
        isProcessingAction = false;

        if (getCurrentPlayer().isBot() && !gameManager.isHandComplete()) {
            handleBotTurn();
        }
    }

    @FXML
    private void handleMenu() {
        System.out.println("Menu button clicked");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Game");
        alert.setHeaderText("Exit Game?");
        alert.setContentText("Are you sure you want to exit the game?");

        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                returnToMainMenu();
            }
        });
    }

    private void returnToMainMenu() {
        System.exit(0);
    }

    @FXML
    private void handlePause() {
        if (gameManager.isPaused()) {
            gameManager.resume();
            pauseButton.setText("Pause");
        } else {
            gameManager.pause();
            pauseButton.setText("Resume");
        }
    }

    private void handleHandComplete() {
        List<Player> winners = gameManager.determineWinners();

        String winnerText = winners.size() == 1 ?
            winners.get(0).getName() + " wins $" + gameManager.getPotSize() :
            "Split pot: " + winners.stream().map(Player::getName).reduce((a, b) -> a + ", " + b).orElse("");

        currentTurnLabel.setText(winnerText);

        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(() -> startNewHand());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private void styleActionButtons() {
        String foldStyle = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;";
        String checkStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;";
        String callStyle = "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;";
        String raiseStyle = "-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;";
        String allInStyle = "-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;";
        
        foldButton.setStyle(foldStyle);
        checkButton.setStyle(checkStyle);
        callButton.setStyle(callStyle);
        raiseButton.setStyle(raiseStyle);
        allInButton.setStyle(allInStyle);
    }

    private class PlayerUI {
        private Player player;
        private Circle avatar;
        private Label nameLabel;
        private Label chipsLabel;
        private Label betLabel;
        private Circle dealerButton;

        public PlayerUI(Player player, double x, double y) {
            this.player = player;

            avatar = new Circle(40);
            avatar.setStyle("-fx-fill: " + (player.isBot() ? "#95a5a6" : "#3498db") + "; -fx-stroke: white; -fx-stroke-width: 3;");
            avatar.setCenterX(x);
            avatar.setCenterY(y);

            nameLabel = new Label(player.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
            nameLabel.setLayoutX(x - 40);
            nameLabel.setLayoutY(y - 60);

            chipsLabel = new Label("$" + player.getChips());
            chipsLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-size: 16px; -fx-font-weight: bold;");
            chipsLabel.setLayoutX(x - 30);
            chipsLabel.setLayoutY(y + 50);

            betLabel = new Label("Bet: $" + player.getCurrentBet());
            betLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 12px;");
            betLabel.setLayoutX(x - 35);
            betLabel.setLayoutY(y + 70);

            dealerButton = new Circle(15);
            dealerButton.setStyle("-fx-fill: white; -fx-stroke: #f1c40f; -fx-stroke-width: 2;");
            dealerButton.setCenterX(x + 50);
            dealerButton.setCenterY(y - 40);
            dealerButton.setVisible(player.isDealer());

            if (player == getCurrentPlayer()) {
                avatar.setStyle("-fx-fill: #f39c12; -fx-stroke: #f1c40f; -fx-stroke-width: 4;");
            }
        }
        
        public void update() {
            chipsLabel.setText("$" + player.getChips());
            betLabel.setText("Bet: $" + player.getCurrentBet());
            dealerButton.setVisible(player.isDealer());

            if (player == getCurrentPlayer()) {
                avatar.setStyle("-fx-fill: #f39c12; -fx-stroke: #f1c40f; -fx-stroke-width: 4;");
            } else if (player.isFolded()) {
                avatar.setStyle("-fx-fill: #7f8c8d; -fx-stroke: #95a5a6; -fx-stroke-width: 2;");
            } else {
                avatar.setStyle("-fx-fill: " + (player.isBot() ? "#95a5a6" : "#3498db") + "; -fx-stroke: white; -fx-stroke-width: 3;");
            }
        }
        
        public java.util.List<javafx.scene.Node> getAllNodes() {
            java.util.List<javafx.scene.Node> nodes = new ArrayList<>();
            nodes.add(avatar);
            nodes.add(nameLabel);
            nodes.add(chipsLabel);
            nodes.add(betLabel);
            nodes.add(dealerButton);
            return nodes;
        }
    }
}