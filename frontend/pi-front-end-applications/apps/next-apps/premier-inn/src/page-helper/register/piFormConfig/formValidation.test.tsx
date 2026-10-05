import '@testing-library/jest-dom';
import { Form } from '@whitbread-eos/atoms';
import React from 'react';

import { render } from '../../../utils/test-utils';
import validateForm from './formValidation';
import { registerDetailsFormConfig } from './registerDetailsFormConfig';

jest.mock('../common/uniqueValidation', () => jest.fn());
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({}),
}));
// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));
const getFormState = jest.fn();
const onSubmit = jest.fn();

const t = (key: string) => {
  return key;
};

const mockedProps = {
  baseDataTestId: 'RegisterPage',
  currentLang: 'en',
  defaultValues: {
    title: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    companyName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    manualAddressToggle: '',
    cityName: '',
    postcodeAddress: '',
    addressSelection: '',
    countryCode: 'GB',
    acceptFutureMailing: true,
  },
};

const Component = () => {
  return (
    <Form
      data-testid={'Form'}
      {...registerDetailsFormConfig({
        getFormState,
        defaultValues: mockedProps.defaultValues,
        onSubmit,
        baseDataTestId: mockedProps.baseDataTestId,
        currentLang: mockedProps.currentLang,
        t,
        isLocationRequired: false,
        setIsLocationRequired: jest.fn(),
      })}
    />
  );
};

const ComponentWithDE = () => {
  return (
    <Form
      data-testid={'Form'}
      {...registerDetailsFormConfig({
        getFormState,
        defaultValues: { ...mockedProps.defaultValues, countryCode: 'DE' },
        onSubmit,
        baseDataTestId: mockedProps.baseDataTestId,
        currentLang: 'de',
        t,
        isLocationRequired: true,
        setIsLocationRequired: jest.fn(),
      })}
    />
  );
};

describe('Render Form', () => {
  it('should render Form with all components correctly for GB', async () => {
    const { findByTestId } = render(<Component />);
    expect(await findByTestId('RegisterPage-Title')).toBeTruthy();
    expect(await findByTestId('RegisterPage-FirstName')).toBeTruthy();
    expect(await findByTestId('RegisterPage-LastName')).toBeTruthy();
    expect(await findByTestId('RegisterPage-Email')).toBeTruthy();
    expect(await findByTestId('RegisterPage-Password')).toBeTruthy();
    expect(await findByTestId('RegisterPage-ConfirmPassword')).toBeTruthy();
    expect(await findByTestId('RegisterPage-Mobile-phoneNumber')).toBeTruthy();
    expect(await findByTestId('RegisterPage-PostcodeAddress')).toBeTruthy();
  });

  it('should render Form with all components correctly for DE', async () => {
    const { queryByTestId } = render(<ComponentWithDE />);
    expect(queryByTestId('RegisterPage-Title')).toBeTruthy();
    expect(queryByTestId('RegisterPage-FirstName')).toBeTruthy();
    expect(queryByTestId('RegisterPage-LastName')).toBeTruthy();
    expect(queryByTestId('RegisterPage-Email')).toBeTruthy();
    expect(queryByTestId('RegisterPage-Password')).toBeTruthy();
    expect(queryByTestId('RegisterPage-ConfirmPassword')).toBeTruthy();
    expect(queryByTestId('RegisterPage-Mobile-phoneNumber')).toBeTruthy();
  });
});

describe('validateForm', () => {
  it('should validate the form with valid inputs', () => {
    const params = {
      t,
      currentLang: 'en',
    };

    const result = validateForm(params);

    expect(result.formValidationObject).toBeDefined();
    expect(result.formValidationSchema).toBeDefined();
  });

  it('should add city validation when currentLang is not "en"', () => {
    const params = {
      t,
      currentLang: 'de',
    };

    const result = validateForm(params);
    expect(result.formValidationObject.cityName).toBeDefined();
  });

  it('should validate the postcode when country code is GB', () => {
    const { currentLang, defaultValues } = mockedProps;

    const params = { t, currentLang, defaultValues };

    const result = validateForm(params);
    const schema = result.formValidationObject.postalCode;

    const validPostcode = 'SW1A 1AA';
    expect(schema.isValidSync(validPostcode)).toBe(true);
    const invalidPostcode = '1234567890123456789012345678901234567890';
    expect(schema.isValidSync(invalidPostcode)).toBe(false);
    const emptyPostcode = '';
    expect(schema.isValidSync(emptyPostcode)).toBe(false);
  });

  it('should validate the postcode when country code is DE', () => {
    const currentLang = 'de';
    const { defaultValues } = mockedProps;

    const params = { t, currentLang, defaultValues };

    const result = validateForm(params);
    const schema = result.formValidationObject.postalCode;

    const validPostcode = '12345';
    expect(schema.isValidSync(validPostcode)).toBe(true);
    const invalidPostcode = '1234567890123456789012345678901234567890';
    expect(schema.isValidSync(invalidPostcode)).toBe(false);
  });

  it('should validate companyName', () => {
    const currentLang = 'de';
    const { defaultValues } = mockedProps;

    const params = {
      t,
      currentLang,
      defaultValues,
    };

    const result = validateForm(params);
    expect(result.formValidationObject.companyName).toBeDefined();

    expect(
      result.formValidationSchema.isValidSync({
        title: 'Mr',
        firstName: 'First',
        lastName: 'Last',
        email: 'email@mail.com',
        phone: '43252345235',
        acceptTermsConditions: true,
        companyName: 'Company name',
        addressLine1: 'address line1',
        addressLine2: 'address line2',
        addressLine3: 'address line3',
        addressLine4: 'address line4',
        postalCode: 'SW1A 1AA',
        manualAddressToggle: true,
        cityName: 'asdf',
        postcodeAddress: 'postcode address',
        addressSelection: 'BUSINESS',
        countryCode: 'GB',
        acceptFutureMailing: true,
        password: 'possword123!@#',
        confirmPassword: 'possword123!@#',
      })
    ).toBe(true);
  });
});
