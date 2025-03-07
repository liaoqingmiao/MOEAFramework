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
package org.moeaframework.problem.ZDT;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.ProblemTest;

/**
 * Tests the ZDT problems.
 */
public class ZDTTest extends ProblemTest {

	/**
	 * Tests the ZDT1 problem.
	 * 
	 * @throws Exception should not occur
	 */
	@Test
	public void testZDT1() throws Exception {
		test("ZDT1");
	}

	/**
	 * Tests the ZDT2 problem.
	 * 
	 * @throws Exception should not occur
	 */
	@Test
	public void testZDT2() throws Exception {
		test("ZDT2");
	}

	/**
	 * Tests the ZDT3 problem.
	 * 
	 * @throws Exception should not occur
	 */
	@Test
	public void testZDT3() throws Exception {
		test("ZDT3");
	}

	/**
	 * Tests the ZDT4 problem.
	 * 
	 * @throws Exception should not occur
	 */
	@Test
	public void testZDT4() throws Exception {
		test("ZDT4");
	}

	/**
	 * Tests the ZDT5 problem.
	 * 
	 * @throws Exception should not occur
	 */
	@Test
	public void testZDT5() throws Exception {
		test("ZDT5");
	}

	/**
	 * Tests the ZDT6 problem.
	 * 
	 * @throws Exception should not occur
	 */
	@Test
	@Ignore("bug in JMetal 5.9 - fixed by https://github.com/jMetal/jMetal/commit/065a141510a3968cc03a8821a7977ee20614548b")
	public void testZDT6() throws Exception {
		test("ZDT6");
	}

	/**
	 * Tests the specified problem by comparing the results against the JMetal
	 * implementation.
	 * 
	 * @param problem the ZDT problem to be tested
	 * @throws Exception should not occur
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void test(String problem) throws Exception {
		org.uma.jmetal.problem.Problem problemA;
		Problem problemB;

		if (problem.equals("ZDT1")) {
			problemA = new org.uma.jmetal.problem.multiobjective.zdt.ZDT1();
			problemB = new org.moeaframework.problem.ZDT.ZDT1();
		} else if (problem.equals("ZDT2")) {
			problemA = new org.uma.jmetal.problem.multiobjective.zdt.ZDT2();
			problemB = new org.moeaframework.problem.ZDT.ZDT2();
		} else if (problem.equals("ZDT3")) {
			problemA = new org.uma.jmetal.problem.multiobjective.zdt.ZDT3();
			problemB = new org.moeaframework.problem.ZDT.ZDT3();
		} else if (problem.equals("ZDT4")) {
			problemA = new org.uma.jmetal.problem.multiobjective.zdt.ZDT4();
			problemB = new org.moeaframework.problem.ZDT.ZDT4();
		} else if (problem.equals("ZDT5")) {
			problemA = new org.uma.jmetal.problem.multiobjective.zdt.ZDT5();
			problemB = new org.moeaframework.problem.ZDT.ZDT5();
		} else if (problem.equals("ZDT6")) {
			problemA = new org.uma.jmetal.problem.multiobjective.zdt.ZDT6();
			problemB = new org.moeaframework.problem.ZDT.ZDT6();
		} else {
			throw new IllegalArgumentException();
		}

		test(problemA, problemB);
	}

}
