package graphplan.graph.planning.cwa;

import graphplan.graph.ActionLevel;
import graphplan.graph.GraphLevel;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.impl.LevelGeneratorClosedWorldAssumptionImpl;
import graphplan.graph.memo.mutexes.StaticMutexesTable;
import graphplan.graph.planning.PlanningGraph;
import graphplan.graph.planning.PlanningGraphException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlanningGraphClosedWorldAssumption extends PlanningGraph {

	private static final long serialVersionUID = 5132706449924120949L;
	
	private LevelGeneratorClosedWorldAssumptionImpl actionLevelGeneratorCwa;
	private boolean closedWorldAssumption = false;

	public PlanningGraphClosedWorldAssumption(PropositionLevel initialLevel, Map<String, Set<String>> types, Map<String, List<String>> parameterTypes, StaticMutexesTable staticsMutexesTable) {
		super(initialLevel, types, parameterTypes, staticsMutexesTable);
		this.actionLevelGeneratorCwa = new LevelGeneratorClosedWorldAssumptionImpl(types, parameterTypes);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void expandGraph() throws PlanningGraphException {
		PropositionLevel lastLevel = (PropositionLevel) getLastGraphLevelCwa();
		//First we create a new action level from the last proposition level
		
		PropositionLevel initialState = (PropositionLevel) this.getGraphLevel(0);
		int initialStateSize = initialState.size();
		ActionLevel actionLevel = this.actionLevelGeneratorCwa.createNextActionLevel(lastLevel, initialState);
		
		if(initialStateSize != initialState.size()) {
			System.out.println("-> Initial State was changed: Maintenance Actions and Mutexes from Level 0 until current Level");
			this.graphLevels = new ArrayList<GraphLevel>();
			this.graphLevels.add(initialState);
			this.closedWorldAssumption = true;
			this.expandGraph();
		}
		this.addGraphLevel(actionLevel);
		this.setIndexForOperators(actionLevel);
		//Then we add the action mutexes for these actions
		this.mutexGenerator.addActionMutexes(lastLevel, actionLevel);
		//And then add the subsequent proposition level
		PropositionLevel propositionLevel = this.propositionLevelGenerator.createNextPropositionLevel(actionLevel);
		this.addGraphLevel(propositionLevel);
		this.setIndexForPropositions(propositionLevel);
		//Finally adding the proposition mutexes
		this.mutexGenerator.addPropositionMutexes(actionLevel, propositionLevel);
	}
	
	@SuppressWarnings("rawtypes")
	public GraphLevel getLastGraphLevelCwa() {
		if(this.closedWorldAssumption){
			this.closedWorldAssumption = false;
			return this.graphLevels.get(0);
		}
		
		if (this.graphLevels.size() > 0) {
			return this.graphLevels.get(this.graphLevels.size() - 1);
		} else {
			return null;
		}
	}
}