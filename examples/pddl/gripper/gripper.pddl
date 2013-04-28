(define (domain gripper)
(:requirements :strips)
(:predicates (room ?r)
             (ball ?b)
             (gripper ?g)
             (atRobby ?r)
             (at ?b ?r)
             (free ?g)
             (carry ?o ?g))

	(:action move
		:parameters  (?from ?to)
		:precondition (and  (room ?from) (room ?to) (atRobby ?from))
		:effect (and  (atRobby ?to) (not (atRobby ?from))))
		
	(:action pick
		:parameters (?obj ?room ?gripper)
		:precondition  (and  (ball ?obj) (room ?room) (gripper ?gripper)
                     (at ?obj ?room) (atRobby ?room) (free ?gripper))
		:effect (and (carry ?obj ?gripper) (not (at ?obj ?room)) 
              (not (free ?gripper))))
 
	(:action drop
		:parameters  (?obj  ?room ?gripper)
		:precondition  (and  (ball ?obj) (room ?room) (gripper ?gripper)
                     (carry ?obj ?gripper) (atRobby ?room))
	:effect (and (at ?obj ?room) (free ?gripper) (not (carry ?obj ?gripper))))
)