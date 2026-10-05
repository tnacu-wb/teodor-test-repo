import { NextApiRequest, NextApiResponse } from 'next';

import handler from './remove-guest-form-data';

const mockRemoveItem = jest.fn();
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
    GUEST_DETAILS_FORM_DATA_CCUI: 'GuestDetailsFormDataCCUI',
  },
  RedisStorageServer: {
    getInstance: (...args: unknown[]) => mockGetInstance(...args),
  },
}));

describe('/api/guest-details/remove-guest-form-data', () => {
  let req: Partial<NextApiRequest>;
  let res: Partial<NextApiResponse>;
  const validBasketReference = 'AJK-abca8225-5226-4db5-aebd-74c78df9b168';

  beforeEach(() => {
    jest.clearAllMocks();

    mockGetInstance.mockReturnValue({
      removeItem: mockRemoveItem,
    });

    req = { method: 'GET', body: {} };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
      setHeader: jest.fn().mockReturnThis(),
    };
  });

  it('should return 400 on PUT when basketReferenceId is missing', async () => {
    req.method = 'PUT';
    req.body = {};

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ error: 'basketReferenceId is required' });
  });

  it('should remove form data from Redis on PUT and return 200', async () => {
    req.method = 'PUT';
    req.body = { basketReferenceId: validBasketReference };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockGetInstance).toHaveBeenCalled();
    expect(mockRemoveItem).toHaveBeenCalledWith(
      `GuestDetailsFormDataCCUI::${validBasketReference}`
    );
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ success: true });
  });

  it('should return 400 on PUT when basketReferenceId format is invalid', async () => {
    req.method = 'PUT';
    req.body = { basketReferenceId: 'ABC123' };
    await handler(req as NextApiRequest, res as NextApiResponse);
    expect(res.status).toHaveBeenCalledWith(400);
    expect(res.json).toHaveBeenCalledWith({ error: 'Invalid basketReferenceId format' });
    expect(mockRemoveItem).not.toHaveBeenCalled();
  });

  it('should return 500 on PUT when Redis remove fails', async () => {
    req.method = 'PUT';
    req.body = { basketReferenceId: validBasketReference };
    mockRemoveItem.mockRejectedValueOnce(new Error('redis remove failed'));

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockLoggerError).toHaveBeenCalledWith(
      { error: expect.any(Error) },
      'CCUI_REMOVE_GUEST_DETAILS_FORM_DATA_ERROR'
    );
    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to remove guest details form data' });
  });

  it('should return 405 for unsupported methods', async () => {
    req.method = 'GET';

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(405);
    expect(res.json).toHaveBeenCalledWith({ error: 'Method not allowed' });
  });
});
