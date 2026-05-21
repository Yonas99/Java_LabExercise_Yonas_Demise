package com.poker.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Hand {
    public enum HandRank {
        HIGH_CARD(1, "High Card"),
        PAIR(2, "Pair"),
        TWO_PAIR(3, "Two Pair"),
        THREE_OF_A_KIND(4, "Three of a Kind"),
        STRAIGHT(5, "Straight"),
        FLUSH(6, "Flush"),
        FULL_HOUSE(7, "Full House"),
        FOUR_OF_A_KIND(8, "Four of a Kind"),
        STRAIGHT_FLUSH(9, "Straight Flush"),
        ROYAL_FLUSH(10, "Royal Flush");

        private final int value;
        private final String displayName;

        HandRank(int value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }

        public int getValue() {
            return value;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private final List<Card> cards;
    private HandRank rank;
    private List<Card> rankCards;
    private List<Card> kickers;

    public Hand(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
        this.rankCards = new ArrayList<>();
        this.kickers = new ArrayList<>();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public HandRank getRank() {
        return rank;
    }

    public void setRank(HandRank rank) {
        this.rank = rank;
    }

    public List<Card> getRankCards() {
        return new ArrayList<>(rankCards);
    }

    public void addRankCard(Card card) {
        rankCards.add(card);
    }

    public List<Card> getKickers() {
        return new ArrayList<>(kickers);
    }

    public void addKicker(Card card) {
        kickers.add(card);
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public int size() {
        return cards.size();
    }

    public void clear() {
        cards.clear();
        rankCards.clear();
        kickers.clear();
        rank = null;
    }

    @Override
    public String toString() {
        if (rank == null) {
            return "Cards: " + cards.stream()
                .map(Card::toString)
                .collect(Collectors.joining(", "));
        }
        return rank.getDisplayName() + " - " + rankCards.stream()
            .map(Card::toString)
            .collect(Collectors.joining(", "));
    }

    public static Comparator<Hand> getHandComparator() {
        return (h1, h2) -> {
            int rankComparison = Integer.compare(h1.getRank().getValue(), h2.getRank().getValue());
            if (rankComparison != 0) {
                return rankComparison;
            }

            return compareCards(h1.getRankCards(), h2.getRankCards());
        };
    }

    private static int compareCards(List<Card> cards1, List<Card> cards2) {
        for (int i = 0; i < Math.min(cards1.size(), cards2.size()); i++) {
            int cardComparison = Integer.compare(
                cards1.get(i).getRank().getValue(),
                cards2.get(i).getRank().getValue()
            );
            if (cardComparison != 0) {
                return cardComparison;
            }
        }
        return 0;
    }
}