interface LinkProps {
  title: string;
  key: string;
  type: 'link';
  action: () => void;
  isLinkEnabled: boolean;
}

export const mockAction = jest.fn();
const createLink = (title: string, key: string, isLinkEnabled = true): LinkProps => ({
  title,
  key,
  type: 'link',
  action: mockAction,
  isLinkEnabled,
});

export const linksForNormalUser = [
  createLink('Resend Confirmation', 'resendConfirmation'),
  createLink('Resend Invoice', 'resendInvoice'),
  {
    title: 'Other Options',
    key: 'otherOptions',
    type: 'header',
  },
];

export const linksForCancelledBookings = [
  createLink('Change log', 'changeLog'),
  createLink('Agent Notes', 'agentNotes'),
  createLink('Repeat Booking', 'repeatBooking'),
];

export const linksForCCUI = [
  ...linksForCancelledBookings,
  createLink('Split Booking', 'splitBooking'),
  createLink('Cancel Rebook', 'cancelRebook'),
  createLink('Override Policies', 'overridePolicies'),
  createLink('Change PIPBA CNP Status', 'changePiba'),
  createLink('Update Discount Amount', 'updateDiscountAmount'),
  createLink('Update Booker Details', 'bookerDetails'),
  createLink('Change Payment Method', 'changePaymentMethod'),
];

export const mockConfig = [...linksForNormalUser, ...linksForCCUI];

export const linksForPIUpcomingBookings = [createLink('Resend Confirmation', 'resendConfirmation')];

export const linksForPIPastBookings = [createLink('Resend Invoice', 'resendInvoice')];

export const linksForPremierInn = [
  ...linksForPIPastBookings,
  createLink('account.dashboard.printBooking', 'printBooking'),
  ...linksForPIUpcomingBookings,
];
