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

import graphplan.domain.Operator;
import graphplan.util.SetUtil;

import java.util.*;

/**
 * A class encapsulating the result of planning. This class allows direct 
 * comparison with a boolean value determining whether or not the plan was 
 * successful, as well as iteration through the plan steps.
 * @author Felipe Meneguzzi
 *
 */
public class PlanResult implements Iterable<Operator> {
	//The steps are a list of list, since we can have multiple operators at any given step
	private List<List<Operator>> steps = new ArrayList<>();
	private int planLength;

	// Contains all possible sequence of steps from this plan
	private Set<List<Operator>> allPossibleSolutions;
	
	/**
	 * Creates a new PlanResult from a stack of operators in 
	 * reverse order.
	 */
	public PlanResult(Stack<Set<Operator>> stack) {
		planLength = 0;
		for (Set<Operator> aStack : stack) {
			List<Operator> mySteps = new ArrayList<>(aStack.size());
			for (Operator step : aStack) {
				if (!step.isNoop()) {
					mySteps.add(step);
					planLength++;
				}
			}
			this.steps.add(mySteps);
		}
	}

	public int getPlanLength() {
		return planLength;
	}

	public Iterator<Operator> iterator() {
		if(steps == null) {
			return null;
		}
		return new Iterator<Operator>() {
			private Iterator<Operator> stepIterator = null;
			private Iterator<List<Operator>> listIterator = PlanResult.this.steps.iterator();

			public boolean hasNext() {
				if(stepIterator == null) {
					if(listIterator.hasNext()) {
						stepIterator = listIterator.next().iterator();
						return this.hasNext();
					} else {
						return false;
					}
				} else {
					if(stepIterator.hasNext()) {
						return true;
					} else {
						stepIterator = null;
						return this.hasNext();
					}
				}
			}

			public Operator next() {
				if(this.hasNext()) {
					return stepIterator.next();
				} else {
					return null;
				}
			}

			public void remove() {
			}

		};
	}

	// Get all possible sequence of steps from this plan
	public Set<List<Operator>> getAllPossibleSolutions() {
		if(allPossibleSolutions == null) {
			allPossibleSolutions = new HashSet<>();
			for (List<Operator> s : steps) {
				List<List<Operator>> permutations = SetUtil.permutation(s);

				Set<List<Operator>> tempSolutions = new HashSet<>();

				if (allPossibleSolutions.isEmpty()) {
					tempSolutions.addAll(permutations);
				} else {
					for (List<Operator> list : allPossibleSolutions) {
						for(List<Operator> per:permutations) {
							List<Operator> newList = new ArrayList<>(list);
							newList.addAll(per);
							tempSolutions.add(newList);
						}
					}
				}

				allPossibleSolutions = tempSolutions;
			}
		}
		return allPossibleSolutions;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		for (Operator step : this) {
			builder.append(step);
			builder.append(System.getProperty("line.separator"));
		}
		
		return builder.toString();
	}
}
