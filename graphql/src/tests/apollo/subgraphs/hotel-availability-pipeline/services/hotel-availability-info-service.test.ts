import { get } from '../../../../../apollo/client/rest-client';
import { getHotelAvailabilityInfo } from '../../../../../apollo/subgraphs/hotel-availabilities-pipeline/services/hotel-availability-info-service';
import {
  availabilityMock,
  criteria,
  rateClassCategMock,
  ratesInfoMock,
  roomTypeInfoMock
} from './constants-hotel-availability-info';
jest.mock('../../../../../apollo/client/rest-client');

describe('getHotelAvailabilityInfo', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should return the correct hotel availability info', async () => {
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(availabilityMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(ratesInfoMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(roomTypeInfoMock));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(rateClassCategMock));

    const response = await getHotelAvailabilityInfo(criteria, context);

    expect(response.roomRates[0].rateClassification.rateClassification).toEqual('FLEXRATE');
    expect(response.roomRates[0].rateClassification.rateName).toEqual('Flex');
    expect(response.roomRates[0].roomTypes[0].rooms[0].roomClassOrder.order).toEqual(1);
    expect(response.roomRates[0].roomTypes[0].rooms[0].roomTypeDetails.roomCategory).toEqual(
      'Premier Plus'
    );

    expect(response.roomRates[0].roomTypes[0].rooms[0].isSubstitution).toEqual(true);
    expect(response.roomRates[0].roomTypes[0].rooms[0].substitution).toEqual('PPLDBL');

    expect(response.roomRates[1].rateClassification).toEqual(undefined);
    expect(response.roomRates[1].roomTypes[0].rooms[0].roomClassOrder).toEqual(undefined);
    expect(response.roomRates[1].roomTypes[0].rooms[0].roomTypeDetails).toEqual(undefined);

    expect(response.roomRates[1].roomTypes[0].rooms[0].isSubstitution).toEqual(false);
    expect(response.roomRates[1].roomTypes[0].rooms[0].substitution).toEqual(null);

    expect(response.roomRates[2].rateClassification).toEqual(undefined);
    expect(response.roomRates[2].roomTypes[0].rooms[0].roomClassOrder).toEqual(undefined);
    expect(response.roomRates[2].roomTypes[0].rooms[0].roomTypeDetails).toEqual(undefined);

    expect(response.roomRates[2].roomTypes[0].rooms[0].isSubstitution).toEqual(true);
    expect(response.roomRates[2].roomTypes[0].rooms[0].substitution).toEqual('SUPERIOR');
  });
});
