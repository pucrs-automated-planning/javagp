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
package graphplan.graph;

import graphplan.Graphplan;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ActionLevel implements GraphLevel<Operator> {
	
	private static final long serialVersionUID = -6424912652419709967L;

	private static final Iterator<Operator> emptyIterator = OperatorFactory.getEmptyIterator();
	protected final HashMap<Proposition, List<Operator>> generatingActionsCache;
	protected final List<Operator> actions;
	protected final HashMap<Operator, HashSet<Operator>> mutexes;
	
	protected PropositionLevel nextLevel = null;
	protected PropositionLevel prevLevel = null;
	
	protected int index;
	
	public ActionLevel() {
		//TODO tune the lists and hashtables to improve performance
		this.actions = new ArrayList<Operator>();
		this.mutexes = new HashMap<Operator, HashSet<Operator>>((int)16, (float)0.5);
		this.generatingActionsCache = new HashMap<Proposition, List<Operator>>();
	}
	
	public ActionLevel(int index) {
		this();
		this.setIndex(index);
	}

	public final boolean accept(GraphElementVisitor visitor) {
		return visitor.visitGraphLevel(this);
	}

	public final Iterator<Operator> iterator() {
		return this.getActions();
	}

	/**
	 * Returns the actions in this action level.
	 * @return
	 */
	public final Iterator<Operator> getActions() {
		return actions.iterator();
	}
	
	/**
	 * Returns whether or not the referred action is in this level.
	 * @param operator
	 * @return
	 */
	public final boolean hasAction(Operator operator) {
		return actions.contains(operator);
	}
	
	/**
	 * Adds the supplied actions to this action level.
	 * @param operator
	 */
	public final void addAction(Operator operator) {
		if(!this.actions.contains(operator)) {
			this.actions.add(operator);
			//Clear the cache
			this.generatingActionsCache.clear();
		}
	}
	
	/**
	 * Returns the actions in this level that have the supplied proposition as
	 * a precondition.
	 * 
	 * @param proposition
	 * @return
	 */
	public List<Operator> getRequiringActions(Proposition proposition) {
		List<Operator> requiringActions = new ArrayList<Operator>();
		
		for (Iterator<Operator> iter = actions.iterator(); iter.hasNext();) {
			Operator oper = iter.next();
			if(oper.getPreconds().contains(proposition)) {
				requiringActions.add(oper);
			}
		}
		
		return requiringActions;
	}
	
	/**
	 * Returns the actions in this level that have the supplied proposition as
	 * an effect.
	 * 
	 * @param proposition
	 * @return
	 */
	public List<Operator> getGeneratingActions(Proposition proposition) {
		final List<Operator> generatingActions;
		//A small caching strategy to speedup things
		if(this.generatingActionsCache.containsKey(proposition)) {
			generatingActions = this.generatingActionsCache.get(proposition);
		} else {
			generatingActions = new ArrayList<Operator>();
			
			for (Iterator<Operator> iter = this.actions.iterator(); iter.hasNext();) {
				Operator oper = iter.next();
				if(oper.getEffects().contains(proposition)) {
					generatingActions.add(oper);
				}
			}
			/*Heuristic: select actions that appears latest in the planning graph*/
			if(Graphplan.operatorsLatest)
				this.sortByIndex(generatingActions);
			
			/*Heuristic: select noops first*/
			if(Graphplan.noopsFirst)
				this.sortByNoopsFirst(generatingActions);
			
			this.generatingActionsCache.put(proposition, generatingActions);
		}
		return generatingActions;
	}
	
	/**
	 * Adds a noop action to propagate the supplied proposition to the subsequent
	 * proposition level.
	 * @param proposition
	 */
	public void addNoop(Proposition proposition) {
		Operator noop = OperatorFactory.getInstance().getNoop(proposition);
		this.addAction(noop);
	}
	
	/**
	 * Returns whether or not the two actions are mutually exclusive in this level
	 * @param operator1
	 * @param operator2
	 * @return
	 */
	public final boolean isMutex(Operator operator1, Operator operator2) {
		if(this.mutexes.containsKey(operator1)) {
			return this.mutexes.get(operator1).contains(operator2);
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a mutex link between operator1 and operator2. Currently we create links
	 * in both directions, which may be unnecessary, and should be reviewed.
	 * @param operator1
	 * @param operator2
	 */
	public final void addMutex(Operator operator1, Operator operator2) {
		if(!this.mutexes.containsKey(operator1)) {
			this.mutexes.put(operator1, new HashSet<Operator>());
		}
		
		if(!this.mutexes.containsKey(operator2)) {
			this.mutexes.put(operator2, new HashSet<Operator>());
		}
		
		this.mutexes.get(operator1).add(operator2);
		this.mutexes.get(operator2).add(operator1);
	}
	
	/**
	 * Returns an iterator to all mutexes for the specified operator.
	 * @param operator The operator for which the mutexes are to be returned.
	 * @return An iterator to the mutexes for the specified operator.
	 */
	public final Iterator<Operator> getMutexes(Operator operator) {
		if(this.mutexes.containsKey(operator)) {
			return this.mutexes.get(operator).iterator();
		} else return emptyIterator;
	}
	
	public final boolean isActionLevel() {
		return true;
	}

	public final boolean isPropositionLevel() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	public final GraphLevel getNextLevel() {
		return this.nextLevel;
	}

	@SuppressWarnings("rawtypes")
	public final GraphLevel getPrevLevel() {
		return this.prevLevel;
	}

	public final int size() {
		return this.actions.size();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.actions.toString());
		
		return sb.toString();
	}
	
	public final int getIndex() {
		return this.index;
	}
	
	public final void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @param nextLevel the nextLevel to set
	 */
	public final void setNextLevel(PropositionLevel nextLevel) {
		this.nextLevel = nextLevel;
	}

	/**
	 * @param prevLevel the prevLevel to set
	 */
	@SuppressWarnings("rawtypes")
	public final void setPrevLevel(GraphLevel prevLevel) {
		if(prevLevel.isPropositionLevel()) {
			this.prevLevel = (PropositionLevel) prevLevel;
			this.prevLevel.setNextLevel(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see graphplan.graph.GraphLevel#mutexCount()
	 */
	public int mutexCount() {
		int res = 0;
		for(HashSet<Operator> mutex : mutexes.values()) {
			res+=mutex.size();
		}
		return res;
	}
	
	private void sortByNoopsFirst(List<Operator> operators){
		Collections.sort(operators, new Comparator<Operator>() {
			public int compare(Operator o1, Operator o2) {
				if( o1.isNoop() && !o2.isNoop()) return -1;
				if(!o1.isNoop() &&  o2.isNoop()) return 1;
				return 0;
			}
		});
	}
	
	private void sortByIndex(List<Operator> operators){
		Collections.sort(operators, new Comparator<Operator>() {
			public int compare(Operator o1, Operator o2) {
				return (o1.getIndex() < o2.getIndex() ? -1: (o1.getIndex() == o2.getIndex() ? 0 : 1));
			}
		});
	}

	public HashMap<Operator, HashSet<Operator>> getMutexes() {
		return mutexes;
	}
}
