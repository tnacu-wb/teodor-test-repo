import '@testing-library/jest-dom';
import * as formatters from '@whitbread-eos/utils';

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
}));

const mockSetBillingAddress = jest.fn();

const companyProfileMock = {
  name: 'Whitbread',
  address: {
    addressLine1: '120 Holborn',
    addressLine2: '',
    addressLine3: '',
    addressLine4: 'London',
    cityName: '',
    country: 'GB',
    postalCode: 'EC1N 2TD',
  },
};

const mockedBillingAddress = {
  currentBillingAddress: {
    addressLine1: '',
    addressLine2: '',
    addressLine3: '',
    addressLine4: '',
    postalCode: '',
    countryCode: '',
    companyName: '',
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

describe('BillingAddress', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.spyOn(formatters, 'formatAssetsUrl').mockReturnValue('url');
  });

  it('should render the form correctly', () => {
    const { getByTestId } = render(
      <BillingAddress {...mockedBillingAddress} setBillingAddress={mockSetBillingAddress} />
    );
    expect(getByTestId('Payment-BillingAddressSelection-SameAddress')).toBeInTheDocument();
    expect(getByTestId('Payment-BillingAddressSelection-DifferentAddress')).toBeInTheDocument();
  });

  it('should render different billing address form when Different Address radio button is selected', () => {
    const { getByTestId } = render(
      <BillingAddress {...mockedBillingAddress} setBillingAddress={mockSetBillingAddress} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    expect(getByTestId('Payment-PostcodeAddress')).toBeInTheDocument();
    expect(getByTestId('input-postcodeAddress-label')).toBeInTheDocument();
    expect(getByTestId('input-postcodeAddress')).toBeInTheDocument();
    expect(getByTestId('PostcodeAddress-FindAddressBtn')).toBeInTheDocument();
    expect(getByTestId('Payment-ManualAddressToggle')).toBeInTheDocument();
  });

  it('should render manual address form when Enter manual address link is clicked', () => {
    const { getByTestId, getByText } = render(
      <BillingAddress {...mockedBillingAddress} setBillingAddress={mockSetBillingAddress} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByText('booking.enterManuallAddress'));
    expect(getByTestId('Payment-addressSelection-PersonalAddress')).toBeInTheDocument();
    expect(getByTestId('Payment-addressSelection-CompanyAddress')).toBeInTheDocument();
  });

  it('should render home address fields correctly when Home radio button is selected', () => {
    const { getByTestId, getByText } = render(
      <BillingAddress {...mockedBillingAddress} setBillingAddress={mockSetBillingAddress} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByText('booking.enterManuallAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));
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
    const { getByTestId, getByText } = render(
      <BillingAddress {...mockedBillingAddress} setBillingAddress={mockSetBillingAddress} />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByText('booking.enterManuallAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-CompanyAddress'));
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-CompanyName')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine1')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine2')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine3')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-AddressLine4')).toBeInTheDocument();
    expect(getByTestId('Payment-AddressSelection-CompanyAddress-PostalCode')).toBeInTheDocument();
    expect(getByTestId('Payment-CountrySelector')).toBeInTheDocument();
  });

  it('should render city field and not render address line 4 when language is not `en`', () => {
    const { getByTestId, getByText, queryByTestId } = render(
      <BillingAddress
        {...{ ...mockedBillingAddress, currentLang: 'de' }}
        setBillingAddress={mockSetBillingAddress}
      />
    );
    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByText('booking.enterManuallAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-PersonalAddress'));
    expect(getByTestId('Payment-AddressSelection-PersonalAddress-CityName')).toBeInTheDocument();
    expect(
      queryByTestId('Payment-AddressSelection-PersonalAddress-AddressLine4')
    ).not.toBeInTheDocument();
  });

  it('should pre-populate form with DE company details and address', () => {
    companyProfileMock.name = 'BMW';
    companyProfileMock.address.addressLine1 = 'Frankfurter Ring 35';
    companyProfileMock.address.addressLine2 = '';
    companyProfileMock.address.addressLine3 = '';
    companyProfileMock.address.addressLine4 = 'München';
    companyProfileMock.address.postalCode = '80807';
    companyProfileMock.address.country = 'DE';
    const { getByTestId } = render(
      <BillingAddress
        {...mockedBillingAddress}
        setBillingAddress={mockSetBillingAddress}
        companyProfile={companyProfileMock}
        currentLang="de"
      />
    );

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-CompanyAddress'));
    const companyName = getByTestId('input-companyName');
    const address1 = getByTestId('input-addressLine1');
    const address2 = getByTestId('input-addressLine2');
    const address3 = getByTestId('input-addressLine3');
    const cityName = getByTestId('input-cityName');
    const postCode = getByTestId('input-postalCode');

    expect(companyName).toHaveValue('BMW');
    expect(address1).toHaveValue('Frankfurter Ring 35');
    expect(address2).toHaveValue('');
    expect(address3).toHaveValue('');
    expect(cityName).toHaveValue('München');
    expect(postCode).toHaveValue('80807');
  });

  it('should NOT pre-populate form', () => {
    const { getByTestId, getByText } = render(
      <BillingAddress
        {...mockedBillingAddress}
        setBillingAddress={mockSetBillingAddress}
        currentLang="de"
      />
    );

    fireEvent.click(getByTestId('Payment-BillingAddressSelection-DifferentAddress'));
    fireEvent.click(getByText('booking.enterManuallAddress'));
    fireEvent.click(getByTestId('Payment-addressSelection-CompanyAddress'));
    const companyName = getByTestId('input-companyName');
    const address1 = getByTestId('input-addressLine1');
    const address2 = getByTestId('input-addressLine2');
    const address3 = getByTestId('input-addressLine3');
    const cityName = getByTestId('input-cityName');
    const postCode = getByTestId('input-postalCode');

    expect(companyName).toHaveValue('');
    expect(address1).toHaveValue('');
    expect(address2).toHaveValue('');
    expect(address3).toHaveValue('');
    expect(cityName).toHaveValue('');
    expect(postCode).toHaveValue('');
  });
});
