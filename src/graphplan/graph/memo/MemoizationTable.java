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
package graphplan.graph.memo;

import graphplan.domain.Proposition;
import graphplan.flyweight.PropositionFactory;

import java.util.*;

public class MemoizationTable implements Iterable<HashSet<String>> {
	protected List<HashSet<String>> noGoodsTable;

	//private static int hits = 0;
	//private static int misses = 0;

	public MemoizationTable() {
		this.noGoodsTable = new ArrayList<>();
	}

	public Iterator<HashSet<String>> iterator() {
		return noGoodsTable.iterator();
	}

	/**
	 * Translates the supplied graph level into an index in the
	 * no goods table.
	 *
	 * @param graphLevel
	 * @return
	 */
	private int tableIndex(int graphLevel) {
		return ((graphLevel >> 1) - 1);
	}


	/**
	 * Ensures that the no goods table has at least the specified size
	 *
	 * @param size
	 */
	public final void ensureCapacity(int size) {
		while (noGoodsTable.size() < size) {
			noGoodsTable.add(new HashSet<>());
		}
	}

	/**
	 * Checks whether or
	 *
	 * @param propositions
	 * @param graphLevel
	 * @return
	 */
	public final boolean isNoGood(Set<Proposition> propositions, int graphLevel) {
		//If this is the first subgoal in the list we are trying to get
		//check the no goods
		final String signature = PropositionFactory.getInstance().getGoalsSignature(propositions);
		final HashSet<String> noGoods = noGoodsTable.get(tableIndex(graphLevel));
		//hits++;
//misses++;
		return noGoods.contains(signature);
		//return noGoods.contains(signature);
	}

	/**
	 * Adds a new entry to the table of no goods, denoting that the specified set of
	 * goals is not possible.
	 *
	 * @param propositions
	 * @param graphLevel
	 */
	public final void addNoGood(Set<Proposition> propositions, int graphLevel) {
		//Add to the mutexes
		final String signature = PropositionFactory.getInstance().getGoalsSignature(propositions);
		noGoodsTable.get(tableIndex(graphLevel)).add(signature);
	}


	/**
	 * Finds out if the plan has not been proven impossible to fulfil given
	 * the memoization table. The criterion behind this is that if the graph
	 * has levelled off at some level <em>graphLevel</em> and the number of
	 * memoized no-goods of <em>graphLevel+1</em> equals the number at level
	 * <em>graphLevel</em>, then no plan exists.
	 *
	 * @param graphLevel
	 * @return
	 */
	public final boolean levelledOff(int graphLevel) {
		if (tableIndex(graphLevel) < noGoodsTable.size()) {
			final int tableIndex = tableIndex(graphLevel);
			return noGoodsTable.get(tableIndex).size() != noGoodsTable.get(tableIndex - 1).size();
		} else {
			return false;
		}
	}

	/**
	 * Returns the total size of the no goods table for every graph level.
	 *
	 * @return
	 */
	public final int noGoodTableSize() {
		int res = 0;
		for (HashSet<String> set : noGoodsTable) {
			res += set.size();
		}
		return res;
	}
}
