package com.poker.game;

import com.poker.model.Player;

import java.util.List;

public class BettingManager {
    private int currentPot;
    private int currentBet;
    private int smallBlind;
    private int bigBlind;
    private int minRaise;

    public BettingManager(int smallBlind, int bigBlind) {
        this.smallBlind = smallBlind;
        this.bigBlind = bigBlind;
        this.currentPot = 0;
        this.currentBet = 0;
        this.minRaise = bigBlind;
    }

    public void resetBettingRound() {
        currentBet = 0;
        minRaise = bigBlind;
    }

    public void resetForNewHand() {
        currentPot = 0;
        currentBet = 0;
        minRaise = bigBlind;
    }

    public int postSmallBlind(Player player) {
        int blindAmount = Math.min(smallBlind, player.getChips());
        int actualBet = player.placeBet(blindAmount);
        currentPot += actualBet;
        currentBet = actualBet;
        return actualBet;
    }

    public int postBigBlind(Player player) {
        int blindAmount = Math.min(bigBlind, player.getChips());
        int actualBet = player.placeBet(blindAmount);
        currentPot += actualBet;
        currentBet = Math.max(currentBet, actualBet);
        return actualBet;
    }

    public int processCall(Player player) {
        int callAmount = currentBet - player.getCurrentBet();
        int actualCall = player.placeBet(callAmount);
        currentPot += actualCall;
        return actualCall;
    }

    public int processRaise(Player player, int raiseAmount) {
        if (raiseAmount < minRaise) {
            throw new IllegalArgumentException("Raise must be at least " + minRaise);
        }

        int totalBet = currentBet + raiseAmount;
        int amountToCall = totalBet - player.getCurrentBet();
        int actualRaise = player.placeBet(amountToCall);

        currentPot += actualRaise;
        currentBet = totalBet;
        minRaise = raiseAmount;

        return actualRaise;
    }

    public int processAllIn(Player player) {
        int allInAmount = player.getChips();
        int actualBet = player.placeBet(allInAmount);
        
        currentPot += actualBet;
        player.setAllIn(true);
        
        if (player.getCurrentBet() > currentBet) {
            currentBet = player.getCurrentBet();
        }
        
        return actualBet;
    }

    public boolean canCheck(Player player) {
        return player.getCurrentBet() >= currentBet || currentBet == 0;
    }

    public boolean isBettingRoundComplete(List<Player> players) {
        if (players.isEmpty()) {
            return true;
        }

        boolean allMatched = players.stream()
            .filter(Player::isInHand)
            .allMatch(p -> p.getCurrentBet() == currentBet || p.isAllIn());

        boolean someoneActed = players.stream()
            .filter(Player::isInHand)
            .anyMatch(p -> p.getCurrentBet() > 0);

        return allMatched && someoneActed;
    }

    public int getCurrentPot() {
        return currentPot;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public int getMinRaise() {
        return minRaise;
    }

    public int getSmallBlind() {
        return smallBlind;
    }

    public int getBigBlind() {
        return bigBlind;
    }

    public void addToPot(int amount) {
        currentPot += amount;
    }

    public int getCallAmount(Player player) {
        return currentBet - player.getCurrentBet();
    }
}