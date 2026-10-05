import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES, type FormDynamicFieldCompProps } from '@whitbread-eos/atoms';

import { render } from '../../utils/test-utils';
import CountriesDropdown from './CountriesDropdown';

const onBlur = jest.fn();
const onChange = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
  }),
}));

const useQueryResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  error: {},
  data: {
    countries: [
      {
        countryCode: 'A',
        countryCodeISO: 'AT',
        countryLegend: 'Austria',
        passportRequired: true,
        dialingCode: '+43',
        flagImg: '/content/dam/global/flags/Austria.png',
      },
      {
        countryCode: 'AFG',
        countryCodeISO: 'AF',
        countryLegend: 'Afghanistan',
        passportRequired: true,
        dialingCode: '+93',
        flagImg: '/content/dam/global/flags/Afghanistan.png',
      },
    ],
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => useQueryResponse,
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const sharedProps = {
  formField: {
    type: FORM_FIELD_TYPES.DROPDOWN,
    label: 'Country',
    dropdownOptions: [
      {
        id: 'uk',
        label: 'United Kingdom',
      },
      {
        id: 'germany',
        label: 'Germany',
      },
      {
        id: 'france',
        label: 'France',
      },
    ],
  },
  field: { name: 'CountrySelector', value: '', onChange, onBlur },
  handleResetField: jest.fn(),
  getValues: jest.fn(),
};

const Component = () => {
  const formField: FieldsType = {
    ...sharedProps.formField,
    name: 'CountrySelector',
    props: {
      isCountrySelectorFilterableEnabled: false,
    },
  };

  const props: FormDynamicFieldCompProps = {
    formField,
    field: { name: 'CountrySelector', value: '', onChange: onChange, onBlur: onBlur },
    handleResetField: jest.fn(),
    getValues: jest.fn(),
  };
  return <CountriesDropdown {...props} />;
};

const FilterableComponent = () => {
  const formField: FieldsType = {
    ...sharedProps.formField,
    name: 'CountrySelectorFilterable',
    props: {
      isCountrySelectorFilterableEnabled: true,
    },
  };

  const props: FormDynamicFieldCompProps = {
    formField,
    field: { name: 'CountryFilterableSelector', value: '', onChange: onChange, onBlur: onBlur },
    handleResetField: jest.fn(),
    getValues: jest.fn(),
  };
  return <CountriesDropdown {...props} />;
};

describe('Default Countries Dropdown ', () => {
  it('should render the Country Dropdown button', () => {
    const { getByTestId, queryByTestId } = render(<Component />);
    expect(getByTestId('DropdownComp-Country-menuButton')).toBeInTheDocument();
    expect(queryByTestId('CountrySelectorFilterable-countrySelector')).not.toBeInTheDocument();
  });
});

describe('Filterable Countries Selector', () => {
  it('should render the Country Dropdown button', () => {
    const { getByTestId, queryByTestId } = render(<FilterableComponent />);
    expect(getByTestId('CountrySelectorFilterable-countrySelector')).toBeInTheDocument();
    expect(queryByTestId('DropdownComp-Country-menuButton')).not.toBeInTheDocument();
  });
});
