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
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.ActionLevel;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import pddl4j.ParserException;

/**
 * Main class and accessor for the Graphplan algorithm
 * @author Felipe Meneguzzi
 *
 */
public class Graphplan {
	private static final Logger logger = Logger.getLogger(Graphplan.class.getName());
	public static final String LOGGER_FILE = "logging.properties";
	
	protected int maxLevels = Integer.MAX_VALUE;
	private PlanningGraph planningGraph;
	private SolutionExtractionVisitor solutionExtraction;
	private boolean pddl = true;

	public static boolean noopsFirst = false;
	public static boolean operatorsLatest = true;
	public static boolean propositionsSmallest = true;
	public static boolean sortGoals = false;
	
	public static void main(String[] args) {
		setupLogger();
		Graphplan graphplan = new Graphplan();
		InputStream operators = null;
		InputStream problem = null;
		
		String problemFilename = null;
		String domainFilename = null;
		
		String graphDrawFile = null;
		
		long timeout = 0;
		boolean argsOk = true;
		boolean pddl = true;
		
		for(int i=0; i<args.length && argsOk; i++) {
			if(args[i].equals("-nopddl")) {
				pddl = false;
				graphplan.setPddl(pddl);
			}else if(args[i].equals("-d")) { /* The domain argument */
				if(++i < args.length && !args[i].startsWith("-")) {
					domainFilename = args[i];
				} else {
					logger.warning("-d argument requires a filename with the domain");
					argsOk = false;
				}
			} else if(args[i].equals("-p")) { /* The problem argument */
				if(++i < args.length && !args[i].startsWith("-")) {
					problemFilename = args[i];
				} else {
					logger.warning("-p argument requires a filename with the problem");
					argsOk = false;
				}
			} else if(args[i].equals("-maxlevels")) {
				if(++i < args.length && !args[i].startsWith("-")) {
					try {
						int levels = Integer.parseInt(args[i]);
						if(levels > 0) {
							graphplan.setMaxLevels(levels);
						}
					} catch (NumberFormatException e) {
						logger.warning("-maxlevels argument requires a positive integer number of levels");
					}
				} else {
					logger.warning("-maxlevels argument requires a positive integer number of levels");
					argsOk = false;
				}
			} else if(args[i].equals("-timeout")) {
				if(++i < args.length && !args[i].startsWith("-")) {
					try {
						timeout = Long.parseLong(args[i]);
					} catch (NumberFormatException e) {
						logger.warning("-timeout argument requires a positive integer amount of time");
					}
				} else {
					logger.warning("-timeout argument requires a positive integer amount of time");
					argsOk = false;
				}
			} else if(args[i].equals("-noopsFirst")) {
				noopsFirst = true;
				operatorsLatest = false;
			} else if(args[i].equals("-operatorsLatest")) {
				operatorsLatest = true;
			} else if(args[i].equals("-propositionsSmallest")) {
				propositionsSmallest = true;
			} else if(args[i].equals("-sortGoals")) {
				sortGoals = true;
				propositionsSmallest = false;
			}  else if(args[i].equals("-noHeuristics")) {
				sortGoals = false;
				propositionsSmallest = false;
				noopsFirst = false;
				operatorsLatest = false;
			} else if(args[i].equals("-draw")) {
				if(++i < args.length && !args[i].startsWith("-")) {
					graphDrawFile = args[i];
				} else {
					logger.warning("-draw argument requires a valid filename");
					argsOk = false;
				}
			}
		}
		
		if(domainFilename == null || problemFilename == null)
			Graphplan.wrongParametersMessage();
		
		File opFile = new File(domainFilename);
		File probFile = new File(problemFilename);
		if(!opFile.exists()) {
			logger.warning("Domain file \'"+domainFilename+"\' does not exist");
			argsOk = false;
		} 
		if(!probFile.exists()){
			logger.warning("Problem file \'"+problemFilename+"\' does not exist");
			argsOk = false;
		} 
		
		if(!pddl) {
			try {
				operators = new FileInputStream(opFile);
				problem = new FileInputStream(probFile);
			} catch (FileNotFoundException e) {
				logger.warning(e.toString());
				argsOk = false;
			}
			argsOk = (operators != null) && (problem != null);
		}
		
		if(argsOk) {
			long t1 = System.currentTimeMillis();
			DomainDescription domain = null;
			try {
				if(pddl){
					logger.finest("JavaGP - PDDL\n");
					logger.finest("+ DOMAIN: " + domainFilename);
					logger.finest("+ PROBLEM: " + problemFilename);
					PDDLPlannerAdapter parserPDDL = new PDDLPlannerAdapter(domainFilename, problemFilename);
					domain = parserPDDL.getDomainDescriptionFromPddlObject();
				} else {
					logger.finest("JavaGP - STRIPS\n");
					logger.finest("+ DOMAIN: " + domainFilename);
					logger.finest("+ PROBLEM: " + problemFilename);
					PlannerParser parser = new PlannerParser();
					domain = parser.parseProblem(operators, problem);
				}
			} catch (ParserException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (ParseException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			PlanResult result = null;
			
			logger.fine("Selected Heuristics: ");
			if(Graphplan.noopsFirst) 
				logger.fine("\t+ Heuristic for actions: Select Noops first");
			
			if(Graphplan.operatorsLatest) 
				logger.fine("\t+ Heuristic for actions: Select actions that appears latest in the Planning Graph.");
			
			if(Graphplan.propositionsSmallest) 
				logger.fine("\t+ Heuristic for subgoals: Select firstly propositions that leads to the smallest set of resolvers.");
			
			if(Graphplan.sortGoals) 
				logger.fine("\t+ Heuristic for subgoals: Sort goals by proposition that appears earliest in the Planning Graph.");
			
			
			try {
				Runtime runtime = Runtime.getRuntime();
				NumberFormat fm = DecimalFormat.getInstance();
				fm.setMaximumFractionDigits(2);
				
				logger.info("Running planner, maximum memory: "+fm.format(runtime.maxMemory()/Math.pow(1024, 2))+"MB");
				if(timeout > 0) {
					result = graphplan.plan(domain, timeout);
				} else {
					result = graphplan.plan(domain); 
				}
				long t2 = System.currentTimeMillis();
				long totalTime = (t2-t1);
				logger.info("Planning took "+(totalTime)+"ms ( " + (totalTime/1000)+"s )");
				logger.info("Total memory used: "+fm.format(runtime.totalMemory()/Math.pow(1024, 2))+"MB");
				if(result.isTrue()) {
					logger.info("Plan found:\n"+result.toString()); 
					logger.info("Plan length: " + result.getPlanLength());
					if(graphDrawFile != null) {
						logger.info("Drawing planning graph to "+graphDrawFile);
						DotGraphDrawVisitor drawVisitor = new DotGraphDrawVisitor();
						if(graphplan.planningGraph.accept(drawVisitor)) {
							PrintWriter writer = new PrintWriter(new File(graphDrawFile));
							writer.println(drawVisitor.toString());
							writer.flush();
							writer.close();
						}
					}
				} else {
					logger.warning("No plan found");
				}
			} catch (PlanningGraphException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (OperatorFactoryException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (TimeoutException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				Runtime runtime = Runtime.getRuntime();
				logger.severe("Memory use exceeded maximum allocated for VM");
				long t2 = System.currentTimeMillis();
				long totalTime = (t2-t1);
				logger.info("Planning took "+(totalTime)+"ms ( " + (totalTime/1000)+"s ) before error");
				logger.severe("Current maximum memory: "+runtime.maxMemory()/1024+"kb");
			}
			
		} else {
			Graphplan.wrongParametersMessage();
		}
	}
	
	private static void wrongParametersMessage(){
		logger.warning("Wrong parameters");
		logger.info("Usage: \'java -jar JavaGP \'" +
				"\n\t>>> STRIPS Language: " +
				"\n\t\t" + "java -jar javagp.jar -nopddl -d examples/strips/ma-prodcell/domain.txt -p examples/strips/ma-prodcell/problem.txt" +
				"\n\n\t>>> PDDL Language: " +
				"\n\t\t" + "java -jar javagp.jar -d examples/pddl/blockworld/blockworlds.pddl -p examples/pddl/blockworld/pb1.pddl" +
				"\n\n\t>>> Planner arguments: " +
				"\n\t-maxlevels <NUMBER>, " + "\tMax Graph levels."+
				"\n\t-timeout <NUMBER>, " + "\tPlanning timeout." +
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
				"\n\t-operatorsLatest"+
				"\n\t-propositionsSmallest"
				+ "\n\n -v write verbose output"
				);
		System.exit(1);
	}
	
	public static void setupLogger() {
		try {
			if (new File(LOGGER_FILE).exists()) {
				LogManager.getLogManager().readConfiguration(new FileInputStream(new File(LOGGER_FILE)));
			} else {
				LogManager.getLogManager().readConfiguration(Graphplan.class.getResourceAsStream("/" + LOGGER_FILE));
			}
		} catch (Exception e) {
			System.err.println("Error setting up logger:" + e);
		}
	}
	
	/**
	 * Empty constructor for testing.
	 */
	public Graphplan() {
	}
	
	/**
	 * Sets the maximum number of levels to be searched for in creating the graph.
	 * @param maxLevels
	 */
	public void setMaxLevels(int maxLevels) {
		this.maxLevels = maxLevels;
	}
	
	/**
	 * 
	 * @param domainDescription
	 * @return
	 * @throws PlanningGraphException
	 * @throws OperatorFactoryException 
	 */
	public PlanResult plan(DomainDescription domainDescription) throws PlanningGraphException, OperatorFactoryException {
		PropositionLevel initialLevel = new PropositionLevel();
		initialLevel.addPropositions(domainDescription.getInitialState());
		this.solutionExtraction = new SolutionExtractionVisitor(domainDescription.getGoalState());
		
		/*Closed World Assumption - Simple Implementation by goals*/
//		for(Proposition g: domainDescription.getGoalState()){
//			if(!initialLevel.hasProposition(g)){
//				//Add negative for proposition g
//				PropositionImpl p = new PropositionImpl(g.negated(), g.getFunctor());
//				p.setTerms(g.getTerms());
//				initialLevel.addProposition(p);
//			}
//		}

		System.out.println("OPTIMIZATION: JavaGP using Static Mutexes Table");
		System.out.println("OPTIMIZATION: JavaGP using Memoization");
		
		if(this.pddl) {
			System.out.println("OPTIMIZATION: JavaGP using Types");
			//If domain has negative preconditions, the planner will use the closed world assumption 
			if(domainDescription.isNegativePreconditions()) {
				System.out.println("OPTIMIZATION: JavaGP using Closed World Assumption (Lazily)");
				this.planningGraph = new PlanningGraphClosedWorldAssumption(initialLevel, domainDescription.getTypes(), domainDescription.getParameterTypes(), new StaticMutexesTable(new ArrayList<Operator>(domainDescription.getOperators())));
			} else this.planningGraph = new PlanningGraph(initialLevel, domainDescription.getTypes(), domainDescription.getParameterTypes(), new StaticMutexesTable(new ArrayList<Operator>(domainDescription.getOperators())));
		} else this.planningGraph = new PlanningGraph(initialLevel, new StaticMutexesTable(new ArrayList<Operator>(domainDescription.getOperators())));
		
		System.out.println();
		
		OperatorFactory.getInstance().resetOperatorTemplates();
		
		for(Operator operator:domainDescription.getOperators()) {
			OperatorFactory.getInstance().addOperatorTemplate(operator);
		}
		
		boolean planFound = false;
		
		while(!planFound && (this.planningGraph.size() <= this.maxLevels)) {
			try {
				logger.info("Expanding graph");
				this.planningGraph.expandGraph();
			} catch (PlanningGraphException e) {
				//If we have a problem with the planning graph
				//Issue the error and quit
				System.err.println(e);
				return new PlanResult(false);
			}
			if(this.planningGraph.goalsPossible(domainDescription.getGoalState(), this.planningGraph.size()-1)) {
				//Extract solution
				logger.info("Extracting solution");
				planFound = this.planningGraph.accept(this.solutionExtraction);
				if(planFound) {
					logger.info("Plan found with "+((int)this.planningGraph.size()/2)+" steps");
				} else {
					logger.info("Plan not found with "+((int)this.planningGraph.size()/2)+" steps");
					if(!planPossible()) {
						throw new PlanningGraphException("Graph has levelled off, plan is not possible.",this.planningGraph.levelOffIndex());
					}
				}
			} else {
				logger.info("Goals not possible with "+((int)this.planningGraph.size()/2)+" steps");
				//If the goals are not possible, and the graph has levelled off,
				//then this problem has no possible plan
				if(this.planningGraph.levelledOff()) {
					throw new PlanningGraphException("Goals are not possible and graph has levelled off, plan is not possible.",this.planningGraph.levelOffIndex());
				}
			}
		}
		
		return this.solutionExtraction.getPlanResult();
	}
	
	/**
	 * Executes the Graphplan algorithm with a specified timeout.
	 * @param domainDescription
	 * @param timeout
	 * @return
	 * @throws PlanningGraphException
	 * @throws OperatorFactoryException
	 * @throws TimeoutException
	 */
	public PlanResult plan(DomainDescription domainDescription, long timeout) throws PlanningGraphException, OperatorFactoryException, TimeoutException {
		PropositionLevel initialLevel = new PropositionLevel();
		initialLevel.addPropositions(domainDescription.getInitialState());
		this.solutionExtraction = new TimeoutSolutionExtractionVisitor(domainDescription.getGoalState());
		((TimeoutSolutionExtractionVisitor)solutionExtraction).setTimeout(timeout);
		
		this.planningGraph = new PlanningGraph(initialLevel, new StaticMutexesTable(new ArrayList<Operator>(domainDescription.getOperators())));
		OperatorFactory.getInstance().resetOperatorTemplates();
		
		for(Operator operator:domainDescription.getOperators()) {
			OperatorFactory.getInstance().addOperatorTemplate(operator);
		}
		
		boolean planFound = false;
		
		while(!planFound && (this.planningGraph.size() <= this.maxLevels)) {
			try {
				logger.info("Expanding graph");
				this.planningGraph.expandGraph();
			} catch (PlanningGraphException e) {
				//If we have a problem with the planning graph
				//Issue the error and quit
				System.err.println(e);
				return new PlanResult(false);
			}
			if(planningGraph.goalsPossible(domainDescription.getGoalState(), this.planningGraph.size()-1)) {
				//Extract solution
				logger.info("Extracting solution");
				planFound = this.planningGraph.accept(this.solutionExtraction);
				if(planFound) {
					logger.info("Plan found with "+((int)this.planningGraph.size()/2)+" steps");
				} else {
					if(((TimeoutSolutionExtractionVisitor)solutionExtraction).timedOut()) {
						logger.info("Planner timed out after "+timeout+" milliseconds");
						throw new TimeoutException("No plan possible in "+timeout+" milliseconds");
					}
					logger.info("Plan not found with "+((int)this.planningGraph.size()/2)+" steps");
					if(!planPossible()) {
						throw new PlanningGraphException("Graph has levelled off, plan is not possible.",this.planningGraph.levelOffIndex());
					}
				}
			} else {
				logger.info("Goals not possible with "+((int)this.planningGraph.size()/2)+" steps");
				//If the goals are not possible, and the graph has levelled off,
				//then this problem has no possible plan
				if(this.planningGraph.levelledOff()) {
					throw new PlanningGraphException("Goals are not possible and graph has levelled off, plan is not possible.",this.planningGraph.levelOffIndex());
				}
			}
		}
		
		return solutionExtraction.getPlanResult();
	}
	
	/**
	 * Returns whether or not a plan is possible according to both the 
	 * memoization table criterion and the graph level size criterion.
	 * @return
	 */
	public boolean planPossible() {
		if(!this.planningGraph.levelledOff()) {
			return true;
		} else {
			return this.solutionExtraction.levelledOff(this.planningGraph.levelOffIndex());
		}
	}
	
	/**
	 * Returns a list with the minimum preconditions necessary for the supplied plan to
	 * be successful.
	 * 
	 * TODO General cleanup and tuning of this method.
	 * 
	 * @param plan 			A list of operator invocations representing a plan
	 * @param description	The domain description in which the supplied plan is executed
	 * @return				The minimum set of propositions that must be true before execution
	 */
	public List<Proposition> getPlanPreconditions(List<String> plan, DomainDescription description) {
		// XXX This variable has been placed here to give us a slight speed up and to ease debug
		// XXX But this might not be a good idea if we try and change the singleton at runtime
		OperatorFactory operatorFactory = OperatorFactory.getInstance();
		
		for(Iterator<Operator> iter = description.getOperators().iterator(); iter.hasNext(); ) {
			try {
				//OperatorFactory.getInstance().addOperatorTemplate(iter.next());
				operatorFactory.addOperatorTemplate(iter.next());
			} catch (OperatorFactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Operator concreteOperators[] = new Operator[plan.size()];
		
		//First, get operator instances corresponing to the operator invocations
		//in the parameter
		for (int i = 0; i < concreteOperators.length; i++) {
			try {
				//concreteOperators[i] = OperatorFactory.getInstance().getOperator(plan.get(i));
				concreteOperators[i] = operatorFactory.getOperator(plan.get(i));
			} catch (OperatorFactoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Then Create the planning graph with an empty initial state
		PlanningGraph graph = new PlanningGraph(new PropositionLevel());
		//And populate it with action levels and proposition levels containing the obvious
		//preconditions from the subsequent action level
		for (int i = 0; i < concreteOperators.length; i++) {
			//We always assume our graph has a proposition level as its last level
			PropositionLevel propositionLevel = (PropositionLevel) graph.getGraphLevel(graph.size()-1);
			//into which we add the preconditions for this action
			propositionLevel.addPropositions(concreteOperators[i].getPreconds());
			//We then create an action level to contain this action
			ActionLevel actionLevel = new ActionLevel();
			actionLevel.addAction(concreteOperators[i]);
			graph.addGraphLevel(actionLevel);
			//And create another proposition level for its effects
			PropositionLevel propositionLevel2 = new PropositionLevel();
			propositionLevel2.addPropositions(concreteOperators[i].getEffects());
			
			graph.addGraphLevel(propositionLevel2);
		}
		
		//Then walk the graph backwards propagating the preconditions that were not generated
		//by the previous action level using noops
		for(int i = graph.size()-1; i > 0; i=i-2) {
			//We need to get the proposition levels surrounding our action level
			//As in: precond <- action <- effect
			PropositionLevel effectLevel = (PropositionLevel) graph.getGraphLevel(i);
			PropositionLevel precondLevel = (PropositionLevel) graph.getGraphLevel(i-2);
			ActionLevel actionLevel = (ActionLevel) graph.getGraphLevel(i-1);
			//Then see if each proposition in the effectLevel is connected by some action
			//to the precondLevel
			for (Iterator<Proposition> iter = effectLevel.getPropositions(); iter.hasNext();) {
				Proposition proposition = iter.next();
				//If no action is connected to the effect level
				if(actionLevel.getGeneratingActions(proposition).size() == 0) {
					//We need to propagate this action to the previous level
					actionLevel.addNoop(proposition);
					precondLevel.addProposition(proposition);
				}
			}
		}
		
		//this last bit looks rather nasty, I should review this when time allows
		PropositionLevel level = (PropositionLevel) graph.getGraphLevel(0);
		List<Proposition> planPreconditions = new ArrayList<Proposition>(level.size());
		
		for (Iterator<Proposition> iter = level.getPropositions(); iter.hasNext();) {
			planPreconditions.add(iter.next());
		}

		return planPreconditions;
	}
	
	/**
	 * @return the solutionExtraction
	 */
	public SolutionExtractionVisitor getSolutionExtraction() {
		return solutionExtraction;
	}

	/**
	 * @param solutionExtraction the solutionExtraction to set
	 */
	public void setSolutionExtraction(SolutionExtractionVisitor solutionExtraction) {
		this.solutionExtraction = solutionExtraction;
	}

	public void setPddl(boolean pddl) {
		this.pddl = pddl;
	}
}