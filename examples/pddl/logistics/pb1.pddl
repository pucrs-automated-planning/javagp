(define (problem pb1)
  (:domain logistics)
  (:requirements :strips :typing) 
  (:objects mxf - package
	    avrim - package
	    alex - package
	    jason - package
	    pencil - package
	    paper - package
	    april - package
	    michelle - package
	    betty - package
	    lisa - package
	    airplane1 - airplane
	    airplane2 - airplane
	    lonairport - airport
	    parairport -  airport
	    jfkairport -  airport
	    bosairport -  airport)
  (:init (at airplane1 jfkairport)
	 (at airplane2 bosairport)
	 (at mxf parairport)
	 (at avrim parairport)
	 (at alex parairport)
	 (at jason jfkairport)
	 (at pencil lonairport)
	 (at paper lonairport)
	 (at michelle lonairport)
	 (at april lonairport)
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