package poker.models;

import poker.api.PokerApiClient;

import java.util.*;

public class PokerGame {

    private Deck deck;
    private List<PokerAI> players;
    private PokerAI mainAI;
    private List<Card> communityCards;
    private double pot;
    private int dealerPosition;
    private double smallBlind;
    private double bigBlind;
    private String currentRound;

    public PokerGame(List<PokerAI> players, PokerAI mainAI, double smallBlind, double bigBlind) {
        this.deck = new Deck();
        this.players = new ArrayList<>(players);
        this.mainAI = mainAI;
        this.players.add(mainAI); // Add main AI to the game
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.dealerPosition = 0; // Start with the first player as dealer
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.currentRound = "Pre-Flop"; // Initialize to the first betting round
    }

    public void playHand() {
        resetGame();
        assignPositions();
        dealHoleCards();
        preFlop();
        flop();
        turn();
        river();
        showdown();
    }

    private void resetGame() {
        deck.shuffle();
        communityCards.clear();
        pot = 0;
        dealerPosition = (dealerPosition + 1) % players.size();
        currentRound = "Pre-Flop";
        for (PokerAI player : players) {
            player.resetHoleCards();
        }
    }

    private void assignPositions() {
        for (int i = 0; i < players.size(); i++) {
            int relativePosition = (i - dealerPosition + players.size()) % players.size();
            players.get(i).updatePosition(relativePosition, dealerPosition, players.size());
        }
    }

    private void dealHoleCards() {
        System.out.println("Dealing hole cards...");
        int startPosition = (dealerPosition + 1) % players.size();
        for (int i = 0; i < 2; i++) { // Deal two rounds of cards
            for (int j = 0; j < players.size(); j++) {
                int position = (startPosition + j) % players.size();
                players.get(position).addHoleCard(deck.dealCard());
            }
        }
    }

    private void burnCard() {
        deck.dealCard(); // Burn the top card
    }

    private void preFlop() {
        System.out.println("Pre-Flop:");
        currentRound = "Pre-Flop";
        collectBlinds();
        playBettingRound();
    }

    private void flop() {
        System.out.println("Flop:");
        currentRound = "Flop";
        burnCard();
        dealCommunityCards(3);
        playBettingRound();
    }

    private void turn() {
        System.out.println("Turn:");
        currentRound = "Turn";
        burnCard();
        dealCommunityCards(1);
        playBettingRound();
    }

    private void river() {
        System.out.println("River:");
        currentRound = "River";
        burnCard();
        dealCommunityCards(1);
        playBettingRound();
    }

    private void showdown() {
        System.out.println("Showdown:");
        PokerApiClient apiClient = new PokerApiClient("https://poker-api-production-24f9.up.railway.app/graphql");
        String playerInput = buildPlayerInput();

        try {
            Map<String, Object> result = apiClient.determineWinner(playerInput);

            List<String> winners = (List<String>) result.get("winners");
            if (winners != null && !winners.isEmpty()) {
                System.out.println("Winner(s): " + String.join(", ", winners));
                distributeWinnings(result);
            } else {
                System.out.println("No winners could be determined.");
            }
        } catch (Exception e) {
            System.err.println("Failed to determine winner: " + e.getMessage());
        }
    }


    private void dealCommunityCards(int numCards) {
        for (int i = 0; i < numCards; i++) {
            communityCards.add(deck.dealCard());
        }
        System.out.println("Community Cards: " + communityCards);
    }

    private void collectBlinds() {
        int smallBlindPlayer = (dealerPosition + 1) % players.size();
        int bigBlindPlayer = (dealerPosition + 2) % players.size();

        players.get(smallBlindPlayer).adjustBalance(-smallBlind);
        players.get(bigBlindPlayer).adjustBalance(-bigBlind);

        pot += smallBlind + bigBlind;

        System.out.println("Small Blind: Player " + players.get(smallBlindPlayer).getId());
        System.out.println("Big Blind: Player " + players.get(bigBlindPlayer).getId());
    }

    private void playBettingRound() {
        List<PokerAI> activePlayers = new ArrayList<>(players);

        while (true) {
            boolean betsAdjusted = false;
            for (PokerAI player : activePlayers) {
                if (player.getBalance() > 0) {
                    double handStrength = estimateHandStrength(player);
                    double potOdds = potOdds(player);
                    String action = player.makeDecision(handStrength, potOdds);
                    System.out.println(player.getId() + " Action: " + action);
                    betsAdjusted |= handleAction(player, action, activePlayers);
                }
            }

            // Break when no more bets are adjusted
            if (!betsAdjusted) break;
        }
    }

    private double potOdds(PokerAI player) {
        return pot / (pot + player.getBalance());
    }

    private boolean handleAction(PokerAI player, String action, List<PokerAI> activePlayers) {
        switch (action) {
            case "Fold":
                System.out.println(player.getId() + " folds.");
                activePlayers.remove(player);
                return false;
            case "Call":
                double callAmount = smallBlind; // Example placeholder
                player.adjustBalance(-callAmount);
                pot += callAmount;
                System.out.println(player.getId() + " calls with " + callAmount);
                return false;
            case "Raise":
                double raiseAmount = bigBlind * 2; // Example placeholder
                player.adjustBalance(-raiseAmount);
                pot += raiseAmount;
                System.out.println(player.getId() + " raises to " + raiseAmount);
                return true;
            default:
                System.out.println(player.getId() + " checks.");
                return false;
        }
    }

    private double estimateHandStrength(PokerAI player) {
        return HandEvaluator.evaluateHandStrength(player.getHoleCards(), communityCards, player.getPosition());
    }

    private String buildPlayerInput() {
        StringBuilder sb = new StringBuilder();

        // Append community cards
        sb.append("cc=");
        for (Card card : communityCards) {
            sb.append(formatCard(card)).append(",");
        }
        if (!communityCards.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1); // Remove the trailing comma
        }

        // Append player hole cards
        for (PokerAI player : players) {
            sb.append("/").append(player.getId()).append("=");
            for (Card card : player.getHoleCards()) {
                sb.append(formatCard(card)).append(",");
            }
            if (!player.getHoleCards().isEmpty()) {
                sb.deleteCharAt(sb.length() - 1); // Remove the trailing comma
            }
        }

        return sb.toString();
    }

    // Helper method to format cards
    private String formatCard(Card card) {
        return card.getRank() + card.getSuit(); // Combine rank and suit without spaces
    }


    private void distributeWinnings(Map<String, Object> result) {
        List<String> winners = (List<String>) result.get("winners");
        double splitPot = pot / winners.size();
        for (PokerAI player : players) {
            if (winners.contains(player.getId())) {
                player.adjustBalance(splitPot);
                System.out.println(player.getId() + " wins " + splitPot);
            }
        }
    }

    // Getters
    public List<PokerAI> getPlayers() {
        return players;
    }

    public PokerAI getMainAI() {
        return mainAI;
    }

    public double getSmallBlind() {
        return smallBlind;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public double getPot() {
        return pot;
    }

    public int getDealerPosition() {
        return dealerPosition;
    }

    public String getCurrentRound() {
        return currentRound;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }
}
