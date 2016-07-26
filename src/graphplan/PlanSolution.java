package graphplan;

import graphplan.domain.Operator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A class encapsulating the result of planning. While PlanResult represents a single high-level plan, this class represents a set of possible plans.
 *
 * @author Guilherme Krzisch
 */
public class PlanSolution {
	private Set<PlanResult> results;
	private Set<List<Operator>> allPlans; // Different results can have same List<Operator> plan, therefore we are using a Set

	public PlanSolution() {
		this(new HashSet<>());
	}

	public PlanSolution(Set<PlanResult> results) {
		this.results = results;
	}

	public void add(PlanResult planResult) {
		results.add(planResult);
		if (allPlans != null) {
			allPlans.clear();
		}
	}

	public void clear() {
		results.clear();
		if (allPlans != null) {
			allPlans.clear();
		}
	}

	public int getNumberOfPlans() {
		return getAllPlans().size();
	}

	public int getNumberOfHighlevelPlans() {
		return results.size();
	}

	public Set<List<Operator>> getAllPlans() {
		if (allPlans == null) {
			allPlans = new HashSet<>();
			for (PlanResult result : results) {
				allPlans.addAll(result.getAllPossibleSolutions());
			}
		}
		return allPlans;
	}

	public Set<PlanResult> getAllHighlevelPlans() {
		return results;
	}
}
