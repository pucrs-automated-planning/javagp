package graphplan.graph.planning.bitset;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
import graphplan.graph.GraphElement;
import graphplan.graph.GraphElementVisitor;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class PlanningGraphBitSet implements GraphElement {

	private static final long serialVersionUID = 3839137792873718097L;
	
	private List<Proposition> propositions;
	private List<BitSet> propositionLayers;

	private List<Operator> operators;
	private List<BitSet> operatorLayers;

	private HashMap<String, BitSet> positiveEffects;
	private HashMap<String, BitSet> negativeEffects;
	private HashMap<String, BitSet> positivePrecond;
	private HashMap<String, BitSet> negativePrecond;
	
	private HashMap<Proposition, Set<Proposition>> propositionsMutexes;
	private HashMap<Operator, Set<Operator>> actionsMutexes;

	private BitSet goal;
	private BitSet init;
	
	private int index = 0;
	
	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;
	private Map<String, List<String>> opsEquals;
	
	@SuppressWarnings("unchecked")
	public PlanningGraphBitSet(DomainDescription domainDescription){
		this.parameterTypes = domainDescription.getParameterTypes();
		this.types = domainDescription.getTypes();
		
		this.propositions = new ArrayList<Proposition>(domainDescription.getInitialState());
		this.propositions.addAll(domainDescription.getGoalState());
		this.operators = new ArrayList<Operator>();
		
		this.propositionLayers = new ArrayList<BitSet>();
		this.operatorLayers = new ArrayList<BitSet>();
		
		this.positiveEffects = new HashMap<String, BitSet>();
		this.negativeEffects = new HashMap<String, BitSet>();
		this.positivePrecond = new HashMap<String, BitSet>();
		this.negativePrecond = new HashMap<String, BitSet>();
		
		this.opsEquals = new HashMap<String, List<String>>();
		
		this.parameters(domainDescription.getOperators());
		
		for(Operator op: domainDescription.getOperators()){
			Term[] termInstances = null;
			int n = this.parameterTypes.get(op.getFunctor()).size();
			TermInstanceIterator it = new TermInstanceIterator(op.getFunctor(), n);

			while(it.hasNext()){
				termInstances = it.next();
				OperatorImpl newOp = new OperatorImpl(op.getFunctor());
				List<Term> newTerms = new ArrayList<Term>();
				
				for(int i=0; i<n; i++)
					newTerms.add(termInstances[i]);

				newOp.addTerms(newTerms);
				List<String> termsOp = new ArrayList<String>();
				for(int i=0; i<newOp.getTerms().toArray().length; i++)
					termsOp.add(op.getTerms().toArray()[i].toString() +"@"+ newOp.getTerms().toArray()[i].toString());
				
				BitSet pe = new BitSet();
				BitSet ne = new BitSet();
				for(Proposition e: op.getEffects()){
					if(e.negated()) ne.set(getIndexProposition(e, getTerms(termsOp, e.getTerms()), false)); 
					else pe.set(getIndexProposition(e, getTerms(termsOp, e.getTerms()), true));
				}

				this.positiveEffects.put(newOp.toString(), pe);
				this.negativeEffects.put(newOp.toString(), ne);

				BitSet pp = new BitSet();
				BitSet np = new BitSet();
				for(Proposition p: op.getPreconds()){
					if(p.negated()) np.set(getIndexProposition(p, getTerms(termsOp, p.getTerms()), false)); 
					else pp.set(getIndexProposition(p, getTerms(termsOp, p.getTerms()), true));
				}

				this.positivePrecond.put(newOp.toString(), pp);
				this.negativePrecond.put(newOp.toString(), np);

				this.operators.add(newOp);
			}
		}
		
		BitSet p0 = new BitSet();
		for(Proposition p: domainDescription.getInitialState()){
			p0.set(this.propositions.indexOf(p));
		}
		this.init = p0;
		this.propositionLayers.add(p0);
		
		BitSet g = new BitSet();
		for(Proposition p: domainDescription.getGoalState()){
			g.set(this.propositions.indexOf(p));
		}
		this.goal = g;

		this.actionsMutexes = new HashMap<Operator, Set<Operator>>();
		this.propositionsMutexes = new HashMap<Proposition, Set<Proposition>>();
	}
	
	public List<Term> getTerms(List<String> termsOp, List<Term> terms){
		List<Term> newTerms = new ArrayList<Term>();
		for(String s:termsOp)
			for(Term t:terms)
				if(s.split("@")[0].equals(t.toString())) {
					newTerms.add(new Atom(s.split("@")[1]));
					continue;
				}
		return newTerms;
	}
	
	private void parameters(List<Operator> operators) {
		for(Operator op: operators)
			for(Operator op1: operators)
				if(!op.getFunctor().equals(op1.getFunctor()))
					if(this.parameterTypes.get(op.getFunctor()).equals(this.parameterTypes.get(op1.getFunctor()))){
						List<String> ops = this.opsEquals.get(op.getFunctor());
						List<String> ops1 = this.opsEquals.get(op1.getFunctor());
						
						if(ops == null)	{
							List<String> newOps = new ArrayList<String>();
							newOps.add(op1.getFunctor());
							this.opsEquals.put(op.getFunctor(), newOps);
						} else if(!ops.contains(op1.getFunctor())) ops.add(op1.getFunctor());
						
						if(ops1 == null) {
							List<String> newOps = new ArrayList<String>();
							newOps.add(op.getFunctor());
							this.opsEquals.put(op1.getFunctor(), newOps);
						} else if(!ops1.contains(op.getFunctor())) ops1.add(op.getFunctor());
					}
	}

	private int getIndexProposition(Proposition p, List<Term> terms, boolean positive) {
		PropositionImpl newP = new PropositionImpl(positive, p.getFunctor());
		newP.setTerms(terms);
		if(!existsProposition(newP)) {
			this.propositions.add(newP);
			return this.propositions.indexOf(newP);
		}
		
		return this.propositions.indexOf(newP);
	}
	
	private boolean existsProposition(Proposition p){
		for(Proposition prop :this.propositions)
			if(prop.toString().equals(p.toString())) return true;
		
		return false;
	}

	@Override
	public boolean accept(GraphElementVisitor visitor) {
		return false;
	}

	@Override
	public Iterator iterator() {
		return null;
	}
	
	public int getSize(){
		return this.index;
	}
	
	public void expandGraph(){
		BitSet pi = this.propositionLayers.get(this.index);
		BitSet newPropLayer = new BitSet();
		BitSet newOpLayer = new BitSet();
		
		for(Operator op: this.operators){
			if(isOk(op, this.index)){
				newOpLayer.set(this.operators.indexOf(op), true);
				
				newPropLayer.or(this.positiveEffects.get(op.toString()));
				newPropLayer.or(this.negativeEffects.get(op.toString()));
			}
		}
		
		this.operatorLayers.add(newOpLayer);
		this.propositionLayers.add(newPropLayer);
		
		//addActionMutexes(lastLevel, actionLevel);
		//addPropositionMutexes(actionLevel, propositionLevel);
		this.index++;
	}
	
	private boolean isOk(Operator op, int index) {
        BitSet preCondition = this.positivePrecond.get(op.toString());
        return this.contains(preCondition, index) && this.isMutexFree(preCondition, index);
	}
	
	private boolean isMutexFree(BitSet preCondition, int index) {
		return true;
	}

	private boolean contains(BitSet prop, int index){
        final BitSet pi = (BitSet) this.propositionLayers.get(index).clone();
        pi.and(prop);
        return pi.equals(prop);
	}

	class TermInstanceIterator implements Iterator<Term[]> {
		protected final Iterator<Term>[] iterators;
		protected final Term[] currentTerms;
		protected final String functor;
		
		@SuppressWarnings("unchecked")
		public TermInstanceIterator(String functor, int size) {
			this.iterators = new Iterator[size];
			this.currentTerms = new Term[size];
			this.functor = functor;
			
			List<String> pTypes = parameterTypes.get(functor);
			for(int i=0; i<iterators.length; i++) {
				iterators[i]=this.getTerms(types.get(pTypes.get(i))).iterator();
				if(i>0) currentTerms[i] = iterators[i].next();
			}
		}
		
		public Set<Term> getTerms(Set<String> termsS){
			Set<Term> terms = new HashSet<Term>();
			for(String s:termsS)
				terms.add(new Atom(s));
			return terms;
		}
		
		@Override
		public boolean hasNext() {
			for(Iterator<Term> iterator : iterators)
				if(iterator.hasNext()) return true;
			return false;
		}
		
		@Override
		public Term[] next() {
			boolean advanceNext = true;
			int i=0;
			List<String> pTypes = parameterTypes.get(functor);
			while(advanceNext) {
				if(iterators[i].hasNext()) {
					advanceNext = false;
					currentTerms[i] = iterators[i].next();
				} else {
					iterators[i] = this.getTerms(types.get(pTypes.get(i))).iterator();
					currentTerms[i] = iterators[i].next();
					i++;
				}
			}
			return currentTerms;
		}

		@Override
		public void remove() {}
	}
}