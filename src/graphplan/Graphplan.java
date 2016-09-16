/*
 * ---------------------------------------------------------------------------
 * Copyright (C) 2010  Felipe Meneguzzi
 * JavaGP is distributed under LGPL. See file LGPL.txt in this directory.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * To contact the author:
 * http://www.meneguzzi.eu/felipe/contact.html
 * ---------------------------------------------------------------------------
 */
package graphplan;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.SolutionExtractionVisitor;
import graphplan.graph.algorithm.TimeoutSolutionExtractionVisitor;
import graphplan.graph.draw.DotGraphDrawVisitor;
import graphplan.graph.memo.mutexes.StaticMutexesTable;
import graphplan.graph.planning.PlanningGraph;
import graphplan.graph.planning.PlanningGraphException;
import graphplan.graph.planning.cwa.PlanningGraphClosedWorldAssumption;
import graphplan.parser.PDDLPlannerAdapter;
import graphplan.parser.ParseException;
import graphplan.parser.PlannerParser;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main class and accessor for the Graphplan algorithm
 *
 * @author Felipe Meneguzzi
 */
public class Graphplan {

	// Logging
	private static final Logger logger = Logger.getLogger(Graphplan.class.getName());
	private static final String LOGGER_FILE = "src/logging.properties";

	// Global parameters
	public static boolean noopsFirst = false;
	public static boolean operatorsLatest = true;
	public static boolean propositionsSmallest = true;
	public static boolean sortGoals = false;

	// Parameters
	private boolean extractAllPossibleSolutions; // Extract all solutions with minimum length
	private int extractAllPossibleSolutionsWithMaxLength; // Extract all solutions with (minimum length + extractAllPossibleWithMaxLength, respective to graph level)
	private boolean pddl;
	private boolean setupLogger;
	private long timeout;
	private int maxLevels;
	private String graphDrawFile;

	// Fields
	private DomainDescription domain;
	private PlanningGraph planningGraph;
	private SolutionExtractionVisitor solutionExtraction;

	public static void main(String[] args) {
		Graphplan graphplan = parseArgs(args);
		if(graphplan != null) {
			graphplan.printDebugInfo();
			graphplan.getPlanSolution();
			graphplan.drawPlanningGraph();
		}
	}

	private Graphplan(Builder builder) {
		if(builder.maxLevels <= 0) {
			throw new IllegalStateException("maxLevels must be greater than 0");
		}
		if(builder.extractAllPossibleSolutionsWithMaxLength < 0) {
			throw new IllegalStateException("extractAllPossibleSolutionsWithMaxLength must be greater than or equal to 0");
		}
		if(builder.domainFilename == null || builder.problemFilename == null) {
			throw new IllegalStateException("domainFilename or problemFilename not provided");
		}
		this.pddl = builder.pddl;
		this.setupLogger = builder.setupLogger;
		this.timeout = builder.timeout;
		this.maxLevels = builder.maxLevels;
		this.extractAllPossibleSolutions = builder.extractAllPossibleSolutions;
		this.extractAllPossibleSolutionsWithMaxLength = builder.extractAllPossibleSolutionsWithMaxLength;
		this.graphDrawFile = builder.graphDrawFile;
		setupLogger();
		parseDomain(builder.domainFilename, builder.problemFilename);
	}

	public boolean isExtractAllPossibleSolutions() {
		return extractAllPossibleSolutions;
	}

	public void setExtractAllPossibleSolutions(boolean extractAllPossibleSolutions) {
		this.extractAllPossibleSolutions = extractAllPossibleSolutions;
	}

	public void setExtractAllPossibleSolutionsWithMaxLength(int extractAllPossibleSolutionsWithMaxLength) {
		this.extractAllPossibleSolutionsWithMaxLength = extractAllPossibleSolutionsWithMaxLength;
	}

