import { Strings } from '../../test-data/strings';
import { ApiCalls } from '../graphql/apiCalls';

/**
 * {
    "cancelledCardDetails": {
        "cardHolderName": "Mr Self Accountholder",
        "cardId": 41371,
        "email": "",
        "expiryDate": "2028-10-13T00:00:00.000+00:00",
        "firstName": "",
        "activated": false,
        "myCard": true,
        "lastName": "",
        "pan": "308950*********6951",
        "primarySchemeCustomerId": "784245",
        "status": "CANCELLED",
        "title": ""
    }
 */
export class CancelledCardDetails {
  [key: string]: unknown;
  cardId?: number;
  expiryDate?: string;
  pan?: string;
  status?: string;

  /**
   * CancelledCardDetails constructor
   * @param data object data
   * @param data.cancelledCardDetails cancelledCardDetails
   */
  constructor(data: { cancelledCardDetails?: Record<string, unknown> } = {}) {
    const cancelledCardDetails = data.cancelledCardDetails ?? {};
    this.cardId = cancelledCardDetails.cardId as number | undefined;
    this.expiryDate = cancelledCardDetails.expiryDate as string | undefined;
    this.pan = cancelledCardDetails.pan as string | undefined;
    this.status = cancelledCardDetails.status as string | undefined;
  }

  static fromResponse(data: { cancelledCardDetails?: Record<string, unknown> }): CancelledCardDetails {
    return new CancelledCardDetails(data);
  }

  /**
   * Cancel card and validate status
   * @param data object data
   * @param data.cancelledCardDetails cancelled card details
   */
  static async cancelCardDetails(data: {
    cancelledCardDetails?: { tetheredUserId?: string; cardId?: number; scheme?: string };
  }): Promise<void> {
    const cancelledCardDetails = data.cancelledCardDetails ?? {};
    const cancelledCard = (await ApiCalls.cancelIbPibaCard({
      tetheredUserId: cancelledCardDetails.tetheredUserId,
      cardId: cancelledCardDetails.cardId,
      scheme: cancelledCardDetails.scheme,
    })) as Record<string, unknown>;
    const expectedStatus = String(Strings.BOOKING_CANCELLED_LABEL.data.default ?? '').toUpperCase();
    if (cancelledCard.status !== expectedStatus) {
      throw new Error(`The expected card status is ${expectedStatus}, while ${String(cancelledCard.status)} has been received.`);
    }
  }

}
