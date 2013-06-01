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
package graphplan.graph.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import graphplan.graph.GraphLevel;
import graphplan.graph.planning.PlanningGraph;

public class XMLExporterVisitor implements GraphElementVisitor {
	protected Document document;
	

	public boolean visitElement(GraphElement element) {
		if(element instanceof PlanningGraph) {
			PlanningGraph planningGraph = (PlanningGraph) element;
			DocumentBuilder builder;
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return false;
			}
			document = builder.newDocument();
			
		}
		return false;
	}

	public boolean visitGraphLevel(GraphLevel graphLevel) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void addPlanningGraphElement(PlanningGraph planningGraph) {
		Element element = document.createElement("planning-graph");
		
	}

	/**
	 * Returns the DOM representation of the previously visited graph
	 * @return
	 */
	public Document getGraphDocument() {
		return document;
	}
}
