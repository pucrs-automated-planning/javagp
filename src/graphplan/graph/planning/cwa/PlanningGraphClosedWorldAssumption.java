package graphplan.graph.planning.cwa;

import graphplan.graph.PropositionLevel;
import graphplan.graph.memo.mutexes.StaticMutexesTable;
import graphplan.graph.planning.PlanningGraph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanningGraphClosedWorldAssumption extends PlanningGraph {

	private static final long serialVersionUID = 5132706449924120949L;

	public PlanningGraphClosedWorldAssumption(PropositionLevel initialLevel, Map<String, Set<String>> types, Map<String, List<String>> parameterTypes, StaticMutexesTable staticsMutexesTable) {
		super(initialLevel, types, parameterTypes, staticsMutexesTable);
	}
}