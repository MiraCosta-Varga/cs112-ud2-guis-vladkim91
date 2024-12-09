package poker.models;

import javafx.scene.control.TextInputDialog;

import java.util.ArrayList;
import java.util.List;


import java.util.Optional;

public class PokerGame {
    private List<Card> communityCards;
    private Player humanPlayer;
    private List<PokerAI> aiPlayers;
    private Deck deck;
    private int pot;
    private int smallBlind;
    private int bigBlind;
    private Player dealer;
    private int currentBet;
    private Player currentPlayer;
    private PokerGameListener listener; // Listener for UI interactions

    public PokerGame(Player humanPlayer, List<PokerAI> aiPlayers, int smallBlind, int bigBlind) {
        this.humanPlayer = humanPlayer;
        this.aiPlayers = aiPlayers;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.deck = new Deck();
        this.pot = 0;
        this.currentBet = 0;
        this.communityCards = new ArrayList<>();
        this.dealer = aiPlayers.get(aiPlayers.size() - 1); // Initial dealer
        this.currentPlayer = humanPlayer; // Start with the human player
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isDealer(Player player) {
        return dealer.equals(player);
    }

    public int getPot() {
        return pot;
    }

    public void nextPlayerTurn() {
        moveToNextPlayer();
    }


    // Add a listener for UI communication
    public void setListener(PokerGameListener listener) {
        this.listener = listener;
    }

    public void revealFlop() {
        communityCards.add(deck.deal());
        communityCards.add(deck.deal());
        communityCards.add(deck.deal());
    }

    public void revealTurn() {
        communityCards.add(deck.deal());
    }

    public void revealRiver() {
        communityCards.add(deck.deal());
    }

    public void endHand() {
        determineWinner();
        resetPlayers();
        startNewHand();
    }

    private void resetPlayers() {
        for (Player player : getAllPlayers()) {
            player.resetForNewHand();
        }
        communityCards.clear();
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public void setCurrentPlayer(Player player) {
        System.out.println("Setting current player to: " + player.getName());
        this.currentPlayer = player;
    }

    public void startNewHand() {
        // Reset deck and pot
        deck.shuffle();
        pot = 0;
        currentBet = 0;

        // Move dealer position to the next player
        moveDealer();

        // Deal cards to all players
        dealCards();

        // Set the current player to the one after BB
        setCurrentPlayerAfterBB();
    }

    public void setCurrentPlayerAfterBB() {
        List<Player> allPlayers = getAllPlayers();

        // Big Blind player acts last in the first betting round
        Player bigBlindPlayer = getNextPlayer(getNextPlayer(dealer));

        // First player to act after Big Blind
        Player firstToAct = getNextPlayer(bigBlindPlayer);

        // Ensure the current player is set to the human player if it's their turn
        setCurrentPlayer(firstToAct);
    }

    private int getPlayerPosition(Player player) {
        List<Player> allPlayers = getAllPlayers();
        int dealerIndex = allPlayers.indexOf(dealer);
        int playerIndex = allPlayers.indexOf(player);

        if (playerIndex == -1) {
            throw new IllegalArgumentException("Player not found in the game.");
        }

        // Calculate the relative position of the player with respect to the dealer
        if (playerIndex >= dealerIndex) {
            return playerIndex - dealerIndex;
        } else {
            return allPlayers.size() - dealerIndex + playerIndex;
        }
    }

    private void aiPlayerTakeAction(PokerAI aiPlayer) {
        System.out.println("AI " + aiPlayer.getName() + " is deciding...");
        String action = aiPlayer.decideAction(currentBet, pot, getPlayerPosition(aiPlayer), communityCards);

        switch (action) {
            case "fold":
                handleFold(aiPlayer);
                break;
            case "call":
                handleCall(aiPlayer);
                break;
            case "raise":
                int raiseAmount = aiPlayer.decideRaiseAmount(pot);
                handleRaise(aiPlayer, raiseAmount);
                break;
        }

        moveToNextPlayer();
    }
    public void playBettingRound() {
        System.out.println("Starting betting round...");

        while (!isRoundOver()) {
            if (currentPlayer.equals(humanPlayer)) {
                System.out.println("Human player's turn. Pausing for input...");
                if (listener != null) {
                    listener.onHumanTurn(); // Notify UI to wait for input
                }
                return; // Pause execution here for human input
            }

            if (currentPlayer instanceof PokerAI) {
                System.out.println("AI's turn: " + currentPlayer.getName());
                aiPlayerTakeAction((PokerAI) currentPlayer); // Let AI take action
            }
        }

        System.out.println("Betting round ended.");
    }




    private void notifyHumanTurn() {
        if (listener != null) {
            listener.onHumanTurn();
        }
    }
    public void moveToNextPlayer() {
        if (isRoundOver()) {
            System.out.println("Round is over. Determining winner...");
            determineWinner();
            return; // Stop further execution
        }

        currentPlayer = getNextPlayer(currentPlayer); // Advance to the next player
        System.out.println("Next player is: " + currentPlayer.getName());

        if (currentPlayer.isFolded()) {
            System.out.println(currentPlayer.getName() + " is folded. Skipping...");
            moveToNextPlayer(); // Skip folded players
            return;
        }

        if (currentPlayer.equals(humanPlayer)) {
            System.out.println("Waiting for human player's action...");
            if (listener != null) {
                listener.onHumanTurn(); // Notify UI to enable controls
            }
            return; // Halt further execution until human action
        }

        if (currentPlayer instanceof PokerAI) {
            aiPlayerTakeAction((PokerAI) currentPlayer); // Let AI act
        }
    }


    public Player getDealer() {
        return dealer; // Replace with your actual dealer logic
    }

    private void moveDealer() {
        int dealerIndex = getAllPlayers().indexOf(dealer);
        dealer = getAllPlayers().get((dealerIndex + 1) % getAllPlayers().size());
    }

    public void dealCards() {
        for (Player player : getAllPlayers()) {
            player.clearCards(); // Ensure no leftover cards
            player.addCardToHand(deck.dealCard()); // Deal first card
            player.addCardToHand(deck.dealCard()); // Deal second card
        }
    }

    public void collectBlinds() {
        System.out.println("Collecting blinds...");

        Player smallBlindPlayer = getNextPlayer(dealer);
        Player bigBlindPlayer = getNextPlayer(smallBlindPlayer);

        smallBlindPlayer.placeBet(smallBlind);
        bigBlindPlayer.placeBet(bigBlind);

        pot += smallBlind + bigBlind;
        currentBet = bigBlind;

        // Log actions for small blind and big blind payments
        logAction(smallBlindPlayer.getName() + " posts the small blind of $" + smallBlind + ".");
        logAction(bigBlindPlayer.getName() + " posts the big blind of $" + bigBlind + ".");
        playBettingRound();
    }

    public void handleFold(Player player) {
        player.fold();
        logAction(player.getName() + " folds.");
        moveToNextPlayer();
    }

    public void handleCall(Player player) {
        int callAmount = currentBet - player.getCurrentBet();
        player.placeBet(callAmount);
        pot += callAmount;

        logAction(player.getName() + " calls $" + callAmount + ".");
        moveToNextPlayer();
    }

    public void handleRaise(Player player, int raiseAmount) {
        int totalBet = currentBet + raiseAmount;
        int callAmount = totalBet - player.getCurrentBet();

        player.placeBet(callAmount);
        pot += callAmount;
        currentBet = totalBet;

        logAction(player.getName() + " raises by $" + raiseAmount + " (total bet: $" + totalBet + ").");
        moveToNextPlayer();
    }

    private void determineWinner() {
        List<Player> activePlayers = getAllPlayers().stream()
                .filter(player -> !player.isFolded())
                .toList();

        Player winner = activePlayers.stream()
                .max((p1, p2) -> HandEvaluator.compareHands(p1.getHand(), p2.getHand()))
                .orElse(null);

        if (winner != null) {
            logAction(winner.getName() + " wins the pot of $" + pot + "!");
            winner.addChips(pot);
            pot = 0; // Reset pot
        }
    }

    private boolean isRoundOver() {
        boolean roundOver = getAllPlayers().stream()
                .allMatch(player -> player.isFolded() || player.getCurrentBet() == currentBet);

        System.out.println("Is round over? " + roundOver);
        return roundOver;
    }

    private Player getNextPlayer(Player current) {
        List<Player> allPlayers = getAllPlayers();
        int currentIndex = allPlayers.indexOf(current);

        // Use modulo to cycle to the next player
        return allPlayers.get((currentIndex + 1) % allPlayers.size());
    }

    private List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.add(humanPlayer);
        allPlayers.addAll(aiPlayers);
        return allPlayers;
    }

    private void logAction(String message) {
        System.out.println(message);
    }
}
