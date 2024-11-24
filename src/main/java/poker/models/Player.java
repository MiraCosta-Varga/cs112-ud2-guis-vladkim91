package poker.models;

public class Player {

    private String id;
    private double chips;
    private Card[] holeCards;

    public Player(String id, double chips) {
        this.id = id;
        this.chips = chips;
        this.holeCards = new Card[2]; // Two hole cards
    }

    public void setHoleCards(Card card1, Card card2) {
        this.holeCards[0] = card1;
        this.holeCards[1] = card2;
    }

    public Card[] getHoleCards() {
        return holeCards;
    }

    public String getId() {
        return id;
    }

    public double getChips() {
        return chips;
    }

    public void call(double amount) {
        chips -= amount;
    }

    public void raise(double amount) {
        chips -= amount;
    }

    public void fold() {
        System.out.println(id + " folds.");
    }
}
