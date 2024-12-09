package poker.models;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int chips;
    private List<Card> hand;
    private boolean folded;
    private int currentBet;

    public Player(String name, int chips) {
        this.name = name;
        this.chips = chips;
        this.hand = new ArrayList<>();
        this.folded = false;
        this.currentBet = 0;
    }

    public String getName() {
        return name;
    }


    public int getChips() {
        return chips;
    }

    public void addChips(int amount) {
        this.chips += amount;
    }

    public void deductChips(int amount) {
        if (amount <= chips) {
            this.chips -= amount;
        } else {
            throw new IllegalArgumentException("Not enough chips to place the bet.");
        }
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCardToHand(Card card) {
        hand.add(card);
    }

    public void resetHand() {
        hand.clear();
        folded = false;
        currentBet = 0;
    }

    public boolean isFolded() {
        return folded;
    }

    public void fold() {
        folded = true;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(int amount) {
        this.currentBet = amount;
    }

    public void resetForNewHand() {
        hand.clear();
        folded = false;
        currentBet = 0;
    }


    public void placeBet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips to place the bet.");
        }
        deductChips(amount);
        this.currentBet += amount;
    }

    public void addToPot(int amount) {
        this.currentBet += amount;
    }
    // Getter for the player's hand
    public List<Card> getCards() {
        return hand;
    }

    public void clearCards() {
        this.hand.clear(); // Assuming `hand` is a list of `Card`
    }

    public void setBalance(int balance) {
        this.chips = balance;
    }

}
