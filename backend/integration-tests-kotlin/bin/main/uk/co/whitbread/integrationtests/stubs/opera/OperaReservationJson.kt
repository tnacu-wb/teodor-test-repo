package uk.co.whitbread.integrationtests.stubs.opera

/**
 * Derives the stable eight-character confirmation number used by existing fixtures.
 *
 * Shared because reservation creation returns it in a link and the lookup and update responses
 * return it as an identifier, and those now live in separate files. Internal rather than private
 * for that reason alone.
 */
internal fun confirmationNumberFor(reservationId: String): String = reservationId.takeLast(8).padStart(8, '0')

/** Derives the Opera cancellation unique id returned by a successful cancel POST. */
internal fun cancellationIdFor(reservationId: String): String = "CXL${reservationId.takeLast(7).padStart(7, '0')}"
