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

public interface GraphLevel<T> extends GraphElement<T> {
		
	/**
	 * Returns whether or not this is a proposition graph level.
	 * @return
	 */
    boolean isPropositionLevel();
	
	/**
	 * Returns whether or not this is an action graph level.
	 * @return
	 */
    boolean isActionLevel();
	
	/**
	 * Returns the next level in the graph
	 * @return
	 */
    GraphLevel getNextLevel();
	
	/**
	 * Returns the previous level in the graph
	 * @return
	 */
    GraphLevel getPrevLevel();
	
	/**
	 * Sets the previous graph level to the specified level,
	 * as well as setting the right forward reference in
	 * the supplied level.
	 * 
	 * @param graphLevel
	 */
    void setPrevLevel(GraphLevel graphLevel);
	
	/**
	 * Returns the number of GraphElements in this Level.
	 * @return
	 */
    int size();
	
	/**
	 * Returns the index of this graph level.
	 * @return
	 */
    int getIndex();
	
	/**
	 * Sets the index for this graph level.
	 *
	 */
    void setIndex(int index);
	
	/**
	 * Returns the number of mutex relations stored in this graph level
	 * @return The number of mutex relations stored in this graph level
	 */
    int mutexCount();
}
