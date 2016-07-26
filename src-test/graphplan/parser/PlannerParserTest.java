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
package graphplan.parser;

import graphplan.domain.DomainDescription;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import static org.junit.Assert.fail;

// TODO: implement missing tests
public class PlannerParserTest {

	static final String OPERATORS_FILE = "src-test/graphplan/parser/operators.txt";
	static final String PROBLEM_FILE = "src-test/graphplan/parser/problem.txt";

	protected PlannerParser plannerParser;

	@Before
	public void setUp() throws Exception {
		plannerParser = new PlannerParser();
	}

	@Test
	public void testParseProblemFileFile() {
		File operators = new File(OPERATORS_FILE);
		File problem = new File(PROBLEM_FILE);

		DomainDescription domainDescription = null;

		try {
			domainDescription = plannerParser.parseProblem(operators, problem);
		} catch (FileNotFoundException | ParseException e) {
			fail(e.toString());
		}

		if (domainDescription == null) {
			fail("Domain description is invalid");
		}
	}

	@Test
	public void testParseProblemInputStreamInputStream() {
		FileInputStream operatorStream = null;
		FileInputStream problemStream = null;
		try {
			operatorStream = new FileInputStream(OPERATORS_FILE);
			problemStream = new FileInputStream(PROBLEM_FILE);
		} catch (FileNotFoundException e) {
			fail(e.toString());
		}

		DomainDescription domainDescription = null;

		try {
			domainDescription = plannerParser.parseProblem(operatorStream, problemStream);
		} catch (ParseException e) {
			fail(e.toString());
		}

		if (domainDescription == null) {
			fail("Domain description is invalid");
		}
	}

	//	@Test
	public void testOperators() {
		fail("Not yet implemented"); // TODO
	}

	//	@Test
	public void testOperator() {
		fail("Not yet implemented"); // TODO
	}

	//	@Test
	public void testStart() {
		fail("Not yet implemented"); // TODO
	}

	//	@Test
	public void testGoal() {
		fail("Not yet implemented"); // TODO
	}

	//	@Test
	public void testPropositions() {
		fail("Not yet implemented"); // TODO
	}

	//	@Test
	public void testProposition() {
		fail("Not yet implemented"); // TODO
	}

}
