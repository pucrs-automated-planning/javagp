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
package graphplan.flyweight;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
import graphplan.graph.GraphLevel;
import graphplan.graph.PropositionLevel;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A factory class to be used in the creation and maintenance of
 * operators for our planning system using the flyweight pattern.
 * XXX It may have to be modified if multiple instances of the planner are to be used 
 * @author Felipe Meneguzzi
 *
 */
public class OperatorFactory {
	public static final String NOOP_FUNCTOR = "noop";
	
	private static OperatorFactory operatorFactory = null;
	
	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;
	
	private boolean usingTypes = false;
	
	/**
	 * Returns the singleton <code>OperatorFactory</code> instance.
	 * @return
	 */
	public static OperatorFactory getInstance() {
		if(operatorFactory == null) {
			operatorFactory = new OperatorFactory();
		}
		
		return operatorFactory;
	}
	
	protected Hashtable<String, OperatorImpl> operatorInstances;
	protected Hashtable<String, OperatorImpl> operatorTemplates;
	
	public OperatorFactory() {
		this.operatorInstances = new Hashtable<String, OperatorImpl>();
		this.operatorTemplates = new Hashtable<String, OperatorImpl>();
	}
	
	public Operator createOperatorTemplate(String signature, String[] preconds, String[] effects) {
		Structure oper = Structure.parse(signature);
		ArrayList<Proposition> precondProps = new ArrayList<Proposition>(preconds.length);
		ArrayList<Proposition> effectProps = new ArrayList<Proposition>(effects.length);
		
		for (int i = 0; i < preconds.length; i++) {
			PropositionImpl precond = new PropositionImpl(preconds[i]);
			precondProps.add(precond);
		}
		
		for (int i = 0; i < effects.length; i++) {
			PropositionImpl effect = new PropositionImpl(effects[i]);
			effectProps.add(effect);
		}
		
		OperatorImpl operator = new OperatorImpl(oper, precondProps, effectProps);
		
		return operator;
	}
	
	public void addOperatorTemplate(Operator operator) throws OperatorFactoryException {
		//We check if the operator has no arguments, or if it is not ground
		//Operators with arguments should not be ground
		if(operator.getTerms() == null || operator.getTerms().size() == 0 || !operator.isGround()) {
			//TODO this cast to OperatorImpl has to be reviewed, since it violates the Bridge pattern 
			this.operatorTemplates.put(operator.getOperatorIndicator(), (OperatorImpl)operator);
		} else {
			throw new OperatorFactoryException("Operator "+operator+" is not ground");
		}
	}
	
	public static void reset() {
		operatorFactory.resetOperatorFactory();
	}
	
	/**
	 * Resets the operator factory so that it is detached from all flyweight
	 * operators created so far. Invoke this method with caution.
	 *
	 */
	protected void resetOperatorFactory() {
		this.operatorInstances.clear();
		this.operatorTemplates.clear();
	}
	
	/**
	 * Resets the operator templates from this operator factory,
	 * this method is relevant when operating with multiple
	 * domains being used at the same time.
	 *
	 */
	public void resetOperatorTemplates() {
		this.operatorTemplates.clear();
	}
	
	/**
	 * Creates a noop action to propagate the supplied proposition between two proposition
	 * levels.
	 * @param proposition
	 */
	public Operator getNoop(Proposition proposition) {
		String noopSignature = NOOP_FUNCTOR+"_";
		if(proposition.negated()) {
			noopSignature+="not_"+(proposition.toString().substring(1));
		} else {
			noopSignature+=proposition.toString();
		}
		if(operatorInstances.containsKey(noopSignature)) {
			return operatorInstances.get(noopSignature);
		} else {
			List<Proposition> precondsEffects = new ArrayList<Proposition>(1);
			precondsEffects.add(proposition);
			Structure signature = Structure.parse(noopSignature);
			OperatorImpl operator = new OperatorImpl(signature, precondsEffects, precondsEffects);
			this.operatorInstances.put(noopSignature, operator);
			return operator;
		}
	}

