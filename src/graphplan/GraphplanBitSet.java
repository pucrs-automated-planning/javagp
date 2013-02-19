package graphplan;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphplanBitSet {

	private List<Proposition> propositions;
	private List<BitSet> propositionLayers;

	private List<Operator> operators;
	private List<BitSet> operatorLayers;
	private HashMap<String, BitSet> positiveEffects;
	private HashMap<String, BitSet> negativeEffects;
	private HashMap<String, BitSet> positivePrecond;
	private HashMap<String, BitSet> negativePrecond;

	private BitSet goal;
	private BitSet init;
	
	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;

	private int index = 0;

	public GraphplanBitSet(DomainDescription domainDescription){
		this.parameterTypes = domainDescription.getParameterTypes();
		this.types = domainDescription.getTypes();
		
		this.propositions = new ArrayList<Proposition>(domainDescription.getInitialState());
		this.propositions.addAll(domainDescription.getGoalState());
		this.operators = new ArrayList<Operator>(domainDescription.getOperators());
		
		this.propositionLayers = new ArrayList<BitSet>();
		BitSet p0 = new BitSet();
		for(Proposition p: domainDescription.getInitialState()){
			p0.set(this.propositions.indexOf(p));
		}
		this.propositionLayers.add(p0);
		
		this.operatorLayers = new ArrayList<BitSet>();
		for(Operator op: this.operators){
			BitSet pe = new BitSet();
			BitSet ne = new BitSet();
			for(Proposition e: op.getEffects()){
				if(e.negated()) ne.set(getIndexProposition(e)); 
				else pe.set(getIndexProposition(e));
			}
			
			this.positiveEffects.put(op.getFunctor(), pe);
			this.negativeEffects.put(op.getFunctor(), ne);

			BitSet pp = new BitSet();
			BitSet np = new BitSet();
			for(Proposition p: op.getPreconds()){
				if(p.negated()) np.set(getIndexProposition(p)); 
				else pp.set(getIndexProposition(p));
			}
			
			this.positivePrecond.put(op.getFunctor(), pp);
			this.negativePrecond.put(op.getFunctor(), np);
		}
	}

	private int getIndexProposition(Proposition p) {
		if(this.propositions.indexOf(p) == -1) {
			List<String> terms = new ArrayList<String>();
			
			for(String s: this.parameterTypes.get(p.getFunctor())){
				terms.addAll(this.types.get(s));
			}
			this.propositions.add(p);
			return this.propositions.indexOf(p);
		}
		
		return this.propositions.indexOf(p);
	}

	public void expand(int index) {
		for (Operator op : this.operators) {

		}
	}
	
	class TermInstanceIterator implements Iterator<Term[]> {
		protected final Iterator<Term>[] iterators;
		protected final Term[] currentTerms;
		protected final Set<Term> terms;
		
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
		
		@Override
		public boolean hasNext() {
			for(Iterator<Term> iterator : iterators) {
				if(iterator.hasNext()) {
					return true;
				}
			}
			return false;
		}
		
		@Override
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

		@Override
		public void remove() {
		}
	}
}