import { validateFormParams } from '@whitbread-eos/api';

import validateForm from './formValidation';

const mockData = {
  reasonForStay: 'Business',
  title: 'Mr',
  firstName: 'Jack',
  lastName: 'Smith',
  email: 'jack@example.com',
  password: 'SecurePassword123',
  confirmPassword: 'SecurePassword123',
  phone: '1234567890',
  addressSelection: 'BUSINESS',
  addressLine1: '123 Main St',
  billing_addressLine1: '123 Main St',
  postalCode: '12345',
  dateOfBirth: '06/06/2000',
  companyName: 'Whitbread',
  bookingForSomeoneElse: true,
  billing_postalCode: '12345',
  billing_countryCode: 'DE',
  billing_cityName: 'London',
  billing_companyName: 'WHB',
  billingAddressCheckbox: true,
  billing_addressSelection: 'BUSINESS',
  cityName: 'London',
  countryCode: 'DE',
  leadGuest: [
    {
      stayInThisRoom: false,
      title: 'Mrs',
      firstName: 'Jenifer',
      lastName: 'Smith',
      email: 'jeni@g.com',
      passport: '123456789',
    },
  ],
};

const singleRoom = [{ roomStay: { adultsNumber: 1, childrenNumber: 0 } }];

const params: validateFormParams = {
  t: jest.fn((key) => key),
  bkndData: {
    hiData: undefined,
    rooms: singleRoom,
  },
  isGermanHotel: true,
  isRegisterSelected: true,
  isSingleRoomRedesignEnabled: false,
  isBookingForSomeoneElse: false,
  isAdditionalInformationEnabled: true,
  currentLang: 'en',
};

describe('validateForm', () => {
  it('should return the form validation schema as true', () => {
    const { formValidationObject, formValidationSchema } = validateForm(params);

    expect(formValidationObject).toBeDefined();
    expect(formValidationSchema.isValidSync(mockData)).toBe(true);
  });
  it('should return the form validation schema as false', () => {
    const { formValidationSchema } = validateForm(params);

    expect(formValidationSchema.isValidSync({ ...mockData, firstName: '' })).toBe(false);
  });

  it('validate if email is blank', () => {
    const { formValidationSchema } = validateForm(params);

    expect(formValidationSchema.isValidSync({ ...mockData, email: '' })).toBe(false);
  });
  it('validate if lead guest is blank', () => {
    const { formValidationSchema } = validateForm(params);

    expect(
      formValidationSchema.isValidSync({ ...mockData, leadGuest: [], bookingForSomeoneElse: false })
    ).toBe(true);
  });
  it('validate if billing_countryCode is GB', () => {
    const { formValidationSchema } = validateForm(params);

    expect(
      formValidationSchema.isValidSync({
        ...mockData,
        billing_countryCode: 'GB',
        billing_postalCode: 'RH6 0NP',
      })
    ).toBe(true);
    expect(
      formValidationSchema.isValidSync({
        ...mockData,
        billing_countryCode: 'GB',
        billing_postalCode: '',
      })
    ).toBe(false);
  });

  it('should validate phone and landline fields when isConsolidateMobileLandlineEnabled is false', () => {
    const { formValidationSchema } = validateForm({
      ...params,
      isConsolidateMobileLandlineEnabled: false,
    });

    // Both phone and landline empty: invalid
    const data = {
      ...mockData,
      phone: '',
      landline: '',
    };
    expect(formValidationSchema.isValidSync(data)).toBe(false);

    // Only phone present: valid
    data.phone = '07123456789';
    expect(formValidationSchema.isValidSync(data)).toBe(true);

    // Only landline present: valid
    data.phone = '';
    data.landline = '02012345678';
    expect(formValidationSchema.isValidSync(data)).toBe(true);
  });

  it('should require phone when isConsolidateMobileLandlineEnabled is true', () => {
    const { formValidationSchema } = validateForm({
      ...params,
      isConsolidateMobileLandlineEnabled: true,
    });
    const data = {
      ...mockData,
      phone: '',
    };
    expect(formValidationSchema.isValidSync(data)).toBe(false);

    data.phone = '07123456789';
    expect(formValidationSchema.isValidSync(data)).toBe(true);
  });
});
