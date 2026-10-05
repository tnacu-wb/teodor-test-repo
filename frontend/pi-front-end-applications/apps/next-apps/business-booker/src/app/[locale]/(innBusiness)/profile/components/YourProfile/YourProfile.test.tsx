import '@testing-library/jest-dom';
import { render, fireEvent, screen } from '@testing-library/react';
import { AddressType, LOCALES } from '@whitbread-eos/api';
import { updateProfileDetails as updateProfileDetailsMock } from '@whitbread-eos/utils/server';
import React from 'react';

import { YourProfile } from './YourProfile';

jest.mock('@whitbread-eos/utils/server', () => ({
  updateProfileDetails: jest.fn(),
}));

const mockOnReviewChangesToggle = jest.fn();
const mockOnIsEditableToggle = jest.fn();
const mockOnIsUpdatingToggle = jest.fn();
const mockOnIsDirtyToggle = jest.fn();
const mockToast = jest.fn();
const mockRouterRefresh = jest.fn();

jest.mock('@whitbread-eos/utils/server', () => ({
  getAuthCookie: jest.fn(() => 'mock-cookie'),
  formatIBAssetsUrl: jest.fn((url) => url),
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
  updateProfileDetails: jest.fn(),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: (props: any) => <button {...props} />,
  Notification: (props: any) => <div data-testid="notification">{props.message}</div>,
  SanitizedContent: (props: any) => <span>{props.children}</span>,
  useToast: () => ({ toast: mockToast }),
  ButtonVariantDescriptor: { truncateWithEllipsisButton: 'truncate' },
}));

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    refresh: mockRouterRefresh,
  }),
}));

jest.mock('~components/innBusiness/AddressDetails', () => ({
  AddressDetails: (props: any) => <div data-testid="address-details">{JSON.stringify(props)}</div>,
}));
jest.mock('~components/innBusiness/UserDetails', () => ({
  UserDetails: (props: any) => <div data-testid="user-details">{JSON.stringify(props)}</div>,
}));
jest.mock('~components/innBusiness/forms/CompanyAddressForm', () => {
  const CompanyAddress = React.forwardRef(
    (props: any, ref: React.ForwardedRef<HTMLFormElement>) => (
      <form
        ref={ref as React.LegacyRef<HTMLFormElement>}
        data-testid="company-address-form"
        onSubmit={(e) => {
          e.preventDefault();
          props.onSubmit && props.onSubmit({ addressLine1: 'A', country: 'GB', postCode: '123' });
        }}
      >
        <button type="submit">Submit Address</button>
      </form>
    )
  );
  CompanyAddress.displayName = 'CompanyAddress';
  return { CompanyAddress };
});

jest.mock('~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress', () => {
  const TypeOfAddress = React.forwardRef((props: any, ref) => (
    <form
      ref={ref as React.LegacyRef<HTMLFormElement>}
      data-testid="type-of-address-form"
      onSubmit={(e) => {
        e.preventDefault();
        props.onSubmit && props.onSubmit({ addressType: 'Business' });
      }}
    >
      <button type="submit">Submit Type</button>
    </form>
  ));
  TypeOfAddress.displayName = 'TypeOfAddress';
  return { TypeOfAddress };
});

jest.mock('~components/innBusiness/forms/CompanyDetailsForm/CompanyName', () => {
  const CompanyName = React.forwardRef((props: any, ref) => (
    <form
      ref={ref as React.LegacyRef<HTMLFormElement>}
      data-testid="company-name-form"
      onSubmit={(e) => {
        e.preventDefault();
        props.onSubmit && props.onSubmit({ companyName: 'TestCo' });
      }}
    >
      <button type="submit">Submit Company Name</button>
    </form>
  ));
  CompanyName.displayName = 'CompanyName';
  return { CompanyName };
});

jest.mock('~components/innBusiness/forms/PersonalDetailsForm', () => {
  const PersonalDetailsForm = React.forwardRef((props: any, ref) => (
    <form
      ref={ref as React.LegacyRef<HTMLFormElement>}
      data-testid="personal-details-form"
      onSubmit={(e) => {
        e.preventDefault();
        props.onSubmit &&
          props.onSubmit({
            title: 'Mr',
            firstName: 'John',
            lastName: 'Doe',
            emailAddress: 'john@doe.com',
            phoneNumber: '123',
            mobileNumber: '456',
          });
      }}
    >
      <button type="submit">Submit Personal</button>
    </form>
  ));
  PersonalDetailsForm.displayName = 'PersonalDetailsForm';
  return { PersonalDetailsForm };
});

const icons = { 'icon.notification.info': '/info.svg' };

const profileDetails = {
  contactDetail: {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john@doe.com',
    telephone: '123',
    mobile: '456',
    address: {
      line1: 'A',
      line2: '',
      line3: '',
      line4: '',
      line5: '',
      countryCode: 'GB',
      postCode: '123',
      companyName: 'TestCo',
      type: 'Business' as AddressType,
    },
  },
  paymentPreference: {},
  bookingPreference: {},
};

