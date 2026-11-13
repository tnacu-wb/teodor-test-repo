# Regression Pack Analysis

> Test coverage summary for the Premier Inn digital platform E2E regression suite.

## Summary

| Application | Baselines | Smoke / E2E Journeys | Total |
|-------------|:---------:|:--------------------:|:-----:|
| PI          | 40        | 7                    | **47**  |
| CCUI        | 26 (1 skipped) | 7              | **33**  |
| PIB (InnBusiness) | —   | 121 (110 E2E + 11 smokes) | **121** |
| **Total**   | **66**    | **135**              | **201** |

> Note: these counts are verified against the reference tree. Earlier versions of this
> document listed PI 38/45, CCUI 25/32, PIB 94, total 171. The discrepancies were:
> PI baselines miscounted (40, not 38 — two specs with paired TC IDs were undercounted),
> CCUI baselines (26, not 25 — the `describe.skip` spec was excluded from the count),
> PIB (121, not 94 — the analysis originally used the regression-pack-analysis.md file
> which predated the InnBusiness expansion; see rationalisation doc Appendix A.1).

---

## PI (premierinn.com)

### PI Baselines (40 tests)

| TestCase ID | Name |
|-------------|------|
| 380786 | Book as guest DE Hotel City Tax 2 nights Family Room 2 Adults 1 Child Amend Remove Adult |
| 266203 | Amend Logged user DE Hotel City Tax Check Dates Remove Room Guests Meals Add Breakfast |
| 223456 | Amend Logged in UK Hub Hotel pay now shorten period add room guest meal |
| 266200 | Amend Logged user UK Hotel Semi-Flex pay now shorten period add room guest kid meal remove breakfast |
| 195251 | Amend Logged user UK Hotel PIBA extend checkout 2 days add room guest kid meal |
| 373793 | Book as guest UK Hotel 1 night 1 room 1 adult no meals Pay Now Employee Website |
| 76466 | Book as guest DE Hotel multi nights one room include meals POA |
| 385014 | Book as Guest DE Hotel 1 night 1 room booking for someone else check-in guest POA |
| 365944 | Book as Guest DE Hotel different billing address booking for someone else POA |
| 76468 | Book as guest DE Hotel multi nights 1 room POA Amend adding breakfast |
| 76467 | Book as guest DE Hotel 1 night 1 room Reserve without credit card with amend |
| 389728 | Book as guest DE Hotel 2 nights 2 rooms 4 adults Flex No meals reserve without card check-in info |
| 389724 | Booker is guest DE Hotel 1 room 1 Guest Booking for someone else |
| 530589 | Baseline booking as guest Hub hotel windowless room flow |
| 385301 | Booker as guest Create Booking Guest user for Reg form (pre-checkin single room) |
| 76469 | Book as guest UK Hotel nine nights four rooms include meals POA PIBA |
| 460870, 464491 | Book guest UK Hotel 1 night 2 rooms meals advance PN Amend Remove room & meal Cancel |
| 90661 | Book as guest UK Hotel one night one room include meals PN Remove breakfast |
| 435201 | Create PN booking 2 rooms WiFi Amend remove room containing WiFi Cancel |
| 376400 | Book as guest HUB Hotel multi nights 1 room PN Amend adding breakfast |
| 366969 | Book as guest UK Hotel 4 nights TWIN room Pay Now AMEX Amend increasing stay dates |
| 76491 | Book as logged user DE Hotel 2 nights 2 rooms 4 adults 2 kids FLEX meals POA |
| 389731 | Book as Logged user DE hotel 2 nights 4 rooms 4 adults Flex No meals reserve without card |
| 385302, 435918 | Booker as logged user DE Hotel 1 room 1 Guest Single Room Booking pre-checkin |
| 389727 | Booker logged in user DE Hotel 1 room 1 Guest Booking for someone else |
| 531538 | Baseline booking as logged user Hub hotel windowless room flow |
| 380774 | Book as Logged user 1 night 1 room Double Flex rate POA amend add breakfast |
| 222161 | Book as Logged user 1 night 1 room 2 adults Semi-Flex meals donations PN |
| 76495 | Book as logged user UK Hotel 1 night 1 room 2 adults meals saved Card PN Accessible LOWDBL amend |
| 376362 | Logged in UK Hotel Flex 1 night 1 adult PIBA allowances POA Amend extend checkout add room guest kid |
| 380777 | Logged in UK Hotel Flex 1 night TWIN room BAC POA Amend increasing stay nights |
| 382591 | Booking with ECI LCO packages one room POA Normal Card (UK, DE, IR hotels) |
| 374328 | Book as guest DE Hotel City Tax Single night Single room 2 Adults Amend add a room |
| 380779 | Book as Guest 1 night 1 room 2 adults Flex meals donations POA BAC Amend change room type |
| 376973 | Book as guest UK Hotel 2 nights 2 rooms 4 adults 2 kids POA PIBA Amend changing Arrival date |
| 366971 | Book as guest HUB Hotel multi nights 1 room meals POA BIGWIN Amend reducing nights |
| 380783 | Book as Logged User DE Hotel City Tax Single night Single room 1 Adult Amend adding adult |
| 374857 | Book as logged user DE Hotel 2 nights 6 adults no meals Pay Now Amend remove room |
| 418503 | Book as logged user UK Hotel 1 night 1 room Pay now multiple amends |
| 76425 | Book as guest UK Hotel 1 night 1 room meals Pay now FMTRPL with amend |

