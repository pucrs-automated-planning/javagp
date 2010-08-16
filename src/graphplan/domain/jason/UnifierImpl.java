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
import jason.asSemantics.Unifier;

/**
 * A Jason-based adapter implementing the graphplan.domain.Unifier bridge pattern.
 * @author Felipe Meneguzzi
 *
 */
public class UnifierImpl extends Unifier implements graphplan.domain.Unifier {

	public boolean unifies(Proposition proposition1, Proposition proposition2) {
		if(proposition1 instanceof PropositionImpl &&
				proposition2 instanceof PropositionImpl) {
			return this.unifies((PropositionImpl)proposition1, (PropositionImpl)proposition2);
		} else {
			return false;
		}
	}

	protected boolean unifies(PropositionImpl prop1, PropositionImpl prop2) {
		return super.unifies(prop1, prop2);
	}
	
	public boolean isEmpty() {
		return this.toString().equals("{}");
	}
}
