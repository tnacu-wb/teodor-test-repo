export const BOOKING_FLOW_PAGE = {
  CONFIRMATION_PAGE: 'CONFIRMATION_PAGE',
  REGISTER_PAGE: 'REGISTER_PAGE',
} as const;

export type BookingFlowPage = (typeof BOOKING_FLOW_PAGE)[keyof typeof BOOKING_FLOW_PAGE];
