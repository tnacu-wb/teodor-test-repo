import { PayPalScriptProvider } from '@paypal/react-paypal-js';
import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import PaypalWBButton, { PaypalWBProps } from './PaypalButton.component';

const paypalOptions: PaypalWBProps = {
  currency: 'GB',
  onApprove: jest.fn().mockImplementation(),
  onError: jest.fn().mockImplementation(),
  onCancel: jest.fn().mockImplementation(),
  createBillingAgreement: jest.fn().mockImplementation(),
  disabled: false,
  style: {
    layout: 'vertical',
    shape: 'rect',
    label: 'pay',
  },
};

const ERROR_MESSAGE =
  'Invalid authorization data. Use dataClientToken or dataUserIdToken to authorize.';

describe('PaypalButtonComponent', () => {
  it('should fail rendering the PaypalWBButton component not as a child of a PayPalScriptProvider component', () => {
    console.error = jest.fn();
    let errorMessage = '';

    try {
      render(<PaypalWBButton />);
    } catch (ex) {
      errorMessage = (ex as Error).message;
    }
    expect(errorMessage).toEqual(
      'usePayPalScriptReducer must be used within a PayPalScriptProvider'
    );
  });

  it('should fail rendering the PaypalWBButton component if the dataClientToken is empty string', () => {
    let errorMessage = '';
    console.error = jest.fn();

    try {
      render(
        <PayPalScriptProvider
          options={{
            clientId: 'test',
            dataClientToken: '',
          }}
        >
          <PaypalWBButton {...paypalOptions} />
        </PayPalScriptProvider>
      );
    } catch (ex) {
      errorMessage = (ex as Error).message;
    }

    expect(errorMessage).toEqual(ERROR_MESSAGE);
  });

  it('should fail rendering the PaypalWBButton with empty component props', () => {
    console.error = jest.fn();
    let errorMessage = '';

    try {
      render(
        // @ts-expect-error this test case validates that the options property is required
        <PayPalScriptProvider>
          <PaypalWBButton />
        </PayPalScriptProvider>
      );
    } catch (ex) {
      errorMessage = (ex as Error).message;
    }
    expect(errorMessage).toEqual(ERROR_MESSAGE);
  });
});
