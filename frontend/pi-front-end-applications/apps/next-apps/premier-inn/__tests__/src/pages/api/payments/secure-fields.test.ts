import type { NextApiRequest, NextApiResponse } from 'next';

import handler from '~pages/api/payments/secure-fields';

describe('POST /api/payments/secure-fields', () => {
  let mockReq: Partial<NextApiRequest>;
  let mockRes: Partial<NextApiResponse>;
  let mockJson: jest.Mock;
  let mockEnd: jest.Mock;
  let mockStatus: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();

    mockJson = jest.fn().mockReturnThis();
    mockEnd = jest.fn().mockReturnThis();
    mockStatus = jest.fn().mockReturnValue({ json: mockJson, end: mockEnd });

    mockReq = {
      method: 'POST',
      body: { basketId: 'basket-123' },
      headers: { referer: 'https://premierinn.com/gb/en/payment' },
    };

    mockRes = {
      status: mockStatus,
    } as Partial<NextApiResponse>;

    process.env.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API = 'http://localhost:9200';

    global.fetch = jest.fn();
  });

  afterEach(() => {
    delete process.env.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API;
  });

  describe('Method validation', () => {
    it.each(['GET', 'PUT', 'DELETE'])('returns 405 for %s method', async (method) => {
      mockReq.method = method;

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockStatus).toHaveBeenCalledWith(405);
      expect(mockJson).toHaveBeenCalledWith({ error: 'Method not allowed' });
    });
  });

  describe('Request validation', () => {
    it('returns 400 when basketId is missing from body', async () => {
      mockReq.body = {};

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockStatus).toHaveBeenCalledWith(400);
      expect(mockJson).toHaveBeenCalledWith({ error: 'basketId is required' });
    });
  });

  describe('Configuration validation', () => {
    it('returns 500 when NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API is not configured', async () => {
      delete process.env.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API;

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockStatus).toHaveBeenCalledWith(500);
      expect(mockJson).toHaveBeenCalledWith({
        error: 'Payment orchestrator URL not configured',
      });
    });
  });

  describe('Successful response', () => {
    it('returns 201 with JSON data on successful backend call', async () => {
      const mockData = { transactionId: 'txn-abc-123' };

      (global.fetch as jest.Mock).mockResolvedValue({
        ok: true,
        json: jest.fn().mockResolvedValue(mockData),
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockStatus).toHaveBeenCalledWith(201);
      expect(mockJson).toHaveBeenCalledWith(mockData);
    });
  });

  describe('Backend failure', () => {
    it('returns 502 when backend call fails (non-ok response)', async () => {
      (global.fetch as jest.Mock).mockResolvedValue({
        ok: false,
        status: 503,
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockStatus).toHaveBeenCalledWith(502);
      expect(mockJson).toHaveBeenCalledWith({
        error: 'Failed to initialize payment session',
      });
    });

    it('returns 502 when fetch throws a network error', async () => {
      (global.fetch as jest.Mock).mockRejectedValue(new Error('Network error'));

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockStatus).toHaveBeenCalledWith(502);
      expect(mockJson).toHaveBeenCalledWith({
        error: 'Failed to initialize payment session',
      });
    });
  });

  describe('returnUrl construction', () => {
    it('appends &source=datatrans when referer has existing query params', async () => {
      mockReq.headers = {
        referer: 'https://premierinn.com/gb/en/payment?reservationId=123',
      };

      const mockData = { transactionId: 'txn-456' };
      (global.fetch as jest.Mock).mockResolvedValue({
        ok: true,
        json: jest.fn().mockResolvedValue(mockData),
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(global.fetch).toHaveBeenCalledWith(
        'http://localhost:9200/api/payments/init',
        expect.objectContaining({
          body: expect.stringContaining('"basketId":"basket-123"'),
        })
      );

      const calledBody = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body as string);
      expect(calledBody).toMatchObject({
        basketId: 'basket-123',
        returnUrl: 'https://premierinn.com/gb/en/payment?reservationId=123&source=datatrans',
      });
    });

    it('appends ?source=datatrans when referer has no existing query params', async () => {
      mockReq.headers = {
        referer: 'https://premierinn.com/gb/en/payment',
      };

      const mockData = { transactionId: 'txn-789' };
      (global.fetch as jest.Mock).mockResolvedValue({
        ok: true,
        json: jest.fn().mockResolvedValue(mockData),
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(global.fetch).toHaveBeenCalledWith(
        'http://localhost:9200/api/payments/init',
        expect.objectContaining({
          body: expect.stringContaining('"basketId":"basket-123"'),
        })
      );

      const calledBody = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body as string);
      expect(calledBody).toMatchObject({
        basketId: 'basket-123',
        returnUrl: 'https://premierinn.com/gb/en/payment?source=datatrans',
      });
    });
  });
});
