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
import graphplan.graph.GraphLevel;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.ActionLevelGeneratorClosedWorldAssumption;
import graphplan.graph.planning.PlanningGraphException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LevelGeneratorClosedWorldAssumptionImpl extends LevelGeneratorImpl implements ActionLevelGeneratorClosedWorldAssumption {

	public LevelGeneratorClosedWorldAssumptionImpl(Map<String, Set<String>> types, Map<String, List<String>> parameterTypes){
		super(types, parameterTypes);
	}
	
	public LevelGeneratorClosedWorldAssumptionImpl(){}
	
	@SuppressWarnings("rawtypes")
	@Override
	public ActionLevel createNextActionLevel(PropositionLevel propositionLevel, GraphLevel initialState) throws PlanningGraphException {
		final ActionLevel actionLevel = new ActionLevel();
		
		final OperatorFactory opFactory = OperatorFactory.getInstance();

		final HashSet<Operator> opTemplateSet = new HashSet<Operator>();
		final Set<Operator> opSet = new HashSet<Operator>();
		final ArrayList<Proposition> preconds = new ArrayList<Proposition>();
		
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
		
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates(null));

		try {
			if(this.isUsingTypes())	{
				opFactory.setTypes(this.getTypes());
				opFactory.setParameterTypes(this.getParameterTypes());
				opSet.addAll(opFactory.getAllPossibleInstantiations(new ArrayList<Operator>(opTemplateSet), preconds, initialState));
			} else opSet.addAll(opFactory.getAllPossibleInstantiations(new ArrayList<Operator>(opTemplateSet), preconds));
		} catch (OperatorFactoryException e) {
			throw new PlanningGraphException(e.getMessage(),propositionLevel.getIndex()+1);
		}
		
		for (Operator operator : opSet) {
			actionLevel.addAction(operator);
		}
		
		return actionLevel;
	}
}
