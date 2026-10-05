import { getPromotionPanel } from '../../../../../apollo/subgraphs/promotion-panel-pipeline/services/promotion-panel-service';
import * as restClient from '../../../../../apollo/client/rest-client';

describe('getPromotionPanel', () => {
  beforeEach(() => {
    process.env.LOCAL = 'true';
  });
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should return the correct promotion panel when provided with bookingFlowCriteria', async () => {
    // Define the input (bookingFlowCriteria)
    const bookingFlowCriteria: any = {
      country: 'gb',
      language: 'en',
      hotelId: 'MANOLD',
      rateCode: 'FLEXRATE',
      bookingChannel: 'BB'
    };

    // Call the service with the input
    const response = await getPromotionPanel({ bookingFlowCriteria }, context);

    const expectedResponse = [
      {
        description:
          '<p>Our luxury beds and our brand new pillows are now available to buy online.</p>\n',
        image: '/content/dam/pi/websites/desktop/booking/bed-shop-promo.jpg',
        linkLabel: 'Find out more',
        linkPath:
          'https://www.premierinn.com/gb/en/why/sleep/buy-our-bed.html?INTCMP=BEDSHOP_confirmationPanel',
        name: 'Buy a Premier Inn bed'
      }
    ];

    expect(response).toEqual(expectedResponse);
  });

  it('should handle errors gracefully when fetching payment info messages', async () => {
    const expectedMessage = 'Hotel information response is empty';
    const expectedJsonError = {
      message: expectedMessage,
      errors: [
        {
          field: 'Error',
          message: expectedMessage
        }
      ],
      errorType: 404
    };
    const expectedError = new Error(JSON.stringify(expectedJsonError));
    const bookingFlowCriteria: any = {
      country: 'gb',
      language: 'en',
      hotelId: 'HOTEL_1',
      rateCode: 'RATE_CODE_1',
      bookingChannel: 'BB'
    };

    jest.spyOn(restClient, 'get').mockReturnValueOnce(Promise.resolve(null));

    await expect(getPromotionPanel({ bookingFlowCriteria }, context)).rejects.toThrow(
      expectedMessage
    );
  });
});
