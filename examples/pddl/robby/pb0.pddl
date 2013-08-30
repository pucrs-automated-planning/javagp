(define (problem pb0)
	(:domain robby)
	(:requirements :strips :typing)
	(:objects 
		room1 - room
		b1 - beacon
		)
	(:init (at room1)
		   (in b1 room1)
		)
	(:goal
		(and (reported b1))
		)
)