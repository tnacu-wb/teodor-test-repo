import { get, post } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/company-employee-service-opera/services/base-service';
import {
  approveRejectEmployee,
  getInnBusinessActivationDetails,
  getInnBusinessActivationDetailsV2,
  resendActivationEmail
} from '../../../../../apollo/subgraphs/company-employee-service-opera/services/innbusiness-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('resendActivationEmail', () => {
  const headers = { 'Content-Type': 'application/json' };
  const context = {};

  const sendActivationRequest = {
    email: 'test@email.com',
    companyName: 'Test Company',
    language: 'en'
  };

  it('should call post function with correct parameters when resendActivationEmail is called', async () => {
    await resendActivationEmail({ sendActivationRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.RESEND_ACTIVATION_EMAIL,
      resendActivationEmail,
      {
        email: 'test@email.com',
        companyName: 'Test Company',
        language: 'en'
      },
      context
    );
  });

  it('should handle errors gracefully when resendActivationEmail throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(resendActivationEmail({ sendActivationRequest }, headers)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('approveRejectEmployee', () => {
  const headers = { 'Content-Type': 'application/json' };
  const context = {};

  const approveRejectRequest = {
    email: 'test@email.com',
    accessLevel: 'BOOKER',
    approved: true,
    language: 'en'
  };

  it('should call post function with correct parameters when approveRejectEmployee is called', async () => {
    const approveRejectEmployeeEndPoint = {
      endpoint: '/v1/company-employee-service/innbusiness/travelManager/approvereject',
      flowCode: 'DIGITAL_CMP_020',
      axiosClient: expect.any(Function)
    };
    await approveRejectEmployee({ approveRejectRequest }, context);

    expect(post).toHaveBeenCalledWith(
      approveRejectEmployeeEndPoint,
      approveRejectEmployee,
      expect.objectContaining({
        email: 'test@email.com',
        accessLevel: 'BOOKER',
        approved: true,
        language: 'en'
      }),
      context
    );
  });

  it('should handle errors gracefully when approveRejectEmployee throws an error', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(approveRejectEmployee({ approveRejectRequest }, headers)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getInnBusinessActivationDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const activationKey = 'activation-123';
  const context = {};

  it('should call get function with correct parameters when getActivationDetails is called', async () => {
    const serviceEndpoint = {
      ...endpoints.GET_INN_BUSINESS_ACTIVATION_DETAILS,
      endpoint: '/v1/company-employee-service/innbusiness/employees/activation-details'
    };

    await getInnBusinessActivationDetails({ activationKey }, context);

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getInnBusinessActivationDetails,
      expect.objectContaining({
        'activation-key': 'activation-123'
      }),
      context
    );
  });

  it('should handle errors gracefully when getActivationDetails throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getInnBusinessActivationDetails({ activationKey }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getInnBusinessActivationDetailsV2', () => {
  const headers = { 'Content-Type': 'application/json' };
  const activationKey = 'activation-123';
  const context = {};

  it('should call get function with correct parameters when getActivationDetails is called', async () => {
    const serviceEndpoint = {
      ...endpoints.GET_INN_BUSINESS_ACTIVATION_DETAILS,
      endpoint: '/v1/company-employee-service/innbusiness/employees/activation-details'
    };

    await getInnBusinessActivationDetailsV2({ activationKey }, context);

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      getInnBusinessActivationDetailsV2,
      expect.objectContaining({
        'activation-key': 'activation-123'
      }),
      context
    );
  });

  it('should handle errors gracefully when getActivationDetails throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getInnBusinessActivationDetailsV2({ activationKey }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
