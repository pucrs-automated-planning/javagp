(define (problem pb1)
    (:domain gripper)
  	(:objects roomA roomB Ball1 Ball2 left right)

	(:init 
		(room roomA)
		(room roomB)
		(ball Ball1)
		(ball Ball2)
		(gripper left)
		(gripper right)
		(atRobby roomA) 
		(free left) 
		(free right)  
		(at Ball1 roomA)
		(at Ball2 roomA))

	(:goal (and (at Ball1 roomB) (at Ball2 roomB)) )
)