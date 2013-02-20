package graphplan;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
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
		this.operators = new ArrayList<Operator>();
		
		this.propositionLayers = new ArrayList<BitSet>();
		BitSet p0 = new BitSet();
		for(Proposition p: domainDescription.getInitialState()){
			p0.set(this.propositions.indexOf(p));
		}
		this.propositionLayers.add(p0);
		
		this.positiveEffects = new HashMap<String, BitSet>();
		this.negativeEffects = new HashMap<String, BitSet>();
		this.positivePrecond = new HashMap<String, BitSet>();
		this.negativePrecond = new HashMap<String, BitSet>();
		
		this.operatorLayers = new ArrayList<BitSet>();
		for(Operator op: domainDescription.getOperators()){
			System.out.println("-");
			Set<Term> terms = new HashSet<Term>();
			List<String> pTypes = this.parameterTypes.get(op.getFunctor());
			
			for(String param: pTypes)
				for(String t :this.types.get(param)) terms.add(new Atom(t));
			
			Term[] termInstances = null;
			int n = this.parameterTypes.get(op.getFunctor()).size();
			for(TermInstanceIterator it = new TermInstanceIterator(terms, n); it.hasNext();){
				termInstances = it.next();
				OperatorImpl newOp = new OperatorImpl(op.getFunctor());
				List<Term> newTerms = new ArrayList<Term>();
				for(int i=0; i<n; i++){
					Set<String> tTypes = this.types.get(pTypes.get(i));
					if(tTypes.contains(((Atom)termInstances[i]).getFunctor())){
						newTerms.add(termInstances[i]);
					} else break;
				}
				if(!newTerms.isEmpty() && newTerms.size() == n){
					newOp.addTerms(newTerms);
					newOp.getPreconds().addAll(op.getPreconds());
					newOp.getEffects().addAll(op.getEffects());
					
					BitSet pe = new BitSet();
					BitSet ne = new BitSet();
					for(Proposition e: newOp.getEffects()){
						if(e.negated()) ne.set(getIndexProposition(e, newOp.getTerms(), false)); 
						else pe.set(getIndexProposition(e, newOp.getTerms(), true));
					}
					
					this.positiveEffects.put(op.getFunctor(), pe);
					this.negativeEffects.put(op.getFunctor(), ne);

					BitSet pp = new BitSet();
					BitSet np = new BitSet();
					for(Proposition p: newOp.getPreconds()){
						if(p.negated()) np.set(getIndexProposition(p, newOp.getTerms(), false)); 
						else pp.set(getIndexProposition(p, newOp.getTerms(), true));
					}
					
					this.positivePrecond.put(op.getFunctor(), pp);
					this.negativePrecond.put(op.getFunctor(), np);
					
					this.operators.add(newOp);
				}
			}
		}
		
		System.out.println();
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

	class TermInstanceIterator implements Iterator<Term[]> {
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
		public void remove() {}
	}
	
	public void expand(int index) {
		for (Operator op : this.operators) {

		}
	}
}