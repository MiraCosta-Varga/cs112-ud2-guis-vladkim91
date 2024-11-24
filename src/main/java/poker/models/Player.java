package poker.models;

public class Player {

    private String id;
    private int chips;
    private boolean active;
    private int currentBet;

    // Constructor
    public Player(String id, int initialChips) {
        this.id = id;
        this.chips = initialChips;
        this.active = true;
        this.currentBet = 0;
    }

    // Player actions
    public void fold() {
        active = false;
        System.out.println(id + " folds.");
    }

    public void call(int amountToCall) {
        if (amountToCall > chips) {
            amountToCall = chips; // Go all-in if the call amount exceeds available chips
        }
        chips -= amountToCall;
        currentBet += amountToCall;
        System.out.println(id + " calls $" + amountToCall);
    }

    public void raise(int raiseAmount) {
        if (raiseAmount > chips) {
            throw new IllegalArgumentException("Not enough chips to raise.");
        }
        chips -= raiseAmount;
        currentBet += raiseAmount;
        System.out.println(id + " raises $" + raiseAmount);
    }

    // Reset player status for a new hand
    public void resetForNewHand() {
        currentBet = 0;
        active = true;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getChips() {
        return chips;
    }

    public boolean isActive() {
        return active;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void bet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips to bet.");
        }
        chips -= amount;
        currentBet += amount; // Optionally track the current bet
    }
    public void adjustBalance(double amount) {
        chips += amount;
    }


}
