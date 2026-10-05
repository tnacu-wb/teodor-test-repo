import { NextApiRequest, NextApiResponse } from 'next';

import handler from './set-guest-form-data';

const mockSetItem = jest.fn();
const mockGetInstance = jest.fn();
const mockLoggerError = jest.fn();
const mockEncodeToBase64 = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  encodeToBase64: (...args: unknown[]) => mockEncodeToBase64(...args),
  logger: {
    error: (...args: unknown[]) => mockLoggerError(...args),
  },
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  RedisKeyPrefix: {
    GUEST_DETAILS_FORM_DATA_PI: 'GuestDetailsFormDataPI',
  },
  RedisStorageServer: {
    getInstance: (...args: unknown[]) => mockGetInstance(...args),
  },
}));

describe('/api/guest-details/set-guest-form-data', () => {
  let req: Partial<NextApiRequest>;
  let res: Partial<NextApiResponse>;
  const validBasketReference = 'AJK-abca8225-5226-4db5-aebd-74c78df9b168';

  beforeEach(() => {
    jest.clearAllMocks();
    mockEncodeToBase64.mockImplementation((value: unknown) => `encoded:${String(value)}`);

    mockGetInstance.mockReturnValue({
      setItem: mockSetItem,
    });

    req = { method: 'GET', body: {} };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
      setHeader: jest.fn().mockReturnThis(),
    };
  });

  it('should save form data to Redis on POST and return 200', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: { firstName: 'John', title: 'Mr' },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockGetInstance).toHaveBeenCalled();
    expect(mockEncodeToBase64).toHaveBeenCalledWith(
      JSON.stringify({ firstName: 'John', title: 'Mr' })
    );
    expect(mockSetItem).toHaveBeenCalledWith(
      `GuestDetailsFormDataPI::${validBasketReference}`,
      `encoded:${JSON.stringify({ firstName: 'John', title: 'Mr' })}`
    );
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ success: true });
  });

  it('should return 400 on POST when required payload is missing', async () => {
    req.method = 'POST';
    req.body = { basketReferenceId: validBasketReference };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({
      error: 'basketReferenceId and formData are required',
    });
  });

  it('should return 400 on POST when basketReferenceId format is invalid', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: 'ABC123',
      formData: { firstName: 'John', title: 'Mr' },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ error: 'Invalid basketReferenceId format' });
    expect(mockSetItem).not.toHaveBeenCalled();
  });

  it('should return 400 on POST when formData has unknown keys', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: { fruit: 'apple', car: 'AudI', firstName: 'John', title: 'Mr' },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ error: 'Invalid formData payload' });
    expect(mockSetItem).not.toHaveBeenCalled();
  });

  it('should save form data when payload has only known keys', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: {
        reasonForStay: 'BUS',
        title: 'Mr',
        firstName: 'Alex',
        lastName: '',
        email: '',
        phone: '',
        landline: '',
        companyName: '',
        addressLine1: '',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        postalCode: '',
        manualAddressToggle: '',
        cityName: '',
        postcodeAddress: '',
        addressSelection: '',
        countryCode: 'GB',
        acceptFutureMailing: true,
        updateProfileConsent: false,
        bookingForSomeoneElse: false,
        basketReferenceId: '',
        billing_countryCode: 'GB',
        billing_companyName: '',
        billing_addressLine1: '',
        billing_addressLine2: '',
        billing_addressLine3: '',
        billing_addressLine4: '',
        billing_cityName: '',
        billing_postalCode: '',
        whoBookerIsTabs: 'MYSELF',
      },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ success: true });
    expect(mockSetItem).toHaveBeenCalled();
  });

  it('should return 400 on POST when leadGuest contains unknown keys', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: {
        firstName: 'John',
        title: 'Mr',
        leadGuest: [{ firstName: 'Jane', spaceship: 'falcon', customField: 'any-value' }],
      },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ error: 'Invalid formData payload' });
    expect(mockSetItem).not.toHaveBeenCalled();
  });

  it('should save form data when leadGuest contains only known keys', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: {
        firstName: 'John',
        title: 'Mr',
        leadGuest: [
          {
            firstName: 'Jane',
            lastName: 'Doe',
            title: 'Ms',
            email: '',
            addressLine1: '',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            postcodeAddress: '',
            companyName: '',
            country: '',
          },
        ],
      },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ success: true });
    expect(mockSetItem).toHaveBeenCalled();
  });

  it('should return 400 on POST when leadGuest has invalid field types', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: {
        firstName: 'John',
        title: 'Mr',
        leadGuest: [{ firstName: 123 }],
      },
    };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ error: 'Invalid formData payload' });
    expect(mockSetItem).not.toHaveBeenCalled();
  });

  it('should return 500 on POST when Redis save fails', async () => {
    req.method = 'POST';
    req.body = {
      basketReferenceId: validBasketReference,
      formData: { firstName: 'John' },
    };
    mockSetItem.mockRejectedValueOnce(new Error('redis set failed'));

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockLoggerError).toHaveBeenCalledWith(
      { error: expect.any(Error) },
      'PI_SAVE_GUEST_DETAILS_FORM_DATA_ERROR'
    );
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to save guest details form data' });
  });

  it('should return 405 for unsupported methods', async () => {
    req.method = 'GET';

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(405);
    expect(res.json).toHaveBeenCalledWith({ error: 'Method not allowed' });
  });
});
