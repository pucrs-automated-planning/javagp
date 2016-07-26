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

import graphplan.domain.Proposition;
import graphplan.domain.Unifier;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;

import java.util.Iterator;

public class PropositionImpl extends LiteralImpl implements Proposition {
	public static final Iterator<GraphElement> emptyIterator = new Iterator<GraphElement>() {
		public boolean hasNext() {
			return false;
		}

		public GraphElement next() {
			return null;
		}

		public void remove() {
		}
	};

	private String signatureCache = null;
	private int index = -1;

	public PropositionImpl(boolean pos, String functor) {
		super(pos, functor);
	}

	public PropositionImpl(Literal literal) {
		super(literal);
	}

	public PropositionImpl(Proposition proposition) {
		this((Literal) proposition);
	}

	public PropositionImpl(String prop) {
		super(Literal.parseLiteral(prop));
	}

	public String getSignature() {
		if (this.signatureCache == null) {
			StringBuilder s = new StringBuilder();
			if (this.getFunctor() != null) {
				s.append(this.getFunctor());
			}
			if (this.getTerms() != null) {
				s.append("(");
				Iterator<Term> i = this.getTerms().iterator();
				while (i.hasNext()) {
					s.append(i.next());
					if (i.hasNext())
						s.append(",");
				}
				s.append(")");
			}
			this.signatureCache = s.toString();
		}

		return signatureCache;
	}

	public boolean isMutex(Proposition proposition) {
		return (proposition.getSignature().equals(this.getSignature()) &&
				proposition.negated() != this.negated());
	}

	public boolean accept(GraphElementVisitor visitor) {
		return true;
	}

	public Iterator<? extends GraphElement> iterator() {
		return emptyIterator;
	}

	public boolean unifies(Proposition proposition) {
		Unifier un = new UnifierImpl();
		return un.unifies(this, proposition);
	}

	public boolean apply(Unifier unifier) {
		//Since this implementation relies on a Jason binding, we have to force our unifier
		//to be Jason's
		if (unifier instanceof jason.asSemantics.Unifier) {
			this.signatureCache = null;
			return this.apply((jason.asSemantics.Unifier) unifier);
		} else {
			return false;
		}
	}

	public Term clone() {
		return new PropositionImpl((Literal) this);
	}

	public Proposition makeGround() {
		PropositionImpl ground = (PropositionImpl) this.clone();
		for (Term term : ground.getTerms()) {
			if (term.isVar() && !term.isGround()) {
				Term t = new Atom(term.toString().toLowerCase());
				jason.asSemantics.Unifier un = new jason.asSemantics.Unifier();
				if (un.unifies(t, term)) {
					term.apply(un);
				}
			}
		}
		return ground;
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
