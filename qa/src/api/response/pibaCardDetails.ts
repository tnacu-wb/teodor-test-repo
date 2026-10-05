/**
{
    "data": {
        "getPIBACardDetails": {
            "cardHolderName": "Tethered Travel Testersons",
            "cardLimit": null,
            "cardId": 37237,
            "cardNumber": "308950*********0054",
            "myCard": true,
            "activated": false,
            "status": "CURRENT",
            "primaryUserId": 784245,
            "userId": 784245,
            "expiryDate": "2028-04-17T00:00:00.000+00:00",
            "title": "",
            "firstName": "",
            "lastName": "",
            "email": "",
            "cardAction": "CardActivate,CardAlternativeCardAddress,CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardReplaceNewNumber",
            "registeredUsers": [
                {
                    "apiUserGuid": "84e517f5-77ed-4684-89f3-8735a1d8e6f0",
                    "emailAddress": null,
                    "displayName": "Mr Tethered Travel Testersons (auto.ibtm@yopmail.com)",
                    "hasAddress": false
                }
            ],
            "cardRestriction": {
                "startDate": null,
                "endDate": null,
                "restrictCardUsage": false
            },
            "amountSpend": {
                "amount": 0,
                "currencyCode": "826"
            }
        }
    }
}
 */
export class PibaCardDetails {
  [key: string]: unknown;
  activated?: boolean;
  cardHolderName?: string;
  cardNumber?: string;
  expiryDate?: string;
  myCard?: boolean;

  /**
   * Piba card details constructor
   * @param data object data
   * @param data.pibaCard piba card object
   */
  constructor(data: { pibaCard?: Record<string, unknown> } = {}) {
    const pibaCard = data.pibaCard ?? {};
    this.cardHolderName = pibaCard.cardHolderName as string | undefined;
    this.cardNumber = pibaCard.cardNumber as string | undefined;
    this.myCard = pibaCard.myCard as boolean | undefined;
    this.activated = pibaCard.activated as boolean | undefined;
    this.expiryDate = pibaCard.expiryDate as string | undefined;
  }

  static fromResponse(data: { pibaCard?: Record<string, unknown> }): PibaCardDetails {
    return new PibaCardDetails(data);
  }
}
