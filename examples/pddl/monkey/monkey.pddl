(define (domain monkey)	       
  (:requirements :strips)
 
  (:constants monkey box knife bananas glass waterfountain)
 
  (:predicates (location ?x)
	       (onFloor)
	       (at ?m ?x)
	       (hasknife)
	       (onbox ?x)
	       (hasbananas)
	       (hasglass)
	       (haswater))
  

  ;; movement and climbing
  (:action GOTO
	     :parameters (?x ?y)
	     :precondition (and (location ?x) (location ?y) (onFloor) (at monkey ?y))
	     :effect (and (at monkey ?x) (not (at monkey ?y))))
  
  (:action CLIMB
	     :parameters (?x)
	     :precondition (and (location ?x) (at box ?x) (at monkey ?x))
	     :effect (and (onbox ?x) (not (onFloor))))
  
  (:action PUSHBOX
	     :parameters (?x ?y)
	     :precondition (and (location ?x) (location ?y) (at box ?y) (at monkey ?y) 
				 (onFloor))
	     :effect (and (at monkey ?x) (not (at monkey ?y))
			   (at box ?x)    (not (at box ?y))))

  ;; getting bananas
  (:action GETKNIFE
	     :parameters (?y)
	     :precondition (and (location ?y) (at knife ?y) (at monkey ?y))
	     :effect (and (hasknife) (not (at knife ?y))))
  
  (:action GRABBANANAS
	     :parameters (?y)
	     :precondition (and (location ?y) (hasknife) 
                                 (at bananas ?y) (onbox ?y))
	     :effect (hasbananas))
  
  ;; getting water
  (:action PICKGLASS
	     :parameters (?y)
	     :precondition (and (location ?y) (at glass ?y) (at monkey ?y))
	     :effect (and (hasglass) (not (at glass ?y))))
  
  (:action GETWATER
	     :parameters (?y)
	     :precondition (and (location ?y) (hasglass)
				 (at waterfountain ?y)
				 (at monkey ?y)
				 (onbox ?y))
	     :effect (haswater)))