import '@testing-library/jest-dom';
import { FieldsType, FORM_FIELD_TYPES, FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { getAuthCookie, useAuthToken, useLocalStorage } from '@whitbread-eos/utils';
import { setGuestFormData } from '@whitbread-eos/utils/server';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import BackButton from './BackButton.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getAuthCookie: jest.fn().mockReturnValue(''),
  useAuthToken: jest.fn(() => ({ token: '', isAuth0Enabled: false, isLoading: false })),
  useLocalStorage: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    setGuestFormData: jest.fn(),
  };
});

const mockClick = jest.fn();
const mockHandleSetValue = jest.fn();
const mockHandleResetField = jest.fn();
const mockReset = jest.fn();
const mockSetFormDetails = jest.fn();
const mockGetValues = jest.fn(() => ({
  addressLine1: 'holborn',
  addressLine2: 'london',
  addressLine3: 'uk',
  addressLine4: 'uk',
  postcodeAddress: 'LSC 1AA',
  addressSelection: 'BUSINESS',
  cityName: 'london',
  companyName: 'whitbread',
  countryCode: 'UK',
  email: 'abc@whitbread.com',
  firstName: 'abc',
  lastName: 'xyz',
  landline: '1234567890',
  manualAddressToggle: 'manualAddress',
  phone: '1234567890',
  postalCode: 'LSC 1AA',
  passport: 'A1234567',
  nationality: { value: 'RO', label: 'Romanian' },
  consent: true,
  dateOfBirth: '2000-01-01',
  leadGuest: [
    {
      firstName: 'guest',
      passport: 'P7654321',
      nationality: { value: 'FR', label: 'French' },
      consent: true,
      dateOfBirth: '2001-02-02',
    },
  ],
}));
const formField: FieldsType = {
  type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
  name: 'backButton',
  label: 'backButton',
  props: {
    basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
    goBack: jest.fn(),
    defaultValues: {
      firstName: 'abc',
      title: 'Mr',
    },
  },
};

const props: FormDynamicFieldCompProps = {
  formField,
  field: { name: 'backButton', value: '', onChange: jest.fn(), onBlur: jest.fn() },
  handleSetValue: mockHandleSetValue,
  handleResetField: mockHandleResetField,
  getValues: mockGetValues,
  reset: mockReset,
};
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    back: mockClick,
  }),
}));

