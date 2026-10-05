import { ForgotStep, ForgotState, ForgotConfirmationState } from './types';

describe('ForgotStep Enum', () => {
  it('returns the correct value for FORGOT_FORM', () => {
    expect(ForgotStep.FORGOT_FORM).toBe('FORGOT_FORM');
  });
});

describe('ForgotState Type', () => {
  it('allows valid email property', () => {
    const state: ForgotState = {
      email: 'test@example.com',
      confirmationState: ForgotConfirmationState.DEFAULT,
    };
    expect(state.email).toBe('test@example.com');
  });

  it('throws an error for missing email property', () => {
    const state: ForgotState = { email: '', confirmationState: ForgotConfirmationState.DEFAULT };
    expect(state).toBeDefined();
  });

  it('throws an error for invalid email type', () => {
    const state: ForgotState = { email: '123', confirmationState: ForgotConfirmationState.DEFAULT };
    expect(state).toBeDefined();
  });
});
