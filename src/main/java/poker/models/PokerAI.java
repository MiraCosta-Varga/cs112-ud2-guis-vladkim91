package poker.models;

import java.util.ArrayList;
import java.util.List;

public class PokerAI {

    private String id; // Unique identifier for the AI player
    private double balance; // Player's current stack size
    private double aggression; // AI's aggression level (0-100)
    private double tightness; // AI's tightness level (0-100)
    private double bluffFrequency; // Bluff frequency (0-100)
    private double riskTolerance; // Risk tolerance (0-100)
    private List<Card> holeCards; // Player's hole cards
    private int position; // Player's position at the table (relative to dealer)

    public PokerAI(String id, double balance, double aggression, double tightness, double bluffFrequency, double riskTolerance) {
        this.id = id;
        this.balance = balance;
        this.aggression = aggression;
        this.tightness = tightness;
        this.bluffFrequency = bluffFrequency;
        this.riskTolerance = riskTolerance;
        this.holeCards = new ArrayList<>();
    }

    // Adjust balance
    public void adjustBalance(double amount) {
        this.balance += amount;
    }

    // Getters
    public String getId() {
        return id;
    }

    public double getBalance() {
        return balance;
    }

    public int getPosition() {
        return position;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    // Set hole cards
    public void addHoleCard(Card card) {
        holeCards.add(card);
    }

    // Reset hole cards for the next hand
    public void resetHoleCards() {
        holeCards.clear();
    }

    // Update position based on dealer
    public void updatePosition(int relativePosition, int dealerPosition, int totalPlayers) {
        this.position = relativePosition;
    }

    // Core AI decision-making logic
    public String makeDecision(double handStrength, double potOdds) {
        // Example: Basic decision-making logic
        if (handStrength > 0.8 && balance > potOdds * balance) {
            return "Raise";
        } else if (handStrength > 0.5 || potOdds > 0.4) {
            return "Call";
        } else if (bluffFrequency > Math.random() * 100) {
            return "Bluff";
        } else {
            return "Fold";
        }
    }

    @Override
    public String toString() {
        return "PokerAI{" +
                "id='" + id + '\'' +
                ", balance=" + balance +
                ", aggression=" + aggression +
                ", tightness=" + tightness +
                ", bluffFrequency=" + bluffFrequency +
                ", riskTolerance=" + riskTolerance +
                ", holeCards=" + holeCards +
                ", position=" + position +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PokerAI pokerAI = (PokerAI) obj;
        return id.equals(pokerAI.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
