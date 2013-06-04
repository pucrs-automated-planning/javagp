(define (domain dwr)
	(:requirements :strips :typing :negative-preconditions)

    (:types 
		robot
		location  
		container	
		pile                                      
		crane        
	)
	
	(:constants-def pallet)	
	
	(:predicates 
				 (at ?r - robot ?l - location)
				 (loaded ?r - robot ?c - container)
				 (unloaded ?r - robot )
				 (on ?c - container ?c2 - container )
				 (adjacent ?l - location ?l2 - location )
				 (attached ?p - pile ?l - location )
				 (belong ?k - crane ?l - location)
				 (occupied ?l - location)
				 (holding ?k - crane ?c - container)
				 (empty ?k - crane)
				 (in ?c - container ?p - pile)
				 (top ?c - container ?p - pile)
	)
	
	(:action move
	 :parameters( ?r - robot ?from - location ?to - location )
	 :precondition(and (adjacent ?from ?to) (not (occupied ?to )) (at ?r ?from))
	 :effect (and (at ?r ?to) (not (at ?r ?from )) (occupied ?to) (not (occupied ?from))  )
	)
					   
	(:action take
	 :parameters(?c - container ?c2 - container ?k - crane ?p - pile ?l - location)
	 :precondition(and (empty ?k) (attached ?p ?l) (belong ?k ?l) (top ?c ?p) (in ?c ?p) (on ?c ?c2) )
	 :effect(and (not(empty ?k)) (not(top ?c ?p)) (holding ?k ?c) (not(in ?c ?p)) (not(on ?c ?c2)) (top ?c2 ?p))
	)

	(:action putdown 
	 :parameters(?c - container ?c2 - container ?k - crane ?p - pile ?l - location)
	 :precondition(and (holding ?k ?c) (attached ?p ?l) (belong ?k ?l) (top ?c2 ?p) )
	 :effect(and (not(holding ?k ?c)) (top ?c ?p) (not(top ?c2 ?p)) (in ?c ?p) (on ?c ?c2) (empty ?k)  )
	)
	
	
	(:action load
	 :parameters(?c - container ?k - crane ?r - robot ?l - location)
	 :precondition(and (holding ?k ?c) (unloaded ?r) (at ?r ?l) (belong ?k ?l))
	 :effect(and (not(holding ?k ?c)) (not(unloaded ?r)) (loaded ?r ?c) (empty ?k))
	)

	(:action unload
	 :parameters(?c - container ?k - crane ?r - robot ?l - location)
	 :precondition(and (empty ?k) (loaded ?r ?c) (at ?r ?l) (belong ?k ?l) )
	 :effect(and (not(empty ?k)) (not(loaded ?r ?c)) (unloaded ?r) (holding ?k ?c))
	)
)