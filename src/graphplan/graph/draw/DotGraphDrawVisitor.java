/*

 */
package graphplan.graph.draw;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.*;
import graphplan.graph.planning.PlanningGraph;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;

/**
 * @author meneguzzi
 *
 */
public class DotGraphDrawVisitor implements GraphElementVisitor {
	
	private PrintWriter writer;
	private StringWriter stringWriter;

	public DotGraphDrawVisitor() {
		stringWriter = new StringWriter();
		writer = new PrintWriter(stringWriter);
		initialize();
	}
	
	protected void initialize() {
		writer.println("digraph g{");
//		writer.println("orientation = landscape;");
		
	}

	@SuppressWarnings("unchecked")
	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			for (int i=0; i<planningGraph.size(); i++) {
				this.visitGraphLevel(planningGraph.getGraphLevel(i));
			}
		}
		return true;
	}

	public boolean visitGraphLevel(GraphLevel graphLevel) {
		if(graphLevel.isPropositionLevel()) {
			this.visitPropositionLevel((PropositionLevel) graphLevel);
		} else {
			this.visitActionLevel((ActionLevel) graphLevel);
		}
		
		return true;
	}

	public boolean visitActionLevel(ActionLevel actionLevel) {
		//Find a way to write this to DOT
		writer.println("subgraph cluster_action"+actionLevel.getIndex()+" {");
		String comment = "Action Level "+actionLevel.getIndex();
		writer.println("label=\""+comment+"\";");
		writer.println("rankdir=TB;");
		for (Iterator<Operator> iter = actionLevel.getActions(); iter.hasNext();) {
			Operator operator = iter.next();
			
			String label = operator.getSignature();
			String id = actionLevel.getIndex()+label;
			this.createNode(id, label,"box");
			
//			for(Iterator<Proposition> iterPre = operator.getPreconds().iterator(); iterPre.hasNext(); ){
//				Proposition prop = iterPre.next();
//				String target = (actionLevel.getIndex()-1)+prop.getSignature();
//				createEdge(target,id);
//			}
//			
//			for(Iterator<Proposition> iterEff = operator.getEffects().iterator(); iterEff.hasNext(); ){
//				Proposition prop = iterEff.next();
//				String target = (actionLevel.getIndex()+1)+prop.getSignature();
//				createEdge(id, target);
//			}
		}
		writer.println("}");
		for (Iterator<Operator> iter = actionLevel.getActions(); iter.hasNext();) {
			Operator operator = iter.next();
			
			String label = operator.getSignature();
			String id = actionLevel.getIndex()+label;
//			this.createNode(id, label,"box");

			for (Proposition prop : operator.getPreconds()) {
				String target = (actionLevel.getIndex() - 1) + prop.getSignature();
				createEdge(target, id);
			}

			for (Proposition prop : operator.getEffects()) {
				String target = (actionLevel.getIndex() + 1) + prop.getSignature();
				createEdge(id, target);
			}
		}
		return true;
	}
	
	public boolean visitPropositionLevel(PropositionLevel propositionLevel) {
		//Find a way to print this to DOT
		writer.println("subgraph cluster_proposition"+propositionLevel.getIndex()+" {");
		String comment = "Proposition Level "+propositionLevel.getIndex();
		writer.println("label=\""+comment+"\";");
		writer.println("rankdir=TB;");
//		writer.println("a"+propositionLevel.getIndex()+" -> b"+propositionLevel.getIndex());
		
		for (Iterator<Proposition> iter = propositionLevel.getPropositions(); iter.hasNext();) {
			Proposition proposition = iter.next();
			
			String label = proposition.getSignature();
			String id = propositionLevel.getIndex()+label;
			
			createNode(id, label,"ellipse");
		}
		writer.println("}");
		return true;
	}
	
	protected void createNode(String id, String label, String shape) {
		writer.println("\""+id+"\" [shape="+shape+",label=\""+label+"\"];");
	}
	
	protected void createEdge(String source, String target) {
		writer.println("\""+source+"\" -> \""+target+"\";");
	}
	
	public String toString() {
		writer.println("}");
		return stringWriter.toString();
	}

}
