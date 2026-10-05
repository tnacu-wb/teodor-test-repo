import { renderHook, act } from '@testing-library/react';
import React from 'react';

import { useIframe } from './useIframe';

global.atob = jest.fn((data) => `<html><body>Mocked iframe content for ${data}</body></html>`);

describe('useIframe Hook', () => {
  beforeEach(() => {
    document.body.innerHTML = '';
    jest.clearAllMocks();
  });

  test('returns a ref object', () => {
    const { result } = renderHook(() =>
      useIframe({
        iframeData: 'dGVzdCBkYXRh',
        providerUrl: 'https://example.com',
      })
    );

    expect(result.current).toBeDefined();
    expect(result.current).toHaveProperty('current');
  });

  test('creates an iframe when valid data is provided', () => {
    const container = document.createElement('div');
    document.body.appendChild(container);

    const mockRefValue = { current: container };
    jest.spyOn(React, 'useRef').mockReturnValue(mockRefValue);

    renderHook(() =>
      useIframe({
        iframeData: 'dGVzdCBkYXRh',
        providerUrl: 'https://example.com',
      })
    );

    const iframe = document.getElementById('payment-iframe') as HTMLIFrameElement;
    expect(iframe).toBeTruthy();
    expect(iframe?.tagName).toBe('IFRAME');
  });

  test('does not create an iframe when iframeData is empty', () => {
    renderHook(() =>
      useIframe({
        iframeData: '',
        providerUrl: 'https://example.com',
      })
    );

    const iframe = document.getElementById('payment-iframe');
    expect(iframe).toBeNull();
  });

  test('removes the iframe on cleanup', () => {
    const container = document.createElement('div');
    document.body.appendChild(container);

    const mockRefValue = { current: container };
    jest.spyOn(React, 'useRef').mockReturnValue(mockRefValue);

    const { unmount } = renderHook(() =>
      useIframe({
        iframeData: 'dGVzdCBkYXRh',
        providerUrl: 'https://example.com',
      })
    );

    const iframeBeforeUnmount = document.getElementById('payment-iframe');
    expect(iframeBeforeUnmount).toBeTruthy();

    act(() => {
      unmount();
    });

    const iframeAfterUnmount = document.getElementById('payment-iframe');
    expect(iframeAfterUnmount).toBeNull();
  });

  test('does not create duplicate iframes with the same ID', () => {
    const container = document.createElement('div');
    document.body.appendChild(container);

    const mockRefValue = { current: container };
    jest.spyOn(React, 'useRef').mockReturnValue(mockRefValue);

    const existingIframe = document.createElement('iframe');
    existingIframe.id = 'payment-iframe';
    document.body.appendChild(existingIframe);

    renderHook(() =>
      useIframe({
        iframeData: 'dGVzdCBkYXRh',
        providerUrl: 'https://example.com',
      })
    );

    const iframes = document.querySelectorAll('iframe');
    expect(iframes.length).toBe(1);
  });
});
