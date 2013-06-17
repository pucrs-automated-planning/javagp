package graphplan;

import static org.junit.Assert.fail;

import org.junit.Test;

public class GraphplanPDDLTest {

	@Test
	public void testBlocksWorldPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/blockworld/blocksworld.pddl", "-p", "examples/pddl/blockworld/pb10.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGripperPDDL(){
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/gripper/gripper.pddl", "-p", "examples/pddl/gripper/pb3.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHanoiPDDL(){
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/hanoi/hanoi.pddl", "-p", "examples/pddl/hanoi/pb2.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d","examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb2.pddl"});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void testDockWorkerRobotsPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d","examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb8.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testAbstractPDDLAll(){
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/abstract/abstract.pddl", "-p", "examples/pddl/abstract/abstract-pb.pddl"});
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
	public void testBriefcasePDDLAll(){
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/briefcase/briefcase.pddl", "-p", "examples/pddl/briefcase/pb1.pddl"});
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
	
	@Test
	public void testFerryPDDLAll(){
		try {
			for(int i=1;i<=2;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/ferry/ferry.pddl", "-p", "examples/pddl/ferry/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGripperPDDLAll(){
		try {
			for(int i=1;i<=4;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/gripper/gripper.pddl", "-p", "examples/pddl/gripper/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHanoiPDDLAll(){
		try {
			for(int i=1;i<=6;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/hanoi/hanoi.pddl", "-p", "examples/pddl/hanoi/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLogisticsPDDLAll(){
		try {
			for(int i=1;i<=5;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/logistics/logistics.pddl", "-p", "examples/pddl/logistics/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMonkeyPDDLAll(){
		try {
			for(int i=1;i<=3;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/monkey/monkey.pddl", "-p", "examples/pddl/monkey/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTravelPDDLAll(){
		try {
			for(int i=1;i<=9;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/travel/travel.pddl", "-p", "examples/pddl/travel/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testPDDLAll(){
			this.testAbstractPDDLAll();
			this.testBlocksWorldPDDLAll();
			this.testBriefcasePDDLAll();
			this.testDinnerPDDLAll();
			this.testFerryPDDLAll();
			this.testGripperPDDLAll();
			this.testHanoiPDDLAll();
			this.testLogisticsPDDLAll();
			this.testMonkeyPDDLAll();
			this.testTravelPDDLAll();
	}
}