### PI Smokes (7 tests)

| TestCase ID | Name |
|-------------|------|
| 468893 | Amend by adding one adult UK/DE Hotel 1 night 1 room 1 adult no meals PN |
| 468894 | Cancel a booking UK/DE Hotel 1 night 1 room 1 adult no meals PN |
| 468890 | Book as guest DE Hotel 1 night 1 room 1 adult no meals PN Visa |
| 468889 | Book as guest UK Hotel 1 night 1 room 1 adult no meals POA PIBA |
| 468892 | Book as logged-in user DE Hotel 1 night 1 room 1 adult no meals PN Visa |
| 468891 | Book as logged-in user UK Hotel 1 night 1 room 1 adult no meals POA PIBA |
| 115179 | Book PI UK Hotel 3 nights 1 room meals donations Pay now or POA FMQUAD |

---

## CCUI (Call Centre UI)

### CCUI Baselines (26 tests, 1 skipped)

| TestCase ID | Name |
|-------------|------|
| 381188 | Book as Agent DE Hotel 1 night 1 Single room Flex Leisure PN Amend add adult |
| 381191 | Book as Agent DE Hotel 1 night 1 room 2 adults Flex Leisure PN Amend remove adult |
| 381189 | Book as Agent DE Hotel 1 night 2 rooms Flex Leisure home address PN Amend remove room |
| 381190 | Book as Agent DE Hotel 1 night 2 rooms Flex Leisure PN Amend add room |
| 379921 | Book as Agent DE Hotel 2 nights 2 adults Double Flex PN non BAC with amend |
| 380723 | Book as Agent UK Hotel 1 night 2 rooms 4 adults FLEX PN Amend room type add child |
| 379925 | Book as Agent UK Hotel 1 night 1 Single room Flex Leisure POA BAC Amend add adult |
| 428938 | Book as Agent UK Hotel 1 night 2 Double rooms 4 adults breakfast Contract rate Company ID PN Visa |
| 428939 | Book as Agent UK Hotel 3 nights 1 Accessible room 2 adults Breakfast Contract rate Corp ID PN Visa |
| 380724 | Book as Agent UK Hotel 3 nights 2 adults Accessible FLEX PN Non BAC Amend decrease nights |
| 369555 | Book as Agent UK Hotel 1 night 1 Double 2 adults standard rate breakfast Leisure PN Non-BAC |
| 369930 | Book as Agent DE Hotel 1 night 2 rooms 3 adults 2 kids Advanced breakfast PN with amend |
| 379940 | Book as Agent UK Hotel 1 night TWIN 2 adults Flex Leisure POA BAC Amend increase stay nights |
| 384400 | Change payment method Confirm changes Account to Company (DE) |
| 384401 | Change payment method Confirm changes New Credit/Debit Card |
| 368688 | Manager DE Hotel RWCC Amend extend days remove room guest kid meal add kid breakfast |
| 369933, 408182 | Manager DE Hotel 1 night 4 rooms 7 adults 1 kid Flex breakfast POA A2C Company ref |
| 369977 | Book as Manager 9 nights 4 rooms 8 adults Hub Flex PN Amend remove room |
| 461417, 464541 | Manager UK Hotel multiple nights 1 room meals advance PN Amend decrease night add room Cancel |
| 369962 | Manager DE Hotel 2 nights 2 rooms Family Accessible 3 adults 1 kid Contract rate A2C |
| 369556 | Manager UK Hotel MLOS 2 nights 4 adults Flex Continental breakfast Business POA **(skipped)** |
| 369964 | Manager UK Hotel 1 night 2 rooms 3 adults 1 kid Semi-Flex breakfast PN Amend increase nights |
| 369950 | Manager DE Hotel 2 nights 2 rooms 3 adults 1 kid Contract rate child breakfast Non Guaranteed |
| 380725 | Manager UK Hotel 1 night 2 rooms 4 adults 1 kid Semi FLEX child breakfast PN Amend remove child meal |
| 409154 | Capture accompanying guest details Multi Room Combination |
| 531538 | View and select as Agent Hub hotel windowless room continue to payment |

