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
package org.moeaframework.examples.ga.tsplib;

import java.io.IOException;
import java.io.InputStream;

/**
 * Example of optimization using a permutation encoding to solve the traveling
 * salesman problem (TSP) on the {@code a280.tsp} instance.
 */
public class A280Example {

	/**
	 * Starts the example running the TSP problem.
	 * 
	 * @param args the command line arguments
	 * @throws IOException if an I/O error occurred
	 */
	public static void main(String[] args) throws IOException {
		try (InputStream is = PR76Example.class.getResourceAsStream("a280.tsp")) {
			if (is == null) {
				System.err.println("Unable to find the file a280.tsp");
				System.exit(-1);
			}
			
			TSPExample.solve(is);
		}
	}
	
}
