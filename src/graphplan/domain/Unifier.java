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

public interface Unifier {
	/**
	 * Tries to unify proposition1 and proposition2, returning whether or not
	 * the two can be unified, and storing the unification in this object.
	 *
	 * @param proposition1
	 * @param proposition2
	 * @return
	 */
	boolean unifies(Proposition proposition1, Proposition proposition2);

	/**
	 * Tells whether or not this unifier is empty in that it does not
	 * have any variable assignments to be made.
	 *
	 * @return
	 */
	boolean isEmpty();
}