describe('<BackButton/>', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (setGuestFormData as jest.Mock).mockResolvedValue(true);
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
        updated: true,
        firstName: 'abc',
      },
      mockSetFormDetails,
    ]);
    (getAuthCookie as jest.Mock).mockReturnValue('');
    (useAuthToken as jest.Mock).mockReturnValue({
      token: '',
      isAuth0Enabled: false,
      isLoading: false,
    });
  });

  describe('auth0 sign-in', () => {
    const anonProps = (): FormDynamicFieldCompProps => ({
      ...props,
      formField: {
        ...formField,
        props: { ...formField.props, isRemovePIIDataFromLocalStorageEnabled: false },
      },
    });

    it('should not reset the form for an Auth0-authenticated user with no saved edits', () => {
      // Auth0 issues an access token and no legacy cookie. Reading the cookie directly made
      // this user look anonymous, so the form was restored from localStorage and blanked the
      // values UserProfile had just prefilled. Non-empty so `shouldReset` would be true if
      // the anonymous branch were taken.
      (useAuthToken as jest.Mock).mockReturnValue({
        token: 'an-auth0-access-token',
        isAuth0Enabled: true,
        isLoading: false,
      });
      (useLocalStorage as jest.Mock).mockReturnValue([
        {
          basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          firstName: 'stale-from-localstorage',
        },
        mockSetFormDetails,
      ]);

      render(<BackButton {...anonProps()} />);

      expect(mockReset).not.toHaveBeenCalled();
    });

    it('should wait for the Auth0 token before deciding the user is anonymous', () => {
      (useAuthToken as jest.Mock).mockReturnValue({
        token: '',
        isAuth0Enabled: true,
        isLoading: true,
      });
      (useLocalStorage as jest.Mock).mockReturnValue([
        { basketReferenceId: 'a-different-basket', firstName: '' },
        mockSetFormDetails,
      ]);

      render(<BackButton {...anonProps()} />);

      expect(mockReset).not.toHaveBeenCalled();
      expect(mockSetFormDetails).not.toHaveBeenCalled();
    });

    it('should still restore saved edits for an Auth0 user when formDetails.updated is set', () => {
      (useAuthToken as jest.Mock).mockReturnValue({
        token: 'an-auth0-access-token',
        isAuth0Enabled: true,
        isLoading: false,
      });
      (useLocalStorage as jest.Mock).mockReturnValue([
        {
          basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          updated: true,
          firstName: 'abc',
        },
        mockSetFormDetails,
      ]);

      render(<BackButton {...anonProps()} />);

      expect(mockReset).toHaveBeenCalledWith(expect.objectContaining({ firstName: 'abc' }));
    });
  });

  it('should render a <BackButton/> ', () => {
    const { getByTestId } = render(<BackButton {...props} />);
    const button = getByTestId('GuestDetails-BackToAncillariesButton');
    expect(button).toBeInTheDocument();
  });
  it('should render BackButton and should call goBack function on click', () => {
    const { getByTestId } = render(<BackButton {...props} />);
    const button = getByTestId('GuestDetails-BackToAncillariesButton');
    fireEvent.click(button);
    expect(formField?.props?.goBack).toHaveBeenCalled();
  });

  describe('isRemovePIIDataFromLocalStorageEnabled', () => {
    it('should save form data on Redis through API and go back when isRemovePIIDataFromLocalStorageEnabled is true', async () => {
      const formFieldWithPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: true,
          channel: 'PI',
        },
      };

      const propsWithPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithPIIRemoval,
      };

      const { getByTestId } = render(<BackButton {...propsWithPIIRemoval} />);
      const button = getByTestId('GuestDetails-BackToAncillariesButton');
      fireEvent.click(button);

      await waitFor(() => {
        expect(setGuestFormData).toHaveBeenCalledWith(
          'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          expect.objectContaining({
            addressLine1: 'holborn',
            addressLine2: 'london',
            addressLine3: 'uk',
            addressLine4: 'uk',
            addressSelection: 'BUSINESS',
            cityName: 'london',
            companyName: 'whitbread',
            consent: true,
            countryCode: 'UK',
            email: 'abc@whitbread.com',
            firstName: 'abc',
            landline: '1234567890',
            lastName: 'xyz',
            leadGuest: [{ firstName: 'guest' }],
            manualAddressToggle: 'manualAddress',
            phone: '1234567890',
            postalCode: 'LSC 1AA',
            postcodeAddress: 'LSC 1AA',
            basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          })
        );
      });

      expect(mockSetFormDetails).not.toHaveBeenCalled();
      expect(formField?.props?.goBack).toHaveBeenCalled();
    });

    it('should save form data on Redis through API when isRemovePIIDataFromLocalStorageEnabled is true', async () => {
      const formFieldWithPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: true,
        },
      };

      const propsWithPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithPIIRemoval,
      };

      const { getByTestId } = render(<BackButton {...propsWithPIIRemoval} />);
      const button = getByTestId('GuestDetails-BackToAncillariesButton');
      fireEvent.click(button);

      await waitFor(() => {
        expect(setGuestFormData).toHaveBeenCalledWith(
          'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          expect.any(Object)
        );
      });

      expect(formField?.props?.goBack).toHaveBeenCalled();
    });

    it('should not call setGuestFormData when isRemovePIIDataFromLocalStorageEnabled is false', async () => {
      const formFieldWithPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: false,
        },
      };

      const propsWithPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithPIIRemoval,
      };

      const { getByTestId } = render(<BackButton {...propsWithPIIRemoval} />);
      const button = getByTestId('GuestDetails-BackToAncillariesButton');
      fireEvent.click(button);

      await waitFor(() => {
        expect(setGuestFormData).not.toHaveBeenCalled();
      });

      expect(formField?.props?.goBack).toHaveBeenCalled();
    });

    it('should not restore form data when isRemovePIIDataFromLocalStorageEnabled is true', () => {
      const formFieldWithPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: true,
        },
      };

      const propsWithPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithPIIRemoval,
      };

      render(<BackButton {...propsWithPIIRemoval} />);

      expect(mockReset).not.toHaveBeenCalled();
    });

    it('should restore form data when isRemovePIIDataFromLocalStorageEnabled is false', () => {
      const formFieldWithoutPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: false,
        },
      };

      const propsWithoutPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithoutPIIRemoval,
      };

      const { getByTestId } = render(<BackButton {...propsWithoutPIIRemoval} />);
      const button = getByTestId('GuestDetails-BackToAncillariesButton');
      fireEvent.click(button);

      expect(mockReset).toHaveBeenCalledWith(expect.objectContaining({ firstName: 'abc' }));
      expect(setGuestFormData).not.toHaveBeenCalled();
    });
  });

  describe('sanitizePIIFields', () => {
    it('should keep only the desired fields in formData', async () => {
      const formFieldWithPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: true,
          channel: 'PI',
        },
      };

      const propsWithPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithPIIRemoval,
        getValues: jest.fn(() => ({
          firstName: 'John',
          lastName: 'Doe',
          email: 'john@example.com',
          passport: 'P1234567',
          nationality: 'UK',
          dateOfBirth: '1990-01-01',
          consent: true,
          additionalInformation: 'Some info',
          anonRfs: 'value',
          backButton: 'value',
          userProfile: 'value',
          leadGuest: [],
        })),
      };

      const { getByTestId } = render(<BackButton {...propsWithPIIRemoval} />);
      const button = getByTestId('GuestDetails-BackToAncillariesButton');
      fireEvent.click(button);

      await waitFor(() => {
        expect(setGuestFormData).toHaveBeenCalledWith(
          'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          expect.not.objectContaining({
            passport: expect.anything(),
            nationality: expect.anything(),
            dateOfBirth: expect.anything(),
            consent: expect.anything(),
            additionalInformation: expect.anything(),
            anonRfs: expect.anything(),
            backButton: expect.anything(),
            userProfile: expect.anything(),
          })
        );

        expect(setGuestFormData).toHaveBeenCalledWith(
          'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          expect.objectContaining({
            firstName: 'John',
            lastName: 'Doe',
            email: 'john@example.com',
            leadGuest: [],
            basketReferenceId: 'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          })
        );
      });
    });

    it('should handle null formData', async () => {
      const formFieldWithPIIRemoval: FieldsType = {
        ...formField,
        props: {
          ...formField.props,
          isRemovePIIDataFromLocalStorageEnabled: true,
          channel: 'PI',
        },
      };

      const propsWithPIIRemoval: FormDynamicFieldCompProps = {
        ...props,
        formField: formFieldWithPIIRemoval,
        getValues: jest.fn(() => null),
      };

      const { getByTestId } = render(<BackButton {...propsWithPIIRemoval} />);
      const button = getByTestId('GuestDetails-BackToAncillariesButton');
      fireEvent.click(button);

      await waitFor(() => {
        expect(setGuestFormData).toHaveBeenCalledWith(
          'AJK-abc12345-1a2b-3c4d-5e6f-123456789abc',
          null
        );
      });
    });
  });
});
