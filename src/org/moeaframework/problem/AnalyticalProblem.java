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
package org.moeaframework.problem;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Interface for problems whose Pareto optimal set is known analytically,
 * providing the {@link #generate} method for producing randomly-generated
 * reference sets.
 */
public interface AnalyticalProblem extends Problem {

	/**
	 * Returns a randomly-generated solution using the analytical solution to
	 * this problem. Note however that discontinuous Pareto surfaces may result
	 * in some solutions generated by this method being dominated by other
	 * generated solutions. It is therefore recommended using a
	 * {@link NondominatedPopulation} to removed dominated solutions prior to
	 * using the generated reference set.
	 * <p>
	 * The generated solutions should be spread uniformly across the entire
	 * Pareto frontier; however, this is a suggestion and is not a requirement
	 * of this interface.
	 * 
	 * @return a randomly-generated Pareto optimal solution to this problem
	 */
	public Solution generate();

}
