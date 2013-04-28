package graphplan.graph.memo.mutexes;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StaticsMutexesTable {

	private HashMap<String, HashMap<String, List<MutexCondition>>> mutexesTable = new HashMap<String, HashMap<String,List<MutexCondition>>>();
	private HashMap<String, Set<String>> tableMutexesHits = new HashMap<String, Set<String>>();
	
	public StaticsMutexesTable(List<Operator> operators){
		this.initializeConstructor(operators);
	}

	private void initializeConstructor(List<Operator> operators){
		
		operators.addAll(this.getNoops(operators));
		
		for(Operator op1: operators){
			for(Operator op2: operators){
				
				if(op1.getSignature().equals(op2.getSignature())) continue;	
				for(Proposition e1: op1.getEffects()){
					
					// check for inconsistent effects
					for(Proposition e2: op2.getEffects()){
						if(!e1.unifies(e2)){
							//if they do not unify check if they are equal
							if(e1.getSignature().equals(e2.getSignature()) ){
//								System.out.println("\nEffects-> " + op1 + "         " + op2);
//								System.out.println("effect op1: "+ e1);
//								System.out.println("effect op2: "+ e2);
								this.populateStaticMutexTable(e1, e2, op1, op2);
							}
						}
					}
					
					// now check for interference
					for (Proposition p1 : op2.getPreconds()) {
						if(!e1.unifies(p1)){
							//if they do not unify check if they are equal
							if(e1.getSignature().equals(p1.getSignature())){
//								System.out.println("\nPrecond of: " + op2 + " - Effect of: " + op1);
//								System.out.println("precond op2: "+ p1);
//								System.out.println("effect op1: "+e1);
								this.populateStaticMutexTable(e1, p1, op1, op2);
							}
						}
					}
					
				}
				
			}
		}
		//for(Operator o: operators){
		//	System.out.println(o.getFunctor() + " -> " + this.mutexesTable.get(o.getFunctor()));
		//}
	}
	
	
	@SuppressWarnings("rawtypes")
	private void populateStaticMutexTable(Proposition p1, Proposition p2, Operator op1, Operator op2){
		HashMap<String, List<MutexCondition>> mutexOp = this.mutexesTable.get(op1.getFunctor());
		if(mutexOp == null) mutexOp = new HashMap<String, List<MutexCondition>>();

		List<MutexCondition> conditionOp = mutexOp.get(op2.getFunctor());
		if(conditionOp == null) conditionOp = new ArrayList<MutexCondition>();

		
		MutexCondition mutexCond = new MutexCondition();
		
		Iterator itE1 = p1.getTerms().iterator();
		while(itE1.hasNext()){
			Term t = (Term) itE1.next();
			mutexCond.addOp1Parameter(op1.getTerms().indexOf(t));
//			System.out.println(op1.getTerms().indexOf(t));
		}

		Iterator itE2 = p2.getTerms().iterator();
		while(itE2.hasNext()){
			Term t = (Term) itE2.next();
			mutexCond.addOp2Parameter(op2.getTerms().indexOf(t));
			//System.out.println(op2.getTerms().indexOf(t));
		}

		conditionOp.add(mutexCond);
		mutexOp.put(op2.getFunctor(), conditionOp);
		this.mutexesTable.put(op1.getFunctor(), mutexOp);
	}

	private Set<Operator> getNoops(List<Operator> operators){
		Set<Operator> noops = new HashSet<Operator>();
		OperatorFactory opFac = OperatorFactory.getInstance();
		
		for (Operator operator : operators) {
			for (Proposition effect : operator.getEffects()) {
				noops.add(opFac.getNoop(effect));
			}
			for (Proposition precond : operator.getPreconds()) {
				noops.add(opFac.getNoop(precond));
			}
		}
		return noops;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isMutex(Operator op1, Operator op2) {
		HashMap<String, List<MutexCondition>> hashOp1 = this.mutexesTable.get(op1.getFunctor());
		
		if (hashOp1 != null ) {
			Set<String> mutexes = this.tableMutexesHits.get(op1.toString());; 
			if(mutexes != null){
				if(mutexes.contains(op2.toString())) {
					return true;
				}
			}

			List<MutexCondition> mutexConditions = hashOp1.get(op2.getFunctor());
			if(mutexConditions != null){
				for (MutexCondition mutextCondition : mutexConditions) {
					if (mutextCondition.verifyConditionsByIndexes(op1.getTerms(), op2.getTerms())) {
						if(mutexes == null) {
							mutexes = new HashSet<String>();
							this.tableMutexesHits.put(op1.toString(), mutexes);
						}
						mutexes.add(op2.toString());
						return true;
					}
				}
			}
		}
		return false;
	}
}