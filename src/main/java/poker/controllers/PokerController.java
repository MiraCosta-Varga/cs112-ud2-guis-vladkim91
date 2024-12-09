package poker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import poker.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokerController implements PokerGameListener{

    @FXML
    private Label potLabel, playerLabel, ai1Label, ai2Label, ai3Label, ai4Label, ai5Label;

    @FXML
    private HBox playerCards, ai1Cards, ai2Cards, ai3Cards, ai4Cards, ai5Cards, communityCardsBox;

    @FXML
    private Button foldButton, callButton, raiseButton, betButton, dealButton;

    @FXML
    private TextField raiseAmountField;

    @FXML
    private StackPane tableContainer;

    private PokerGame pokerGame;
    private Player humanPlayer;
    private List<PokerAI> aiPlayers;


    @FXML
    public void initialize() {
        System.out.println("Initializing PokerController...");

        // Initialize the game without dealing cards
        initializeGame();

        // Ensure community cards and player cards are empty
        clearCardDisplays();

        // Ensure pot is displayed as $0
        potLabel.setText("Pot: $0");

        // Disable all action buttons initially
        disableHumanControls(); // Disable buttons at the start
        setupButtonActions(); // Link button actions to game logic

        System.out.println("Initialization complete. Fold, Call, and Raise buttons disabled.");
    }


    @Override
    public void onHumanTurn() {
        System.out.println("Enabling controls for human player.");
        enableHumanControls(); // Activate buttons for human input
    }

    public void enableHumanControls() {
        foldButton.setDisable(false);
        callButton.setDisable(false);
        raiseButton.setDisable(false);
    }

    public void disableHumanControls() {
        foldButton.setDisable(true);
        callButton.setDisable(true);
        raiseButton.setDisable(true);
    }

    private void setupButtonActions() {
        foldButton.setOnAction(e -> {
            pokerGame.handleFold(humanPlayer);
            disableHumanControls(); // Disable buttons after the action
            pokerGame.moveToNextPlayer(); // Resume game
        });

        callButton.setOnAction(e -> {
            pokerGame.handleCall(humanPlayer);
            disableHumanControls(); // Disable buttons after the action
            pokerGame.moveToNextPlayer(); // Resume game
        });

        raiseButton.setOnAction(e -> {
            try {
                int raiseAmount = Integer.parseInt(raiseAmountField.getText());
                pokerGame.handleRaise(humanPlayer, raiseAmount);
                disableHumanControls(); // Disable buttons after the action
                pokerGame.moveToNextPlayer(); // Resume game
            } catch (NumberFormatException ex) {
                System.out.println("Invalid raise amount entered.");
            }
        });
    }




    /**
     * Simulates getting a raise amount from the user. You can replace this with a dialog.
     * @return the raise amount (placeholder value for now).
     */
    private int getRaiseAmountFromUser() {
        // TODO: Replace this with actual user input logic (e.g., dialog box).
        return 50; // Placeholder value for testing
    }

    /**
     * Called when it's the human player's turn to enable controls.
     */
    public void handleHumanTurn() {
        System.out.println("Human player's turn. Enabling controls.");
        enableHumanControls(); // Enable buttons for the human player's action
    }




    private void updateButtonStates() {
        boolean isHumanTurn = pokerGame.getCurrentPlayer() == pokerGame.getHumanPlayer();
        foldButton.setDisable(!isHumanTurn);
        callButton.setDisable(!isHumanTurn);
        raiseButton.setDisable(!isHumanTurn);


    }


    private void clearCardDisplays() {
        playerCards.getChildren().clear();
        ai1Cards.getChildren().clear();
        ai2Cards.getChildren().clear();
        ai3Cards.getChildren().clear();
        ai4Cards.getChildren().clear();
        ai5Cards.getChildren().clear();
        communityCardsBox.getChildren().clear();
    }



    private void initializeGame() {
        // Create the human player
        humanPlayer = new Player("YOU", 1000);

        // Create AI players
        aiPlayers = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 5; i++) {
            aiPlayers.add(new PokerAI("AI Player " + i, 1000, random.nextInt(21) + 40, random.nextInt(21) + 40,
                    random.nextInt(21) + 40, random.nextInt(21) + 40));
        }

        // Initialize PokerGame
        pokerGame = new PokerGame(humanPlayer, aiPlayers, 10, 20);

        // Set the current player to the one after the Big Blind (but do not collect blinds yet)

    }




    public void nextPlayerTurn() {
        pokerGame.nextPlayerTurn();
        updateUI(); // Refresh UI to reflect the new current player
    }

    // Add these utility methods to resolve the errors
    private Label getAIPlayerLabel(int index) {
        switch (index) {
            case 0: return ai1Label;
            case 1: return ai2Label;
            case 2: return ai3Label;
            case 3: return ai4Label;
            case 4: return ai5Label;
            default: throw new IllegalArgumentException("Invalid AI index: " + index);
        }
    }



    private HBox getAICardBox(int index) {
        switch (index) {
            case 0: return ai1Cards;
            case 1: return ai2Cards;
            case 2: return ai3Cards;
            case 3: return ai4Cards;
            case 4: return ai5Cards;
            default: throw new IllegalArgumentException("Invalid AI index: " + index);
        }
    }


    private void updateUI() {
        // Update pot label
        potLabel.setText("Pot: $" + pokerGame.getPot());

        // Update player labels with dealer and chip info
        playerLabel.setText(humanPlayer.getName() + (pokerGame.isDealer(humanPlayer) ? " (D)" : "") + " - $" + humanPlayer.getChips());
        for (int i = 0; i < aiPlayers.size(); i++) {
            Label aiLabel = getAIPlayerLabel(i);
            PokerAI aiPlayer = aiPlayers.get(i);
            aiLabel.setText(aiPlayer.getName() + (pokerGame.isDealer(aiPlayer) ? " (D)" : "") + " - $" + aiPlayer.getChips());
        }

        // Update current player indicator
        updateCurrentPlayerIndicator();

        // Enable or disable human player buttons based on turn
        boolean isHumanTurn = pokerGame.getCurrentPlayer() == humanPlayer;
        foldButton.setDisable(!isHumanTurn);
        callButton.setDisable(!isHumanTurn);
        raiseButton.setDisable(!isHumanTurn);

        // Clear card displays
        clearCardDisplays();

        // Add human player's cards face-up
        for (Card card : humanPlayer.getCards()) {
            playerCards.getChildren().add(createCardFace(card));
        }

        // Add AI players' card backs
        for (int i = 0; i < aiPlayers.size(); i++) {
            for (int j = 0; j < 2; j++) {
                getAICardBox(i).getChildren().add(createCardBack());
            }
        }

        // Update community cards
        updateCommunityCards();
    }


    private void updateCurrentPlayerIndicator() {
        // Reset styles for all labels
        playerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: normal;");
        ai1Label.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");
        ai2Label.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");
        ai3Label.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");
        ai4Label.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");
        ai5Label.setStyle("-fx-font-size: 14px; -fx-font-weight: normal;");

        // Highlight the current player
        if (pokerGame.getCurrentPlayer() == humanPlayer) {
            playerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: blue;");
        } else if (pokerGame.getCurrentPlayer() == aiPlayers.get(0)) {
            ai1Label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");
        } else if (pokerGame.getCurrentPlayer() == aiPlayers.get(1)) {
            ai2Label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");
        } else if (pokerGame.getCurrentPlayer() == aiPlayers.get(2)) {
            ai3Label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");
        } else if (pokerGame.getCurrentPlayer() == aiPlayers.get(3)) {
            ai4Label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");
        } else if (pokerGame.getCurrentPlayer() == aiPlayers.get(4)) {
            ai5Label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: blue;");
        }

        // Ensure blinds and dealer annotations are still displayed
        updateBlinds();
    }




    private void updateBlinds() {
        // Reset all labels with default chip counts
        playerLabel.setText(String.format("YOU - $%d", humanPlayer.getChips()));
        ai1Label.setText(String.format("AI 1 - $%d", aiPlayers.get(0).getChips()));
        ai2Label.setText(String.format("AI 2 - $%d", aiPlayers.get(1).getChips()));
        ai3Label.setText(String.format("AI 3 - $%d", aiPlayers.get(2).getChips()));
        ai4Label.setText(String.format("AI 4 - $%d", aiPlayers.get(3).getChips()));
        ai5Label.setText(String.format("AI 5 - $%d", aiPlayers.get(4).getChips()));

        // Get the dealer position
        Player dealer = pokerGame.getDealer();
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.add(humanPlayer);
        allPlayers.addAll(aiPlayers);
        int dealerIndex = allPlayers.indexOf(dealer);

        // Update the dealer's label
        if (allPlayers.get(dealerIndex) == humanPlayer) {
            playerLabel.setText(String.format("YOU (D) - $%d", humanPlayer.getChips()));
        } else if (allPlayers.get(dealerIndex) == aiPlayers.get(0)) {
            ai1Label.setText(String.format("AI 1 (D) - $%d", aiPlayers.get(0).getChips()));
        } else if (allPlayers.get(dealerIndex) == aiPlayers.get(1)) {
            ai2Label.setText(String.format("AI 2 (D) - $%d", aiPlayers.get(1).getChips()));
        } else if (allPlayers.get(dealerIndex) == aiPlayers.get(2)) {
            ai3Label.setText(String.format("AI 3 (D) - $%d", aiPlayers.get(2).getChips()));
        } else if (allPlayers.get(dealerIndex) == aiPlayers.get(3)) {
            ai4Label.setText(String.format("AI 4 (D) - $%d", aiPlayers.get(3).getChips()));
        } else if (allPlayers.get(dealerIndex) == aiPlayers.get(4)) {
            ai5Label.setText(String.format("AI 5 (D) - $%d", aiPlayers.get(4).getChips()));
        }

        // Assign SB and BB
        int sbIndex = (dealerIndex + 1) % allPlayers.size(); // Next player after dealer
        int bbIndex = (dealerIndex + 2) % allPlayers.size(); // Player after SB

        if (allPlayers.get(sbIndex) == humanPlayer) {
            playerLabel.setText(String.format("YOU (SB) - $%d", humanPlayer.getChips()));
        } else if (allPlayers.get(sbIndex) == aiPlayers.get(0)) {
            ai1Label.setText(String.format("AI 1 (SB) - $%d", aiPlayers.get(0).getChips()));
        } else if (allPlayers.get(sbIndex) == aiPlayers.get(1)) {
            ai2Label.setText(String.format("AI 2 (SB) - $%d", aiPlayers.get(1).getChips()));
        } else if (allPlayers.get(sbIndex) == aiPlayers.get(2)) {
            ai3Label.setText(String.format("AI 3 (SB) - $%d", aiPlayers.get(2).getChips()));
        } else if (allPlayers.get(sbIndex) == aiPlayers.get(3)) {
            ai4Label.setText(String.format("AI 4 (SB) - $%d", aiPlayers.get(3).getChips()));
        } else if (allPlayers.get(sbIndex) == aiPlayers.get(4)) {
            ai5Label.setText(String.format("AI 5 (SB) - $%d", aiPlayers.get(4).getChips()));
        }

        if (allPlayers.get(bbIndex) == humanPlayer) {
            playerLabel.setText(String.format("YOU (BB) - $%d", humanPlayer.getChips()));
        } else if (allPlayers.get(bbIndex) == aiPlayers.get(0)) {
            ai1Label.setText(String.format("AI 1 (BB) - $%d", aiPlayers.get(0).getChips()));
        } else if (allPlayers.get(bbIndex) == aiPlayers.get(1)) {
            ai2Label.setText(String.format("AI 2 (BB) - $%d", aiPlayers.get(1).getChips()));
        } else if (allPlayers.get(bbIndex) == aiPlayers.get(2)) {
            ai3Label.setText(String.format("AI 3 (BB) - $%d", aiPlayers.get(2).getChips()));
        } else if (allPlayers.get(bbIndex) == aiPlayers.get(3)) {
            ai4Label.setText(String.format("AI 4 (BB) - $%d", aiPlayers.get(3).getChips()));
        } else if (allPlayers.get(bbIndex) == aiPlayers.get(4)) {
            ai5Label.setText(String.format("AI 5 (BB) - $%d", aiPlayers.get(4).getChips()));
        }
    }






    private ImageView createCardFace(Card card) {
        try {
            // Assuming card.getSuit() returns "H", "D", "C", "S" for Hearts, Diamonds, Clubs, Spades
            // and card.getRank() returns "A", "2", ..., "K"
            String suit = card.getSuit(); // e.g., "H"
            String rank = card.getRank(); // e.g., "A"

            if (!suit.matches("[HDSC]")) {
                throw new IllegalArgumentException("Invalid suit: " + suit);
            }

            if (!rank.matches("A|K|Q|J|T|[2-9]")) {
                throw new IllegalArgumentException("Invalid rank: " + rank);
            }

            // Construct file path based on naming convention (e.g., "AD.png" for Ace of Diamonds)
            String fileName = rank + suit + ".png";
            String imagePath = "/images/cards/" + fileName;

            // Load image
            Image cardImage = new Image(getClass().getResourceAsStream(imagePath));
            ImageView cardView = new ImageView(cardImage);
            cardView.setFitWidth(50); // Set card dimensions
            cardView.setFitHeight(75);
            return cardView;
        } catch (Exception e) {
            System.err.println("Error creating card face: " + e.getMessage());
            e.printStackTrace();
            return new ImageView(); // Return an empty ImageView on failure
        }
    }



    private ImageView createCardBack() {
        // Load the card back image
        Image cardBackImage = new Image(getClass().getResource("/images/cards/card-back.png").toExternalForm());

        // Create an ImageView to display the card back
        ImageView cardBack = new ImageView(cardBackImage);
        cardBack.setFitWidth(50);  // Set the desired width
        cardBack.setFitHeight(70); // Set the desired height
        cardBack.setPreserveRatio(true);

        return cardBack;
    }


    private void updateCommunityCards() {
        communityCardsBox.getChildren().clear();
        pokerGame.getCommunityCards().forEach(card -> {
            Label cardLabel = new Label(card.toString());
            cardLabel.setStyle("-fx-font-size: 24px; -fx-border-color: black; -fx-padding: 5px;");
            communityCardsBox.getChildren().add(cardLabel);
        });
    }

    @FXML
    public void handleDealButton() {
        pokerGame.startNewHand(); // Initialize a new hand

        // Collect blinds only after dealing
        pokerGame.collectBlinds();

        // Refresh the UI
        updateUI();
        updateButtonStates();

        // Hide the deal button after dealing
//        dealButton.setVisible(false);
        dealButton.setDisable(true);
    }




    @FXML
    private void fold() {
        pokerGame.handleFold(pokerGame.getHumanPlayer());
        updateButtonStates(); // Update buttons after human action
    }

    @FXML
    private void call() {
        pokerGame.handleCall(pokerGame.getHumanPlayer());
        updateButtonStates(); // Update buttons after human action
    }


    @FXML
    private void raise() {
        if (raiseButton.isDisabled()) return; // Ignore action if button is disabled
        raiseAmountField.setVisible(true);
        betButton.setVisible(true);
    }



    @FXML
    private void confirmRaise() {
        int raiseAmount = Integer.parseInt(raiseAmountField.getText());
        pokerGame.handleRaise(pokerGame.getHumanPlayer(), raiseAmount);
        updateButtonStates(); // Update buttons after human action
    }
}
