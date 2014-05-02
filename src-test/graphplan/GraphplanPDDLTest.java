package graphplan;

import static org.junit.Assert.fail;

import org.junit.Test;

public class GraphplanPDDLTest {

	@Test
	public void testRubiksPDDL() {
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/rubiks/rubiks.pddl", "-p", "examples/pddl/rubiks/pb5.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testRushPDDL() {
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/rush/rush.pddl", "-p", "examples/pddl/rush/pb0.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRobbyPDDL() {
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/robby/robby.pddl", "-p", "examples/pddl/robby/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBlocksWorldPDDL() {
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/blockworld/blocksworld.pddl", "-p", "examples/pddl/blockworld/pb10.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGripperPDDL(){
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/gripper/gripper.pddl", "-p", "examples/pddl/gripper/pb3.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHanoiPDDL(){
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/hanoi/hanoi.pddl", "-p", "examples/pddl/hanoi/pb3.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDL() {
		try {
			Graphplan.main(new String[] {"-d","examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb1.pddl"});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void testDockWorkerRobotsPDDL() {
		try {
			Graphplan.main(new String[] {"-d","examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb1.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testAbstractPDDLAll(){
		try {
			Graphplan.main(new String[] {"-d", "examples/pddl/abstract/abstract.pddl", "-p", "examples/pddl/abstract/pb1.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBlocksWorldPDDLAll(){
		try {
			for(int i=1;i<=15;i++)
				Graphplan.main(new String[] {"-d", "examples/pddl/blockworld/blocksworld.pddl", "-p", "examples/pddl/blockworld/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDockWorkerRobotsPDDLAll() {
		try {
			for(int i=1;i<=8;i++)
				Graphplan.main(new String[] {"-d","examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDLAll() {
		try {
			for(int i=1;i<=1;i++)
				Graphplan.main(new String[] {"-d","examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGripperPDDLAll(){
		try {
			for(int i=1;i<=4;i++)
				Graphplan.main(new String[] {"-d", "examples/pddl/gripper/gripper.pddl", "-p", "examples/pddl/gripper/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHanoiPDDLAll(){
		try {
			for(int i=1;i<=6;i++)
				Graphplan.main(new String[] {"-d", "examples/pddl/hanoi/hanoi.pddl", "-p", "examples/pddl/hanoi/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLogisticsPDDLAll(){
		try {
			for(int i=1;i<=2;i++)
				Graphplan.main(new String[] {"-d", "examples/pddl/logistics/logistics.pddl", "-p", "examples/pddl/logistics/pb" + i + ".pddl"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testRubiksPDDLAll() {
		try {
			for(int i=1;i<=5;i++)
				Graphplan.main(new String[] {"-d", "examples/pddl/rubiks/rubiks.pddl", "-p", "examples/pddl/rubiks/pb"+ i +".pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPDDLAll(){
		this.testAbstractPDDLAll();
		this.testBlocksWorldPDDLAll();
		this.testDockWorkerRobotsPDDLAll();
		this.testDinnerPDDLAll();
		this.testGripperPDDLAll();
		this.testHanoiPDDLAll();
		this.testLogisticsPDDLAll();
	}
}