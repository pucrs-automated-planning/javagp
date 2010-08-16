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
import graphplan.graph.PlanningGraph;
import graphplan.graph.PropositionLevel;
import java.util.ArrayList;
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
@SuppressWarnings("unchecked")
public class NoMemoSolutionExtractionVisitor implements GraphElementVisitor {
	private static final Logger logger = Logger.getLogger(NoMemoSolutionExtractionVisitor.class.getName());
	
	protected final List<Proposition> goals;
	
	protected Stack<Set<Proposition>> subGoalStack;
	protected Stack<Set<Operator>> supportActionStack;
	
	protected PlanResult planResult = null;
	
	public NoMemoSolutionExtractionVisitor(List<Proposition> goals) {
		this.goals = goals;
		//By default the plan result will be false, unless changed during a
		//round of solution extraction
		planResult = new PlanResult(false);
		subGoalStack = new Stack<Set<Proposition>>();
		supportActionStack = new Stack<Set<Operator>>();
	}

	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			//TODO implement the actual iteration over the graph
			if(planningGraph.getLastGraphLevel().isPropositionLevel()) {
				subGoalStack.clear();
				supportActionStack.clear();
				subGoalStack.push(new TreeSet<Proposition>(goals));
				
				/*TextDrawVisitor visitor = new TextDrawVisitor();
				planningGraph.accept(visitor);
				logger.fine("Planning Graph is:");
				logger.fine(visitor.toString());*/
				
				if(planningGraph.getLastGraphLevel().accept(this)) {
					planResult = new PlanResult(this.supportActionStack);
				} else {
					planResult = new PlanResult(false);
				}
				
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
			PropositionLevel propositionLevel = (PropositionLevel) graphLevel;
			Set<Proposition> subGoals = new TreeSet<Proposition>(subGoalStack.peek());
			//If the goals are possible in this level
			if(propositionLevel.goalsPossible(subGoals)){
				//Then push a set of potential actions for them
				//And try to fill this up with operators
				supportActionStack.push(new TreeSet<Operator>());
				logger.fine("At level "+propositionLevel.getIndex()+", trying to achieve "+subGoalStack.peek());
				boolean planFound = this.visitPropositionLevel(propositionLevel, subGoals);
				if(!planFound) {
					this.supportActionStack.pop();
				}
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
		if(propositionLevel.getPrevLevel() == null) {
			return true;
		}
		
		ActionLevel actionLevel = (ActionLevel) propositionLevel.getPrevLevel();
		//If we have collected supporting actions for all subgoals
		//We can proceed the iteration into the graph using the actions
		//recently selected
		if(subGoals.isEmpty()) {
			//If the goals chosen for this level are not minimal, force failure
			if(!isMinimalActionSet(supportActionStack.peek(), subGoalStack.peek())) {
				return false;
			} 
			
			//Otherwise, push the dependencies for this subgoal into the
			//stack of subgoals
			this.subGoalStack.push(determineSubgoals(supportActionStack.peek()));
			
			logger.fine("At level "+(propositionLevel.getIndex()-1)+", creeping with operators: "+this.supportActionStack.peek());
			
			//And creep into the graph
			boolean planFound = propositionLevel.getPrevLevel().accept(this);
			
			//If we have not found a plan, unstack the partial data from 
			//this attempt.
			if(!planFound) {
				this.subGoalStack.pop();
			}
			
			return planFound;
		} else {
			final Proposition proposition = subGoals.iterator().next();
			subGoals.remove(proposition);
			//Get the actions that generate this proposition to search for a solution
			List<Operator> requiringActions = actionLevel.getGeneratingActions(proposition);
			for(Operator operator: requiringActions) {
				//Check if the operator has not already been selected
				boolean addOper = !supportActionStack.peek().contains(operator);
				if(addOper) {
					//If the selected operator is not already in the plan
					//Check it against other operators in this step of the plan
					boolean isMutex = false;
					for(Operator operator2 : supportActionStack.peek()) {
						//If this support action
						if(actionLevel.isMutex(operator, operator2)) {
							isMutex = true;
							break;
						}
					}
					//If this particular operator was mutex with the ones in the
					//plan, then try another one
					if(isMutex) {
						continue;
					} else {
						//If no mutex was found the operator should be added
						supportActionStack.peek().add(operator);
					}
				}
				//If the operator has passed the tests, then search deeper into the operators
				boolean planFound = visitPropositionLevel(propositionLevel, subGoals);
				
				if(!planFound && addOper) {
					supportActionStack.peek().remove(operator);
				}
				
				if(planFound) {
					return true;
				}
			}
			subGoals.add(proposition);
		}
		return false;
	}
	
	/**
	 * Determines if the action set contained in the referred plan level 
	 * is minimal, given the set of goals supplied
	 * @param actions
	 * @param subGoals
	 * 
	 * @return
	 */
	private boolean isMinimalActionSet(Set<Operator> actions, Set<Proposition> subGoals) {
		boolean bRes = true;
		final List<Proposition> achievableGoals = new ArrayList<Proposition>(subGoals.size());
		//Select one action and check if the goals can be achieved without it
		//operatorOut is the action being taken out from the vector of selected
		//actions
		for(Operator operatorOut : actions) {
			//We store the possible goals in this list
			achievableGoals.clear();
			
			for(Operator operator: actions) {
				//We jump the removed operator
				if(operator == operatorOut) {
					continue;
				}
				//And check if the goals are still achievable
				
				//By getting the action's effects
				List<Proposition> effects = (List<Proposition>) operator.getEffects();
				//And trying to add the new ones to the achieved list
				for(Proposition effect: effects) {
					//If the given effect is in the goals being checked for minimality and not
					//in the goals currently in the temporary list, then add it to the temp list
					if(subGoals.contains(effect) && !achievableGoals.contains(effect)) {
						achievableGoals.add(effect);
					}
				}
			}
			//If the goals achievable with the removal of the action "i", are different
			//than the goals achievable otherwise, then the goal list will have different sizes
			bRes = achievableGoals.size() != subGoals.size();
			if(!bRes) {
				return bRes;
			}
		}
		
		return bRes;
	}
	
	/**
	 * Gets the preconditions for the operators 
	 * given as parameter
	 * @param operators
	 * @return
	 */
	private Set<Proposition> determineSubgoals(Set<Operator> operators) {
		final HashSet<Proposition> subGoals = new HashSet<Proposition>();
		
		for (Operator operator : operators) {
			subGoals.addAll(operator.getPreconds());
		}
		
		return subGoals;
	}

	public PlanResult getPlanResult() {
		//TODO make a real implementation of this method
		return planResult;
	}
}
