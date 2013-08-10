// Copyright 2012, Google Inc.  All Rights Reserved.

package org.willden.wordmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for WordMapper
 *
 * @author shawn@willden.org (Shawn Willden)
 */
public class WordMapperTest {

    private static final Dictionary TINY_DICTIONARY = new Dictionary() {
	private String[] WORDS = { "0", "1" };

	@Override
	public String[] getWords() {
	    return WORDS;
	}
    };

    private WordMapper mapper;

    @Before
    public void setUp() {
	mapper = new WordMapper(TINY_DICTIONARY);
    }

    /**
     * By testing with a very small dictionary, we guarantee coverage of all of
     * the boundary cases that have to do with the "edges" of the dictionary.
     * Besides, it's kind of cool that we can easily implement a binary counter.
     */
    @Test
    public void testIntMapping() {
	for (int i = 0; i < 256; ++i) {
	    String words = mapper.intToWords(i);
	    assertEquals(i, mapper.wordsToInt(words));
	}
    }

    /**
     * Test integer mapping behavior at the Integer boundary.
     */
    @Test
    public void testIntegerMax() {
	String words = mapper.intToWords(Integer.MAX_VALUE);
	assertEquals(Integer.MAX_VALUE, mapper.wordsToInt(words));

	words = mapper.longToWords(Integer.MAX_VALUE + 1L);
	try {
	    mapper.wordsToInt(words);
	    fail("Should throw");
	} catch (NumberFormatException e) {
	}
    }

    /**
     * Test mapping of negative values.
     */
    @Test
    public void testNegativeValues() {
	try {
	    mapper.intToWords(-1);
	    fail("Should throw");
	} catch (RuntimeException e) {
	}
	try {
	    mapper.longToWords(-1L);
	    fail("Should throw");
	} catch (RuntimeException e) {
	}
	try {
	    mapper.bigToWords(BigInteger.ONE.negate());
	    fail("Should throw");
	} catch (RuntimeException e) {
	}
    }

    /**
     * Long integer smoke test
     */
    @Test
    public void testLongMapping() {
	for (long i = Integer.MAX_VALUE; i < Integer.MAX_VALUE + 256; ++i) {
	    String words = mapper.longToWords(i);
	    assertEquals(i, mapper.wordsToLong(words));
	}
    }

    /**
     * BigInteger smoke test
     */
    @Test
    public void testBigIntegerMapping() {
	BigInteger start = new BigInteger(Long.toString(Long.MAX_VALUE));
	BigInteger end = start.add(new BigInteger("256"));
	for (BigInteger i = start; i.compareTo(end) < 0;) {
	    String words = mapper.bigToWords(i);
	    assertEquals(i, mapper.wordsToBigInteger(words));
	    i = i.add(BigInteger.ONE);
	}
    }

    /**
     * Standard dictionaries tests. Just verify some known results, so the test
     * breaks if something changes the outputs.
     */
    @Test
    public void testStandardDictionaries() {
	mapper = new WordMapper(new LargeDictionary());
	long jennysNumber = 5558675309L;
	String words = mapper.longToWords(jennysNumber);
	assertEquals("benefits float language", words);

	mapper = new WordMapper(new SmallDictionary());
	words = mapper.longToWords(jennysNumber);
	assertEquals("about fill liar mail", words);
	System.out.println(mapper.longToWords(3506928));
    }

}
