package graphplan.parser;

import graphplan.domain.DomainDescription;
import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
import jason.asSyntax.Atom;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import pddl4j.*;
import pddl4j.ErrorManager.Message;
import pddl4j.exp.*;
import pddl4j.exp.action.Action;
import pddl4j.exp.action.ActionDef;
import pddl4j.exp.term.Constant;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;

public class PDDLPlannerAdapter {

	private static final Logger logger = Logger.getLogger(PDDLPlannerAdapter.class.getName());

	private PDDLObject pddlObject;

	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;

	/**
	 *
	 */
	public PDDLPlannerAdapter(PDDLObject pddlObject) {
		this.pddlObject = pddlObject;
	}

	/**
	 * @throws ParserException
	 */
	public PDDLPlannerAdapter(String domain, String problem) throws ParserException {

		this.types = new HashMap<>();
		this.parameterTypes = new HashMap<>();

		Properties options = new Properties();
		options.put("source", Source.V3_0);
		options.put("debug", true);
		options.put(RequireKey.STRIPS, true);
		options.put(RequireKey.TYPING, true);
//        options.put(RequireKey.EQUALITY, false);
		options.put(RequireKey.NEGATIVE_PRECONDITIONS, true);
//        options.put(RequireKey.DISJUNCTIVE_PRECONDITIONS, false);
//        options.put(RequireKey.EXISTENTIAL_PRECONDITIONS, false);
//        options.put(RequireKey.UNIVERSAL_PRECONDITIONS, false);
//        options.put(RequireKey.CONDITIONAL_EFFECTS, false);

		try {
			Parser pddlParser = new Parser(options);
			PDDLObject pddlDomain = pddlParser.parse(new File(domain));
			boolean domainParseError = pddlParser.getErrorManager().contains(Message.ERROR);
			PDDLObject pddlProblem = pddlParser.parse(new File(problem));
			boolean problemParseError = pddlParser.getErrorManager().contains(Message.ERROR);
			ErrorManager mgr = pddlParser.getErrorManager();
			// If the parser produces errors we print it and stop
			if (mgr.contains(Message.ERROR)) {
				for (String m : mgr.getMessages(Message.ALL)) {
					logger.severe(m);
				}
			} else {// else we print the warnings
				for (String m : mgr.getMessages(Message.WARNING)) {
					logger.severe(m);
				}
			}
			if (pddlDomain == null || domainParseError) {
				throw new pddl4j.ParserException("Parse error in PDDL Domain");
			} else if (pddlProblem == null || problemParseError) {
				throw new pddl4j.ParserException("Parse error in PDDL Problem");
			} else {
				this.pddlObject = pddlParser.link(pddlDomain, pddlProblem);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @return DomainDescription
	 */
	@SuppressWarnings("rawtypes")
	public DomainDescription getDomainDescriptionFromPddlObject() {
		if (this.pddlObject != null) {
			logger.finest("\nPDDL Parser\n");

			boolean negativePreconditions = false;

			Iterator<RequireKey> requirements = this.pddlObject.requirementsIterator();
			while (requirements.hasNext()) {
				RequireKey requireKey = requirements.next();
				if (requireKey == RequireKey.NEGATIVE_PRECONDITIONS) {
					negativePreconditions = true;
					logger.severe("--> The Problem has Negative Preconditions\n");
				}
			}

			Iterator<ActionDef> actionsIterator = this.pddlObject.actionsIterator();

			List<Operator> operators = new ArrayList<>();
			List<Proposition> initialState = new ArrayList<>();
			List<Proposition> goalState = new ArrayList<>();

			logger.finest("--> Actions\n");

			while (actionsIterator.hasNext()) {
				ActionDef actionDef = actionsIterator.next();
				logger.finest(actionDef.toString());

				Exp precontidion = ((Action) actionDef).getPrecondition();
				Exp effect = ((Action) actionDef).getEffect();

				List<String> parameterTypes = new ArrayList<>();

				OperatorImpl operatorImpl = new OperatorImpl(actionDef.getName());
				List<Term> termsOp = new ArrayList<>();
				for (pddl4j.exp.term.Term term : actionDef.getParameters()) {
					termsOp.add(new VarTerm(term.getImage().replace("?", "").toUpperCase()));
					parameterTypes.add(term.getTypeSet().toString());

					Set<String> setVar = this.types.get(term.getTypeSet().toString());

					for (Constant c : this.pddlObject.getTypedDomain(term.getTypeSet())) {
						if (setVar == null) {
							setVar = new HashSet<>();
							setVar.add(c.toString());
							this.types.put(term.getTypeSet().toString(), setVar);
						} else setVar.add(c.toString());
					}
				}

				this.parameterTypes.put(actionDef.getName(), parameterTypes);

				operatorImpl.addTerms(termsOp);

				operatorImpl.getPreconds().addAll(this.getPropositionFromDomainExp(precontidion));
				operatorImpl.getEffects().addAll(this.getPropositionFromDomainExp(effect));

				operators.add(operatorImpl);
			}

			logger.finest("\n--> Init\n");

			for (InitEl init : this.pddlObject.getInit()) {
				logger.finest(init.toString());
				boolean negated = false;
				Literal p;

				if (init instanceof NotAtomicFormula) {
					p = (NotAtomicFormula) init;
					negated = true;
				} else p = (AtomicFormula) init;
				PropositionImpl proposition = new PropositionImpl(!negated, p.getPredicate());
				Iterator variables = p.iterator();

				List<Term> terms = new ArrayList<>();
				while (variables.hasNext()) {
					Constant var = (Constant) variables.next();
					Atom term = new Atom(var.getImage());
					terms.add(term);

					Set<String> setVar = this.types.get(var.getTypeSet().toString());
					setVar.add(var.getImage());
				}

				proposition.addTerms(terms);
				initialState.add(proposition);
			}

			logger.finest("\n--> Goal\n");
			logger.finest(this.pddlObject.getGoal().toString());

			goalState.addAll(this.getPropositionFromProblemExp(this.pddlObject.getGoal()));

			logger.finest("\nPDDL Parser\n");
			return new DomainDescription(operators, initialState, goalState, this.types, this.parameterTypes, negativePreconditions);
		}

		return null;
	}

	/**
	 * @return List<PropositionImpl>
	 */
	private List<PropositionImpl> getPropositionFromDomainExp(Exp exp) {
		return getPropositionFromDomainExp(exp, false);
	}

	/**
	 * @return List<PropositionImpl>
	 */
	@SuppressWarnings("rawtypes")
	private List<PropositionImpl> getPropositionFromDomainExp(Exp exp, boolean negated) {
		List<PropositionImpl> propositionImpls = new ArrayList<>();
		switch (exp.getExpID()) {
			case AND:
				AndExp andExp = (AndExp) exp;
				for (Exp and : andExp) {
					propositionImpls.addAll(getPropositionFromDomainExp(and, false));
				}
				break;
			case ATOMIC_FORMULA:
				AtomicFormula p = (AtomicFormula) exp;
				PropositionImpl proposition = new PropositionImpl(new LiteralImpl(!negated, p.getPredicate()));

				Iterator pddlTerms = p.iterator();

				List<Term> terms = new ArrayList<>();
				while (pddlTerms.hasNext()) {
					pddl4j.exp.term.Term var = (pddl4j.exp.term.Term) pddlTerms.next();
					VarTerm term = new VarTerm(var.getImage().replace("?", "").toUpperCase());
					terms.add(term);

					Set<String> setVar = this.types.get(var.getTypeSet().toString());

					for (Constant c : this.pddlObject.getTypedDomain(var.getTypeSet())) {
						if (setVar == null) {
							setVar = new HashSet<>();
							setVar.add(c.toString());
							this.types.put(var.getTypeSet().toString(), setVar);
						} else setVar.add(c.toString());
					}
				}

				proposition.addTerms(terms);
				propositionImpls.add(proposition);

				break;
			case NOT:
				NotExp notExp = (NotExp) exp;
				propositionImpls.addAll(getPropositionFromDomainExp(notExp.getExp(), true));
				break;
			default:
				break;
		}
		return propositionImpls;
	}


	/**
	 * @return List<PropositionImpl>
	 */
	private List<PropositionImpl> getPropositionFromProblemExp(Exp exp) {
		return getPropositionFromProblemExp(exp, false);
	}

	/**
	 * @return List<PropositionImpl>
	 */
	@SuppressWarnings("rawtypes")
	private List<PropositionImpl> getPropositionFromProblemExp(Exp exp, boolean negated) {
		List<PropositionImpl> propositionImpls = new ArrayList<>();
		switch (exp.getExpID()) {
			case AND:
				AndExp andExp = (AndExp) exp;
				for (Exp and : andExp) {
					propositionImpls.addAll(getPropositionFromProblemExp(and, false));
				}
				break;
			case ATOMIC_FORMULA:
				AtomicFormula p = (AtomicFormula) exp;
				PropositionImpl proposition = new PropositionImpl(new LiteralImpl(!negated, p.getPredicate()));

				Iterator constants = p.iterator();

				List<Term> terms = new ArrayList<>();
				while (constants.hasNext()) {
					Constant con = (Constant) constants.next();
					Atom term = new Atom(con.getImage());
					terms.add(term);

					Set<String> setCon = this.types.get(con.getTypeSet().toString());

					for (Constant c : this.pddlObject.getTypedDomain(con.getTypeSet())) {
						if (setCon == null) {
							setCon = new HashSet<>();
							setCon.add(c.toString());
							this.types.put(con.getTypeSet().toString(), setCon);
						} else setCon.add(c.toString());
					}
				}

				proposition.addTerms(terms);
				propositionImpls.add(proposition);

				break;
			case NOT:
				NotExp notExp = (NotExp) exp;
				propositionImpls.addAll(getPropositionFromProblemExp(notExp.getExp(), true));
				break;
			default:
				break;
		}

		return propositionImpls;
	}
}
