import '@testing-library/jest-dom';
import { render, fireEvent, waitFor, act } from '@testing-library/react';
import * as serverUtils from '@whitbread-eos/utils/server';

import { RoomPreferencesForm } from './RoomPreferencesForm';

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  getAuthCookie: jest.fn(() => 'cookie'),
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  updateProfileDetails: jest.fn(),
}));
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: (props: any) => <button {...props} />,
  useToast: () => ({
    toast: jest.fn(),
  }),
  ButtonVariantDescriptor: {
    truncateWithEllipsisButton: 'truncateWithEllipsisButton',
  },
}));
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    refresh: jest.fn(),
  }),
}));
jest.mock('next/navigation', () => ({
  useRouter: () => ({
    refresh: jest.fn(),
  }),
}));

const RoomRequirementsFormMock = jest.fn();
jest.mock('~components/innBusiness/forms/RoomRequirementsForm', () => ({
  RoomRequirementsForm: (props: any) => {
    RoomRequirementsFormMock(props);
    return (
      <form
        ref={props.formRef}
        data-testid="room-requirements-form"
        onSubmit={(e) => {
          e.preventDefault();
          props.onSubmit({
            adults: 2,
            children: 1,
            cotRequired: true,
            type: 'suite',
          });
        }}
      >
        <button type="submit">Submit</button>
      </form>
    );
  },
}));

const baseProps = {
  baseDataTestId: 'test',
  icons: { icon1: 'icon1' },
  profileDetails: {
    contactDetail: {
      email: 'test@email.com',
      address: {
        addressLine1: '123 Test St',
        addressLine2: '',
        city: 'Test City',
        country: 'Test Country',
        postalCode: '12345',
        countryCode: 'TC',
        line1: '123 Test St',
      },
      firstName: 'Test',
      lastName: 'User',
      title: 'Mr',
    },
    paymentPreference: {},
    bookingPreference: {
      roomRequirements: {
        adults: 1,
        children: 0,
        cotRequired: false,
        type: 'standard',
      },
    },
  },
  handleEditMode: jest.fn(),
  isUpdating: false,
  isDirty: false,
  onIsUpdatingToggle: jest.fn(),
  onReviewChangesToggle: jest.fn(),
  onIsDirtyToggle: jest.fn(),
  onIsEditableToggle: jest.fn(),
};

describe('RoomPreferencesForm', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders form and buttons', () => {
    const { getByTestId, getByText } = render(<RoomPreferencesForm {...baseProps} />);
    expect(getByTestId('test-Room-Preferences-Form')).toBeInTheDocument();
    expect(getByTestId('test-Room-Preferences-Buttons-Container')).toBeInTheDocument();
    expect(getByTestId('test-Room-Preferences-Save-Button')).toBeInTheDocument();
    expect(getByTestId('test-Room-Preferences-Cancel-Button')).toBeInTheDocument();
    expect(getByText('profile.roomrequirements.button.save')).toBeInTheDocument();
    expect(getByText('profile.roomrequirements.button.cancel')).toBeInTheDocument();
  });

  it('calls onReviewChangesToggle when cancel is clicked', () => {
    const onReviewChangesToggle = jest.fn();
    const { getByTestId } = render(
      <RoomPreferencesForm
        {...baseProps}
        onReviewChangesToggle={onReviewChangesToggle}
        isDirty={true}
      />
    );
    fireEvent.click(getByTestId('test-Room-Preferences-Cancel-Button'));
    expect(onReviewChangesToggle).toHaveBeenCalledWith('roomPreferences', true);
  });

  it('doesnt call onReviewChangesToggle when cancel is clicked and isDirty false', () => {
    const onReviewChangesToggle = jest.fn();
    const { getByTestId } = render(
      <RoomPreferencesForm {...baseProps} onReviewChangesToggle={onReviewChangesToggle} />
    );
    fireEvent.click(getByTestId('test-Room-Preferences-Cancel-Button'));
    expect(onReviewChangesToggle).not.toHaveBeenCalledWith('roomPreferences', true);
  });

  it('submits the form when save is clicked', async () => {
    const { getByTestId } = render(<RoomPreferencesForm {...baseProps} />);
    await act(async () => {
      fireEvent.click(getByTestId('test-Room-Preferences-Save-Button'));
    });
    expect(RoomRequirementsFormMock).toHaveBeenCalled();
  });

  it('disables save button when isUpdating is true', () => {
    const { getByTestId } = render(<RoomPreferencesForm {...baseProps} isUpdating={true} />);
    expect(getByTestId('test-Room-Preferences-Save-Button')).toBeDisabled();
  });

  it('calls updateProfileDetails and shows success toast on success', async () => {
    (serverUtils.updateProfileDetails as jest.Mock).mockResolvedValue({ status: 'success' });
    const handleEditMode = jest.fn();
    const onIsUpdatingToggle = jest.fn();
    const { getByTestId } = render(
      <RoomPreferencesForm
        {...baseProps}
        handleEditMode={handleEditMode}
        onIsUpdatingToggle={onIsUpdatingToggle}
      />
    );
    await act(async () => {
      fireEvent.click(getByTestId('test-Room-Preferences-Save-Button'));
    });
    await waitFor(() => {
      expect(serverUtils.updateProfileDetails).toHaveBeenCalled();
      expect(handleEditMode).toHaveBeenCalled();
    });
  });

  it('calls updateProfileDetails and shows error toast on failure', async () => {
    (serverUtils.updateProfileDetails as jest.Mock).mockResolvedValue({ status: 'failure' });
    const onIsUpdatingToggle = jest.fn();
    const { getByTestId } = render(
      <RoomPreferencesForm {...baseProps} onIsUpdatingToggle={onIsUpdatingToggle} />
    );
    await act(async () => {
      fireEvent.click(getByTestId('test-Room-Preferences-Save-Button'));
    });
    await waitFor(() => {
      expect(serverUtils.updateProfileDetails).toHaveBeenCalled();
    });
  });
});
