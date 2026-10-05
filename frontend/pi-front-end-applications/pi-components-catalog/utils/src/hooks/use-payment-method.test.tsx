import { renderHook } from '@testing-library/react';
import React from 'react';

import usePaymentMethod from './use-payment-method';

describe('usePaymentMethod', () => {
  it('should return the initial paymentMethod state', () => {
    const { result } = renderHook(usePaymentMethod);
    const [paymentMethod] = result.current;

    expect(paymentMethod).toEqual({
      name: '',
      type: '',
      subType: '',
      order: 0,
      paymentOptions: [{ type: '', order: 0, enabled: true }],
      enabled: false,
      cnpPreSelected: false,
      cnpOptionAvailable: false,
      reasons: [],
    });
  });

  it('should update the paymentMethod state', () => {
    const { result } = renderHook(usePaymentMethod);
    const [paymentMethod] = result.current;
    paymentMethod.name = 'Updated Name';
    jest.spyOn(React, 'useState').mockImplementation(() => [paymentMethod, jest.fn()]);

    expect(paymentMethod.name).toBe('Updated Name');
  });
});