	/**
	 * Returns a fully instantiated operator.
	 * 
	 * @param operatorSignature
	 * @return
	 * @throws OperatorFactoryException
	 */
	@SuppressWarnings("rawtypes")
	public Operator getOperator(String operatorSignature) throws OperatorFactoryException {
		//If this operator has been used before, retrieve it from the flyweights
		if(operatorInstances.containsKey(operatorSignature)) {
			return operatorInstances.get(operatorSignature);
		} else {
			//Otherwise, we have to create a new one from a template
			Structure signature = Structure.parse(operatorSignature);
			//If a template exists matching the supplied indicator
			if(this.operatorTemplates.containsKey(signature.getPredicateIndicator().toString())) {
				//We get the template
				OperatorImpl template = (OperatorImpl) this.operatorTemplates.get(signature.getPredicateIndicator().toString());
				//And start copying it
				Structure templateSignature = new Structure(template);
				Unifier un = new Unifier();
				//The signature should unify with the template
				if(!un.unifies(signature, templateSignature)) {
					//This should not happen in a properly described domain
					throw new OperatorFactoryException("Failed to unify "+templateSignature+" with operator "+templateSignature+" instantiating "+operatorSignature);
				}
				templateSignature.apply(un);
				PropositionFactory propositionFactory = PropositionFactory.getInstance();
				
				//Then get the preconditions in the template
				List<Proposition> templatePreconds = template.getPreconds();
				List<Proposition> concretePreconds = new ArrayList<Proposition>(templatePreconds.size());
				//And apply the unifier to them
				for (Iterator iter = templatePreconds.iterator(); iter
						.hasNext();) {
					PropositionImpl precond = (PropositionImpl) iter.next();
					Literal literal = new LiteralImpl(precond);
					literal.apply(un);
					PropositionImpl concretePrecond = (PropositionImpl)propositionFactory.getProposition(literal.toString());
					// XXX Uncomment the code to debug operator instantiation, it is removed to avoid wasting time here
//					if(!concretePrecond.isGround()) {
//						//throw new OperatorFactoryException("We have a non-concrete precond in operator "+signature+": "+concretePrecond);
//						System.err.println("We have a non-concrete precond in operator "+signature+": "+concretePrecond);
//					}
					concretePreconds.add(concretePrecond);
				}
				
				List<Proposition> templateEffects = template.getEffects();
				List<Proposition> concreteEffects = new ArrayList<Proposition>(templateEffects.size());
				
				for (Iterator iter = templateEffects.iterator(); iter
						.hasNext();) {
					PropositionImpl effect = (PropositionImpl) iter.next();
					Literal literal = new LiteralImpl(effect);
					literal.apply(un);
					PropositionImpl concreteEffect = (PropositionImpl)propositionFactory.getProposition(literal.toString());
					// XXX Uncomment the code to debug operator instantiation, it is removed to avoid wasting time here
//					if(!concreteEffect.isGround()) {
//						//throw new OperatorFactoryException("We have a non-concrete effect in operator "+signature+": "+concreteEffect);
//						System.err.println("We have a non-concrete effect in operator "+signature+": "+concreteEffect);
//					}
					concreteEffects.add(concreteEffect);
				}
				
				OperatorImpl operatorImpl = new OperatorImpl(templateSignature, concretePreconds, concreteEffects);
				this.operatorInstances.put(operatorImpl.getSignature(), operatorImpl);
				return operatorImpl;
			} else {
				throw new OperatorFactoryException("No operator template for "+operatorSignature);
			}
			//return null;
		}
	}
	
	public List<Operator> getRequiringOperatorTemplates(Proposition precond) {
		List<Operator> templates = new ArrayList<Operator>();
		//Scan every operator template
		for(Enumeration<OperatorImpl> e = operatorTemplates.elements(); e.hasMoreElements(); ) {
			OperatorImpl oper = e.nextElement();
			//if the parameter is null or a true proposition
			//return any empty preconditions operator
			if(precond == null) {
				if(oper.getPreconds().isEmpty()) {
					templates.add(oper);
				}
				continue;
			}			
			//or trying to unify the supplied precondition with any of the preconditions
			for (Proposition oPrecond : oper.getPreconds()) {
				if(precond.unifies(oPrecond)) {
					templates.add(oper);
				}
			}
		}
		return templates;
	}
	
	public List<Operator> getCausingOperatorsTemplates(Proposition effect) {
		List<Operator> templates = new ArrayList<Operator>();
		//Scan every operator template
		for(Enumeration<OperatorImpl> e = operatorTemplates.elements(); e.hasMoreElements(); ) {
			OperatorImpl oper = e.nextElement();
			//and trying to unify the supplied effect with any of the effects
			for (Proposition oEffect : oper.getEffects()) {
				if(effect.unifies(oEffect)) {
					templates.add(oper);
				}
			}
		}
		return templates;
	}
	
	public Set<Operator> getAllPossibleInstantiations(List<Operator> operators, List<Proposition> preconds) throws OperatorFactoryException {
		return this.getAllPossibleInstantiations(operators, preconds, null);
	}
	
