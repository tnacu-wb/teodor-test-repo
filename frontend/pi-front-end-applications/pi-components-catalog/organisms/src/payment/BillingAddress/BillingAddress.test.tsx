import '@testing-library/jest-dom';
import TotalCostCard from '@whitbread-eos/molecules/dist/payment/TotalCostCard/TotalCostCard.component';

import { fireEvent, render } from '../../utils/test-utils';
import BillingAddress from './BillingAddress.component';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQueryRequest: (...args: any[]) => {
    const queryKey = args[0];
    if (Array.isArray(queryKey) && queryKey[0] === 'GetCountries') {
      return mockCountriesResponse;
    }
    return mockResponse;
  },
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    invalidateQueries: jest.fn(),
  }),
}));

const mockCountriesResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: { message: '' },
  data: {
    countries: {
      countries: [
        {
          countryCode: 'GB',
          countryCodeLegacy: 'UK',
          countryName: 'United Kingdom',
          passportRequired: false,
          dialingCode: '+44',
          flagSrc: '',
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'DE',
          countryName: 'Germany',
          passportRequired: false,
          dialingCode: '+49',
          flagSrc: '',
        },
      ],
    },
  },
};

const mockResponse = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  error: { message: '' },
  data: {
    partialAddress: [
      {
        id: 'test',
        addressText: 'Chris 2 Bit Fix Ltd, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY',
      },
    ],
    formattedAddress: {
      companyName: null as string | null,
      addressLine1: 'Flat 2, Belgravia Court' as string,
      addressLine2: '33 Ebury Street' as string | null,
      addressLine3: '' as string | null,
      addressLine4: 'LONDON' as string | null,
      postalCode: 'SW1W 0NY' as string,
      cityName: 'LONDON',
      postcodeAddress: '',
      addressSelection: 'HOME',
      billingAddressSelection: 'DifferentAddress',
      countryCode: 'GB' as string | null,
      manualAddressToggle: 'manualAddress',
    },
  },
};

const mockedBillingAddress = {
  horizontalRadioButtons: false,
  isAmendPage: false,
  currentBillingAddress: {
    companyName: '',
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    cityName: '',
    postcodeAddress: '',
    addressSelection: 'HOME',
    billingAddressSelection: 'CurrentAddress',
    countryCode: 'GB',
    manualAddressToggle: '',
  },
  continueToNextStep: jest.fn(),
  t: (key: string) => {
    switch (key) {
      case 'billingAddress.current':
        return 'billingAddress.current';
      case 'billingAddress.different':
        return 'billingAddress.different';
      case 'booking.enterManuallAddress':
        return 'booking.enterManuallAddress';
      default:
        return 'default';
    }
  },
  currentLang: 'en',
};

const totalCostCardProps = {
  isAmendPage: false,
  hotelName: 'Manchester Old Trafford',
  hotelId: 'MANOLD',
  rateCode: 'A',
  ratePlan: {
    name: 'FLEXRATE',
    totalCost: {
      amount: '345',
      currency: 'EUR',
    },
  },
  selectedPaymentDetail: {
    type: '',
    order: 0,
    enabled: false,
  },
  isError: false,
  error: {
    message: undefined,
  },
  isLoading: false,
  data: {
    termsAndConditions: {
      rate: '',
      text: '',
    },
  },
  isBillingAddressDisplayed: false,
  errorMessagePayment: undefined as string | undefined,
  continueToNextStep: jest.fn(),
};

