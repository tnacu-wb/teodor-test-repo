import { Area, BC_RESERVATION_STATUS, BOOKING_TYPE, paymentOptions } from '@whitbread-eos/api';

import getActionsByCriteria from './bookingActionsFactory';

const mockData = {
  area: Area.CCUI,
  role: 'ccui',
  bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
  bookingType: BOOKING_TYPE.UPCOMING,
  isChangePaymentFeatureEnabled: false,
  isBICDownloadInvoiceEnabled: false,
  handleResendConfirmationAction: {},
};

const mockValueDisplay = jest.fn();

describe('getActionsByCriteria', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  it('should return Array of objects', () => {
    const data = getActionsByCriteria(mockData, null, '');
    expect(data instanceof Array).toBeTruthy();
  });

  it('should return Array of objects for PI app without data', () => {
    mockValueDisplay.mockReturnValue('true');
    const data = getActionsByCriteria(
      {
        area: Area.PI,
        role: 'all',
        bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
        bookingType: BOOKING_TYPE.UPCOMING,
        isChangePaymentFeatureEnabled: false,
        isBICDownloadInvoiceEnabled: false,
      },
      null,
      ''
    );
    expect(data).toEqual([
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'resendConfirmation',
        title: 'ccui.manageBooking.options.resendConfirmation',
        type: 'link',
      },
    ]);
  });

  it('should return Array of objects for CCUI app', () => {
    mockValueDisplay.mockReturnValue('true');
    const data = getActionsByCriteria(
      {
        area: Area.CCUI,
        role: 'ccui',
        bookingType: BOOKING_TYPE.UPCOMING,
        bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
        isChangePaymentFeatureEnabled: true,
        isBICDownloadInvoiceEnabled: false,
      },
      'TEST-1234',
      paymentOptions.RESERVE_WITHOUT_CARD
    );
    expect(data).toEqual([
      {
        action: expect.any(Function),
        key: 'otherOptions',
        title: 'ccui.manageBooking.options.otherOptions',
        type: 'header',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'changeLog',
        title: 'ccui.manageBooking.options.changeLog',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'agentNotes',
        title: 'ccui.manageBooking.options.agentNotes',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'splitBooking',
        title: 'ccui.manageBooking.options.splitBooking',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'repeatBooking',
        title: 'ccui.manageBooking.options.repeatBooking',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'cancelRebook',
        title: 'ccui.manageBooking.options.cancelRebook',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'overridePolicies',
        title: 'ccui.manageBooking.options.overridePolicies',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'changePiba',
        title: 'ccui.manageBooking.options.changePIPBACNPStatus',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'updateDiscountAmount',
        title: 'ccui.manageBooking.options.updateDiscountAmount',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'bookerDetails',
        title: 'ccui.manageBooking.options.updateBookerDetails',
        type: 'link',
      },
      {
        action: expect.any(Function),
        isLinkEnabled: true,
        key: 'changePaymentMethod',
        title: 'ccui.manageBooking.option.changePaymentMethod',
        type: 'link',
      },
    ]);
  });

  describe('Download Invoice Feature', () => {
    it('should NOT include downloadInvoice link for CCUI area even when feature flag is enabled', () => {
      const data = getActionsByCriteria(
        {
          area: Area.CCUI,
          role: 'manager',
          bookingType: BOOKING_TYPE.PAST,
          bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
          isChangePaymentFeatureEnabled: false,
          isBICDownloadInvoiceEnabled: true,
        },
        'TEST-1234',
        paymentOptions.PAY_NOW
      );

      const downloadInvoiceLink = data.find((link) => link.key === 'downloadInvoice');
      expect(downloadInvoiceLink).toBeUndefined();
    });

    it('should NOT include downloadInvoice link when feature flag is disabled', () => {
      const data = getActionsByCriteria(
        {
          area: Area.CCUI,
          role: 'manager',
          bookingType: BOOKING_TYPE.PAST,
          bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
          isChangePaymentFeatureEnabled: false,
          isBICDownloadInvoiceEnabled: false,
        },
        'TEST-1234',
        paymentOptions.PAY_NOW
      );

      const downloadInvoiceLink = data.find((link) => link.key === 'downloadInvoice');
      expect(downloadInvoiceLink).toBeUndefined();
    });

    it('should include downloadInvoice for PI area with past bookings when feature flag is enabled', () => {
      const data = getActionsByCriteria(
        {
          area: Area.PI,
          role: 'manager',
          bookingType: BOOKING_TYPE.PAST,
          bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
          isChangePaymentFeatureEnabled: false,
          isBICDownloadInvoiceEnabled: true,
        },
        'TEST-1234',
        paymentOptions.PAY_NOW
      );

      const downloadInvoiceLink = data.find((link) => link.key === 'downloadInvoice');
      expect(downloadInvoiceLink).toBeDefined();
    });

    it('should call handleDownloadInvoiceAction when downloadInvoice link is clicked', () => {
      const handleDownloadInvoiceAction = jest.fn();
      const data = getActionsByCriteria(
        {
          area: Area.PI,
          role: 'manager',
          bookingType: BOOKING_TYPE.PAST,
          bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
          isChangePaymentFeatureEnabled: false,
          isBICDownloadInvoiceEnabled: true,
        },
        'TEST-1234',
        paymentOptions.PAY_NOW,
        undefined,
        undefined,
        undefined,
        handleDownloadInvoiceAction
      );

      const downloadInvoiceLink = data.find((link) => link.key === 'downloadInvoice');
      downloadInvoiceLink?.action?.();

      expect(handleDownloadInvoiceAction).toHaveBeenCalledTimes(1);
    });
  });

  describe('CHECKED_IN booking type', () => {
    it('should NOT include downloadInvoice or resendInvoice for CHECKED_IN bookings in PI area', () => {
      const data = getActionsByCriteria(
        {
          area: Area.PI,
          role: 'manager',
          bookingType: BOOKING_TYPE.CHECKED_IN,
          bookingStatus: BC_RESERVATION_STATUS.CHECKEDIN,
          isChangePaymentFeatureEnabled: false,
          isBICDownloadInvoiceEnabled: true,
        },
        'TEST-1234',
        paymentOptions.PAY_NOW
      );

      const downloadInvoiceLink = data.find((link) => link.key === 'downloadInvoice');
      const resendInvoiceLink = data.find((link) => link.key === 'resendInvoice');
      expect(downloadInvoiceLink).toBeUndefined();
      expect(resendInvoiceLink).toBeUndefined();
    });

    it('should include resendConfirmation for CHECKED_IN bookings (treated as upcoming)', () => {
      const data = getActionsByCriteria(
        {
          area: Area.PI,
          role: 'all',
          bookingType: BOOKING_TYPE.CHECKED_IN,
          bookingStatus: BC_RESERVATION_STATUS.CHECKEDIN,
          isChangePaymentFeatureEnabled: false,
          isBICDownloadInvoiceEnabled: true,
        },
        'TEST-1234',
        paymentOptions.PAY_NOW
      );

      const resendConfirmationLink = data.find((link) => link.key === 'resendConfirmation');
      expect(resendConfirmationLink).toBeDefined();
      expect(resendConfirmationLink?.key).toBe('resendConfirmation');

      // But invoice actions should still be hidden
      const downloadInvoiceLink = data.find((link) => link.key === 'downloadInvoice');
      const resendInvoiceLink = data.find((link) => link.key === 'resendInvoice');
      expect(downloadInvoiceLink).toBeUndefined();
      expect(resendInvoiceLink).toBeUndefined();
    });
  });
});
