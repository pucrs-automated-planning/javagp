(define (problem pbP2)
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
		   (in b1 hall1)
		   (in b1 hall1)
		   (connected room1 hall1)
		   (connected hall1 room1)
		   (connected room2 hall1)
		   (connected hall1 room2)
		   (connected hall1 hall2)
		   (connected hall2 hall1)
		   (connected room2 hall2)
		)
	(:goal
		(and (reported b1) (at hall2))
		)
)