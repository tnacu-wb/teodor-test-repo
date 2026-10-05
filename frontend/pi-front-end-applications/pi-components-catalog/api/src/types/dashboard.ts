export interface ManageBookingResponse {
  manageBooking: {
    isCancellable: boolean;
    isAmendable: boolean;
    isRuleCompliant: boolean;
    aemLabelKey?: string;
  };
}
