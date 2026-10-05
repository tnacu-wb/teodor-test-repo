import * as yup from 'yup';

import billingAddressFormValidation from './billingAddressFormValidation';

describe('billingAddressFormValidation', () => {
  const t = (key: string) => key;
  let currentLang = 'GB';

  it('should return a yup object with the correct shape', () => {
    const result = billingAddressFormValidation({ t, currentLang });
    expect(result).toBeInstanceOf(yup.ObjectSchema);
    expect(result.fields).toHaveProperty('billingAddressSelection');
    expect(result.fields).toHaveProperty('addressLine1');
    expect(result.fields).toHaveProperty('addressLine2');
    expect(result.fields).toHaveProperty('addressLine3');
    expect(result.fields).toHaveProperty('addressLine4');
    expect(result.fields).toHaveProperty('postalCode');
    expect(result.fields).toHaveProperty('cityName');
    expect(result.fields).toHaveProperty('companyName');
  });
  it('should return a yup object with the correct shape in de', () => {
    currentLang = 'D';
    const result = billingAddressFormValidation({ t, currentLang });
    expect(result).toBeInstanceOf(yup.ObjectSchema);
    expect(result.fields).toHaveProperty('billingAddressSelection');
    expect(result.fields).toHaveProperty('addressLine1');
    expect(result.fields).toHaveProperty('addressLine2');
    expect(result.fields).toHaveProperty('addressLine3');
    expect(result.fields).toHaveProperty('addressLine4');
    expect(result.fields).toHaveProperty('postalCode');
    expect(result.fields).toHaveProperty('cityName');
    expect(result.fields).toHaveProperty('companyName');
  });
});
