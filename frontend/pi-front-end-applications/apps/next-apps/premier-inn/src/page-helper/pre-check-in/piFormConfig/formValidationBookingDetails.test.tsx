import '@testing-library/jest-dom';
import { Form } from '@whitbread-eos/atoms';
import React from 'react';

import { render } from '../../../utils/test-utils';
import validateForm from './formValidationBookingDetails';
import { preCheckinBookingDetailsFormConfig } from './preCheckinFormConfig';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({}),
}));
const getFormState = jest.fn();
const onSubmit = jest.fn();

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

const t = (key: string) => {
  return key;
};

const mockedProps = {
  baseDataTestId: 'PreCheckInPage',
  currentLang: 'en',
  defaultValues: {
    bookingNumber: '',
    arrivalDate: '',
    lastName: '',
  },
};

const fields = [
  'input-bookingNumber',
  'PreCheckInPage-Form-ArrivalDate-SingleDatePicker',
  'input-surname',
];

const Component = ({
  countryCode,
  currentLang,
}: {
  countryCode?: string;
  currentLang?: 'en' | 'de';
}) => {
  const defaultValues: {
    [key: string]: string | number | boolean | object;
  } = { ...mockedProps.defaultValues };
  if (countryCode) defaultValues.countryCode = countryCode;
  return (
    <Form
      data-testid={'Form'}
      {...preCheckinBookingDetailsFormConfig({
        getFormState,
        defaultValues,
        onSubmit,
        baseDataTestId: mockedProps.baseDataTestId,
        currentLang: currentLang && mockedProps.currentLang,
        t,
        bookingReferenceError: false,
      })}
    />
  );
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Render Form', () => {
  it('should render Form with all components correctly for GB', () => {
    const { findByTestId } = render(<Component />);

    fields.forEach((field) => {
      const element = findByTestId(field);
      expect(element).toBeTruthy();
    });
  });

  it('should render Form with all components correctly for DE', () => {
    const { getByTestId } = render(<Component countryCode="DE" currentLang="de" />);

    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });
});

describe('Validate Form', () => {
  it('should validate the form with valid input validators', () => {
    const params = {
      t,
      currentLang: 'en',
    };

    const result = validateForm(params);

    expect(result.formValidationObject).toBeDefined();
    expect(result.formValidationSchemaBookingDetails).toBeDefined();
  });

  it('should validate all the fields', () => {
    const currentLang = 'de';
    const { defaultValues } = mockedProps;

    const params = {
      t,
      currentLang,
      defaultValues,
    };

    const result = validateForm(params);
    expect(result.formValidationObject.bookingNumber).toBeDefined();

    expect(
      result.formValidationSchemaBookingDetails.isValidSync({
        bookingNumber: 'AWM8159458',
        surname: 'pi',
        arrivalDate: 'Mon Feb 19 2024 13:28:21 GMT+0530 (India Standard Time)',
      })
    ).toBe(true);

    expect(
      result.formValidationSchemaBookingDetails.isValidSync({
        bookingNumber: '$#%',
        surname: 'pi 12&',
        arrivalDate: 'Mon Feb 21 2024 13:28:21 GMT+0530 (India Standard Time)',
      })
    ).toBe(false);
  });
});
