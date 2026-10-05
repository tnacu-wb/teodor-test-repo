import {
  digitalKeyGenerateOtp,
  digitalKeyProvision,
  digitalKeyCheckIn
} from '../../../../../apollo/subgraphs/digital-key-service/services/digital-key-service';
import { get, post, put } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/digital-key-service/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');
beforeAll(() => {
  jest.clearAllMocks();
});

describe('digitalKeyGenerateOtp', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const email = 'abc@test.com';
  const args = { email };

  it('should call method when generate OTP is called with correct parameters', async () => {
    await digitalKeyGenerateOtp(args, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.GENERATE_OTP,
      digitalKeyGenerateOtp,
      email,
      context
    );
  });

  it('should handle errors when generateOTP fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(digitalKeyGenerateOtp(args, context)).rejects.toThrow('Test error');
  });
});

describe('digitalKeyProvision', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const digitalkeyProvisionRequest = {
    otpCode: '123456',
    email: 'abc@gmail.com',
    bookingReference: 'AQR12345',
    reservationId: '654321'
  };
  const args = { digitalkeyProvisionRequest };

  it('should call method when digitalKeyProvision is called with correct parameters', async () => {
    await digitalKeyProvision(args, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.PASS_PROVISIONING_WITH_OTP,
      digitalKeyProvision,
      digitalkeyProvisionRequest,
      context
    );
  });

  it('should handle errors when digitalKeyProvision fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(digitalKeyProvision(args, context)).rejects.toThrow('Test error');
  });
});

describe('digitalKeyCheckIn', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const digitalKeyCheckInCriteria = {
    reservationId: 'AJK123456',
    hotelId: 'LONEUS'
  };

  it('should call the post function when checkIn is called with correct parameters', async () => {
    await digitalKeyCheckIn({ digitalKeyCheckInCriteria }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.CHECKIN,
      digitalKeyCheckIn,
      digitalKeyCheckInCriteria,
      context
    );
  });

  it('should handle errors gracefully when checkIn fails', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(digitalKeyCheckIn({ digitalKeyCheckInCriteria }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
