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
package graphplan.flyweight;

import graphplan.domain.Proposition;
import graphplan.domain.jason.PropositionImpl;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * A factory class to be used in the creation and maintenance of
 * propositions for our planning system using the flyweight pattern.
 * XXX It may have to be modified if multiple instances of the planner are to be used 
 * @author Felipe Meneguzzi
 *
 */
public class PropositionFactory {
	private static PropositionFactory propositionFactory = null;
	
	/**
	 * Returns the singleton <code>PropositionFactory</code> instance.
	 * @return
	 */
	public static PropositionFactory getInstance() {
		if(propositionFactory == null) {
			propositionFactory = new PropositionFactory();
		}
		
		return propositionFactory;
	}
	
	public static void reset() {
		propositionFactory.resetPropositionFactory();
	}
	
	private Hashtable<String, Proposition> propositionInstances;
	
	private PropositionFactory() {
		this.propositionInstances = new Hashtable<>();
	}
	
	/**
	 * Returns a lightweight proposition.
	 * @param propositionSignature
	 * @return
	 */
	public final Proposition getProposition(String propositionSignature) {
		if(!propositionInstances.containsKey(propositionSignature)) {
			PropositionImpl proposition = new PropositionImpl(propositionSignature);
			propositionInstances.put(propositionSignature, proposition);
			return proposition;
		}
		return propositionInstances.get(propositionSignature);
	}
	
	/**
	 * Helper method for instantiating multiple propositions, mainly used for testing.
	 * @param propositionSignatures
	 * @return
	 */
	public Proposition []getPropositions(String []propositionSignatures) {
		Proposition propositions[] = new Proposition[propositionSignatures.length];
		
		for (int i = 0; i < propositionSignatures.length; i++) {
			propositions[i] = getProposition(propositionSignatures[i]);
		}
		
		return propositions;
	}
	
	/**
	 * Resets the proposition factory so that it is detached from all flyweight
	 * propositions created so far. Invoke this method with caution.
	 *
	 */
	protected final void resetPropositionFactory() {
		this.propositionInstances.clear();
	}
	
	/**
	 * Returns a unique signature for any list of propositions.
	 * @param propositions
	 * @return
	 */
	public String getGoalsSignature(Set<Proposition> propositions) {
		final StringBuilder builder = new StringBuilder();
		for(Proposition prop : propositions) {
			builder.append(prop.hashCode());
		}
		
		return builder.toString();
	}
	
	/**
	 * A utility function that returns an empty proposition iterator
	 * @return
	 */
	public static Iterator<Proposition> getEmptyIterator() {

		return new Iterator<Proposition>() {

			public boolean hasNext() {return false;}

			public Proposition next() {return null;}

			public void remove() {}

		};
	}
}
