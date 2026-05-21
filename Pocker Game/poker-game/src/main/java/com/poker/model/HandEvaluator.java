package com.poker.model;

import java.util.*;
import java.util.stream.Collectors;

public class HandEvaluator {

    public static Hand evaluateHand(List<Card> cards) {
        if (cards == null || cards.size() < 5) {
            throw new IllegalArgumentException("Need at least 5 cards to evaluate");
        }

        List<List<Card>> combinations = generateCombinations(cards, 5);
        
        Hand bestHand = null;
        for (List<Card> combo : combinations) {
            Hand currentHand = evaluateFiveCards(combo);
            if (bestHand == null || compareHands(currentHand, bestHand) > 0) {
                bestHand = currentHand;
            }
        }

        return bestHand;
    }

    private static Hand evaluateFiveCards(List<Card> cards) {
        List<Card> sortedCards = cards.stream()
            .sorted(Comparator.comparing((Card c) -> c.getRank().getValue()).reversed())
            .collect(Collectors.toList());

        boolean isFlush = isFlush(sortedCards);
        
        boolean isStraight = isStraight(sortedCards);
        boolean isRoyalFlush = isRoyalFlush(sortedCards);
        boolean isStraightFlush = isStraightFlush(sortedCards);

        Map<Card.Rank, Long> rankCounts = sortedCards.stream()
            .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));

        boolean isFourOfAKind = rankCounts.containsValue(4L);
        boolean isThreeOfAKind = rankCounts.containsValue(3L);
        long pairCount = rankCounts.values().stream().filter(count -> count == 2L).count();
        boolean isTwoPair = pairCount >= 2;
        boolean isOnePair = pairCount == 1;
        boolean isFullHouse = isThreeOfAKind && isOnePair;

        Hand hand = new Hand(sortedCards);

        if (isRoyalFlush) {
            hand.setRank(Hand.HandRank.ROYAL_FLUSH);
            sortedCards.forEach(hand::addRankCard);
        } else if (isStraightFlush) {
            hand.setRank(Hand.HandRank.STRAIGHT_FLUSH);
            sortedCards.forEach(hand::addRankCard);
        } else if (isFourOfAKind) {
            hand.setRank(Hand.HandRank.FOUR_OF_A_KIND);
            setRankCardsAndKickers(hand, rankCounts, 4, 1);
        } else if (isFullHouse) {
            hand.setRank(Hand.HandRank.FULL_HOUSE);
            setRankCardsAndKickers(hand, rankCounts, 3, 2);
        } else if (isFlush) {
            hand.setRank(Hand.HandRank.FLUSH);
            sortedCards.forEach(hand::addRankCard);
        } else if (isStraight) {
            hand.setRank(Hand.HandRank.STRAIGHT);
            sortedCards.forEach(hand::addRankCard);
        } else if (isThreeOfAKind) {
            hand.setRank(Hand.HandRank.THREE_OF_A_KIND);
            setRankCardsAndKickers(hand, rankCounts, 3);
        } else if (isTwoPair) {
            hand.setRank(Hand.HandRank.TWO_PAIR);
            setRankCardsAndKickers(hand, rankCounts, 2, 2, 1);
        } else if (isOnePair) {
            hand.setRank(Hand.HandRank.PAIR);
            setRankCardsAndKickers(hand, rankCounts, 2);
        } else {
            hand.setRank(Hand.HandRank.HIGH_CARD);
            sortedCards.forEach(hand::addRankCard);
        }

        return hand;
    }

    private static void setRankCardsAndKickers(Hand hand, Map<Card.Rank, Long> rankCounts, int... counts) {
        List<Card> sortedCards = hand.getCards();
        List<Card> rankCards = new ArrayList<>();
        List<Card> kickers = new ArrayList<>();

        for (int count : counts) {
            rankCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == count)
                .sorted((e1, e2) -> Long.compare(e2.getKey().getValue(), e1.getKey().getValue()))
                .forEach(entry -> {
                    sortedCards.stream()
                        .filter(card -> card.getRank() == entry.getKey())
                        .forEach(rankCards::add);
                });
        }

        Set<Card.Rank> usedRanks = rankCards.stream().map(Card::getRank).collect(Collectors.toSet());
        sortedCards.stream()
            .filter(card -> !usedRanks.contains(card.getRank()))
            .limit(5 - rankCards.size())
            .forEach(kickers::add);

        rankCards.forEach(hand::addRankCard);
        kickers.forEach(hand::addKicker);
    }

    private static boolean isFlush(List<Card> cards) {
        Card.Suit firstSuit = cards.get(0).getSuit();
        return cards.stream().allMatch(card -> card.getSuit() == firstSuit);
    }

    private static boolean isStraight(List<Card> cards) {
        boolean regularStraight = checkRegularStraight(cards);
        if (regularStraight) return true;

        return checkWheel(cards);
    }

    private static boolean checkRegularStraight(List<Card> cards) {
        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i).getRank().getValue() - cards.get(i + 1).getRank().getValue() != 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkWheel(List<Card> cards) {
        return cards.stream().map(Card::getRank).collect(Collectors.toSet()).equals(
            new HashSet<>(Arrays.asList(
                Card.Rank.ACE, Card.Rank.TWO, Card.Rank.THREE, 
                Card.Rank.FOUR, Card.Rank.FIVE
            ))
        );
    }

    private static boolean isRoyalFlush(List<Card> cards) {
        return isFlush(cards) && 
               cards.stream().allMatch(card -> card.getRank().getValue() >= 10) &&
               isStraight(cards);
    }

    private static boolean isStraightFlush(List<Card> cards) {
        return isFlush(cards) && isStraight(cards);
    }

    public static int compareHands(Hand hand1, Hand hand2) {
        int rankComparison = Integer.compare(hand1.getRank().getValue(), hand2.getRank().getValue());
        if (rankComparison != 0) {
            return rankComparison;
        }

        List<Card> cards1 = hand1.getRankCards();
        List<Card> cards2 = hand2.getRankCards();

        for (int i = 0; i < Math.min(cards1.size(), cards2.size()); i++) {
            int cardComparison = Integer.compare(
                cards1.get(i).getRank().getValue(),
                cards2.get(i).getRank().getValue()
            );
            if (cardComparison != 0) {
                return cardComparison;
            }
        }

        List<Card> kickers1 = hand1.getKickers();
        List<Card> kickers2 = hand2.getKickers();

        for (int i = 0; i < Math.min(kickers1.size(), kickers2.size()); i++) {
            int kickerComparison = Integer.compare(
                kickers1.get(i).getRank().getValue(),
                kickers2.get(i).getRank().getValue()
            );
            if (kickerComparison != 0) {
                return kickerComparison;
            }
        }

        return 0;
    }

    private static <T> List<List<T>> generateCombinations(List<T> elements, int k) {
        List<List<T>> result = new ArrayList<>();
        generateCombinationsHelper(elements, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void generateCombinationsHelper(List<T> elements, int k, int start, 
                                                      List<T> current, List<List<T>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < elements.size(); i++) {
            current.add(elements.get(i));
            generateCombinationsHelper(elements, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}