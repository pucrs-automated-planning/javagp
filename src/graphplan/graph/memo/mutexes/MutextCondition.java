package graphplan.graph.memo.mutexes;

import java.util.ArrayList;
import java.util.List;


public class MutextCondition {

	private List<Integer> op1Parameters;
	private List<Integer> op2Parameters;
	
	public MutextCondition(){
		this.op1Parameters = new ArrayList<Integer>();
		this.op2Parameters = new ArrayList<Integer>();
	}

	public List<Integer> getOp1Parameters() {
		return op1Parameters;
	}

	public void setOp1Parameters(List<Integer> op1Parameters) {
		this.op1Parameters = op1Parameters;
	}

	public List<Integer> getOp2Parameters() {
		return op2Parameters;
	}

	public void setOp2Parameters(List<Integer> op2Parameters) {
		this.op2Parameters = op2Parameters;
	}
}