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
package graphplan.graph.draw;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.*;
import graphplan.graph.planning.PlanningGraph;

import java.util.Iterator;

public class TextDrawVisitor implements GraphElementVisitor {
	
	protected StringBuffer sbOutput = null;
	
	public TextDrawVisitor() {
		this.sbOutput = new StringBuffer();
	}

	@SuppressWarnings("unchecked")
	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			for (int i=0; i<planningGraph.size(); i++) {
				this.visitGraphLevel(planningGraph.getGraphLevel(i));
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see graphplan.graph.GraphElementVisitor#visitGraphLevel(graphplan.graph.GraphLevel)
	 */
	@SuppressWarnings("unchecked")
	public boolean visitGraphLevel(GraphLevel graphLevel) {
		if(graphLevel.isPropositionLevel()) {
			this.visitPropositionLevel((PropositionLevel) graphLevel);
		} else {
			this.visitActionLevel((ActionLevel) graphLevel);
		}
		
		return true;
	}
	
	/**
	 * Visits an action level to draw its elements.
	 * @param actionLevel
	 * @return
	 */
	public boolean visitActionLevel(ActionLevel actionLevel) {
		StringBuilder sbMutexes = new StringBuilder();
		
		sbOutput.append(" Action Level [");
		for(Iterator<Operator> iter = actionLevel.getActions(); iter.hasNext(); ) {
			Operator operator = iter.next();
			sbOutput.append(operator.toString());
			if(iter.hasNext()) {
				sbOutput.append(", ");
			}
			sbMutexes.append("   "+operator.toString()+": ");
			for(Iterator<Operator> j = actionLevel.getMutexes(operator); j.hasNext(); ) {
				Operator mutex = j.next();
				sbMutexes.append(mutex.toString());
				if(j.hasNext()) {
					sbMutexes.append(", ");
				}
			}
			sbMutexes.append(System.getProperty("line.separator"));
		}
		sbOutput.append("]");
		sbOutput.append(System.getProperty("line.separator"));
		
		sbOutput.append(" - Mutexes: "+System.getProperty("line.separator"));
		sbOutput.append(sbMutexes.toString());
		//sbOutput.append(System.getProperty("line.separator"));
		return true;
	}
	
	public boolean visitPropositionLevel(PropositionLevel propositionLevel) {
		StringBuilder sbMutexes = new StringBuilder();
		
		sbOutput.append("Proposition Level [");
		for (Iterator<Proposition> iter = propositionLevel.getPropositions(); iter.hasNext();) {
			Proposition proposition = iter.next();
			sbOutput.append(proposition.toString());
			if(iter.hasNext()) {
				sbOutput.append(", ");
			}
			
			sbMutexes.append("   "+proposition.toString()+": ");
			for(Iterator<Proposition> j = propositionLevel.getMutexes(proposition); j.hasNext(); ) {
				Proposition mutex = j.next();
				sbMutexes.append(mutex.toString());
				if(j.hasNext()) {
					sbMutexes.append(", ");
				}
			}
			sbMutexes.append(System.getProperty("line.separator"));
		}
		sbOutput.append("]");
		sbOutput.append(System.getProperty("line.separator"));
		
		sbOutput.append(" - Mutexes: "+System.getProperty("line.separator"));
		sbOutput.append(sbMutexes.toString());
		//sbOutput.append(System.getProperty("line.separator"));
		return true;
	}
	
	/**
	 * Clears the graph drawing created so far, allowing this visitor
	 * to be reused.
	 */
	public void reset() {
		this.sbOutput.delete(0, sbOutput.length());
	}

	public String toString() {
		return this.sbOutput.toString();
	}
}
