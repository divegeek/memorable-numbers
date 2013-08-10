// Copyright 2012, Google Inc.  All Rights Reserved.

package org.willden.wordmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Maps non-negative integral values to and from a word-based representation.  This enables
 * programs to present user-memorable and therefore user-friendly representations of numeric
 * identifiers.  User ID numbers, telephone numbers, support ticket numbers, etc. etc.
 *
 * For example, a help desk support ticket #28223847 would translate to "admit radar could",
 * an odd sequence of words, but much more memorable than the number, and for most touch
 * typists, faster to type as well.
 *
 * @author shawn@willden.org (Shawn Willden)
 */
public class WordMapper {
    private final Map<String, Integer> wordMap;
    private final String[] dictionary;
    private final BigInteger dictLength;

    /**
     * Creates a WordMapper instance that uses the large, 4096-word, default dictionary.
     */
    public WordMapper() {
        this(new LargeDictionary());
    }

    /**
     * Creates a WordMapper instance that uses a dictionary read from the specified
     * {@link Reader}.  It expects one word per line, and the words must be distinct
     * and contain no embedded whitespace.
     */
    public WordMapper(Reader input) throws IOException {
        this(parseDictionary(input));
    }

    /**
     * Creates a WordMapper instance with the provided dictionary.  The elements of the
     * dictionary must contain no embedded whitespace.
     */
    public WordMapper(Dictionary dict) {
        dictionary = dict.getWords();
        HashMap<String, Integer> wordMap1 = new HashMap<String, Integer>();
        for (int i = 0; i < dictionary.length; ++i) {
            wordMap1.put(dictionary[i], i);
        }
        wordMap = wordMap1;
        dictLength = new BigInteger(Integer.toString(dictionary.length));
    }

    /**
     * Converts the provided string of words into an integer.
     *
     * @throws NumberFormatException if one of the words is not in the dictionary, or if
     * the integer would overflow.
     */
    public int wordsToInt(String words) throws NumberFormatException {
        long result = wordsToLong(words);
        if (result > Integer.MAX_VALUE) {
            throw new NumberFormatException(words + " is out of integer range.");
        }
        return (int) result;
    }

    /**
     * Converts the provided string of words into a long.
     *
     * @throws NumberFormatException if one of the words is not in the dictionary, or if
     * the long would overflow.
     */
    public long wordsToLong(String words) throws NumberFormatException {
        long result = 0;
        for (String word : new WordIterator(words)) {
            long newResult = result * dictionary.length;
            newResult += mapWord(word);
            if (newResult < result) {
                throw new NumberFormatException(words + "cannot be represented as a long");
            }
            result = newResult;
        }
        return result;
    }

    /**
     * Converts the provided string of words into a BigInteger.
     *
     * @throws NumberFormatException if one of the words is not in the dictionary.
     */
    public BigInteger wordsToBigInteger(String words) throws NumberFormatException {
        BigInteger result = BigInteger.ZERO;
        for (String word : new WordIterator(words)) {
            result = result.multiply(dictLength);
            result = result.add(new BigInteger(Integer.toString(mapWord(word))));
        }
        return result;
    }

    /**
     * Converts the provided long into a string of words
     *
     * @throws RuntimeException if value is negative.
     */
    public String longToWords(long value) {
        assertNonNegative(value);
        LinkedList<String> words = new LinkedList<String>();
        do {
            int componentValue = (int) (value % dictionary.length);
            words.push(dictionary[componentValue]);
            value /= dictionary.length;
        } while (value > 0);
        return join(words, " ");
    }

    /**
     * Converts the provided BigInteger into a string of words
     *
     * @throws RuntimeException if value is negative.
     */
    public String bigToWords(BigInteger value) {
        assertNonNegative(value);
        LinkedList<String> words = new LinkedList<String>();
        do {
            BigInteger[] divAndRemainder = value.divideAndRemainder(dictLength);
            words.push(dictionary[divAndRemainder[1].intValue()]);
            value = divAndRemainder[0];
        } while (value.compareTo(BigInteger.ZERO) > 0);
        return join(words, " ");
    }

    /**
     * Converts the provided integer into a string of words
     *
     * @throws RuntimeException if value is negative.
     */
    public String intToWords(int value) {
        return longToWords(value);
    }

    private String join(List<String> words, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (Iterator<String> iter = words.iterator(); iter.hasNext();) {
            builder.append(iter.next());
            if (iter.hasNext()) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    private void assertNonNegative(long value) {
        if (value < 0) {
            throw new RuntimeException("WordMapper does not support negative values");
        }
    }

    private void assertNonNegative(BigInteger value) {
        if (value.compareTo(BigInteger.ZERO) < 0) {
            throw new RuntimeException("WordMapper does not support negative values");
        }
    }

    private static Dictionary parseDictionary(Reader input) throws IOException {
        final ArrayList<String> dictionaryList = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(input);
        String word;
        while ((word = reader.readLine()) != null) {
            dictionaryList.add(word);
        }

        return new Dictionary() {

            @Override
            public String[] getWords() {
                return dictionaryList
                        .toArray(new String[dictionaryList.size()]);
            }
        };
    }

    private int mapWord(String word) {
        Integer value = wordMap.get(word);
        if (value == null) {
            throw new NumberFormatException(word + " is not a valid number word");
        }
        return value;
    }

    private static final class WordIterator implements Iterable<String> {

        private final StringTokenizer tokenizer;

        WordIterator(final String words) {
            tokenizer = new StringTokenizer(words);
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {

                @Override
                public boolean hasNext() {
                    return tokenizer.hasMoreTokens();
                }

                @Override
                public String next() {
                    return tokenizer.nextToken();
                }

                @Override
                public void remove() {
                    throw new RuntimeException("Removal is not implemented");
                }
            };
        }
    }
}
