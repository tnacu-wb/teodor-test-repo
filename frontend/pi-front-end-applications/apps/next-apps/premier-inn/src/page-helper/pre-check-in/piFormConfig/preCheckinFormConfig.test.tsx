import '@testing-library/jest-dom';
import { Form } from '@whitbread-eos/atoms';
import React from 'react';

import { render } from '../../../utils/test-utils';
import { SUBMIT_TYPE } from '../common';
import { preCheckinFormConfig } from './preCheckinFormConfig';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({}),
}));

const mockUseRouter = jest.fn().mockReturnValue({
  query: {},
});
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
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

const dependents = [
  { id: '0', label: 0 },
  { id: '1', label: 1 },
  { id: '2', label: 2 },
  { id: '3', label: 3 },
];

const setIsLocationRequired = jest.fn();

const mockedProps = {
  baseDataTestId: 'PreCheckInPage',
  currentLang: 'en',
  defaultValues: {
    firstName: '',
    lastName: '',
    address: '',
    postalCode: '',
    city: '',
    country: '',
    dateOfBirth: '',
    dependent: '',
  },
};

const fields = [
  'PreCheckInPage-Form-FirstName',
  'PreCheckInPage-Form-Surname',
  'PreCheckInPage-Form-Address',
  'PreCheckInPage-Form-PostalCode',
  'PreCheckInPage-Form-City',
  'PreCheckInPage-Form-Country',
  'PreCheckInPage-Form-DateOfBirth',
  'PreCheckInPage-Form-Nationality',
  'PreCheckInPage-Form-Dependents',
];

const Component = ({
  countryCode,
  currentLang,
  submitType,
}: {
  countryCode?: string;
  currentLang?: 'en' | 'de';
  submitType?: SUBMIT_TYPE;
}) => {
  const defaultValues: {
    [key: string]: string | number | boolean | object;
  } = { ...mockedProps.defaultValues };
  if (countryCode) defaultValues.countryCode = countryCode;
  return (
    <Form
      data-testid={'Form'}
      {...preCheckinFormConfig({
        getFormState,
        defaultValues,
        onSubmit,
        baseDataTestId: mockedProps.baseDataTestId,
        currentLang: currentLang && mockedProps.currentLang,
        t,
        dependents,
        setIsLocationRequired,
        setSubmitType: jest.fn(),
        submitType: submitType || SUBMIT_TYPE.SAVE,
      })}
    />
  );
};

describe('Render Form', () => {
  it('should render Form with all components correctly for GB', () => {
    const { getByTestId } = render(<Component />);

    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });
  it('should render Form with all components correctly for DE', () => {
    const { getByTestId } = render(<Component countryCode="DE" currentLang="de" />);

    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });
  it('should render Form with all components correctly for DE', () => {
    const { getByTestId } = render(
      <Component countryCode="DE" currentLang="de" submitType={SUBMIT_TYPE.SUBMIT} />
    );

    fields.forEach((field) => expect(getByTestId(field)).toBeTruthy());
  });
});
