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
import graphplan.graph.algorithm.ActionLevelGenerator;
import graphplan.graph.algorithm.MutexGenerator;
import graphplan.graph.algorithm.PropositionLevelGenerator;
import graphplan.graph.algorithm.impl.LevelGeneratorImpl;
import graphplan.graph.algorithm.impl.MutexGeneratorImpl;
import graphplan.graph.draw.TextDrawVisitor;
import graphplan.graph.memo.mutexes.StaticsMutexesTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@SuppressWarnings("unchecked")
public class PlanningGraph implements GraphElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PlanningGraph.class.getName());

	protected List<GraphLevel> graphLevels = null;
	
	protected ActionLevelGenerator actionLevelGenerator = null;
	protected PropositionLevelGenerator propositionLevelGenerator = null;
	protected MutexGenerator mutexGenerator = null;
	
	//The level in which the graph has levelled off
	protected int levelOff = 0;

	public PlanningGraph() {
		this.graphLevels = new ArrayList<GraphLevel>();
		//XXX Right now I'm hard coding the instantiation of the level generators
		//TODO Eventually this should be done through configuration and reflection
		LevelGeneratorImpl levelGenerator = new LevelGeneratorImpl();
		this.actionLevelGenerator = levelGenerator;
		this.propositionLevelGenerator = levelGenerator;
		//And the mutex generator
		this.mutexGenerator = new MutexGeneratorImpl();
	}

	public PlanningGraph(PropositionLevel initialState) {
		this();
		this.addGraphLevel(initialState);
	}

	public PlanningGraph(PropositionLevel initialState, Map<String, Set<String>> types, Map<String, List<String>> parameterTypes, StaticsMutexesTable staticsMutexesTable) {
		this.graphLevels = new ArrayList<GraphLevel>();

		LevelGeneratorImpl levelGenerator = new LevelGeneratorImpl(types, parameterTypes);
		this.actionLevelGenerator = levelGenerator;
		this.propositionLevelGenerator = levelGenerator;
		
		this.mutexGenerator = new MutexGeneratorImpl(staticsMutexesTable);
		this.addGraphLevel(initialState);
	}

	public boolean accept(GraphElementVisitor visitor) {
		/*for (GraphLevel level : graphLevels) {
			visitor.visitGraphLevel(level);
		}*/
		return visitor.visitElement(this);
	}

	public Iterator<GraphElement> iterator() {
		Iterator<GraphElement> iterator = new Iterator<GraphElement>() {
			protected Iterator<GraphElement> elementIterator = null;

			protected Iterator<GraphLevel> levelIterator = graphLevels
					.iterator();

			@SuppressWarnings("unchecked")
			public boolean hasNext() {
				if (elementIterator == null) {
					if (levelIterator.hasNext()) {
						elementIterator = (Iterator<GraphElement>) levelIterator
								.next().iterator();
					}
				}
				if (elementIterator.hasNext()) {
					return true;
				} else {
					if (levelIterator.hasNext()) {
						elementIterator = (Iterator<GraphElement>) levelIterator
								.next().iterator();
						return this.hasNext();
					}
				}
				return false;
			}

			public GraphElement next() {
				return elementIterator.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
		return iterator;
	}

	/**
	 * Returns the number of levels in this graph
	 * 
	 * @return
	 */
	public int size() {
		return graphLevels.size();
	}

	/**
	 * Returns the specified graph level
	 * 
	 * @param iLevel
	 * @return
	 */
	public GraphLevel getGraphLevel(int iLevel) {
		return this.graphLevels.get(iLevel);
	}

	/**
	 * Helper method to get the last GraphLevel.
	 * 
	 * @return The last level in the planning graph.
	 */
	public GraphLevel getLastGraphLevel() {
		if (graphLevels.size() > 0) {
			return graphLevels.get(graphLevels.size() - 1);
		} else {
			return null;
		}
	}

	/**
	 * Adds a new graph level, enforcing the alternation of Proposition and
	 * Action levels.
	 * 
	 * @param graphLevel
	 *            The new level to be added.
	 */
	public boolean addGraphLevel(GraphLevel graphLevel) {
		if ((graphLevels.isEmpty() && graphLevel.isPropositionLevel()) || 
			(this.graphLevels.get(this.graphLevels.size() - 1).isActionLevel() 
					!= graphLevel.isActionLevel())) {
			if(!graphLevels.isEmpty()){
				graphLevel.setPrevLevel(this.getLastGraphLevel());
			}
			
			this.graphLevels.add(graphLevel);
			graphLevel.setIndex(this.graphLevels.size() - 1);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines if the propositions given as a parameter are a possible set of
	 * goals in the specified proposition level.
	 * 
	 * @param goals
	 * @return
	 */
	public boolean goalsPossible(List<Proposition> goals, int level) {
		if (graphLevels.get(level).isPropositionLevel()) {
			PropositionLevel propositionLevel = (PropositionLevel) this.graphLevels.get(level);
			return propositionLevel.goalsPossible(goals);
		} else {
			return false;
		}
	}

	/**
	 * Expands the planning graph by adding one action level and one proposition
	 * level.
	 * 
	 */
	public void expandGraph() throws PlanningGraphException {
		if (getLastGraphLevel().isPropositionLevel()) {
			PropositionLevel lastLevel = (PropositionLevel) getLastGraphLevel();
			//First we create a new action level from the last proposition level
			ActionLevel actionLevel = actionLevelGenerator.createNextActionLevel(lastLevel);
			this.addGraphLevel(actionLevel);
			//Then we add the action mutexes for these actions
			this.mutexGenerator.addActionMutexes(lastLevel, actionLevel);
			//And then add the subsequent proposition level
			PropositionLevel propositionLevel = propositionLevelGenerator.createNextPropositionLevel(actionLevel);
			this.addGraphLevel(propositionLevel);
			//Finally adding the proposition mutexes
			this.mutexGenerator.addPropositionMutexes(actionLevel, propositionLevel);
		} else {
			throw new PlanningGraphException(
					"Last graph level is not proposition.",
					graphLevels.size() - 1);
		}
	}

	/**
	 * Returns whether or not this graph has levelled off according to the
	 * level size criterion. The entire graph levels off when both the levels
	 * have levelled off and the memoization table has levelled off.
	 * @return
	 */
	public boolean levelledOff() {
		//If the graph has already levelled off, then we don't have to check
		//again
		if(levelOff > 0) {
			return true;
		} else {
			final int lastGraphLevel = graphLevels.size() - 1;
			
			if(lastGraphLevel > 3) {
				boolean levelledOff = false;
				
				//If the graph has levelled off, store the index where it 
				//happened to check for the memoization stop condition				
				levelledOff = 
					(graphLevels.get(lastGraphLevel).size() == graphLevels.get(lastGraphLevel-2).size())
				  &&(graphLevels.get(lastGraphLevel).mutexCount() == graphLevels.get(lastGraphLevel-2).mutexCount())
				  &&(graphLevels.get(lastGraphLevel-1).size() == graphLevels.get(lastGraphLevel-3).size())
				  &&(graphLevels.get(lastGraphLevel-1).mutexCount() == graphLevels.get(lastGraphLevel-3).mutexCount());
				
				if(levelledOff) {
					levelOff = lastGraphLevel;
				}
				
				return levelledOff;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * Returns the level at which the graph has levelled off.
	 * @return The level at which the graph has levelled off.
	 */
	public final int levelOffIndex() {
		return levelOff;
	}
	
	public String toString() {
		TextDrawVisitor visitor = new TextDrawVisitor();
		if(this.accept(visitor)) {
			return visitor.toString();
		} else {
			return super.toString();
		}
	}
}
