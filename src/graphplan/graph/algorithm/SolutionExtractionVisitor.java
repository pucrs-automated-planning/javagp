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
package graphplan.graph.algorithm;

import graphplan.PlanResult;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.ActionLevel;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import graphplan.graph.GraphLevel;
import graphplan.graph.PropositionLevel;
import graphplan.graph.memo.MemoizationTable;
import graphplan.graph.planning.PlanningGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * A visitor class that implements the Graphplan solution extraction
 * algorithm.
 * 
 * TODO implement solution extraction using this pattern
 * @author Felipe Meneguzzi
 *
 */
public class SolutionExtractionVisitor implements GraphElementVisitor {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(SolutionExtractionVisitor.class.getName());
	
	protected final List<Proposition> goals;
	
	protected final MemoizationTable memoizationTable ;
	
	protected Stack<Set<Proposition>> subGoalStack;
	protected Stack<Set<Operator>> supportActionStack;
	
	protected PlanResult planResult = null;
	protected PlanningGraph planningGraph;
	
	public SolutionExtractionVisitor(List<Proposition> goals) {
		this.goals = goals;
		//By default the plan result will be false, unless changed during a
		//round of solution extraction
		planResult = new PlanResult(false);
		subGoalStack = new Stack<Set<Proposition>>();
		supportActionStack = new Stack<Set<Operator>>();
		
		memoizationTable = new MemoizationTable();
	}

