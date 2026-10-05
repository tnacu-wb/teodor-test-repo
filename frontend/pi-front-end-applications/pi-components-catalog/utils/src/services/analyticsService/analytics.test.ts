import {
  AnalyticsData,
  AnalyticsDataCartConfirmation,
  RestaurantsAnalyticsData,
} from '@whitbread-eos/api';

import analytics, {
  analyticsConfirmation,
  timeTrackerSeconds,
  restaurantFormAnalytics,
  configureAnalytics,
} from './analytics';

jest.mock('../../helpers/cookies', () => ({
  getCookie: jest.fn<string | undefined, [string]>(),
}));

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    analyticsDataCartConfirmation: AnalyticsDataCartConfirmation;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

describe('analytics', () => {
  beforeEach(() => {
    Object.defineProperty(window, 'analyticsData', {
      value: {
        language: 'en',
        currencyCode: 'GBP',
      },
      writable: true,
    });
  });

  afterEach(() => {
    jest.runOnlyPendingTimers();
    jest.clearAllTimers();
    jest.useRealTimers();
    configureAnalytics({ isAnalyticsDebounceEnabled: false });
    jest.clearAllMocks();
  });

  describe('analytics.update Method', () => {
    it('should update analyticsData object on window', () => {
      analytics.update({
        pageName: 'hotel-details',
      });
      expect(window.analyticsData).toEqual({
        language: 'en',
        pageName: 'hotel-details',
        currencyCode: 'GBP',
      });
    });
  });

  describe('analytics.remove Method', () => {
    it('should remove specified payload from analyticsData', () => {
      analytics.remove(['currencyCode']);
      expect(window.analyticsData).toEqual({ language: 'en' });
    });

    it('should not remove specified payload from analyticsData if it is not present', () => {
      analytics.remove(['CCUI']);
      expect(window.analyticsData).toEqual({ language: 'en', currencyCode: 'GBP' });
    });
  });

  describe('analyticsConfirmation object', () => {
    beforeEach(() => {
      Object.defineProperty(window, 'analyticsDataCartConfirmation', {
        value: {
          bookingZipCode: 'GB',
          bookingCity: 'London',
        },
        writable: true,
      });
    });

    it('should remove specified payload from analyticsData', () => {
      analyticsConfirmation.update({
        cardType: 'PIBA_UK',
      });
      expect(window.analyticsDataCartConfirmation).toEqual({
        bookingZipCode: 'GB',
        bookingCity: 'London',
        cardType: 'PIBA_UK',
      });
    });
  });

  describe('timeTrackerSeconds Method', () => {
    beforeAll(() => {
      jest.useFakeTimers('modern');
      jest.setSystemTime(0);
    });

    afterAll(() => {
      jest.useRealTimers();
    });

    it('should return a function that keeps track of passed time', () => {
      const timeTracker = timeTrackerSeconds();

      // Simulates 1 second passing
      jest.setSystemTime(1000);
      expect(timeTracker()).toBe('1');

      // Simulates another 2 seconds passing (3 seconds in total from start)
      jest.setSystemTime(3000);
      expect(timeTracker()).toBe('3');
    });
  });

  describe('analytics.track Method', () => {
    beforeEach(() => {
      window._satellite = { track: jest.fn() };
    });

    afterEach(() => {
      delete window._satellite;
    });

    it('should call window._satellite.track with the event name if _satellite exists', () => {
      analytics.track('TestEvent');
      expect(window._satellite.track).toHaveBeenCalledWith('TestEvent');
    });

    it('should call window._satellite.track with the event name and payload', () => {
      const payload = {
        bookingReference: 'BCQR231999',
        hotelCode: 'LONMON',
        channelID: 'PI',
      };

      analytics.track('invoice_download', payload);
      expect(window._satellite.track).toHaveBeenCalledWith('invoice_download', payload);
    });

    it('should not throw if window._satellite is undefined', () => {
      delete window._satellite;
      expect(() => analytics.track('TestEvent')).not.toThrow();
    });
  });

  describe('configureAnalytics', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should enable analytics debounce when passed true', () => {
      configureAnalytics({ isAnalyticsDebounceEnabled: true });
      window._satellite = { track: jest.fn() };

      analytics.update({ pageName: 'test' });
      jest.advanceTimersByTime(2000);

      expect(window._satellite.track.mock.calls[0][0]).toBe('analyticsData_updated');
      delete window._satellite;
    });

    it('should disable analytics debounce when passed false', () => {
      configureAnalytics({ isAnalyticsDebounceEnabled: false });
      window._satellite = { track: jest.fn() };

      analytics.update({ pageName: 'test' });
      jest.advanceTimersByTime(2000);

      expect(window._satellite.track).not.toHaveBeenCalledWith('analyticsData_updated');
      delete window._satellite;
    });
  });

  describe('analytics debounce behavior', () => {
    beforeEach(() => {
      jest.useFakeTimers();
      window._satellite = { track: jest.fn() };
      configureAnalytics({ isAnalyticsDebounceEnabled: true });
    });

    afterEach(() => {
      jest.useRealTimers();
      delete window._satellite;
    });

    it('should debounce analyticsData_updated calls and reschedule on multiple updates', () => {
      analytics.update({ pageName: 'search' });
      analytics.update({ pageName: 'hotel-details' });

      jest.advanceTimersByTime(1999);
      expect(window._satellite.track).not.toHaveBeenCalledWith('analyticsData_updated');

      jest.advanceTimersByTime(1);
      expect(window._satellite.track.mock.calls[0][0]).toBe('analyticsData_updated');
      expect(window._satellite.track).toHaveBeenCalledTimes(1);
    });

    it('should call analyticsData_updated with 2000ms default delay when enabled', () => {
      analytics.update({ pageName: 'search' });
      jest.advanceTimersByTime(2000);

      expect(window._satellite.track.mock.calls[0][0]).toBe('analyticsData_updated');
    });

    it('should queue track calls with waitForUpdates and flush them before analyticsData_updated fires', () => {
      const payload = { bookingReference: 'ABC123' };

      analytics.update({ pageName: 'confirmation' });
      analytics.track('event_without_payload', undefined, true);
      analytics.track('event_with_payload', payload, true);

      expect(window._satellite.track).not.toHaveBeenCalled();

      jest.advanceTimersByTime(2000);

      expect(window._satellite.track).toHaveBeenCalledTimes(3);
      expect(window._satellite.track.mock.calls[0]).toEqual(['event_without_payload']);
      expect(window._satellite.track.mock.calls[1]).toEqual(['event_with_payload', payload]);
      expect(window._satellite.track.mock.calls[2][0]).toBe('analyticsData_updated');
    });
  });
});

describe('restaurantFormAnalytics', () => {
  beforeEach(() => {
    window.analyticsData = {
      restaurants: {
        restaurantID: '40015070',
      },
    };
  });

  it('should update window.analyticsData.restaurants with new payload', () => {
    const payload: RestaurantsAnalyticsData = {
      adults: 2,
    };

    restaurantFormAnalytics.update(payload);

    expect(window.analyticsData.restaurants).toEqual({
      restaurantID: '40015070',
      adults: 2,
    });
  });

  it('should override existing keys with payload values', () => {
    const payload: RestaurantsAnalyticsData = {
      restaurantID: '40015070',
    };

    restaurantFormAnalytics.update(payload);

    expect(window.analyticsData.restaurants).toEqual({
      restaurantID: '40015070',
    });
  });
});
