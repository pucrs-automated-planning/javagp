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

import java.util.List;

/**
 * The interface to an operator in the planning graph, this is a bridge
 * pattern between what the planning graph and algorithm expect, and
 * any type of logic implementation we choose to use.
 *
 * @author Felipe Meneguzzi
 */
@SuppressWarnings("unchecked")
public interface Operator extends GraphElement {

	/**
	 * Gets the functor for this operator
	 *
	 * @return This operator's functor.
	 */
	String getFunctor();

	/**
	 * Gets the signature of this operator, which is composed of the
	 * operator's functor and its terms.
	 *
	 * @return This operator's signature.
	 */
	String getSignature();

	/**
	 * Returns the indicator for this operator, which consists of the
	 * operator functor "/" its arity.
	 *
	 * @return
	 */
	String getOperatorIndicator();

	/**
	 * Returns whether or not this operator is ground, i.e. is an action or
	 * an operator template.
	 *
	 * @return Whether or not all variables in this operator are instantiated.
	 */
	boolean isGround();

	/**
	 * Gets the terms applied to this operator.
	 *
	 * @return This operator's terms.
	 */
	List getTerms();

	/**
	 * Gets the pre-conditions of this operator.
	 *
	 * @return This operator's preconditions.
	 */
	List<Proposition> getPreconds();

	/**
	 * Gets the effects of this operator.
	 *
	 * @return This operator's effects.
	 */
	List<Proposition> getEffects();

	/**
	 * Returns whether or not <code>proposition</code> is a precondition
	 * of this operator.
	 *
	 * @param proposition
	 * @return
	 */
	boolean isPrecond(Proposition proposition);

	/**
	 * Returns whether or not <code>proposition</code> is an effect of
	 * this operator.
	 *
	 * @param proposition
	 * @return
	 */
	boolean isEffect(Proposition proposition);

	/**
	 * Returns whether or not this operator is mutually exclusive to the
	 * supplied operator through static relationships. This method detects
	 * mainly competing proposition needs, inconsistent effects and
	 * interference.
	 *
	 * @param operator
	 * @return
	 */
	boolean isMutex(Operator operator);

	/**
	 * Applies the specified unifier to this proposition, binding its variables
	 *
	 * @param unifier
	 * @return
	 */
	boolean apply(Unifier unifier);

	/**
	 * Tells whether or not this operator is a maintenance operator (Noop).
	 *
	 * @return whether or not this operator is a maintenance operator (Noop)
	 */
	boolean isNoop();

	/**
	 * Clones this operator
	 *
	 * @return A deep copy of this operator.
	 */
	Object clone();

	int getIndex();

	void setIndex(int index);
}
