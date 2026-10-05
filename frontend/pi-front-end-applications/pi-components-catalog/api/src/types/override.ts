export interface OverrideReason {
  code: string;
  name: string;
  description: string;
  active: boolean;
  managerApprovalNeeded: boolean;
}

export interface UpdateReservationOverrideReasonsCriteria {
  basketReference: string;
  hotelId: string;
  reasonCode: string;
  reasonName: string;
  callerName: string;
  managerName?: string;
}

export interface ReservationOverrideReasons {
  reasonCode: string;
  reasonName: string;
  callerName: string;
  managerName?: string;
}

export interface OverridenUserInfo {
  reservationOverrideReasons: ReservationOverrideReasons;
  reservationOverridden: boolean;
}
