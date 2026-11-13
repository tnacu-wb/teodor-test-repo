/**
 * {
    "data": {
        "getAllPIBACards": {
            "innBusinessPayCardList": [
                {
                    "myCard": false,
                    "cardId": "37484",
                    "cardHolderName": "Activate Limbo Card",
                    "cardRegistration": "CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardReplaceNewNumber",
                    "cardNumber": "308950*********0211",
                    "cardStatus": "CURRENT",
                    "cardRegistrationCount": 1,
                    "isActivated": false
                },
                {
                    "myCard": false,
                    "cardId": "37486",
                    "cardHolderName": "Card Test 1235",
                    "cardRegistration": "CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardInvite,CardReplaceNewNumber",
                    "cardNumber": "308950*********0229",
                    "cardStatus": "CURRENT",
                    "cardRegistrationCount": 0,
                    "isActivated": false
                },
                {
                    "myCard": false,
                    "cardId": "37482",
                    "cardHolderName": "Cardholder Booker",
                    "cardRegistration": "CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardReplaceNewNumber",
                    "cardNumber": "308950*********0195",
                    "cardStatus": "CURRENT",
                    "cardRegistrationCount": 1,
                    "isActivated": false
                },
                {
                    "myCard": false,
                    "cardId": "37483",
                    "cardHolderName": "Cardholder Guest",
                    "cardRegistration": "CardCancel,CardChangeOwnerShipOf,CardEditUpdate,CardEditUpdateCardLimit,CardEditUpdateDisplayName,CardEditUpdateRestrictCardUsage,CardReplaceNewNumber",
                    "cardNumber": "308950*********0203",
                    "cardStatus": "CURRENT",
                    "cardRegistrationCount": 1,
                    "isActivated": false
                }
            ],
        }
    }
}
 */
export class PibaCards {
  [key: string]: unknown;
  cardHolderName?: string;
  cardId?: string;
  cardNumber?: string;
  cardRegistration?: string;
  cardRegistrationCount?: number;
  cardStatus?: string;
  isActivated?: boolean;
  myCard?: boolean;

  /**
   * Piba cards constructor
   * @param data data object
   * @param data.pibaCards piba cards object
   */
  constructor(data: { pibaCards?: Record<string, unknown> } = {}) {
    const pibaCards = data.pibaCards ?? {};
    this.myCard = pibaCards.myCard as boolean | undefined;
    this.cardId = pibaCards.cardId as string | undefined;
    this.cardHolderName = pibaCards.cardHolderName as string | undefined;
    this.cardRegistration = pibaCards.cardRegistration as string | undefined;
    this.cardNumber = pibaCards.cardNumber as string | undefined;
    this.cardStatus = pibaCards.cardStatus as string | undefined;
    this.cardRegistrationCount = pibaCards.cardRegistrationCount as number | undefined;
    this.isActivated = pibaCards.isActivated as boolean | undefined;
  }

  static fromResponse(data: { pibaCards?: Record<string, unknown> }): PibaCards {
    return new PibaCards(data);
  }
}
