import '@testing-library/jest-dom';

import { registerSchema } from './form-data';

const mockedT = (key: string) => key;

describe('registerSchema', () => {
  const schema = registerSchema(mockedT);

  it('should validate a valid input', () => {
    const validData = {
      emailAddress: 'test@example.com',
      companyName: 'Valid Company',
      uniqueTaxpayerReference: '123456789',
      socialMediaType: { displayValue: 'Facebook', value: 'facebook' },
      socialMediaValue: 'facebook.com/valid',
      selectAddress: { displayValue: '123 Street', value: '123-street' },
    };

    expect(() => schema.parse(validData)).not.toThrow();
  });

  it('should throw an error for invalid email address', () => {
    const invalidData = {
      emailAddress: 'invalid-email',
      companyName: 'Valid Company',
    };

    expect(() => schema.parse(invalidData)).toThrow();
  });

  it('should throw an error for invalid company name', () => {
    const invalidData = {
      emailAddress: 'test@example.com',
      companyName: '!',
    };

    expect(() => schema.parse(invalidData)).toThrow();
  });

  it('should allow optional fields to be undefined', () => {
    const validData = {
      emailAddress: 'test@example.com',
      companyName: 'Valid Company',
    };

    expect(() => schema.parse(validData)).not.toThrow();
  });

  it('should validate socialMediaType if provided', () => {
    const invalidData = {
      emailAddress: 'test@example.com',
      companyName: 'Valid Company',
      socialMediaType: { displayValue: 123, value: 'facebook' },
    };

    expect(() => schema.parse(invalidData)).toThrow();
  });

  it('should validate selectAddress if provided', () => {
    const invalidData = {
      emailAddress: 'test@example.com',
      companyName: 'Valid Company',
      selectAddress: { displayValue: '123 Street', value: 123 },
    };

    expect(() => schema.parse(invalidData)).toThrow();
  });
});
