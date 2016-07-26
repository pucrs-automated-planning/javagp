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

import graphplan.GraphplanTestUtil;
import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.flyweight.PropositionFactory;
import graphplan.graph.draw.TextDrawVisitor;
import graphplan.graph.planning.PlanningGraph;
import graphplan.graph.planning.PlanningGraphException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class PlanningGraphTest {

	private static final Logger logger = Logger.getLogger(PlanningGraphTest.class.getName());

	private PlanningGraph planningGraph = null;

	private DomainDescription descriptions[] = null;

	private List<Proposition> initialPropositions = null;

	private OperatorFactory operatorFactory = null;

	@Before
	public void setUp() throws Exception {
		PropositionFactory propositionFactory = PropositionFactory.getInstance();

		planningGraph = new PlanningGraph();

		PropositionLevel initialState = new PropositionLevel();

		operatorFactory = OperatorFactory.getInstance();
		Operator operTemplate = operatorFactory.createOperatorTemplate("move(A,B)",
				new String[]{"~at(B)", "at(A)"},
				new String[]{"at(B)", "~at(A)"});

		operatorFactory.addOperatorTemplate(operTemplate);

		String propositions[] = new String[]{"at(a)", "over(x,y)", "~at(b)"};
		initialPropositions = Arrays.asList(propositionFactory.getPropositions(propositions));

		for (Proposition proposition : initialPropositions) {
			initialState.addProposition(proposition);
		}

		planningGraph.addGraphLevel(initialState);

		GraphplanTestUtil util = GraphplanTestUtil.getInstance();
		descriptions = util.createDomains();
	}

	private void addLevels() {
		ActionLevel level = new ActionLevel();
		try {
			Operator op = operatorFactory.getOperator("move(a,b)");
			level.addAction(op);
			planningGraph.addGraphLevel(level);
		} catch (OperatorFactoryException e) {
			fail(e.toString());
		}

		PropositionLevel propositionLevel = new PropositionLevel();
		for (Proposition proposition : initialPropositions) {
			propositionLevel.addProposition(proposition);
		}
		planningGraph.addGraphLevel(propositionLevel);
	}

	@Test
	public void testAccept() {
		addLevels();
		try {
			TextDrawVisitor textDrawVisitor = new TextDrawVisitor();
			planningGraph.accept(textDrawVisitor);
			logger.info("****************************************************");
			logger.info("Visitor results");
			logger.info(textDrawVisitor.toString());
			logger.info("****************************************************");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetChildren() {
		try {
			for (Object element : planningGraph) {
				//				System.out.println(element);
				assertNotNull(element);
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void testGetGraphLevel() {
		GraphLevel level = planningGraph.getGraphLevel(0);
		assertNotNull("Graph level at 0 should not be null", level);
	}

	@Test
	public void testAddGraphLevel() {
		logger.info("Testing addition of action level");
		ActionLevel actionLevel = new ActionLevel();
		Operator operator = null;
		try {
			operator = OperatorFactory.getInstance().getOperator(
					"move(a,b)");
		} catch (Exception e) {
			fail(e.toString());
		}

		actionLevel.addAction(operator);
		assertTrue(planningGraph.addGraphLevel(actionLevel));

		TextDrawVisitor visitor = new TextDrawVisitor();
		planningGraph.accept(visitor);
		logger.info(visitor.toString());

		logger.info("Testing addition of proposition level");
		PropositionLevel level = new PropositionLevel();
		for (Proposition proposition : initialPropositions) {
			level.addProposition(proposition);
		}
		assertTrue("Failed to add proposition level " + level, planningGraph.addGraphLevel(level));


		visitor = new TextDrawVisitor();
		planningGraph.accept(visitor);
		logger.info(visitor.toString());
	}

	@Test
	public void testGoalsPossible() {
		assertTrue("Same goals not possible", planningGraph.goalsPossible(initialPropositions, 0));
		logger.info("*************************************************");
		logger.info("Goals: " + initialPropositions + " possible in graph");
		TextDrawVisitor visitor = new TextDrawVisitor();
		planningGraph.accept(visitor);
		logger.info(visitor.toString());
		logger.info("*************************************************");
	}

	@Test
	public void testExpandGraph() {
		try {
			logger.info("*************************************************");
			TextDrawVisitor visitor = new TextDrawVisitor();
			planningGraph.accept(visitor);
			logger.info("Initial Graph is: " + visitor.toString());
			planningGraph.expandGraph();
			visitor.reset();
			planningGraph.accept(visitor);
			logger.info("Expanded Graph is: " + visitor.toString());

			logger.info("*************************************************");

			logger.info("Testing dinner date problem");
			operatorFactory.resetOperatorTemplates();

			PropositionLevel level = new PropositionLevel();
			for (Proposition proposition : descriptions[0].getInitialState()) {
				level.addProposition(proposition);
			}

			for (Operator operator : descriptions[0].getOperators()) {
				try {
					operatorFactory.addOperatorTemplate(operator);
				} catch (OperatorFactoryException e) {
					fail(e.getMessage());
				}
			}

			planningGraph = new PlanningGraph(level);
			visitor.reset();
			planningGraph.accept(visitor);
			logger.info("Initial Graph is: " + visitor.toString());
			logger.info("Expanding Graph...");
			planningGraph.expandGraph();
			visitor.reset();
			planningGraph.accept(visitor);
			logger.info("Expanded Graph is: " + visitor.toString());
			logger.info("Expanding Graph...");
			planningGraph.expandGraph();
			visitor.reset();
			planningGraph.accept(visitor);
			logger.info("Expanded Graph is: " + visitor.toString());


			logger.info("*************************************************");
		} catch (PlanningGraphException e) {
			fail(e.toString());
		}
	}

}
