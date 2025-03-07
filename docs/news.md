# Release Notes

This page documents notable changes introduced in each chronological release of the MOEA Framework.


## Version 3.2 (TBD)

  * Removes JMetal as a required dependency at runtime.  Not only does this reduce the jar file size by
    ~20 MBs, it removes overlapping class names shared by both libraries.
    
  * Set up the following repos and add documentation for these extensions:
  
    - http://github.com/MOEAFramework/JMetal-Plugin
    - http://github.com/MOEAFramework/GeneralizedDecomposition


## Version 3.1 (19 Nov 2022)

  * Removes all deprecated methods from `2.x` release.
  
  * Updates Javadoc formatting to fix linter warnings.
  
  * Removes all use of `java.util.Properties` and replaces with `TypedProperties`.  Also modifies
    `TypedProperties` be case-insensitive and warn if any properties are unused.
    
  * Adds two preview features:
  
    - The simulated annealing algorithm AMOSA in `org.moeaframework.algorithm.sa`.
    
    - Support for island model parallelization in `org.moeaframework.parallel`
    
  * Upgrades code to use try-with-resource for automatically closing resources.  This also includes
    making some classes `AutoCloseable`.  This may produce compiler warnings that can be fixed by
    wrapping the resource in a try-with-resource block.


## Version 3.0 (7 Nov 2022)

  * Upgrades minimum JDK version to 8.

  * Upgrades all dependencies to their latest version.
  
  * Upgrades to JMetal 5.9.
  
    - Includes new algorithms added to JMetal, including CDG, ESPEA,
      NSGA-III, and additional MOEA/D variants.
    
    - Removes DENSEA and FastPGA, which appear to have been removed from
      JMetal.  As a result, they will no longer be supported.

  * Rename the settings file `global.properties` to `moeaframework.properties`.
  
  * Converted NEWS, HELP, README, etc. to Markdown and moved into `docs/` folder.  Also replaces the
    quick start guide (PDF) with Markdown files in `docs/`.
    
  * Published new version of the Beginner's Guide to the MOEA Framework with updates for 
    newer Java versions.


## Version 2.13 (30 Dec 2019)

  * Adds debugging mode to ExternalProblem by setting
    ```
    org.moeaframework.problem.external_problem_debugging = true
    ```
    in `global.properties`.

  * Enable variable-length subsets in addition to the fixed-length subsets
    currently supported.
    
  * Distributed problem provides an evaluation id that can be used to
    replicate results when the problem is stochastic.
    
  * Guard against NaNs in `NondominatedSorting` that would occasionally produce
    `IllegalArgumentExceptions` when calling `Population#sort`.
    
  * Support all decision variable types in MOEA/D (previously only supported
    real variables).

  * Several improvements to the progress listener, including:
    
    - `ProgressListener` now provides access to the current algorithm being run by
      the `Executor`.  This can be used, for example, to get the contents of the
      population each iteration.

    - Fixes an "off by one" error in the `ProgressListener`, where it would report
      100% complete while the last seed was still running.

    - The `ProgressListener` is now called when running a single seed. Previously
      it was only called inside `runSeeds()`.



