# OHIP Adapter Service - Booking model and default-stub gaps

This is a live queue of unresolved gaps that work inside
`backend/integration-tests-kotlin` can fix: Booking-model facts, default-stub shapes, and
testkit capabilities. Remove an entry when its blocker is resolved. Service-only bugs and
historical implementation notes belong in `bug/`, flow docs, or plans rather than here.
When no unresolved entries remain, leave this file empty.

## Deferred design cleanup — reservationPreferences double duty (from Wave 1 code review)

**Status:** deferred to the Wave 5 row-97 support pass; unresolved.

`BookingRoom.reservationPreferences` currently both renders `preferenceCollection` on the
reservation GET and flips the reservation PUT to a restrictive exactly-these-collections pin,
and it consumes the one-update-fact-per-room exclusivity slot. A future read-only journey
(row 97 reads preferences) that states the fact just to seed the GET would silently restrict
that booking's PUT and be blocked from combining the seed with a genuine after-update fact.
When row 97's support lands, split it into a read-side holdings fact and a write-pin fact (or
exempt the read-only use from the exclusivity guard), keeping both stub IDs unchanged.
