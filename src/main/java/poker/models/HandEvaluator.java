package poker.models;

import java.util.List;

public class HandEvaluator {

    private static final HandRange handRange = new HandRange();
    public static int compareHands(List<Card> hand1, List<Card> hand2) {
        // Validate the hands
        if (hand1 == null || hand2 == null || hand1.size() != 2 || hand2.size() != 2) {
            throw new IllegalArgumentException("Each hand must contain exactly 2 cards.");
        }

        // Get hand range values for both hands
        double hand1Rank = HandRange.getHandRangeValue(hand1);
        double hand2Rank = HandRange.getHandRangeValue(hand2);

        // Compare the hand rankings
        return Double.compare(hand1Rank, hand2Rank);
    }

    public static int evaluateHand(List<Card> hand) {
        if (hand == null || hand.size() != 2) {
            throw new IllegalArgumentException("Hand must contain exactly 2 cards.");
        }
        return (int) HandRange.getHandRangeValue(hand);
    }
}
