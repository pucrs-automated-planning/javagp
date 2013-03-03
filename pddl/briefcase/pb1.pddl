(define (problem pb1)
   	(:domain briefcase)
   	(:requirements :strips :typing :conditional-effects :universal-preconditions)
   	(:objects home l1 - location 
   			  o1 - portable)
	(:init (isat home) (at o1 l1))              
	(:goal (and (isat home)  (at o1 home)))
)