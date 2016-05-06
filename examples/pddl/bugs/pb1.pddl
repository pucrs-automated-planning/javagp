; --------------------
; Problem file
; --------------------

(define (problem pb1)
  (:domain planks)

  (:requirements :strips :negative-preconditions)

  (:objects a0_1 a0_2)

  (:init
    (neighbor a0_1 a0_2)
    (hasplank a0_1 a0_2)
    (walkable a0_2)
    (at a0_1)
  )

  (:goal
    (and
      (at a0_2)
      (at a0_2)
    )
  )
)

; PLAN
; do_example(b,c)
; do_example(a,b)
