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
package org.moeaframework.core.indicator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.algorithm.jmetal.JMetalUtils;
import org.moeaframework.algorithm.jmetal.ProblemAdapter;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.util.FrontNormalizer;

/**
 * Tests the {@link Hypervolume} class against the JMetal implementation. Due
 * to performance, these tests only go up to 4 dimensions.
 */
public class HypervolumeTest extends IndicatorTest {
	
	/**
	 * Tests if an exception is thrown when using an empty reference set.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyReferenceSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		NondominatedPopulation approximationSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");

		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		hypervolume.evaluate(approximationSet);
	}
	
	/**
	 * Tests if an empty approximation set returns a hypervolume of zero.
	 */
	@Test
	public void testEmptyApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
	}
	
	/**
	 * Tests if infeasible solutions are properly ignored.
	 */
	@Test
	public void testInfeasibleApproximationSet() {
		Problem problem = ProblemFactory.getInstance().getProblem("CF1");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("CF1");
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Solution solution = problem.newSolution();
		solution.setObjectives(new double[] { 0.5, 0.5 });
		solution.setConstraints(new double[] { 10.0 });
		approximationSet.add(solution);

		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
	}
	
	public void test(Hypervolume hypervolume) {
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		Assert.assertEquals(0.25, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 0.0));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(1.0, 1.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(2.0, 2.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(-0.5, -0.5));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.5, 0.0));
		approximationSet.add(TestUtils.newSolution(0.0, 0.5));
		Assert.assertEquals(0.75, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
	}

	/**
	 * Runs through some simple cases to ensure the hypervolume is computed
	 * correctly.
	 */
	@Test
	public void testSimple() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));
		
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		
		test(hypervolume);
	}
	
	@Test
	public void testExplicitBounds() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		Hypervolume hypervolume = new Hypervolume(problem,
				new double[] { 0.0, 0.0 },
				new double[] { 1.0, 1.0 });
		
		test(hypervolume);
	}
	
	public void test2(Hypervolume hypervolume) {
		NondominatedPopulation approximationSet = new NondominatedPopulation();
		
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		// target value is 1.5^2 / 2^2
		approximationSet.add(TestUtils.newSolution(0.5, 0.5));
		Assert.assertEquals(0.5625, hypervolume.evaluate(approximationSet),
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(0.0, 0.0));
		Assert.assertEquals(1.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(1.0, 1.0));
		Assert.assertEquals(0.25, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
		
		approximationSet.clear();
		approximationSet.add(TestUtils.newSolution(2.0, 2.0));
		Assert.assertEquals(0.0, hypervolume.evaluate(approximationSet), 
				Settings.EPS);
	}
	
	@Test
	public void testExplicitBounds2_RefOnly() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		NondominatedPopulation referenceSet = new NondominatedPopulation();
		referenceSet.add(TestUtils.newSolution(0.0, 1.0));
		referenceSet.add(TestUtils.newSolution(1.0, 0.0));
		
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet,
				new double[] { 2.0, 2.0 });
		
		test2(hypervolume);
	}
	
	@Test
	public void testExplicitBounds2_Both() {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		Hypervolume hypervolume = new Hypervolume(problem,
				new double[] { 0.0, 0.0 },
				new double[] { 2.0, 2.0 });
		
		test2(hypervolume);
	}
	
	@Test
	public void testExplicitBounds2_Properties() {
		Settings.PROPERTIES.setDouble("org.moeaframework.core.indicator.hypervolume_idealpt.DTLZ2", 0.0);
		Settings.PROPERTIES.setDouble("org.moeaframework.core.indicator.hypervolume_refpt.DTLZ2", 2.0);
		
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		Hypervolume hypervolume = new Hypervolume(problem, new NondominatedPopulation());
		
		test2(hypervolume);
		
		Settings.PROPERTIES.remove("org.moeaframework.core.indicator.hypervolume_idealpt.DTLZ2");
		Settings.PROPERTIES.remove("org.moeaframework.core.indicator.hypervolume_refpt.DTLZ2");
	}

	/**
	 * Tests the hypervolume calculation on a 2D continuous Pareto front.
	 */
	@Test
	public void testDTLZ2_2D() {
		test("DTLZ2_2");
	}

	/**
	 * Tests the hypervolume calculation on a 4D continuous Pareto front.
	 */
	@Test
	public void testDTLZ2_4D() {
		test("DTLZ2_4");
	}

	/**
	 * Tests the hypervolume calculation on a 6D continuous Pareto front.
	 */
	@Test
	public void testDTLZ2_6D() {
		test("DTLZ2_6");
	}

	/**
	 * Tests the hypervolume calculation on a 8D continuous Pareto front.
	 */
	@Test
	public void testDTLZ2_8D() {
		test("DTLZ2_8");
	}

	/**
	 * Tests the hypervolume calculation on a 2D disconnected Pareto front.
	 */
	@Test
	public void testDTLZ7_2D() {
		test("DTLZ7_2");
	}

	/**
	 * Tests the hypervolume calculation on a 4D disconnected Pareto front.
	 */
	@Test
	public void testDTLZ7_4D() {
		test("DTLZ7_4");
	}

	/**
	 * Tests the hypervolume calculation on a 6D disconnected Pareto front.
	 */
	@Test
	public void testDTLZ7_6D() {
		test("DTLZ7_6");
	}

	/**
	 * Tests the hypervolume calculation on a 8D disconnected Pareto front.
	 */
	@Test
	public void testDTLZ7_8D() {
		test("DTLZ7_8");
	}

	/**
	 * Generates a subset of the reference set and tests if the hypervolume
	 * metric is computed correctly. Evaluating only the subset is necessary
	 * for performance at higher dimensions.
	 * 
	 * @param problemName the problem being tested
	 * @throws IOException should not occur
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void test(String problemName) {
		Problem problem = ProblemFactory.getInstance().getProblem(problemName);
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet(problemName);
		NondominatedPopulation approximationSet = new NondominatedPopulation();

		for (int i = 0; i < 25; i++) {
			approximationSet.add(referenceSet.get(PRNG.nextInt(referenceSet
					.size())));
		}
		
		ProblemAdapter adapter = JMetalUtils.createProblemAdapter(problem);
		Front theirReferenceSet = JMetalUtils.toFront(adapter, referenceSet);
		List theirApproximationSet = JMetalUtils.toSolutionSet(adapter, approximationSet);
		FrontNormalizer normalizer = new FrontNormalizer(theirReferenceSet);

		Hypervolume myHypervolume = new Hypervolume(problem, referenceSet);
		org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume theirHypervolume = 
				new org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume(
						normalizer.normalize(theirReferenceSet));

		double actual = myHypervolume.evaluate(approximationSet);
		double expected = theirHypervolume.evaluate(normalizer.normalize(theirApproximationSet));

		Assert.assertEquals(expected, actual, TestThresholds.INDICATOR_EPS);
	}

}
