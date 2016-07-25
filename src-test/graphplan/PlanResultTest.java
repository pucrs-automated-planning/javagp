package graphplan;

import graphplan.domain.Operator;
import graphplan.flyweight.OperatorFactory;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class PlanResultTest {

    @Test
    public void getAllPlanResults01() throws Exception {
        Stack<Set<Operator>> stack = new Stack<>();
        // Assert
        PlanResult planResult = new PlanResult(stack);
        Set<List<Operator>> allPlanResults = planResult.getAllPossibleSolutions();
        assertEquals(0, allPlanResults.size());
        Set<List<Operator>> expectedSolutions = new HashSet<>();
        assertEquals(expectedSolutions, allPlanResults);
    }

    @Test
    public void getAllPlanResults02() throws Exception {
        Stack<Set<Operator>> stack = new Stack<>();
        // First level
        Set<Operator> operators1 = new HashSet<>();
        Operator op11 = OperatorFactory.getInstance().createOperatorTemplate("move1", new String[]{"at a"}, new String[]{"at b"});
        operators1.add(op11);
        stack.add(operators1);
        // Assert
        PlanResult planResult = new PlanResult(stack);
        Set<List<Operator>> allPlanResults = planResult.getAllPossibleSolutions();
        assertEquals(1, allPlanResults.size());
        Set<List<Operator>> expectedSolutions = new HashSet<>(Collections.singletonList(Collections.singletonList(op11)));
        assertEquals(expectedSolutions, allPlanResults);
    }

    @Test
    public void getAllPlanResults03() throws Exception {
        Stack<Set<Operator>> stack = new Stack<>();
        // First level
        Set<Operator> operators1 = new HashSet<>();
        Operator op11 = OperatorFactory.getInstance().createOperatorTemplate("move1", new String[]{"at a"}, new String[]{"at b"});
        operators1.add(op11);
        Operator op12 = OperatorFactory.getInstance().createOperatorTemplate("add1",new String[]{"at a"}, new String[]{"at c"});
        operators1.add(op12);
        stack.add(operators1);
        // Assert
        PlanResult planResult = new PlanResult(stack);
        Set<List<Operator>> allPlanResults = planResult.getAllPossibleSolutions();
        assertEquals(2, allPlanResults.size());
        Set<List<Operator>> expectedSolutions = new HashSet<>(Arrays.asList(Arrays.asList(op11, op12), Arrays.asList(op12, op11)));
        assertEquals(expectedSolutions, allPlanResults);
    }

    @Test
    public void getAllPlanResults1() throws Exception {
        Stack<Set<Operator>> stack = new Stack<>();
        // First level
        Set<Operator> operators1 = new HashSet<>();
        Operator op11 = OperatorFactory.getInstance().createOperatorTemplate("move1", new String[]{"at a"}, new String[]{"at b"});
        operators1.add(op11);
        Operator op12 = OperatorFactory.getInstance().createOperatorTemplate("add1",new String[]{"at a"}, new String[]{"at c"});
        operators1.add(op12);
        stack.add(operators1);
        // Second level
        Set<Operator> operators2 = new HashSet<>();
        Operator op21 = OperatorFactory.getInstance().createOperatorTemplate("move2",new String[]{"at a"}, new String[]{"at b"});
        operators2.add(op21);
        Operator op22 = OperatorFactory.getInstance().createOperatorTemplate("add2",new String[]{"at a"}, new String[]{"at c"});
        operators2.add(op22);
        stack.add(operators2);
        // Assert
        PlanResult planResult = new PlanResult(stack);
        Set<List<Operator>> allPlanResults = planResult.getAllPossibleSolutions();
        assertEquals(4, allPlanResults.size());
        Set<List<Operator>> expectedSolutions = new HashSet<>(
                Arrays.asList(Arrays.asList(op11, op12, op21, op22),
                        Arrays.asList(op11, op12, op22, op21),
                        Arrays.asList(op12, op11, op21, op22),
                        Arrays.asList(op12, op11, op22, op21)));
        assertEquals(expectedSolutions, allPlanResults);
    }

    @Test
    public void getAllPlanResults2() throws Exception {
        Stack<Set<Operator>> stack = new Stack<>();
        // First level
        Set<Operator> operators1 = new HashSet<>();
        Operator op11 = OperatorFactory.getInstance().createOperatorTemplate("move1", new String[]{"at a"}, new String[]{"at b"});
        operators1.add(op11);
        stack.add(operators1);
        // Second level
        Set<Operator> operators2 = new HashSet<>();
        Operator op21 = OperatorFactory.getInstance().createOperatorTemplate("move2",new String[]{"at a"}, new String[]{"at b"});
        operators2.add(op21);
        Operator op22 = OperatorFactory.getInstance().createOperatorTemplate("add2",new String[]{"at a"}, new String[]{"at c"});
        operators2.add(op22);
        stack.add(operators2);
        // Assert
        PlanResult planResult = new PlanResult(stack);
        Set<List<Operator>> allPlanResults = planResult.getAllPossibleSolutions();
        assertEquals(2, allPlanResults.size());
        Set<List<Operator>> expectedSolutions = new HashSet<>(Arrays.asList(Arrays.asList(op11, op21, op22), Arrays.asList(op11, op22, op21)));
        assertEquals(expectedSolutions, allPlanResults);
    }

    @Test
    public void getAllPlanResults3() throws Exception {
        Stack<Set<Operator>> stack = new Stack<>();
        // First level
        Set<Operator> operators1 = new HashSet<>();
        Operator op11 = OperatorFactory.getInstance().createOperatorTemplate("move1", new String[]{"at a"}, new String[]{"at b"});
        operators1.add(op11);
        Operator op12 = OperatorFactory.getInstance().createOperatorTemplate("add1",new String[]{"at a"}, new String[]{"at c"});
        operators1.add(op12);
        stack.add(operators1);
        // Second level
        Set<Operator> operators2 = new HashSet<>();
        Operator op21 = OperatorFactory.getInstance().createOperatorTemplate("move2",new String[]{"at a"}, new String[]{"at b"});
        operators2.add(op21);
        Operator op22 = OperatorFactory.getInstance().createOperatorTemplate("add2",new String[]{"at a"}, new String[]{"at c"});
        operators2.add(op22);
        stack.add(operators2);
        // Third level
        Set<Operator> operators3 = new HashSet<>();
        Operator op31 = OperatorFactory.getInstance().createOperatorTemplate("move3",new String[]{"at a"}, new String[]{"at b"});
        operators3.add(op31);
        Operator op32 = OperatorFactory.getInstance().createOperatorTemplate("add3",new String[]{"at a"}, new String[]{"at c"});
        operators3.add(op32);
        stack.add(operators3);
        // Assert
        PlanResult planResult = new PlanResult(stack);
        Set<List<Operator>> allPlanResults = planResult.getAllPossibleSolutions();
        assertEquals(8, allPlanResults.size());
        Set<List<Operator>> expectedSolutions = new HashSet<>(Arrays.asList(
                Arrays.asList(op11, op12, op21, op22, op31, op32), Arrays.asList(op11, op12, op21, op22, op32, op31),
                Arrays.asList(op11, op12, op22, op21, op31, op32), Arrays.asList(op11, op12, op22, op21, op32, op31),
                Arrays.asList(op12, op11, op21, op22, op31, op32), Arrays.asList(op12, op11, op21, op22, op32, op31),
                Arrays.asList(op12, op11, op22, op21, op31, op32), Arrays.asList(op12, op11, op22, op21, op32, op31)));
        assertEquals(expectedSolutions, allPlanResults);
    }

}