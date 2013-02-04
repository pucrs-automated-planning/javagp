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
package graphplan.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The description of a planning problem, including the set of ground
 * uninstantiated operators as well as the initial state and goal state.
 * 
 * @author Felipe Meneguzzi
 * 
 */
public class DomainDescription {

	protected List<Operator> operators;

	protected List<Proposition> initialState;

	protected List<Proposition> goalState;
	
	protected Map<String, Set<String>> types;
	
	protected Map<String, Set<String>> parameterTypes;
	
	/**
	 * Instantiate a domain description with the supplied  
	 * <code>initialState</code> and <code>goalState</code>.
	 * @param initialState
	 * @param goalState
	 */
	public DomainDescription(List<Proposition> initialState, List<Proposition> goalState) {
		this.initialState = new ArrayList<Proposition>(initialState);
		this.goalState = new ArrayList<Proposition>(goalState);
	}

	/**
	 * Instantiate a domain description with the supplied <code>operators</code>, 
	 * <code>initialState</code> and <code>goalState</code>.
	 * @param operators
	 * @param initialState
	 * @param goalState
	 */
	public DomainDescription(List<Operator> operators,
			List<Proposition> initialState, List<Proposition> goalState) {
		this(initialState, goalState);
		this.operators = new ArrayList<Operator>(operators);
	}
	
	/**
	 * Instantiate a domain description with the supplied <code>operators</code>, 
	 * <code>initialState</code> and <code>goalState</code>.
	 * @param operators
	 * @param initialState
	 * @param goalState
	 * @param types
	 * @param parametertypes
	 */
	public DomainDescription(List<Operator> operators,
			List<Proposition> initialState, List<Proposition> goalState, Map<String, Set<String>> types, Map<String, Set<String>> parameterTypes) {
		this(initialState, goalState);
		this.operators = new ArrayList<Operator>(operators);
		this.types = types;
		this.parameterTypes = parameterTypes;
	}

	/**
	 * Returns the operators for this domain.
	 * 
	 * @return
	 */
	public List<Operator> getOperators() {
		return this.operators;
	}

	/**
	 * Returns the initial state of this domain.
	 * 
	 * @return
	 */
	public List<Proposition> getInitialState() {
		return this.initialState;
	}

	/**
	 * Returns the goal state of this domain.
	 * 
	 * @return
	 */
	public List<Proposition> getGoalState() {
		return this.goalState;
	}
	
	/**
	 * Sets the operators in a planning problem.
	 * @param operators
	 */
	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}
}
