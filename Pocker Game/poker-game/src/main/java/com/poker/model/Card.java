package com.poker.model;

import java.io.Serializable;

public class Card implements Serializable {
    private static final long serialVersionUID = 1L;
    public enum Suit {
        HEARTS("♥", "Hearts"),
        DIAMONDS("♦", "Diamonds"),
        CLUBS("♣", "Clubs"),
        SPADES("♠", "Spades");

        private final String symbol;
        private final String displayName;

        Suit(String symbol, String displayName) {
            this.symbol = symbol;
            this.displayName = displayName;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Rank {
        TWO("2", 2),
        THREE("3", 3),
        FOUR("4", 4),
        FIVE("5", 5),
        SIX("6", 6),
        SEVEN("7", 7),
        EIGHT("8", 8),
        NINE("9", 9),
        TEN("10", 10),
        JACK("J", 11),
        QUEEN("Q", 12),
        KING("K", 13),
        ACE("A", 14);

        private final String symbol;
        private final int value;

        Rank(String symbol, int value) {
            this.symbol = symbol;
            this.value = value;
        }

        public String getSymbol() {
            return symbol;
        }

        public int getValue() {
            return value;
        }
    }

    private final Suit suit;
    private final Rank rank;
    private boolean faceUp;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.faceUp = false;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }

    public String toFullString() {
        return rank.name() + " of " + suit.getDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Card card = (Card) obj;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return 31 * suit.hashCode() + rank.hashCode();
    }
}