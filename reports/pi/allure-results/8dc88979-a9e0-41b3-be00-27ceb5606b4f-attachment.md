# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts >> PI Baseline E2E - Guest PIBA Pay on Arrival Amendment >> Test Book as Guest user: 1 night, 1 room (Double), 2 adults, Flex rate, meals, donations - POA BAC card and Amend by change room type. TestCase ID: 380779.
- Location: qa/tests/regressions/pi/baseline-e2e-guest-uk-hotel-flex-poa-piba-amendment.spec.ts:51:8

# Error details

```
Error: Unexpected price per night. Expected room rates were not found

expect(received).toBe(expected) // Object.is equality

Expected: true
Received: false
```

# Page snapshot

```yaml
- generic [ref=f49e1]:
  - generic [ref=f49e3]:
    - generic [ref=f49e6]:
      - link [ref=f49e9] [cursor=pointer]:
        - /url: https://www.uat.premierinn.digital/gb/en/home.html
      - generic [ref=f49e13]:
        - paragraph [ref=f49e22]: Choose your meals
        - generic [ref=f49e23]:
          - separator [ref=f49e24]
          - paragraph [ref=f49e32]: Your details
        - generic [ref=f49e33]:
          - separator [ref=f49e34]
          - paragraph [ref=f49e42]: Payment details
    - generic [ref=f49e43]:
      - generic [ref=f49e44]:
        - generic [ref=f49e45]:
          - paragraph [ref=f49e46]: Thanks for your booking, John
          - paragraph [ref=f49e47]:
            - text: A confirmation email has been sent to
            - strong [ref=f49e48]: test-a3fa0b30-f5db-4a30-ad80-a0a6586b3375@mailinator.com
            - text: .
        - separator [ref=f49e49]
        - generic [ref=f49e50]:
          - paragraph [ref=f49e51]: "Booking reference:"
          - paragraph [ref=f49e52]: AQN9299092
          - paragraph [ref=f49e53]: London Heathrow Airport (M4/J4)
          - paragraph [ref=f49e54]: Shepiston Lane, Middlesex, UB3 1RW
          - paragraph [ref=f49e55]: Tel. 0333 003 1715
        - generic [ref=f49e56]:
          - paragraph [ref=f49e58]: Room details
          - generic [ref=f49e60]:
            - generic [ref=f49e61] [cursor=pointer]:
              - generic [ref=f49e62]:
                - paragraph [ref=f49e63]: Room 1
                - paragraph [ref=f49e64]: 20 Sep - 21 Sep
              - generic [ref=f49e65]:
                - paragraph [ref=f49e66]: "Room total:"
                - paragraph [ref=f49e68]: £83.00
            - generic [ref=f49e72]:
              - generic [ref=f49e73]:
                - generic [ref=f49e74]:
                  - generic [ref=f49e75]:
                    - paragraph [ref=f49e76]: Lead guest
                    - paragraph [ref=f49e77]: Mr John Smith
                  - generic [ref=f49e78]:
                    - paragraph [ref=f49e79]: Your room
                    - paragraph [ref=f49e80]: Double room
                  - generic [ref=f49e81]:
                    - paragraph [ref=f49e82]: Your rate
                    - paragraph [ref=f49e83]: Flex - Pay now or on arrival. Fully refundable up to 1 pm on day of arrival
                  - generic [ref=f49e84]:
                    - paragraph [ref=f49e85]: Your group
                    - paragraph [ref=f49e86]: 2 Adults
                - generic [ref=f49e87]:
                  - generic [ref=f49e88]:
                    - paragraph [ref=f49e89]: Arriving
                    - generic [ref=f49e90]:
                      - generic [ref=f49e91]:
                        - paragraph [ref=f49e92]: Sunday
                        - paragraph [ref=f49e93]: Check in from 3pm
                      - generic [ref=f49e94]:
                        - paragraph [ref=f49e95]: 20 Sep 2026
                        - paragraph [ref=f49e96]: £59.00
                      - separator [ref=f49e97]
                  - generic [ref=f49e98]:
                    - paragraph [ref=f49e99]: Leaving
                    - generic [ref=f49e100]:
                      - paragraph [ref=f49e101]: Monday
                      - paragraph [ref=f49e102]: Check out before 12pm
                    - paragraph [ref=f49e103]: 21 Sep 2026
                  - separator [ref=f49e104]
                  - generic [ref=f49e105]:
                    - paragraph [ref=f49e106]: Meals
                    - generic [ref=f49e107]:
                      - paragraph [ref=f49e109]: 2 Adults Unlimited Premier Inn Breakfast
                      - paragraph [ref=f49e110]: £24.00
              - generic [ref=f49e111]:
                - paragraph [ref=f49e112]: "Room 1 total cost:"
                - paragraph [ref=f49e113]: £83.00
        - generic [ref=f49e114]:
          - paragraph [ref=f49e115]: Create an account
          - paragraph [ref=f49e116]: Making and managing bookings is even easier with a My Premier Inn account! We have your booking details to help you get started – click below to finish the last few steps.
          - button [ref=f49e117] [cursor=pointer]:
            - paragraph [ref=f49e118]: Create account
        - generic [ref=f49e121]:
          - paragraph [ref=f49e122]: Hotel directions
          - generic [ref=f49e125]:
            - generic:
              - button "Keyboard shortcuts"
            - region "Map" [ref=f49e126]
            - generic [ref=f49e127]:
              - generic [ref=f49e149] [cursor=pointer]
              - iframe [ref=f49e150]:
                
              - button "Toggle fullscreen view" [ref=f49e151] [cursor=pointer]
              - link "Open this area in Google Maps (opens a new window)" [ref=f49e153] [cursor=pointer]:
                - /url: https://maps.google.com/maps?ll=51.496015,-0.447979&z=13&t=m&hl=en-GB&gl=US&mapclient=apiv3
                - img "Google" [ref=f49e155]
              - generic [ref=f49e156]:
                - button "Keyboard shortcuts" [ref=f49e162] [cursor=pointer]
                - generic [ref=f49e163]: Map data ©2026 Google
                - link "Terms (opens in new tab)" [ref=f49e172] [cursor=pointer]:
                  - /url: https://www.google.com/intl/en-GB_US/help/terms_maps.html
                  - text: Terms
                - link "Report a map error (opens in new tab)" [ref=f49e177] [cursor=pointer]:
                  - /url: https://www.google.com/maps/@51.496015,-0.447979,13z/data=!10m1!1e1!12b1?source=apiv3&rapsrc=apiv3
                  - text: Report a map error
          - paragraph [ref=f49e179]: From M4 exit Jtn 4 then follow signs to Uxbridge remaining in left hand lane. Bear left following signs for other routes and Hayes. At give way point, use as a roundabout and take the 4th exit off (sign posted Hayes). The hotel is 200 yards away on the right hand side. (SAT NAV - UB3 1RW) Parking costs 12 GBP per night for Premier Inn guests.
        - generic [ref=f49e181]:
          - generic [ref=f49e182]:
            - generic [ref=f49e183]: On-line charitable pledge
            - generic [ref=f49e184]: £3.00
          - separator [ref=f49e185]
          - generic [ref=f49e186]:
            - generic [ref=f49e187]:
              - generic [ref=f49e188]: "Total cost:"
              - paragraph [ref=f49e189]: Thank you, your payment will be taken on arrival.
            - paragraph [ref=f49e191]: £86.00
        - button "Continue to homepage" [ref=f49e193] [cursor=pointer]
        - generic [ref=f49e200]:
          - heading "We keep your personal data safe and secure." [level=3] [ref=f49e202]
          - generic [ref=f49e203]:
            - paragraph [ref=f49e205]: "We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE."
            - link "View our Privacy Notice" [ref=f49e206] [cursor=pointer]:
              - /url: https://www.uat.premierinn.digital/gb/en/terms/privacy-policy.html
          - button [ref=f49e207] [cursor=pointer]:
            - heading "Find out more" [level=3] [ref=f49e208]
      - generic [ref=f49e212]:
        - button "Continue to homepage" [ref=f49e213] [cursor=pointer]
        - button [ref=f49e214] [cursor=pointer]:
          - paragraph [ref=f49e218]: Print details
        - link "Book a table" [ref=f49e223] [cursor=pointer]:
          - /url: /gb/en/restaurants/the-social/london-heathrow-airport-m4j4/book
  - alert [ref=f49e224]: /en/booking-a1/confirmation?reservationId=AQN-9fb2ea30-9fa1-4684-b663-1d980c9428d2
  - generic:
    - region "Notifications-top"
    - region "Notifications-top-left"
    - region "Notifications-top-right"
    - region "Notifications-bottom-left"
    - region "Notifications-bottom"
    - region "Notifications-bottom-right"
```

