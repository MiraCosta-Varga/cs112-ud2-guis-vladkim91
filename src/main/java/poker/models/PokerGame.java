package poker.models;

import poker.api.PokerApiClient;

import java.util.*;

public class PokerGame {

    private Deck deck;
    private List<Player> players;
    private Player humanPlayer;
    private List<Card> communityCards;
    private double pot;
    private int dealerPosition;
    private int smallBlind;
    private int bigBlind;
    private String currentRound;

    public PokerGame(Player humanPlayer, List<Player> otherPlayers, int smallBlind, double bigBlind) {
        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.players.add(humanPlayer);
        this.players.addAll(otherPlayers);
        this.humanPlayer = humanPlayer;
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.dealerPosition = 0; // Start with the first player as dealer
        this.smallBlind = smallBlind;
        this.bigBlind = (int) bigBlind;
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
        for (Player player : players) {
            if (player instanceof PokerAI ai) {
                ai.resetHoleCards();
            }
        }
    }

    private void assignPositions() {
        for (int i = 0; i < players.size(); i++) {
            int relativePosition = (i - dealerPosition + players.size()) % players.size();
            if (players.get(i) instanceof PokerAI ai) {
                ai.updatePosition(relativePosition, dealerPosition, players.size());
            }
        }
    }

    private void dealHoleCards() {
        System.out.println("Dealing hole cards...");
        int startPosition = (dealerPosition + 1) % players.size();
        for (int i = 0; i < 2; i++) { // Deal two rounds of cards
            for (int j = 0; j < players.size(); j++) {
                int position = (startPosition + j) % players.size();
                Card dealtCard = deck.dealCard();
                if (players.get(position) instanceof PokerAI ai) {
                    ai.addHoleCard(dealtCard);
                }
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

        players.get(smallBlindPlayer).bet((int) smallBlind);
        players.get(bigBlindPlayer).bet((int) bigBlind);

        pot += smallBlind + bigBlind;

        System.out.println("Small Blind: Player " + players.get(smallBlindPlayer).getId());
        System.out.println("Big Blind: Player " + players.get(bigBlindPlayer).getId());
    }


    public void playBettingRound() {
        for (Player player : players) {
            if (player instanceof PokerAI ai) {
                double handStrength = estimateHandStrength(ai);
                double potOdds = pot / (pot + ai.getChips());
                String action = ai.makeDecision(handStrength, potOdds);
                handleAIAction(ai, action);
            }
        }
    }
    private double estimateHandStrength(PokerAI ai) {
        return HandEvaluator.evaluateHandStrength(ai.getHoleCards(), communityCards, ai.getPosition());
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
        for (Player player : players) {
            sb.append("/").append(player.getId()).append("=");
            if (player instanceof PokerAI ai) {
                for (Card card : ai.getHoleCards()) {
                    sb.append(formatCard(card)).append(",");
                }
                if (!ai.getHoleCards().isEmpty()) {
                    sb.deleteCharAt(sb.length() - 1); // Remove the trailing comma
                }
            }
        }

        return sb.toString();
    }

    private String formatCard(Card card) {
        return card.getRank() + card.getSuit(); // Combine rank and suit without spaces
    }

    private void distributeWinnings(Map<String, Object> result) {
        List<String> winners = (List<String>) result.get("winners");
        double splitPot = pot / winners.size();
        for (Player player : players) {
            if (winners.contains(player.getId())) {
                player.adjustBalance(splitPot);
                System.out.println(player.getId() + " wins $" + splitPot);
            }
        }
    }


    // Getters for required methods
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
        return new ArrayList<>(communityCards);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public int getSmallBlind() {
        return (int)smallBlind;
    }

    public double getBigBlind() {
        return bigBlind;
    }

    public void addToPot(double amount) {
        this.pot += amount;
    }
    private void handleAIAction(PokerAI ai, String action) {
        switch (action) {
            case "Fold" -> {
                ai.fold();
                System.out.println(ai.getId() + " folds.");
            }
            case "Call" -> {
                int amountToCall = (int) smallBlind;
                ai.call(amountToCall);
                addToPot(amountToCall);
                System.out.println(ai.getId() + " calls $" + amountToCall);
            }
            case "Raise" -> {
                int raiseAmount = (int) (bigBlind * 2); // Example AI raise logic
                ai.raise(raiseAmount);
                addToPot(raiseAmount);
                System.out.println(ai.getId() + " raises $" + raiseAmount);
            }
        }
    }

}
