import { addFieldsToMap } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get, put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { BookingChannelCriteria } from '../models/booking-channel-criteria';
import { AmendConfirmationPricesRequest } from '../models/amend-confirmation-prices-request';
import { PaymentOptionsCriteria } from '../models/payment-options-criteria';
import { ConfirmAmendLogicCriteria } from '../models/confirm-amend-logic-criteria';

export const getAmendSummary = async (
  {
    originalBasketRef,
    copyBasketRef,
    token,
    bookingChannel,
    country
  }: {
    originalBasketRef: string;
    copyBasketRef: string;
    token?: string;
    bookingChannel: BookingChannelCriteria;
    country: string;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'originalBasketRef', value: originalBasketRef, required: true },
      { key: 'copyBasketRef', value: copyBasketRef, required: true },
      { key: 'token', value: token ?? '', required: false },
      { key: 'bookingChannel.channel', value: bookingChannel.channel, required: true },
      { key: 'bookingChannel.subchannel', value: bookingChannel.subchannel, required: true },
      { key: 'bookingChannel.language', value: bookingChannel.language, required: false },
      { key: 'country', value: country, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.AMEND_SUMMARY, getAmendSummary, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

export const getAmendConfirmationPrices = async (
  {
    amendConfirmationPricesRequest
  }: { amendConfirmationPricesRequest: AmendConfirmationPricesRequest },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      {
        key: 'originalBookingRef',
        value: amendConfirmationPricesRequest.originalBookingRef,
        required: true
      },
      {
        key: 'tempBookingRef',
        value: amendConfirmationPricesRequest.tempBookingRef,
        required: true
      },
      { key: 'token', value: amendConfirmationPricesRequest.token, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(
      endpoints.AMEND_CONFIRMATION_PRICES,
      getAmendConfirmationPrices,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getAmendPaymentOptions = async (
  { paymentOptionsCriteria }: { paymentOptionsCriteria: PaymentOptionsCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      {
        key: 'originalBookingRef',
        value: paymentOptionsCriteria.originalBookingRef,
        required: true
      },
      { key: 'tempBookingRef', value: paymentOptionsCriteria.tempBookingRef, required: true },
      { key: 'token', value: paymentOptionsCriteria.token, required: true },
      { key: 'country', value: paymentOptionsCriteria.country, required: true },
      {
        key: 'bookingChannel.channel',
        value: paymentOptionsCriteria.bookingChannel.channel,
        required: true
      },
      {
        key: 'bookingChannel.subchannel',
        value: paymentOptionsCriteria.bookingChannel.subchannel,
        required: true
      },
      {
        key: 'bookingChannel.language',
        value: paymentOptionsCriteria.bookingChannel.language,
        required: false
      }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.AMEND_PAYMENT_OPTIONS, getAmendPaymentOptions, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, paymentOptionsCriteria);
  }
};

export const confirmAmend = async (
  { confirmAmendCriteria }: { confirmAmendCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.CONFIRM_AMEND,
      confirmAmend,
      confirmAmendCriteria,
      context
    );

    if (response.status === 201) {
      return response.data;
    }
  } catch (error: Error | any) {
    handleError(error, confirmAmendCriteria);
  }
};

export const confirmAmendLogic = async (
  { confirmAmendLogicCriteria }: { confirmAmendLogicCriteria: ConfirmAmendLogicCriteria },
  context: any
): Promise<any> => {
  try {
    let response = await put(
      endpoints.CONFIRM_AMEND_LOGIC,
      confirmAmendLogic,
      confirmAmendLogicCriteria,
      context
    );
    if (response.status === 200) {
      return response.data;
    } else return response;
  } catch (error: Error | any) {
    handleError(error, confirmAmendLogicCriteria);
  }
};
