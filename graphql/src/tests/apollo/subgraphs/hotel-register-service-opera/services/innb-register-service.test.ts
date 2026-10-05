import { post } from '../../../../../apollo/client/rest-client';
import {
  innBRegistrationStepOne,
  innBRegistrationStepTwo
} from '../../../../../apollo/subgraphs/hotel-register-service-opera/services/innb-register-service';
import { endpoints } from '../../../../../apollo/subgraphs/hotel-register-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');
beforeAll(() => {
  jest.clearAllMocks();
});

describe('innBRegistrationStepOne', () => {
  const context = {};
  const innBRegistrationStepOneRequest = {
    email: 'aa@aa.com',
    companyName: 'company1',
    language: 'en'
  };

  it('should call method when innBRegistrationStepOne is called with correct parameters', async () => {
    await innBRegistrationStepOne(
      { innBRegistrationStepOneRequest: innBRegistrationStepOneRequest },
      context
    );

    expect(post).toHaveBeenCalledWith(
      endpoints.REGISTRATION_STEP_ONE,
      innBRegistrationStepOne,
      innBRegistrationStepOneRequest,
      context
    );
  });

  it('should handle errors when innBRegistrationStepOne fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);
    await expect(
      innBRegistrationStepOne(
        { innBRegistrationStepOneRequest: innBRegistrationStepOneRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });
});

describe('innBRegistrationStepTwo', () => {
  const context = {};
  const innBRegistrationStepTwoRequest = {
    title: 'Mr.',
    firstName: 'John',
    lastName: 'Doe',
    phoneNumber: '1234567890',
    password: 'securePassword123',
    activationKey: 'activation-123'
  };

  it('should call method when innBRegistrationStepTwo is called with correct parameters', async () => {
    await innBRegistrationStepTwo({ innBRegistrationStepTwoRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.REGISTRATION_STEP_TWO,
      innBRegistrationStepTwo,
      innBRegistrationStepTwoRequest,
      context
    );
  });

  it('should handle errors when innBRegistrationStepTwo fails correctly', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValue(error);

    await expect(
      innBRegistrationStepTwo({ innBRegistrationStepTwoRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
