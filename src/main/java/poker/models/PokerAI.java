package poker.models;

import java.util.Random;

public class PokerAI extends Player {
    private int aggression;
    private int tightness;
    private int bluffFrequency;
    private int riskTolerance;

    private Random random;

    public PokerAI(String name, int chips, int aggression, int tightness, int bluffFrequency, int riskTolerance) {
        super(name, chips);
        this.aggression = aggression;
        this.tightness = tightness;
        this.bluffFrequency = bluffFrequency;
        this.riskTolerance = riskTolerance;
        this.random = new Random();
    }


    // Getters for sliders (useful for debugging or future GUI integration)
    public int getAggression() {
        return aggression;
    }

    public int getTightness() {
        return tightness;
    }

    public int getBluffFrequency() {
        return bluffFrequency;
    }

    public int getRiskTolerance() {
        return riskTolerance;
    }

    public String decideAction(int currentBet, int pot) {
        if (isFolded()) return "fold";

        int callAmount = currentBet - getCurrentBet();
        int decisionFactor = aggression + bluffFrequency - tightness;

        if (decisionFactor > 70) {
            return "raise";
        } else if (callAmount <= getChips() / riskTolerance) {
            return "call";
        } else {
            return "fold";
        }
    }

    public int decideRaiseAmount(int currentBet, int pot) {
        return currentBet + (pot / 4); // Example logic: raise by 25% of the pot
    }
}
