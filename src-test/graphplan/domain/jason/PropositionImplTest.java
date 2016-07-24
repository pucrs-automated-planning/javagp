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
package graphplan.domain.jason;

import graphplan.domain.Proposition;
import graphplan.flyweight.PropositionFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

// TODO: implement missing tests
public class PropositionImplTest {
	private PropositionFactory propositionFactory = PropositionFactory.getInstance();
	private Proposition propositions[];

	@Before
	public void setUp() throws Exception {
		String propositionSignatures[] = new String[] {"at(a)", "~at(a)"};
		propositions = propositionFactory.getPropositions(propositionSignatures);
	}

//	@Test
	public void testApply() {
		fail("Not yet implemented");
	}

//	@Test
	public void testGetSignature() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsMutex() {
		if(!propositions[0].isMutex(propositions[1])) {
			fail("Proposition "+propositions[0]+" should be mutex with "+propositions[1]);
		}
	}

//	@Test
	public void testAccept() {
		fail("Not yet implemented");
	}

//	@Test
	public void testIterator() {
		fail("Not yet implemented");
	}

//	@Test
	public void testUnifies() {
		fail("Not yet implemented");
	}

//	@Test
	public void testMakeGround() {
		fail("Not yet implemented");
	}

}
