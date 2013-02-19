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
import graphplan.graph.PlanningGraph;
import graphplan.graph.PlanningGraphException;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.SolutionExtractionVisitor;
import graphplan.graph.algorithm.TimeoutSolutionExtractionVisitor;
import graphplan.parser.PlannerParser;
import graphplan.parser.PDDLPlannerParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
	
	private static boolean pddl = false;
	
	public static void main(String[] args) throws Exception {
		setupLogger();
		Graphplan graphplan = new Graphplan();
		InputStream operators = null;
		InputStream problem = null;
		
		String pddlProblem = null;
		String pddlDomain = null;
		
		long timeout = 0;
		boolean argsOk = true;

		for(int i=0; i<args.length && argsOk; i++) {
			if(args[i].equals("-d")) { /* The domain argument */
				if(++i < args.length && !args[i].startsWith("-")) {
					if(args[0].equals("-pddl")) {
						pddl = true;
						pddlDomain = args[i];  
					} else {
						try {
							operators = new FileInputStream(args[i]);
						} catch (FileNotFoundException e) {
							logger.warning(e.toString());
							argsOk = false;
						}
					}
				} else {
					logger.warning("-d argument requires a filename with the domain");
					argsOk = false;
				}
			} else if(args[i].equals("-p")) { /* The problem argument */
				if(++i < args.length && !args[i].startsWith("-")) {
					if(args[0].equals("-pddl")) {
						pddl = true;
						pddlProblem = args[i];  
					} else {
						try {
							problem = new FileInputStream(args[i]);
						} catch (FileNotFoundException e) {
							logger.warning(e.toString());
							argsOk = false;
						}
					}
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
			}
		}
		
		if(!pddl) argsOk = (operators != null) && (problem != null);
		
		if(argsOk) {
			long t1 = System.currentTimeMillis();
			DomainDescription domain = null;
			
			if(pddl){
				PDDLPlannerParser parserPDDL = new PDDLPlannerParser(pddlDomain, pddlProblem);
				domain = parserPDDL.getDomainDescriptionFromPddlObject();
			} else {
				PlannerParser parser = new PlannerParser();
				domain = parser.parseProblem(operators, problem);
			}
			
			PlanResult result = null;
			GraphplanBitSet graphplanBitSet = new GraphplanBitSet(domain);
			
			if(timeout > 0) {
				result = graphplan.plan(domain, timeout);
			} else {
				result = graphplan.plan(domain); 
			}
			
			if(result.isTrue()) {
				logger.info("Plan found");
				//logger.info(result.toString());
				//Change plan output to standard output for easier redirection.
				System.out.println(result.toString());
			} else {
				logger.warning("No plan found");
			}
			long t2 = System.currentTimeMillis();
			logger.info("Planning took "+((t2-t1)/1000)+"s");
		} else {
			logger.warning("Wrong parameters");
			logger.info("Usage is java -jar JavaGP -p <problem> -d <domain> [-maxlevels <max_graph_levels>] [-timeout <planning_timeout>]");
			System.exit(1);
		}
	}
	
	public static void setupLogger() {
		try {
			if (new File(LOGGER_FILE).exists()) {
				LogManager.getLogManager().readConfiguration(new FileInputStream(new File(LOGGER_FILE)));
			} else {
				LogManager.getLogManager().readConfiguration(Graphplan.class.getResourceAsStream("/" + LOGGER_FILE));
			}
		} catch (Exception e) {
			// e.printStackTrace();
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
		
		if(pddl) this.planningGraph = new PlanningGraph(initialLevel, domainDescription.getTypes(), domainDescription.getParameterTypes());
		else this.planningGraph = new PlanningGraph(initialLevel);
		
		OperatorFactory.getInstance().resetOperatorTemplates();
		
		for(Operator operator:domainDescription.getOperators()) {
			OperatorFactory.getInstance().addOperatorTemplate(operator);
		}
		
		boolean planFound = false;
		
		while(!planFound && (planningGraph.size() <= maxLevels)) {
			try {
				logger.info("Expanding graph");
				planningGraph.expandGraph();
			} catch (PlanningGraphException e) {
				//If we have a problem with the planning graph
				//Issue the error and quit
				System.err.println(e);
				return new PlanResult(false);
			}
			if(planningGraph.goalsPossible(domainDescription.getGoalState(), planningGraph.size()-1)) {
				//extract solution
				logger.info("Extracting solution");
				planFound = planningGraph.accept(solutionExtraction);
				if(planFound) {
					logger.info("Plan found with "+((int)planningGraph.size()/2)+" steps");
				} else {
					logger.info("Plan not found with "+((int)planningGraph.size()/2)+" steps");
					if(!planPossible()) {
						throw new PlanningGraphException("Graph has levelled off, plan is not possible.",planningGraph.levelOffIndex());
					}
				}
			} else {
				logger.info("Goals not possible with "+((int)planningGraph.size()/2)+" steps");
				//If the goals are not possible, and the graph has levelled off,
				//then this problem has no possible plan
				if(planningGraph.levelledOff()) {
					throw new PlanningGraphException("Goals are not possible and graph has levelled off, plan is not possible.",planningGraph.levelOffIndex());
				}
			}
		}
		
		return solutionExtraction.getPlanResult();
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
	public PlanResult plan(DomainDescription domainDescription, long timeout) 
	       throws PlanningGraphException, OperatorFactoryException, TimeoutException {
		PropositionLevel initialLevel = new PropositionLevel();
		initialLevel.addPropositions(domainDescription.getInitialState());
		solutionExtraction = new TimeoutSolutionExtractionVisitor(domainDescription.getGoalState());
		((TimeoutSolutionExtractionVisitor)solutionExtraction).setTimeout(timeout);
		
		planningGraph = new PlanningGraph(initialLevel);
		OperatorFactory.getInstance().resetOperatorTemplates();
		
		for(Operator operator:domainDescription.getOperators()) {
			OperatorFactory.getInstance().addOperatorTemplate(operator);
		}
		
		boolean planFound = false;
		
		while(!planFound && (planningGraph.size() <= maxLevels)) {
			try {
				logger.info("Expanding graph");
				planningGraph.expandGraph();
			} catch (PlanningGraphException e) {
				//If we have a problem with the planning graph
				//Issue the error and quit
				System.err.println(e);
				return new PlanResult(false);
			}
			if(planningGraph.goalsPossible(domainDescription.getGoalState(), planningGraph.size()-1)) {
				//extract solution
				logger.info("Extracting solution");
				planFound = planningGraph.accept(solutionExtraction);
				if(planFound) {
					logger.info("Plan found with "+((int)planningGraph.size()/2)+" steps");
				} else {
					if(((TimeoutSolutionExtractionVisitor)solutionExtraction).timedOut()) {
						logger.info("Planner timed out after "+timeout+" milliseconds");
						throw new TimeoutException("No plan possible in "+timeout+" milliseconds");
					}
					logger.info("Plan not found with "+((int)planningGraph.size()/2)+" steps");
					if(!planPossible()) {
						throw new PlanningGraphException("Graph has levelled off, plan is not possible.",planningGraph.levelOffIndex());
					}
				}
			} else {
				logger.info("Goals not possible with "+((int)planningGraph.size()/2)+" steps");
				//If the goals are not possible, and the graph has levelled off,
				//then this problem has no possible plan
				if(planningGraph.levelledOff()) {
					throw new PlanningGraphException("Goals are not possible and graph has levelled off, plan is not possible.",planningGraph.levelOffIndex());
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
		if(!planningGraph.levelledOff()) {
			return true;
		} else {
			return solutionExtraction.levelledOff(planningGraph.levelOffIndex());
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
}
