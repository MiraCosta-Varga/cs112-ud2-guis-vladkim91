package poker.models;

import java.util.ArrayList;
import java.util.List;

public abstract class PokerAI extends Player {

    private double aggression; // AI's aggression level (0-100)
    private double tightness; // AI's tightness level (0-100)
    private double bluffFrequency; // Bluff frequency (0-100)
    private double riskTolerance; // Risk tolerance (0-100)
    private List<Card> holeCards; // Player's hole cards
    private int position;

    public PokerAI(String id, int chips, double aggression, double tightness, double bluffFrequency, double riskTolerance) {
        super(id, chips);
        this.aggression = aggression;
        this.tightness = tightness;
        this.bluffFrequency = bluffFrequency;
        this.riskTolerance = riskTolerance;
        this.holeCards = new ArrayList<>();
    }

    // Getters and Setters for AI tendencies
    public double getAggression() {
        return aggression;
    }

    public int getPosition() {
        return position;
    }


    public void setAggression(double aggression) {
        this.aggression = aggression;
    }

    public double getTightness() {
        return tightness;
    }

    public void setTightness(double tightness) {
        this.tightness = tightness;
    }

    public double getBluffFrequency() {
        return bluffFrequency;
    }

    public void setBluffFrequency(double bluffFrequency) {
        this.bluffFrequency = bluffFrequency;
    }

    public double getRiskTolerance() {
        return riskTolerance;
    }

    public void setRiskTolerance(double riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public void addHoleCard(Card card) {
        holeCards.add(card);
    }

    public void resetHoleCards() {
        holeCards.clear();
    }

    public void updatePosition(int relativePosition, int dealerPosition, int totalPlayers) {
             this.position = relativePosition;
    }


    // Abstract decision-making method to be implemented by subclasses
    public abstract String makeDecision(double handStrength, double potOdds);

    @Override
    public String toString() {
        return "PokerAI{" +
                "id='" + getId() + '\'' +
                ", chips=" + getChips() +
                ", aggression=" + aggression +
                ", tightness=" + tightness +
                ", bluffFrequency=" + bluffFrequency +
                ", riskTolerance=" + riskTolerance +
                ", holeCards=" + holeCards +
                '}';
    }

}
