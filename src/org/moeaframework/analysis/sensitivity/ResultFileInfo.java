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
package org.moeaframework.analysis.sensitivity;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.moeaframework.core.Problem;
import org.moeaframework.util.CommandLineUtility;

/**
 * Command line utility for counting the number of entries in a result file.
 * This is primarily used to ensure a call to {@link Evaluator} completed
 * successfully and generated the correct number of entries in the result file.
 * <p>
 * Usage: {@code java -cp "..." org.moeaframework.analysis.sensitivity.ResultFileInfo <options> <files>}
 * 
 * <table>
 *   <caption style="text-align: left">Arguments:</caption>
 *   <tr>
 *     <td>{@code -b, --problem}</td>
 *     <td>The name of the problem.  This name should reference one of the
 *         problems recognized by the MOEA Framework.</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -d, --dimension}</td>
 *     <td>The number of objectives (use instead of -b).</td>
 *   </tr>
 *   <tr>
 *     <td>{@code -o, --output}</td>
 *     <td>The output file where the extract data will be saved.</td>
 *   </tr>
 * </table>
 */
public class ResultFileInfo extends CommandLineUtility {
	
	/**
	 * Constructs the command line utility for counting the number of entries
	 * in a result file.
	 */
	public ResultFileInfo() {
		super();
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		
		OptionUtils.addProblemOption(options, true);
		
		options.addOption(Option.builder("o")
				.longOpt("output")
				.hasArg()
				.argName("file")
				.build());
		
		return options;
	}

	@Override
	public void run(CommandLine commandLine) throws Exception {
		try (Problem problem = OptionUtils.getProblemInstance(commandLine, true);
				OutputLogger output = new OutputLogger(commandLine.hasOption("output") ?
					new File(commandLine.getOptionValue("output")) : null)) {
			// display info for all result files
			for (String filename : commandLine.getArgs()) {
				try (ResultFileReader reader = new ResultFileReader(problem, new File(filename))) {
					int count = 0;
						
					while (reader.hasNext()) {
						reader.next();
						count++;
					}

					output.println(filename + " " + count);
				}
			}
		}
	}
	
	/**
	 * Starts the command line utility for counting the number of entries in a 
	 * result file.
	 * 
	 * @param args the command line arguments
	 * @throws Exception if an error occurred
	 */
	public static void main(String[] args) throws Exception {
		new ResultFileInfo().start(args);
	}

}
