package graphplan.graph.algorithm;

import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import graphplan.graph.GraphLevel;

@SuppressWarnings("rawtypes")
public class SoluctionExtractionBitSetVisitor implements GraphElementVisitor {

	@Override
	public boolean visitElement(GraphElement element) {
		return false;
	}

	@Override
	public boolean visitGraphLevel(GraphLevel graphLevel) {
		return false;
	}
}