	@SuppressWarnings("rawtypes")
	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			this.planningGraph = (PlanningGraph) element;
			if(this.planningGraph.getLastGraphLevel().isPropositionLevel()) {
				this.subGoalStack.clear();
				this.supportActionStack.clear();
				this.subGoalStack.push(new TreeSet<Proposition>(this.goals));
				
				/*TextDrawVisitor visitor = new TextDrawVisitor();
				planningGraph.accept(visitor);
				logger.info("Planning Graph is:");
				logger.info(visitor.toString());*/
				
				//Whenever we try to iterate in the graph, we need to expand
				//the no goods table to match the size of the graph
				memoizationTable.ensureCapacity(planningGraph.size()/2);
				
				if(this.planningGraph.getLastGraphLevel().accept(this)) {
					this.planResult = new PlanResult(this.supportActionStack);
				} else {
					this.planResult = new PlanResult(false);
				}
				
				//logger.info("Table size is: "+noGoodTableSize());
				//logger.info("Hits         : "+hits);
				//logger.info("Misses       : "+misses);
				
				return planResult.isTrue();
			} else {
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public boolean visitGraphLevel(GraphLevel graphLevel) {
		if(graphLevel.isActionLevel()) {
			//For every action level we visit, we add a list
			//of actions to the support action stack to be
			//used in the iteration of the proposition level
			boolean planFound = graphLevel.getPrevLevel().accept(this);
			return planFound;
		} else {
			if(graphLevel.getPrevLevel() == null) {
				//We hit the first level, return
				return true;
			}
			PropositionLevel propositionLevel = (PropositionLevel) graphLevel;
			Set<Proposition> subGoals = new TreeSet<Proposition>(this.subGoalStack.peek());
			
			//First, check the no goods table
			if(this.memoizationTable.isNoGood(this.subGoalStack.peek(), propositionLevel.getIndex())) {
				//And not even creep if the goals are no good
				return false;
			}
			
			//Then check if the goals are conceptually possible
			//If the goals are possible in this level
			if(propositionLevel.goalsPossible(subGoals)){
				//Then push a set of potential actions for them
				//And try to fill this up with operators
				//supportActionStack.push(new LinkedHashSet<Operator>());
				//logger.fine("At level "+propositionLevel.getIndex()+", trying to achieve "+subGoalStack.peek());
				
				boolean planFound = this.visitPropositionLevel(propositionLevel, subGoals);
				/*if(!planFound) {
					this.supportActionStack.pop();
				}*/
				return planFound;
			} else {
				//When memoization is in, check this
				return false;
			}
		}

		//return false;
	}
	
	private boolean visitPropositionLevel(PropositionLevel propositionLevel, 
										Set<Proposition> subGoals) {
		//If we have reached the first proposition level
		//We have found a plan
		//TODO check this for redundancy
		if(propositionLevel.getPrevLevel() == null) {
			return true;
		}
		
		if(this.memoizationTable.isNoGood(subGoals, propositionLevel.getIndex())) {
			return false;
		}
		
		final ActionLevel actionLevel = (ActionLevel) propositionLevel.getPrevLevel();
		
		ArrayList<Proposition> subGoalsSorted = new ArrayList<Proposition>(subGoals);
		
		/* Heuristic: sort goals by proposition that appears earliest in the planning graph */
//		Collections.sort(subGoalsSorted, new Comparator<Proposition>() {
//			public int compare(Proposition o1, Proposition o2) {
//				return (o1.getIndex() > o2.getIndex() ? -1: (o1.getIndex() == o2.getIndex() ? 0 : 1));
//			}
//		});

		/* Heuristic: select firstly propositions that leads to the smallest set of resolvers */
		Collections.sort(subGoalsSorted, new Comparator<Proposition>() {
			public int compare(Proposition o1, Proposition o2) {
				int o1Size = actionLevel.getGeneratingActions(o1).size();
				int o2Size = actionLevel.getGeneratingActions(o2).size();
				return (o1Size < o2Size ? -1: (o1Size == o2Size ? 0 : 1));
			}
		});
		
		boolean planFound = this.search(subGoalsSorted, new HashSet<Operator>(), actionLevel, new HashSet<Operator>());
		if(!planFound) {
			this.memoizationTable.addNoGood(subGoals, propositionLevel.getIndex());
			this.subGoalStack.pop();
		} else return true;

		return false;
	}
	
	public boolean search(List<Proposition> subGoals, Set<Operator> operators, ActionLevel actionLevel, Set<Operator> mutex){
		boolean planFound = false;
		
		if(subGoals.isEmpty()){
			Set<Proposition> newSubGoals = determineSubgoals(operators);
			this.subGoalStack.push(newSubGoals);
			planFound = this.visitPropositionLevel((PropositionLevel) actionLevel.getPrevLevel(), newSubGoals);
			if(planFound) this.supportActionStack.push(operators);
		} else {
			List<Operator> resolvers = actionLevel.getGeneratingActions(this.popGoal(subGoals));
			resolvers = this.andNotMutexes(resolvers, mutex);
			while(!resolvers.isEmpty() && !planFound){
				Operator resolver = this.popResolver(resolvers);
				Set<Operator> newOperators = new HashSet<Operator>(operators);
				newOperators.add(resolver);
				List<Proposition> newSubGoals = this.getSubGoals(resolver, subGoals);
				Set<Operator> newMutex = new HashSet<Operator>(mutex);
				if(actionLevel.getMutexes().get(resolver) != null) newMutex.addAll(actionLevel.getMutexes().get(resolver));
				
				planFound = this.search(newSubGoals, newOperators, actionLevel, newMutex);
			}
		}
		return planFound;
	}
	
	/**
	 * andNot
	 * @param resolvers
	 * @param mutex
	 * @return
	 */
	private List<Operator> andNotMutexes(List<Operator> resolvers, Set<Operator> mutex) {
		List<Operator> andNot = new ArrayList<Operator>();

		for(Operator op: resolvers){
			if(!mutex.contains(op)) andNot.add(op);
		}
		
		return andNot;
	}

	/**
	 * andNot
	 * @param resolver
	 * @param subGoals
	 * @return
	 */
	private List<Proposition> getSubGoals(Operator resolver, List<Proposition> subGoals) {
		List<Proposition> andNot = new ArrayList<Proposition>();
		
		for(Proposition p: subGoals){
			if(!resolver.getEffects().contains(p)) andNot.add(p);
		}
		return andNot;
	}

	private Proposition popGoal(List<Proposition> subGoals){
		Proposition p = subGoals.get(0);
		return p;
	}
	
	private Operator popResolver(List<Operator> resolvers){
		Operator op = resolvers.remove(0);
		return op;
	}
	
	/**
	 * Tries to determine early one if a set of actions will not be minimal;
	 * @param proposition
	 * @param actions
	 * @return
	 */
	protected final boolean alreadySatisfied(Proposition proposition, Set<Operator> actions) {
		for(Operator operator: actions) {
			if(operator.getEffects().contains(proposition))
				return true;
		}
		return false;
	}
	
	/**
	 * Gets the preconditions for the operators 
	 * given as parameter
	 * @param operators
	 * @return
	 */
	private Set<Proposition> determineSubgoals(Set<Operator> operators) {
		final TreeSet<Proposition> subGoals = new TreeSet<Proposition>();
		
		for (Operator operator : operators) {
			for(Proposition proposition : operator.getPreconds()) {
				subGoals.add(proposition);
			}
		}
		
		return subGoals;
	}
	
	/**
	 * Returns whether or not the memoization table has levelled off. This just
	 * forwards the call to the memoization table.
	 * 
	 * XXX Review this method, I suspect it may be wrong according to Blum and
	 * Furst's paper.
	 * 
	 * @param graphLevel The last graph level
	 * @return
	 */
	public final boolean levelledOff(int graphLevel) {
		return memoizationTable.levelledOff(graphLevel);
	}

	/**
	 * Returns the plan resulting from this solution extraction cycle.
	 * @return
	 */
	public PlanResult getPlanResult() {
		//TODO make a real implementation of this method
		return planResult;
	}
}
