import { CreateGroupBookingCriteria } from '../models/create-group-booking-criteria';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, getServiceEndpoint } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get, post } from '../../../client/rest-client';

export const getCreateGroupBooking = async (
  {
    hotelCode,
    createGroupBookingCriteria
  }: { hotelCode: string; createGroupBookingCriteria: CreateGroupBookingCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const createGroupBookingEndpoint = endpoints.CREATE_GROUP_BOOKING.endpoint.replace(
      '{hotelId}',
      hotelCode
    );
    const createGroupBookingEndPoint = getServiceEndpoint(
      createGroupBookingEndpoint,
      endpoints.CREATE_GROUP_BOOKING
    );
    const fieldsToAdd = [
      { key: 'title', value: createGroupBookingCriteria.title, required: true },
      { key: 'firstName', value: createGroupBookingCriteria.firstName, required: true },
      { key: 'lastName', value: createGroupBookingCriteria.lastName, required: true },
      { key: 'emailAddress', value: createGroupBookingCriteria.emailAddress, required: true },
      { key: 'phoneNumber', value: createGroupBookingCriteria.phoneNumber, required: true },
      { key: 'bookerType', value: createGroupBookingCriteria.bookerType, required: true },
      { key: 'purposeOfStay', value: createGroupBookingCriteria.purposeOfStay, required: true },
      {
        key: 'isSchoolOrYouth',
        value: createGroupBookingCriteria.isSchoolOrYouth ?? false,
        required: false
      },
      { key: 'reasonForVisit', value: createGroupBookingCriteria.reasonForVisit, required: true },
      {
        key: 'reasonForVisitOther',
        value: createGroupBookingCriteria.reasonForVisitOther,
        required: false
      },
      { key: 'companyName', value: createGroupBookingCriteria.companyName, required: false },
      {
        key: 'isPackageTypeBf',
        value: createGroupBookingCriteria.isPackageTypeBf ?? false,
        required: true
      },
      {
        key: 'isPackageTypeMealDeal',
        value: createGroupBookingCriteria.isPackageTypeMealDeal ?? false,
        required: true
      },
      { key: 'hotelName', value: createGroupBookingCriteria.hotelName, required: false },
      { key: 'hotelBrand', value: createGroupBookingCriteria.hotelBrand, required: false },
      { key: 'arrivalDate', value: createGroupBookingCriteria.arrivalDate, required: true },
      { key: 'departureDate', value: createGroupBookingCriteria.departureDate, required: true },
      {
        key: 'singleOccupancy',
        value: createGroupBookingCriteria.singleOccupancy,
        required: false
      },
      {
        key: 'doubleOccupancy',
        value: createGroupBookingCriteria.doubleOccupancy,
        required: false
      },
      { key: 'twinRooms', value: createGroupBookingCriteria.twinRooms, required: false },
      {
        key: 'isTravellingWithChild',
        value: createGroupBookingCriteria.isTravellingWithChild ?? false,
        required: false
      },
      {
        key: 'isAccessibleRoom',
        value: createGroupBookingCriteria.isAccessibleRoom ?? false,
        required: false
      },
      {
        key: 'familyOf21A1C',
        value: createGroupBookingCriteria.familyOf21A1C,
        required: false
      },
      {
        key: 'familyOf32A1C',
        value: createGroupBookingCriteria.familyOf32A1C,
        required: false
      },
      {
        key: 'familyOf31A2C',
        value: createGroupBookingCriteria.familyOf31A2C,
        required: false
      },
      {
        key: 'familyOf42A2C',
        value: createGroupBookingCriteria.familyOf42A2C,
        required: false
      },
      {
        key: 'accessibleSingle',
        value: createGroupBookingCriteria.accessibleSingle,
        required: false
      },
      {
        key: 'accessibleDouble',
        value: createGroupBookingCriteria.accessibleDouble,
        required: false
      },
      {
        key: 'accessibleTwin',
        value: createGroupBookingCriteria.accessibleTwin,
        required: false
      },
      {
        key: 'additionalInformation',
        value: createGroupBookingCriteria.additionalInformation,
        required: false
      },
      { key: 'language', value: createGroupBookingCriteria.language, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    return await post(createGroupBookingEndPoint, getCreateGroupBooking, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, createGroupBookingCriteria);
  }
};
