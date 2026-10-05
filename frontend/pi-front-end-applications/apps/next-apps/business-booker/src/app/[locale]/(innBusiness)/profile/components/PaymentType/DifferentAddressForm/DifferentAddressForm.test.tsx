import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { AddressType } from '@whitbread-eos/api';

import { CompanyAddress } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddress';

import DifferentAddressForm from './DifferentAddressForm';

jest.mock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress', () => ({
  CompanyAddress: jest.fn(() => <div data-testid="mock-company-address" />),
}));

jest.mock('~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress', () => ({
  TypeOfAddress: jest.fn(() => <div data-testid="mock-type-of-address" />),
}));

jest.mock('~components/innBusiness/forms/CompanyDetailsForm/CompanyName', () => ({
  CompanyName: jest.fn(() => <div data-testid="mock-company-name" />),
}));

describe('DifferentAddressForm', () => {
  const mockProps = {
    icons: {},
    addressFormRef: { current: null },
    companyNameFormRef: { current: null },
    addressTypeFormRef: { current: null },
    onAddressSubmit: jest.fn(),
    onAddressTypeSubmit: jest.fn(),
    onCompanyNameSubmit: jest.fn(),
    onAddressTypeChange: jest.fn(),
    addressType: AddressType.Home,
    companyName: '',
    language: 'en' as const,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render all components for business address type', () => {
    render(<DifferentAddressForm {...mockProps} addressType={AddressType.Business} />);

    expect(screen.getByTestId('mock-type-of-address')).toBeInTheDocument();
    expect(screen.getByTestId('mock-company-name')).toBeInTheDocument();
    expect(screen.getByTestId('mock-company-address')).toBeInTheDocument();
  });

  it('should not show CompanyName component for home address type', () => {
    render(<DifferentAddressForm {...mockProps} addressType={AddressType.Home} />);

    expect(screen.getByTestId('mock-type-of-address')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-company-name')).not.toBeInTheDocument();
    expect(screen.getByTestId('mock-company-address')).toBeInTheDocument();
  });

  it('should handle address data transformation correctly', () => {
    const profileDetails = {
      contactDetail: {
        address: {
          line1: 'Test Street 1',
          line2: 'Test Area',
          line3: 'Test District',
          line4: 'Test City',
          line5: 'Test Region',
          postCode: '12345',
          countryCode: 'D',
        },
      },
    };

    render(<DifferentAddressForm {...mockProps} profileDetails={profileDetails} />);

    const mockCalls = (CompanyAddress as jest.Mock).mock.calls[0][0];

    expect(mockCalls.addressData).toEqual({
      addressLine1: 'Test Street 1',
      addressLine2: 'Test Area',
      addressLine3: 'Test District',
      addressLine4: 'Test City',
      addressLine5: 'Test Region',
      postCode: '12345',
      country: 'DE',
    });
  });

  it('should call onAddressTypeChange when TypeOfAddress triggers change', () => {
    const onAddressTypeChange = jest.fn();
    render(<DifferentAddressForm {...mockProps} onAddressTypeChange={onAddressTypeChange} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress')
        .TypeOfAddress as jest.Mock
    ).mock.calls[0][0];
    mockCalls.onAddressTypeChange(AddressType.Business);
    expect(onAddressTypeChange).toHaveBeenCalledWith(AddressType.Business);
  });

  it('should call onAddressTypeSubmit when TypeOfAddress triggers submit', () => {
    const onAddressTypeSubmit = jest.fn();
    render(<DifferentAddressForm {...mockProps} onAddressTypeSubmit={onAddressTypeSubmit} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress')
        .TypeOfAddress as jest.Mock
    ).mock.calls[0][0];
    mockCalls.onSubmit({ type: 'Business' });
    expect(onAddressTypeSubmit).toHaveBeenCalledWith({ type: 'Business' });
  });

  it('should call onCompanyNameSubmit when CompanyName triggers submit', () => {
    const onCompanyNameSubmit = jest.fn();
    render(
      <DifferentAddressForm
        {...mockProps}
        addressType={AddressType.Business}
        onCompanyNameSubmit={onCompanyNameSubmit}
      />
    );
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyDetailsForm/CompanyName')
        .CompanyName as jest.Mock
    ).mock.calls[0][0];
    mockCalls.onSubmit({ name: 'Test Company' });
    expect(onCompanyNameSubmit).toHaveBeenCalledWith({ name: 'Test Company' });
  });

  it('should call onAddressSubmit when CompanyAddress triggers submit', () => {
    const onAddressSubmit = jest.fn();
    render(<DifferentAddressForm {...mockProps} onAddressSubmit={onAddressSubmit} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress')
        .CompanyAddress as jest.Mock
    ).mock.calls[0][0];
    mockCalls.onSubmit({ addressLine1: 'A', postCode: 'B' });
    expect(onAddressSubmit).toHaveBeenCalledWith({ addressLine1: 'A', postCode: 'B' });
  });

  it('should pass postalCode prop to CompanyAddress', () => {
    render(<DifferentAddressForm {...mockProps} postalCode="W1A 1AA" />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress')
        .CompanyAddress as jest.Mock
    ).mock.calls[0][0];
    expect(mockCalls.postalCode).toBe('W1A 1AA');
  });

  it('should use default language if not provided', () => {
    render(<DifferentAddressForm {...mockProps} language={undefined} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress')
        .CompanyAddress as jest.Mock
    ).mock.calls[0][0];
    expect(mockCalls.language).toBe('en');
  });

  it('should handle missing profileDetails gracefully', () => {
    render(<DifferentAddressForm {...mockProps} profileDetails={undefined} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress')
        .CompanyAddress as jest.Mock
    ).mock.calls[0][0];
    expect(mockCalls.addressData).toBeUndefined();
  });

  it('should handle missing address in profileDetails gracefully', () => {
    render(<DifferentAddressForm {...mockProps} profileDetails={{ contactDetail: {} }} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress')
        .CompanyAddress as jest.Mock
    ).mock.calls[0][0];
    expect(mockCalls.addressData).toBeUndefined();
  });

  it('should transform legacy country code DE to short country DE', () => {
    const profileDetails = {
      contactDetail: {
        address: {
          line1: 'Test',
          countryCode: 'D',
        },
      },
    };
    render(<DifferentAddressForm {...mockProps} profileDetails={profileDetails} />);
    const mockCalls = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/CompanyAddress')
        .CompanyAddress as jest.Mock
    ).mock.calls[0][0];
    expect(mockCalls.addressData.country).toBe('DE');
  });

  it('should pass refs to TypeOfAddress and CompanyName', () => {
    render(<DifferentAddressForm {...mockProps} addressType={AddressType.Business} />);
    const typeOfAddressProps = (
      jest.requireMock('~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress')
        .TypeOfAddress as jest.Mock
    ).mock.calls[0][0];
    const companyNameProps = (
      jest.requireMock('~components/innBusiness/forms/CompanyDetailsForm/CompanyName')
        .CompanyName as jest.Mock
    ).mock.calls[0][0];
    expect(typeOfAddressProps.formRef).toBeDefined();
    expect(companyNameProps.formRef).toBeDefined();
  });
});
