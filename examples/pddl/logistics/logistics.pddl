(define (domain logistics)
  (:requirements :strips :typing) 
  (:types 
  	package location vehicle - object
  	truck airplane - vehicle
  	city airport - location)
  
  (:predicates 	
		(at ?vehicleOrPackage - (either vehicle package)  ?location - location)
		(in ?package - package ?vehicle - vehicle)
		(inCity ?locOrTruck - (either location truck) ?citys - city))
		
  (:action loadtruck
	:parameters
		 (?obj - package
		  ?truck - truck
		  ?loc - location)
	:precondition
		(and 	(at ?truck ?loc) 
			(at ?obj ?loc))
	:effect
		(and 	(not (at ?obj ?loc)) 
			(in ?obj ?truck)))

  (:action loadairplane
	:parameters
		(?obj - package
		 ?airplane - airplane
		 ?loc - airport)
	:precondition
		(and
			(at ?obj ?loc) 
			(at ?airplane ?loc))
	:effect
   		(and 	(not (at ?obj ?loc)) 
			(in ?obj ?airplane)))

  (:action unloadtruck
	:parameters
		(?obj - package
		 ?truck - truck
		 ?loc - location)
	:precondition
		(and    (at ?truck ?loc) 
			(in ?obj ?truck))
	:effect
		(and	(not (in ?obj ?truck)) 
			(at ?obj ?loc)))

  (:action unloadairplane
	:parameters
		(?obj - package
		 ?airplane - airplane
		 ?loc - airport)
	:precondition
		(and	(in ?obj ?airplane) 
			(at ?airplane ?loc))
	:effect
		(and 
			(not (in ?obj ?airplane)) 
			(at ?obj ?loc)))

  (:action drivetruck
	:parameters
		(?truck - truck
		 ?locFrom - location
		 ?locTo - location
		 ?city - city)
	:precondition
		(and 	(at ?truck ?locFrom)
			(incity ?locFrom ?city)
			(incity ?locTo ?city))
	:effect
		(and 	(not (at ?truck ?locFrom)) 
			(at ?truck ?locTo)))

  (:action flyairplane
	:parameters
		(?airplane - airplane
		 ?locFrom - airport
		 ?locTo - airport)
	:precondition
		(at ?airplane ?locFrom)
	:effect
		(and 	(not (at ?airplane ?locFrom)) 
		(at ?airplane ?locTo)))
)
