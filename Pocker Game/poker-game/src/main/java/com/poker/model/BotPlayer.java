package com.poker.model;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BotPlayer extends Player {
    private final Random random;
    private final int aggressiveness;

    public BotPlayer(String name, int startingChips) {
        super(name, startingChips, true);
        this.random = new Random();
        this.aggressiveness = ThreadLocalRandom.current().nextInt(30, 80);
    }

    public BettingDecision makeDecision(int minBet, int potSize, int currentRoundBet) {
        try {
            Thread.sleep(1000 + random.nextInt(2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int roll = random.nextInt(100);
        
        if (roll < 10 && minBet > 0) {
            return BettingDecision.FOLD;
        }
        
        if (minBet == 0 && roll < 60) {
            return BettingDecision.CHECK;
        }
        
        if (minBet > 0 && roll < 50) {
            return BettingDecision.CALL;
        }
        
        if (roll < aggressiveness && canRaise(minBet)) {
            int raiseAmount = calculateRaiseAmount(minBet, potSize);
            return new BettingDecision(BettingDecision.Action.RAISE, raiseAmount);
        }
        
        if (minBet == 0) {
            return BettingDecision.CHECK;
        } else {
            return BettingDecision.CALL;
        }
    }

    private boolean canRaise(int minBet) {
        return getChips() > minBet * 2;
    }

    private int calculateRaiseAmount(int minBet, int potSize) {
        int minRaise = minBet * 2;
        int maxRaise = Math.min(getChips(), potSize);
        
        if (maxRaise <= minRaise) {
            return minRaise;
        }
        
        return minRaise + random.nextInt(maxRaise - minRaise);
    }

    public static class BettingDecision {
        public enum Action {
            FOLD, CHECK, CALL, RAISE
        }

        public static final BettingDecision FOLD = new BettingDecision(Action.FOLD, 0);
        public static final BettingDecision CHECK = new BettingDecision(Action.CHECK, 0);
        public static final BettingDecision CALL = new BettingDecision(Action.CALL, 0);

        private final Action action;
        private final int amount;

        public BettingDecision(Action action, int amount) {
            this.action = action;
            this.amount = amount;
        }

        public Action getAction() {
            return action;
        }

        public int getAmount() {
            return amount;
        }
    }
}