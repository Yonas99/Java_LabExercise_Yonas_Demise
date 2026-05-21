package com.poker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Action {
        FOLD, CHECK, CALL, RAISE, ALL_IN
    }

    private final String name;
    private int chips;
    private final List<Card> holeCards;
    private int currentBet;
    private boolean isFolded;
    private boolean isAllIn;
    private boolean isDealer;
    private final boolean isBot;
    private int totalWins;
    private int totalLosses;
    private int gamesPlayed;

    public Player(String name, int startingChips, boolean isBot) {
        this.name = name;
        this.chips = startingChips;
        this.holeCards = new ArrayList<>();
        this.currentBet = 0;
        this.isFolded = false;
        this.isAllIn = false;
        this.isDealer = false;
        this.isBot = isBot;
        this.totalWins = 0;
        this.totalLosses = 0;
        this.gamesPlayed = 0;
    }

    public String getName() {
        return name;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }

    public List<Card> getHoleCards() {
        return new ArrayList<>(holeCards);
    }

    public void addHoleCard(Card card) {
        holeCards.add(card);
    }

    public void clearHoleCards() {
        holeCards.clear();
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void setCurrentBet(int bet) {
        this.currentBet = bet;
    }

    public void addToCurrentBet(int amount) {
        this.currentBet += amount;
    }

    public boolean isFolded() {
        return isFolded;
    }

    public void fold() {
        this.isFolded = true;
    }

    public boolean isAllIn() {
        return isAllIn;
    }

    public void setAllIn(boolean allIn) {
        this.isAllIn = allIn;
    }

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean dealer) {
        this.isDealer = dealer;
    }

    public boolean isBot() {
        return isBot;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getWins() {
        return totalWins;
    }

    public void incrementWins() {
        this.totalWins++;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public int getLosses() {
        return totalLosses;
    }

    public void incrementLosses() {
        this.totalLosses++;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public boolean canBet(int amount) {
        return chips >= amount;
    }

    public int placeBet(int amount) {
        if (amount >= chips) {
            int betAmount = chips;
            chips = 0;
            isAllIn = true;
            currentBet += betAmount;
            return betAmount;
        } else {
            chips -= amount;
            currentBet += amount;
            return amount;
        }
    }

    public void resetForNewHand() {
        holeCards.clear();
        currentBet = 0;
        isFolded = false;
        isAllIn = false;
    }

    public boolean isActive() {
        return !isFolded && !isAllIn && chips > 0;
    }

    public boolean isInHand() {
        return !isFolded;
    }

    @Override
    public String toString() {
        return name + " (Chips: " + chips + ")";
    }
}