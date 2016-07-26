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

import graphplan.graph.GraphElement;
import jason.asSemantics.Unifier;

import java.util.List;

/**
 * The interface to a Proposition in the planning graph, this is a bridge
 * pattern between what the planning graph and algorithm expect, and 
 * any type of logic implementation we choose to use.
 * @author Felipe Meneguzzi
 *
 */
@SuppressWarnings("unchecked")
public interface Proposition extends GraphElement {
	/**
	 * Gets the functor for this operator
	 * @return This operator's functor.
	 */
    String getFunctor();
	
	/**
	 * Gets the signature of this operator, which is composed of the
	 * operator's functor and its terms.
	 * @return This operator's signature.
	 */
    String getSignature();
	
	/**
	 * Returns whether or not this operator is ground, i.e. is an action or
	 * an operator template.
	 * @return Whether or not all variables in this operator are instantiated.
	 */
    boolean isGround();
	
	/**
	 * Gets the terms applied to this proposition.
	 * @return This proposition's terms.
	 */
    List getTerms();
	
	/**
	 * Returns whether or not this is a negated proposition.
	 * @return Whether or not this is a negated proposition.
	 */
    boolean negated();
	
	/**
	 * Determines whether or not the supplied proposition is mutually exclusive
	 * of this one. This method only checks for complementarity.
	 * @param proposition The proposition against which mutex relations are to be checked
	 * @return Whether or not the supplied proposition is mutex with this one.
	 */
    boolean isMutex(Proposition proposition);
	
	/**
	 * Tells whether or not this proposition unifies with the supplied proposition.
	 * @param proposition
	 * @return
	 */
    boolean unifies(Proposition proposition);
	
	/**
	 * Applies the specified unifier to this proposition, binding its variables
	 * @param unifier
	 * @return
	 */
    boolean apply(Unifier unifier);
	
	/**
	 * Makes this proposition ground by instantiating its variables as a lower
	 * case representation of the variable names.
	 *
	 */
    Proposition makeGround();
	
	/**
	 * Clones this proposition
	 * @return A deep copy of this operator.
	 */
    Object clone();
	
	void setIndex(int index);
	int getIndex();
}