### CCUI Smokes (7 tests)

| TestCase ID | Name |
|-------------|------|
| 470152 | Book as Agent Hotel 1 night 1 room 1 adult FLEX no meals |
| 474131 | Agent amend by adding one adult CCUI UK/DE Hotel 1 night 1 room PN |
| 474133 | Agent cancel a booking CCUI UK/DE Hotel 1 night 1 room PN |
| 470151 | Book as Manager Hotel 1 night 1 room 1 adult FLEX no meals |
| 474130 | Manager amend by adding one adult CCUI UK/DE Hotel 1 night 1 room PN |
| 474132 | Manager cancel a booking CCUI UK/DE Hotel 1 night 1 room PN |
| 115170 | DE Hotel 1 Night 1 Room Flex No meals Non Guaranteed Booking FMTRPL |

---

## PIB (InnBusiness)

### PIB E2E Journeys (110 tests across 9 journey areas)

#### Auth Journey (5 tests)

| TestCase ID | Name |
|-------------|------|
| 469744, 440413, 483451 | Link An Existing Account UK |
| 467846 | Login Reset Password Login DE Account |
| 467953, 466687, 466925, 466713, 466714, 466715, 467347, 467198 | Register Finance User UK |
| *(TBD)* | Sign Up Booker Existing Company UK |
| 467769, 446971 | Sign Up New Company DE |

#### Booking Rebranding Journey (34 tests)

| TestCase ID | Name (representative samples) |
|-------------|------|
| 450422, 450424 | Booking Rebranding UK Hotel POA |
| *(34 total)* | Various booking/amend flows as Booker, Manager, Self-Booker |

#### Card Management Journey (15 tests)

#### Company Management Journey (9 tests)

#### Home Journey (5 tests)

#### Pay App Journey (14 tests)

#### Profile Management Journey (15 tests)

#### Spending & Reporting Journey (6 tests)

#### User Management Journey (7 tests)

### PIB Smokes (11 tests)

| TestCase ID | Name (representative sample) |
|-------------|------|
| 469383 | Book as Booker 1 night 1 room BFlex PN MasterCard |

---

## Grand Totals

| Category | PI | CCUI | PIB | Total |
|----------|:--:|:----:|:---:|:-----:|
| Baselines | 40 | 26 | — | **66** |
| Smokes / E2E | 7 | 7 | 121 | **135** |
| **Total** | **47** | **33** | **121** | **201** |
