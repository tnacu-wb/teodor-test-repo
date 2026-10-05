import { AdditionalGuestInfo } from './additionalGuestInfo';
import { DepositPoliciesItem } from './depositPoliciesItem';
import { ReservationGuestItem } from './reservationGuestItem';
import { BillingDetails } from './billingDetails';
import { RoomStay } from './roomStay';
import { PaymentCard } from './paymentCard';

/**
 * One reservation item from reservationByIdList in bookingInformation API response
 */
export class ReservationByIdItem {
  [key: string]: unknown;
  additionalGuestInfo?: AdditionalGuestInfo;
  billing?: BillingDetails;
  depositPolicies: DepositPoliciesItem[] = [];
  paymentCard?: PaymentCard;
  reservationGuestList: ReservationGuestItem[] = [];
  reservationOverridden?: boolean;
  reservationOverrideReasons?: unknown;
  reservationPackageList?: unknown;
  roomStay?: RoomStay;

  /**
   * ReservationByIdItem
   * @param data data
   * @param data.reservationByIdItem reservationByIdItem
   */
  constructor(data: { reservationByIdItem?: Record<string, unknown> } = {}) {
    const reservationByIdItem = data.reservationByIdItem ?? {};
    this.roomStay = new RoomStay({ bookingInfo: reservationByIdItem.roomStay as Record<string, unknown> });

    const reservationGuestList = Array.isArray(reservationByIdItem.reservationGuestList)
      ? (reservationByIdItem.reservationGuestList as Array<Record<string, unknown>>)
      : [];
    this.reservationGuestList = reservationGuestList.map((item) => new ReservationGuestItem({ reservationGuestItem: item }));

    this.additionalGuestInfo = new AdditionalGuestInfo({ additionalGuestInfo: reservationByIdItem.additionalGuestInfo as Record<string, unknown> });

    const depositPolicies = Array.isArray(reservationByIdItem.depositPolicies)
      ? (reservationByIdItem.depositPolicies as Array<Record<string, unknown>>)
      : [];
    this.depositPolicies = depositPolicies.map((item) => new DepositPoliciesItem({ depositPoliciesItem: item }));

    this.billing = new BillingDetails({ billingDetails: reservationByIdItem.billing });
    this.paymentCard = new PaymentCard({ paymentCard: reservationByIdItem.paymentCard as Record<string, unknown> });
    this.reservationPackageList = reservationByIdItem.reservationPackageList;
    this.reservationOverrideReasons = reservationByIdItem.reservationOverrideReasons;
    this.reservationOverridden = reservationByIdItem.reservationOverridden as boolean | undefined;
  }

  static fromResponse(data: { reservationByIdItem?: Record<string, unknown> }): ReservationByIdItem {
    return new ReservationByIdItem(data);
  }

}
