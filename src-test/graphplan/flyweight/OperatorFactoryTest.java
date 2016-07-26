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
package graphplan.flyweight;

import graphplan.GraphplanTestUtil;
import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

// TODO: implement missing tests
public class OperatorFactoryTest {
	private static final Logger logger = Logger.getLogger(OperatorFactoryTest.class.getName());
	protected OperatorFactory operatorFactory = null;

	@Before
	public void setUp() throws Exception {
		operatorFactory = OperatorFactory.getInstance();
	}

	//	@Test
	public void testGetNoop() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
	public void testGetOperator() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
	public void testGetRequiringOperatorTemplates() {
		fail("Not yet implemented"); // TODO
	}

//	@Test
	public void testGetCausingOperatorsTemplates() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetAllPossibleInstantiations() {
		GraphplanTestUtil util = GraphplanTestUtil.getInstance();
		DomainDescription domainDescriptions[] = util.createDomains();
		operatorFactory.resetOperatorTemplates();
		
		for(Operator operator : domainDescriptions[1].getOperators()) {
			try {
				operatorFactory.addOperatorTemplate(operator);
			} catch (OperatorFactoryException e) {
				fail(e.toString());
			}
		}
		
		List<Operator> operators = domainDescriptions[1].getOperators();
		List<Proposition> propositions = domainDescriptions[1].getInitialState();
		
		try {
			Set<Operator> instances = operatorFactory.getAllPossibleInstantiations(operators, propositions);
			logger.info("Instances");
			for(Operator op : instances) {
				logger.info(op.toString());
			}
		} catch (OperatorFactoryException e) {
			fail(e.toString());
		}
		
		propositions = domainDescriptions[2].getInitialState();
		try {
			Set<Operator> instances = operatorFactory.getAllPossibleInstantiations(operators, propositions);
			logger.info("Instances");
			for(Operator op : instances) {
				logger.info(op.toString());
			}
		} catch (OperatorFactoryException e) {
			fail(e.toString());
		}
	}

}
