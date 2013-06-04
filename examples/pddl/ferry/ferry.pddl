(define (domain ferry)
  (:requirements :strips :equality :typing)

  (:types auto place ferry)
  (:constants theFerry - ferry)
  (:predicates (atFerry ?l - place)
	       (at ?x - auto
		   ?y - place)
	       (emptyFerry)
	       (on ?x - auto
		   ?f - ferry))

  (:action board
	     :parameters (?x - auto ?y - place)
	     :precondition (and (at ?x ?y)(atFerry ?y)(emptyFerry))
	     :effect 
	     (and (on ?x theFerry)
		   (not (at ?x ?y))
		   (not (emptyFerry))))
  (:action sail
	     :parameters (?x ?y - place)
	     :precondition (and (atFerry ?x) (not (= ?x ?y)))
	     :effect (and (atFerry ?y)
			   (not (atFerry ?x))))
  (:action debark
	     :parameters (?x - auto ?y - place)
	     :precondition (and (on ?x theFerry)(atFerry ?y))
	     :effect (and (not (on ?x theFerry))
			   (at ?x ?y)
			   (emptyFerry))))