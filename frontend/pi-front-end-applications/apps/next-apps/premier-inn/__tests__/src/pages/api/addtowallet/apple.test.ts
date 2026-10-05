import { decryptCryptString, formatReservationNumberInQuery } from '@whitbread-eos/utils';
import axios from 'axios';
import { NextApiRequest, NextApiResponse } from 'next';
import handler from '../../../../../src/pages/api/addtowallet/apple';

jest.mock('@whitbread-eos/utils', () => ({
  decryptCryptString: jest.fn(),
  formatReservationNumberInQuery: jest.requireActual('@whitbread-eos/utils').formatReservationNumberInQuery,
}));

jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;
const mockedDecrypt = decryptCryptString as jest.MockedFunction<typeof decryptCryptString>;

describe('/api/addtowallet/apple', () => {
  let mockReq: Partial<NextApiRequest>;
  let mockRes: Partial<NextApiResponse>;
  let statusMock: jest.Mock;
  let jsonMock: jest.Mock;
  let setHeaderMock: jest.Mock;
  let endMock: jest.Mock;

  beforeEach(() => {
    statusMock = jest.fn().mockReturnThis();
    jsonMock = jest.fn();
    setHeaderMock = jest.fn();
    endMock = jest.fn();
    mockRes = { status: statusMock, json: jsonMock, setHeader: setHeaderMock, end: endMock };
    jest.clearAllMocks();
    process.env.NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION = 'test-key';
    process.env.NEXT_PUBLIC_REST_API = 'https://api.example.com';
  });

  afterEach(() => {
    delete process.env.NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION;
    delete process.env.NEXT_PUBLIC_REST_API;
  });

  it('returns 405 for non-GET methods', async () => {
    mockReq = { method: 'POST' };
    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

    expect(statusMock).toHaveBeenCalledWith(405);
    expect(setHeaderMock).toHaveBeenCalledWith('Allow', 'GET');
    expect(endMock).toHaveBeenCalledWith('Method Not Allowed');
  });

  it('returns 400 for missing or invalid referrer', async () => {
    mockReq = { method: 'GET', query: {} };
    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);
    expect(statusMock).toHaveBeenCalledWith(400);
    expect(jsonMock).toHaveBeenCalledWith({ message: 'Missing or invalid referrer parameter' });

    mockReq = { method: 'GET', query: { referrer: 123 } };
    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);
    expect(statusMock).toHaveBeenCalledWith(400);
    expect(jsonMock).toHaveBeenCalledWith({ message: 'Missing or invalid referrer parameter' });
  });

  it('replaces spaces with + and decodes referrer correctly', async () => {
    mockReq = { method: 'GET', query: { referrer: 'param1 value1' } };
    const mockData = Buffer.from('pkpass data');
    mockedDecrypt.mockResolvedValue('param1=value1&reservationNumber=ABC123');
    mockedAxios.get.mockResolvedValue({ data: mockData });

    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

    expect(mockedDecrypt).toHaveBeenCalledWith('param1+value1', 'test-key');
    expect(mockedAxios.get).toHaveBeenCalledWith(
      expect.stringContaining('v1/hotel-wallet?'),
      { responseType: 'arraybuffer' }
    );
  });

  it('returns 500 on decryption failure', async () => {
    mockReq = { method: 'GET', query: { referrer: 'encoded%20referrer' } };
    mockedDecrypt.mockRejectedValue(new Error('Decryption failed'));

    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

    expect(statusMock).toHaveBeenCalledWith(500);
    expect(jsonMock).toHaveBeenCalledWith({ error: 'Failed to fetch pkpass' });
  });

  it('returns 500 on axios failure', async () => {
    mockReq = { method: 'GET', query: { referrer: 'encoded%20referrer' } };
    mockedDecrypt.mockResolvedValue('param1=value1&reservationNumber=ABC123');
    mockedAxios.get.mockRejectedValue(new Error('API failed'));

    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

    expect(statusMock).toHaveBeenCalledWith(500);
    expect(jsonMock).toHaveBeenCalledWith({ error: 'Failed to fetch pkpass' });
  });

  it('successfully fetches and returns pkpass', async () => {
    mockReq = { method: 'GET', query: { referrer: 'encoded%20referrer' } };
    const mockData = Buffer.from('pkpass data');
    mockedDecrypt.mockResolvedValue('param1=value1&reservationNumber=ABC123');
    mockedAxios.get.mockResolvedValue({ data: mockData });

    await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

    expect(setHeaderMock).toHaveBeenCalledWith('Content-Type', 'application/vnd.apple.pkpass');
    expect(setHeaderMock).toHaveBeenCalledWith(
      'Content-Disposition',
      'attachment; filename="hotel-wallet.pkpass"'
    );
    expect(setHeaderMock).toHaveBeenCalledWith('Cache-Control', 'no-store');
    expect(statusMock).toHaveBeenCalledWith(200);
    expect(endMock).toHaveBeenCalledWith(mockData);
  });
});

describe('formatReservationNumberInQuery', () => {
  it('handles missing reservationNumber', () => {
    const result = formatReservationNumberInQuery('param1=value1');
    expect(result).toContain('reservationNumber=');
  });

  it('removes non-alphanumeric characters', () => {
    const result = formatReservationNumberInQuery('reservationNumber=AB@#C-1234&param=xyz');
    expect(result).toContain('reservationNumber=ABC1234');
  });

  it('limits letters to 3 and digits to 7', () => {
    const result = formatReservationNumberInQuery('reservationNumber=ABCDEFG1234567890');
    expect(result).toContain('reservationNumber=ABC1234567');
  });

  it('handles only letters', () => {
    const result = formatReservationNumberInQuery('reservationNumber=ABCDE');
    expect(result).toContain('reservationNumber=ABC');
  });

  it('handles only digits', () => {
    const result = formatReservationNumberInQuery('reservationNumber=1234567890');
    expect(result).toContain('reservationNumber=1234567');
  });

  it('handles empty reservationNumber', () => {
    const result = formatReservationNumberInQuery('reservationNumber=');
    expect(result).toContain('reservationNumber=');
  });
});
