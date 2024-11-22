package poker.models;

import java.util.ArrayList;
import java.util.List;

public class HandEvaluator {

    // Evaluates the hand strength based on hole cards, community cards, and position
    public static double evaluateHandStrength(List<Card> holeCards, List<Card> communityCards, int position) {
        if (holeCards.size() != 2) {
            throw new IllegalArgumentException("Hole cards must contain exactly 2 cards.");
        }

        // Combine hole cards and community cards
        List<Card> combinedCards = new ArrayList<>(holeCards);
        combinedCards.addAll(communityCards);

        // Use hand range chart (placeholder logic)
        double handRangeValue = HandRange.getHandRangeValue(holeCards);

        // Adjust hand strength based on position
        double positionMultiplier = getPositionMultiplier(position);

        // Calculate final hand strength
        return handRangeValue * positionMultiplier;
    }

    // Determines position multiplier for hand strength (e.g., late position gets more weight)
    private static double getPositionMultiplier(int position) {
        if (position == 0) { // Dealer
            return 1.2;
        } else if (position == 1 || position == 2) { // Small Blind or Big Blind
            return 0.8;
        } else if (position >= 3) { // Late position
            return 1.0;
        } else { // Early or middle positions
            return 0.9;
        }
    }
}
