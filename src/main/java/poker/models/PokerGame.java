package poker.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PokerGame {
    private List<Card> communityCards;
    private Player humanPlayer;
    private List<PokerAI> aiPlayers;
    private Deck deck;
    private int pot;
    private int smallBlind;
    private int bigBlind;
    private Player dealer;
    private int currentBet;
    private Player currentPlayer;

    public PokerGame(Player humanPlayer, List<PokerAI> aiPlayers, int smallBlind, int bigBlind) {
        this.humanPlayer = humanPlayer;
        this.aiPlayers = aiPlayers;
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.deck = new Deck();
        this.pot = 0;
        this.currentBet = 0;
        this.communityCards = new ArrayList<>();
        this.dealer = aiPlayers.get(aiPlayers.size() - 1); // Initial dealer
        this.currentPlayer = humanPlayer; // Start with the human player
    }

    public void revealFlop() {
        communityCards.add(deck.deal());
        communityCards.add(deck.deal());
        communityCards.add(deck.deal());
    }

    public void revealTurn() {
        communityCards.add(deck.deal());
    }

    public void revealRiver() {
        communityCards.add(deck.deal());
    }

    public void endHand() {
        determineWinner();
        resetPlayers();
        startNewHand();
    }

    private void resetPlayers() {
        for (Player player : getAllPlayers()) {
            player.resetForNewHand();
        }
        communityCards.clear();
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public void setCurrentPlayer(Player player) {
        System.out.println("Setting current player to: " + player.getName());
        this.currentPlayer = player;
    }


    public void startNewHand() {
        // Reset deck and pot
        deck.shuffle();
        pot = 0;
        currentBet = 0;

        // Move dealer position to the next player
        moveDealer();

        // Deal cards to all players
        dealCards();

        // Set the current player to the one after BB
        setCurrentPlayerAfterBB();
    }


    public void setCurrentPlayerAfterBB() {
        // Avoid duplicate calls
        if (currentPlayer != null && currentPlayer.isFolded()) {
            return; // Skip if already set
        }

        // Identify all players
        List<Player> allPlayers = getAllPlayers();

        // Find the Big Blind player
        Player bigBlindPlayer = getNextPlayer(getNextPlayer(dealer));

        // Find the player after the Big Blind
        Player firstToAct = getNextPlayer(bigBlindPlayer);

        // Log the first player to act after BB (only log once)
        if (currentPlayer != firstToAct) {
            System.out.println(firstToAct.getName() + " is the first to act after BB.");
        }

        // Set the current player
        setCurrentPlayer(firstToAct);
    }




    public void moveToNextPlayer() {
        currentPlayer = getNextPlayer(currentPlayer);
        logAction("It is now " + currentPlayer.getName() + "'s turn.");
    }




    public Player getDealer() {
        return dealer; // Replace with your actual dealer logic
    }

    public void resetPot() {
        pot = 0;
    }


    private void moveDealer() {
        int dealerIndex = getAllPlayers().indexOf(dealer);
        dealer = getAllPlayers().get((dealerIndex + 1) % getAllPlayers().size());
    }

    public void dealCards() {
        for (Player player : getAllPlayers()) {
            player.clearCards(); // Ensure no leftover cards
            player.addCardToHand(deck.dealCard()); // Deal first card
            player.addCardToHand(deck.dealCard()); // Deal second card
        }
    }


    public void collectBlinds() {
        System.out.println("Collecting blinds...");

        Player smallBlindPlayer = getNextPlayer(dealer);
        Player bigBlindPlayer = getNextPlayer(smallBlindPlayer);

        smallBlindPlayer.placeBet(smallBlind);
        bigBlindPlayer.placeBet(bigBlind);

        pot += smallBlind + bigBlind;
        currentBet = bigBlind;

        // Log actions for small blind and big blind payments
        logAction(smallBlindPlayer.getName() + " posts the small blind of $" + smallBlind + ".");
        logAction(bigBlindPlayer.getName() + " posts the big blind of $" + bigBlind + ".");
    }



    public void handleFold(Player player) {
        player.fold();
        logAction(player.getName() + " folds.");
        if (isRoundOver()) {
            determineWinner();
        } else {
            moveToNextPlayer();
        }
    }

    public void handleCall(Player player) {
        int callAmount = currentBet - player.getCurrentBet();
        player.placeBet(callAmount);
        pot += callAmount;

        logAction(player.getName() + " calls $" + callAmount + ".");
        if (isRoundOver()) {
            determineWinner();
        } else {
            moveToNextPlayer();
        }
    }

    public void handleRaise(Player player, int raiseAmount) {
        int totalBet = currentBet + raiseAmount;
        int callAmount = totalBet - player.getCurrentBet();

        player.placeBet(callAmount);
        pot += callAmount;
        currentBet = totalBet;

        logAction(player.getName() + " raises by $" + raiseAmount + " (total bet: $" + totalBet + ").");
        if (isRoundOver()) {
            determineWinner();
        } else {
            moveToNextPlayer();
        }
    }



    public void proceedToNextPhase() {
        if (communityCards.size() == 0) {
            dealCommunityCards(); // Flop
        } else if (communityCards.size() == 3) {
            dealCommunityCards(); // Turn
        } else if (communityCards.size() == 4) {
            dealCommunityCards(); // River
        } else {
            determineWinner();
            resetForNextHand();
            return;
        }


        // Trigger the next betting round
        playBettingRound();
    }


    private void dealCommunityCards() {
        if (communityCards.isEmpty()) {
            // Flop: Deal the first three community cards
            for (int i = 0; i < 3; i++) {
                communityCards.add(deck.deal());
            }
        } else if (communityCards.size() < 5) {
            // Turn or River: Deal one card at a time
            communityCards.add(deck.deal());
        }
    }



    private void resetForNextHand() {
        for (Player player : getAllPlayers()) {
            player.resetHand();
        }
        communityCards.clear();
        startNewHand();
    }


    private Player getNextPlayer(Player current) {
        List<Player> allPlayers = getAllPlayers();
        int currentIndex = allPlayers.indexOf(current);

        // Use modulo to cycle to the next player
        return allPlayers.get((currentIndex + 1) % allPlayers.size());
    }


    private List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        allPlayers.add(humanPlayer);
        allPlayers.addAll(aiPlayers);
        return allPlayers;
    }

    private boolean isRoundOver() {
        // Check if all players have either folded or matched the current bet
        return getAllPlayers().stream().allMatch(player ->
                player.isFolded() || player.getCurrentBet() == currentBet
        );
    }

    private void determineWinner() {
        // Simplified logic to determine the winner based on hand strength
        List<Player> activePlayers = getAllPlayers().stream()
                .filter(player -> !player.isFolded())
                .toList();

        Player winner = activePlayers.stream()
                .max((p1, p2) -> HandEvaluator.compareHands(p1.getHand(), p2.getHand()))
                .orElse(null);

        if (winner != null) {
            logAction(winner.getName() + " wins the pot of $" + pot + "!");
            winner.addChips(pot);
            pot = 0; // Reset pot
        }
    }
//    private void determineWinner() {
//        List<Player> activePlayers = getAllPlayers().stream()
//                .filter(player -> !player.isFolded())
//                .toList();
//
//        Player winner = activePlayers.stream()
//                .max((p1, p2) -> HandEvaluator.compareHands(
//                        HandEvaluator.evaluateHand(p1.getHand(), communityCards),
//                        HandEvaluator.evaluateHand(p2.getHand(), communityCards)))
//                .orElse(null);
//
//        if (winner != null) {
//            winner.addChips(pot);
//            pot = 0; // Reset the pot
//        }
//    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(int bet) {
        this.currentBet = bet;
    }

    public void addToPot(int amount) {
        pot += amount;
    }

    public void playBettingRound() {
        while (!isRoundOver()) {
            if (currentPlayer.equals(humanPlayer)) {
                // Wait for the human player to act (handled via the controller)
                break;
            } else if (currentPlayer instanceof PokerAI) {
                aiPlayerTakeAction((PokerAI) currentPlayer); // AI acts only if it's their turn
            }
        }
    }


    private void aiPlayerTakeAction(PokerAI aiPlayer) {
        if (currentPlayer != aiPlayer) {
            return; // Skip if it's not the AI's turn
        }

        // AI decision-making logic
        if (aiPlayer.getChips() < currentBet) {
            handleFold(aiPlayer);
        } else {
            int decision = new Random().nextInt(3); // Randomly decide: 0 = fold, 1 = call, 2 = raise
            switch (decision) {
                case 0:
                    handleFold(aiPlayer);
                    break;
                case 1:
                    handleCall(aiPlayer);
                    break;
                case 2:
                    int raiseAmount = Math.min(50, aiPlayer.getChips()); // Raise by $50 or remaining chips
                    handleRaise(aiPlayer, raiseAmount);
                    break;
            }
        }

        moveToNextPlayer(); // Move to the next player after the action
    }





    public void processTurn(Player player) {
        if (player instanceof PokerAI) {
            PokerAI ai = (PokerAI) player;
            String action = ai.decideAction(currentBet, pot);

            switch (action) {
                case "fold":
                    handleFold(ai);
                    break;
                case "call":
                    handleCall(ai);
                    break;
                case "raise":
                    int raiseAmount = ai.decideRaiseAmount(currentBet, pot);
                    handleRaise(ai, raiseAmount);
                    break;
            }
        } else {
            // Wait for human player action (handled via UI)
        }
    }

    public void nextPlayerTurn() {
        currentPlayer = getNextPlayer(currentPlayer);
        if (currentPlayer.isFolded() || currentPlayer.getCurrentBet() == currentBet) {
            if (isRoundOver()) {
                proceedToNextPhase();
            } else {
                nextPlayerTurn();
            }
        } else {
            processTurn(currentPlayer);
        }
    }


    public boolean isDealer(Player player) {
        return dealer.equals(player);
    }

    public int getPot() {
        return pot;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public List<PokerAI> getAiPlayers() {
        return aiPlayers;
    }

    private void logAction(String message) {
        System.out.println(message);
    }
}
