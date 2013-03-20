(define ( domain vehicle )
(:predicates
	(vehicle_info_sent ?V)
	(location ?L)
	(weather_info ?L)
	(mobility_info ?L ?V))

	(:action getWeatherInfo
		:parameters(?L)
		:precondition(and)
		:effect(weather_info ?L)
	)

	(:action sendVehicleInfo
		:parameters(?V)
		:precondition(and)
		:effect(vehicle_info_sent ?V)
	)

	(:action mobilityInfo
		:parameters(?L ?V)
		:precondition (and( (weather_info ?L) (vehicle_info_sent ?V) ))
		:effect(mobility_info ?L ?V)
	)

	(:action getClosestLocation
		:parameters(?L)
		:precondition (and)
		:effect(location ?L)
	)

)