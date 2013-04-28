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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * A class encapsulating the result of planning. This class allows direct 
 * comparison with a boolean value determining whether or not the plan was 
 * successful, as well as iteration through the plan steps.
 * @author Felipe Meneguzzi
 *
 */
public class PlanResult implements Comparable<Boolean>, Iterable<Operator> {
	protected boolean value = false;
	//The steps are a list of list, since we can have multiple operators
	//at any given step
	protected List<List<Operator>> steps;
	
	public PlanResult(boolean value) {
		this.value = value;
		if(value) {
			this.steps = new ArrayList<List<Operator>>();
		}
	}
	
	/**
	 * Creates a new PlanResult from a stack of operators in 
	 * reverse order.
	 */
	public PlanResult(Stack<Set<Operator>> stack) {
		this(true);
		for(int i = 0; i<stack.size(); i++) {
			Collection<Operator> stepsInStack = stack.get(i);
			List<Operator> mySteps = new ArrayList<Operator>(stepsInStack.size());
			for(Operator step : stepsInStack) {
				if(!step.isNoop()) {
					mySteps.add(step);
				}
			}
			this.steps.add(mySteps);
		}
	}
	
	/**
	 * Creates a new plan with the specified steps. This assumes that planning 
	 * was successful
	 * @param steps
	 */
	public PlanResult(List<Operator> steps) {
		this(true);
		this.steps = new ArrayList<List<Operator>>(steps.size());
		this.addSteps(steps);
	}
	
	/**
	 * Adds a step to this plan.
	 * @param step
	 */
	public void addStep(Operator step) {
		this.steps.add(new ArrayList<Operator>());
		this.steps.get(this.steps.size()-1).add(step);
	}
	
	/**
	 * Adds a step in the specified time
	 * @param time
	 * @param step
	 */
	public void addStep(int time, Operator step) {
		if(this.steps.size() <= time) {
			this.steps.add(time, new ArrayList<Operator>());
		}
		this.steps.get(time).add(step);
	}
	
	/**
	 * Adds all the steps in the list of operators to this plan, one for
	 * each unit of time.
	 * @param list
	 */
	public void addSteps(List<Operator> list) {
		for(Operator step : list) {
			this.addStep(step);
		}
	}

	public int compareTo(Boolean b) {
		return (b.booleanValue() == value ? 0 : (value ? 1 : -1));
	}

	public Iterator<Operator> iterator() {
		if(steps == null) {
			return null;
		}
		Iterator<Operator> iterator = new Iterator<Operator>() {
			private Iterator<Operator> stepIterator = null;
			private Iterator<List<Operator>> listIterator = steps.iterator();
			
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
				// TODO Auto-generated method stub				
			}
			
		};
		return iterator;
	}
	
	public boolean isTrue() {
		return value;
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
