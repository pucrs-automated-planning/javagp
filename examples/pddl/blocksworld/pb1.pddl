(define (problem pb1)
   (:domain blocksworld)
   (:objects a b)
   (:init (onTable a) (onTable b) (clear a) (clear b))
   (:goal (and (on a b))))