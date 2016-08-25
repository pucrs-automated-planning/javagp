package graphplan.parser;

import fr.uga.pddl4j.parser.*;
import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
import jason.asSyntax.Atom;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

public class PDDLPlannerAdapter {

	private static final Logger logger = Logger.getLogger(PDDLPlannerAdapter.class.getName());
	private Parser parser;
	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;

	public static PDDLPlannerAdapter getInstance(String domain, String problem) {
		try {
			Parser parser = new Parser();
			parser.parse(domain, problem);
			return new PDDLPlannerAdapter(parser);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private PDDLPlannerAdapter(Parser parser) {
		this.types = new HashMap<>();
		this.parameterTypes = new HashMap<>();
		this.parser = parser;
	}

	public DomainDescription getDomainDescriptionFromPddlObject() {
		logger.finest("\nPDDL Parser\n");

		// Parse requirements
		boolean negativePreconditions = false;

		for(RequireKey requireKey : getRequirements()) {
			if (requireKey == RequireKey.NEGATIVE_PRECONDITIONS) {
				negativePreconditions = true;
				// TODO: why do we need this log? "The Problem has Negative Preconditions"
				logger.severe("--> The Problem has Negative Preconditions\n");
				break;
			}
		}

		// Parse operators
		List<Operator> operators = new ArrayList<>();
		for(Op operator : this.parser.getDomain().getOperators()) {
			logger.finest(operator.toString());
			Exp preconditions = operator.getPreconditions();
			Exp effects = operator.getEffects();

			List<String> parameterTypes = new ArrayList<>();
			OperatorImpl operatorImpl = new OperatorImpl(operator.getName().getImage());
			List<Term> termsOp = new ArrayList<>();

			for(TypedSymbol typedSymbol : operator.getParameters()) {
				termsOp.add(new VarTerm(typedSymbol.getImage().replace("?", "").toUpperCase()));
				parameterTypes.add(listOfTypesToString(typedSymbol.getTypes()));
			}

			this.parameterTypes.put(operator.getName().toString(), parameterTypes);

			operatorImpl.addTerms(termsOp);

			operatorImpl.getPreconds().addAll(this.getPropositionsFromExp(preconditions, false));
			operatorImpl.getEffects().addAll(this.getPropositionsFromExp(effects, false));

			operators.add(operatorImpl);
		}

		// Parse init
		logger.finest("\n--> Init\n");
		List<Proposition> initialState = new ArrayList<>();

		for (Exp exp : this.parser.getProblem().getInit()) {
			logger.finest(exp.toString());

			switch (exp.getConnective()) {
				case ATOM:
					initialState.add(getPropositionImpl(false, exp, true));
					break;
				case NOT:
					initialState.add(getPropositionImpl(true, exp.getChildren().get(0), true));
					break;
				default:
					throw new IllegalStateException("Exp not expected when parsing Proposition: " + exp.getConnective());
			}
		}

		// Parse types
		setupTypes();

		// Parse goals

		logger.finest("\n--> Goal\n");
		List<Proposition> goalState = new ArrayList<>();
		logger.finest(this.parser.getProblem().getGoal().toString());
		goalState.addAll(this.getPropositionsFromExp(this.parser.getProblem().getGoal(), true));

		// Setup
		logger.finest("\nPDDL Parser\n");
		return new DomainDescription(operators, initialState, goalState, this.types, this.parameterTypes, negativePreconditions);
	}

	/**
	 * Utility methods
	 */

	private Set<RequireKey> getRequirements() {
		Set<RequireKey> domainRequirements = this.parser.getDomain().getRequirements();
		Set<RequireKey> problemRequirements = this.parser.getProblem().getRequirements();
		Set<RequireKey> requirements = new HashSet<>();
		requirements.addAll(domainRequirements);
		requirements.addAll(problemRequirements);
		return requirements;
	}

	// Setup types data structure
	private void setupTypes() {
		// We first make a loop through all existing types to populate with its corresponding constants
		for(TypedSymbol typedSymbol : this.parser.getDomain().getTypes()) {
			Set<String> constants = new HashSet<>();
			for(TypedSymbol constant : this.parser.getProblem().getObjects()) {
				for(Symbol symbol : constant.getTypes()) {
					if(symbol.getImage().equals(typedSymbol.getImage())) {
						constants.add(constant.getImage());
						break;
					}
				}
			}
			this.types.put(typedSymbol.getImage(), constants);
		}

		// After this, we must take into account type hierarchies (e.g. room - location - object).
		// As this hierarchies can go a long way down, we keep looping while there are changes
		boolean somethingChanged = true;
		while(somethingChanged) {
			somethingChanged = false;
			for(TypedSymbol typedSymbol : this.parser.getDomain().getTypes()) {
				for(Symbol symbol : typedSymbol.getTypes()) {
					Set<String> existingTypes = this.types.get(symbol.getImage());
					Set<String> newTypes = this.types.get(typedSymbol.getImage());
					Set<String> finalTypes = new HashSet<>();
					finalTypes.addAll(existingTypes);
					if(finalTypes.addAll(newTypes)) {
						somethingChanged = true;
					}
					this.types.put(symbol.getImage(),finalTypes);
				}
			}

		}
	}

	// Returns a list of symbol types
	private String listOfTypesToString(List<Symbol> symbols) {
		StringBuilder str = new StringBuilder();
		if(symbols.size() == 0) {
			str.append("empty-type");
		} else if(symbols.size() == 1) {
			str.append((symbols.iterator().next()).toString());
		} else {
			str.append("(either");
			for(Symbol s : symbols) {
				str.append(" ").append(s.getImage());
			}
			str.append(")");
		}

		return str.toString();
	}

	// Get Proposition from Exp and ground
	private List<PropositionImpl> getPropositionsFromExp(Exp exp, boolean isGround) {
		return getPropositionsFromExp(exp, false, isGround);
	}

	private List<PropositionImpl> getPropositionsFromExp(Exp exp, boolean negated, boolean isGround) {
		List<PropositionImpl> propositionImpls = new ArrayList<>();
		switch (exp.getConnective()) {
			case AND:
				for (Exp and : exp.getChildren()) {
					propositionImpls.addAll(getPropositionsFromExp(and, false, isGround));
				}
				break;
			case ATOM:
				propositionImpls.add(getPropositionImpl(negated, exp, isGround));
				break;
			case NOT:
				for (Exp and : exp.getChildren()) {
					propositionImpls.addAll(getPropositionsFromExp(and, true, isGround));
				}
				break;
			default:
				throw new IllegalStateException("Exp not expected when parsing Proposition: " + exp.getConnective());
		}
		return propositionImpls;
	}

	private PropositionImpl getPropositionImpl(boolean negated, Exp exp, boolean isGround) {
		PropositionImpl proposition = new PropositionImpl(new LiteralImpl(!negated, exp.getAtom().get(0).getImage()));

		List<Term> terms = new ArrayList<>();
		boolean isPredicate = true;
		for(Symbol symbol : exp.getAtom()) {
			if(isPredicate) {
				isPredicate = false;
				continue;
			}

			Term term;
			if(isGround) {
				term = new Atom(symbol.getImage());
			} else {
				term = new VarTerm(symbol.getImage().replace("?", "").toUpperCase());
			}
			terms.add(term);
		}
		proposition.addTerms(terms);
		return proposition;
	}
}
