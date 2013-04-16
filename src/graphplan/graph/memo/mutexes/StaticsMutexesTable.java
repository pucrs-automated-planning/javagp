package graphplan.graph.memo.mutexes;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StaticsMutexesTable {

	private HashMap<String, HashMap<String, List<MutextCondition>>> mutexesTable = new HashMap<String, HashMap<String,List<MutextCondition>>>();
	private HashMap<String, Set<String>> table = new HashMap<String, Set<String>>();
	
	public StaticsMutexesTable(List<Operator> operators){
		for(Operator op1: operators){
			for(Operator op2: operators){

				for(Proposition e1: op1.getEffects()){
					for(Proposition e2: op2.getEffects()){
						if(!e1.unifies(e2)){
							if(e1.getSignature().equals(e2.getSignature()) && (e1.negated() && !e2.negated() || e2.negated() && !e1.negated())){
								//System.out.println("\nEffects-> " + op1 + "         " + op2);
								//System.out.println(e1);
								//System.out.println(e2);

								HashMap<String, List<MutextCondition>> mutexOp = this.mutexesTable.get(op1.getFunctor());
								if(mutexOp == null) mutexOp = new HashMap<String, List<MutextCondition>>();

								List<MutextCondition> conditionOp = mutexOp.get(op2.getFunctor());
								if(conditionOp == null) conditionOp = new ArrayList<MutextCondition>();

								Iterator itE1 = e1.getTerms().iterator();
								MutextCondition m1 = new MutextCondition();
								while(itE1.hasNext()){
									Term t = (Term) itE1.next();
									m1.getOp1Parameters().add(op1.getTerms().indexOf(t));
									//System.out.println(op1.getTerms().indexOf(t));
								}

								Iterator itE2 = e2.getTerms().iterator();
								while(itE2.hasNext()){
									Term t = (Term) itE2.next();
									m1.getOp2Parameters().add(op2.getTerms().indexOf(t));
									//System.out.println(op2.getTerms().indexOf(t));
								}

								conditionOp.add(m1);
								mutexOp.put(op2.getFunctor(), conditionOp);
								this.mutexesTable.put(op1.getFunctor(), mutexOp);
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

	public HashMap<String, HashMap<String, List<MutextCondition>>> getMutexesTable() {
		return mutexesTable;
	}

	public boolean isMutex(Operator op1, Operator op2) {
		return false;
	}
}
