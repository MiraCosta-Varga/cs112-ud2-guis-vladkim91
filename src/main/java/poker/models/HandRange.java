package poker.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandRange {

    private final Map<String, Integer> handStrengthMap;

    public HandRange() {
        handStrengthMap = new HashMap<>();
        generateHandRange();
    }

    // Generates all 169 starting hand combinations with predefined rankings
    private void generateHandRange() {
        String[] rankedHands = {
                "AA", "KK", "QQ", "AKs", "JJ", "AQs", "KQs", "AJs", "KJs", "TT", "AKo", "ATs", "QJs", "KTs", "QTs",
                "JTs", "99", "AQo", "A9s", "KQo", "88", "K9s", "T9s", "A8s", "Q9s", "J9s", "AJo", "A5s", "77",
                "A7s", "KJo", "A4s", "A3s", "A6s", "QJo", "66", "K8s", "T8s", "A2s", "98s", "J8s", "ATo", "Q8s",
                "K7s", "KTo", "55", "JTo", "87s", "QTo", "44", "33", "22", "K6s", "97s", "K5s", "76s", "T7s", "K4s",
                "K3s", "K2s", "Q7s", "86s", "65s", "J7s", "54s", "Q6s", "75s", "96s", "Q5s", "64s", "Q4s", "Q3s",
                "T9o", "T6s", "Q2s", "A9o", "53s", "85s", "J6s", "J9o", "K9o", "J5s", "Q9o", "43s", "74s", "J4s",
                "J3s", "95s", "J2s", "63s", "A8o", "52s", "T5s", "84s", "T4s", "T3s", "42s", "T2s", "98o", "T8o",
                "A5o", "A7o", "73s", "A4o", "32s", "94s", "93s", "J8o", "A3o", "62s", "92s", "K8o", "A6o", "87o",
                "Q8o", "83s", "A2o", "82s", "97o", "72s", "76o", "K7o", "65o", "T7o", "K6o", "86o", "54o", "K5o",
                "J7o", "75o", "Q7o", "K4o", "K3o", "96o", "K2o", "64o", "Q6o", "53o", "85o", "T6o", "Q5o", "43o",
                "Q4o", "Q3o", "74o", "Q2o", "J6o", "63o", "J5o", "95o", "52o", "J4o", "J3o", "42o", "J2o", "84o",
                "T5o", "T4o", "32o", "T3o", "73o", "T2o", "62o", "94o", "93o", "92o", "83o", "82o", "72o"
        };

        // Map hands to rankings
        for (int i = 0; i < rankedHands.length; i++) {
            handStrengthMap.put(rankedHands[i], i + 1);
        }
    }

    // Get strength rank for a hand
    public int getHandRank(String hand) {
        return handStrengthMap.getOrDefault(hand, 169); // Default to weakest rank
    }

    // Check if a hand is suited
    public static boolean isSuited(String hand) {
        return hand.contains("s");
    }

    // Check if a hand is off-suit
    public static boolean isOffSuit(String hand) {
        return hand.contains("o");
    }
    public static double getHandRangeValue(List<Card> holeCards) {
        if (holeCards == null || holeCards.size() != 2) {
            throw new IllegalArgumentException("Hole cards must contain exactly 2 cards.");
        }

        Card card1 = holeCards.get(0);
        Card card2 = holeCards.get(1);

        String handKey;
        if (card1.getRank().equals(card2.getRank())) {
            handKey = card1.getRank() + card2.getRank(); // Pocket pairs
        } else {
            String cardCombo = card1.getRank() + card2.getRank();
            boolean suited = card1.getSuit().equals(card2.getSuit());
            handKey = suited ? cardCombo + "s" : cardCombo + "o"; // Suited or offsuit combos
        }

        HandRange range = new HandRange();
        return range.getHandRank(handKey);
    }
    // Get hand description
    public String describeHand(String hand) {
        int rank = getHandRank(hand);
        if (rank <= 10) return "Premium Hand";
        else if (rank <= 50) return "Playable Hand";
        else return "Marginal Hand";
    }

    @Override
    public String toString() {
        return "HandRange{" +
                "handStrengthMap=" + handStrengthMap +
                '}';
    }
}
