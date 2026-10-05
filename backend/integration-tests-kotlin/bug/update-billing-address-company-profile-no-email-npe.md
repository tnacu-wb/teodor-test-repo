# updateBillingAddress crashes on a company profile that carries no email

- **Endpoint**: `PUT /ohip/v1/reservation/updateBillingAddress` (ohip-adapter-service)
- **Chain**: per distinct reservation id — `GET /rsv/v1/hotels/{hotelId}/reservations/{id}`
  (ten-instruction fetch set) → per selected profile — `GET /crm/v1/profiles/{profileId}`
  (six-instruction fetch set) → `PUT /crm/v1/profiles/{profileId}`

## Expected

A profile read whose 2xx body carries no `profileDetails.emails` block — the shape Opera
reports for any profile with no email on file, company profiles in particular — should map
cleanly: the billing update writes addresses only, so the missing email block is irrelevant
to this endpoint and the selected profile should still receive its `BILLING`-address `PUT`,
letting the request return its normal `204`.

## Actual

`BookerProfileOhipMapper.injectEmail` dereferences
`profile.getProfileDetails().getEmails().getEmailInfo()` before its null check guards
anything (`getEmails()` itself is unguarded), so reading a company profile without an
`emails` block fails the whole request with an unmapped HTTP 500 whose envelope carries the
literal number 400 in `errCode`, after any guest-profile updates already applied (no
rollback):

```
HTTP 500
{"errCode":400,"debugMessage":"Cannot invoke \"...crm.CompanyProfileTypeEmails.getEmailInfo()\"
 because the return value of \"...crm.ProfileType.getEmails()\" is null",
 "globalErrTextTemplate":"generic.server.exception"}
```

Observed 2026-09-01 by a live probe against the integration stack: the row 71-S6 world
(one reservation with a ReservationContact guest profile and an attached Company profile,
`booker.address.addressType=BUSINESS`, flag off) reads the guest profile (which carries
emails) and updates it, then reads the company profile served by
`booking.opera.company-profile-by-id` — whose designed full body carries `profileIdList`
and `profileDetails.addresses` but, like a real email-less Opera company profile, no
`emails` block — and crashes with the 500 above. The indicator path (71-S7,
`updateCompanyProfile=true`) reproduces identically.

Same unguarded-dereference class as the row 71 empty-profile-body NPE recorded in
`flows/ohip-adapter-service/UpdateBillingAddress.md` (Branches) and plan item 9's
unmapped-500 candidates.

## Reproduction

`UpdateBillingAddressSpec` — scenarios
"!flag off with a BUSINESS address: guest, company and contact profiles are all written"
(71-S6) and "!flag on and channel BB: guest and company indicators leave the contact
untouched" (71-S7), both disabled asserting the designed correct behaviour (`204` with the
company profile GET/PUT counted). Re-enable when the mapper guards the missing email block.
