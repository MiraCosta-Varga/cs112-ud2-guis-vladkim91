package poker.models;

public class BasicPokerAI extends PokerAI {

    public BasicPokerAI(String id, int chips, int aggression, int tightness, int bluffFrequency, int riskTolerance) {
        super(id, chips, aggression, tightness, bluffFrequency, riskTolerance);
    }

    @Override
    public String makeDecision(double handStrength, double potOdds) {
        // Implement AI decision logic
        if (handStrength > 0.8) {
            return "Raise";
        } else if (handStrength > 0.5) {
            return "Call";
        }
        return "Fold";
    }
}
