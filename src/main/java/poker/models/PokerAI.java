package poker.models;

import java.util.List;

public class PokerAI extends Player {
    private int aggression;
    private int tightness;
    private int bluffFrequency;
    private int riskTolerance;

    public PokerAI(String name, int chips, int aggression, int tightness, int bluffFrequency, int riskTolerance) {
        super(name, chips);
        this.aggression = aggression;
        this.tightness = tightness;
        this.bluffFrequency = bluffFrequency;
        this.riskTolerance = riskTolerance;
    }
    public String decideAction(int currentBet, int pot, int position, List<Card> communityCards) {
        System.out.println(getName() + " is evaluating the action...");
        int handStrength = HandEvaluator.evaluateHand(getHand());
        System.out.println("Hand strength: " + handStrength);
        int decisionThreshold = calculateDecisionThreshold(position);
        System.out.println("Decision threshold: " + decisionThreshold);

        decisionThreshold -= aggression;
        decisionThreshold += tightness;

        if (Math.random() * 100 < bluffFrequency) {
            System.out.println(getName() + " decides to bluff.");
            return "raise";
        }

        if (handStrength <= decisionThreshold) {
            if (currentBet == 0) {
                System.out.println(getName() + " decides to bet.");
                return "raise";
            } else if (currentBet <= getChips() / riskTolerance) {
                System.out.println(getName() + " decides to call.");
                return "call";
            } else {
                System.out.println(getName() + " decides to fold.");
                return "fold";
            }
        } else {
            System.out.println(getName() + " decides to fold due to weak hand.");
            return "fold";
        }
    }


    public int decideRaiseAmount(int pot) {
        return Math.min(50, pot / 4); // Raise by a quarter of the pot or $50, whichever is smaller
    }

    private int calculateDecisionThreshold(int position) {
        // Early position: higher threshold (play tighter)
        // Late position: lower threshold (play looser)
        switch (position) {
            case 0: return 20; // Early position
            case 1: return 15; // Middle position
            default: return 10; // Late position
        }
    }
}
