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
package graphplan.domain.jason;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.Unifier;
import graphplan.flyweight.OperatorFactory;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public class OperatorImpl extends Structure implements Operator {
	public static final Iterator<GraphElement> emptyIterator = new Iterator<GraphElement>() {
		public final boolean hasNext() {
			return false;
		}

		public final GraphElement next() {
			return null;
		}

		public void remove() {
		}
	};
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected final List<Proposition> preconds;
	protected final List<Proposition> effects;
	private int index = -1;

	public OperatorImpl(String declaration) {
		super(Structure.parse(declaration));
		this.preconds = new ArrayList<>();
		this.effects = new ArrayList<>();
	}

	public OperatorImpl(Structure functor, List<Proposition> preconds, List<Proposition> effects) {
		super(functor);
		this.preconds = new ArrayList<>();
		this.effects = new ArrayList<>();
		this.addPreconds(preconds);
		this.addEffects(effects);
	}

	public OperatorImpl(Operator operator) {
		this((Structure) operator, operator.getPreconds(), operator.getEffects());
	}

	/**
	 * Adds preconditions to this operator while checking for variable consistency.
	 *
	 * @param preconds The preconditions to be added to this operator.
	 */
	protected void addPreconds(List<Proposition> preconds) {
		for (Proposition proposition : preconds) {
			if (checkVariables(proposition)) {
				this.preconds.add(proposition);
			}
		}
	}

	/**
	 * Adds effects to this operator while checking for variable consistency.
	 *
	 * @param effects The effects to be added to this operator.
	 */
	protected void addEffects(List<Proposition> effects) {
		for (Proposition proposition : effects) {
			if (checkVariables(proposition)) {
				this.effects.add(proposition);
			}
		}
	}

	/**
	 * Check whether or not the variables in this proposition match those
	 * in this operator.
	 *
	 * @param proposition
	 * @return Whether the variables in the proposition match those of this operator's header
	 */
	private boolean checkVariables(Proposition proposition) {
		//TODO no checking is being made
		return true;
	}

	public final List<Proposition> getEffects() {
		return this.effects;
	}

	public final List<Proposition> getPreconds() {
		return this.preconds;
	}

	public final String getSignature() {
		return super.toString();
	}

	public final String getOperatorIndicator() {
		return super.getPredicateIndicator().toString();
	}

	public final boolean accept(GraphElementVisitor visitor) {
		// TODO Auto-generated method stub
		return false;
	}

	public Iterator<? extends GraphElement> iterator() {
		return emptyIterator;
	}

	public final boolean isEffect(Proposition proposition) {
		return effects.contains(proposition);
	}

	public final boolean isPrecond(Proposition proposition) {
		return preconds.contains(proposition);
	}

	public final boolean isMutex(Operator operator) {
		//First, we verify if any of this Operator's preconditions is mutex
		//with any precondition or effect in the target Operator

		//If any mutex is found, we can return immediately
		for (Proposition proposition : preconds) {
			for (Proposition precond : operator.getPreconds()) {
				if (proposition.isMutex(precond)) {
					return true;
				}
			}

			for (Proposition effect : operator.getEffects()) {
				if (proposition.isMutex(effect)) {
					return true;
				}
			}
		}

		//Verify if any of this Operator's effetcs is mutex with any
		//precondition or effect in the target Operator
		//Again, if we bump into any mutex, we can return immediately
		for (Proposition proposition : effects) {
			for (Proposition precond : operator.getPreconds()) {
				if (proposition.isMutex(precond)) {
					return true;
				}
			}

			for (Proposition effect : operator.getEffects()) {
				if (proposition.isMutex(effect)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean apply(jason.asSemantics.Unifier u) {
		boolean r = false;
		boolean tr = super.apply(u);
		r = r || tr;
		//XXX We are also having to apply the unifier throughout the
		//XXX preconds and effects, rather than merging variables in the
		//XXX operator globally
		List<Proposition> temp = this.preconds;
		for (Proposition aTemp1 : temp) {
			tr = aTemp1.apply(u);
			r = r || tr;
		}

		temp = this.effects;
		for (Proposition aTemp : temp) {
			tr = aTemp.apply(u);
			r = r || tr;
		}
		return r;
	}

	public boolean apply(Unifier unifier) {
		//XXX Hack to fix the problem of an empty unifier's application returning empty
		if (unifier.isEmpty()) {
			return true;
		}
		//Since this implementation relies on a Jason binding, we have to force our unifier
		//to be Jason's
		if (unifier instanceof jason.asSemantics.Unifier) {
			return this.apply((jason.asSemantics.Unifier) unifier);
		} else {
			return false;
		}
	}

	public Term clone() {
		OperatorImpl newOp = new OperatorImpl(this);

		newOp.preconds.clear();
		newOp.effects.clear();

		for (Proposition prop : preconds) {
			newOp.preconds.add((Proposition) prop.clone());
		}

		for (Proposition prop : effects) {
			newOp.effects.add((Proposition) prop.clone());
		}

		return newOp;
	}

	public final boolean isNoop() {
		return this.getFunctor().startsWith(OperatorFactory.NOOP_FUNCTOR);
	}

	@Override
	public int getIndex() {
		return this.index;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}
}
