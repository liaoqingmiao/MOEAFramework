/* Copyright 2009-2022 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.comparator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.Solution;

/**
 * Tests the {@code LexicographicalComparator} class.
 */
public class LexicographicalComparatorTest {

	/**
	 * The lexicographical comparator used for testing.
	 */
	private LexicographicalComparator comparator;

	/**
	 * Setup the comparator for use by all test methods.
	 */
	@Before
	public void setUp() {
		comparator = new LexicographicalComparator();
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		comparator = null;
	}

	/**
	 * Tests if the comparator correctly detects dominance.
	 */
	@Test
	public void testDominance() {
		Solution solution1 = new Solution(new double[] { 0, 0 });
		Solution solution2 = new Solution(new double[] { 0, 1 });
		Solution solution3 = new Solution(new double[] { 1, 0 });

		Assert.assertTrue(comparator.compare(solution1, solution2) < 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) > 0);
		Assert.assertTrue(comparator.compare(solution1, solution3) < 0);
		Assert.assertTrue(comparator.compare(solution3, solution1) > 0);
		Assert.assertTrue(comparator.compare(solution2, solution3) < 0);
		Assert.assertTrue(comparator.compare(solution3, solution2) > 0);
	}

	/**
	 * Tests if the comparator correctly detects non-dominance.
	 */
	@Test
	public void testNondominance() {
		Solution solution1 = new Solution(new double[] { 0, 0 });
		Solution solution2 = new Solution(new double[] { 0, 0 });

		Assert.assertTrue(comparator.compare(solution1, solution2) == 0);
		Assert.assertTrue(comparator.compare(solution2, solution1) == 0);
	}

}
