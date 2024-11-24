package poker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import poker.models.*;

import java.net.URL;
import java.util.List;

public class PokerController {

    @FXML
    private ImageView pokerTableImage;

    @FXML
    private HBox communityCards, aiPlayer1Cards, aiPlayer2Cards, playerCardsBox;

    @FXML
    private HBox aiPlayersBox;

    @FXML
    private Label potLabel;

    @FXML
    private Button startButton, foldButton, callButton, raiseButton;

    @FXML
    private TextField raiseAmountField;

    private PokerGame pokerGame;
    private Player humanPlayer;

    @FXML
    public void initialize() {
        pokerTableImage.setImage(new Image(getClass().getResource("/images/poker-table.png").toString()));// Set poker table background

        // Initialize players
        humanPlayer = new Player("Human", 1000);

// Use BasicPokerAI to create AI players
        PokerAI aiPlayer1 = new BasicPokerAI("AI_1", 1000, 50, 50, 50, 50);
        PokerAI aiPlayer2 = new BasicPokerAI("AI_2", 1000, 60, 40, 30, 70);

// Initialize the poker game
        pokerGame = new PokerGame(humanPlayer, List.of(aiPlayer1, aiPlayer2), 10, 20);

// Disable controls initially
        togglePlayerControls(false);

    }

    @FXML
    private void startGame() {
        pokerGame.playHand();
        updateUI();
        togglePlayerControls(true); // Enable controls after dealing
    }

    @FXML
    private void playerFold() {
        pokerGame.getHumanPlayer().fold();
        updateUI();
    }

    @FXML
    private void playerCall() {
        pokerGame.getHumanPlayer().call(pokerGame.getCurrentBet());
        updateUI();
    }

    @FXML
    private void playerRaise() {
        try {
            int raiseAmount = Integer.parseInt(raiseAmountField.getText());
            pokerGame.getHumanPlayer().raise(raiseAmount);
            pokerGame.addToPot(raiseAmount);
            updateUI();
        } catch (NumberFormatException e) {
            showError("Invalid raise amount. Please enter a valid number.");
        }
    }



    private void togglePlayerControls(boolean enable) {
        foldButton.setDisable(!enable);
        callButton.setDisable(!enable);
        raiseButton.setDisable(!enable);
    }

    private void updateUI() {
        // Update pot
        potLabel.setText("Pot: $" + pokerGame.getPot());

        // Update community cards
        communityCards.getChildren().clear();
        for (Card card : pokerGame.getCommunityCards()) {
            communityCards.getChildren().add(createCardImage(card));
        }

        // Update AI players
        aiPlayer1Cards.getChildren().clear();
        for (Card card : pokerGame.getAiPlayers().get(0).getHoleCards()) {
            aiPlayer1Cards.getChildren().add(createCardBack());
        }

        aiPlayer2Cards.getChildren().clear();
        for (Card card : pokerGame.getAiPlayers().get(1).getHoleCards()) {
            aiPlayer2Cards.getChildren().add(createCardBack());
        }

        // Update human player cards
        playerCardsBox.getChildren().clear();
        for (Card card : pokerGame.getHumanPlayer().getHoleCards()) {
            playerCardsBox.getChildren().add(createCardImage(card));
        }
    }

    private StackPane createCardImage(Card card) {
        StackPane cardPane = new StackPane();
        String imagePath = "/images/cards/" + card.toString() + ".png";
        URL resource = getClass().getResource(imagePath);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + imagePath);
        }
        ImageView cardImage = new ImageView(new Image(resource.toString()));
        cardImage.setFitWidth(80);
        cardImage.setFitHeight(120);
        cardPane.getChildren().add(cardImage);
        return cardPane;
    }


    private StackPane createCardBack() {
        StackPane cardPane = new StackPane();
        ImageView cardImage = new ImageView(new Image(
                getClass().getResource("/images/cards/card-back.png").toString()));
        cardImage.setFitWidth(80);
        cardImage.setFitHeight(120);
        cardPane.getChildren().add(cardImage);
        return cardPane;
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
