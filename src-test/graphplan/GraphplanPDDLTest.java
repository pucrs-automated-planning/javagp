package graphplan;

import static org.junit.Assert.fail;

import org.junit.Test;

public class GraphplanPDDLTest {

	@Test
	public void testBlocksWorldPDDL() {
		try {
			
			Graphplan.main(new String[] {"-pddl", "-d", "examples/pddl/blockworld/blocksworld.pddl", "-p", "examples/pddl/blockworld/pb3.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d","pddl/dinner/dinner.pddl", "-p", "pddl/dinner/pb1.pddl"});
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	@Test
	public void testSimpleCaseBlockWordlPDDL(){
		try {
			Graphplan.main(new String[] {"-pddl","-d","pddl/blockworld/blocksworld.pddl","-p", "pddl/blockworld/pb3.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void testDockWorkerRobotsPDDL() {
		try {
			Graphplan.main(new String[] {"-pddl", "-d","pddl/dinner/dinner.pddl", "-p", "pddl/dinner/pb1.pddl"});
			Graphplan.main(new String[] {"-pddl", "-d","examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb1.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	

	//MASS TESTS
	
	@Test
	public void testAbstractPDDLAll(){
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "pddl/abstract/abstract.pddl", "-p", "pddl/abstract/abstract-pb.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBlocksWorldPDDLAll(){
		try {
			for(int i=1;i<=15;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/blockworld/blocksworld.pddl", "-p", "pddl/blockworld/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testBriefcasePDDLAll(){
		try {
			Graphplan.main(new String[] {"-pddl", "-d", "pddl/briefcase/briefcase.pddl", "-p", "pddl/briefcase/pb1.pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDinnerPDDLAll() {
		try {
			for(int i=1;i<=1;i++)
				Graphplan.main(new String[] {"-pddl", "-d","pddl/dinner/dinner.pddl", "-p", "pddl/dinner/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDockWorkerRobotsPDDLAll(){
		try {
			for(int i=1;i<=3;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/dwr/dwr.pddl", "-p", "pddl/dwr/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testFerryPDDLAll(){
		try {
			for(int i=1;i<=2;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/ferry/ferry.pddl", "-p", "pddl/ferry/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGripperPDDLAll(){
		try {
			for(int i=1;i<=4;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/gripper/gripper.pddl", "-p", "pddl/gripper/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testHanoiPDDLAll(){
		try {
			for(int i=1;i<=6;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/hanoi/hanoi.pddl", "-p", "pddl/hanoi/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLogisticsPDDLAll(){
		try {
			for(int i=1;i<=5;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/logistics/logistics.pddl", "-p", "pddl/logistics/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testMonkeyPDDLAll(){
		try {
			for(int i=1;i<=3;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/monkey/monkey.pddl", "-p", "pddl/monkey/pb" + i + ".pddl"});
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testTravelPDDLAll(){
		try {
			for(int i=1;i<=9;i++)
				Graphplan.main(new String[] {"-pddl", "-d", "pddl/travel/travel.pddl", "-p", "pddl/travel/pb" + i + ".pddl"});
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
			this.testDockWorkerRobotsPDDLAll();
			this.testFerryPDDLAll();
			this.testGripperPDDLAll();
			this.testHanoiPDDLAll();
			this.testLogisticsPDDLAll();
			this.testMonkeyPDDLAll();
			this.testTravelPDDLAll();
	}
}
