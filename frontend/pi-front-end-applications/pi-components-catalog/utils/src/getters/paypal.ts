import {
  CreateBillingAgreementActions,
  OnApproveBraintreeActions,
  OnApproveBraintreeData,
} from '@paypal/react-paypal-js';
import { PAYMENT_FAILED_VALUE } from '@whitbread-eos/api';
import { client as _client, dataCollector } from 'braintree-web';

interface GetPaypalOptionsParams {
  currencyCode: string | undefined;
  approveCallBack: (nonce: string) => void;
  errorCallBack?: (value: string) => void;
}

export const getPaypalDeviceData = async (authorization: string | undefined) => {
  if (authorization) {
    const brainTreeClient = await _client.create({
      authorization,
    });
    const collector = await dataCollector.create({
      client: brainTreeClient,
      paypal: true,
    });
    const deviceData = await collector.getDeviceData();
    return deviceData;
  }
};

export const getPaypalOptionsParams = ({
  currencyCode,
  approveCallBack,
  errorCallBack,
}: GetPaypalOptionsParams) => {
  const createBillingAgreement = (
    data: Record<string, unknown>,
    actions: CreateBillingAgreementActions
  ): Promise<string> => {
    return actions?.braintree
      ?.createPayment({
        flow: 'vault',
        currency: currencyCode,
        enableShippingAddress: false,
        shippingAddressEditable: false,
      })
      .then((orderId: string) => orderId);
  };

  const onApprove = (
    data: OnApproveBraintreeData,
    actions: OnApproveBraintreeActions
  ): Promise<void> => {
    return actions?.braintree?.tokenizePayment(data).then((payload: any) => {
      // call new endpoint here with Nonce. Console to be removed after API integration
      approveCallBack(payload.nonce);
    });
  };

  const onError = (err: Record<string, unknown>): void => {
    // error to be handled. Console to be removed after API integration
    // eslint-disable-next-line no-console
    console.error('PayPal error', err);
    errorCallBack?.(PAYMENT_FAILED_VALUE);
  };

  return {
    createBillingAgreement,
    onApprove,
    onError,
  };
};
