package com.poker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards;
    private final Random random;
    private int currentIndex;

    public Deck() {
        this.cards = new ArrayList<>();
        this.random = new Random();
        this.currentIndex = 0;
        initializeDeck();
    }

    private void initializeDeck() {
        cards.clear();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        currentIndex = 0;
    }

    public void shuffle() {
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Collections.swap(cards, i, j);
        }
        currentIndex = 0;
    }

    public Card dealCard() {
        if (currentIndex >= cards.size()) {
            return null;
        }
        return cards.get(currentIndex++);
    }

    public List<Card> dealCards(int count) {
        List<Card> dealtCards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card card = dealCard();
            if (card != null) {
                dealtCards.add(card);
            }
        }
        return dealtCards;
    }

    public int remainingCards() {
        return cards.size() - currentIndex;
    }

    public boolean hasCards() {
        return currentIndex < cards.size();
    }

    public void reset() {
        initializeDeck();
        shuffle();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards.subList(currentIndex, cards.size()));
    }
}