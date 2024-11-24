package poker.models;

public abstract class PokerAI extends Player {

    private final int aggression;
    private final int tightness;
    private final int bluffFrequency;
    private final int riskTolerance;

    public PokerAI(String id, int chips, int aggression, int tightness, int bluffFrequency, int riskTolerance) {
        super(id, chips);
        this.aggression = aggression;
        this.tightness = tightness;
        this.bluffFrequency = bluffFrequency;
        this.riskTolerance = riskTolerance;
    }

    public abstract String makeDecision(double handStrength, double potOdds);

    // Add getters if needed
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
}
