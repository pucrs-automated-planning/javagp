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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import pddl4j.PDDLObject;
import pddl4j.Parser;
import pddl4j.RequireKey;
import pddl4j.Source;
import pddl4j.exp.AndExp;
import pddl4j.exp.AtomicFormula;
import pddl4j.exp.Exp;
import pddl4j.exp.InitEl;
import pddl4j.exp.NotExp;
import pddl4j.exp.action.Action;
import pddl4j.exp.action.ActionDef;
import pddl4j.exp.term.Constant;
import pddl4j.exp.term.Variable;

public class PDDLPlannerAdapter {
	
	private PDDLObject pddlObject;
	
	private String domain;
	private String problem;

	private Map<String, Set<String>> types;
	private Map<String, List<String>> parameterTypes;
	
	/**
	 * 
	 * @param pddl4j.PDDLObject pddlObject
	 */
	public PDDLPlannerAdapter(PDDLObject pddlObject){
		this.pddlObject = pddlObject;
	}

	/**
	 * 
	 * @param String domain
	 * @param String problem
	 */
	public PDDLPlannerAdapter(String domain, String problem){
		this.domain = domain;
		this.problem = problem;
		
		this.types = new HashMap<String, Set<String>>();
		this.parameterTypes = new HashMap<String, List<String>>();
		
        Properties options = new Properties();
        options.put("source", Source.V3_0);
        options.put(RequireKey.STRIPS, true);
        options.put(RequireKey.TYPING, true);
        options.put(RequireKey.EQUALITY, true);
        options.put(RequireKey.NEGATIVE_PRECONDITIONS, true);
        options.put(RequireKey.DISJUNCTIVE_PRECONDITIONS, true);
        options.put(RequireKey.EXISTENTIAL_PRECONDITIONS, true);
        options.put(RequireKey.UNIVERSAL_PRECONDITIONS, true);
        options.put(RequireKey.CONDITIONAL_EFFECTS, true);

		try {
			Parser parserPDDL = new Parser(options);
			PDDLObject domainPDDL = parserPDDL.parse(new File(domain));
			PDDLObject problemPDDL = parserPDDL.parse(new File(problem));
			if (domainPDDL != null && problemPDDL != null) {
				this.pddlObject = parserPDDL.link(domainPDDL, problemPDDL);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param PDDLObject pddlObject
	 * @return DomainDescription
	 */
	@SuppressWarnings("rawtypes")
	public DomainDescription getDomainDescriptionFromPddlObject(){
		if(this.pddlObject != null){
			System.out.println("JavaGP with PDDL\n");
			
			System.out.println("+ DOMAIN: " + this.domain);
			System.out.println("+ PROBLEM: " + this.problem);
			
			System.out.println("\nPDDL Parser\n");
			Iterator<ActionDef> actionsIterator = this.pddlObject.actionsIterator();
			
			List<Operator>    operators    = new ArrayList<Operator>();
			List<Proposition> initialState = new ArrayList<Proposition>();
			List<Proposition> goalState    = new ArrayList<Proposition>();
			
			System.out.println("--> Actions\n");
			
			while(actionsIterator.hasNext()){
				ActionDef actionDef = actionsIterator.next();
				System.out.println(actionDef);
				
				Exp precontidion = (Exp) ((Action)actionDef).getPrecondition();
				Exp effect = (Exp) ((Action)actionDef).getEffect();
				
				List<String> parameterTypes = new ArrayList<String>();
				
				OperatorImpl operatorImpl = new OperatorImpl(actionDef.getName());
				List<Term> termsOp = new ArrayList<Term>();
				for(pddl4j.exp.term.Term term: actionDef.getParameters()){
					termsOp.add(new VarTerm(term.getImage().replace("?", "").toUpperCase()));
					parameterTypes.add(term.getTypeSet().toString());
					
					Set<String> setVar = this.types.get(term.getTypeSet().toString());
					
					if(setVar == null){
						setVar = new HashSet<String>();
						setVar.add(term.getImage().replace("?", ""));
						this.types.put(term.getTypeSet().toString(), setVar);
					} else setVar.add(term.getImage().replace("?", ""));
				}

				this.parameterTypes.put(actionDef.getName(), parameterTypes);
				
				operatorImpl.addTerms(termsOp);
				
				operatorImpl.getPreconds().addAll(this.getPropositionFromDomainExp(precontidion));
				operatorImpl.getEffects().addAll(this.getPropositionFromDomainExp(effect));
				
				operators.add(operatorImpl);
			}
			
			System.out.println("\n--> Init\n");
			
			for(InitEl init: this.pddlObject.getInit()){
				System.out.println(init);
				
                AtomicFormula p = (AtomicFormula) init;
				PropositionImpl proposition = new PropositionImpl(p.getPredicate());
				Iterator variables = p.iterator();
				
				List<Term> terms = new ArrayList<Term>();
				while(variables.hasNext()){
					Constant var = (Constant) variables.next();
					Atom term = new Atom(var.getImage());
					terms.add(term);
					
					Set<String> setVar = this.types.get(var.getTypeSet().toString());
					setVar.add(var.getImage());
				}
				
				proposition.addTerms(terms);
				initialState.add(proposition);
			}
			
			System.out.println("\n--> Goal\n");
			
			System.out.println(this.pddlObject.getGoal());
			
			/*
		
			ListExp list = (ListExp) this.pddlObject.getGoal();
			AndExp andGoal = (AndExp) this.pddlObject.getGoal();
			
			
			for(Exp goal:andGoal){
				
				AtomicFormula p = (AtomicFormula) goal;
                PropositionImpl	proposition = new PropositionImpl(p.getPredicate());
				
                Iterator variables = p.iterator();
				List<Term> terms = new ArrayList<Term>();

				while(variables.hasNext()){
					Constant var = (Constant) variables.next();
					Atom term = new Atom(var.getImage());
					terms.add(term);
				}
				proposition.addTerms(terms);
				goalState.add(proposition);
			}
			*/
			//ArrayList<E> lista = this.getPropositionFromDomainExp(this.pddlObject.getInit());
			
			goalState.addAll(this.getPropositionFromProblemExp(this.pddlObject.getGoal()));
			
			System.out.println("\nPDDL Parser\n");

			DomainDescription domainDescription = new DomainDescription(operators, initialState, goalState, this.types, this.parameterTypes);
			return domainDescription;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param Exp exp
	 * @return List<PropositionImpl>
	 */
	private List<PropositionImpl> getPropositionFromDomainExp(Exp exp){
		return getPropositionFromDomainExp(exp, false);
	}
	
	/**
	 * 
	 * @param Exp exp
	 * @param boolean negated
	 * @return List<PropositionImpl>
	 */
	@SuppressWarnings("rawtypes")
	private List<PropositionImpl> getPropositionFromDomainExp(Exp exp, boolean negated){
		List<PropositionImpl> propositionImpls = new ArrayList<PropositionImpl>();
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
				
				Iterator variables = p.iterator();
				
				List<Term> terms = new ArrayList<Term>();
				while(variables.hasNext()){
					Variable var = (Variable) variables.next();
					VarTerm term = new VarTerm(var.getImage().replace("?", "").toUpperCase());
					terms.add(term);
					
					Set<String> setVar = this.types.get(var.getTypeSet().toString());
					
					if(setVar == null){
						setVar = new HashSet<String>();
						setVar.add(var.getImage().replace("?", ""));
						this.types.put(var.getTypeSet().toString(), setVar);
					} else setVar.add(var.getImage().replace("?", ""));
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
	 * 
	 * @param Exp exp
	 * @return List<PropositionImpl>
	 */
	private List<PropositionImpl> getPropositionFromProblemExp(Exp exp){
		return getPropositionFromProblemExp(exp, false);
	}
	
	/**
	 * 
	 * @param Exp exp
	 * @param boolean negated
	 * @return List<PropositionImpl>
	 */
	@SuppressWarnings("rawtypes")
	private List<PropositionImpl> getPropositionFromProblemExp(Exp exp, boolean negated){
		List<PropositionImpl> propositionImpls = new ArrayList<PropositionImpl>();
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
				
				List<Term> terms = new ArrayList<Term>();
				while(constants.hasNext()){
					Constant con = (Constant) constants.next();
					Atom term = new Atom(con.getImage());
					terms.add(term);
					
					Set<String> setCon = this.types.get(con.getTypeSet().toString());
					
					if(setCon == null){
						setCon = new HashSet<String>();
						setCon.add(con.getImage());
						this.types.put(con.getTypeSet().toString(), setCon);
					} else setCon.add(con.getImage());
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


