import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render, userEvent } from '../utils/test-utils';
import PreCheckInPage from './PreCheckInForm';

const getCountriesData = {
  data: {
    countries: {
      countries: [
        {
          countryCode: 'AT',
          countryCodeLegacy: 'A',
          countryName: 'Austria',
          dialingCode: '+43',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          countryName: 'United Kingdom (the)',
          dialingCode: '+44',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'D',
          countryName: 'Germany',
          dialingCode: '+49',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'RO',
          countryCodeLegacy: 'RO',
          countryName: 'Romania',
          dialingCode: '+40',
          flagSrc: '',
          passportRequired: false,
        },
      ],
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error country selection',
  },
};

function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetCountries':
        return getCountriesData;

      default:
        return {};
    }
  }
}

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: () => ({}),
  useFeatureSwitch: () => true,
}));

const mockUseRouter = jest.fn().mockReturnValue({
  query: {},
});
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

const ComponentWithDynamicField = () => {
  const {
    control,
    formState: { errors },
  } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'fieldName',
    label: 'Field Name',
    testid: 'PreCheckInPage-Form',
    props: { setIsLocationRequired: jest.fn(), dependents: [], setSubmitType: jest.fn() },
  };

  const props = {
    control,
    formField,
    errors,
    getValues() {
      return {
        firstName: 'John',
        lastName: 'Doe',
        adultsNumber: 2,
        childrenNumber: 1,
      };
    },
  };

  return <PreCheckInPage {...props} />;
};

describe('PreRegister PI ', () => {
  it('should render Pre register page skeleton', async () => {
    const { getByTestId } = render(<ComponentWithDynamicField />);

    const saveButton = getByTestId('submit-reg-form');
    const completeButton = getByTestId('reg-form-submit-btn');

    expect(saveButton).toBeInTheDocument();
    expect(completeButton).toBeInTheDocument();

    userEvent.click(saveButton);
  });
});
