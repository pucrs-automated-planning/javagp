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
package graphplan.graph.memo;

import graphplan.GraphplanTestUtil;
import graphplan.domain.DomainDescription;
import graphplan.domain.Proposition;
import graphplan.flyweight.PropositionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import static org.junit.Assert.fail;

public class MemoizationTableTest {
	protected static final Logger logger = Logger.getLogger(MemoizationTableTest.class.getName());
	
	protected MemoizationTable table;
	protected DomainDescription domainDescriptions[];

	@Before
	public void setUp() throws Exception {
		domainDescriptions = GraphplanTestUtil.getInstance().createDomains();
		table = new MemoizationTable();
		table.ensureCapacity(3);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsNoGood() {
		Set<Proposition> goalSet1 = new TreeSet<Proposition>();
		goalSet1.add(domainDescriptions[1].getInitialState().get(1));
		goalSet1.add(domainDescriptions[1].getInitialState().get(3));
		goalSet1.add(domainDescriptions[1].getInitialState().get(5));
		
		logger.info("Goal Set 1 is "+goalSet1.toString());
		String signature1 = PropositionFactory.getInstance().getGoalsSignature(goalSet1);
		logger.info("Signature is "+signature1);
		
		Set<Proposition> goalSet2 = new TreeSet<Proposition>();
		goalSet2.add(domainDescriptions[1].getInitialState().get(3));
		goalSet2.add(domainDescriptions[1].getInitialState().get(5));
		goalSet2.add(domainDescriptions[1].getInitialState().get(1));
		
		logger.info("Goal Set 2 is "+goalSet2.toString());
		String signature2 = PropositionFactory.getInstance().getGoalsSignature(goalSet2);
		logger.info("Signature is "+signature2);
		
		goalSet1 = new TreeSet<Proposition>();
		goalSet1.add(domainDescriptions[1].getInitialState().get(2));
		goalSet1.add(domainDescriptions[1].getInitialState().get(4));
		goalSet1.add(domainDescriptions[1].getInitialState().get(6));
		goalSet1.add(domainDescriptions[1].getInitialState().get(8));
		
		logger.info("Goal Set 1 is "+goalSet1.toString());
		signature1 = PropositionFactory.getInstance().getGoalsSignature(goalSet1);
		logger.info("Signature is "+signature1);
		
		goalSet2 = new TreeSet<Proposition>();
		goalSet2.add(domainDescriptions[1].getInitialState().get(6));
		goalSet2.add(domainDescriptions[1].getInitialState().get(4));
		goalSet2.add(domainDescriptions[1].getInitialState().get(2));
		goalSet2.add(domainDescriptions[1].getInitialState().get(8));
		
		logger.info("Goal Set 2 is "+goalSet2.toString());
		signature2 = PropositionFactory.getInstance().getGoalsSignature(goalSet2);
		logger.info("Signature is "+signature2);
	}

	@Test
	public void testAddNoGood() {
		Set<Proposition> goalSet1 = new TreeSet<Proposition>();
		goalSet1.add(domainDescriptions[1].getInitialState().get(1));
		goalSet1.add(domainDescriptions[1].getInitialState().get(3));
		goalSet1.add(domainDescriptions[1].getInitialState().get(5));
		
		table.addNoGood(goalSet1, 2);
		
		if(!table.isNoGood(goalSet1, 2)) {
			fail("No goods table is busted");
		}
		
		Set<Proposition> goalSet2 = new TreeSet<Proposition>();
		goalSet2.add(domainDescriptions[1].getInitialState().get(3));
		goalSet2.add(domainDescriptions[1].getInitialState().get(5));
		goalSet2.add(domainDescriptions[1].getInitialState().get(1));
		
		if(!table.isNoGood(goalSet2, 2)) {
			fail("Failed to find another combination of the same set of goals");
		}
	}

}
