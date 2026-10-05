import { getAmendSectionTranslations, getTitleDropdownValues } from './amend';

const t = (key: string) => {
  switch (key) {
    case 'booking.header.summary':
      return 'booking.header.summary';
    case 'amend.confirmChanges':
      return 'amend.confirmChanges';
    case 'booking.hotel.summary.meals':
      return 'booking.hotel.summary.meals';
    case 'amend.extras.extras':
      return 'amend.extras.extras';
    case 'dashboard.bookings.previousTotal':
      return 'dashboard.bookings.previousTotal';
    case 'booking.confirmation.totalCost':
      return 'booking.confirmation.totalCost';
    case 'ccui.amend.continue.to.payment':
      return 'ccui.amend.continue.to.payment';
    case 'amend.expandDetail':
      return 'amend.expandDetail';
    default:
      return 'default';
  }
};

const mockResponse = {
  bookingSummaryLabels: {
    title: 'booking.header.summary',
    confirmChangesLabel: 'amend.confirmChanges',
    mealsLabel: 'booking.hotel.summary.meals',
    extrasLabel: 'amend.extras.extras',
    previousTotalLabel: 'dashboard.bookings.previousTotal',
    totalCostLabel: 'booking.confirmation.totalCost',
    continueToPaymentLabel: 'ccui.amend.continue.to.payment',
    expandDetail: 'amend.expandDetail',
  },
};

const titleOptionsMock = [{ id: 'Mr', label: 'Mr' }];
const titleOptionsMockResponse = [
  { id: 'Mrs', label: 'Mrs' },
  { id: 'Mr', label: 'Mr' },
];

describe('Get Amend labels function', () => {
  it('response should match the bookingSumary labels mock value', () => {
    const { bookingSummaryLabels } = getAmendSectionTranslations(t);
    expect(bookingSummaryLabels).toEqual(mockResponse.bookingSummaryLabels);
  });
});

describe('getTitleDropdownValues', () => {
  it('should add new option', () => {
    expect(getTitleDropdownValues(titleOptionsMock, 'Mrs')).toEqual(titleOptionsMockResponse);
  });
  it('should not add new option', () => {
    expect(getTitleDropdownValues(titleOptionsMock, 'Mr')).toEqual(titleOptionsMock);
  });
});
