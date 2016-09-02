(define (domain blocks)
	(:requirements :strips)
	(:constants-def table)
	(:predicates (on ?a ?b)
		         (block ?b)
				 (clear ?b) )	
  (:action move
	  :parameters (?b ?x ?y)	
	  :precondition (and (on ?b ?x) (clear ?b) (block ?b) (block ?y))
	  :effect (and (on ?b ?y) (clear ?x) (not (on ?b ?x)) (not (clear ?y))) )	

  (:action moveToTable
	  :parameters (?b ?x)	
	  :precondition (and (on ?b ?x) (clear ?b) (block ?b) (clear ?x))
	  :effect (and (on ?b table) (clear ?x) (not (on ?b ?x))) )
)