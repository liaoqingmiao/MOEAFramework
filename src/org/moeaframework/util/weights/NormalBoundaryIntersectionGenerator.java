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
package org.moeaframework.util.weights;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates weights using the Normal Boundary Intersection (NBI) method.
 * For {@code d} divisions and {@code M} objectives, this class will generate
 * {@code M+d-1 choose d} weights.
 * <p>
 * References:
 * <ol>
 *   <li>Das, I. and J. Dennis (1998).  "Normal-boundary intersection: A new
 *       method for generating the Pareto surface in nonlinear multicriteria
 *       optimization problems."  SIAM J. Optimization, 8(3):631-657.
 *   <li>Deb, K. and H. Jain (2014).  "An Evolutionary Many-Objective
 *       Optimization Algorithm Using Reference-Point-Based Nondominated
 *       Sorting Approach, Part I: Solving Problems With Box Constraints."
 *       IEEE Transactions on Evolutionary Computation, 18(4):577-601.
 * </ol>
 */
public class NormalBoundaryIntersectionGenerator implements WeightGenerator {
	
	/**
	 * The number of objectives.
	 */
	private final int numberOfObjectives;
	
	/**
	 * The number of divisions.
	 */
	private final NormalBoundaryDivisions divisions;
	
	/**
	 * Constructs a new normal-boundary intersection weight generator.  If divisions specifies both an
	 * inner and outer division, the two-layer approach of Deb and Jain (2014) is used.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @param divisions the number of divisions
	 */
	public NormalBoundaryIntersectionGenerator(int numberOfObjectives, NormalBoundaryDivisions divisions) {
		super();
		this.numberOfObjectives = numberOfObjectives;
		this.divisions = divisions;
	}

	@Override
	public int size() {
		return divisions.getNumberOfReferencePoints(numberOfObjectives);
	}

	@Override
	public List<double[]> generate() {
		List<double[]> weights = null;
		
		if (divisions.getInnerDivisions() > 0) {
			if (divisions.getOuterDivisions() >= numberOfObjectives) {
				System.err.println("The specified number of outer divisions produces intermediate reference points, recommend setting divisionsOuter < numberOfObjectives.");
			}

			weights = generateWeights(divisions.getOuterDivisions());

			// offset the inner weights
			List<double[]> inner = generateWeights(divisions.getInnerDivisions());

			for (int i = 0; i < inner.size(); i++) {
				double[] weight = inner.get(i);

				for (int j = 0; j < weight.length; j++) {
					weight[j] = (1.0/numberOfObjectives + weight[j])/2;
				}
			}

			weights.addAll(inner);
		} else {
			if (divisions.getOuterDivisions() < numberOfObjectives) {
				System.err.println("No intermediate reference points will be generated for the specified number of divisions, recommend increasing divisions");
			}

			weights = generateWeights(divisions.getOuterDivisions());
		}
		
		return weights;
	}
	
	/**
	 * Generates the reference points (weights) for the given number of
	 * divisions.
	 * 
	 * @param divisions the number of divisions
	 * @return the list of reference points
	 */
	private List<double[]> generateWeights(int divisions) {
		List<double[]> result = new ArrayList<double[]>();
		double[] weight = new double[numberOfObjectives];
		
		generateRecursive(result, weight, numberOfObjectives, divisions, divisions, 0);

		return result;
	}
	
	/**
	 * Generate reference points (weights) recursively.
	 * 
	 * @param weights list storing the generated reference points
	 * @param weight the partial reference point being recursively generated
	 * @param numberOfObjectives the number of objectives
	 * @param left the number of remaining divisions
	 * @param total the total number of divisions
	 * @param index the current index being generated
	 */
	private void generateRecursive(List<double[]> weights,
			double[] weight, int numberOfObjectives, int left, int total, int index) {
		if (index == (numberOfObjectives - 1)) {
			weight[index] = (double)left/total;
			weights.add(weight.clone());
		} else {
			for (int i = 0; i <= left; i += 1) {
				weight[index] = (double) i / total;
				generateRecursive(weights, weight, numberOfObjectives, left - i, total, index + 1);
			}
		}
	}

}
