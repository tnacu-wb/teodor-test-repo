import { handleError } from '../../../exception/error-handler';
import { CancelInformationCriteria } from '../models/cancel-information-criteria';
import { addFieldsToMap, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get, put } from '../../../client/rest-client';
import { FindBookingCriteria } from '../models/find-booking-criteria';
import { Channel } from '../models/channel';
import { BookingChannelCriteria } from '../models/booking-channel-criteria';
import { SearchBookingsCriteria } from '../models/search-bookings-criteria';
import { FindBookingForKioskCriteria } from '../models/find-booking-for-kiosk-criteria';

export const getManageBookingInformation = async (
  { cancelInformationCriteria }: { cancelInformationCriteria: CancelInformationCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'hotelId', value: cancelInformationCriteria.hotelId, required: true },
      { key: 'basketReference', value: cancelInformationCriteria.basketReference, required: true },
      { key: 'userDateTime', value: cancelInformationCriteria.userDateTime, required: true },
      { key: 'token', value: cancelInformationCriteria.token ?? '', required: false },
      { key: 'channel', value: cancelInformationCriteria.bookingChannel.channel, required: true },
      {
        key: 'subchannel',
        value: cancelInformationCriteria.bookingChannel.subchannel,
        required: true
      },
      { key: 'language', value: cancelInformationCriteria.bookingChannel.language, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.MANAGE_BOOKING, getManageBookingInformation, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getFindBooking = async (
  { findBookingCriteria }: { findBookingCriteria: FindBookingCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const subChannel = findBookingCriteria.bookingChannel?.subchannel
      ? findBookingCriteria.bookingChannel.subchannel
      : 'WEB';
    const channel = findBookingCriteria.bookingChannel?.channel
      ? findBookingCriteria.bookingChannel.channel
      : Channel.PI;

    if (objectIsNullEmptyOrUndefined(findBookingCriteria.bookingChannel)) {
      findBookingCriteria.bookingChannel = new BookingChannelCriteria({
        channel: channel,
        subchannel: subChannel
      });
    }

    if (objectIsNullEmptyOrUndefined(findBookingCriteria.bookingChannel?.channel)) {
      findBookingCriteria.bookingChannel!.channel = channel;
    }

    if (objectIsNullEmptyOrUndefined(findBookingCriteria.bookingChannel?.subchannel)) {
      findBookingCriteria.bookingChannel!.subchannel = subChannel;
    }

    let languageLowerCase =
      findBookingCriteria?.bookingChannel?.language?.toLowerCase() ??
      findBookingCriteria?.language?.toLowerCase() ??
      null;
    if (languageLowerCase) {
      findBookingCriteria.bookingChannel!.language = languageLowerCase;
    }

    const fieldsToAdd = [
      { key: 'resNo', value: findBookingCriteria.resNo, required: true },
      { key: 'lastName', value: encodeURIComponent(findBookingCriteria.lastName), required: true },
      { key: 'arrivalDate', value: findBookingCriteria.arrivalDate, required: true },
      { key: 'country', value: findBookingCriteria.country, required: false },
      { key: 'language', value: findBookingCriteria.bookingChannel?.language, required: false },
      {
        key: 'channel',
        value: findBookingCriteria.bookingChannel?.channel,
        required: false
      },
      { key: 'subchannel', value: findBookingCriteria.bookingChannel?.subchannel, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.FIND_BOOKING, getFindBooking, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getSearchBookings = async (
  { searchBookingsCriteria }: { searchBookingsCriteria: SearchBookingsCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      {
        key: 'bookingReference',
        value: searchBookingsCriteria.bookingReference! ?? '',
        required: false
      },
      {
        key: 'bookerLastName',
        value: searchBookingsCriteria.bookerLastName! ?? '',
        required: false
      },
      { key: 'guestLastName', value: searchBookingsCriteria.guestLastName! ?? '', required: false },
      { key: 'arrivalDate', value: searchBookingsCriteria.arrivalDate! ?? '', required: false },
      {
        key: 'bookerPostcode',
        value: searchBookingsCriteria.bookerPostcode! ?? '',
        required: false
      },
      { key: 'hotelId', value: searchBookingsCriteria.hotelId! ?? '', required: false },
      { key: 'bookerEmail', value: searchBookingsCriteria.bookerEmail! ?? '', required: false },
      { key: 'bookerPhone', value: searchBookingsCriteria.bookerPhone! ?? '', required: false },
      {
        key: 'cancellationDate',
        value: searchBookingsCriteria.cancellationDate! ?? '',
        required: false
      },
      { key: 'companyName', value: searchBookingsCriteria.companyName! ?? '', required: false },
      {
        key: 'thirdPartyBookingReferenceNumber',
        value: searchBookingsCriteria.thirdPartyBookingReferenceNumber! ?? '',
        required: false
      },
      { key: 'channel', value: searchBookingsCriteria.channel! ?? null, required: false }
    ];

    if (!objectIsNullEmptyOrUndefined(searchBookingsCriteria.offset)) {
      fieldsToAdd.push({
        key: 'offset',
        value: searchBookingsCriteria.offset! ?? 0,
        required: false
      });
    }
    if (!objectIsNullEmptyOrUndefined(searchBookingsCriteria.limit)) {
      fieldsToAdd.push({
        key: 'limit',
        value: searchBookingsCriteria.limit! ?? 0,
        required: false
      });
    }

    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.SEARCH_BOOKINGS, getSearchBookings, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getFindBookingForKiosk = async (
  { findBookingForKioskCriteria }: { findBookingForKioskCriteria: FindBookingForKioskCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [
      { key: 'resNo', value: findBookingForKioskCriteria.resNo, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.FIND_BOOKING_FOR_KIOSK, getFindBookingForKiosk, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const updateUdfc20 = async (
  { updateUdfc20Criteria }: { updateUdfc20Criteria: any },
  context: any
) => {
  try {
    await put(endpoints.UPDATE_UDFC_20, updateUdfc20, updateUdfc20Criteria, context);
    return updateUdfc20Criteria.ciolStatus;
  } catch (error: Error | any) {
    handleError(error, updateUdfc20Criteria);
  }
};