	/**
	 * Returns all possible instantiations of the supplied operators, given
	 * the supplied preconditions.
	 * 
	 * @param operators
	 * @param propositions
	 * @return
	 * @throws OperatorFactoryException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Set<Operator> getAllPossibleInstantiations(List<Operator> operators, List<Proposition> preconds, GraphLevel initialState) throws OperatorFactoryException {
		final Set<Operator> instances = new HashSet<Operator>();
		final Set<Term> terms = getAllPossibleTerms(preconds);
		//For each operator template 
		//We need to instantiate it in every possible way
		//allowed by the propositions
		for(Operator operator : operators) {
			//If this operator has no parameters, there is only one way
			//to instantiate it
			if(operator.getTerms() == null) {
				instances.add(getOperator(operator.getSignature()));
			} else {
				int size = operator.getTerms().size();
				//Otherwise, we have to come up with all possible combinations
				//of parameters
				Term termInstances[] = null;
				
				for(TermInstanceIterator it = new TermInstanceIterator(terms, size);
					it.hasNext(); ){
					termInstances = it.next();
					
					//Never allow different variables to have the same value
					boolean addInstance = true;
					for(int i=0; addInstance && i<termInstances.length; i++) {
						for(int j=i+1; j<termInstances.length; j++) {
							if(termInstances[i]==termInstances[j]) {
								addInstance = false;
								break;
							}
						}
					}
					
					if(!addInstance) {
						continue;
					}
					
					if(this.usingTypes){
						List<String> pTypes = this.parameterTypes.get(operator.getFunctor());
						int n = pTypes.size();
						addInstance = true;
						
						for(int i=0; i<n; i++){
							Set<String> tTypes = this.types.get(pTypes.get(i));
							if(tTypes == null || !tTypes.contains(((Atom)termInstances[i]).getFunctor())) {
								addInstance = false;
								break;
							}
						}

						if(!addInstance) continue;
					}
					
					final OperatorImpl copy = (OperatorImpl) operator.clone();
					Structure struct = new Structure(copy.getFunctor());
					for(Term term : termInstances) {
						struct.addTerm(term);
					}
					
					Unifier unifier = new Unifier();
					if(unifier.unifies(copy, struct)) {
						copy.apply(unifier);
					} else {
						System.err.println("Big mess!");
					}
					
					for(Proposition proposition : copy.getPreconds()) {
						PropositionImpl realProp = (PropositionImpl) proposition;
						realProp.apply(unifier);
						addInstance = preconds.contains(proposition);
						
						if(proposition.negated() && !addInstance) {
							/*Closed World Assumption*/
							if(initialState != null && initialState.isPropositionLevel()){
								PropositionLevel initial = (PropositionLevel) initialState;
								PropositionImpl positiveProposition = new PropositionImpl(proposition.getFunctor());
								positiveProposition.setTerms(proposition.getTerms());

								if(!initial.hasProposition(positiveProposition)){
									initial.addProposition(proposition);
								}
							}
							//addInstance = true;
						}
						if(!addInstance) {
							break;
						}
					}
					if(addInstance) {
						copy.apply(unifier);
						instances.add(getOperator(copy.getSignature()));
					}
				}
			}
		}
		return instances;
	}
	
	@SuppressWarnings("unchecked")
	protected Set<Term> getAllPossibleTerms(List<Proposition> propositions) {
		Set<Term> possibleTerms = new HashSet<Term>();
		
		for(Proposition proposition : propositions) {
			if(proposition.getTerms() !=  null) {
				possibleTerms.addAll(proposition.getTerms());
			}
		}
		
		return possibleTerms;
	}
	
	/**
	 * A utility function that returns an empty operator iterator
	 * @return
	 */
	public static Iterator<Operator> getEmptyIterator() {
		Iterator<Operator> iterator = new Iterator<Operator>() {

			public boolean hasNext() {return false;}

			public Operator next() {return null;}

			public void remove() {}
			
		};
		
		return iterator;
	}
	
	/**
	 * An iterator to generate all possible combinations of
	 * terms for a given vector size
	 * @author Felipe Meneguzzi
	 *
	 */
	protected class TermInstanceIterator implements Iterator<Term[]> {
		protected final Iterator<Term>[] iterators;
		protected final Term[] currentTerms;
		protected final Set<Term> terms;
		
		@SuppressWarnings("unchecked")
		public TermInstanceIterator(Set<Term> terms, int size) {
			iterators = new Iterator[size];
			currentTerms = new Term[size];
			this.terms = terms;
			for(int i=0; i<iterators.length; i++) {
				iterators[i]=terms.iterator();
				//Initialize all but the first term
				//to comply with the next method
				if(i>0) {
					currentTerms[i] = iterators[i].next();
				}
			}
		}

		public boolean hasNext() {
			for(Iterator<Term> iterator : iterators) {
				if(iterator.hasNext()) {
					return true;
				}
			}
			return false;
		}

		public Term[] next() {
			boolean advanceNext = true;
			int i=0;
			while(advanceNext) {
				if(iterators[i].hasNext()) {
					advanceNext = false;
					currentTerms[i] = iterators[i].next();
				} else {
					iterators[i] = terms.iterator();
					currentTerms[i] = iterators[i].next();
					i++;
				}
			}
			return currentTerms;
		}

		public void remove() {
			// TODO Auto-generated method stub
			
		}
	}

	public void setTypes(Map<String, Set<String>> types) {
		this.usingTypes = true;
		this.types = types;
	}

	public void setParameterTypes(Map<String, List<String>> parameterTypes) {
		this.usingTypes = true;
		this.parameterTypes = parameterTypes;
	}
}