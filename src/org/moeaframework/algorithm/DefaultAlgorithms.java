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
package org.moeaframework.algorithm;

import java.util.List;

import org.moeaframework.algorithm.pso.OMOPSO;
import org.moeaframework.algorithm.pso.SMPSO;
import org.moeaframework.algorithm.sa.AMOSA;
import org.moeaframework.algorithm.single.DifferentialEvolution;
import org.moeaframework.algorithm.single.EvolutionStrategy;
import org.moeaframework.algorithm.single.GeneticAlgorithm;
import org.moeaframework.algorithm.single.RepeatedSingleObjective;
import org.moeaframework.algorithm.single.AggregateObjectiveComparator;
import org.moeaframework.algorithm.single.MinMaxDominanceComparator;
import org.moeaframework.algorithm.single.LinearDominanceComparator;
import org.moeaframework.algorithm.single.SelfAdaptiveNormalVariation;
import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.FitnessEvaluator;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.NondominatedSortingPopulation;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.CrowdingComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.fitness.AdditiveEpsilonIndicatorFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeContributionFitnessEvaluator;
import org.moeaframework.core.fitness.HypervolumeFitnessEvaluator;
import org.moeaframework.core.fitness.IndicatorFitnessEvaluator;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.UniformSelection;
import org.moeaframework.core.operator.real.DifferentialEvolutionSelection;
import org.moeaframework.core.operator.real.DifferentialEvolutionVariation;
import org.moeaframework.core.operator.real.UM;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.spi.ProviderLookupException;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.core.spi.RegisteredAlgorithmProvider;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.Vector;
import org.moeaframework.util.weights.RandomGenerator;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

/**
 * A provider of default algorithms.  Refer to {@code docs/algorithms.md}, Appendix A in the
 * Beginner's Guide to the MOEA Framework, or the Javadoc for the specific algorithm
 * for the specifics of parameterizing the algorithms.
 */
public class DefaultAlgorithms extends RegisteredAlgorithmProvider {

	/**
	 * Constructs the default algorithm provider.
	 */
	public DefaultAlgorithms() {
		super();
		
		register(this::newMOEAD, "MOEAD", "MOEA/D");
		register(this::newGDE3, "GDE3");
		register(this::newNSGAII, "NSGAII", "NSGA-II", "NSGA2");
		register(this::newNSGAIII, "NSGAIII", "NSGA-III", "NSGA3");
		register(this::neweNSGAII, "eNSGAII", "e-NSGA-II", "eNSGA2");
		register(this::neweMOEA, "eMOEA", "e-MOEA");
		register(this::newCMAES, "CMA-ES", "CMAES", "MO-CMA-ES");
		register(this::newSPEA2, "SPEA2");
		register(this::newPAES, "PAES");
		register(this::newPESA2, "PESA2");
		register(this::newOMOPSO, "OMOPSO");
		register(this::newSMPSO, "SMPSO");
		register(this::newIBEA, "IBEA");
		register(this::newSMSEMOA, "SMSEMOA", "SMS-EMOA");
		register(this::newVEGA, "VEGA");
		register(this::newDBEA, "DBEA", "I-DBEA");
		register(this::newRVEA, "RVEA");
		register(this::newMSOPS, "MSOPS");
		register(this::newAMOSA, "AMOSA");
		register(this::newRandomSearch, "Random");
		register(this::newDifferentialEvolution, "DifferentialEvolution", "DE", "DE/rand/1/bin");
		register(this::newGeneticAlgorithm, "GeneticAlgorithm", "GA");
		register(this::newEvolutionaryStrategy, "EvolutionStrategy", "EvolutionaryStrategy", "ES");
		register(this::newRSO, "RSO");
	}

	/**
	 * Returns a new {@link eMOEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code eMOEA} instance
	 * @param problem the problem
	 * @return a new {@code eMOEA} instance
	 */
	private Algorithm neweMOEA(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);

		Initialization initialization = new RandomInitialization(problem, populationSize);
		Population population = new Population();

		DominanceComparator comparator = new ParetoDominanceComparator();

		EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
				properties.getDoubleArray("epsilon", 
						new double[] { EpsilonHelper.getEpsilon(problem) }));

		TournamentSelection selection = new TournamentSelection(2, comparator);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		return new EpsilonMOEA(problem, population, archive,
				selection, variation, initialization, comparator);
	}

	/**
	 * Returns a new {@link NSGAII} instance.
	 * 
	 * @param properties the properties for customizing the new {@code NSGAII} instance
	 * @param problem the problem
	 * @return a new {@code NSGAII} instance
	 */
	private Algorithm newNSGAII(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);

		Initialization initialization = new RandomInitialization(problem, populationSize);
		NondominatedSortingPopulation population = new NondominatedSortingPopulation();
		TournamentSelection selection = null;
		
		if (properties.getBoolean("withReplacement", true)) {
			selection = new TournamentSelection(2, new ChainedComparator(
					new ParetoDominanceComparator(),
					new CrowdingComparator()));
		}

		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		return new NSGAII(problem, population, null, selection, variation, initialization);
	}
	
	/**
	 * Returns a new {@link NSGAIII} instance.
	 * 
	 * @param properties the properties for customizing the new {@code NSGAIII} instance
	 * @param problem the problem
	 * @return a new {@code NSGAIII} instance
	 */
	private Algorithm newNSGAIII(TypedProperties properties, Problem problem) {
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.fromProperties(properties, problem);
		int populationSize;
		
		if (properties.contains("populationSize")) {
			populationSize = (int)properties.getDouble("populationSize", 100);
		} else {
			// get the number of reference points
			int referencePoints = divisions.getNumberOfReferencePoints(problem);

			// round up to a multiple of 4
			populationSize = (int)Math.ceil(referencePoints / 4.0) * 4;
		}
		
		Initialization initialization = new RandomInitialization(problem, populationSize);
		
		ReferencePointNondominatedSortingPopulation population = new ReferencePointNondominatedSortingPopulation(
				problem.getNumberOfObjectives(), divisions);

		Selection selection = null;
		
		if (problem.getNumberOfConstraints() == 0) {
			selection = new Selection() {
	
				@Override
				public Solution[] select(int arity, Population population) {
					Solution[] result = new Solution[arity];
					
					for (int i = 0; i < arity; i++) {
						result[i] = population.get(PRNG.nextInt(population.size()));
					}
					
					return result;
				}
				
			};
		} else {
			selection = new TournamentSelection(2, new ChainedComparator(
					new AggregateConstraintComparator(),
					new DominanceComparator() {

						@Override
						public int compare(Solution solution1, Solution solution2) {
							return PRNG.nextBoolean() ? -1 : 1;
						}
						
					}));
		}
		
		// disable swapping variables in SBX operator to remain consistent with
		// Deb's implementation (thanks to Haitham Seada for identifying this
		// discrepancy)
		if (!properties.contains("operator") && problem.isType(RealVariable.class)) {
			if (!properties.contains("sbx.swap")) {
				properties.setBoolean("sbx.swap", false);
			}
			
			if (!properties.contains("sbx.distributionIndex")) {
				properties.setDouble("sbx.distributionIndex", 30.0);
			}
			
			if (!properties.contains("pm.distributionIndex")) {
				properties.setDouble("pm.distributionIndex", 20.0);
			}
		}

		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		return new NSGAII(problem, population, null, selection, variation, initialization);
	}

	/**
	 * Returns a new {@link MOEAD} instance.  Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code MOEAD} instance
	 * @param problem the problem
	 * @return a new {@code MOEAD} instance
	 * @throws FrameworkException if the decision variables are not real valued
	 */
	private Algorithm newMOEAD(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);
		
		//enforce population size lower bound
		if (populationSize < problem.getNumberOfObjectives()) {
			System.err.println("increasing MOEA/D population size");
			populationSize = problem.getNumberOfObjectives();
		}

		Initialization initialization = new RandomInitialization(problem, populationSize);
		
		//default to de+pm for real-encodings
		String operator = properties.getString("operator", null);
		
		if ((operator == null) && problem.isType(RealVariable.class)) {
			operator = "de+pm";
		}

		Variation variation = OperatorFactory.getInstance().getVariation(operator, properties, problem);
		
		int neighborhoodSize = 20;
		int eta = 2;
		
		if (properties.contains("neighborhoodSize")) {
			neighborhoodSize = Math.max(2, 
					(int)(properties.getDouble("neighborhoodSize", 0.1) * populationSize));
		}
		
		if (neighborhoodSize > populationSize) {
			neighborhoodSize = populationSize;
		}
		
		if (properties.contains("eta")) {
			eta = Math.max(2, (int)(properties.getDouble("eta", 0.01) * populationSize));
		}

		return new MOEAD(
				problem,
				neighborhoodSize,
				initialization,
				variation,
				properties.getDouble("delta", 0.9),
				eta,
				(int)properties.getDouble("updateUtility", -1));
	}

	/**
	 * Returns a new {@link GDE3} instance.  Only real encodings are supported.
	 * 
	 * @param properties the properties for customizing the new {@code GDE3} instance
	 * @param problem the problem
	 * @return a new {@code GDE3} instance
	 * @throws FrameworkException if the decision variables are not real valued
	 */
	private Algorithm newGDE3(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		
		DominanceComparator comparator = new ParetoDominanceComparator();
		NondominatedSortingPopulation population = new NondominatedSortingPopulation(comparator);
		Initialization initialization = new RandomInitialization(problem, populationSize);
		DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();
		DifferentialEvolutionVariation variation = (DifferentialEvolutionVariation)OperatorFactory
				.getInstance().getVariation("de", properties, problem);

		return new GDE3(problem, population, comparator, selection, variation, initialization);
	}

	/**
	 * Returns a new {@link eNSGAII} instance.
	 * 
	 * @param properties the properties for customizing the new {@code eNSGAII} instance
	 * @param problem the problem
	 * @return a new {@code eNSGAII} instance
	 */
	private Algorithm neweNSGAII(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);

		Initialization initialization = new RandomInitialization(problem, populationSize);

		NondominatedSortingPopulation population = new NondominatedSortingPopulation(
				new ParetoDominanceComparator());

		EpsilonBoxDominanceArchive archive = new EpsilonBoxDominanceArchive(
				properties.getDoubleArray("epsilon", 
						new double[] { EpsilonHelper.getEpsilon(problem) }));

		TournamentSelection selection = new TournamentSelection(2, new ChainedComparator(
						new ParetoDominanceComparator(), new CrowdingComparator()));

		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		NSGAII nsgaii = new NSGAII(problem, population, archive, selection, variation, initialization);

		return new AdaptiveTimeContinuation(
				nsgaii,
				properties.getInt("windowSize", 100),
				Math.max(properties.getInt("windowSize", 100),
						 properties.getInt("maxWindowSize", 100)),
				1.0 / properties.getDouble("injectionRate", 0.25),
				properties.getInt("minimumPopulationSize", 100),
				properties.getInt("maximumPopulationSize", 10000),
				new UniformSelection(),
				new UM(1.0));
	}
	
	/**
	 * Returns a new {@link CMAES} instance.
	 * 
	 * @param properties the properties for customizing the new {@code CMAES} instance
	 * @param problem the problem
	 * @return a new {@code CMAES} instance
	 */
	private Algorithm newCMAES(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int lambda = (int)properties.getDouble("lambda", 100);
		double cc = properties.getDouble("cc", -1.0);
		double cs = properties.getDouble("cs", -1.0);
		double damps = properties.getDouble("damps", -1.0);
		double ccov = properties.getDouble("ccov", -1.0);
		double ccovsep = properties.getDouble("ccovsep", -1.0);
		double sigma = properties.getDouble("sigma", -1.0);
		int diagonalIterations = (int)properties.getDouble("diagonalIterations", 0);
		String indicator = properties.getString("indicator", "crowding");
		double[] initialSearchPoint = properties.getDoubleArray("initialSearchPoint", null);
		NondominatedPopulation archive = null;
		FitnessEvaluator fitnessEvaluator = null;
		
		if (problem.getNumberOfObjectives() == 1) {
			archive = new NondominatedPopulation();
		} else {
			archive = new EpsilonBoxDominanceArchive(
					properties.getDoubleArray("epsilon", 
							new double[] { EpsilonHelper.getEpsilon(problem) }));
		}
		
		if ("hypervolume".equalsIgnoreCase(indicator)) {
			fitnessEvaluator = new HypervolumeFitnessEvaluator(problem);
		} else if ("epsilon".equalsIgnoreCase(indicator)) {
			fitnessEvaluator = new AdditiveEpsilonIndicatorFitnessEvaluator(problem);
		} else if ("crowding".equalsIgnoreCase(indicator)) {
			fitnessEvaluator = null;
		} else {
			throw new IllegalArgumentException("invalid indicator: " + indicator);
		}
		
		return new CMAES(problem, lambda, fitnessEvaluator, archive, initialSearchPoint, false,
				cc, cs, damps, ccov, ccovsep, sigma, diagonalIterations);
	}
	
	/**
	 * Returns a new {@link SPEA2} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SPEA2} instance
	 * @param problem the problem
	 * @return a new {@code SPEA2} instance
	 */
	private Algorithm newSPEA2(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int offspringSize = (int)properties.getDouble("offspringSize", 100);
		int k = (int)properties.getDouble("k", 1);
		
		Initialization initialization = new RandomInitialization(problem, populationSize);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		return new SPEA2(problem, initialization, variation, offspringSize, k);
	}
	
	/**
	 * Returns a new {@link PAES} instance.
	 * 
	 * @param properties the properties for customizing the new {@code PAES} instance
	 * @param problem the problem
	 * @return a new {@code PAES} instance
	 */
	private Algorithm newPAES(TypedProperties properties, Problem problem) {
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		int bisections = (int)properties.getDouble("bisections", 8);

		Variation variation = OperatorFactory.getInstance().getVariation(
				OperatorFactory.getInstance().getDefaultMutation(problem), 
				properties,
				problem);

		return new PAES(problem, variation, bisections, archiveSize);
	}
	
	/**
	 * Returns a new {@link PESA2} instance.
	 * 
	 * @param properties the properties for customizing the new {@code PESA2} instance
	 * @param problem the problem
	 * @return a new {@code PESA2} instance
	 */
	private Algorithm newPESA2(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		int bisections = (int)properties.getDouble("bisections", 8);
		
		Initialization initialization = new RandomInitialization(problem, populationSize);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		return new PESA2(problem, variation, initialization, bisections, archiveSize);
	}
	
	/**
	 * Returns a new {@link OMOPSO} instance.
	 * 
	 * @param properties the properties for customizing the new {@code OMOPSO} instance
	 * @param problem the problem
	 * @return a new {@code OMOPSO} instance
	 */
	private Algorithm newOMOPSO(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		int maxIterations = (int)properties.getDouble("maxEvaluations", 25000) / populationSize;
		double mutationProbability = properties.getDouble("mutationProbability", 1.0 / problem.getNumberOfVariables());
		double perturbationIndex = properties.getDouble("perturbationIndex", 0.5);
		double[] epsilon = properties.getDoubleArray("epsilon", new double[] { EpsilonHelper.getEpsilon(problem) });
		
		return new OMOPSO(problem, populationSize, archiveSize, epsilon, mutationProbability,
				perturbationIndex, maxIterations);
	}
	
	/**
	 * Returns a new {@link SMPSO} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SMPSO} instance
	 * @param problem the problem
	 * @return a new {@code SMPSO} instance
	 */
	private Algorithm newSMPSO(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int archiveSize = (int)properties.getDouble("archiveSize", 100);
		double mutationProbability = properties.getDouble("pm.rate", 1.0 / problem.getNumberOfVariables());
		double distributionIndex = properties.getDouble("pm.distributionIndex", 20.0);
		
		return new SMPSO(problem, populationSize, archiveSize, mutationProbability, distributionIndex);
	}
	
	/**
	 * Returns a new {@link IBEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code IBEA} instance
	 * @param problem the problem
	 * @return a new {@code IBEA} instance
	 */
	private Algorithm newIBEA(TypedProperties properties, Problem problem) {
		if (problem.getNumberOfConstraints() > 0) {
			throw new ProviderNotFoundException("IBEA", 
					new ProviderLookupException("constraints not supported"));
		}
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		String indicator = properties.getString("indicator", "hypervolume");
		IndicatorFitnessEvaluator fitnessEvaluator = null;

		Initialization initialization = new RandomInitialization(problem, populationSize);

		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);
		
		if ("hypervolume".equalsIgnoreCase(indicator)) {
			fitnessEvaluator = new HypervolumeFitnessEvaluator(problem);
		} else if ("epsilon".equalsIgnoreCase(indicator)) {
			fitnessEvaluator = new AdditiveEpsilonIndicatorFitnessEvaluator(problem);
		} else {
			throw new IllegalArgumentException("invalid indicator: " + indicator);
		}

		return new IBEA(problem, null, initialization, variation, fitnessEvaluator);
	}
	
	/**
	 * Returns a new {@link SMSEMOA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code SMSEMOA} instance
	 * @param problem the problem
	 * @return a new {@code SMSEMOA} instance
	 */
	private Algorithm newSMSEMOA(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);
		double offset = properties.getDouble("offset", 100.0);
		String indicator = properties.getString("indicator", "hypervolume");
		FitnessEvaluator fitnessEvaluator = null;
		
		Initialization initialization = new RandomInitialization(problem, populationSize);

		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);
		
		if ("hypervolume".equalsIgnoreCase(indicator)) {
			fitnessEvaluator = new HypervolumeContributionFitnessEvaluator(problem, offset);
		}

		return new SMSEMOA(problem, initialization, variation, fitnessEvaluator);
	}
	
	/**
	 * Returns a new {@link VEGA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code VEGA} instance
	 * @param problem the problem
	 * @return a new {@code VEGA} instance
	 */
	private Algorithm newVEGA(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);

		Initialization initialization = new RandomInitialization(problem, populationSize);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);
		
		return new VEGA(problem, new Population(), null, initialization, variation);
	}
	
	/**
	 * Returns a new {@link DBEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code DBEA} instance
	 * @param problem the problem
	 * @return a new {@code DBEA} instance
	 */
	private Algorithm newDBEA(TypedProperties properties, Problem problem) {
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.fromProperties(properties, problem);
		int populationSize = divisions.getNumberOfReferencePoints(problem);
		
		Initialization initialization = new RandomInitialization(problem, populationSize);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);
		
		return new DBEA(problem, initialization, variation, divisions);
	}
	
	/**
	 * Returns a new {@link RVEA} instance.
	 * 
	 * @param properties the properties for customizing the new {@code RVEA}
	 *        instance
	 * @param problem the problem
	 * @return a new {@code RVEA} instance
	 */
	private Algorithm newRVEA(TypedProperties properties, Problem problem) {
		if (problem.getNumberOfObjectives() < 2) {
			throw new FrameworkException("RVEA requires at least two objectives");
		}
		
		NormalBoundaryDivisions divisions = NormalBoundaryDivisions.fromProperties(properties, problem);
		int populationSize = divisions.getNumberOfReferencePoints(problem);
		
		Initialization initialization = new RandomInitialization(problem, populationSize);
		
		ReferenceVectorGuidedPopulation population = new ReferenceVectorGuidedPopulation(
				problem.getNumberOfObjectives(), divisions,
				properties.getDouble("alpha", 2.0));

		if (!properties.contains("operator") && problem.isType(RealVariable.class)) {
			if (!properties.contains("sbx.swap")) {
				properties.setBoolean("sbx.swap", false);
			}
			
			if (!properties.contains("sbx.distributionIndex")) {
				properties.setDouble("sbx.distributionIndex", 30.0);
			}
			
			if (!properties.contains("pm.distributionIndex")) {
				properties.setDouble("pm.distributionIndex", 20.0);
			}
		}

		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);
		
		int maxGenerations = (int)(properties.getDouble("maxEvaluations", 10000) / populationSize);
		int adaptFrequency = (int)properties.getDouble("adaptFrequency", maxGenerations / 10);

		return new RVEA(problem, population, variation, initialization, maxGenerations, adaptFrequency);
	}
	
	/**
	 * Returns a new {@link RandomSearch} instance.
	 * 
	 * @param properties the properties for customizing the new {@code RandomSearch} instance
	 * @param problem the problem
	 * @return a new {@code RandomSearch} instance
	 */
	private Algorithm newRandomSearch(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);
		
		Initialization generator = new RandomInitialization(problem, populationSize);
		
		NondominatedPopulation archive = null;
		
		if (properties.contains("epsilon")) {
			archive = new EpsilonBoxDominanceArchive(properties.getDoubleArray("epsilon",
					new double[] { EpsilonHelper.getEpsilon(problem) }));
		} else {
			archive = new NondominatedPopulation();
		}
		
		return new RandomSearch(problem, generator, archive);
	}
	
	/**
	 * Returns a new {@link MSOPS} instance.
	 * 
	 * @param properties the properties for customizing the new {@code MSOPS} instance
	 * @param problem the problem
	 * @return a new {@code MSOPS} instance
	 */
	private Algorithm newMSOPS(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		int numberOfWeights = (int)properties.getDouble("numberOfWeights", 50);

		Initialization initialization = new RandomInitialization(problem, populationSize);
		
		List<double[]> weights = new RandomGenerator(problem.getNumberOfObjectives(), numberOfWeights).generate();
		
		// normalize weights so their magnitude is 1
		for (int i = 0; i < weights.size(); i++) {
			weights.set(i, Vector.normalize(weights.get(i)));
		}

		MSOPSRankedPopulation population = new MSOPSRankedPopulation(weights);
		DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();
		DifferentialEvolutionVariation variation = (DifferentialEvolutionVariation)OperatorFactory.getInstance().getVariation(
				"de", properties, problem);

		return new MSOPS(problem, population, selection, variation, initialization);
	}
	
	/**
	 * Returns a new single-objective {@link RepeatedSingleObjective} instance.
	 * 
	 * @param properties the properties for customizing the new {@code RepeatedSingleObjective} instance
	 * @param problem the problem
	 * @return a new {@code RepeatedSingleObjective} instance
	 */
	private Algorithm newRSO(TypedProperties properties, Problem problem) {
		String algorithmName = properties.getString("algorithm", "GA");
		int instances = (int)properties.getDouble("instances", 100);
		
		if (!properties.contains("method")) {
			properties.setString("method", "min-max");
		}

		return new RepeatedSingleObjective(problem, algorithmName, properties, instances);
	}
	
	/**
	 * Returns a new single-objective {@link GeneticAlgorithm} instance.
	 * 
	 * @param properties the properties for customizing the new {@code GeneticAlgorithm} instance
	 * @param problem the problem
	 * @return a new {@code GeneticAlgorithm} instance
	 */
	private Algorithm newGeneticAlgorithm(TypedProperties properties, Problem problem) {
		int populationSize = (int)properties.getDouble("populationSize", 100);
		double[] weights = properties.getDoubleArray("weights", new double[] { 1.0 });
		String method = properties.getString("method", "linear");
		
		AggregateObjectiveComparator comparator = null;
		
		if (method.equalsIgnoreCase("linear")) {
			comparator = new LinearDominanceComparator(weights);
		} else if (method.equalsIgnoreCase("min-max")) {
			comparator = new MinMaxDominanceComparator(weights);
		} else {
			throw new FrameworkException("unrecognized weighting method: " + method);
		}

		Initialization initialization = new RandomInitialization(problem, populationSize);
		Selection selection = new TournamentSelection(2, comparator);
		Variation variation = OperatorFactory.getInstance().getVariation(null, properties, problem);

		return new GeneticAlgorithm(problem, comparator, initialization, selection, variation);
	}
	
	/**
	 * Returns a new single-objective {@link EvolutionStrategy} instance.
	 * 
	 * @param properties the properties for customizing the new {@code EvolutionaryStrategy} instance
	 * @param problem the problem
	 * @return a new {@code EvolutionaryStrategy} instance
	 */
	private Algorithm newEvolutionaryStrategy(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		double[] weights = properties.getDoubleArray("weights", new double[] { 1.0 });
		String method = properties.getString("method", "linear");
		
		AggregateObjectiveComparator comparator = null;
		
		if (method.equalsIgnoreCase("linear")) {
			comparator = new LinearDominanceComparator(weights);
		} else if (method.equalsIgnoreCase("min-max")) {
			comparator = new MinMaxDominanceComparator(weights);
		} else {
			throw new FrameworkException("unrecognized weighting method: " + method);
		}

		Initialization initialization = new RandomInitialization(problem, populationSize);
		Variation variation = new SelfAdaptiveNormalVariation();

		return new EvolutionStrategy(problem, comparator, initialization, variation);
	}

	/**
	 * Returns a new single-objective {@link DE/rand/1/bin} instance.
	 * 
	 * @param properties the properties for customizing the new {@code DE/rand/1/bin} instance
	 * @param problem the problem
	 * @return a new {@code DE/rand/1/bin} instance
	 */
	private Algorithm newDifferentialEvolution(TypedProperties properties, Problem problem) {
		problem.assertType(RealVariable.class);
		
		int populationSize = (int)properties.getDouble("populationSize", 100);
		double[] weights = properties.getDoubleArray("weights", new double[] { 1.0 });
		String method = properties.getString("method", "linear");
		
		AggregateObjectiveComparator comparator = null;
		
		if (method.equalsIgnoreCase("linear")) {
			comparator = new LinearDominanceComparator(weights);
		} else if (method.equalsIgnoreCase("min-max")) {
			comparator = new MinMaxDominanceComparator(weights);
		} else {
			throw new FrameworkException("unrecognized weighting method: " + method);
		}

		Initialization initialization = new RandomInitialization(problem, populationSize);
		DifferentialEvolutionSelection selection = new DifferentialEvolutionSelection();
		DifferentialEvolutionVariation variation = (DifferentialEvolutionVariation)OperatorFactory.getInstance()
				.getVariation("de", properties, problem);

		return new DifferentialEvolution(problem, comparator, initialization, selection, variation);
	}
	
	private Algorithm newAMOSA(TypedProperties properties, Problem problem) {
		// to be used at initialization the archive by the size of gamma*SL (default to 100)(gamma > 1)
		double gamma = properties.getDouble("gamma", 2.0d);
		gamma = gamma < 1.0d ? 2.0d : gamma;
			
		// Soft Limit SL (default to 100)
		int softLimit = properties.getInt("SL", 100);
			
		// Hard Limit HL (default to 10)
		int hardLimit = properties.getInt("HL", 10);

		double tMin = properties.getDouble("tMin", 0.0000001d);
		double tMax = properties.getDouble("tMax", 200d);
		double alpha = properties.getDouble("alpha", 0.8d);
		int numberOfIterationPerTemperature = properties.getInt("iter", 500);
		int numberOfHillClimbingIterationsForRefinement = properties.getInt("hillClimbIter", 20);
			
		// Initialize the algorithm with randomly-generated solutions
		Initialization initialization = new RandomInitialization(problem, (int)gamma*softLimit);
			
		// Use the operator factory that problem provides
		Variation variation = OperatorFactory.getInstance().getVariation(
				OperatorFactory.getInstance().getDefaultMutation(problem), 
				properties,
				problem);
			
		return new AMOSA(problem, initialization, variation, softLimit, hardLimit, tMin, tMax,
				alpha, numberOfIterationPerTemperature, numberOfHillClimbingIterationsForRefinement);
	}
	
}
