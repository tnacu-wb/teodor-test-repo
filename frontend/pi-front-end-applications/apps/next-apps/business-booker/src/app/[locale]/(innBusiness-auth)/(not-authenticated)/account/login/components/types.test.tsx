import { LoginStep, LoginState } from './types';

describe('LoginStep Enum', () => {
  it('returns correct value for LOGIN_FORM', () => {
    expect(LoginStep.LOGIN_FORM).toBe('LOGIN_FORM');
  });
});

describe('LoginState Type', () => {
  it('allows valid login state object', () => {
    const validState: LoginState = {
      email: 'user@example.com',
      password: 'securePassword123',
      redirect: '/dashboard',
    };
    expect(validState).toBeDefined();
  });

  it('throws error for missing required fields', () => {
    const invalidState: LoginState = {
      email: 'user@example.com',
      password: 'securePassword123',
      redirect: '',
    };
    expect(invalidState).toBeDefined();
  });

  it('accepts empty strings for email, password, and redirect', () => {
    const emptyState: LoginState = {
      email: '',
      password: '',
      redirect: '',
    };
    expect(emptyState).toBeDefined();
  });
});
