import '@testing-library/jest-dom';
import { FormWithAccordian } from '@whitbread-eos/atoms';
import React from 'react';

import { render } from '../../../utils/test-utils';
import { GroupBookingFormConfig } from './GroupBookingFormConfig';
import validateForm from './formValidation';

const useQueryResponse = {
  isSuccess: true,
  error: {
    request: {
      status: 200,
    },
    response: {
      status: {
        details: '',
      },
    },
  },
  data: [
    { code: 'ABETIR', title: 'Aberdare', brand: 'PI' },
    { code: 'ABECOC', title: 'Aberdeen (Anderson Drive)', brand: 'PI' },
  ],
};
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({}),
  useRestQueryRequest: jest.fn(() => useQueryResponse),
}));

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
  baseDataTestId: 'GroupBookingsPage',
  currentLang: 'en',
  defaultValues: {
    title: '',
    firstName: '',
    lastName: '',
    emailAddress: '',
    phoneName: '',
  },
  openNextAccordian: jest.fn(),
  handleAccordionToggle: jest.fn(),
  accordionIndex: 0,
  hotels: [
    { value: 'Aberdare', component: 'brand' },
    { value: 'Aberdeen (Anderson Drive)', component: 'brand' },
  ],
  islessThanMinCount: false,
};

const Component = () => {
  return (
    <FormWithAccordian
      data-testid={'Form'}
      {...GroupBookingFormConfig({
        getFormState,
        defaultValues: mockedProps.defaultValues,
        onSubmit,
        baseTestId: mockedProps.baseDataTestId,
        currentLang: mockedProps.currentLang,
        t,
        openNextAccordian: mockedProps.openNextAccordian,
        handleAccordionToggle: mockedProps.handleAccordionToggle,
        accordionIndex: mockedProps.accordionIndex,
        hotels: mockedProps.hotels,
        islessThanMinCount: mockedProps.islessThanMinCount,
        createGroupBookingLoading: false,
        createGroupBookingError: false,
      })}
    />
  );
};

describe('Render Form', () => {
  it('should render Contact details Form with all components correctly', async () => {
    const { findByTestId } = render(<Component />);
    expect(await findByTestId('GroupBookingsPage-Title')).toBeTruthy();
    expect(await findByTestId('GroupBookingsPage-firstName')).toBeTruthy();
    expect(await findByTestId('GroupBookingsPage-lastName')).toBeTruthy();
    expect(await findByTestId('GroupBookingsPage-emailAddress')).toBeTruthy();
    expect(await findByTestId('GroupBookingsPage-Mobile-phoneNumber')).toBeTruthy();
  });
});

