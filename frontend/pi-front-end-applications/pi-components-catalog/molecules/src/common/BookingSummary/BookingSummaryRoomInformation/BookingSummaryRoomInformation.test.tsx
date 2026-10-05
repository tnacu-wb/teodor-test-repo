import '@testing-library/jest-dom';
import { getLocalStorageMock } from '@whitbread-eos/utils';

import { render } from '../../../utils/test-utils';
import BookingSummaryRoomInformation, { Props } from './BookingSummaryRoomInformation';

const mockUseFeatureSwitch = jest.fn();

const mockCookies = {
  bundles: 'class',
};

const mockFlags = {
  release_pi_display_soft_bundles: false,
};

const mockSoftBundles = [
  {
    id: 'BFADBF',
    price: 86.99,
    description: 'Breakfast',
  },
  {
    id: 'HSATWN',
    price: 45.0,
    description: 'Wifi',
  },
];

const mockUseSessionStorage = jest.fn((key) => {
  if (key === 'softBundles') {
    return [mockSoftBundles, jest.fn()];
  }
  return ['', jest.fn()];
});

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useFeatureSwitch: () => mockUseFeatureSwitch(),
    useFeatureToggle: jest.fn(() => ({
      ...mockFlags,
    })),
    useSessionStorage: (key: string) => mockUseSessionStorage(key),
  };
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      locale: 'en',
    },
    query: {
      reservationId: 'basket123',
    },
  }),
}));

const mockProps: Props = {
  t: (key: string) => {
    switch (key) {
      default:
        return 'default';
    }
  },
  roomInformation: [
    {
      accessibleRoom: {
        isAccessible: true,
        phoneNumber: '0333 321 3104',
      },
      roomName: '',
      roomType: 'Double',
      nrChildren: 1,
      nrAdults: 2,
      selectedMeals: {
        adultsMeals: [],
        childrenMeals: [],
      },
      selectedExtrasList: {
        packagesSelection: [
          {
            id: 'HSCKIN',
            noOfSelections: 1,
          },
          {
            id: 'HSCOU2',
            noOfSelections: 1,
          },
        ],
        price: 10,
        reservationId: '1988739',
      },
    },
  ],
  isExtrasDisplayed: true,
};

const mockPropsWithAdultAndChildrenMeal = {
  ...mockProps,
  roomInformation: [
    {
      accessibleRoom: {
        isAccessible: true,
        phoneNumber: '0333 321 3104',
      },
      roomType: 'Double',
      roomName: 'Double',
      nrChildren: 1,
      nrAdults: 2,
      selectedMeals: {
        adultsMeals: [
          {
            id: 'BFADBF',
            noSelections: 1,
            price: 9.5,
            title: 'Premier Inn Breakfast',
          },
        ],
        childrenMeals: [
          {
            id: 'BFCHDF',
            noSelections: 1,
            title: 'Free breakfast for kids',
          },
        ],
      },
    },
  ],
};

const localStorageMock = getLocalStorageMock();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