## Version 2.12 (04 Jan 2017)

  * Adds single-objective implementations of a genetic algorithm (GA),
    evolution strategies (ES), and differential evolution (DE).
    
  * Adds multi-objective implementations of the Multiple Single Objective Pareto
    Sampling (MSOPS) and Repeated Single Objective (RSO) algorithms.  RSO
    runs a single-objective optimizer multiple times with different weights.
    
  * Updated examples so problems with only one objective (TSP, Ant, etc.) use
    a single-objective optimizer.
    
  * The `Instrumenter` collection frequency can now be set to either number of
    evaluations or number of steps (issue #92).
    


## Version 2.11 (18 Aug 2016)

  * Removes the populationSize parameter from DBEA.  The population size is
    determined by the number of reference points.  Setting to a different value
    would cause an exception.  Thanks to @henryyuri for reporting (issue #69).
    
  * Fixes bug in RVEA.  If any objective has a range of 0, which could be
    caused by duplicates or infeasible solutions, the normalization breaks and
    all solutions are associated with one reference vector.  The fix ensures
    a non-zero range for normalization.
    
  * Prevent integer overflow exception if attempting to use an adaptive grid
    archive with too many divisions that exceeds the storage capacity of an
    array.  A custom error message is produced indicating the problem.  We may
    want to consider a sparse array or map for storage to remove this
    limitation.  Thanks to @zhoudiprivateChina for reporting (issue #67).
    
  * At the request of @abhinavgaur (issue #65), added an option to run NSGA-II
    using binary tournament selection *without* replacement.  This follows the
    original NSGA-II algorithm more closely.  Note that on many test problems,
    there is no statistically significant change in end-of-run indicators.
    To use, run:
    ```
        new Executor()
                .withAlgorithm("NSGAII")
                .withProperty("withReplacement", false)
                ...
    ```
    
  * Adds more control over handling duplicate solutions within non-dominated
    populations.  Previously, solutions with identical objectives were excluded.
    Now, this behavior is controlled with one of three options:
    
      - `NO_DUPLICATE_OBJECTIVES` (default) - Exclude solutions with identical
            objectives
      - `ALLOW_DUPLICATE_OBJECTIVES` - Allow solutions with identical objectives
            as long as their decision variables differ
      - `ALLOW_DUPLICATES` - Allow solutions with identical objectives and
            decision variables
      
    This can also be controlled by the new global property:
    ```
    `org.moeaframework.core.duplicate_mode = ALLOW_DUPLICATE_OBJECTIVES
    ```
    This also ensures duplicate solutions are assigned a crowding distance of 0
    so they are truncated/pruned from a population first.  Thanks to @Nozo
    (issue #15) for reporting this issue.
    
  * Adds global property to switch the non-dominated sorting algorithm between
    a brute-force implementation and Deb et al.'s fast non-dominated sorting.
    The brute-force implementation tends to have a faster average time while the
    fast non-dominated sorting implementation has better worst-case runtime.
    

## Version 2.10 (11 May 2016)

  * Adds the reference vector guided evolutionary algorithm (RVEA) by
    Cheng, Jin, Olhofer, and Sendhoff.

  * A new version of the SBX operator is included.  This version includes two
    new parameters:
    
      - `sbx.swap` - controls if the variables are randomly swapped between parents;
         set to false to replicate NSGA-III
      - `sbx.symmetric` - controls if the offspring distribution is symmetric or
         asymmetric

  * Prevent crashing MOEA/D if the user selects a neighborhood size larger than
    the population size.
    
  * Better documentation for the command-line methods.  The Javadocs now list
    all arguments with their descriptions.
    
  * Ability to define custom ideal and reference points when calculating
    hypervolume in `global.properties`.  For example:
    ```
    org.moeaframework.core.indicator.hypervolume_idealpt.DTLZ3 = 0
    org.moeaframework.core.indicator.hypervolume_refpt.DTLZ3 = 2
    ```
    indicates the hypervolume will be calculated with the volume between
    `(0,...,0)` and `(2,...,2)`.
    
  * `Analyzer` now includes `withIdealPoint(...)` and `withReferencePoint(...)` for
    use with the hypervolume calculations.



## Version 2.9 (9 Mar 2016)

  * Adds support for termination conditions.  The Executor can now support
    running for fixed wall-clock times:
    ```
    executor.withMaxTime(1000) // run for 1 second (1000 milliseconds)
    ```
    or with user-defined termination conditions:
    ```
    executor.withTerminationCondition(...)
    ```
       
  * Adds the 55 bi-objective test problems that will be appearing in the BBOB
    Workshop this year at GECCO 2016.
    
  * Adds subset decision variable for representing fixed-size subsets.
    


## Version 2.8 (13 Jan 2016)

  * Adds the Plot class to facilitate quick and simple plots of Pareto fronts,
    runtime dynamics, box-and-whisker comparisons, and more.  Able to save
    PNG, JPEG, and SVG figures (if JFreeSVG is added to the classpath).
    
  * Adds toCSV and saveCSV methods to the Accumulator class to allow exporting
    its contents to a file.
    
  * NSGA-III and DBEA now default to 99 divisions on bi-objective problems.
    This results in 100 reference points, matching other algorithms that use
    a population size of 100.
    
  * Released a new beginner's guide that will replace the outdated user manual.
    The new beginner's guide is available for purchase on the website and will
    help raise funds to continue development of this software.



## Version 2.7 (11 Dec 2015)

  * Adds ability to incorporate new problems or algorithms without having to
    use Java's service provider interface (SPI).  Now, providers can be
    programatically added via:
    
      - `AlgorithmFactory.getInstance().addProvider(...)` or
      - `ProblemFactory.getInstance().addProvider(...)`
      
  * Fixes bug in MOEA/D-DRA's update utility calculation (this was a bug in the
    official MOEA/D-DRA implementation that has since been corrected).
    
  * Like JMetal, the generational distance and inverted generational distance
    calculations use the mean square distance.  In the literature, however,
    inverted generational distance is typically computed using the average
    distance.  For consistency, inverted generational distance is now computed
    using the average.  This can also be controlled by two new global
    properties:
    ```
    org.moeaframework.core.indicator.gd_power = 2.0
    org.moeaframework.core.indicator.igd_power = 1.0
    ```
      
  * Adds more flexible SPI for introducing new variation operators.  This also
    improves support for defining new decision variable types.
    
  * Introduces the `BinaryIntegerVariable` for representing integer values
    encoded as binary strings.


## Version 2.6 (28 Sept 2015)

  * Fixes several bugs:
   
      - Fixes bug where the R indicators were not being included by the
        `Instrumenter`.
       
      - Fixes `NullPointerException` when combining parallelization with the
        program encoding.
   
  * Improves NSGA-III's ability to handle problems with 8+ objectives.  With
    8+ objectives, the calculation of intercepts can quickly degenerate and
    cause the algorithm to struggle.
    
  * Adds the constrained DTLZ problems introduced by K. Deb and H. Jain.
   
  * Adds the Improved Decomposition-Based Evolutionary Algorithm (DBEA).  This
    is another MOEA designed for many-objective problems, similar to NSGA-III.
    
  * Adds weights package for generalizing the generation of weight vectors for
    decomposition and reference point methods.  MOEA/D now accepts a weight
    generator as an argument, allowing the use of non-standard weights.
    
  * The `Analyzer` can now return an AnalyzerResults object storing all the
    statistical results.
   
  * Transitioned hosting of the MOEA Framework to Github.  We welcome all
    contributors!
    

## Version 2.5 (30 June 2015)

  * Adds support for single and multi-objective CMA-ES.
  
  * Adds native implementations of SPEA2, PAES, PESA2, OMPSO, SMPSO, IBEA,
    SMS-EMOA.  Having native implementations expands the capabilities of these
    algorithms, including collecting runtime dynamics, access to better
    operators and representations, automatic parallelization, and more.
    
  * Adds VEGA implementation.  VEGA is one of the first MOEAs, and while it
    does not perform well compared to modern MOEAs, we are maintaining it for
    its historical significance.
  
  * Adds R1, R2, and R3 binary indicators.  The R2 indicator is most often
    seen in the literature, but the others are included for completeness.
  
  * User manual now includes the algorithm parameters and their default values
    in chapter 9.

  * Updates chapter 2 in user manual to resolve bugs 32 and 33.
  
  * Upgraded to Commons-Math 3.4 (from version 3.1.1) to get access to newer
    methods (primarily KthSelector).
    
  * Fixes bug #34, where the scaling of plots in the Approximation Set Viewer
    were incorrect.
    
  * The Instrumenter can now visit classes not contained in the
    org.moeaframework package, meaning the Instrumenter can collect data from
    user-defined classes.  Specify these classes with
    Instrumenter#addAllowedPackage.


## Version 2.4 (2 Jan 2015)

  * Replace several hard-coded parameters for e-NSGA-II with customizable
    parameters.  The new parameters are windowSize, maxWindowSize,
    minimumPopulationSize, and maximumPopulationSize.
    
  * Provides a fix for reported issues with Java's Service Provider Interface
    not finding providers on some platforms (e.g., Android).  Default providers
    should now be discovered on all platforms.
    
  * Guard against out-of-bounds errors in the SBX operator.  If the random
    number generator (RNG) draws the value of 1.0, SBX will produce out-of-
    bounds values.  This has not been an issue since the default RNG never
    produces the value 1.0, but a custom RNG may do so.  The guards ensure this
    error will never occur.
    
  * Adds chapter in the user manual discussing master-slave, island-model, and
    hybrid parallelization techniques.
    
  * Adds support for Maven.  Version 2.4 and all subsequent releases will be
    available on the Maven Central Repository.
    
  * Addition of two new GA examples with binary encodings: NK-landscapes and
    additively decomposable problems.


## Version 2.3 (5 Oct 2014)
 
  * Fixes bug #25.  The original PISA implementation of IBEA had a typo in the
    binary hypervolume calculation that was recently discovered by researchers.
    The affected code in the MOEA Framework, which was derived from the original
    PISA implementation, has been corrected.
 
  * Adds references section to user manual to give appropriate recognition to
    the academic foundations for the MOEA Framework.


## Version 2.2 (28 Sept 2014)

  * Adds the recently published NSGA-III algorithm.
  
  * Fixes incorrect line numbers in section 7.3 in the user manual.
  
  * Updates user manual to better describe setting up the project in NetBeans
    in response to bug #23.


## Version 2.1 (19 Feb 2014)
 
  * Fixes several reported issues when running in Cygwin
  
    - The C wrapper file (`moeaframework.c`) now correctly handles Windows-style
      newlines.  Previously, the executable would crash when running on Cygwin.
      
    - Updates documentation to clarify how to specify the classpath on Cygwin.
      Since Cygwin runs the native Windows Java executable, the classpath
      separator is ';'.  However, the Cygwin command line interprets the ';'
      as a command separator unless the classpath is surrounded by quotes.
      
  * Moves the C example files from the `auxiliary/c/` folder to the `examples/`
    folder.  This ensures the necessary C code is distributed in the binary
    distribution for running Example5 and Example6.
    
  * Adds several new command line utilities:
  
    - Solve: Solves an optimization problem using the MOEA Framework.  The
      problem can be one recognized by the MOEA Framework by name, such as any
      built-in problem, or an external problem.  See the Defining New Problems
      chapter in the user manual for details on writing external problems.
      
    - ARFFConverter: Converts a result file into an ARFF file that can be
      analyzed using data mining tools.
      
    - AerovisConverter: Converts a result file into an input file for Aerovis,
      a high-dimensional data visualization and exploration tool.
      
  * Includes new demo application to demonstrate the capabilities of the MOEA
    Framework.


## Version 2.0 (11 Sep 2013)

  * The release of version 2.0 brings about significant changes to the MOEA
    Framework.  Old, deprecated APIs have been removed.  Third-party libraries
    have been upgraded to their latest versions.  As a result, upgrading from
    the 1.x to 2.x may break existing code.  Users may continue to use the 1.x
    series, but new features and improvements will only be added to the 2.x
    series.
    
  * Upgrades all libraries to their latest version.  Several libraries, like
    Commons Math and JMetal are not backwards compatible.
    
  * Adds reference sets for all of the test problems in the
    org.moeaframework.problems.misc package.  These problems are now accessible
    in the MOEA Diagnostic Tool GUI.
    
  * Executor can now report the progress, elapsed time, and estimated time
    remaining.  Additionally, the executor can be canceled at any time.
    
  * Fixes integer overflow bug in epsilon-dominance calculations.


## Version 1.17 (14 Nov 2012)

  * Adds constructors to EpsilonBoxDominanceArchive for passing arrays of
    epsilon values.

  * ResultFileReader and MetricFileReader would discard any remaining entries
    in an input file upon encountering any invalid data.  This is the intended
    behavior, which allows the software to automatically resume execution at
    the last valid entry.  However, since no notification was provided that the
    software was discarding data, this surprised some users.  The software now
    emits a warning message when discarding data in this manner.
    
  * Adds more predictable behavior when ResultFileWriter and MetricFileWriter
    attempt to recover data from an interrupted run.  The user can now choose
    to restore, overwrite, or exit with an error.  See the
      org.moeaframework.analysis.sensitivity.cleanup
    property in global.properties for more details.
    
  * Adds the --ignore and --maximum options to SimpleStatistics to give users
    better control over how infinity and NaN values are handled.

  * Adds error message if the resource files used by some examples are not
    found.  This can occur when the classpath is not configured correctly.
    
  * Analyzer would throw an exception when performing statistical significance
    tests on a dataset with only one sample.  The Analyzer no longer throws the
    exception, but instead outputs "Indifferent: []" to indicate no samples are
    statistically indifferent.
    
  * Fixes bounds of the Schaffer problem.  Changed from [0, 2] to [-10, 10].
  
  * Adds reference sets for several problems, including Binh, Binh4, Laumanns,
    Murata, Rendon2, Schaffer, Schaffer2, Fonseca, Fonseca2.
    
  * Built-in problem names are now case-insensitive.  For instance, you can
    instantiate the ZDT1 problem using the strings "ZDT1" or "zdt1".

  * Adds NetBeans installation instructions to the user manual.


## Version 1.16 (23 Sept 2012)

  * Adds several new examples, including:
      - Two new symbolic regression functions: QuarticExample and SexticExample
      - A new, harder ant trail map in LosAltosExample.
      - One-max binary optimization problem.
      
  * Includes examples in binary distribution.

  * Several bug fixes:
  
      - The Accumulator would throw a NullPointerException if an invalid key
        was provided.  Now this throws an IllegalArgumentException with an
        improved error message.
        
      - SimpleStatistics required the --output command-line option even though
        it was designed to be optional.  The --output option is now optional,
        redirecting to standard output if the option is not specified.
        
      - Some of the I/O methods were failing to correctly parse files if the
        line contained whitespace at the start of the line.  To improve the
        flexibility of these methods, such whitespace is now trimmed.


## Version 1.15 (31 July 2012)

  * Adds genetic programming (GP) support for evolving expression trees.  This
    includes over 45 program building blocks (logic functions, arithmetic
    functions, control structures, etc.) and the variation operators for
    evolving programs.
    
  * Adds a number of example problems in the examples/ folder to demonstrate
    various features.  The new example problems include:
      - 0/1 multiobjective knapsack problem (binary encoding)
      - Ant trail problem (genetic programming)
      - Symbolic regression (genetic programming)

  * Fixes algorithm "resumability".  Before, wrapping an algorithm inside
    another class would sometimes hide the Resumable interface, preventing the
    algorithm from saving/restoring its state.  Now, all algorithms (and their
    wrappers) by default provide the getState and setState methods.  As a
    result, MOEA/D and epsilon-NSGA-II can now be resumed.
    
  * The Resumable interface is deprecated since its methods are now
    redundant (see the item above for details).

  * Adds several helper methods to EncodingUtils to support creating, getting,
    and setting the values of common decision variable types.  These helper
    methods handle all necessary type checking and type conversions.  Included
    in this addition is support for integer decision variables.  See the
    class documentation for EncodingUtils for details.

  * Adds detailed list of errors and warning messages to the user manual.
  
  * Performed some code reorganization to eventually remove the CoreUtils class,
    since it is currently an amalgamation of functions serving different
    purposes.  These methods have been moved to more appropriate classes, and
    CoreUtils has been deprecated.

  * Several bug fixes:
  
      - QualityIndicator previously returned indicator values of 0 if the
        calculate method had not been invoked.  Now, attempting to retrieve any
        indicator value results in an IllegalStateException.
        
      - NSGA-II was not performing non-dominated sorting on the initial
        population, which resulted in incorrect ranks and crowding distances
        for the first generation.  This is now fixed.
        
      - The Kruskal-Wallis test would sometimes throw an exception when all
        observations were equal.  This causes the correction factor to be 0,
        which subsequently results in division-by-zero.  This case is now
        guarded against.


## Version 1.14 (26 June 2012)

  * The MOEAD algorithm now provides both the original MOEA/D implementation as
    well as the utility-based search extension introduced in 2009.  See the
    MOEAD class documentation for details.

  * Adds chapter on using the diagnostic tool to the user manual.

  * Several improvements to the diagnostic tool GUI, including
  
      - Hide the 'Show Approximation Set' right-click menu if the approximation
        set is not available
        
      - Adds the ability to plot individual trace lines rather than the
        condensed quantile view.  Adds the view menu to toggle between these
        two options.
        
      - The statistical results window now includes a Save option to save the
        statistical results to a text file.
        
      - Fixes bug where using the File > Exit menu option closed the window but
        the program was still running.
        
  * Adds internationalization and localization support to the diagnostic tool
    and command-line utilities, so end-user facing messages can be displayed in
    local languages, if translations are available.

  * Improved support for specifying executable commands in the configuration
    files.  Commands which contain whitespace within an argument are now
    supported by placing quotes around the argument ("...").
    
  * Adds support of instantiating overloaded JMetal algorithms by appending
    "-JMetal" to the algorithm name, such as "NSGAII-JMetal".
    
  * Improves support for configuring PISA selectors.  Previously, a static file
    was used to configure the selector.  Now, this configuration file can be
    generated dynamically using the parameters set inside the MOEA Framework.


## Version 1.13 (10 May 2012)

  * Significant improvements to the user manual, including
      - Restructuring into beginner, advanced and developer sections
      - Added introduction chapter
      - Added several sections to the Advanced Topics chapter

  * Fixes minor bug where AdaptiveMultimethodVariation was permitting one
    additional invocation prior to updating the operator selection
    probabilities.  E.g., if the UPDATE_WINDOW is 100, it was allowing 101
    invocations prior to updating the operator selection probabilities.

  * Deprecates the Portable Batch System (PBS) utilities in the
    org.moeaframework.util.pbs package.  These utilities are redundant and it
    is much simpler to use shell scripts when submitting PBS jobs.  See the
    user manual for example scripts.  The PBS package will be removed in
    version 2.0.
    
  * Adds Checkstyle 5.5 Ant script to automatically scan code for missing
    copyright and license headers and to ensure conformity to coding style
    guidelines.  This Ant script is located in the auxiliary/checkstyle folder
    in the source code distribution.

  * Adds copyright and license headers to a number of Java files that were
    missing the required header.
    
  * Adds several missing entries to the API documentation.
    
  * Adds the Negater command line utility for negating objectives in result
    files.  As the MOEA Framework only operates on minimization objectives,
    maximization objectives must be negated prior to their use.  The Negater is
    a temporary measure for working with maximization objectives until more
    integrated support is available.
    
  * Restructures the dominance comparators so that the default comparators
    include constraint violation checks by default.  Also deprecates the
    EpsilonBoxConstraintComparator as its functionality is now redundant with
    EpsilonBoxDominanceComparator.
    
  * Updates Instrumenter to only record epsilon-dominant solutions if epsilons
    are provided.
    
  * Removes over 300 lines of redundant code from UF11, UF12 and UF13 test
    problems from the CEC 2009 test problem suite.  The redundant code was
    replaced by calls to existing code in DTLZ2, DTLZ3 and WFG1.


## Version 1.12 (10 Apr 2012)

  * Improved support for custom hypervolume calculators.  It is known to work
    with the following implementations:
      - http://ls11-www.cs.tu-dortmund.de/people/beume/publications/hoy.cpp
      - http://iridia.ulb.ac.be/~manuel/hypervolume/
      - http://www.wfg.csse.uwa.edu.au/hypervolume/
    See the user manual for details on configuring a custom hypervolume
    calculator.

  * Modified PopulationIO to accept Iterable<Solution> instead of Population
    in the write and writeObjectives methods.  This allows saving solutions
    stored in lists and other collections.

  * Upgrades the C/C++ interface code to support sockets by adding the
    MOEA_Init_socket initialization method.  The ExternalProblem has also been
    extended with a host/port constructor to connect to such remote processes.
    Currently, sockets are only supported on POSIX systems.
    
  * Adds extern "C" to the C/C++ interface code to allow easier code mixing.
  
  * Makes C/C++ interface ANSI C and ISO/IEC C++ 1998 compliant, allowing its
    use in nearly any C/C++ compiler.  More advanced features (e.g., sockets)
    are conditionally compiled by the MOEA_SOCKETS flag, which is automatically
    enabled on POSIX systems (_POSIX_SOURCE flag).
    
  * Cleans up several build scripts and unit tests.  Uses JUnit's Assume
    class to skip tests rather than using an ad hoc method.
    
  * Reworked some of the examples in the examples/ folder.  The Rosenbrock
    problem (1 objective) has been replaced by the two-objective DTLZ2
    problem.


## Version 1.11 (29 Feb 2012)

  * Adds the MOEA_Finalize() method to the C/C++ interface code.  This method
    will be used to release any resources (memory, sockets, etc.) used by the
    interface.

  * Adds ClassLoaderProblems, allowing problems to be instantiated using their
    fully-qualified class name.  For example:
      new Executor().withProblem("org.moeaframework.problem.misc.Kita")

  * Adds the current directory to the classpath used by the executable JAR 
    distribution, allowing the configuration file, data files and classes to be
    referenced outside the JAR.

  * Miscellaneous bug fixes:
  
      - ExternalProblem#close would fail to close the reader if the writer 
        threw an exception.
        
      - The MOEA Diagnostic Tool encountered an IndexOutOfBoundsException when
        the problem only contained one objective.
        
      - The MOEA Diagnostic Tool now correctly detects and handles cases where
        the decision variables are not specified in the reference set.
        
      - Fixes concurrency bug in the MOEA Diagnostic Tool, whereby a list was
        being concurrently modified while used in an iterator.  The code now
        uses a CopyOnWriteArrayList.


## Version 1.10 (22 Jan 2012)

  * ExternalProblem, the class allowing the MOEA Framework to use problems
    implemented in C/C++ and other compiled languages, now supports real-valued,
    binary and permutation decision-variables.  The moeaframework.h library for
    C/C++ has been provided to facilitate the communication process.  The
    source code distribution contains these files and examples in the
    auxiliary/c/ folder.

  * Adds support in Executor, Analyzer and Instrumenter for instantiating
    problem instances using constructors with arguments.   These classes also
    provide the withSameProblemAs(...) method, allowing instances to easily
    copy settings from one another.
    
  * With the inclusion of Apache Commons Lang, several methods and classes are
    being deprecated as their functionality is redundant.  This includes the
    ArrayMath class, the Listeners class and several methods in CoreUtils.
    
  * Adds Apache Commons Lang 3.1 dependency, which is required for changes in
    this release and planned changes in future releases.

  * Improved support for accessing reference sets and other resources that can
    either be located on the local file system or bundled in a JAR file.

  * Adds the --epsilon command line argument to SetHypervolume.
  
  * Miscellaneous bug fixes:
  
      - CoreUtils#fillVariablesFromDoubleArray now throws an
        IllegalArgumentException if the size of the array does not match the
        required number of variables
        
      - Removes non-UTF8 characters from several source files that were
        preventing compilation in OpenJDK 7.
    

## Version 1.9 (03 Jan 2012)

  * Adds 31 test problems to the misc package.  This includes Belegundu, Binh,
    Binh2, Binh3, Binh4, Fonseca, Fonseca2, Jimenez, Kita, Kursawe, Laumanns,
    Lis, Murata, Obayashi, OKA1, OKA2, Osyczka, Osyczka2, Poloni, Quagliarella,
    Rendon, Rendon2, Schaffer, Schaffer2, Srinivas, Tamaki, Tanaka, Viennet,
    Viennet2, Viennet3, Viennet4.
    
  * Adds three new menu items to the diagnostic tool, mostly for convenience:
      - Enable All Performance Indicators
      - Disable All Performance Indicators
      - Display Last Trace
      
  * Adds the --force command line argument to Evaluator and ResultFileEvaluator,
    which ignores the file timestamp check.  The timestamp check helps prevent
    working with modified or outdated files.  A failed check would normally
    stop execution and report an error, but can now be overridden with --force.

  * Several changes to the SobolAnalysis command line utility:
      - No longer restricted to only loading metric files, can support any
        number columns in the model output file
      - The number of resamples for bootstrap confidence intervals can be set
        using a command line argument

  * Several changes to the Analysis command line utility:
      - Efficiency and controllability calculations are now optional, and are
        enabled by a command line flag
      - The band width and threshold value can be set using command line
        arguments
      - Fixes a scaling issue, where efficiency requires the unscaled parameter
        values but controllability requires scaled parameter values

  * PropertiesProblems is now case-insensitive, which is now consistent with
    other built-in problem providers.
    
  * Updated copyright notices for 2012.
  

## Version 1.8 (21 Nov 2011)

  * Adds a new diagnostics GUI for analyzing the runtime dynamics of algorithms.

  * Removed PolynomialStepMutation operator as it was indifferent from PM.
    All uses of PolynomialStepMutation should be switched to PM.

  * Updates to the webpage, getting it ready for public release.  This includes
    the addition of an examples page with several simple examples.

  * ProblemFactory and AlgorithmFactory now handle service provider
    configuration and instantiation errors, preventing an errant problem
    or algorithm provider from crashing the application.  The code now
    determines if any other problem or algorithm providers are available prior 
    to throwing ProviderNotFoundException.

  * Includes a manifest file which records the specification and implementation
    version, which will help improve bug tracking.

  * Adds the new Instrumenter and collector classes, allowing the
    instrumentation of algorithms to collect information concerning their
    runtime dynamics.

  * Updated the global.properties configuration file to refer to the new PISA
    package.  All applications must change org.moeaframework.util.pisa to 
    org.moeaframework.algorithm.pisa to function properly.

  * Added support for PISA algorithms with any alpha (number of parents).
    Previous versions only supported two parents.

  * Adds RandomSearch for comparing the performance of algorithms against
    random sampling.

  * Adds ScriptedProblem, allowing the implementation of problems in one of the
    many available JSR 223 Scripting API languages.  This supports the use of
    problems written in Python, Ruby, JavaScript, Scheme, Groovy, Scala, 
    Smalltalk, PHP, OCaml, etc.

  * Miscellaneous bug fixes and enhancements.
   

## Version 1.7 (29 Sept 2011)

  * Adds Analyzer class, similar in nature to Executor, to reduce the amount of
    boilerplate code necessary to analyze end-of-run results.
   
  * Moves the Executor class to reside in the top-level org.moeaframework
    package alongside the Analyzer.
   
  * Adds the Contribution indicator for measuring the percentage of the
    reference set which is produced by a given algorithm.
   
  * Removes the deprecated BooleanVariable class, which has been replaced by
    BinaryVariable.
   
  * Adds the SetGenerator command line utility for generating reference sets
    for problems whose solutions are known analytically (i.e., implement 
    AnalyticalProblem).
   
  * AdditiveEpsilonIndicator is normalized by default.  Subsequently, reversing
    the change in v1.4, the metric file no longer contains both the unnormalized
    and normalized additive epsilon indicator values.


## Version 1.6 (10 September 2011)

  * New result file format enables storing additional data alongside end-of-run
    approximation sets.  The new format is backwards compatible.  

  * ExtractData command line utility for extracting the additional data stored
    in the new result files.  Also supports "+ commands", such as +hypervolume,
    to evaluate performance measures.  (Note: the old ResultFileEvaluator is 
    still preferred if all performance measures are to be evaluated.)

  * Enhanced Executor features, including:
      - specifying the problem by its class name (allowing the introduction of 
        new problems without using the ProblemFactory)
      - accumulating the result from multiple seeds
   
  * Adds OperatorFactory, allowing the construction of operators using encoded
    names.  For example, "sbx+pm" constructs an operator consisting of
    simulated binary crossover (SBX) and polynomial mutation (PM).
   
  * Providers and factories moved to org.moeaframework.core.spi.  This places
    the service provider codes in a centralized, standardized location.  The
    factories have also been converted from static factories to object 
    factories, allowing the default factories to be replaced at runtime.
   

## Version 1.5 (28 August 2011)

  * Fixes bug in SinglePointCrossover and TwoPointCrossover causing an exception
    when operating on decision variables of length <= 1.

  * Adds Executor class for convenient, programmatic execution of algorithms.

  * Adds SetHypervolume command line utility for calculating the hypervolume of
    reference or approximation sets.
   
  * Improved support for PISA selectors, including the download-pisa-windows
    Ant script to automatically download and install all Windows PISA selectors.


## Version 1.4 (10 August 2011)

  * Adds RotatedProblems problem provider to allow instantiating rotated
    instances of any available problems by prefixing their names with UNROT_ and
    ROT_.
   
  * Visibility of static evaluate(...) methods in several indicators have been
    changed to package-private to prevent unintentionally passing unnormalized
    sets.  The constructor instead enforces any necessary normalization.
   
  * MetricFileWriter and impacted codes updated to include a header line
    identifying the metric contained in each column.  Metric files now also
    include the normalized epsilon-indicator metric.
   
  * Adds ResultFileSeedMerger to combine reference sets across multiple seeds.
    Unlike the current codes which evaluate each seed independently,
    preprocessing with ResultFileSeedMerger allows evaluating the result of
    combining multiple seeds.


## Version 1.3 (26 July 2011)

  * All problems now implement the close() method to free any underlying
    resources.  This is in contrast to previous versions where only
    ExternalProblem supported the close() method.  This change provides a more 
    consistent mechanism for releasing resources.
   
  * Fixed resource loading in several command line utilities to ensure safe
    and complete shutdown (including ensuring Problem#close() is invoked).
   
  * The MetricFileStatistics utility has been renamed to SimpleStatistics and 
    now works on any set of files containing a matrix (equal rows and columns) 
    of numeric values.

  * Problems can now be defined in the global.properties file with
    ```
    org.moeaframework.problem.NAME.class = ...
    ```
    and optionally
    ```
    org.moeaframework.problem.NAME.referenceSet = ...
    ```

  * ReferenceSetMerger now emits a warning if duplicate solutions exist.
 
  * Adds RotationMatrixBuilder and RotatedProblem to support the new rotated
    test problem suite.  These tools allow constructing arbitrary or random
    rotation matrices and applying such rotations to arbitrary real-valued test
    problems.


## Version 1.2 (5 July 2011)

  * Adds SetContribution class to determine the percentage of a reference set 
    that individual algorithms contributed.  Unlike ReferenceSetMerger, this 
    percentage accounts for duplicate solutions.

  * Fixes an issue when Evaluator, ResultFileInfo and ResultFileEvaluator fail 
    to close an ExternalProblem, thus keeping the external process alive and the
    JVM active.
   
  * Miscellaneous bug fixes.


## Version 1.1 (24 June 2011)

  * Adds global.properties file to store configuration data.  The Settings
    class facilitates access to the properties.

  * Adds support for PISA algorithms.  This uses the new global.properties file
    to enumerate and configure the available PISA selectors.
   
  * Includes the ResultFileInfo command line utility to count the number of 
    valid entries in a result file.


## Version 1.0 (14 June 2011)

  * First major release.
  
