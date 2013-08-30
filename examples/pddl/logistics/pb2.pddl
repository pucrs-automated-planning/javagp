(define (problem pb2)
  (:domain logistics)
  (:requirements :strips :typing)
  (:objects mxf - package
	    avrim - package
	    alex - package
	    jason  - package
	    pencil  - package
	    paper  - package
	    april  - package
	    michelle  - package
	    betty  - package
	    lisa  - package
	    airplane1 - airplane
	    airplane2 - airplane
	    lonairport  - airport
	    parairport   - airport
	    jfkairport   - airport
	    bosairport  - airport)
  (:init (at airplane1 jfkairport)
	 (at airplane2 parairport)
	 (at mxf jfkairport)
	 (at avrim parairport)
	 (at alex bosairport)
	 (at jason jfkairport)
	 (at pencil parairport)
	 (at paper lonairport)
	 (at michelle bosairport)
	 (at april parairport)
	 (at betty lonairport)
	 (at lisa lonairport)
	 )
  (:goal (and 
	  (at mxf bosairport)
	  (at avrim jfkairport)
	  (at pencil bosairport)
	  (at alex jfkairport)
	  (at april bosairport)
	  (at lisa parairport)
	  (at michelle jfkairport)
	  (at jason bosairport)
	  (at paper parairport)
	  (at betty jfkairport)
	  )
	 )
  )