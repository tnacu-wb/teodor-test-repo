import { FORM_FIELD_TYPES } from '@whitbread-eos/atoms';
import { FieldsType } from '@whitbread-eos/atoms/dist/components/Form/formTypes';
import React from 'react';
import { useForm } from 'react-hook-form';

import { render } from '../../utils/test-utils';
import LeadGuestFields from './LeadGuestAdditionalFields';

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

const Component = ({ type, label, name }: { type: string; label: string; name: string }) => {
  const { control } = useForm();

  const formField: FieldsType = {
    type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
    name: 'additionalInformation',
    label: 'additionalInformation',
    testid: 'additionalInformation',
  };

  const props: any = {
    control,
    formField,
    testId: `leadGuest-${name}`,
    type,
    label,
    name,
  };

  return <LeadGuestFields {...props} />;
};

describe('Additional fields are displayed on the guest details page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });
  const data = {
    data: [],
    isLoading: false,
    isError: false,
    error: null,
    isSuccess: true,
  };
  jest.mock('@whitbread-eos/utils', () => ({
    ...jest.requireActual('@whitbread-eos/utils'),
    useQueryRequest: () => data,
  }));
  it('Should render the Lead guest passport component', async () => {
    const { getByTestId, getByText } = render(
      <Component type="input" label="passport" name="passport" />
    );
    expect(getByTestId('additionalInformation-leadGuest-passport')).toBeInTheDocument();
    expect(getByText('precheckin.details.passport')).toBeInTheDocument();
  });
  it('Should render the Lead guest nationality component', async () => {
    const { getByTestId } = render(
      <Component type="autoComplete" label="nationality" name="nationality" />
    );

    expect(getByTestId('additionalInformation-leadGuest-nationality')).toBeInTheDocument();
  });
  it('Should render the Lead guest date of birth component', async () => {
    const { getByTestId, getByText } = render(
      <Component type="datePicker" label="dob" name="dob" />
    );

    expect(getByTestId('additionalInformation-leadGuest-dob')).toBeInTheDocument();
    expect(getByText('precheckin.additionalfields.dob')).toBeInTheDocument();
  });
});
