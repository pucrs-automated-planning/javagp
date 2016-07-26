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
package graphplan;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.PropositionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GraphplanTestUtil {
	private static final GraphplanTestUtil singleton = new GraphplanTestUtil();
	
	private final PropositionFactory propositionFactory;
	private final OperatorFactory operatorFactory;
	
	private GraphplanTestUtil() {
		this.propositionFactory = PropositionFactory.getInstance();
		this.operatorFactory = OperatorFactory.getInstance();
	}
	
	public static GraphplanTestUtil getInstance() {
		return singleton;
	}

	public DomainDescription []createDomains() {
		DomainDescription domains[] = new DomainDescription[3];
		
		String propositions[];
		List<Proposition> initialState;
		List<Proposition> goalState;
		List<Operator> operators;
		// The dinner date domain
		propositions = new String[] {"garbage","cleanHands","quiet"};
		initialState = Arrays.asList(propositionFactory.getPropositions(propositions));
		
		propositions = new String[] {"dinner","present","~garbage"};
		goalState = Arrays.asList(propositionFactory.getPropositions(propositions));
		
		operators = new ArrayList<>(4);
		operators.add(operatorFactory.createOperatorTemplate("cook", 
										new String[] {"cleanHands"}, 
										new String[] {"dinner"}));
		
		operators.add(operatorFactory.createOperatorTemplate("wrap", 
				new String[] {"quiet"}, 
				new String[] {"present"}));
	
		operators.add(operatorFactory.createOperatorTemplate("carry", 
				new String[] {}, 
				new String[] {"~cleanHands", "~garbage"}));
		
		operators.add(operatorFactory.createOperatorTemplate("dolly", 
				new String[] {}, 
				new String[] {"~quiet", "~garbage"}));
		
		domains[0] = new DomainDescription(operators, initialState, goalState);
		
		// The production cell domain
		propositions = new String[] {"procUnit(procUnit1)", "procUnit(procUnit2)", 
									   "procUnit(procUnit3)", "procUnit(procUnit4)",
									   "device(procUnit1)", "device(procUnit2)", 
									   "device(procUnit3)", "device(procUnit4)", 
									   "device(depositBelt)", "device(feedBelt)",
									   "empty(procUnit1)", "empty(procUnit2)", 
									   "empty(procUnit3)", "empty(procUnit4)", 
									   "empty(depositBelt)", "empty(feedBelt)",
									   "block(block1)","over(block1,procUnit1)"};
		initialState = Arrays.asList(propositionFactory.getPropositions(propositions));
		
		propositions = new String[] {"processed(block1,procUnit2)", "processed(block1,procUnit4)", 
									 "finished(block1)"};
		goalState = Arrays.asList(propositionFactory.getPropositions(propositions));
		
		operators = new ArrayList<>(3);
		
		operators.add(operatorFactory.createOperatorTemplate("process(Block,ProcUnit)",
						new String[] {"block(Block)", "procUnit(ProcUnit)", 
									  "over(Block, ProcUnit)"},
						new String[] {"processed(Block, ProcUnit)"}));
		
		operators.add(operatorFactory.createOperatorTemplate("consume(Block)",
				new String[] {"block(Block)", "over(Block, depositBelt)"},
				new String[] {"~over(Block, depositBelt)","empty(depositBelt)",
							  "finished(Block)"}));
		
		operators.add(operatorFactory.createOperatorTemplate("move(Block,Device1,Device2)",
				new String[] {"block(Block)", "empty(Device2)", 
							  "over(Block, Device1)", "device(Device2)",
							  "device(Device1)"},
				new String[] {"over(Block, Device2)", "~over(Block, Device1)", 
							  "~empty(Device2)", "empty(Device1)"}));
		
		domains[1] = new DomainDescription(operators, initialState, goalState);
		
		// A larger variation of the production cell domain
		propositions = new String[] {"procUnit(procUnit1)", "procUnit(procUnit2)", 
				   "procUnit(procUnit3)", "procUnit(procUnit4)",
				   "device(procUnit1)", "device(procUnit2)", 
				   "device(procUnit3)", "device(procUnit4)", 
				   "device(depositBelt)", "device(feedBelt)",
				   "empty(procUnit1)", "empty(procUnit2)", 
				   "empty(procUnit3)", "empty(procUnit4)", 
				   "empty(depositBelt)", "empty(feedBelt)",
				   "block(block1)","over(block1,procUnit1)",
				   "block(block2)","over(block2,procUnit2)"};
		initialState = Arrays.asList(propositionFactory.getPropositions(propositions));
		domains[2] = new DomainDescription(operators, initialState, goalState);
		
		return domains;
	}
}
