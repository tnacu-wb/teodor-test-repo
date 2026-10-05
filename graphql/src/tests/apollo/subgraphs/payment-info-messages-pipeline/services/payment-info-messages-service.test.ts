import { getPaymentInfoMessagesService } from '../../../../../apollo/subgraphs/payment-info-messages-pipeline/services/payment-info-messages-service';
import * as restClient from '../../../../../apollo/client/rest-client';

describe('getPaymentInfoMessagesService', () => {
  beforeEach(() => {
    process.env.LOCAL = 'true';
  });
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should return the correct payment info messages when provided with bookingFlowCriteria', async () => {
    // Define the input (bookingFlowCriteria)
    const bookingFlowCriteria: any = {
      country: 'gb',
      language: 'en',
      hotelId: 'LONEUS',
      rateCode: 'NONFLEX',
      bookingChannel: 'BB'
    };

    // Call the service with the input
    const response = await getPaymentInfoMessagesService({ bookingFlowCriteria }, context);

    // Define the expected response
    const expectedResponse = [
      {
        messages: ['<p>Pay now. No amends or refunds</p>\n'],
        paymentType: 'PAY_NOW'
      }
    ];

    // Assert that the response matches the expected response
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

    await expect(getPaymentInfoMessagesService({ bookingFlowCriteria }, context)).rejects.toThrow(
      expectedMessage
    );
  });
});
