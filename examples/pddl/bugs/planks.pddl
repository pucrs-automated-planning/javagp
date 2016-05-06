; --------------------
; Domain file
; --------------------
(define (domain planks)
	
	(:requirements :strips :negative-preconditions)

	(:predicates
		(at ?x_y)
		(walkable ?x_y)
		(neighbor ?x_y1 ?x_y2)
		(hasplank ?x_y1 ?x_y2)
	)

	(:action walk

		:parameters (?x_y1 ?x_y2)

		:precondition
		(and
			(at ?x_y1)
			(walkable ?x_y2)
			(neighbor ?x_y1 ?x_y2)
			(hasplank ?x_y1 ?x_y2)
		)

		:effect
		(and
			(at ?x_y)
			(not (at ?x_y))
			(not (walkable ?x_y))
			(walkable ?x_y)
		)
	)

	(:action moveplank

		:parameters (?x_y0 ?x_y1 ?x_y2)

		:precondition
		(and
			(at ?x_y0)
			(hasplank ?x_y1 ?x_y2)
			(not (hasplank ?x_y1 ?x_y2))
		)

		:effect
		(and
			(not (hasplank ?x_y0 ?x_y1))
			(hasplank ?x_y1 ?x_y2)
		)
	)

)
