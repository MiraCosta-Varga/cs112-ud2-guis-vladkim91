package poker.models;

import java.util.ArrayList;
import java.util.List;

public class PokerGame {

    private Player humanPlayer;
    private List<PokerAI> aiPlayers;
    private List<Card> communityCards;
    private double pot;
    private double currentBet; // Tracks the current bet amount in the round

    public PokerGame(Player humanPlayer, List<PokerAI> aiPlayers, int smallBlind, int bigBlind) {
        this.humanPlayer = humanPlayer;
        this.aiPlayers = new ArrayList<>(aiPlayers);
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentBet = bigBlind;
    }

    // Play a hand (logic for dealing cards, betting rounds, etc.)
    public void playHand() {
        // Reset and start a new hand
        resetGame();
        dealCards();
    }

    private void resetGame() {
        communityCards.clear();
        pot = 0;
        currentBet = 0;
    }

    private void dealCards() {
        // Assign example cards to the human player
        humanPlayer.setHoleCards(new Card("A", "H"), new Card("K", "H")); // Ace of Hearts, King of Hearts

        // Assign example cards to AI players
        for (PokerAI ai : aiPlayers) {
            ai.setHoleCards(new Card("2", "D"), new Card("3", "C")); // 2 of Diamonds, 3 of Clubs
        }
    }


    // Add to the pot
    public void addToPot(double amount) {
        pot += amount;
    }

    // Getters
    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public List<PokerAI> getAiPlayers() {
        return aiPlayers;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public double getPot() {
        return pot;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    // Setters
    public void setCommunityCards(List<Card> communityCards) {
        this.communityCards = communityCards;
    }

    public void setCurrentBet(double currentBet) {
        this.currentBet = currentBet;
    }
}
