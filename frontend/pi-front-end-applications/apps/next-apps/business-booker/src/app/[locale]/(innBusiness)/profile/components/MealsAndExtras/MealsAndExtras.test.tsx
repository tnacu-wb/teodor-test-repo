import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import { MealsAndExtras } from './MealsAndExtras';

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
  onReviewChangesToggle: jest.fn(),
  onIsEditableToggle: jest.fn(),
  onIsUpdatingToggle: jest.fn(),
  onIsDirtyToggle: jest.fn(),
  isEditable: false,
  isUpdating: false,
  isDirty: false,
};

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(),
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
  };
});

describe('MealsAndExtras Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render MealsAndExtras component', async () => {
    const { getByTestId } = render(<MealsAndExtras {...mockProps} />);

    const extrasContainer = getByTestId('testId-Meals-And-Extras-Container');
    const extrasTitle = getByTestId('testId-Meals-And-Extras-Title');
    const extrasButton = getByTestId('testId-Meals-And-Extras-Edit-Button');

    await waitFor(() => {
      expect(extrasContainer).toBeInTheDocument();
      expect(extrasButton).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(extrasButton);
    });

    await waitFor(() => {
      expect(extrasTitle).toBeInTheDocument();
    });
  });
});
