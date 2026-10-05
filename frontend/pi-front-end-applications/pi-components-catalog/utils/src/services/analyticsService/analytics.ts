/* eslint-disable @typescript-eslint/no-explicit-any */
import {
  AnalyticsData,
  AnalyticsDataCartConfirmation,
  RestaurantsAnalyticsData,
} from '@whitbread-eos/api';

import { getCookie } from '../../helpers/cookies';

const DEFAULT_ANALYTICS_DEBOUNCE_DELAY_MS = 2000;
const ANALYTICS_DEBOUNCE_DELAY_COOKIE = 'analytics_debounce_delay';

let analyticsUpdateTimeout: number | undefined;
let isAnalyticsDebounceEnabled = false;
let trackQueue: Array<{ eventName: string; payload?: Record<string, unknown> }> = [];

function getAnalyticsDebounceDelay() {
  if (typeof window === 'undefined') {
    return DEFAULT_ANALYTICS_DEBOUNCE_DELAY_MS;
  }

  const rawDelay = getCookie(ANALYTICS_DEBOUNCE_DELAY_COOKIE);
  const parsedDelay = Number(rawDelay);

  if (!Number.isFinite(parsedDelay) || parsedDelay < 0) {
    return DEFAULT_ANALYTICS_DEBOUNCE_DELAY_MS;
  }

  return parsedDelay;
}

const analyticsDebounceDelay = getAnalyticsDebounceDelay();

export function configureAnalytics(options: { isAnalyticsDebounceEnabled?: boolean }) {
  isAnalyticsDebounceEnabled = options.isAnalyticsDebounceEnabled ?? false;
}

function flushTrackQueue() {
  if (typeof window !== 'undefined' && window._satellite) {
    trackQueue.forEach(({ eventName, payload }) => {
      if (!payload) {
        window._satellite.track(eventName);
      } else {
        window._satellite.track(eventName, payload);
      }
    });
  }
  trackQueue = [];
}

function triggerAnalyticsUpdatedDebounced() {
  if (!isAnalyticsDebounceEnabled) {
    return;
  }

  if (typeof window !== 'undefined') {
    if (analyticsUpdateTimeout) {
      clearTimeout(analyticsUpdateTimeout);
    }

    analyticsUpdateTimeout = window.setTimeout(() => {
      analyticsUpdateTimeout = undefined;
      flushTrackQueue();
      if (window._satellite) {
        window._satellite.track('analyticsData_updated', window.analyticsData);
      }
    }, analyticsDebounceDelay);
  }
}

const analytics = {
  update: (payload: AnalyticsData) => {
    window.analyticsData = {
      ...window.analyticsData,
      ...payload,
    };
    triggerAnalyticsUpdatedDebounced();
  },
  updateEncodedAnalytics: (payload: string[]) => {
    window.rdata = payload ?? [];
  },
  remove: (payload: (keyof AnalyticsData)[]) => {
    payload.forEach((item) => {
      if (Object.keys(window.analyticsData).includes(item)) {
        delete window.analyticsData[item];
      }
    });
    triggerAnalyticsUpdatedDebounced();
  },
  track: (eventName: string, payload?: Record<string, unknown>, waitForUpdates?: boolean): void => {
    if (waitForUpdates && isAnalyticsDebounceEnabled && analyticsUpdateTimeout) {
      trackQueue.push({ eventName, payload });
      return;
    }

    if (typeof window !== 'undefined' && window._satellite) {
      if (!payload) {
        window._satellite.track(eventName);
        return;
      }

      window._satellite.track(eventName, payload);
    }
  },
};

export const restaurantFormAnalytics = {
  update: (payload: RestaurantsAnalyticsData) => {
    window.analyticsData.restaurants = {
      ...window.analyticsData.restaurants,
      ...payload,
    };
    triggerAnalyticsUpdatedDebounced();
  },
};

export const analyticsConfirmation = {
  update: (payload: AnalyticsDataCartConfirmation) => {
    window.analyticsDataCartConfirmation = {
      ...window.analyticsDataCartConfirmation,
      ...payload,
    };
    triggerAnalyticsUpdatedDebounced();
  },
};

export const timeTrackerSeconds = () => {
  const beforeTime = new Date() as any;
  return () => {
    const nowTime = new Date() as any;
    return ((nowTime - beforeTime) / 1000).toString();
  };
};

export enum analyticsTrackings {
  HOTEL = 'Hotel',
  DATE = 'Date',
  FOOD = 'Food',
  ADDON = 'Addon',
  RATE_PLAN = 'ratePlan',
}

export default analytics;
