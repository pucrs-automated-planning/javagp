(define ( domain dinner )
(:requirements :strips :equality)
(:predicates
	(lixo ?Obj)
	(maosLimpas ?Obj)
	(silencio ?Obj)
	(presente ?Obj)
	(jantar ?Obj))

(:action cozinhar
	:parameters(?Obj)
	:precondition(maosLimpas ?Obj)
	:effect(jantar ?Obj))
(:action embrulhar
	:parameters(?Obj)
	:precondition(silencio ?Obj)
	:effect(presente ?Obj)
)
(:action carregarLixo
	:parameters(?Obj)
	:precondition ()
	:effect(and (not(lixo ?Obj)) (not(maosLimpas ?Obj)))
)
(:action reciclarLixo
	:parameters(?Obj)
	:precondition ()
	:effect(and (not(lixo ?Obj)) (not(silencio ?Obj)))
))