describe('validateForm', () => {
  it('should validate the form with valid inputs', () => {
    const { currentLang } = mockedProps;
    const params = {
      t,
      currentLang,
    };
    const result = validateForm(params);
    expect(result.formValidationObject).toBeDefined();
    expect(result.formValidationSchema).toBeDefined();
  });

  it('should validate title', () => {
    const { defaultValues, currentLang } = mockedProps;

    const params = { t, currentLang, defaultValues };
    const result = validateForm(params);
    const schema = result.formValidationObject.title;

    const validTitle = 'Sir';
    expect(schema.isValidSync(validTitle)).toBe(true);
    const invalidTitle = '';
    expect(schema.isValidSync(invalidTitle)).toBe(false);
  });

  it('should validate firstName', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues };
    const result = validateForm(params);
    const schema = result.formValidationObject.firstName;

    const validFirstName = 'Bob';
    expect(schema.isValidSync(validFirstName)).toBe(true);
    const invalidFirstName = 'a';
    expect(schema.isValidSync(invalidFirstName)).toBe(false);
  });

  it('should validate lastName', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues };
    const result = validateForm(params);
    const schema = result.formValidationObject.lastName;

    const validLastName = 'le Bob';
    expect(schema.isValidSync(validLastName)).toBe(true);
    const invalidLastName = 'a';
    expect(schema.isValidSync(invalidLastName)).toBe(false);
  });

  it('should validate email', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues };
    const result = validateForm(params);
    const schema = result.formValidationObject.emailAddress;

    const validEmailAddress = 'bob@lebob.com';
    expect(schema.isValidSync(validEmailAddress)).toBe(true);
    const invalidEmailAddress = 'boblebob.com';
    expect(schema.isValidSync(invalidEmailAddress)).toBe(false);
  });

  it('should validate phone number', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues };
    const result = validateForm(params);
    const schema = result.formValidationObject.phoneNumber;

    const validPhoneNumber = '07527268912';
    expect(schema.isValidSync(validPhoneNumber)).toBe(true);
    const invalidPhoneNumber = '12ddff1df';
    expect(schema.isValidSync(invalidPhoneNumber)).toBe(false);
  });

  it('should NOT validate comments if isSchoolYouthEnabled checkbox is false', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues, isSchoolYouthEnabled: false };
    const result = validateForm(params);
    const schema = result.formValidationObject.comments;
    const validComments = '';
    expect(schema.isValidSync(validComments)).toBe(true);
  });

  it('SHOULD validate comments if isSchoolYouthEnabled checkbox is true', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues, isSchoolYouthEnabled: true };
    const result = validateForm(params);
    const schema = result.formValidationObject.comments;
    const validComments = '';
    expect(schema.isValidSync(validComments)).toBe(false);
  });

  it('SHOULD validate user entered comments - if isSchoolYouthEnabled checkbox is true', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues, isSchoolYouthEnabled: true };
    const result = validateForm(params);
    const schema = result.formValidationObject.comments;
    const validComments = 'some random comments by the user';
    expect(schema.isValidSync(validComments)).toBe(true);
  });

  it('SHOULD validate company name if hideCompanyName is false', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = {
      t,
      currentLang,
      defaultValues,
      isSchoolYouthEnabled: true,
      hideCompanyName: false,
    };
    const result = validateForm(params);
    const schema = result.formValidationObject.companyName;
    const companyName = 'company name';
    expect(schema.isValidSync(companyName)).toBe(true);
  });

  it('SHOULD validate company name if hideCompanyName is true', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = {
      t,
      currentLang,
      defaultValues,
      isSchoolYouthEnabled: true,
      hideCompanyName: true,
    };
    const result = validateForm(params);
    const schema = result.formValidationObject.companyName;
    const companyName = '';
    expect(schema.isValidSync(companyName)).toBe(true);
  });
  it('SHOULD validate date if selected', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = {
      t,
      currentLang,
      defaultValues,
    };
    const result = validateForm(params);
    const schema = result.formValidationObject.datepicker;
    const datepicker = [new Date(), new Date()];
    expect(schema.isValidSync(datepicker)).toBe(true);
  });
  it('SHOULD validate date if not selected', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = {
      t,
      currentLang,
      defaultValues,
    };
    const result = validateForm(params);
    const schema = result.formValidationObject.datepicker;
    const datepicker = [new Date(), null];
    expect(schema.isValidSync(datepicker)).toBe(false);
  });

  it('should validate reasonForVisit', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues };
    const result = validateForm(params);
    const schema = result.formValidationObject.reasonForVisit;

    const validReasonForVisit = 'Conference';
    expect(schema.isValidSync(validReasonForVisit)).toBe(true);
    const invalidReasonForVisit = '';
    expect(schema.isValidSync(invalidReasonForVisit)).toBe(false);
  });

  it('should validate reasonForVisitOther when hideReasonForVisit is false', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues, hideReasonForVisit: false };
    const result = validateForm(params);
    const schema = result.formValidationObject.reasonForVisitOther;

    const validReasonForVisitOther = 'Other reason';
    expect(schema.isValidSync(validReasonForVisitOther)).toBe(true);
    const invalidReasonForVisitOther = '';
    expect(schema.isValidSync(invalidReasonForVisitOther)).toBe(false);
  });

  it('should NOT validate reasonForVisitOther when hideReasonForVisit is true', () => {
    const { defaultValues, currentLang } = mockedProps;
    const params = { t, currentLang, defaultValues, hideReasonForVisit: true };
    const result = validateForm(params);
    const schema = result.formValidationObject.reasonForVisitOther;

    const validReasonForVisitOther = '';
    expect(schema.isValidSync(validReasonForVisitOther)).toBe(true);
  });
});
