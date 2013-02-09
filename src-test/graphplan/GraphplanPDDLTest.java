package graphplan;

import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * 
 * @author Ramon Pereira
 *
 */
public class GraphplanPDDLTest {

	@Test
	public void testBlocksWorldPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/blockworld/blocksworld.pddl", "-p", "examples/pddl/blockworld/pb15.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d","examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb1.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDockWorkerRobotsPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d","examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb1.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBlocksWorldPDDLAll(){
		try {
			for(int i=1;i<=15;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/blockworld/blocksworld.pddl", "-p", "examples/pddl/blockworld/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDLAll() {
		try {
			for(int i=1;i<=1;i++)
				Graphplan.main(new String[] {"-pddl", "-d","examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