describe('BookingSummaryRoomInformation', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';
  it('should render a <BookingSummaryRoomInformation> without data-testid', function () {
    const { getByTestId } = render(<BookingSummaryRoomInformation {...mockProps} />);

    expect(getByTestId('RoomInformation-Wrapper')).toBeInTheDocument();
    expect(getByTestId('RoomInformation-RoomNumber')).toBeInTheDocument();
    expect(getByTestId('RoomInformation-AdultsNumber')).toBeInTheDocument();
    expect(getByTestId('RoomInformation-ChildrenNumber')).toBeInTheDocument();
    expect(getByTestId('RoomInformation-NoMealsSelected')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryRoomInformation> with data-testid', function () {
    mockProps.prefixDataTestId = 'BookingSummary';
    mockProps.roomInformation[0].selectedExtrasList.packagesSelection = [];
    const { getByTestId } = render(<BookingSummaryRoomInformation {...mockProps} />);
    expect(getByTestId('BookingSummary-RoomInformation-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RoomInformation-RoomNumber')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RoomInformation-AdultsNumber')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RoomInformation-ChildrenNumber')).toBeInTheDocument();
    expect(getByTestId('BookingSummary-RoomInformation-NoMealsSelected')).toBeInTheDocument();
  });

  it('should render a <BookingSummaryRoomInformation> with a meal for adults and children', function () {
    mockPropsWithAdultAndChildrenMeal.prefixDataTestId = 'BookingSummary';
    const expectedTextForAdult = 'Premier Inn Breakfast default 1 default';
    const expectedTextForChildren = 'Free breakfast for kids default 1 default';
    const { getByTestId, queryByTestId } = render(
      <BookingSummaryRoomInformation {...mockPropsWithAdultAndChildrenMeal} />
    );
    expect(queryByTestId('BookingSummary-RoomInformation-NoMealsSelected')).not.toBeInTheDocument();
    expect(getByTestId('BookingSummary-RoomInformation-AdultMeal').textContent).toBe(
      expectedTextForAdult
    );
    expect(getByTestId('BookingSummary-RoomInformation-ChildrenMeal').textContent).toBe(
      expectedTextForChildren
    );
  });

  it('should render a <BookingSummaryRoomInformation> with a meal for adults and 2 children', function () {
    const expectedTextForChildren = 'Free breakfast for kids default 2 default';
    mockPropsWithAdultAndChildrenMeal.prefixDataTestId = 'BookingSummary';
    mockPropsWithAdultAndChildrenMeal.roomInformation[0].selectedMeals.childrenMeals[0].noSelections = 2;
    const { getByTestId } = render(
      <BookingSummaryRoomInformation {...mockPropsWithAdultAndChildrenMeal} />
    );
    expect(getByTestId('BookingSummary-RoomInformation-ChildrenMeal').textContent).toBe(
      expectedTextForChildren
    );
  });

  describe('Accessible Notification component', () => {
    it('should render <Notification> component if isAccessible is true--------', function () {
      const { queryByTestId } = render(<BookingSummaryRoomInformation {...mockProps} />);
      expect(queryByTestId('Alert')).toBeInTheDocument();
    });

    it('should NOT render <Notification> component if isAccessible is false', function () {
      mockProps.roomInformation[0].accessibleRoom.isAccessible = false;
      const { queryByTestId } = render(<BookingSummaryRoomInformation {...mockProps} />);
      expect(queryByTestId('Alert')).not.toBeInTheDocument();
    });
  });

  describe('Check Silent Substitution Feature Flag states', () => {
    it('should check Silent Substitution Feature Flag true', function () {
      localStorageMock.setItem(
        SILENT_SUBSTITUTION_STORAGE_KEY,
        JSON.stringify({
          basket123: {
            value: [
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
            ],
            expire: 123,
          },
        })
      );

      mockUseFeatureSwitch.mockReturnValue(true);

      const { getByText } = render(
        <BookingSummaryRoomInformation {...mockPropsWithAdultAndChildrenMeal} />
      );

      expect(getByText('(Double room)')).toBeInTheDocument();
    });

    it('should check Silent Substitution Feature Flag true when nothing is stored inside the local storage', function () {
      localStorageMock.clear();

      mockPropsWithAdultAndChildrenMeal.roomInformation[0].roomName = 'Bigger room';
      mockPropsWithAdultAndChildrenMeal.roomInformation[0].roomType = 'BIGWIN';
      mockUseFeatureSwitch.mockReturnValue(true);

      const { getByText } = render(
        <BookingSummaryRoomInformation {...mockPropsWithAdultAndChildrenMeal} />
      );

      expect(getByText('(Bigger room)')).toBeInTheDocument();
    });

    it('should check Silent Substitution Feature Flag false', function () {
      mockPropsWithAdultAndChildrenMeal.roomInformation[0].roomName = 'Bigger room';
      mockPropsWithAdultAndChildrenMeal.roomInformation[0].roomType = 'BIGWIN';
      mockUseFeatureSwitch.mockReturnValue(false);

      const { getByText } = render(
        <BookingSummaryRoomInformation {...mockPropsWithAdultAndChildrenMeal} />
      );

      expect(getByText('(Bigger room)')).toBeInTheDocument();
    });
  });

  it('should check Silent Substitution Feature Flag true and silentSubstitution false', function () {
    mockPropsWithAdultAndChildrenMeal.roomInformation[0].roomName = 'Bigger room';
    mockPropsWithAdultAndChildrenMeal.roomInformation[0].roomType = 'BIGWIN';

    localStorageMock.setItem(
      SILENT_SUBSTITUTION_STORAGE_KEY,
      JSON.stringify({
        basket1234: {
          value: [
            {
              roomLabelCode: 'BIGWIN',
              silentSubstitution: false,
            },
          ],
          expire: 1234,
        },
      })
    );

    mockUseFeatureSwitch.mockReturnValue(true);

    const { getByText } = render(
      <BookingSummaryRoomInformation {...mockPropsWithAdultAndChildrenMeal} />
    );

    expect(getByText('(Bigger room)')).toBeInTheDocument();
  });

  it('should render summary room info with soft bundles', function () {
    const { getByTestId } = render(
      <BookingSummaryRoomInformation
        {...mockPropsWithAdultAndChildrenMeal}
        isSoftBundlesVisible={true}
      />
    );

    expect(getByTestId('BookingSummary-RoomInformation-Wrapper')).toBeInTheDocument();
  });

  it('should render summary room info with soft bundles prop false', function () {
    const { getByTestId } = render(
      <BookingSummaryRoomInformation
        {...mockPropsWithAdultAndChildrenMeal}
        isSoftBundlesVisible={false}
      />
    );
    expect(getByTestId('BookingSummary-RoomInformation-Wrapper')).toBeInTheDocument();
  });
});
