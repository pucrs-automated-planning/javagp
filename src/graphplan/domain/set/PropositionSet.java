package graphplan.domain.set;

import graphplan.domain.Proposition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PropositionSet<E extends Proposition> implements Set<Proposition> {

	private HashMap<Proposition, Proposition> set = new HashMap<Proposition, Proposition>();
	
	@Override
	public int size() {
		return this.set.size();
	}

	@Override
	public boolean isEmpty() {
		return this.set.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.set.containsKey(o);
	}

	@Override
	public Iterator<Proposition> iterator() {
		return (Iterator<Proposition>) this.set.keySet().iterator();
	}

	@Override
	public Object[] toArray() {
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return null;
	}

	@Override
	public boolean add(Proposition e) {
		if (this.set.containsKey(e)){
			return false;
		} else {
            this.set.put(e, e);
            return true;
        }
	}

	@Override
	public boolean remove(Object o) {
		if (this.set.containsKey(o)){
			this.set.remove(o);
			return true;
		} else return false;
	}
	
	public Proposition get(Proposition p){
		return this.set.get(p);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Proposition> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
		this.set.clear();
	}
}