/*
 * ---------------------------------------------------------------------------
 * Copyright (C) 2010  Felipe Meneguzzi
 * JavaGP is distributed under LGPL. See file LGPL.txt in this directory.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * To contact the author:
 * http://www.meneguzzi.eu/felipe/contact.html
 * ---------------------------------------------------------------------------
 */
package graphplan.graph.draw;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.graph.*;
import graphplan.graph.planning.PlanningGraph;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class GraphDrawVisitor implements GraphElementVisitor {

	protected Document graphDoc;
	protected Element graphElement;

	public GraphDrawVisitor() throws Exception {
		try {
			graphDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			initialize();
		} catch (ParserConfigurationException e) {
			throw new Exception(e);
		}
	}

	protected void initialize() {
		Element element = graphDoc.createElement("graphml");
		graphDoc.appendChild(element);
		graphElement = graphDoc.createElement("graph");
		element.appendChild(graphElement);
		graphElement.setAttribute("isDirected", "true");
		graphElement.setAttribute("isAcyclic", "true");
	}

	@SuppressWarnings("unchecked")
	public boolean visitElement(GraphElement element) {
		if (element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			for (int i = 0; i < planningGraph.size(); i++) {
				this.visitGraphLevel(planningGraph.getGraphLevel(i));
			}
		}
		return true;
	}

	public boolean visitGraphLevel(GraphLevel graphLevel) {
		if (graphLevel.isPropositionLevel()) {
			this.visitPropositionLevel((PropositionLevel) graphLevel);
		} else {
			this.visitActionLevel((ActionLevel) graphLevel);
		}

		return true;
	}

	public boolean visitActionLevel(ActionLevel actionLevel) {
		Comment comment = graphDoc.createComment("Action Level " + actionLevel.getIndex());
		graphElement.appendChild(comment);
		for (Iterator<Operator> iter = actionLevel.getActions(); iter.hasNext(); ) {
			Operator operator = iter.next();

			String label = operator.getSignature();
			String id = actionLevel.getIndex() + label;

			Element nodeElement = createNode(id, label);
			graphElement.appendChild(nodeElement);

			for (Proposition prop : operator.getPreconds()) {
				String target = (actionLevel.getIndex() - 1) + prop.getSignature();
				Element edgeElement = createEdge(id, target);
				graphElement.appendChild(edgeElement);
			}

			for (Proposition prop : operator.getEffects()) {
				String target = (actionLevel.getIndex() + 1) + prop.getSignature();
				Element edgeElement = createEdge(id, target);
				graphElement.appendChild(edgeElement);
			}
		}
		return true;
	}

	public boolean visitPropositionLevel(PropositionLevel propositionLevel) {
		Comment comment = graphDoc.createComment("Proposition Level " + propositionLevel.getIndex());
		graphElement.appendChild(comment);
		for (Iterator<Proposition> iter = propositionLevel.getPropositions(); iter.hasNext(); ) {
			Proposition proposition = iter.next();

			String label = proposition.getSignature();
			String id = propositionLevel.getIndex() + label;

			Element nodeElement = createNode(id, label);
			graphElement.appendChild(nodeElement);
		}
		return true;
	}

	protected Element createNode(String id, String label) {
		Element nodeElement = graphDoc.createElement("node");
		nodeElement.setAttribute("id", id);

		Element labelElement = graphDoc.createElement("label");
		Text labelText = graphDoc.createTextNode(label);
		labelElement.appendChild(labelText);

		nodeElement.appendChild(labelElement);
		return nodeElement;
	}

	protected Element createEdge(String source, String target) {
		Element edgeElement = graphDoc.createElement("edge");
		edgeElement.setAttribute("source", source);
		edgeElement.setAttribute("target", target);

		return edgeElement;
	}

	public String toString() {
		StringWriter stringWriter = new StringWriter();

		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(graphDoc);
			StreamResult result = new StreamResult(stringWriter);
			transformer.setOutputProperty("indent", "yes");
			transformer.transform(source, result);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringWriter.toString();
	}
}
