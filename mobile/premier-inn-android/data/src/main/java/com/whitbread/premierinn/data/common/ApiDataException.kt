package com.whitbread.premierinn.data.common

/**
 * This exception represents a data issue that comes from the Api.
 * Most of the API responses have a loose/relaxed contract, so there might be cases that an Api Entity could not form a valid domain object.
 *
 * For example: A valid use case would be when the API returns
 * 1) a Hotel object with null address and null location
 * 2) a Booking without a reservationId etc.
 *
 * The app cannot consume these object properly cause they are missing fundamental properties for the normal execution of the system.
 *
 * Note: Whenever this exception is thrown, the app should always log the issue in the crash reporting tool.
 */
open class ApiDataException(override var message: String) : Throwable(message)