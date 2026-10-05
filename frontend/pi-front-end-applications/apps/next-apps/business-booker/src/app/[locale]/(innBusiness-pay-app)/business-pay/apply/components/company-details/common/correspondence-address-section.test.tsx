import '@testing-library/jest-dom/extend-expect';
import { render, waitFor } from '@testing-library/react';
import { Language } from '@whitbread-eos/api';

import { CorrespondenceAddressSection } from './correspondence-address-section';

const mockProps = {
  showCorrespondenceButton: true,
  showCompanyCorrespondenceAddress: true,
  handleAddCorrespondenceAddress: jest.fn(),
  handleCompanyCorrespondenceAddress: jest.fn(),
  correspondenceAddressFormRef: { current: null },
  icons: {},
  buttonStyle: 'button-style',
  buttonIconStyle: 'button-icon-style',
  baseDataTestId: 'CorrespondenceAddressSection',
  nameStyle: 'name-style',
  t: (key: string) => key,
  language: 'en' as Language,
  onOpen: jest.fn(),
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountriesList: () => {
      return;
    },
    addressSchema: () => {
      return {
        parseAsync: jest.fn(),
      };
    },
    getVariant: () => {
      return 'variantName.fieldName';
    },
    findError: serverUtils.findError,
  };
});

jest.mock('react-hook-form', () => ({
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    watch: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
  }),
  useForm: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    watch: jest.fn(),
    setValue: jest.fn(),
    getValues: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('CorrespondenceAddressSection', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders AddCorrespondenceButton when showCorrespondenceButton is true', async () => {
    const { getByTestId } = render(<CorrespondenceAddressSection {...mockProps} />);

    const button = getByTestId(`${mockProps.baseDataTestId}-button-add-correspondence`);

    await waitFor(async () => {
      expect(button).toBeInTheDocument();
    });
  });
});
