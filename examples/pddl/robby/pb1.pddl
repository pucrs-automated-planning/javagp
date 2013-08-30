(define (problem pb1)
	(:domain robby)
	(:requirements :strips :typing)
	(:objects 
		room1 - room
		room2 - room
		hall1 - hallway
		hall2 - hallway
		b1 - beacon
		)
	(:init (at room1)
		   (at room2)
		   (at hall1)
		   (at hall2)
		   (in b1 hall1)
		)
	(:goal
		(and (reported b1))
		)
)