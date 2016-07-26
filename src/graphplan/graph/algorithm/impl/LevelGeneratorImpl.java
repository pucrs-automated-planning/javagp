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

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.ActionLevel;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.ActionLevelGenerator;
import graphplan.graph.algorithm.PropositionLevelGenerator;
import graphplan.graph.planning.PlanningGraphException;

import java.util.*;
import java.util.logging.Logger;

public class LevelGeneratorImpl implements ActionLevelGenerator, PropositionLevelGenerator {
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ActionLevelGenerator.class.getName());
	
	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;
	
	private boolean usingTypes = false;

	public LevelGeneratorImpl(Map<String, Set<String>> types, Map<String, List<String>> parameterTypes){
		this.types = types;
		this.parameterTypes = parameterTypes;
		this.usingTypes = true;
		// Felipe: Moved the code here
		OperatorFactory opFactory = OperatorFactory.getInstance();
		opFactory.setTypes(this.types);
		opFactory.setParameterTypes(this.parameterTypes);
	}
	
	public LevelGeneratorImpl(){
		if (usingTypes) {
			OperatorFactory opFactory = OperatorFactory.getInstance();
			opFactory.setTypes(this.types);
			opFactory.setParameterTypes(this.parameterTypes);
		}		
	}
	
	/*
	 * TODO Optimize this method
	 * (non-Javadoc)
	 * @see graphplan.graph.algorithm.ActionLevelGenerator#createNextActionLevel(graphplan.graph.PropositionLevel)
	 */
	@Override
	public ActionLevel createNextActionLevel(PropositionLevel propositionLevel) throws PlanningGraphException {
		final ActionLevel actionLevel = new ActionLevel();
		
		final OperatorFactory opFactory = OperatorFactory.getInstance();

		final HashSet<Operator> opTemplateSet = new HashSet<>();
		final Set<Operator> opSet = new HashSet<>();
		final ArrayList<Proposition> preconds = new ArrayList<>();
		
		//TODO Change this to scan by operator rather than by proposition
		
		// For every proposition
		for (Proposition proposition : propositionLevel) {
			final List<Operator> templates;
			//Gather potential operator templates
			templates = opFactory.getRequiringOperatorTemplates(proposition);
			opTemplateSet.addAll(templates);
			//Add all noops
			opSet.add(opFactory.getNoop(proposition));
			//And prepare the list of preconditons for later
			preconds.add(proposition);
		}
		
		// XXX Why the hell do we use a null parameter here?
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates(null));

		/*for (Proposition proposition : propositionLevel) {
			preconds.add(proposition);
		}*/
		
		//Piece of crap algorithm used before has been replaced by this call
		
		try {
//			opSet.addAll(opFactory.getAllPossibleInstantiations(new ArrayList<Operator>(opTemplateSet), preconds));
			opSet.addAll(opFactory.getAllPossibleInstantiations(new ArrayList<>(opTemplateSet), preconds,propositionLevel));
		} catch (OperatorFactoryException e) {
			throw new PlanningGraphException(e.getMessage(),propositionLevel.getIndex()+1);
		}
		
		for (Operator operator : opSet) {
			actionLevel.addAction(operator);
		}
		// TODO discover how to properly instantiate operator templates
		// TODO optimize this algorithm
		
		return actionLevel;
	}
	
	@Override
	public PropositionLevel createNextPropositionLevel(ActionLevel actionLevel) {
		PropositionLevel propositionLevel = new PropositionLevel();
		for (Operator operator : actionLevel) {
			propositionLevel.addPropositions(operator.getEffects());
		}
		return propositionLevel;
	}

	public Map<String, Set<String>> getTypes() {
		return types;
	}

	public Map<String, List<String>> getParameterTypes() {
		return parameterTypes;
	}

	public boolean isUsingTypes() {
		return usingTypes;
	}
}
