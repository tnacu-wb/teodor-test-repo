import { waitFor } from '@testing-library/react';
import { renderHook } from '@testing-library/react';
import * as firebase from 'firebase/messaging';
import React from 'react';

import useServiceWorker from './use-service-worker';

const mockMessaging = jest.fn(() => {
  return {};
});
jest.mock('firebase/messaging');
const tokenTest = jest
  .spyOn(firebase, 'getToken')
  .mockImplementation(() => Promise.resolve('token'));

jest.mock('../firebase/firebase', () => ({
  ...jest.requireActual('../firebase/firebase'),
  messaging: () => mockMessaging(),
}));

const mockServiceWorkerRegistration = (state: string) => {
  const registration = {
    scope: '/firebase-cloud-messaging-push-scope',
    installing:
      state === 'installing'
        ? {
            state: 'installing',
            addEventListener: jest.fn((event, callback) => {
              if (event === 'statechange') {
                callback({ target: { state: 'activated' } });
              }
            }),
          }
        : null,
    waiting:
      state === 'waiting'
        ? {
            state: 'waiting',
            addEventListener: jest.fn((event, callback) => {
              if (event === 'statechange') {
                callback({ target: { state: 'activated' } });
              }
            }),
          }
        : null,
    active:
      state === 'active'
        ? {
            state: 'activated',
            addEventListener: jest.fn((event, callback) => {
              if (event === 'statechange') {
                callback({ target: { state: 'activated' } });
              }
            }),
          }
        : null,
  };
  Object.defineProperty(navigator, 'serviceWorker', {
    value: {
      register: jest.fn().mockResolvedValue(registration),
    },
    writable: true,
  });
};
const mockNotificationPermission = (permission: NotificationPermission) => {
  global.Notification = {
    requestPermission: jest.fn().mockResolvedValue(permission),
  } as any;
};

describe('useServiceWorker', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const originalUserAgent = navigator.userAgent;
  afterEach(() => {
    Object.defineProperty(navigator, 'userAgent', {
      value: originalUserAgent,
      configurable: true,
    });
  });
  test('registers service worker as state active and requests notification permission', async () => {
    mockServiceWorkerRegistration('active');
    mockNotificationPermission('granted');
    const isBrowserNotificationEnabled = true;
    jest
      .spyOn(React, 'useState')
      .mockImplementation(() => [isBrowserNotificationEnabled, jest.fn()]);
    renderHook(() => useServiceWorker(true));

    await waitFor(() => {
      expect(Notification.requestPermission).toHaveBeenCalled();
      const expectedArgs = expect.objectContaining({
        serviceWorkerRegistration: expect.objectContaining({
          active: expect.objectContaining({
            state: 'activated',
          }),
        }),
      });
      expect(tokenTest).toHaveBeenCalledWith({}, expectedArgs);
    });
  });
  test('registers service worker as state intalling and requests notification permission', async () => {
    mockServiceWorkerRegistration('installing');
    mockNotificationPermission('granted');

    const isBrowserNotificationEnabled = true;
    jest
      .spyOn(React, 'useState')
      .mockImplementation(() => [isBrowserNotificationEnabled, jest.fn()]);
    renderHook(() => useServiceWorker(true));

    await waitFor(() => {
      expect(Notification.requestPermission).toHaveBeenCalled();
      const expectedArgs = expect.objectContaining({
        serviceWorkerRegistration: expect.objectContaining({
          installing: expect.objectContaining({
            state: 'installing',
          }),
        }),
      });
      expect(tokenTest).toHaveBeenCalledWith({}, expectedArgs);
    });
  });
  test('does not register service worker if enableServiceWorker is false', async () => {
    mockServiceWorkerRegistration('active');
    mockNotificationPermission('granted');

    const isBrowserNotificationEnabled = true;
    jest
      .spyOn(React, 'useState')
      .mockImplementation(() => [isBrowserNotificationEnabled, jest.fn()]);
    renderHook(() => useServiceWorker(false));

    await waitFor(() => {
      expect(Notification.requestPermission).not.toHaveBeenCalled();
    });
  });
  test('handles permission denied scenario', () => {
    mockServiceWorkerRegistration('active');
    mockNotificationPermission('denied');

    const isBrowserNotificationEnabled = true;
    jest
      .spyOn(React, 'useState')
      .mockImplementation(() => [isBrowserNotificationEnabled, jest.fn()]);
    renderHook(() => useServiceWorker(true));
    expect(Notification.requestPermission).toHaveBeenCalled();
  });
});
