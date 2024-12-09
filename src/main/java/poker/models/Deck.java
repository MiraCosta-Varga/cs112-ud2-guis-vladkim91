package poker.models;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private Stack<Card> cards;

    public Deck() {
        cards = new Stack<>();
        String[] suits = {"H", "D", "C", "S"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.push(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (!cards.isEmpty()) {
            return cards.pop();
        } else {
            throw new IllegalStateException("Deck is empty");
        }
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty!");
        }
        return cards.remove(0); // Assuming `cards` is a list of `Card`
    }
    public List<Card> getCards() {
        return cards;
    }

    public Card deal() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        } else {
            throw new IllegalStateException("Deck is empty");
        }
    }
}
