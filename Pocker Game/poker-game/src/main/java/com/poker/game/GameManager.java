package com.poker.game;

import com.poker.model.*;

import java.util.*;


public class GameManager {
    public enum GamePhase {
        PRE_FLOP, FLOP, TURN, RIVER, SHOWDOWN
    }

    private List<Player> players;
    private Deck deck;
    private List<Card> communityCards;
    private BettingManager bettingManager;
    private GamePhase currentPhase;
    private int currentPlayerIndex;
    private int dealerIndex;
    private boolean isPaused;

    public GameManager(List<Player> players, int smallBlind, int bigBlind) {
        this.players = new ArrayList<>(players);
        this.deck = new Deck();
        this.communityCards = new ArrayList<>();
        this.bettingManager = new BettingManager(smallBlind, bigBlind);
        this.currentPhase = GamePhase.PRE_FLOP;
        this.currentPlayerIndex = 0;
        this.dealerIndex = 0;
        this.isPaused = false;

        if (!players.isEmpty()) {
            players.get(dealerIndex).setDealer(true);
        }
    }

    public void startNewHand() {
        deck.reset();
        communityCards.clear();
        bettingManager.resetForNewHand();
        currentPhase = GamePhase.PRE_FLOP;

        for (Player player : players) {
            player.resetForNewHand();
            player.incrementGamesPlayed();
        }

        if (!players.isEmpty()) {
            players.get(dealerIndex).setDealer(false);
            dealerIndex = (dealerIndex + 1) % players.size();
            players.get(dealerIndex).setDealer(true);
        }

        dealHoleCards();

        postBlinds();

        currentPlayerIndex = (dealerIndex + 3) % players.size();
    }

    private void dealHoleCards() {
        for (Player player : players) {
            if (!player.isBot() || player.getChips() > 0) {
                player.addHoleCard(deck.dealCard());
                player.addHoleCard(deck.dealCard());
            }
        }
    }

    private void postBlinds() {
        int smallBlindIndex = (dealerIndex + 1) % players.size();
        int bigBlindIndex = (dealerIndex + 2) % players.size();
        
        Player sbPlayer = players.get(smallBlindIndex);
        Player bbPlayer = players.get(bigBlindIndex);
        
        bettingManager.postSmallBlind(sbPlayer);
        bettingManager.postBigBlind(bbPlayer);
    }

    public void nextPhase() {
        bettingManager.resetBettingRound();

        switch (currentPhase) {
            case PRE_FLOP:
                currentPhase = GamePhase.FLOP;
                dealCommunityCards(3);
                break;
            case FLOP:
                currentPhase = GamePhase.TURN;
                dealCommunityCards(1);
                break;
            case TURN:
                currentPhase = GamePhase.RIVER;
                dealCommunityCards(1);
                break;
            case RIVER:
                currentPhase = GamePhase.SHOWDOWN;
                break;
            case SHOWDOWN:
                startNewHand();
                break;
        }

        if (currentPhase != GamePhase.PRE_FLOP) {
            currentPlayerIndex = (dealerIndex + 1) % players.size();
        }
    }

    private void dealCommunityCards(int count) {
        for (int i = 0; i < count; i++) {
            Card card = deck.dealCard();
            if (card != null) {
                card.setFaceUp(true);
                communityCards.add(card);
            }
        }
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (!players.get(currentPlayerIndex).isInHand());
    }

    public boolean processAction(Player.Action action, int amount) {
        Player player = getCurrentPlayer();
        
        if (!player.isInHand()) {
            nextPlayer();
            return false;
        }
        
        switch (action) {
            case FOLD:
                player.fold();
                break;
            case CHECK:
                if (!bettingManager.canCheck(player)) {
                    return false;
                }
                break;
            case CALL:
                bettingManager.processCall(player);
                break;
            case RAISE:
                bettingManager.processRaise(player, amount);
                break;
            case ALL_IN:
                bettingManager.processAllIn(player);
                break;
        }

        if (getActivePlayerCount() <= 1) {
            currentPhase = GamePhase.SHOWDOWN;
            return true;
        }

        if (bettingManager.isBettingRoundComplete(players)) {
            nextPhase();
        } else {
            nextPlayer();
        }
        
        return true;
    }

    public int getActivePlayerCount() {
        return (int) players.stream().filter(Player::isInHand).count();
    }

    public List<Player> determineWinners() {
        List<Player> winners = new ArrayList<>();
        Hand bestHand = null;
        
        for (Player player : players) {
            if (!player.isInHand()) {
                continue;
            }

            List<Card> allCards = new ArrayList<>();
            allCards.addAll(player.getHoleCards());
            allCards.addAll(communityCards);

            Hand currentHand = HandEvaluator.evaluateHand(allCards);

            if (bestHand == null || HandEvaluator.compareHands(currentHand, bestHand) > 0) {
                bestHand = currentHand;
                winners.clear();
                winners.add(player);
            } else if (HandEvaluator.compareHands(currentHand, bestHand) == 0) {
                winners.add(player);
            }
        }

        int winAmount = bettingManager.getCurrentPot() / winners.size();
        for (Player winner : winners) {
            winner.setChips(winner.getChips() + winAmount);
            winner.incrementWins();
        }

        for (Player player : players) {
            if (!winners.contains(player) && player.isInHand()) {
                player.incrementLosses();
            }
        }
        
        return winners;
    }

    public List<Card> getCommunityCards() {
        return new ArrayList<>(communityCards);
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public int getPotSize() {
        return bettingManager.getCurrentPot();
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public BettingManager getBettingManager() {
        return bettingManager;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }
    public void removePlayer(Player player) {
        players.remove(player);
    }
    public boolean isHandComplete() {
        return currentPhase == GamePhase.SHOWDOWN || getActivePlayerCount() <= 1;
    }
}