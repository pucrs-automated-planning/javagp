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

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

public class GraphplanTest {
	private static final Logger logger = Logger.getLogger(GraphplanTest.class.getName());

	@Test
	public void testBlocks() {
		int i;
		try {
			for (i = 1; i <= 7; i++) {
				logger.info("Testing Blocksworld STRIPS problem" + i);
				Graphplan.main(new String[]{"-nopddl", "-d", "examples/strips/blocksworld/domain.txt", "-p", "examples/strips/blocksworld/problem" + i + ".txt"});
			}
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testDinner() {
		try {
			Graphplan.main(new String[]{"-nopddl", "-d", "examples/strips/dinner/domain.txt", "-p", "examples/strips/dinner/problem.txt"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

}
