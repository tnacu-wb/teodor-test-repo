package com.whitbread.premierinn.reviewbooking

class RateTypeException(rateType: String) : IllegalArgumentException("Unknown rate type ${rateType}")