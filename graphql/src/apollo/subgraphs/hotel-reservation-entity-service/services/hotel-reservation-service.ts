import {
  addDefaultValuesFieldsToMap,
  addField,
  addFieldIfNotUndefined,
  addFieldsToMap,
  getServiceEndpoint,
  getURL,
  replaceServiceEndpoint
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get, post, put } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { CancellationPolicies } from '../models/cancellation-policies-criteria';
import { SearchBookingsCcuiCriteria } from '../models/search-bookings-ccui-criteria';
import { RemoveRoomCriteria } from '../models/remove-room-criteria';
import { promotionsInformation } from '../../content-entity-service/services/global-config-service';

export const getBookingAllowances = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const bookingAllowancesPath = endpoints.BOOKING_ALLOWANCES.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );
    const bookingAllowancesEndPoint = getServiceEndpoint(
      bookingAllowancesPath,
      endpoints.BOOKING_ALLOWANCES
    );

    return await get(bookingAllowancesEndPoint, getBookingAllowances, null, context);
  } catch (error: Error | any) {
    handleError(error, basketReference);
  }
};

export const getCancellationPolicies = async (
  { cancellationPolicies }: { cancellationPolicies: CancellationPolicies },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [{ key: 'hotelId', value: cancellationPolicies.hotelId, required: true }];

    addFieldsToMap(fieldsToAdd, finalMap);
    // This field is required in the structure even if it has empty value
    addField(cancellationPolicies.basketRef ?? '', 'basketReference', finalMap);
    addField(cancellationPolicies.arrivalDate ?? '', 'arrivalDate', finalMap);
    addField(cancellationPolicies.ratePlanCode ?? '', 'ratePlanCode', finalMap);
    return await get(endpoints.CANCELLATION_POLICIES, getCancellationPolicies, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getMemos = async (
  { basketReference }: { basketReference: string },
  context: any
): Promise<any> => {
  try {
    const memosPath = endpoints.MEMOS.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );
    const memosEndPoint = getServiceEndpoint(memosPath, endpoints.MEMOS);

    return await get(memosEndPoint, getMemos, null, context);
  } catch (error: Error | any) {
    handleError(error, basketReference);
  }
};

export const getPmsBookingInformation = async (
  {
    basketReference,
    priceBreakdownNeeded
  }: { basketReference: string; priceBreakdownNeeded?: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const pmsBookingInformationPath = endpoints.PMS_BOOKING_INFORMATION.endpoint.replace(
      '{basketReference}',
      basketReference.toString()
    );
    const pmsBookingInformationEndPoint = getServiceEndpoint(
      pmsBookingInformationPath,
      endpoints.PMS_BOOKING_INFORMATION
    );

    const fieldsToAdd = [
      { key: 'priceBreakdownNeeded', value: priceBreakdownNeeded, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(pmsBookingInformationEndPoint, getPmsBookingInformation, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

export const getSearchBookingsCcui = async (
  { searchBookingsCcuiCriteria }: { searchBookingsCcuiCriteria: SearchBookingsCcuiCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const defaultValuesFieldsToAdd: any = [
      {
        key: 'bookingReference',
        value: searchBookingsCcuiCriteria.bookingReference! ?? '',
        required: false
      },
      {
        key: 'bookingsDatabaseSearch',
        value: searchBookingsCcuiCriteria.bookingsDatabaseSearch! ?? false,
        required: false
      },
      {
        key: 'pageSize',
        value: searchBookingsCcuiCriteria.pageSize! ?? 0,
        required: false
      },
      {
        key: 'pageNumber',
        value: searchBookingsCcuiCriteria.pageNumber! ?? 0,
        required: false
      }
    ];

    const fieldsToAdd: any = [
      {
        key: 'bookerLastName',
        value: searchBookingsCcuiCriteria.bookerLastName! ?? '',
        required: false
      },
      {
        key: 'guestLastName',
        value: searchBookingsCcuiCriteria.guestLastName! ?? '',
        required: false
      },
      {
        key: 'bookerPostcode',
        value: searchBookingsCcuiCriteria.bookerPostcode! ?? '',
        required: false
      },
      { key: 'hotelId', value: searchBookingsCcuiCriteria.hotelId! ?? '', required: false },
      { key: 'bookerEmail', value: searchBookingsCcuiCriteria.bookerEmail! ?? '', required: false },
      { key: 'bookerPhone', value: searchBookingsCcuiCriteria.bookerPhone! ?? '', required: false },
      {
        key: 'arrivalDateFrom',
        value: searchBookingsCcuiCriteria.arrivalDateFrom! ?? '',
        required: false
      },
      {
        key: 'arrivalDateTo',
        value: searchBookingsCcuiCriteria.arrivalDateTo! ?? '',
        required: false
      },
      {
        key: 'cancellationDate',
        value: searchBookingsCcuiCriteria.cancellationDate! ?? '',
        required: false
      },
      { key: 'companyName', value: searchBookingsCcuiCriteria.companyName! ?? '', required: false },
      {
        key: 'thirdPartyBookingReferenceNumber',
        value: searchBookingsCcuiCriteria.thirdPartyBookingReferenceNumber! ?? '',
        required: false
      },
      {
        key: 'continuationToken',
        value: searchBookingsCcuiCriteria.continuationToken! ?? '',
        required: false
      }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    addDefaultValuesFieldsToMap(defaultValuesFieldsToAdd, finalMap);
    return await get(endpoints.SEARCH_BOOKINGS_CCUI, getSearchBookingsCcui, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const cancelOnHoldReservation = async (
  { basketReference, hotelId }: { basketReference: String; hotelId: String },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.CANCEL_ON_HOLD_RESERVATION,
      cancelOnHoldReservation,
      {
        basketReference: basketReference,
        hotelId: hotelId
      },
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, {
      basketReference: basketReference,
      hotelId: hotelId
    });
  }
};

export const updateReasonForStay = async (
  { updateReasonForStayRequest }: { updateReasonForStayRequest: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.REASON_FOR_STAY,
      updateReasonForStay,
      updateReasonForStayRequest,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, updateReasonForStayRequest);
  }
};

export const updateRateCode = async (
  { rateCodeCriteria }: { rateCodeCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(endpoints.RATE_CODE, updateRateCode, rateCodeCriteria, context);
    //the output is a JSON and has to return a string
    return JSON.stringify(response.data);
  } catch (error: Error | any) {
    handleError(error, rateCodeCriteria);
  }
};

export const updateRoomType = async (
  { roomTypeCriteria }: { roomTypeCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(endpoints.ROOM_TYPE, updateRoomType, roomTypeCriteria, context);
    //the output is a JSON and has to return a string
    return JSON.stringify(response.data);
  } catch (error: Error | any) {
    handleError(error, roomTypeCriteria);
  }
};

export const amendDistribution = async (
  {
    basketReference,
    amendDistributionCriteria
  }: { basketReference: String; amendDistributionCriteria: any },
  context: any
): Promise<any> => {
  try {
    const amendDistributionEndPoint = replaceServiceEndpoint(
      endpoints.AMEND_DISTRIBUTION,
      '{basketReference}',
      basketReference.toString()
    );

    return await post(
      amendDistributionEndPoint,
      amendDistribution,
      amendDistributionCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, {
      amendDistributionCriteria: amendDistributionCriteria,
      basketReference: basketReference
    });
  }
};

export const updateReservationOverrideReasons = async (
  { updateReservationOverrideReasonsCriteria }: { updateReservationOverrideReasonsCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.RESERVATION_OVERRIDE_REASONS,
      updateReservationOverrideReasons,
      updateReservationOverrideReasonsCriteria,
      context
    );
    return response.data;
  } catch (error: Error | any) {
    handleError(error, updateReservationOverrideReasonsCriteria);
  }
};

export const copyBooking = async (
  { copyBookingCriteria }: { copyBookingCriteria: any },
  context: any
): Promise<any> => {
  try {
    return await post(endpoints.COPY_BOOKING, copyBooking, copyBookingCriteria, context);
  } catch (error: Error | any) {
    handleError(error, copyBookingCriteria);
  }
};

export const saveCharityPackage = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let roomsSelections: any = [];
    let packagesSelection = [];
    let rooms: { [k: string]: any } = {};
    let packageObj: { [k: string]: any } = {};
    let emptyPackagesSelection: any[] = [];
    let emptyRooms: { [k: string]: any } = {};
    let end = args.createPaymentCriteria.booking.rooms.length - 1;

    packageObj.id = args.createPaymentCriteria.charityPackageCode;
    packageObj.noOfSelections = 1;
    packagesSelection.push(packageObj);
    rooms.packagesSelection = packagesSelection;
    roomsSelections.push(rooms);

    emptyRooms.packagesSelection = emptyPackagesSelection;
    if (args.createPaymentCriteria.booking.rooms.length > 1) {
      for (let i = 0; i < end; i++) {
        roomsSelections.push(emptyRooms);
      }
    }
    const fieldsToAdd: any = [
      { key: 'basketReferenceId', value: args.basketReference, required: true },
      { key: 'hotelId', value: args.createPaymentCriteria.hotelId, required: true },
      {
        key: 'arrivalDate',
        value: args.createPaymentCriteria.booking.arrivalDate,
        required: true
      },
      {
        key: 'departureDate',
        value: args.createPaymentCriteria.booking.departureDate,
        required: true
      },
      { key: 'roomsSelections', value: roomsSelections, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await put(endpoints.SAVE_CHARITY_PACKAGES, saveCharityPackage, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const createMemo = async (
  { createMemoCriteria }: { createMemoCriteria: any },
  context: any
): Promise<any> => {
  try {
    return await post(endpoints.CREATE_MEMO, createMemo, createMemoCriteria, context);
  } catch (error: Error | any) {
    handleError(error, createMemoCriteria);
  }
};

export const updateCnp = async (
  { basketReference, updateCnpCriteria }: { basketReference: String; updateCnpCriteria: any },
  context: any
): Promise<any> => {
  try {
    const updateCnpEndPoint = replaceServiceEndpoint(
      endpoints.UPDATE_CNP,
      '{basketReference}',
      basketReference.toString()
    );

    const response = await put(updateCnpEndPoint, updateCnp, updateCnpCriteria, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, { basketReference: basketReference, updateCnpCriteria: updateCnpCriteria });
  }
};

export const createReservation = async (args: any, context: any): Promise<any> => {
  try {
    return await post(
      endpoints.CREATE_RESERVATION,
      createReservation,
      args.createReservationCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, {
      args
    });
  }
};

export const cancelReservation = async (args: any, context: any): Promise<any> => {
  try {
    return await post(
      endpoints.CANCEL_RESERVATION,
      cancelReservation,
      args.cancellationCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, {
      args
    });
  }
};

export const attachFileToReservation = async (
  { fileAttachmentCriteria }: { fileAttachmentCriteria: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.ATTACH_TO_RESERVATION,
      attachFileToReservation,
      fileAttachmentCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, fileAttachmentCriteria);
  }
};

export const updateReservationPackageScheduled = async (
  { updateReservationPackagesScheduledRequest }: { updateReservationPackagesScheduledRequest: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.UPDATE_RESERVATION_PACKAGES_SCHEDULED,
      updateReservationPackageScheduled,
      updateReservationPackagesScheduledRequest,
      context
    );
    //the output is a JSON and has to return a string
    return JSON.stringify(response.data);
  } catch (error: Error | any) {
    handleError(error, updateReservationPackagesScheduledRequest);
  }
};

export const updateReservationPreferences = async (
  { updateReservationPreferencesRequest }: { updateReservationPreferencesRequest: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.UPDATE_RESERVATION_PREFERENCES,
      updateReservationPreferences,
      updateReservationPreferencesRequest,
      context
    );
    // Check if the response status is 204 (No Content)
    if (response.status === 204) {
      return '{statusCode=204}';
    }
  } catch (error: Error | any) {
    handleError(error, updateReservationPreferences);
  }
};

export const addNewRoom = async (args: any, context: any): Promise<any> => {
  try {
    return await post(endpoints.ADD_NEW_ROOM, addNewRoom, args.addNewRoomCriteria, context);
  } catch (error: Error | any) {
    handleError(error, args);
  }
};

export const removeRoom = async (args: RemoveRoomCriteria, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'tempBookingRef', value: args.tempBookingRef, required: true },
      { key: 'reservationId', value: args.reservationId, required: true },
      { key: 'channel', value: args.bookingChannel.channel, required: true },
      { key: 'subchannel', value: args.bookingChannel.subchannel, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    addField(args.token ?? '', 'token', finalMap);
    addField(args.bookingChannel.language ?? '', 'language', finalMap);
    const endpointWithParams = getURL(endpoints.REMOVE_ROOM.endpoint, finalMap);
    const serviceEndpoint = { ...endpoints.REMOVE_ROOM, endpoint: endpointWithParams };
    return await post(serviceEndpoint, removeRoom, null, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const updateEmail = async (
  { basketReference, updateEmailCriteria }: { basketReference: string; updateEmailCriteria: any },
  context: any
): Promise<any> => {
  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.UPDATE_EMAIL,
      '{basketReference}',
      basketReference
    );
    const response = await put(serviceEndpoint, updateEmail, updateEmailCriteria, context);

    return response.data;
  } catch (error: Error | any) {
    handleError(error, {
      basketReference: basketReference,
      updateEmailCriteria: updateEmailCriteria
    });
  }
};

export const saveReservationAncillaries = async (
  { ancillariesCriteria }: { ancillariesCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.SAVE_RESERVATION,
      saveReservationAncillaries,
      ancillariesCriteria,
      context
    );
    return JSON.stringify(response.data).replace(':', '=').replaceAll('\"', '');
  } catch (error: Error | any) {
    handleError(error, ancillariesCriteria);
  }
};

export const createReservationGuest = async (
  { createReservationGuestCriteria }: { createReservationGuestCriteria: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.CREATE_RESERVATION_GUEST,
      createReservationGuest,
      createReservationGuestCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, createReservationGuestCriteria);
  }
};

export const updateReservationPackagesByReservation = async (
  { updateReservationPackagesRequest }: { updateReservationPackagesRequest: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(
      endpoints.UPDATE_RESERVATION_PACKAGES_BY_RESERVATION,
      updateReservationPackagesByReservation,
      updateReservationPackagesRequest,
      context
    );
    if (response.status === 200) {
      return `{basketReference=${response.data.basketReference}}`;
    }
  } catch (error: Error | any) {
    handleError(error, updateReservationPackagesRequest);
  }
};

export const amendEditRoom = async (
  { editRoomCriteria }: { editRoomCriteria: any },
  context: any
): Promise<any> => {
  try {
    const response = await put(endpoints.EDIT_ROOM, amendEditRoom, editRoomCriteria, context);
    return response.data;
  } catch (error: Error | any) {
    handleError(error, editRoomCriteria);
  }
};

export const changeBookingDates = async (
  { amendStayDatesCriteria }: { amendStayDatesCriteria: any },
  context: any
): Promise<any> => {
  try {
    let promotionInformationResponse = null;
    if (amendStayDatesCriteria?.country && amendStayDatesCriteria.brand) {
      const promotionArgs = {
        promotionsInformationCriteria: {
          country: amendStayDatesCriteria.country,
          language: amendStayDatesCriteria.bookingChannel?.language,
          channel: amendStayDatesCriteria.bookingChannel?.channel,
          brand: amendStayDatesCriteria.brand,
          stayStartDate: amendStayDatesCriteria.newStartDate,
          stayEndDate: amendStayDatesCriteria.newEndDate,
          basketReference: amendStayDatesCriteria.originalBasketReference
        }
      };
      promotionInformationResponse = await promotionsInformation(promotionArgs, context);

      const hasPromotion = !!promotionInformationResponse?.promotionCode;
      const promotionInvalid =
        hasPromotion &&
        (promotionInformationResponse?.showPromo !== true ||
          promotionInformationResponse?.isWithinPromoWindow !== true);
      if (promotionInvalid) {
        return {
          tempBasket: '',
          promotionsInformation: promotionInformationResponse
        };
      }
    }

    const amendResponse = await post(
      endpoints.CHANGE_BOOKING_DATES,
      changeBookingDates,
      amendStayDatesCriteria,
      context
    );

    return {
      ...amendResponse,
      promotionsInformation: promotionInformationResponse
    };
  } catch (error: Error | any) {
    handleError(error, amendStayDatesCriteria);
  }
};

export const getBookingInformationAuthenticated = async (args: any, context: any): Promise<any> => {
  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.BOOKING_INFORMATION_AUTHENTICATED,
      '{bookingReference}',
      args.bookingReference
    );

    return await get(serviceEndpoint, getBookingInformationAuthenticated, {}, context);
  } catch (error: Error | any) {
    handleError(error, args);
  }
};

export const getBookingInformationAuthenticatedWithToken = async (
  args: any,
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.BOOKING_INFORMATION_AUTHENTICATED_WITH_TOKEN,
      '{bookingReference}',
      args.bookingReference
    );
    addFieldIfNotUndefined(args.token, 'token', finalMap);

    return await get(
      serviceEndpoint,
      getBookingInformationAuthenticatedWithToken,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, args);
  }
};
