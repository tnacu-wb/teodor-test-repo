import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { analytics } from '@whitbread-eos/utils';

import { Analytics } from './Analytics';

jest.mock('@whitbread-eos/utils', () => ({
  analytics: {
    update: jest.fn(),
    remove: jest.fn(),
  },
}));

describe('Analytics Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any).analyticsData = {
      innBusiness: {
        visitedCardsPages: new Set(),
      },
    };
    (window as any)._satellite = { track: jest.fn() };
  });

  describe('pageName effect', () => {
    it('should update analytics with page name', () => {
      render(<Analytics pageName="Test Page" />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
        },
        pageName: 'Test Page',
      });
    });

    it('should not update analytics for pageName if pageName is undefined', () => {
      render(<Analytics pageName={undefined} />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          activeCards: 0,
          expiredCards: 0,
          dispatchedCards: 0,
          activateCards: 0,
          cancelledCards: 0,
          visitedCardsPages: new Set(),
        },
      });
    });

    it('should remove validation when currentValidation exists', () => {
      (window as any).analyticsData = {
        validation: 'some-validation',
        innBusiness: { visitedCardsPages: new Set() },
      };

      render(<Analytics pageName="Test Page" />);

      expect(analytics.remove).toHaveBeenCalledWith(['validation']);
    });

    it('should not remove validation when no currentValidation exists', () => {
      render(<Analytics pageName="Test Page" />);
      expect(analytics.remove).not.toHaveBeenCalled();
    });
  });

  describe('tab effect', () => {
    it('should reset card analytics when tab is provided', () => {
      render(<Analytics tab="inn-business-pay" />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          tab: 'inn-business-pay',
          activeCards: 0,
          expiredCards: 0,
          dispatchedCards: 0,
          activateCards: 0,
          cancelledCards: 0,
          visitedCardsPages: new Set(),
        },
      });
    });

    it('should not update analytics for tab if tab is undefined', () => {
      render(<Analytics tab={undefined} />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('totalPages and pageIndex effect', () => {
    it('should reset analytics when totalPages equals pageIndex and both equal 1', () => {
      render(<Analytics totalPages={1} pageIndex={1} />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          activeCards: 0,
          expiredCards: 0,
          dispatchedCards: 0,
          activateCards: 0,
          cancelledCards: 0,
        },
      });
    });

    it('should not reset analytics when conditions are not met', () => {
      render(<Analytics totalPages={2} pageIndex={1} />);
      expect(analytics.update).toHaveBeenCalledTimes(2);
    });
  });

  describe('rowsData and pageIndex effect - CDH cards', () => {
    it('should initialize cards data when visitedPages is empty', () => {
      (window as any).analyticsData.innBusiness.visitedCardsPages = undefined;

      const mockRowData = [{ expiryDate: '12/25' }];
      render(<Analytics rowsData={mockRowData} pageIndex={1} />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          activeCards: 0,
          expiredCards: 0,
          dispatchedCards: 0,
          activateCards: 0,
          cancelledCards: 0,
          visitedCardsPages: new Set(),
        },
      });
    });

    it('should process CDH cards with expired and active cards', () => {
      const currentYear = new Date().getFullYear();
      const mockRowData = [
        { expiryDate: '12/23' },
        { expiryDate: `12/${String(currentYear + 1).slice(-2)}` },
      ];

      render(<Analytics rowsData={mockRowData} pageIndex={1} />);

      const lastCall = (analytics.update as jest.Mock).mock.calls.slice(-1)[0][0];
      expect(lastCall.innBusiness.activeCards).toBe(1);
      expect(lastCall.innBusiness.expiredCards).toBe(1);
      expect(lastCall.innBusiness.visitedCardsPages.has(1)).toBe(true);
    });

    it('should not process same page twice', () => {
      const visitedPages = new Set([1]);
      (window as any).analyticsData.innBusiness.visitedCardsPages = visitedPages;

      const mockRowData = [{ expiryDate: '12/25' }];
      render(<Analytics rowsData={mockRowData} pageIndex={1} />);

      expect(analytics.update).not.toHaveBeenCalled();
    });

    it('should not process when pageIndex is undefined', () => {
      const mockRowData = [{ expiryDate: '12/25' }];
      render(<Analytics rowsData={mockRowData} pageIndex={undefined} />);

      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('rowsData and pageIndex effect - Inn Business Pay cards', () => {
    it('should process Inn Business Pay cards with different statuses', () => {
      const mockRowData = [
        { cardStatus: 'CURRENT', isActivated: true },
        { cardStatus: 'CURRENT', isActivated: false },
        { cardStatus: 'PENDING', isActivated: false },
        { cardStatus: 'CANCELLED', isActivated: false },
        { cardStatus: 'HOT', isActivated: false },
      ];

      render(<Analytics rowsData={mockRowData} pageIndex={2} />);

      const lastCall = (analytics.update as jest.Mock).mock.calls.slice(-1)[0][0];
      expect(lastCall.innBusiness.visitedCardsPages.has(2)).toBe(true);
      expect(lastCall.innBusiness.activeCards).toBe(2);
      expect(lastCall.innBusiness.dispatchedCards).toBe(1);
      expect(lastCall.innBusiness.cancelledCards).toBe(1);
      expect(lastCall.innBusiness.activateCards).toBe(1);
    });
  });

  describe('cardUpdateId effect', () => {
    it('should update analytics with card Id when cardUpdateId is provided', () => {
      render(<Analytics cardUpdateId="card-123" />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          cardId: 'card-123',
        },
      });
    });

    it('should not update analytics for cardId when cardUpdateId is empty', () => {
      render(<Analytics cardUpdateId="" />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('cardType effect', () => {
    it('should update analytics with cardType when provided', () => {
      render(<Analytics cardType="visa" />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          cardType: 'visa',
        },
      });
    });

    it('should not update analytics for cardType when cardType is empty', () => {
      render(<Analytics cardType="" />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('isCancelledCardsFilterOn effect', () => {
    it('should update analytics when filter is turned on', () => {
      render(<Analytics isCancelledCardsFilterOn={true} />);

      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          showCancelledCards: true,
        },
      });
    });

    it('should delete showCancelledCards property when filter is turned off', () => {
      (window as any).analyticsData.innBusiness.showCancelledCards = true;
      render(<Analytics isCancelledCardsFilterOn={false} />);

      expect((window as any).analyticsData.innBusiness.showCancelledCards).toBeUndefined();
    });

    it('should not update for filter when isCancelledCardsFilterOn is undefined', () => {
      render(<Analytics isCancelledCardsFilterOn={undefined} />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('selectedSetCreditLimit effect', () => {
    it('should update analytics with selectedSetCreditLimit when selectedSetCreditLimit is provided', () => {
      render(<Analytics selectedSetCreditLimit={true} />);
      expect(analytics.update).toHaveBeenCalledTimes(2);
      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          selectedSetCreditLimit: true,
        },
      });
    });

    it('should not update analytics for selectedSetCreditLimit when selectedSetCreditLimit is not provided', () => {
      render(<Analytics />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('selectedRestrictedUsage effect', () => {
    it('should update analytics with selectedRestrictedUsage when selectedRestrictedUsage is provided', () => {
      render(<Analytics selectedRestrictedUsage={true} />);
      expect(analytics.update).toHaveBeenCalledTimes(2);
      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          selectedRestrictedUsage: true,
        },
      });
    });

    it('should not update analytics for selectedRestrictedUsage when selectedRestrictedUsage is not provided', () => {
      render(<Analytics />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('cardReplaceReason effect', () => {
    it('should update analytics with cardReplaceReason when cardReplaceReason is provided', () => {
      render(<Analytics cardReplaceReason="Damaged" />);
      expect(analytics.update).toHaveBeenCalledTimes(2);
      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          cardReplaceReason: 'Damaged',
        },
      });
    });

    it('should not update analytics for cardReplaceReason when cardReplaceReason is not provided', () => {
      render(<Analytics />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('cardReplaceAddress effect', () => {
    it('should update analytics with cardReplaceAddress when cardReplaceAddress is provided', () => {
      const address = {
        addressLine1: 'Line 1',
        addressLine2: 'Line 2',
        addressLine3: 'Line 3',
        addressLine4: 'Line 4',
        addressLine5: 'Line 5',
        postCode: 'AB1 2CD',
        country: 'GB',
      };
      render(<Analytics cardReplaceAddress={address} />);
      expect(analytics.update).toHaveBeenCalledTimes(2);
      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
          cardReplaceAddress: {
            addressLine1: 'Line 1',
            addressLine2: 'Line 2',
            addressLine3: 'Line 3',
            addressLine4: 'Line 4',
            addressLine5: 'Line 5',
            postCode: 'AB1 2CD',
            country: 'GB',
          },
        },
      });
    });

    it('should not update analytics for cardReplaceAddress when cardReplaceAddress is not provided', () => {
      render(<Analytics />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('validation effect', () => {
    it('should update analytics with validation when validation is provided', () => {
      render(<Analytics validation="Invalid name format" />);
      expect(analytics.update).toHaveBeenCalledTimes(2);
      expect(analytics.update).toHaveBeenCalledWith({
        innBusiness: {
          visitedCardsPages: new Set(),
        },
        validation: 'Invalid name format',
      });
    });

    it('should not update analytics for validation when validation is not provided', () => {
      render(<Analytics />);
      expect(analytics.update).toHaveBeenCalledTimes(1);
    });
  });

  describe('track effect', () => {
    it('should call satellite track when track is provided', () => {
      render(<Analytics track="test-event" />);

      expect(window._satellite.track).toHaveBeenCalledWith('test-event');
    });

    it('should not call satellite track when track is undefined', () => {
      render(<Analytics track={undefined} />);
      expect(window._satellite.track).not.toHaveBeenCalled();
    });
  });
});
