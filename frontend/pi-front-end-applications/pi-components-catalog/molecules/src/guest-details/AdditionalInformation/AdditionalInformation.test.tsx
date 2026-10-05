import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { FieldsType } from '@whitbread-eos/atoms/dist/components/Form/formTypes';
import { add } from 'date-fns';
import React from 'react';
import { useForm } from 'react-hook-form';

import { act, fireEvent, render } from '../../utils/test-utils';
import AdditionalFields from './AdditionalInformation.component';
import { handleNationalityChange } from './common';

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

jest.mock('next/router', () => ({
  useRouter() {
    return {
      query: { key: '' },
    };
  },
}));

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
          nationality: 'Austrian',
        },
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          countryName: 'United Kingdom (the)',
          dialingCode: '+44',
          flagSrc: '',
          passportRequired: true,
          nationality: 'British, UK',
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'D',
          countryName: 'Germany',
          dialingCode: '+49',
          flagSrc: '',
          passportRequired: true,
          nationality: 'German',
        },
        {
          countryCode: 'RO',
          countryCodeLegacy: 'RO',
          countryName: 'Romania',
          dialingCode: '+40',
          flagSrc: '',
          passportRequired: false,
          nationality: 'Romanian',
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

const mockCustomLocale = jest.fn();
jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

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

const mockComponentProps = {
  data: getCountriesData.data,
  isLoading: false,
  isError: false,
  error: null,
  isSuccess: true,
};

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div></div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: () => mockComponentProps,
  useFeatureSwitch: () => true,
}));

const Component = () => {
  const { control, getValues } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'additionalInformation',
    label: 'additionalInformation',
    testid: 'additionalInformation',
  };

  const props: any = {
    control,
    formField,
    getValues,
  };

  return <AdditionalFields {...props} />;
};
describe('Additional Information component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('Should render the Additional Information component', async () => {
    const { getByText, getByTestId, getByRole } = render(<Component />);

    const button = getByTestId('additionalInformation-CheckinButton');

    fireEvent.click(button);

    expect(getByTestId('additionalInformation-Nationality')).toBeInTheDocument();
    expect(getByText('precheckin.additionalfields.dateofbirth')).toBeInTheDocument();

    const scheduledDate = getByTestId('additionalInformation--SingleDatePicker');
    const sDate = add(new Date(), { years: -20 });

    await act(async () => {
      fireEvent.click(scheduledDate);
      fireEvent.change(scheduledDate, { target: { value: sDate } });
    });

    const nationalityInput = getByRole('combobox');
    await act(async () => {
      fireEvent.change(nationalityInput, {
        target: { value: ['Austria'] },
      });
    });
  });
});

describe('AdditionalInformation component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call handleNationalityChange when nationality changes', async () => {
    const { getByRole, getByTestId } = render(<Component />);
    const button = getByTestId('additionalInformation-CheckinButton');

    fireEvent.click(button);
    const nationalityInput = getByRole('combobox');

    const mockSetValue = jest.fn();
    const mockField = {
      onChange: jest.fn(),
    };

    const control = {
      setValue: mockSetValue,
      getValues: jest.fn(),
    };

    const handleSetValue = (field: string, value: string) => {
      control.setValue(field, value);
    };

    const fieldProp: any = { value: 'AT' };

    fireEvent.change(nationalityInput, {
      target: { value: 'AT' },
    });

    handleNationalityChange(fieldProp, mockField, 'passport', handleSetValue);

    expect(mockField.onChange).toHaveBeenCalledWith(fieldProp);
    expect(mockSetValue).toHaveBeenCalledWith('passport', '');
  });
});