describe('BillingAddress', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the form correctly', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    expect(getByTestId('Payment-BillingAddressSelection-SameAddress')).toBeInTheDocument();
    expect(getByTestId('Payment-BillingAddressSelection-DifferentAddress')).toBeInTheDocument();
  });

  it('should render different billing address form when Different Address radio button is selected', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(getByTestId('Payment-PostcodeAddress')).toBeInTheDocument();
    expect(getByTestId('input-postcodeAddress-label')).toBeInTheDocument();
    expect(getByTestId('input-postcodeAddress')).toBeInTheDocument();
    expect(getByTestId('PostcodeAddress-FindAddressBtn')).toBeInTheDocument();
    expect(getByTestId('Payment-ManualAddressToggle')).toBeInTheDocument();
  });

  it('should render manual address form when Enter manual address link is clicked', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-ManualAddressToggle'));
    expect(getByTestId('Payment-addressSelection-PersonalAddress')).toBeInTheDocument();
    expect(getByTestId('Payment-addressSelection-CompanyAddress')).toBeInTheDocument();
  });

  it('should render home address fields correctly when Home radio button is selected', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-ManualAddressToggle'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine1')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine2')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine3')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine4')
    ).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-PersonalAddress-PostalCode')).toBeInTheDocument();
    expect(getByTestId('Payment-CountrySelector')).toBeInTheDocument();
  });

  it('should render company address fields correctly when Business radio button is selected', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-ManualAddressToggle'));
    fireEvent.click(getByTestId('Payment-addressSelection-CompanyAddress'));
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-CompanyName')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine1')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine2')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine3')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine4')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-PostalCode')).toBeInTheDocument();
    expect(getByTestId('Payment-CountrySelector')).toBeInTheDocument();
  });

  it('should render city field and not render address line 4 when language is not `en`', async () => {
    const { getByTestId, queryByTestId, findByTestId } = render(
      <BillingAddress {...{ ...mockedBillingAddress, currentLang: 'DE' }} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(
      await findByTestId('Payment-AddressSelection-PersonalAddress-CityName')
    ).toBeInTheDocument();
    expect(
      queryByTestId('Payment-AddressSelection-PersonalAddress-AddressLine4')
    ).not.toBeInTheDocument();
  });

  it('should click the manual address form link', () => {
    const { getByTestId } = render(
      <BillingAddress {...{ ...mockedBillingAddress, isAmendPage: true, currentLang: 'DE' }} />
    );

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));
    fireEvent.click(getByTestId('Payment-ManualAddressToggle'));

    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine1')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine2')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine3')
    ).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-PersonalAddress-PostalCode')).toBeInTheDocument();
    expect(getByTestId('Payment-CountrySelector')).toBeInTheDocument();
  });

  it('should open the manual address form when there is no postal code on submit', () => {
    mockResponse.data.formattedAddress.addressLine1 = '';

    const { getByTestId } = render(
      <>
        <BillingAddress {...{ ...mockedBillingAddress, currentLang: 'DE' }} />
        <TotalCostCard {...totalCostCardProps} />
      </>
    );

    const submitButton = getByTestId('submitButton');

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));
    fireEvent.click(submitButton);

    expect(totalCostCardProps.continueToNextStep).toHaveBeenCalled();

    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine1')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine2')
    ).toBeInTheDocument();
    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine3')
    ).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-PersonalAddress-PostalCode')).toBeInTheDocument();
    expect(getByTestId('Payment-CountrySelector')).toBeInTheDocument();
  });

  it('should call onSubmit without address field value', () => {
    const { getByTestId } = render(
      <>
        <BillingAddress {...mockedBillingAddress} />
        <TotalCostCard {...totalCostCardProps} />
      </>
    );

    const submitButton = getByTestId('submitButton');

    fireEvent.click(submitButton);

    expect(totalCostCardProps.continueToNextStep).toHaveBeenCalled();
  });

  it('should pass the horizontalRadioButtons props to true', () => {
    mockedBillingAddress.horizontalRadioButtons = true;

    render(
      <>
        <BillingAddress {...mockedBillingAddress} />
        <TotalCostCard {...totalCostCardProps} />
      </>
    );
    expect(mockedBillingAddress.horizontalRadioButtons).toBe(true);
  });

  it('should render the billing address title', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    expect(getByTestId('Payment-BillingAddress-Title')).toBeInTheDocument();
  });

  it('should render the current address label inside the CurrentAddress radio option', () => {
    const { getByTestId } = render(
      <BillingAddress
        {...{
          ...mockedBillingAddress,
          currentBillingAddress: {
            ...mockedBillingAddress.currentBillingAddress,
            postalCode: 'SW1W 0NY',
            addressLine1: 'Belgravia Court',
          },
        }}
      />
    );
    const label = getByTestId('Payment-BillingAddress-UseCurrentAdress-Label');
    expect(label).toBeInTheDocument();
    expect(label).toHaveTextContent('SW1W 0NY Belgravia Court');
  });

  it('should render the different billing address title when DifferentAddress is selected', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(getByTestId('Payment-BillingAddress-YourDifferentAdress-Title')).toBeInTheDocument();
  });

  it('should expand the manual address form after DifferentAddress is selected on English locale', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(getByTestId('Payment-ManualAddressToggle')).toBeInTheDocument();
    expect(getByTestId('Payment-addressSelection-PersonalAddress')).toBeInTheDocument();
  });

  it('should hide the "Enter address manually" link after it is clicked', () => {
    const { getByTestId, queryByText } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-ManualAddressToggle'));
    expect(queryByText('booking.enterManuallAddress')).not.toBeInTheDocument();
  });

  it('should not show the "Enter address manually" link for German locale (manual form expands automatically)', () => {
    const { getByTestId, queryByText } = render(
      <BillingAddress {...{ ...mockedBillingAddress, currentLang: 'de' }} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(queryByText('booking.enterManuallAddress')).not.toBeInTheDocument();
  });

  it('should not render the postcode lookup field for German locale', () => {
    const { getByTestId, queryByTestId } = render(
      <BillingAddress {...{ ...mockedBillingAddress, currentLang: 'de' }} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(queryByTestId('Payment-PostcodeAddress')).not.toBeInTheDocument();
  });

  it('should render the postcode lookup field for English locale', () => {
    const { getByTestId } = render(<BillingAddress {...mockedBillingAddress} />);
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(getByTestId('Payment-PostcodeAddress')).toBeInTheDocument();
  });

  it('should hide manual address fields when switching back to CurrentAddress', () => {
    const { getByTestId, queryByTestId } = render(<BillingAddress {...mockedBillingAddress} />);

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-ManualAddressToggle'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));

    expect(
      getByTestId('Payment-AddressSelection-PersonalAddress-AddressLine1')
    ).toBeInTheDocument();

    // Switch back to CurrentAddress — all DifferentAddress related fields should unmount
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-SameAddress'));

    expect(
      queryByTestId('Payment-AddressSelection-PersonalAddress-AddressLine1')
    ).not.toBeInTheDocument();
    expect(queryByTestId('Payment-ManualAddressToggle')).not.toBeInTheDocument();
  });

  it('should not auto-expand manual address form when billingAddressSelection is not DifferentAddress (guard condition)', () => {
    const { getByTestId, queryByText, queryByTestId } = render(
      <>
        <BillingAddress {...{ ...mockedBillingAddress, currentLang: 'de' }} />
        <TotalCostCard {...totalCostCardProps} />
      </>
    );

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));
    fireEvent.click(getByTestId('submitButton'));

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-SameAddress'));

    expect(queryByText('booking.enterManuallAddress')).not.toBeInTheDocument();
    expect(
      queryByTestId('Payment-AddressSelection-PersonalAddress-AddressLine1')
    ).not.toBeInTheDocument();
  });
});