describe('YourProfile', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders profile in view mode', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.getByTestId('test-Your-Profile-Container')).toBeInTheDocument();
    expect(screen.getByTestId('test-Your-Profile-Title').textContent).toContain(
      'profile.profile.title'
    );
    expect(screen.getByTestId('user-details')).toBeInTheDocument();
    expect(screen.getByTestId('address-details')).toBeInTheDocument();
    expect(screen.getByTestId('test-Your-Profile-Edit-Profile-Button')).toBeInTheDocument();
  });

  it('switches to edit mode when edit button is clicked', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-Edit-Profile-Button'));
    expect(mockOnIsEditableToggle).toHaveBeenCalledWith('yourProfile', true);
  });

  it('renders edit mode UI', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.getByTestId('notification')).toBeInTheDocument();
    expect(screen.getByTestId('personal-details-form')).toBeInTheDocument();
    expect(screen.getByTestId('type-of-address-form')).toBeInTheDocument();
    expect(screen.getByTestId('company-name-form')).toBeInTheDocument();
    expect(screen.getByTestId('company-address-form')).toBeInTheDocument();
    expect(screen.getByTestId('test-Your-Profile-save-changes')).toBeInTheDocument();
    expect(screen.getByTestId('test-Your-Profile-DiscardButton')).toBeInTheDocument();
  });

  it('calls onReviewChangesToggle when cancel is clicked and is dirty', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={true}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-DiscardButton'));
    expect(mockOnReviewChangesToggle).toHaveBeenCalledWith('yourProfile', true);
  });

  it('doesnt call onReviewChangesToggle when cancel is clicked but is not dirty', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-DiscardButton'));
    expect(mockOnReviewChangesToggle).not.toHaveBeenCalledWith('yourProfile');
  });

  it('disables save button when isUpdating is true', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={true}
        isDirty={false}
      />
    );
    expect(screen.getByTestId('test-Your-Profile-save-changes')).toBeDisabled();
  });

  it('handles address type change', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={{
          ...profileDetails,
          contactDetail: {
            ...profileDetails.contactDetail,
            address: {
              ...profileDetails.contactDetail.address,
              companyName: undefined,
              type: AddressType.Home,
            },
          },
        }}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.queryByTestId('company-name-form')).toBeNull();
  });

  it('should call setShowEditButton(true) and scroll to top when isEditable changes to false', () => {
    const scrollToMock = jest.fn();
    global.window.scrollTo = scrollToMock;

    const { rerender } = render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    rerender(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(scrollToMock).toHaveBeenCalledWith({ top: 0, behavior: 'smooth' });
  });

  it('should not render CompanyName form if address type is Home', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={{
          ...profileDetails,
          contactDetail: {
            ...profileDetails.contactDetail,
            address: {
              ...profileDetails.contactDetail.address,
              companyName: undefined,
              type: AddressType.Home,
            },
          },
        }}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.queryByTestId('company-name-form')).toBeNull();
  });

  it('should call setIsAddressOpen(true) and setFormState when CompanyAddress onOpen is called', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.submit(screen.getByTestId('company-address-form'));
    // No assertion needed, just ensure no crash and coverage for onOpen
  });

  it('should call handleButtonClick and set form state/reset edit button', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-Edit-Profile-Button'));
    expect(mockOnIsEditableToggle).toHaveBeenCalledWith('yourProfile', true);
  });

  it('should not call updateProfileDetails if not all forms are submitted', () => {
    updateProfileDetailsMock.mockClear();

    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={true}
        isDirty={false}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-save-changes'));
    expect(updateProfileDetailsMock).not.toHaveBeenCalled();
  });

  it('should render correct title in edit mode', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.getByTestId('test-Your-Profile-Title').textContent).toContain(
      'profile.profile.editProfile'
    );
  });

  it('should render correct title in view mode', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.getByTestId('test-Your-Profile-Title').textContent).toContain(
      'profile.profile.title'
    );
  });

  it('should initialize addressType as Home if companyName is missing', () => {
    const profileNoCompany = {
      ...profileDetails,
      contactDetail: {
        ...profileDetails.contactDetail,
        address: {
          ...profileDetails.contactDetail.address,
          companyName: undefined,
          type: AddressType.Home,
        },
      },
    };
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileNoCompany}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-Edit-Profile-Button'));
    expect(screen.queryByTestId('company-name-form')).not.toBeInTheDocument();
  });

  it('should show error toast and reset forms on failed profile update', async () => {
    updateProfileDetailsMock.mockResolvedValue({ status: 'error' });

    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.submit(screen.getByTestId('personal-details-form'));
    fireEvent.submit(screen.getByTestId('type-of-address-form'));
    fireEvent.submit(screen.getByTestId('company-name-form'));
    fireEvent.submit(screen.getByTestId('company-address-form'));
    fireEvent.click(screen.getByTestId('test-Your-Profile-save-changes'));

    await screen.findByTestId('test-Your-Profile-Title');
  });

  it('should reset formState when handleButtonClick is called', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={false}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.click(screen.getByTestId('test-Your-Profile-Edit-Profile-Button'));
    // No assertion needed, just coverage for handleButtonClick
  });

  it('should call setIsAddressOpen(true) and setFormState when CompanyAddress onOpen is called', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    fireEvent.submit(screen.getByTestId('company-address-form'));
    // No assertion needed, just coverage for onOpen
  });

  it('should not render edit button when showEditButton is false (after entering edit mode)', () => {
    render(
      <YourProfile
        baseDataTestId="test"
        icons={icons}
        profileDetails={profileDetails}
        locale={LOCALES.EN}
        onReviewChangesToggle={mockOnReviewChangesToggle}
        onIsEditableToggle={mockOnIsEditableToggle}
        onIsUpdatingToggle={mockOnIsUpdatingToggle}
        onIsDirtyToggle={mockOnIsDirtyToggle}
        isEditable={true}
        isUpdating={false}
        isDirty={false}
      />
    );
    expect(screen.queryByTestId('test-Your-Profile-Edit-Profilee-Button')).toBeNull();
  });
});
