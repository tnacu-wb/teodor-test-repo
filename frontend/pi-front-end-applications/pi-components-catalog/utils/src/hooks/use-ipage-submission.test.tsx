import { waitFor } from '@testing-library/react';
import { act, renderHook } from '@testing-library/react';

import { useIPageSubmission } from './use-ipage-submission';

jest.spyOn(global.console, 'log').mockImplementation(() => ({}));

describe('use-ipage-submission hooks', () => {
  it('should listen to window message event', async () => {
    const { result } = renderHook(() => useIPageSubmission());
    await act(async () => {
      window.postMessage(JSON.stringify({ paymentId: 'payment-id' }), 'http://localhost');
    });

    await waitFor(() => {
      expect(result.current).toEqual(
        expect.objectContaining({
          isPaymentComplete: true,
        })
      );
    });
  });

  it('should listen to window message event', async () => {
    const { result } = renderHook(() => useIPageSubmission());
    await act(async () => {
      window.postMessage({ paymentId: 'payment-id' }, 'http://localhost');
    });

    await waitFor(() => {
      expect(result.current).toEqual(
        expect.objectContaining({
          isPaymentComplete: false,
        })
      );
    });
  });
});
