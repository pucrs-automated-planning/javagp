package graphplan;

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

// TODO: Implement more tests using the "extractAllPossibleGoals"
// Today, this parameter is (I hope) returning all possible solutions with minimal length
public class GraphplanPDDLTest {

	private static final Logger logger = Logger.getLogger(GraphplanPDDLTest.class.getName());

	@Test
	public void testRubiksPDDL() {
		logger.info("Testing Rubiks problem in PDDL");
		try {
			Graphplan.main(new String[]{"-d", "examples/pddl/rubiks/rubiks.pddl", "-p", "examples/pddl/rubiks/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testRushPDDL() {
		logger.info("Testing Rush hour problem in PDDL");
		try {
			Graphplan.main(new String[]{"-d", "examples/pddl/rush/rush.pddl", "-p", "examples/pddl/rush/pb0.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testRobbyPDDL() {
		logger.info("Testing Robby problem in PDDL");
		try {
			Graphplan.main(new String[]{"-d", "examples/pddl/robby/robby.pddl", "-p", "examples/pddl/robby/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testBlocksWorldPDDL() {
		try {
			logger.info("Testing Blocks world problem in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/blocksworld/blocksworld.pddl", "-p", "examples/pddl/blocksworld/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testGripperPDDL() {
		try {
			logger.info("Testing Gripper problem in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/gripper/gripper.pddl", "-p", "examples/pddl/gripper/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testHanoiPDDL() {
		try {
			logger.info("Testing Towers of Hanoi problem in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/hanoi/hanoi.pddl", "-p", "examples/pddl/hanoi/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testDinnerPDDL() {
		try {
			logger.info("Testing Dinner Date problem in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb1.pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testDockWorkerRobotsPDDL() {
		try {
			logger.info("Testing DWR problem in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb2.pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testAbstractPDDL() {
		try {
			logger.info("Testing Abstract problem in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/abstract/abstract.pddl", "-p", "examples/pddl/abstract/pb1.pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testBlocksWorldPDDLAll() {
		try {
			logger.info("Testing All Blocks world problems in PDDL");
			for (int i = 1; i <= 15; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/blocksworld/blocksworld.pddl", "-p", "examples/pddl/blocksworld/pb" + i + ".pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testDockWorkerRobotsPDDLAll() {
		try {
			logger.info("Testing All DWR problems in PDDL");
			for (int i = 1; i <= 8; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/dwr/dwr.pddl", "-p", "examples/pddl/dwr/pb" + i + ".pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testDinnerPDDLAll() {
		try {
			logger.info("Testing All Dinner date problems in PDDL");
			for (int i = 1; i <= 1; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/dinner/dinner.pddl", "-p", "examples/pddl/dinner/pb" + i + ".pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testGripperPDDLAll() {
		try {
			logger.info("Testing All Gripper problems in PDDL");
			for (int i = 1; i <= 4; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/gripper/gripper.pddl", "-p", "examples/pddl/gripper/pb" + i + ".pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testHanoiPDDLAll() {
		try {
			logger.info("Testing All Tower of Hanoi problems in PDDL");
			for (int i = 1; i <= 6; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/hanoi/hanoi.pddl", "-p", "examples/pddl/hanoi/pb" + i + ".pddl"});
		} catch (Exception e) {
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testLogisticsPDDLAll() {
		try {
			logger.info("Testing All Logistics problems in PDDL");
			for (int i = 1; i <= 2; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/logistics/logistics.pddl", "-p", "examples/pddl/logistics/pb" + i + ".pddl"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	@Test
	public void testRubiksPDDLAll() {
		try {
			logger.info("Testing All Rubik cube problems in PDDL");
			for (int i = 1; i <= 5; i++)
				Graphplan.main(new String[]{"-d", "examples/pddl/rubiks/rubiks.pddl", "-p", "examples/pddl/rubiks/pb" + i + ".pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	@Test
	public void testConstantsPDDL() {
		try {
			logger.info("Testing Constants parsing in PDDL");
			Graphplan.main(new String[]{"-d", "examples/pddl/constants/blocks.pddl", "-p", "examples/pddl/constants/pb1.pddl"});
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage(), false);
		}
	}

	//	@Test
	public void testPDDLAll() {
		logger.info("Testing All problems in PDDL, this may take a while");
		this.testBlocksWorldPDDLAll();
		this.testDockWorkerRobotsPDDLAll();
		this.testDinnerPDDLAll();
		this.testGripperPDDLAll();
		this.testHanoiPDDLAll();
		this.testLogisticsPDDLAll();
	}

	/**
	 * extractAllPossibleSolutions
	 */

	@Test
	public void testAllGripperPDDL0() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/gripper/gripper.pddl")
				.setProblemFilename("examples/pddl/gripper/pb1.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(0).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(2, planSolution.getNumberOfHighlevelPlans());
		assertEquals(8, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllGripperPDDL1() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/gripper/gripper.pddl")
				.setProblemFilename("examples/pddl/gripper/pb1.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(1).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(16, planSolution.getNumberOfHighlevelPlans());
		assertEquals(8, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllGripperPDDL2() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/gripper/gripper.pddl")
				.setProblemFilename("examples/pddl/gripper/pb1.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(2).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(112, planSolution.getNumberOfHighlevelPlans());
		assertEquals(248, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL10() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb1.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(0).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(1, planSolution.getNumberOfHighlevelPlans());
		assertEquals(1, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL11() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb1.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(1).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(5, planSolution.getNumberOfHighlevelPlans());
		assertEquals(1, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL20() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb2.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(0).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(6, planSolution.getNumberOfHighlevelPlans());
		assertEquals(6, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL21() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb2.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(1).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(42, planSolution.getNumberOfHighlevelPlans());
		assertEquals(6, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL22() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb2.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(2).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(258, planSolution.getNumberOfHighlevelPlans());
		assertEquals(96, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL30() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb3.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(0).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(2, planSolution.getNumberOfHighlevelPlans());
		assertEquals(2, planSolution.getNumberOfPlans());
	}

	@Test
	public void testAllDrinkAndDrivePDDL31() {
		Graphplan graphplan = new Graphplan.Builder().setDomainFilename("examples/pddl/drinkanddrive/drinkanddrive.pddl")
				.setProblemFilename("examples/pddl/drinkanddrive/pb3.pddl")
				.setExtractAllPossibleSolutions(true)
				.setExtractAllPossibleSolutionsWithMaxLength(1).build();
		PlanSolution planSolution = graphplan.getPlanSolution();
		assertNotNull(planSolution);
		assertEquals(16, planSolution.getNumberOfHighlevelPlans());
		assertEquals(6, planSolution.getNumberOfPlans());
	}
}