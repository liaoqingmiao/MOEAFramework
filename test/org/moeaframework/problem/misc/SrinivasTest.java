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
package org.moeaframework.problem.misc;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.problem.ProblemTest;

public class SrinivasTest extends ProblemTest {

	@Test
	@Ignore("JMetal 5.9 modifies the constraint values")
	public void testJMetal() throws Exception {
		test(new org.uma.jmetal.problem.multiobjective.Srinivas(), new Srinivas());
	}
	
	@Test
	public void test() {
		Problem problem = new Srinivas();
		
		Assert.assertArrayEquals(new double[] { 7.0, -1.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 0.0, 10.0 }, 
				TestUtils.evaluateAt(problem, 0.0, 0.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 927.0, -621.0 }, 
				TestUtils.evaluateAt(problem, -20.0, -20.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 575.0, 50.0 }, 
				TestUtils.evaluateAt(problem, -20.0, -20.0).getConstraints(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 687.0, -181.0 }, 
				TestUtils.evaluateAt(problem, 20.0, 20.0).getObjectives(),
				Settings.EPS);
		
		Assert.assertArrayEquals(new double[] { 575.0, 0.0 }, 
				TestUtils.evaluateAt(problem, 20.0, 20.0).getConstraints(),
				Settings.EPS);
	}

}
