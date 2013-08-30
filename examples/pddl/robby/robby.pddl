(define (domain robby)
	(:requirements :strips :typing)
	(:types 
		location beacon - object
		hallway room - location
		)
	(:predicates 
		(at ?l - location)
		(connected ?l1 ?l2  - location)
		(in ?b - beacon ?l - location)
		(reported ?b - beacon)
		)
	
	(:action enter
		:parameters 
			(?l1 - hallway
			 ?l2 - room)
	    :precondition
			(and (connected ?l1 ?l2) (at ?l1))
		:effect
			(and (not (at ?l1)) (at ?l2))
	)
	
	(:action exit
		:parameters 
			(?l1 - room
			 ?l2 - hallway)
 	    :precondition
 			(and (connected ?l1 ?l2) (at ?l1))
 		:effect
 			(and (not (at ?l1)) (at ?l2))
	)
	
	(:action move
		:parameters 
			(?l1 - hallway
			 ?l2 - hallway)
 	    :precondition
 			(and (connected ?l1 ?l2) (at ?l1))
 		:effect
 			(and (not (at ?l1)) (at ?l2))
	    
	)
	
	(:action report
		:parameters 
			(?l - location
			 ?b - beacon)
	    :precondition
			(and (at ?l) 
				 (in ?b ?l)
				 )
		:effect
			(and (reported ?b))
	)
)