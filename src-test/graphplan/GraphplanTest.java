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

import static org.junit.Assert.fail;
import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.PropositionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GraphplanTest {
	
	Graphplan graphplan = null;
	List<Operator> operators;
	OperatorFactory operatorFactory;
	
	PropositionFactory propositionFactory;
	List<Proposition> startState;
	List<Proposition> goalState;

	@Before
	public void setUp() throws Exception {
		graphplan = new Graphplan();
		operators = new ArrayList<Operator>();
		operatorFactory = OperatorFactory.getInstance();
		Operator o = operatorFactory.createOperatorTemplate("process(Block,ProcUnit)", 
				new String[] {"over(Block,ProcUnit)"}, 
				new String[] {"processed(Block,ProcUnit)"});
		operators.add(o);
		
		o = operatorFactory.createOperatorTemplate("consume(Block)", 
				new String[] {"over(Block,depositBelt)"}, 
				new String[] {"~over(Block,depositBelt)", "empty(depositBelt)", "finished(Block)"});
		operators.add(o);
		
		o = operatorFactory.createOperatorTemplate("move(Block,Device1,Device2)", 
				new String[] {"over(Block,Device1)", "empty(Device2)"}, 
				new String[] {"~over(Block,Device1)", "over(Block,Device2)", 
								"empty(Device1)", "~empty(Device2)"});
		operators.add(o);
		
		propositionFactory = PropositionFactory.getInstance();
		
		String propositions[] = new String[] {"procUnit(procUnit1)", "procUnit(procUnit2)", 
											  "procUnit(procUnit3)", "procUnit(procUnit4)", 
											  "device(procUnit1)", "device(procUnit2)", 
											  "device(procUnit3)", "device(procUnit4)", 
											  "device(depositBelt)", "device(feedBelt)",
											  "empty(procUnit1)", "empty(procUnit2)", 
											  "empty(procUnit3)", "empty(procUnit4)", 
											  "empty(depositBelt)", "empty(feedBelt)", 
											  "over(block6, feedBelt)", "block(block6)"};
		
		startState = Arrays.asList(propositionFactory.getPropositions(propositions));
		
		propositions = new String[] {"processed(block6,procUnit2)", 
									 "processed(block6,procUnit4)", 
									 "finished(block6)"};
		
		goalState = Arrays.asList(propositionFactory.getPropositions(propositions));
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void testBlocks() {
		try {
			for(int i=1;i<=7;i++)
				Graphplan.main(new String[] {"-d","examples/strips/blocksworld/domain.txt","-p","examples/strips/blocksworld/problem" + i + ".txt"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinner() {
		try {
			Graphplan.main(new String[] {"-d","examples/strips/dinner/domain.txt","-p","examples/strips/dinner/problem.txt"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	public void testPlan() {
		GraphplanTestUtil util = GraphplanTestUtil.getInstance();
		DomainDescription domainDescriptions[] = util.createDomains();
			
		PlanResult planResult = null;
		try {
			graphplan.setMaxLevels(6);
			planResult = graphplan.plan(domainDescriptions[0]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		if(planResult.isTrue()) {
			System.out.println("Plan succeeded");
			System.out.println(planResult);
		} else {
			fail("Plan failed");
		}
		
		//Another domain
		try {
			graphplan.setMaxLevels(30);
			planResult = graphplan.plan(domainDescriptions[1]);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		if(planResult.isTrue()) {
			System.out.println("Plan succeeded");
			System.out.println(planResult);
		} else {
			fail("Plan failed");
		}
	}

	public void testGetPlanPreconditions() {
		DomainDescription description = new DomainDescription(operators, Arrays.asList(new Proposition[] {}), Arrays.asList(new Proposition[] {}));
		String plan[] = new String[] {"move(b1,pu1,pu2)",
									"process(b1,pu2)",
									"move(b1,pu2,pu3)"};
		
		List<Proposition> planPreconds = graphplan.getPlanPreconditions(Arrays.asList(plan), description);
		
		String signatures[] = new String[] {"over(b1,pu1)","empty(pu2)","empty(pu3)"};
		Proposition propositions[] = PropositionFactory.getInstance().getPropositions(signatures);
		
		if(propositions.length != planPreconds.size()) {
			fail("Preconditions are not minimal");
		}
		
		for (int i = 0; i < propositions.length; i++) {
			if(!planPreconds.contains(propositions[i])) {
				fail("Missing precondition "+propositions[i]);
			}
		}
		
		//Second test
		plan = new String[] { "move(b1,pu1,pu2)",
				"process(b1,pu2)",
				"move(b1,pu2,pu3)",
				"process(b1,pu3)",
				"move(b1, pu3, depositBelt)",
				"consume(b1)"};
		
		planPreconds = graphplan.getPlanPreconditions(Arrays.asList(plan), description);
		
		signatures = new String[] {"over(b1,pu1)","empty(pu2)","empty(pu3)","empty(depositBelt)"};
		propositions = PropositionFactory.getInstance().getPropositions(signatures);
		
		if(propositions.length != planPreconds.size()) {
			fail("Preconditions are not minimal");
		}
		
		for (int i = 0; i < propositions.length; i++) {
			if(!planPreconds.contains(propositions[i])) {
				fail("Missing precondition "+propositions[i]);
			}
		}
		
		//Third test
		plan = new String[] { "move(b1,pu1,pu2)",
				"move(b2,feedBelt,pu1)",
				"process(b2,pu1)",
				"process(b1,pu2)",
				"move(b1,pu2,pu3)",
				"process(b1,pu3)",
				"move(b1, pu3, depositBelt)",
				"consume(b1)"};
		
		planPreconds = graphplan.getPlanPreconditions(Arrays.asList(plan), description);
		
		signatures = new String[] {"over(b2,feedBelt)",
								"over(b1,pu1)",
								"empty(pu2)",
								"empty(pu3)",
								"empty(depositBelt)"};
		propositions = PropositionFactory.getInstance().getPropositions(signatures);
		
		if(propositions.length != planPreconds.size()) {
			fail("Preconditions are not minimal");
		}
		
		for (int i = 0; i < propositions.length; i++) {
			if(!planPreconds.contains(propositions[i])) {
				fail("Missing precondition "+propositions[i]);
			}
		}
		
		//Fourth test
		
//		Third test
		plan = new String[] { "move(block1,feedBelt,procUnit3)",
				"process(block1,procUnit3)",
				"move(block1,procUnit3,procUnit2)",
				"process(block1,procUnit2)",
				"move(block1,procUnit2,procUnit1)",
				"process(block1,procUnit1)",
				"move(block1,procUnit1,depositBelt)",
				"consume(block1)"};
		
		planPreconds = graphplan.getPlanPreconditions(Arrays.asList(plan), description);
		
		signatures = new String[] {"over(block1,feedBelt)",
								"empty(procUnit3)",
								"empty(procUnit2)",
								"empty(procUnit1)",
								"empty(depositBelt)"};
		propositions = PropositionFactory.getInstance().getPropositions(signatures);
		
		if(propositions.length != planPreconds.size()) {
			fail("Preconditions are not minimal");
		}
		
		for (int i = 0; i < propositions.length; i++) {
			if(!planPreconds.contains(propositions[i])) {
				fail("Missing precondition "+propositions[i]);
			}
		}
	}

}
