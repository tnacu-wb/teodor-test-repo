import { registerSchema } from './reset-password-form.schema';

const mockedT = (key: string) => key;

describe('ResetPasswordFormSchema', () => {
  const schema = registerSchema(mockedT);

  it('should throw when min char number', () => {
    expect(() => schema.parse({ password: '' })).toThrow('reset.password.input.required');
  });

  it('should throw when do not contains A-Z', () => {
    expect(() => schema.parse({ password: 'abcd1234' })).toThrow('reset.password.input.error');
  });

  it('should throw when do not contains digits', () => {
    expect(() => schema.parse({ password: 'ABCDabcd' })).toThrow('reset.password.input.error');
  });

  it('should throw when do not contains a-z', () => {
    expect(() => schema.parse({ password: 'ABCD1234' })).toThrow('reset.password.input.error');
  });

  it('should throw when contains special characters', () => {
    expect(() => schema.parse({ password: 'ABCabc123()' })).toThrow('reset.password.input.error');
  });

  it('should throw when confirm password do not match', () => {
    expect(() => schema.parse({ password: 'ABCabc123', confirmPassword: '12345678' })).toThrow(
      'reset.passwords.notMatch'
    );
  });

  it('should not throw', () => {
    expect(() =>
      schema.parse({ password: 'ABCabc123', confirmPassword: 'ABCabc123' })
    ).not.toThrow();
  });
});
