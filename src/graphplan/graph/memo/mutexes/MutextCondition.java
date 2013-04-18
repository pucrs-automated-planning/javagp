package graphplan.graph.memo.mutexes;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

public class MutextCondition {

	private List<Integer> op1Parameters;
	private List<Integer> op2Parameters;
	
	public MutextCondition(){
		this.op1Parameters = new ArrayList<Integer>();
		this.op2Parameters = new ArrayList<Integer>();
	}
	
	public void addOp1Parameter(int index){
		this.op1Parameters.add(index);
	}
	
	public void addOp2Parameter(int index){
		this.op2Parameters.add(index);
	}
	
	public boolean verifyConditionsByIndexes(List<Term> termsOp1, List<Term> termsOp2){
		for (int i = 0; i < op1Parameters.size(); i++) {
			if(!termsOp1.get(op1Parameters.get(i)).toString().equals(termsOp2.get(op2Parameters.get(i)).toString())){
				return false;
			}
		}
		return true;
	}
}