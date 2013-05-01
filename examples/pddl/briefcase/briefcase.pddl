(define (domain briefcase)
(:requirements :strips :typing :conditional-effects :universal-preconditions)
(:types portable location - object)
(:predicates (at ?y - portable ?x - location)
             (in ?x - portable)
             (isat ?x - location))


(:action move
  :parameters (?m ?l - location)
  :precondition  (isat ?m)
  :effect (and (isat ?l) (not (isat ?m))
		    (forall (?x - portable) (when (in ?x)
		      (and (at ?x ?l) (not (at ?x ?m)))))))

  (:action takeout
      :parameters (?x - portable)
      :precondition (in ?x)
      :effect (not (in ?x)))
      
  (:action putin
      :parameters (?x - portable ?l - location)
      :precondition (and (not (in ?x)) (at ?x ?l) (isat ?l))
      :effect (in ?x)))