	private void parseDomain(String domainFilename, String problemFilename) {
		try {
			if (pddl) {
				logger.finest("JavaGP - PDDL\n");
				logger.finest("+ DOMAIN: " + domainFilename);
				logger.finest("+ PROBLEM: " + problemFilename);
				PDDLPlannerAdapter parserPDDL = PDDLPlannerAdapter.getInstance(domainFilename, problemFilename);
				domain = parserPDDL.getDomainDescriptionFromPddlObject();
			} else {
				File opFile = new File(domainFilename);
				File probFile = new File(problemFilename);
				if (!opFile.exists()) {
					logger.warning("Domain file \'" + domainFilename + "\' does not exist");
					System.exit(1);
				}
				if (!probFile.exists()) {
					logger.warning("Problem file \'" + problemFilename + "\' does not exist");
					System.exit(1);
				}
				InputStream operators = new FileInputStream(opFile);
				InputStream problem = new FileInputStream(probFile);
				logger.finest("JavaGP - STRIPS\n");
				logger.finest("+ DOMAIN: " + domainFilename);
				logger.finest("+ PROBLEM: " + problemFilename);
				PlannerParser parser = new PlannerParser();
				domain = parser.parseProblem(operators, problem);
			}
		} catch (ParseException | FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void printDebugInfo() {
		logger.fine("Selected Heuristics: ");
		if (Graphplan.noopsFirst) {
			logger.fine("\t+ Heuristic for actions: Select Noops first");
		}

		if (Graphplan.operatorsLatest) {
			logger.fine("\t+ Heuristic for actions: Select actions that appears latest in the Planning Graph.");
		}

		if (Graphplan.propositionsSmallest) {
			logger.fine("\t+ Heuristic for subgoals: Select firstly propositions that leads to the smallest set of resolvers.");
		}

		if (Graphplan.sortGoals) {
			logger.fine("\t+ Heuristic for subgoals: Sort goals by proposition that appears earliest in the Planning Graph.");
		}
	}

	/**
	 * Return plan solution containing all found solutions given specified parameters
	 */
	@Nullable
	public PlanSolution getPlanSolution() {
		PlanSolution planSolution = null;
		long t1 = System.currentTimeMillis();

		try {
			Runtime runtime = Runtime.getRuntime();
			NumberFormat fm = DecimalFormat.getInstance();
			fm.setMaximumFractionDigits(2);

			logger.info("Running planner, maximum memory: " + fm.format(runtime.maxMemory() / Math.pow(1024, 2)) + "MB");
			planSolution = plan();
			long t2 = System.currentTimeMillis();
			long totalTime = (t2 - t1);
			logger.info("Planning took " + (totalTime) + "ms ( " + (totalTime / 1000) + "s )");
			logger.info("Total memory used: " + fm.format(runtime.totalMemory() / Math.pow(1024, 2)) + "MB");
			if (!planSolution.getAllHighlevelPlans().isEmpty()) {
				if (extractAllPossibleSolutions) {
					logger.info("Number of plan founds: " + planSolution.getAllPlans().size());
					logger.info("---------------------");

					for (List<Operator> listResult : planSolution.getAllPlans()) {
						logger.info("Plan found:\n" + listResult.toString());
						logger.info("Plan length: " + listResult.size());
						logger.info("---------------------");
					}
				} else {
					PlanResult result = planSolution.getAllHighlevelPlans().iterator().next();
					logger.info("Plan found:\n" + result.toString());
					logger.info("Plan length: " + result.getPlanLength());
				}
			} else {
				logger.warning("No plan found");
			}
		} catch (PlanningGraphException | OperatorFactoryException | OutOfMemoryError | TimeoutException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return planSolution;
	}

	/**
	 * Get levelled planning graph, i.e. a complete planning graph with all mutex relations
	 */
	@Nullable
	public PlanningGraph getLevelledPlanningGraph() {
		PlanningGraph levelledPlanningGraph = null;
		try {
			PropositionLevel initialLevel = new PropositionLevel();
			initialLevel.addPropositions(domain.getInitialState());
			this.solutionExtraction = new SolutionExtractionVisitor(domain.getGoalState(), this);

			if (pddl) {
				//If domain has negative preconditions, the planner will use the closed world assumption
				if (domain.isNegativePreconditions()) {
					levelledPlanningGraph = new PlanningGraphClosedWorldAssumption(initialLevel, domain.getTypes(), domain.getParameterTypes(), new StaticMutexesTable(new ArrayList<>(domain.getOperators())));
				} else
					levelledPlanningGraph = new PlanningGraph(initialLevel, domain.getTypes(), domain.getParameterTypes(), new StaticMutexesTable(new ArrayList<>(domain.getOperators())));
			} else {
				levelledPlanningGraph = new PlanningGraph(initialLevel, new StaticMutexesTable(new ArrayList<>(domain.getOperators())));
			}

			OperatorFactory.getInstance().resetOperatorTemplates();
			for (Operator operator : domain.getOperators()) {
				OperatorFactory.getInstance().addOperatorTemplate(operator);
			}

			while (!levelledPlanningGraph.levelledOff()) {
				levelledPlanningGraph.expandGraph();
			}
		} catch (OperatorFactoryException | PlanningGraphException e) {
			logger.warning(e.getLocalizedMessage());
		}
		return levelledPlanningGraph;
	}

	public void drawPlanningGraph() {
		if (graphDrawFile != null) {
			logger.info("Drawing planning graph to " + graphDrawFile);
			DotGraphDrawVisitor drawVisitor = new DotGraphDrawVisitor();
			if (planningGraph.accept(drawVisitor)) {
				try {
					PrintWriter writer = new PrintWriter(new File(graphDrawFile));
					writer.println(drawVisitor.toString());
					writer.flush();
					writer.close();
				} catch (FileNotFoundException e) {
					logger.warning(e.getLocalizedMessage());
				}
			}
		}
	}

	private static void wrongParametersMessage() {
		logger.warning("Wrong parameters");
		logger.info("Usage: \'java -jar JavaGP \'" +
				"\n\t>>> STRIPS Language: " +
				"\n\t\t" + "java -jar javagp.jar -nopddl -d examples/strips/ma-prodcell/domain.txt -p examples/strips/ma-prodcell/problem.txt" +
				"\n\n\t>>> PDDL Language: " +
				"\n\t\t" + "java -jar javagp.jar -d examples/pddl/blocksworld/blocksworld.pddl -p examples/pddl/blocksworld/pb1.pddl" +
				"\n\n\t>>> Planner arguments: " +
				"\n\t-maxlevels <NUMBER>, " + "\t\t\t\t\tMax Graph levels." +
				"\n\t-timeout <NUMBER>, " + "\t\t\t\t\t\tPlanning timeout." +
				"\n\t-extractAllPossibleSolutions <NUMBER>, " + "\tExtract all solutions with (minimum length + NUMBER) (TODO: need more tests)." +
				"\n\n\t-noHeuristics, " + "\t\tNo Heuristics." +
				"\n\n\t[Heuristics for actions]" +
				"\n\t-operatorsLatest, " + "\tSelect actions that appears latest in the Planning Graph." +
				"\n\tor" +
				"\n\t-noopsFirst, " + "\t\tSelect Noops first." +
				"\n\n\t[Heuristic for propositions]" +
				"\n\t-propositionsSmallest,	Select firstly propositions that leads to the smallest set of resolvers." +
				"\n\tor" +
				"\n\t-sortGoals,		Sort goals by proposition that appears earliest in the Planning Graph." +
				"\n\n\t[JavaGP Default Heuristics]" +
				"\n\t-operatorsLatest" +
				"\n\t-propositionsSmallest"
				+ "\n\n -v write verbose output"
				+ "\n\n -draw <filename> draws the planning graph (for debugging)"
		);
		System.exit(1);
	}

	private void setupLogger() {
		try {
			if (new File(LOGGER_FILE).exists()) {
				LogManager.getLogManager().readConfiguration(new FileInputStream(new File(LOGGER_FILE)));
			} else {
				LogManager.getLogManager().readConfiguration(Graphplan.class.getResourceAsStream("/" + LOGGER_FILE));
			}

			if(!setupLogger) {
				Handler[] handlers = Logger.getLogger("").getHandlers();
				for (Handler handler : handlers) {
					handler.setLevel(Level.SEVERE);
				}
			}
		} catch (Exception e) {
			if(setupLogger) {
				System.err.println("Error setting up logger:" + e);
			}
		}
	}

	private PlanSolution plan() throws PlanningGraphException, OperatorFactoryException, TimeoutException {
		PropositionLevel initialLevel = new PropositionLevel();
		initialLevel.addPropositions(domain.getInitialState());
		if(timeout > 0) {
			this.solutionExtraction = new TimeoutSolutionExtractionVisitor(domain.getGoalState(), this);
			((TimeoutSolutionExtractionVisitor) solutionExtraction).setTimeout(timeout);
		} else {
			this.solutionExtraction = new SolutionExtractionVisitor(domain.getGoalState(), this);
		}

		logger.fine("OPTIMIZATION: JavaGP using Static Mutexes Table");
		logger.fine("OPTIMIZATION: JavaGP using Memoization");

		if (pddl) {
			logger.fine("OPTIMIZATION: JavaGP using Types");
			//If domain has negative preconditions, the planner will use the closed world assumption
			if (domain.isNegativePreconditions()) {
				logger.fine("OPTIMIZATION: JavaGP using Closed World Assumption (Lazily)");
				this.planningGraph = new PlanningGraphClosedWorldAssumption(initialLevel, domain.getTypes(), domain.getParameterTypes(), new StaticMutexesTable(new ArrayList<>(domain.getOperators())));
			} else
				this.planningGraph = new PlanningGraph(initialLevel, domain.getTypes(), domain.getParameterTypes(), new StaticMutexesTable(new ArrayList<>(domain.getOperators())));
		} else {
			this.planningGraph = new PlanningGraph(initialLevel, new StaticMutexesTable(new ArrayList<>(domain.getOperators())));
		}

		OperatorFactory.getInstance().resetOperatorTemplates();

		for (Operator operator : domain.getOperators()) {
			OperatorFactory.getInstance().addOperatorTemplate(operator);
		}

		boolean planFound = false;
		int maxLength = extractAllPossibleSolutionsWithMaxLength;

		while ((!planFound && (this.planningGraph.size() <= maxLevels)) || maxLength >= 0) {
			try {
				logger.info("Expanding graph");
				this.planningGraph.expandGraph();
			} catch (PlanningGraphException e) {
				System.err.println(e.getMessage());
				return new PlanSolution();
			}
			if (this.planningGraph.goalsPossible(domain.getGoalState(), this.planningGraph.size() - 1)) {
				//Extract solution
				logger.info("Extracting solution");
				planFound = this.planningGraph.accept(this.solutionExtraction);
				if (planFound) {
					logger.info("Plan found with " + (this.planningGraph.size() / 2) + " steps");
					maxLength--;
				} else {
					if(timeout > 0 && ((TimeoutSolutionExtractionVisitor) solutionExtraction).timedOut()) {
						logger.info("Planner timed out after " + timeout + " milliseconds");
						throw new TimeoutException("No plan possible in " + timeout + " milliseconds");
					}
					logger.info("Plan not found with " + (this.planningGraph.size() / 2) + " steps");
					if (!planPossible()) {
						throw new PlanningGraphException("Graph has levelled off, plan is not possible.", this.planningGraph.levelOffIndex());
					}
				}
			} else {
				logger.info("Goals not possible with " + (this.planningGraph.size() / 2) + " steps");
				//If the goals are not possible, and the graph has levelled off, then this problem has no possible plan
				if (this.planningGraph.levelledOff()) {
					throw new PlanningGraphException("Goals are not possible and graph has levelled off, plan is not possible.", this.planningGraph.levelOffIndex());
				}
			}
		}

		return this.solutionExtraction.getPlanSolution();
	}

	/**
	 * Returns whether or not a plan is possible according to both the
	 * memoization table criterion and the graph level size criterion.
	 *
	 * @return if plan is possible
	 */
	private boolean planPossible() {
		if (!this.planningGraph.levelledOff()) {
			return true;
		} else {
			return this.solutionExtraction.levelledOff(this.planningGraph.levelOffIndex());
		}
	}

	/**
	 * Builder pattern to create an instance of Graphplan
	 */

	public static class Builder {
		private String domainFilename;
		private String problemFilename;
		private String graphDrawFile;
		private boolean pddl = true;
		private boolean setupLogger = false;
		private long timeout = 0;
		private int maxLevels = Integer.MAX_VALUE;
		private boolean extractAllPossibleSolutions = false;
		private int extractAllPossibleSolutionsWithMaxLength = 0;

		public Builder setDomainFilename(String domainFilename) {
			this.domainFilename = domainFilename;
			return this;
		}

		public Builder setProblemFilename(String problemFilename) {
			this.problemFilename = problemFilename;
			return this;
		}

		public Builder setPddl(boolean pddl) {
			this.pddl = pddl;
			return this;
		}

		public Builder setSetupLogger(boolean setupLogger) {
			this.setupLogger = setupLogger;
			return this;
		}

		public Builder setTimeout(long timeout) {
			this.timeout = timeout;
			return this;
		}

		public Builder setMaxLevels(int maxLevels) {
			this.maxLevels = maxLevels;
			return this;
		}

		public Builder setExtractAllPossibleSolutions(boolean extractAllPossibleSolutions) {
			this.extractAllPossibleSolutions = extractAllPossibleSolutions;
			return this;
		}

		public Builder setExtractAllPossibleSolutionsWithMaxLength(int extractAllPossibleSolutionsWithMaxLength) {
			this.extractAllPossibleSolutionsWithMaxLength = extractAllPossibleSolutionsWithMaxLength;
			return this;
		}

		public Builder setGraphDrawFile(String graphDrawFile) {
			this.graphDrawFile = graphDrawFile;
			return this;
		}

		public Graphplan build() {
			return new Graphplan(this);
		}
	}

	@Nullable
	private static Graphplan parseArgs(String[] args) {
		Builder builder = new Builder();
		builder.setSetupLogger(true);
		boolean argsOk = true;

		for (int i = 0; i < args.length && argsOk; i++) {
			switch (args[i]) {
				case "-nopddl":
					builder.setPddl(false);
					break;
				case "-d":  /* The domain argument */
					if (++i < args.length && !args[i].startsWith("-")) {
						builder.setDomainFilename(args[i]);
					} else {
						logger.warning("-d argument requires a filename with the domain");
						argsOk = false;
					}
					break;
				case "-p":  /* The problem argument */
					if (++i < args.length && !args[i].startsWith("-")) {
						builder.setProblemFilename(args[i]);
					} else {
						logger.warning("-p argument requires a filename with the problem");
						argsOk = false;
					}
					break;
				case "-maxlevels":
					if (++i < args.length && !args[i].startsWith("-")) {
						try {
							builder.setMaxLevels(Integer.parseInt(args[i]));
						} catch (NumberFormatException e) {
							logger.warning("-maxlevels argument requires a positive integer number of levels");
						}
					} else {
						logger.warning("-maxlevels argument requires a positive integer number of levels");
						argsOk = false;
					}
					break;
				case "-timeout":
					if (++i < args.length && !args[i].startsWith("-")) {
						try {
							builder.setTimeout(Long.parseLong(args[i]));
						} catch (NumberFormatException e) {
							logger.warning("-timeout argument requires a positive integer amount of time");
						}
					} else {
						logger.warning("-timeout argument requires a positive integer amount of time");
						argsOk = false;
					}
					break;
				case "-noopsFirst":
					noopsFirst = true;
					operatorsLatest = false;
					break;
				case "-operatorsLatest":
					operatorsLatest = true;
					break;
				case "-propositionsSmallest":
					propositionsSmallest = true;
					break;
				case "-sortGoals":
					sortGoals = true;
					propositionsSmallest = false;
					break;
				case "-noHeuristics":
					sortGoals = false;
					propositionsSmallest = false;
					noopsFirst = false;
					operatorsLatest = false;
					break;
				case "-draw":
					if (++i < args.length && !args[i].startsWith("-")) {
						builder.setGraphDrawFile(args[i]);
					} else {
						logger.warning("-draw argument requires a valid filename");
						argsOk = false;
					}
					break;
				case "-extractAllPossibleSolutions":
					if (++i < args.length && !args[i].startsWith("-")) {
						try {
							builder.setExtractAllPossibleSolutions(true);
							builder.setExtractAllPossibleSolutionsWithMaxLength(Integer.parseInt(args[i]));
							logger.info("Extracting all possible solutions with length up to "+Integer.parseInt(args[i]));
						} catch (NumberFormatException e) {
							logger.warning("-extractAllPossibleSolutions argument requires an integer max length");
						}
					} else {
						logger.warning("-extractAllPossibleSolutions argument requires a valid non-negative integer max length");
						argsOk = false;
					}
					break;
			}
		}

		if (argsOk) {
			return builder.build();
		} else {
			Graphplan.wrongParametersMessage();
			return null;
		}
	}
}
