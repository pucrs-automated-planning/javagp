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
package graphplan.graph.algorithm;

import graphplan.domain.Proposition;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphLevel;

import java.util.List;

/**
 * A SolutionExtraction wrapper that interrupts computation when a timeout 
 * occurs.
 * 
 * TODO Many concurrency problems are bound to happen if this is used 
 *      in a threaded environment.
 * 
 * @author Felipe Meneguzzi
 *
 */
public class TimeoutSolutionExtractionVisitor extends SolutionExtractionVisitor {
	
	protected long timeoutTime = 0;
	
	private long targetTime = 0;
	private boolean timedOut = false;

	public TimeoutSolutionExtractionVisitor(List<Proposition> goals) {
		super(goals);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visitElement(GraphElement element) {
		//First we must check if timeout has been reached
		if(System.currentTimeMillis() > targetTime) {
			//If the timeout has been reached, we set the appropriate
			//flags and return false
			timedOut = true;
			targetTime = 0;
			return false;
		} else {
			//Otherwise, we carry on with the computation
			return super.visitElement(element);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visitGraphLevel(GraphLevel graphLevel) {
		//First we check if the timeout has been reached
		if(System.currentTimeMillis() > targetTime) {
			//If the timeout has been reached, we set the appropriate
			//flags and return false
			timedOut = true;
			targetTime = 0;
			return false;
		} else {
			//Otherwise, we carry on with the computation
			return super.visitGraphLevel(graphLevel);
		}
	}
	
	/**
	 * Returns whether or not the last computation terminated due to a timeout
	 * @return
	 */
	public boolean timedOut() {
		return timedOut;
	}
	
	/**
	 * Sets the timeout value for invocations of visitElement
	 * @param timeout
	 */
	public void setTimeout(long timeout) {
		this.timeoutTime = timeout;
		//When this method is invoked, we calculate the target time
		this.targetTime = System.currentTimeMillis() + timeoutTime;
		timedOut = false;
	}

	/**
	 * @return the timeoutTime
	 */
	public long getTimeoutTime() {
		return timeoutTime;
	}
	
	
}
