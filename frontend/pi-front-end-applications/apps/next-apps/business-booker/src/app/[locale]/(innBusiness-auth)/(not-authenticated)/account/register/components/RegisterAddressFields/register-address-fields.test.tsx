import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';
import { useForm, FormProvider } from 'react-hook-form';

import RegisterAddressFields from './register-address-fields';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  return {
    getPostCodeAddresses: jest.fn(),
    cn: jest.fn(),
    useTranslation: jest.fn(() => ({
      t: (str: string) => str,
    })),
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCountriesList: () => {
      return [];
    },
    getVariant: () => {
      return 'variantName.fieldName';
    },
    findError: serverUtils.findError,
  };
});

const renderWithFormProvider = (ui: React.ReactElement) => {
  const Wrapper: React.FC<React.PropsWithChildren> = ({ children }) => {
    const methods = useForm();
    return <FormProvider {...methods}>{children}</FormProvider>;
  };
  return render(ui, { wrapper: Wrapper });
};

const mockGetPostCodeAddresses = jest.fn();

describe('RegisterAddressFields', () => {
  const icons = {
    'icon.notification.error': 'error-icon-url',
    'icon.chevron.down': 'chevron-icon-url',
  };
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders postcode lookup fields by default', () => {
    renderWithFormProvider(
      <RegisterAddressFields baseDataTestId="RegisterPage" icons={icons} language="en" />
    );

    const elements = screen.getAllByText(/or/i);
    expect(elements.length).toBeGreaterThan(0);
    expect(
      screen.getByRole('button', { name: /auth.signup.accountCreation.manualAddress.link/i })
    ).toBeInTheDocument();
  });

  it('switches to manual address entry when "enter address manually" is clicked', () => {
    renderWithFormProvider(<RegisterAddressFields icons={icons} language="en" />);

    fireEvent.mouseDown(
      screen.getByRole('button', { name: /auth.signup.accountCreation.manualAddress.link/i })
    );

    expect(
      screen.getByText(/auth.signup.accountCreation.manualAddress.title/i)
    ).toBeInTheDocument();
    expect(
      screen.getByRole('button', { name: /auth.signup.accountCreation.postcodeLookup.link/i })
    ).toBeInTheDocument();
  });

  it('switches back to postcode lookup when "Return to postcode lookup" is clicked', () => {
    renderWithFormProvider(<RegisterAddressFields icons={icons} language="en" />);

    fireEvent.mouseDown(
      screen.getByRole('button', { name: /auth.signup.accountCreation.manualAddress.link/i })
    );
    fireEvent.click(
      screen.getByRole('button', { name: /auth.signup.accountCreation.postcodeLookup.link/i })
    );

    expect(
      screen.getByRole('button', { name: /auth.signup.accountCreation.manualAddress.link/i })
    ).toBeInTheDocument();
  });

  it('displays an error message for invalid postcode', async () => {
    mockGetPostCodeAddresses.mockRejectedValueOnce(null);

    renderWithFormProvider(<RegisterAddressFields icons={icons} language="en" />);

    fireEvent.click(screen.getByTestId('RegisterForm-findAddressButton'));

    await waitFor(() => {
      expect(
        screen.getByText('users.userMgmt.employee.add.companyAddress.error.invalidPostcode')
      ).toBeInTheDocument();
    });
  });

  it('updates social media placeholder when social media type changes', () => {
    renderWithFormProvider(
      <RegisterAddressFields icons={icons} language="en" showSocialMedia={true} />
    );

    const manualFieldsButton = screen.getByRole('button', {
      name: /auth.signup.accountCreation.manualAddress.link/i,
    });
    fireEvent.mouseDown(manualFieldsButton);

    const selectField = screen.getByTestId('socialMediaType-IB-Form-Select-Button');
    fireEvent.click(selectField);
    fireEvent.click(screen.getByTestId('socialMediaType-twitter-Option'));
    expect(screen.getByPlaceholderText(/Account Name/i)).toBeInTheDocument();

    fireEvent.click(selectField);
    fireEvent.click(screen.getByTestId('socialMediaType-other-Option'));
    expect(screen.getByPlaceholderText(/Other company profile/i)).toBeInTheDocument();

    fireEvent.click(selectField);
    fireEvent.click(screen.getByTestId('socialMediaType-website-Option'));
    expect(screen.getByPlaceholderText(/URL/i)).toBeInTheDocument();
  });

  it('hide social media media section when showSocialMedia is false', () => {
    renderWithFormProvider(
      <RegisterAddressFields icons={icons} language="en" showSocialMedia={false} />
    );

    const manualFieldsButton = screen.getByRole('button', {
      name: /auth.signup.accountCreation.manualAddress.link/i,
    });
    fireEvent.mouseDown(manualFieldsButton);

    const selectField = screen.queryByTestId('socialMediaType-IB-Form-Select-Button');
    expect(selectField).toBeNull();
  });

  it('renders uniqueTaxpayerReference input in manual address mode', async () => {
    renderWithFormProvider(
      <RegisterAddressFields
        icons={{
          'icon.notification.error': 'error-icon-url',
          'icon.chevron.down': 'chevron-icon-url',
        }}
        language="en"
      />
    );
    fireEvent.mouseDown(
      screen.getByRole('button', { name: /auth.signup.accountCreation.manualAddress.link/i })
    );
    expect(screen.getByPlaceholderText(/taxpayerReference.placeholder/i)).toBeInTheDocument();
  });

  it('sets correct placeholder for social media value input', async () => {
    renderWithFormProvider(
      <RegisterAddressFields
        icons={{
          'icon.notification.error': 'error-icon-url',
          'icon.chevron.down': 'chevron-icon-url',
        }}
        language="en"
        showSocialMedia={true}
      />
    );
    fireEvent.mouseDown(
      screen.getByRole('button', { name: /auth.signup.accountCreation.manualAddress.link/i })
    );
    fireEvent.click(screen.getByTestId('socialMediaType-IB-Form-Select-Button'));
    fireEvent.click(screen.getByTestId('socialMediaType-facebook-Option'));
    expect(screen.getByPlaceholderText(/Account Name/i)).toBeInTheDocument();
  });

  it('shows manual address fields by default for German language', () => {
    renderWithFormProvider(
      <RegisterAddressFields
        icons={{
          'icon.notification.error': 'error-icon-url',
          'icon.chevron.down': 'chevron-icon-url',
        }}
        language="de"
      />
    );
    expect(
      screen.getByText(/auth.signup.accountCreation.manualAddress.title/i)
    ).toBeInTheDocument();
  });

  it('switches to manual address when postcode field is focused and manual link is pressed', () => {
    renderWithFormProvider(
      <RegisterAddressFields baseDataTestId="RegisterPage" icons={icons} language="en" />
    );

    const postCodeInput = screen.getByPlaceholderText(
      /userMgmt.employee.add.companyAddress.postcode/i
    );
    fireEvent.focus(postCodeInput);

    const manualAddressButton = screen.getByRole('button', {
      name: /auth.signup.accountCreation.manualAddress.link/i,
    });
    fireEvent.mouseDown(manualAddressButton);

    expect(
      screen.getByText(/auth.signup.accountCreation.manualAddress.title/i)
    ).toBeInTheDocument();
  });

  it('switches to manual address via keyboard activation (Enter key)', () => {
    renderWithFormProvider(
      <RegisterAddressFields baseDataTestId="RegisterPage" icons={icons} language="en" />
    );

    const manualAddressButton = screen.getByRole('button', {
      name: /auth.signup.accountCreation.manualAddress.link/i,
    });
    fireEvent.keyDown(manualAddressButton, { key: 'Enter', code: 'Enter' });

    expect(
      screen.getByText(/auth.signup.accountCreation.manualAddress.title/i)
    ).toBeInTheDocument();
  });

  it('switches to manual address via keyboard activation (Space key)', () => {
    renderWithFormProvider(
      <RegisterAddressFields baseDataTestId="RegisterPage" icons={icons} language="en" />
    );

    const manualAddressButton = screen.getByRole('button', {
      name: /auth.signup.accountCreation.manualAddress.link/i,
    });
    fireEvent.keyDown(manualAddressButton, { key: ' ', code: 'Space' });

    expect(
      screen.getByText(/auth.signup.accountCreation.manualAddress.title/i)
    ).toBeInTheDocument();
  });
});
