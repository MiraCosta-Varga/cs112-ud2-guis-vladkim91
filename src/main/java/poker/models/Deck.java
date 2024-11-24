package poker.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    // Constructor to initialize the deck with all 52 cards
    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};

        // Populate the deck with cards
        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    // Shuffle the deck
    public void shuffle() {
        Collections.shuffle(cards);
    }

    // Deal the top card from the deck
    public Card dealCard() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        } else {
            throw new IllegalStateException("No cards left in the deck");
        }
    }

    // Burn the top card (remove without returning)
    public void burnCard() {
        if (!cards.isEmpty()) {
            cards.remove(0);
        } else {
            throw new IllegalStateException("No cards left to burn");
        }
    }

    // Get the remaining number of cards in the deck
    public int remainingCards() {
        return cards.size();
    }

    // Reset the deck to its full state (52 cards)
    public void reset() {
        cards.clear();
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    // For debugging: print all cards in the deck
    public void printDeck() {
        for (Card card : cards) {
            System.out.println(card);
        }
    }
}
