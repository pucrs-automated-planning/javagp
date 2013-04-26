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
import graphplan.domain.jason.OperatorImpl;
import graphplan.graph.ActionLevel;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import graphplan.graph.GraphLevel;
import graphplan.graph.PlanningGraph;
import graphplan.graph.PropositionLevel;
import graphplan.graph.memo.MemoizationTable;

import java.util.HashSet;
import java.util.Iterator;
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
	
	public SolutionExtractionVisitor(List<Proposition> goals) {
		this.goals = goals;
		//By default the plan result will be false, unless changed during a
		//round of solution extraction
		planResult = new PlanResult(false);
		subGoalStack = new Stack<Set<Proposition>>();
		supportActionStack = new Stack<Set<Operator>>();
		
		memoizationTable = new MemoizationTable();
	}

	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			
			if(planningGraph.getLastGraphLevel().isPropositionLevel()) {
				subGoalStack.clear();
				supportActionStack.clear();
				subGoalStack.push(new TreeSet<Proposition>(goals));
				
				/*TextDrawVisitor visitor = new TextDrawVisitor();
				planningGraph.accept(visitor);
				logger.info("Planning Graph is:");
				logger.info(visitor.toString());*/
				
				//Whenever we try to iterate in the graph, we need to expand
				//the no goods table to match the size of the graph
				memoizationTable.ensureCapacity(planningGraph.size()/2);
				
				if(planningGraph.getLastGraphLevel().accept(this)) {
					planResult = new PlanResult(this.supportActionStack);
				} else {
					planResult = new PlanResult(false);
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
			Set<Proposition> subGoals = new TreeSet<Proposition>(subGoalStack.peek());
			
			//First, check the no goods table
			if(memoizationTable.isNoGood(subGoalStack.peek(), propositionLevel.getIndex())) {
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
		
		if(memoizationTable.isNoGood(subGoals, propositionLevel.getIndex())) {
			return false;
		}
		
		ActionLevel actionLevel = (ActionLevel) propositionLevel.getPrevLevel();
		
		//For each possible set of actions
		for(ActionSetIterator iterator = new ActionSetIterator(subGoals, actionLevel); iterator.hasNext(); ) {
			Set<Operator> selectedOperators = iterator.next();
			if(selectedOperators != null) {
				supportActionStack.push(selectedOperators);
				Set<Proposition> newSubGoals = determineSubgoals(selectedOperators);
				this.subGoalStack.push(newSubGoals);
				
				boolean planFound = propositionLevel.getPrevLevel().accept(this);
				if(!planFound) {
					this.memoizationTable.addNoGood(newSubGoals, propositionLevel.getIndex()-2);
					this.subGoalStack.pop();
					this.supportActionStack.pop();
				} else {
					return true;
				}
			}
		}
		
		return false;
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
	 * An iterator over the set of all possible combinations of operators
	 * needed to satisfy the specified set of subgoals in the specified
	 * action level.
	 * @author Felipe Meneguzzi
	 *
	 */
	public class ActionSetIterator implements Iterator<Set<Operator>> {
		//The current actions selected by this iterator
		private final Set<Operator> actionSet;
		//The subgoals for which action sets are trying to satisfy
		private final Proposition[] subGoals;
		//A reference to the action level used in this iterator
		private final ActionLevel actionLevel;
		//The iterators over the possible operators for each proposition
		private final Iterator<Operator>[] iterators;
		//A cache of the required operators list
		private final List<Operator>[] requiredOperators;
		//The operators selected so far
		private final Operator[] selectedOperators;
		
		//Temp variable
		protected final Set<Proposition> achievableGoals;
		
		public ActionSetIterator(Set<Proposition> subGoals, ActionLevel actionLevel) {
			this.actionSet = new HashSet<Operator>(subGoals.size());
			this.achievableGoals = new HashSet<Proposition>();
			this.subGoals = subGoals.toArray(new Proposition[subGoals.size()]);
			this.iterators = new Iterator[subGoals.size()];
			this.actionLevel = actionLevel;
			this.requiredOperators = new List[subGoals.size()];
			this.selectedOperators = new Operator[subGoals.size()];
			
//			Proposition[] newSubGoals = this.subGoals.clone();
//			for(int i=0; i< this.subGoals.length; i++) {
//				this.subGoals[i] = newSubGoals[(this.subGoals.length-1)-i];
//			}
			
			for(int i=0; i< this.subGoals.length; i++) {
				List<Operator> ops = actionLevel.getGeneratingActions(this.subGoals[i]);
				this.requiredOperators[i] = ops;
				this.iterators[i] = ops.iterator();
				if(i>0) {
					selectedOperators[i] = iterators[i].next();
				}
			}
		}

		public boolean hasNext() {
			for(Iterator<Operator> iterator : iterators) {
				if(iterator.hasNext()) {
					return true;
				}
			}
			return false;
		}

		public Set<Operator> next() {
			boolean advanceNext = true;
			
			int i=0;
			//We only advance the next iterator if we had
			//to reset the current one
			while(advanceNext) {
				if(iterators[i].hasNext()) {
					advanceNext = false;
					selectedOperators[i] = iterators[i].next();
				} else {
					iterators[i] = requiredOperators[i].iterator();
					selectedOperators[i] = iterators[i].next();
					i++;
				}
			}
			
			achievableGoals.clear();
			actionSet.clear();
			
			for(i=0; i<selectedOperators.length; i++) {
				//Maybe we don't need to do all these checks
				//Make sure we did not select the same operator twice
				if(actionSet.contains(selectedOperators[i])) {
					//return null;
					continue;
				}
				//If the selected operator is not already in the set
				//And the proposition it is supposed to achieve
				//has already been achieved through some other operator
				//Then this set is not minimal
				if(achievableGoals.contains(subGoals[i])) {
					return null;
				}
				//Make sure this operator is not inconsistent with the 
				//ones selected
				for(int j=0; j<i; j++) {
					if(actionLevel.isMutex(selectedOperators[i], selectedOperators[j])) {
						//continue;
						return null;
					}
				}
				actionSet.add(selectedOperators[i]);
				for(Proposition prop : selectedOperators[i].getEffects()) {
					achievableGoals.add(prop);
				}
			}
			
			return actionSet;
		}
		
		/**
		 * Verifies if the selected operators are consistent among themselves
		 * @return
		 */
		private final boolean isConsistent() {
			for(int i=0; i<selectedOperators.length; i++) {
				for(int j=i+1; j<selectedOperators.length; j++) {
					if(actionLevel.isMutex(selectedOperators[i], selectedOperators[j])) {
						return false;
					}
				}
			}
			return true;
		}
		
		/**
		 * Determines if the action set contained in the referred plan level 
		 * is minimal, given the set of goals supplied
		 * @param actions
		 * @param subGoals
		 * 
		 * @return
		 */
		private final boolean isMinimalActionSet() {
			//Select one action and check if the goals can be achieved without it
			//operatorOut is the action being taken out from the vector of selected
			//actions
			for(Operator operatorOut : selectedOperators) {
				//We store the possible goals in this list
				achievableGoals.clear();
				for(Proposition proposition : subGoals) {
					achievableGoals.add(proposition);
				}
				
				for(Operator operator: selectedOperators) {
					//We jump the removed operator
					if(operator == operatorOut) {
						continue;
					}
					//And check if the goals are still achievable
					
					//By getting the action's effects
					//List<Proposition> effects = (List<Proposition>) operator.getEffects();
					//And removing them from the list of achievable ones
					//achievableGoals.removeAll(effects);
					for(Proposition effect:operator.getEffects()) {
						achievableGoals.remove(effect);
					}
					if(achievableGoals.isEmpty()) {
						//If we manage to achieve them all, this set is not minimal
						return false;
					}
				}
				//If the goals achievable with the removal of the action "i", are different
				//than the goals achievable otherwise, then the goal list will have different sizes
				/*bRes = achievableGoals.size() != subGoals.size();
				if(!bRes) {
					return bRes;
				}*/
			}
			
			return true;
		}

		public void remove() {
			// TODO Auto-generated method stub
		}
		
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
