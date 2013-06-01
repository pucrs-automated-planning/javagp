(define (problem pb4)
  (:domain dwr)
  (:requirements :typing :negative-preconditions)
  (:objects
      pallet - container
      l1 l2 l3 l4 l5 - location
      p1 p2 p3 - pile
      k1 k2 k3 - crane
      r1 r2 - robot
      c1 c2 c3 c4 c5 - container)
	(:init 
		(adjacent l1 l2) (adjacent l2 l1) (adjacent l2 l3) (adjacent l3 l2)
		(adjacent l3 l4) (adjacent l4 l3) (adjacent l4 l5) (adjacent l5 l4)
		(attached p1 l1) (attached p2 l3) (attached p3 l5)
		(belong k1 l1) (belong k2 l3) (belong k3 l5)
    (empty k1) (empty k2) (empty k3)
    
		(unloaded r1)
		(occupied l1)
		(at r1 l1)

		(unloaded r2)
		(occupied l5)
		(at r2 l5)
				
		(in pallet p1) (in c1 p1) (in c2 p1) (in c3 p1) (in c4 p1) (in c5 p1) 
		(on c1 c2) (on c2 c3) (on c3 c4) (on c4 c5) (on c5 pallet)
		(top c1 p1)
		
    (in pallet p2)
		(top pallet p2)

		(in pallet p3)
		(top pallet p3)
  )
	(:goal (and (in c5 p3) ))
)