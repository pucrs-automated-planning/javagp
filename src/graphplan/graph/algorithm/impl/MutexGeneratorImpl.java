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
package graphplan.graph.algorithm.impl;

import java.util.Iterator;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.ActionLevel;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.MutexGenerator;

public class MutexGeneratorImpl implements MutexGenerator {

	public void addActionMutexes(PropositionLevel previousLevel,
			ActionLevel actionLevel) {
		//For every action in this level
		for(Iterator<Operator> i=actionLevel.iterator(); i.hasNext();) {
			//Check every other operator for static mutexes
			Operator operator1 = i.next();
			for(Iterator<Operator> j=actionLevel.iterator(); j.hasNext(); ) {
				Operator operator2 = j.next();
				//for nested iterators, we check everything before the current
				//node, so if we hit the same operator twice, break
				if(operator1 == operator2) {
					break;
				}
				//Old way of generating mutexes
				/*if(operator1.isMutex(operator2)) {
					actionLevel.addMutex(operator1, operator2);
				}*/
				if(isMutex(operator1, operator2, previousLevel)) {
					actionLevel.addMutex(operator1, operator2);
				}
			}
		}
	}
	
	public void addPropositionMutexes(ActionLevel previousLevel,
			PropositionLevel propositionLevel) {
		for (Proposition proposition1 : propositionLevel) {
			for (Proposition proposition2 : propositionLevel) {
				//for nested iterators, we check everything before the current
				//node, so if we hit the same proposition twice, break
				if(proposition1 == proposition2) {
					break;
				}
				//Old way of generating mutexes
				/*if(proposition1.isMutex(proposition2)) {
					propositionLevel.addMutex(proposition1, proposition2);
				}*/
				if(isMutex(proposition1, proposition2, previousLevel)) {
					propositionLevel.addMutex(proposition1, proposition2);
				}
			}
		}
	}
	
	/**
	 * A helper method to verify mutex relations using not only static 
	 * relations, but also checking for dynamic ones from the previous level.
	 * @param operator1
	 * @param operator2
	 * @param previousLevel
	 * @return
	 */
	public boolean isMutex(Operator operator1, Operator operator2, PropositionLevel previousLevel) {
		//First, we verify if any of operator1 preconditions is mutex 
		//with any precondition or effect in operator2
		
		//If any mutex is found, we can return immediately
		for(Proposition proposition : operator1.getPreconds()) {
			for(Proposition precond : operator2.getPreconds()) {
				if(previousLevel.isMutex(proposition, precond)
				   //|| proposition.isMutex(precond) //this second test seems unnecessary
					) {
					return true;
				}
			}
			
			for(Proposition effect : operator2.getEffects()) {
				if(proposition.isMutex(effect)) {
					return true;
				}
			}
		}
		
		//Verify if any of this Operator's effetcs is mutex with any 
		//precondition or effect in the target Operator
		//Again, if we bump into any mutex, we can return immediately
		for(Proposition proposition : operator1.getEffects()) {
			for(Proposition precond : operator2.getPreconds()) {
				if(proposition.isMutex(precond)) {
					return true;
				}
			}
			
			for(Proposition effect : operator1.getEffects()) {
				if(proposition.isMutex(effect)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * A helper method to verify mutex relations using not only static 
	 * relations, but also checking for dynamic ones from the previous level.
	 * @param proposition1
	 * @param proposition2
	 * @param previousLevel
	 * @return
	 */
	public boolean isMutex(Proposition proposition1, Proposition proposition2, ActionLevel previousLevel) {
		//First check for the static mutex
		if(proposition1.isMutex(proposition2)) {
			return true;
		}
		//Otherwise, check for inconsistent support in the previous level
		//This is done by checking all combinations of operators in the
		// previous level for mutexes
		//If there is at least one pair of operators that can be used to 
		// generate proposition1 and proposition2 at the same time they do not 
		// have inconsistent 
		for(Operator operator1 : previousLevel.getGeneratingActions(proposition1)) {
			for(Operator operator2 : previousLevel.getGeneratingActions(proposition2)) {
				if(!previousLevel.isMutex(operator1, operator2)) {
					return false;
				}
			}
		}
		return true;
	}

}
