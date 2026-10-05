import { fireEvent, render, waitFor } from '../../../utils/test-utils';
import UpdateAccountForm from './UpdateAccountForm.component';

const dataForUpdateForm = {
  title: 'Mr',
  firstName: 'Gabriel',
  lastName: ' Miron',
  companyName: ' Mironian S.R.L.',
  email: ' gabriel.miron@whitbread.com',
  address: '',
  postalCode: 'E1 6AN',
  mobileNumber: '+40724256888',
  landlineNumber: '',
};

const selectedRow = {
  '0': {
    id: 'GuestName',
    value: 'Mr Gabriel Miron',
  },
  '1': {
    id: 'CompanyName',
    value: 'Mironian S.R.L.',
  },
  '2': {
    id: 'Email',
    value: 'gabriel.miron@whitbread.com',
  },
  '3': {
    id: 'PostCodeHome',
    value: '567 MH',
  },
  '4': {
    id: 'PostCodeCompany',
    value: '567 MH',
  },
  rowNr: 0,
  accountId: '897671234',
};

const mockLocalStorage = {
  reasonForStay: '',
  title: '',
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  landline: '',
  companyName: '',
  addressLine1: '',
  addressLine2: '',
  addressLine3: '',
  addressLine4: '',
  postalCode: '',
  cityName: '',
  postcodeAddress: '',
  addressSelection: '',
  countryCode: 'GB',
  manualAddressToggle: '',
  basketReferenceId: 'AWM-b8a5cc85-0e3c-4890-86fa-a388638d0093',
  leadGuest: [],
  bookingForSomeoneElse: false,
  acceptFutureMailing: false,
  country: 'gb',
  language: 'en',
};

const mockUseRouter = jest.fn();
const mockUseFeatureSwitch = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
}));

describe('UpdateAccountForm component', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseRouter.mockReturnValue({
      query: null,
      locale: 'en',
    });
    mockUseFeatureSwitch.mockReturnValue(true);
  });

  it('should render component', () => {
    const { getByTestId } = render(
      <UpdateAccountForm dataForUpdateForm={dataForUpdateForm} selectedRow={selectedRow} />
    );
    expect(getByTestId('UpdateAccountPage-wrapper')).toBeInTheDocument();
  });

  it('should be pressed Re-use details Btn', async () => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'AWM-b3d99cb6-f8ec-4b8f-988d-f765055a9770' },
      locale: 'gb',
    });

    window.localStorage.setItem('formDetails', JSON.stringify(mockLocalStorage));

    const url = '/gb/en/guest-details?reservationId=AWM-b3d99cb6-f8ec-4b8f-988d-f765055a9770';
    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
      writable: true,
    });

    const { getByTestId, queryByTestId } = render(
      <UpdateAccountForm dataForUpdateForm={dataForUpdateForm} selectedRow={selectedRow} />
    );

    const guestVerifyButton = getByTestId('UpdateAccountPage-Guest-Verified-Btn');
    fireEvent.click(guestVerifyButton);

    // Wait for the appearance of the other buttons
    await waitFor(() => {
      expect(queryByTestId('UpdateAccountPage-Reuse-Details-Btn')).toBeInTheDocument();
    });

    const reuseDetailsBtn = getByTestId('UpdateAccountPage-Reuse-Details-Btn');
    fireEvent.click(reuseDetailsBtn);

    expect(window.location.href).toEqual(url);
  });

  it('should persist cityName from addressLine4 on Re-use details when cityName is empty', async () => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'AWM-b3d99cb6-f8ec-4b8f-988d-f765055a9770' },
      locale: 'gb',
    });

    window.localStorage.setItem(
      'formDetails',
      JSON.stringify({
        ...mockLocalStorage,
        cityName: '',
        addressLine4: 'Berlin',
      })
    );

    const url = '/gb/en/guest-details?reservationId=AWM-b3d99cb6-f8ec-4b8f-988d-f765055a9770';
    Object.defineProperty(window, 'location', {
      value: {
        href: url,
      },
      writable: true,
    });

    const { getByTestId, queryByTestId } = render(
      <UpdateAccountForm dataForUpdateForm={dataForUpdateForm} selectedRow={selectedRow} />
    );

    const guestVerifyButton = getByTestId('UpdateAccountPage-Guest-Verified-Btn');
    fireEvent.click(guestVerifyButton);

    await waitFor(() => {
      expect(queryByTestId('UpdateAccountPage-Reuse-Details-Btn')).toBeInTheDocument();
    });

    const reuseDetailsBtn = getByTestId('UpdateAccountPage-Reuse-Details-Btn');
    fireEvent.click(reuseDetailsBtn);

    const formDetails = JSON.parse(window.localStorage.getItem('formDetails') || '{}');
    expect(formDetails.cityName).toBe('Berlin');
  });

  it('should show buttons after guest verification', async () => {
    const { getByTestId, queryByTestId } = render(
      <UpdateAccountForm dataForUpdateForm={dataForUpdateForm} selectedRow={selectedRow} />
    );

    const guestVerifyButton = getByTestId('UpdateAccountPage-Guest-Verified-Btn');
    fireEvent.click(guestVerifyButton);

    // Wait for the appearance of the other buttons
    await waitFor(() => {
      expect(queryByTestId('UpdateAccountPage-Save-Changes-Btn')).toBeInTheDocument();
      expect(queryByTestId('UpdateAccountPage-Save-Changes-Btn')).not.toBeDisabled();
      expect(queryByTestId('UpdateAccountPage-Unlock-Account-Btn')).toBeInTheDocument();
      expect(queryByTestId('UpdateAccountPage-Reset-Password-Btn')).toBeInTheDocument();
      expect(queryByTestId('UpdateAccountPage-Reuse-Details-Btn')).not.toBeInTheDocument();
      // For future testing purpose
      const saveChangesBtn = getByTestId('UpdateAccountPage-Save-Changes-Btn');
      fireEvent.click(saveChangesBtn);
    });
  });

  it('should disable SaveChangesBtn when feature flag is false', async () => {
    mockUseFeatureSwitch.mockReturnValue(false);

    const { getByTestId, queryByTestId } = render(
      <UpdateAccountForm dataForUpdateForm={dataForUpdateForm} selectedRow={selectedRow} />
    );

    const guestVerifyButton = getByTestId('UpdateAccountPage-Guest-Verified-Btn');
    fireEvent.click(guestVerifyButton);

    // Wait for the appearance of the other buttons
    await waitFor(() => {
      expect(queryByTestId('UpdateAccountPage-Save-Changes-Btn')).toBeDisabled();
    });
  });
});
