import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import { MealsAndExtrasForm } from './MealsAndExtrasForm';

const mockProfileDetails = {
  contactDetail: {
    title: 'Mr',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@yupmail.com',
    phoneNumber: '+4411111111',
    mobileNumber: '+4411111112',
    address: {
      line1: 'Test',
      line2: '',
      line3: '',
      line4: '',
      line5: '',
      countryCode: 'GB',
      postCode: 'SW1A 2AA',
    },
  },
  bookingPreference: {
    roomRequirements: {
      type: 'FAM',
      lettingType: null,
      adults: 2,
      children: 1,
      cotRequired: true,
      hotelBrand: null,
    },
    reason: null,
    foodPreference: 0,
    wantSmsConfirmations: null,
    preselectWifi: false,
  },
  paymentPreference: {},
} as any;

const mockProps = {
  baseDataTestId: 'testId',
  icons: { icon: 'test' },
  profileDetails: mockProfileDetails,
  handleEditMode: jest.fn(),
};

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

let mockUpdateResponse = {
  status: 'success',
};

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    refresh: jest.fn(),
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    getCountriesList: () => {
      return 'United Kingdom (the)';
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    updateProfileDetails: () => mockUpdateResponse,
  };
});

window.scrollTo = jest.fn();

describe('MealsAndExtrasForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render MealsAndExtrasForm component', async () => {
    const { getByTestId } = render(<MealsAndExtrasForm {...mockProps} />);

    const extrasForm = getByTestId('testId-Meals-And-Extras-Form');

    await waitFor(() => {
      expect(extrasForm).toBeInTheDocument();
    });
  });

  it('should click Save button and call handleEditMode', async () => {
    const { handleEditMode } = mockProps;
    const { getByTestId } = render(<MealsAndExtrasForm {...mockProps} />);

    expect(getByTestId('testId-Meals-And-Extras-Form')).toBeInTheDocument();

    const saveButton = getByTestId('testId-Meals-And-Extras-Save-Button');

    expect(saveButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(saveButton);
    });

    await waitFor(() => {
      expect(handleEditMode).toBeCalled();
    });
  });

  it('should click Save button with no data', async () => {
    mockUpdateResponse = { status: 'fail' };
    mockProfileDetails.bookingPreference = {};
    const { handleEditMode } = mockProps;
    const { getByTestId } = render(<MealsAndExtrasForm {...mockProps} />);

    expect(getByTestId('testId-Meals-And-Extras-Form')).toBeInTheDocument();

    const saveButton = getByTestId('testId-Meals-And-Extras-Save-Button');

    expect(saveButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(saveButton);
    });

    await waitFor(() => {
      expect(handleEditMode).toBeCalledTimes(0);
    });
  });
});