# Test source

```ts
  604 |     for (const reservation of reservationByIdList) {
  605 |       const roomStay = this.asObject(reservation.roomStay);
  606 |       const expectedPackages = this.asArray(await ApiCalls.graphqlGetAncillariesWiFiExtras({
  607 |         hotelId: String(this.bookingConfirmationData.hotelId ?? ''), bookingFlowId, nightsNumber, startDate: arrivalDate, endDate: departureDate,
  608 |         adultsNumber: Number(roomStay.adultsNumber ?? 0), childrenNumber: Number(roomStay.childrenNumber ?? 0),
  609 |       }));
  610 |       const actualPackage = this.asArray(reservation.reservationPackageList).find((pkg) => String(pkg.packageCode ?? '') === Constants.WIFI_CODE);
  611 |       if (noPackage) {
  612 |         global.expect(actualPackage, 'WiFi package is present and it was not expected').toBeUndefined();
  613 |       } else {
  614 |         global.expect(Number(this.asObject(actualPackage).unitPrice ?? 0), 'Unexpected WiFi package unit price').toBe(Number(this.asObject(expectedPackages[0]).price ?? 0));
  615 |       }
  616 |     }
  617 |   }
  618 | 
  619 |   /**
  620 |    * Validate rates per night
  621 |   * @param {Array<number | Array<ExpectedRatePerNight>>} expectedRatesPerRoomPerNight expected rates per room
  622 |    * @param {Number} discountAmount the discount amount applied
  623 |    * @param {Boolean} includesCityTax true if the expectedRatesPerRoomPerNight per room includes the city tax.
  624 |    */
  625 |   async validateRatesPerNight(
  626 |     expectedRatesPerRoomPerNight: number[] | ExpectedRatePerNight[][],
  627 |     discountAmount: number = 0,
  628 |     includesCityTax: boolean = true,
  629 |   ): Promise<void> {
  630 |     console.log('Validate rates per night');
  631 |     const expectedRates = Array.isArray(expectedRatesPerRoomPerNight) ? expectedRatesPerRoomPerNight : [];
  632 |     const discount = Number(discountAmount ?? 0);
  633 |     const includesTax = Boolean(includesCityTax ?? true);
  634 |     const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
  635 | 
  636 |     if (expectedRates.length > 0 && typeof expectedRates[0] === 'number') {
  637 |       const expectedFlat = expectedRates as number[];
  638 |       const firstRoom = this.asObject(reservationByIdList[0]);
  639 |       const ratesPerNight = this.asArray(this.asObject(firstRoom.roomStay).ratesPerNight);
  640 |       global.expect(ratesPerNight.length, 'Rates per room per night rooms count').toBe(expectedFlat.length);
  641 |       for (let i = 0; i < ratesPerNight.length; i++) {
  642 |         const rate = this.asObject(ratesPerNight[i]);
  643 |         const cityTax = includesTax ? Number(rate.cityTaxPerNight ?? 0) : 0;
  644 |         const actual = Number((Number(rate.pricePerNight ?? 0) + cityTax).toFixed(2));
  645 |         const expected = Number(Number(expectedFlat[i]).toFixed(2));
  646 |         global.expect(actual, 'Unexpected price per night').toBe(expected);
  647 |       }
  648 |       return;
  649 |     }
  650 | 
  651 |     global.expect(reservationByIdList.length, 'Rates per room per night rooms count').toBe(expectedRates.length);
  652 |     const unmatchedRooms = [...reservationByIdList];
  653 |     for (const expectedRoom of expectedRates as ExpectedRatePerNight[][]) {
  654 |       let hasThisRoomPricePerNight = false;
  655 |       let hasThisRoomStartDate = false;
  656 | 
  657 |       for (let roomIndex = 0; roomIndex < unmatchedRooms.length; roomIndex++) {
  658 |         const room = this.asObject(unmatchedRooms[roomIndex]);
  659 |         const rates = this.asArray(this.asObject(room.roomStay).ratesPerNight);
  660 |         let totalPricePerNight = 0;
  661 |         let expectedTotalPricePerNight = 0;
  662 | 
  663 |         for (let rateIndex = 0; rateIndex < rates.length; rateIndex++) {
  664 |           const rate = this.asObject(rates[rateIndex]);
  665 |           const expectedRate = this.asObject(expectedRoom[rateIndex]);
  666 |           const cityTaxAmount = includesTax ? Number(rate.cityTaxPerNight ?? 0) : 0;
  667 |           const expectedPricePerNightValue = (roomIndex === 0 && rateIndex === 0 && discount > 0)
  668 |             ? Number(expectedRate.pricePerNight ?? 0) - discount
  669 |             : Number(expectedRate.pricePerNight ?? 0);
  670 | 
  671 |           hasThisRoomPricePerNight = Number((Number(rate.pricePerNight ?? 0) + cityTaxAmount).toFixed(2))
  672 |             === Number(Number(expectedPricePerNightValue).toFixed(2));
  673 |           hasThisRoomStartDate = String(rate.startDate ?? '') === String(expectedRate.date ?? '');
  674 | 
  675 |           // Workaround for date mismatch issue — check if dates are off by 1 day
  676 |           if (!hasThisRoomStartDate && rate.startDate && expectedRate.date) {
  677 |             const rateDate = new Date(String(rate.startDate));
  678 |             const expectedDate = new Date(String(expectedRate.date));
  679 |             const dateDiffMs = Math.abs(rateDate.getTime() - expectedDate.getTime());
  680 |             const oneDayMs = 24 * 60 * 60 * 1000;
  681 |             if (dateDiffMs === oneDayMs) {
  682 |               hasThisRoomStartDate = true;
  683 |             }
  684 |           }
  685 | 
  686 |           expectedTotalPricePerNight += expectedPricePerNightValue;
  687 |           totalPricePerNight += Number(rate.pricePerNight ?? 0);
  688 | 
  689 |           if (discount === 0 && (!hasThisRoomPricePerNight || !hasThisRoomStartDate)) {
  690 |             break;
  691 |           }
  692 |         }
  693 | 
  694 |         if (discount > 0 && Number(totalPricePerNight.toFixed(2)) === Number(expectedTotalPricePerNight.toFixed(2))) {
  695 |           hasThisRoomPricePerNight = true;
  696 |         }
  697 | 
  698 |         if (hasThisRoomPricePerNight && hasThisRoomStartDate) {
  699 |           unmatchedRooms.splice(roomIndex, 1);
  700 |           break;
  701 |         }
  702 |       }
  703 | 
> 704 |       global.expect(hasThisRoomPricePerNight, 'Unexpected price per night. Expected room rates were not found').toBe(true);
      |                                                                                                                 ^ Error: Unexpected price per night. Expected room rates were not found
  705 |       global.expect(hasThisRoomStartDate, 'Unexpected rate start date. Expected room dates were not found').toBe(true);
  706 |     }
  707 |   }
  708 | 
  709 |   /**
  710 |    * Validate Currency
  711 |    * @param {String} expectedCurrency expected currency
  712 |    */
  713 |   async validateCurrency(expectedCurrencyInput: string): Promise<void> {
  714 |     const expectedCurrency = String(expectedCurrencyInput ?? '');
  715 |     global.expect(String(this.bookingConfirmationData.currencyCode ?? ''), 'Unexpected currency code').toBe(expectedCurrency);
  716 |   }
  717 | 
  718 |   /**
  719 |    * Validate Booking flow id
  720 |    * @param {String} expectedBookingFlowId expected booking flow id
  721 |    */
  722 |   async validateBookingFlowId(expectedBookingFlowIdInput: string): Promise<void> {
  723 |     const expectedBookingFlowId = String(expectedBookingFlowIdInput ?? '');
  724 |     global.expect(String(this.bookingConfirmationData.bookingFlowId ?? ''), 'Unexpected booking flow id').toBe(expectedBookingFlowId);
  725 |   }
  726 | 
  727 |   /**
  728 |    * Validate policy code
  729 |    * @param {String} policyCode policy code 
  730 |    */
  731 |   async validatePolicyCode(policyCodeInput: string): Promise<void> {
  732 |     const policyCode = String(policyCodeInput ?? '');
  733 |     global.expect(String(this.bookingConfirmationData.policyCode ?? ''), 'policyCode is not as expected').toBe(policyCode);
  734 |   }
  735 | 
  736 |   /**
  737 |    * Format a date as an ISO calendar day in UTC.
  738 |    * @param date date to format
  739 |    * @returns ISO date in YYYY-MM-DD format
  740 |    */
  741 |   private toIsoDayDate(date: Date): string {
  742 |     const year = date.getUTCFullYear();
  743 |     const month = `${date.getUTCMonth() + 1}`.padStart(2, '0');
  744 |     const day = `${date.getUTCDate()}`.padStart(2, '0');
  745 |     return `${year}-${month}-${day}`;
  746 |   }
  747 | 
  748 |   /**
  749 |    * Calculate the number of nights between arrival and departure dates.
  750 |    * @param arrivalDate arrival date string
  751 |    * @param departureDate departure date string
  752 |    * @returns number of nights between the supplied dates
  753 |    */
  754 |   private getNightsBetween(arrivalDate: string, departureDate: string): number {
  755 |     return Math.round((new Date(departureDate).getTime() - new Date(arrivalDate).getTime()) / (24 * 60 * 60 * 1000));
  756 |   }
  757 | 
  758 |   /**
  759 |    * Validate room total price 
  760 |    * @param {Array<String>} expectedRoomPrices expectedRoomPrices
  761 |    * @param {Number} discountAmount the discount amount applied
  762 |    * @param {Boolean} includesCityTax true if the final expected room price should include the city tax
  763 |    */
  764 |   async validateRoomPrice(
  765 |     expectedRoomPricesInput: Array<number | string | Record<string, unknown>>,
  766 |     discountAmount: number = 0,
  767 |     includesCityTax: boolean = false,
  768 |   ): Promise<void> {
  769 |     console.log('Validate room total price');
  770 |     const expectedRoomPrices = Array.isArray(expectedRoomPricesInput) ? expectedRoomPricesInput : [];
  771 |     const discount = Number(discountAmount ?? 0);
  772 |     const includesTax = Boolean(includesCityTax ?? false);
  773 |     const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
  774 | 
  775 |     global.expect(reservationByIdList.length, 'Total price rooms count mismatch').toBe(expectedRoomPrices.length);
  776 |     const unmatchedRooms = [...reservationByIdList];
  777 | 
  778 |     for (const expectedRoomPriceRaw of expectedRoomPrices) {
  779 |       const expectedRoomPriceObj = this.asObject(expectedRoomPriceRaw);
  780 |       const expectedRoomPrice = Number(
  781 |         typeof expectedRoomPriceRaw === 'number' || typeof expectedRoomPriceRaw === 'string'
  782 |           ? expectedRoomPriceRaw
  783 |           : (expectedRoomPriceObj.totalPrice ?? 0),
  784 |       );
  785 | 
  786 |       let hasThisRoomPrice = false;
  787 |       for (let roomIndex = 0; roomIndex < unmatchedRooms.length; roomIndex++) {
  788 |         const room = this.asObject(unmatchedRooms[roomIndex]);
  789 |         const roomStay = this.asObject(room.roomStay);
  790 |         const ratesPerNight = this.asArray(roomStay.ratesPerNight);
  791 |         const cityTaxAmount = includesTax
  792 |           ? ratesPerNight.reduce((totalCityTax, currentValue) => totalCityTax + Number(this.asObject(currentValue).cityTaxPerNight ?? 0), 0)
  793 |           : 0;
  794 | 
  795 |         let expectedRoomPriceValue = discount > 0
  796 |           ? Math.trunc((expectedRoomPrice + Math.round(cityTaxAmount * 100) / 100 - discount) * 100) / 100
  797 |           : Math.trunc((expectedRoomPrice + Math.round(cityTaxAmount * 100) / 100) * 100) / 100;
  798 | 
  799 |         const actualRoomPrice = Number((Math.round(Number.parseFloat(String(roomStay.roomPrice ?? 0)) * 100) / 100).toFixed(2));
  800 |         expectedRoomPriceValue = Number((Math.round(Number.parseFloat(String(expectedRoomPriceValue)) * 100) / 100).toFixed(2));
  801 | 
  802 |         hasThisRoomPrice = actualRoomPrice === expectedRoomPriceValue
  803 |           || Number((actualRoomPrice + 0.01).toFixed(2)) === expectedRoomPriceValue
  804 |           || actualRoomPrice === Number((expectedRoomPriceValue + 0.01).toFixed(2));
```