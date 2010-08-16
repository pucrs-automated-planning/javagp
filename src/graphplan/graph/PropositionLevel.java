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

import graphplan.domain.Proposition;
import graphplan.flyweight.PropositionFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author Felipe Meneguzzi
 *
 */
public class PropositionLevel implements GraphLevel<Proposition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Iterator<Proposition> emptyIterator = PropositionFactory.getEmptyIterator();
	
	protected List<Proposition> propositions;
	protected HashMap<Proposition, HashSet<Proposition>> mutexes;
	
	protected ActionLevel nextLevel;
	protected ActionLevel prevLevel;
	
	protected int index;
	
	public PropositionLevel() {
		//TODO tune the lists and hashtables to improve performance
		this.propositions = new ArrayList<Proposition>();
		this.mutexes = new HashMap<Proposition, HashSet<Proposition>>();
	}
	
	public PropositionLevel(int index) {
		this();
		this.setIndex(index);
	}

	public final boolean accept(GraphElementVisitor visitor) {
		return visitor.visitGraphLevel(this);
	}

	public final Iterator<Proposition> iterator() {
		return this.getPropositions();
	}
	
	/**
	 * Returns the propositions in this proposition level.
	 * @return
	 */
	public final Iterator<Proposition> getPropositions() {
		return this.propositions.iterator();
	}
	
	/**
	 * Returns whether or not the referred proposition is in this level
	 * @param proposition
	 * @return
	 */
	public final boolean hasProposition(Proposition proposition) {
		return propositions.contains(proposition);
	}
	
	/**
	 * Adds the supplied proposition to this proposition level.
	 * @param proposition
	 */
	public final void addProposition(Proposition proposition) {
		if(!this.propositions.contains(proposition)) {
			this.propositions.add(proposition);
		}
	}
	
	/**
	 * Adds a list of propositions to an action level
	 * @param propositions
	 */
	public void addPropositions(List<Proposition> propositions) {
		for (Iterator<Proposition> iter = propositions.iterator(); iter.hasNext();) {
			Proposition proposition = iter.next();
			this.addProposition(proposition);
		}
	}
	
	/**
	 * Returns whether or not the two propositions are mutually exclusive in this level
	 * @param proposition1
	 * @param proposition2
	 * @return
	 */
	public final boolean isMutex(Proposition proposition1, Proposition proposition2) {
		if(this.mutexes.containsKey(proposition1)) {
			return this.mutexes.get(proposition1).contains(proposition2);
		} else {
			return false;
		}
	}
	
	/**
	 * Adds a mutex link between proposition1 and proposition2. Currently we are creating
	 * links in both directions, which may be unnecessary, this should be checked.
	 * 
	 * TODO Check if it is really necessary to keep links both ways in the mutex relation.
	 * @param proposition1
	 * @param proposition2
	 */
	public final void addMutex(Proposition proposition1, Proposition proposition2) {
		if(!this.mutexes.containsKey(proposition1)) {
			this.mutexes.put(proposition1, new HashSet<Proposition>());
		}
		if(!this.mutexes.containsKey(proposition2)) {
			this.mutexes.put(proposition2, new HashSet<Proposition>());
		}
		this.mutexes.get(proposition1).add(proposition2);
		this.mutexes.get(proposition2).add(proposition1);
	}
	
	/**
	 * Returns an iterator to all mutexes for the specified proposition.
	 * @param proposition The proposition for which the mutexes are to be returned.
	 * @return An iterator to the mutexes for the specified proposition.
	 */
	public final Iterator<Proposition> getMutexes(Proposition proposition) {
		if(this.mutexes.containsKey(proposition)) {
			return this.mutexes.get(proposition).iterator();
		} else {
			return emptyIterator;
		}
	}

	/**
	 * Determines if the propositions given as a parameter are a possible
	 * set of goals in this proposition level.
	 * 
	 * @param goals The goals to check for.
	 * @return Wheter or not the specified goals are possible in this level
	 */
	public boolean goalsPossible(Collection<Proposition> goals) {
		//Essentially search the level for every goal given as parameter,
		for (Proposition goal : goals) {
			if(!this.propositions.contains(goal)) {
				return false;
			}
		}
		//and then match them with the others to see if they are mutex
		for(Proposition goal : goals) {
			if(this.mutexes.containsKey(goal)) {
				for(Proposition goal2 : goals) {
					if(goal2 == goal)
						break;
					if(this.isMutex(goal, goal2)) {
						return false;
					}
				}
			}
		}
		/*for (int i = 0; i < goals.size(); i++) {
			Proposition goal = goals.get(i);
			//No need to check for each other goal if this one is not in 
			//the mutex table
			if(this.mutexes.containsKey(goal)) {
				for(int j = i+1; j < goals.size(); j++) {
					Proposition goal2 = goals.get(j);
					if(this.isMutex(goal, goal2)) {
						return false;
					}
				}
			}
		}*/
		return true;
	}
	
	public final boolean isActionLevel() {
		return false;
	}

	public final boolean isPropositionLevel() {
		return true;
	}
	
	public final GraphLevel getNextLevel() {
		return this.nextLevel;
	}

	public final GraphLevel getPrevLevel() {
		return this.prevLevel;
	}
	
	public final int getIndex() {
		return this.index;
	}

	public final void setIndex(int index) {
		this.index = index;
	}

	public final int size() {
		return this.propositions.size();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.propositions.toString());
		
		return sb.toString();
	}

	/**
	 * @param nextLevel the nextLevel to set
	 */
	public final void setNextLevel(ActionLevel nextLevel) {
		this.nextLevel = nextLevel;
	}

	/**
	 * @param prevLevel the prevLevel to set
	 */
	public final void setPrevLevel(GraphLevel prevLevel) {
		if(prevLevel.isActionLevel()) {
			this.prevLevel = (ActionLevel) prevLevel;
			this.prevLevel.setNextLevel(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see graphplan.graph.GraphLevel#mutexCount()
	 */
	public int mutexCount() {
		int res = 0;
		for(HashSet<Proposition> mutex : mutexes.values()) {
			res+=mutex.size();
		}
		return res;
	}
}
