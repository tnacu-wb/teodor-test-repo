import { post } from '../../../../../../src/apollo/client/rest-client';
import { CreateGroupBookingCriteria } from '../../../../../apollo/subgraphs/hotel-entity-service/models/create-group-booking-criteria';
import {
  objectIsNullEmptyOrUndefined,
  validateDateRange
} from '../../../../../apollo/utils/base-utils';
import { getCreateGroupBooking } from '../../../../../apollo/subgraphs/hotel-entity-service/services/create-group-boocking-service';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-entity-service/services/base-service';
import { handleError } from '../../../../../apollo/exception/error-handler';
import { getHotelAvailabilitiesByIdsV2 } from '../../../../../apollo/subgraphs/hotel-entity-service/services/hotel-availability-by-ids-v2-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getCreateGroupBooking', () => {
  const context = {};
  const hotelCode = 'HOTEL123';
  const expectedEndpoint = {
    endpoint: '/v1/hotels/HOTEL123/group/bookingForm',
    flowCode: 'DIGITAL_CRE_001',
    axiosClient: expect.any(Function)
  };
  const createGroupBookingCriteria: CreateGroupBookingCriteria = {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    emailAddress: 'john.doe@example.com',
    phoneNumber: '1234567890',
    bookerType: 'Individual',
    purposeOfStay: 'Business',
    isSchoolOrYouth: false,
    reasonForVisit: 'Conference',
    reasonForVisitOther: '',
    companyName: 'Example Corp',
    isPackageTypeBf: true,
    isPackageTypeMealDeal: false,
    hotelName: 'Example Hotel',
    hotelBrand: 'Example Brand',
    arrivalDate: '2023-12-01',
    departureDate: '2023-12-05',
    singleOccupancy: 1,
    doubleOccupancy: 1,
    twinRooms: 0,
    isTravellingWithChild: false,
    isAccessibleRoom: false,
    familyOf21A1C: 0,
    familyOf32A1C: 0,
    familyOf31A2C: 0,
    familyOf42A2C: 0,
    accessibleSingle: 0,
    accessibleDouble: 0,
    accessibleTwin: 0,
    additionalInformation: '',
    language: 'en'
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call post with correct parameters when creating group booking', async () => {
    const expectedMap = {
      title: 'Mr',
      firstName: 'John',
      lastName: 'Doe',
      emailAddress: 'john.doe@example.com',
      phoneNumber: '1234567890',
      bookerType: 'Individual',
      purposeOfStay: 'Business',
      isSchoolOrYouth: false,
      reasonForVisit: 'Conference',
      companyName: 'Example Corp',
      isPackageTypeBf: true,
      isPackageTypeMealDeal: false,
      hotelName: 'Example Hotel',
      hotelBrand: 'Example Brand',
      arrivalDate: '2023-12-01',
      departureDate: '2023-12-05',
      singleOccupancy: 1,
      doubleOccupancy: 1,
      twinRooms: 0,
      isTravellingWithChild: false,
      isAccessibleRoom: false,
      familyOf21A1C: 0,
      familyOf32A1C: 0,
      familyOf31A2C: 0,
      familyOf42A2C: 0,
      accessibleSingle: 0,
      accessibleDouble: 0,
      accessibleTwin: 0,
      language: 'en'
    };
    await getCreateGroupBooking({ hotelCode, createGroupBookingCriteria }, context);
    expect(post).toHaveBeenCalledWith(
      expectedEndpoint,
      getCreateGroupBooking,
      expectedMap,
      context
    );
  });

  it('should handle errors when creating group booking', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      getCreateGroupBooking({ hotelCode, createGroupBookingCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});
