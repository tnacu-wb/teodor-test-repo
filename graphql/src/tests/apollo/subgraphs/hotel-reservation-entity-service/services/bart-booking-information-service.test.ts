import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/base-service';
import { getBartBookingInformation } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/services/bart-booking-information-service';
import { BartBookingInformationCriteria } from '../../../../../apollo/subgraphs/hotel-reservation-entity-service/models/bart-booking-information-criteria';

jest.mock('../../../../../apollo/client/rest-client');

describe('getBartBookingInformation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const context = {};
  const bartBookingInformationCriteria: BartBookingInformationCriteria = {
    language: 'en',
    country: 'UK',
    bookingReference: '123',
    arrivalDate: '2023-10-10',
    bookerLastName: 'booker123'
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should handle all fields when getBartBookingInformation is called', async () => {
    await getBartBookingInformation(
      { bartBookingInformationCriteria: bartBookingInformationCriteria },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.BART_BOOKING_INFORMATION,
      getBartBookingInformation,
      {
        bookingReference: '123',
        arrivalDate: '2023-10-10',
        bookerLastName: 'booker123'
      },
      context
    );
  });

  it('should handle missing basketRef field when getBartBookingInformation is called', async () => {
    const emptyBookingInformationCriteria: BartBookingInformationCriteria = {
      language: 'en',
      country: 'UK',
      bookingReference: '',
      arrivalDate: '',
      bookerLastName: ''
    };
    await getBartBookingInformation(
      { bartBookingInformationCriteria: emptyBookingInformationCriteria },
      context
    );
    expect(get).toHaveBeenCalledWith(
      endpoints.BART_BOOKING_INFORMATION,
      getBartBookingInformation,
      {
        bookingReference: '',
        arrivalDate: '',
        bookerLastName: ''
      },
      context
    );
  });

  it('should handle errors correctly when getBartBookingInformation fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValue(error);

    await expect(
      getBartBookingInformation(
        { bartBookingInformationCriteria: bartBookingInformationCriteria },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
