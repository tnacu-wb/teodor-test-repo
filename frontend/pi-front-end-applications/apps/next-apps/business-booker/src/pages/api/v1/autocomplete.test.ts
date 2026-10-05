import axios from 'axios';
import { NextApiRequest, NextApiResponse } from 'next';

import handler from './autocomplete';

jest.mock('axios');

const mockedAxios = axios as jest.Mocked<typeof axios>;

const createMockRequest = (overrides: Partial<NextApiRequest> = {}): NextApiRequest =>
  ({
    method: 'GET',
    query: {
      input: '/lon',
      'gplaces[components]': 'country:uk|country:de',
    },
    ...overrides,
  }) as NextApiRequest;

const createMockResponse = (): NextApiResponse => {
  const res: Partial<NextApiResponse> = {
    status: jest.fn().mockReturnThis(),
    json: jest.fn().mockReturnThis(),
    end: jest.fn().mockReturnThis(),
    setHeader: jest.fn().mockReturnThis(),
  };
  return res as NextApiResponse;
};

describe('Autocomplete API handler', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.spyOn(console, 'error').mockImplementation();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('GET requests', () => {
    it('should return autocomplete results on successful API call', async () => {
      const mockData = { predictions: [{ description: 'London, UK' }] };
      mockedAxios.get.mockResolvedValueOnce({ data: mockData });

      const req = createMockRequest();
      const res = createMockResponse();

      await handler(req, res);

      expect(mockedAxios.get).toHaveBeenCalledWith(
        expect.stringContaining('https://api-uat.whitbread.co.uk/v1/autocomplete')
      );
      expect(res.status).toHaveBeenCalledWith(200);
      expect(res.json).toHaveBeenCalledWith(mockData);
    });

    it('should include correct query parameters in API call', async () => {
      const mockData = { predictions: [] };
      mockedAxios.get.mockResolvedValueOnce({ data: mockData });

      const req = createMockRequest({
        query: {
          input: 'manchester',
          'gplaces[components]': 'country:uk',
        },
      });
      const res = createMockResponse();

      await handler(req, res);

      expect(mockedAxios.get).toHaveBeenCalledWith(expect.stringContaining('input=manchester'));
      expect(mockedAxios.get).toHaveBeenCalledWith(
        expect.stringContaining('gplaces[components]=country%3Auk')
      );
    });

    it('should return 500 error when API call fails', async () => {
      const errorMessage = 'Network Error';
      mockedAxios.get.mockRejectedValueOnce(new Error(errorMessage));

      const req = createMockRequest();
      const res = createMockResponse();

      await handler(req, res);

      expect(res.status).toHaveBeenCalledWith(500);
      expect(res.json).toHaveBeenCalledWith({
        error: 'Internal Server Error',
        details: errorMessage,
      });
    });

    it('should log error when API call fails', async () => {
      const errorMessage = 'Network Error';
      mockedAxios.get.mockRejectedValueOnce(new Error(errorMessage));

      const req = createMockRequest();
      const res = createMockResponse();

      await handler(req, res);

      expect(console.error).toHaveBeenCalledWith('Error during API call:', errorMessage);
    });

    it('should handle non-Error objects in catch block', async () => {
      mockedAxios.get.mockRejectedValueOnce('Unknown error type');

      const req = createMockRequest();
      const res = createMockResponse();

      await handler(req, res);

      expect(res.status).toHaveBeenCalledWith(500);
      expect(res.json).toHaveBeenCalledWith({
        error: 'Internal Server Error',
        details: 'Unknown error',
      });
    });
  });

  describe('Non-GET requests', () => {
    it('should return 405 for POST requests', async () => {
      const req = createMockRequest({ method: 'POST' });
      const res = createMockResponse();

      await handler(req, res);

      expect(res.setHeader).toHaveBeenCalledWith('Allow', ['GET']);
      expect(res.status).toHaveBeenCalledWith(405);
      expect(res.end).toHaveBeenCalledWith('Method POST Not Allowed');
    });

    it('should return 405 for PUT requests', async () => {
      const req = createMockRequest({ method: 'PUT' });
      const res = createMockResponse();

      await handler(req, res);

      expect(res.setHeader).toHaveBeenCalledWith('Allow', ['GET']);
      expect(res.status).toHaveBeenCalledWith(405);
      expect(res.end).toHaveBeenCalledWith('Method PUT Not Allowed');
    });

    it('should return 405 for DELETE requests', async () => {
      const req = createMockRequest({ method: 'DELETE' });
      const res = createMockResponse();

      await handler(req, res);

      expect(res.setHeader).toHaveBeenCalledWith('Allow', ['GET']);
      expect(res.status).toHaveBeenCalledWith(405);
      expect(res.end).toHaveBeenCalledWith('Method DELETE Not Allowed');
    });
  });
});
