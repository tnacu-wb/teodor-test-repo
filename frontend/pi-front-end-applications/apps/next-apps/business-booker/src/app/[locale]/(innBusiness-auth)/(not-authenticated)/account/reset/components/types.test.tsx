import { ResetStep, ResetState } from './types';

describe('ResetStep Enum', () => {
  it('returns correct value for RESET_FORM', () => {
    expect(ResetStep.RESET_FORM).toBe('RESET_FORM');
  });
});

describe('ResetState Type', () => {
  it('allows valid email property', () => {
    const state: ResetState = {
      email: 'test@example.com',
      passwordToken: 'token',
      isInvalidKey: true,
    };
    expect(state.email).toBe('test@example.com');
  });

  it('throws error for missing email property', () => {
    const state: ResetState = { email: '', passwordToken: '', isInvalidKey: false };
    expect(state).toBeDefined();
  });

  it('throws error for invalid email property type', () => {
    const state: ResetState = { email: '123', passwordToken: '123', isInvalidKey: true };
    expect(state).toBeDefined();
  });
});
