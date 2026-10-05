import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Channel } from '@whitbread-eos/api';
import { getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests } from '@whitbread-eos/utils';
import * as React from 'react';

import {
  mockBasketDetailsState,
  mockBasketDetailsStateWithAccessibleDoubleRoomAndLoweredAndWetRoom,
  mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
  mockBasketDetailsStateWithTwoRooms,
  mockBasketDetailsStateWithAccessibleRoomSubstitutedForDouble,
  mockHotelInventory,
  mockMutationRequest,
  visualDisplayContext,
  mockEmptyBasketDetails,
} from '~mocks/hotel-details';
import { render } from '~utils/test-utils';

import ChooseRoomTypePage from './page.bb';

const roomAvailability = [
  {
    availableCount: 7,
    code: 'BRFDBL',
  },
  {
    availableCount: 0,
    code: 'WETDBL',
  },
  {
    availableCount: 8,
    code: 'LOWDBL',
  },
];

const mockCustomLocale = jest.fn();
const mockUseLocalStorage = jest.fn();
const mockSetBasketDetailsCookie = jest.fn();

const mockRouter = {
  push: jest.fn(),
  asPath: '/en/hotels/choose-roomtype',
};
const queryClient = new ReactQuery.QueryClient();

const mockProps = {
  channel: 'PI' as Channel,
  visualDisplayContext,
  queryClient,
  router: mockRouter as any,
};
jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    invalidateQueries: jest.fn(),
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useLocalStorage: () => mockUseLocalStorage(),
  useCustomLocale: () => mockCustomLocale(),
  setBasketDetailsCookie: () => mockSetBasketDetailsCookie(),
  useQuery: () => mockHotelInventory,
  useQueryRequest: () => mockHotelInventory,
  useMutationRequest: () => mockMutationRequest,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  AccessibleBathroomOptions: () => <div />,
  Basket: () => <div />,
  BackButton: () => <div />,
  AccessibleGallery: () => <div />,
  SEO: () => <div />,
  ChooseRoomContinueBtn: () => <div data-testid="ChooseRoomTypePage-ContinueButton" />,
  RoomChoiceGallery: () => <div />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Info: () => <div />,
  Notification: () => <div />,
  BritishFlagRounded: () => <div />,
  GermanFlagRounded: () => <div />,
  Icon: () => <div />,
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Page PI Choose Room Type', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render Choose Room Type PI page', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { container } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Room Type PI page with no basket reference', () => {
    mockMutationRequest.isSuccess = true;
    mockMutationRequest.isError = false;
    mockMutationRequest.data = { createReservation: undefined };
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { container } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should navigate to ancillaries page when bookReservationData is returned', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = true;
    mockMutationRequest.isError = false;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    render(<ChooseRoomTypePage {...mockProps} />);
    mockRouter.push('/gb/en/booking-a1/ancillaries?reservationId=123');
    expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/booking-a1/ancillaries?reservationId=123');
  });

  it('should render Choose Room Type PI page with wet double', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories[0].code = 'WETDBL';
    const { container } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Room Type PI page with wet twin', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories[0].code = 'WETTWN';
    const { container } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Room Type PI page with low twin', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories[0].code = 'LOWTWN';
    const { container } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Room Type PI page with no hotel inventory data', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories = [];
    const { container } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render empty Choose Roomtype PI page', () => {
    mockUseLocalStorage.mockReturnValue([mockEmptyBasketDetails, jest.fn()]);
    const { queryByTestId } = render(<ChooseRoomTypePage {...mockProps} />);
    expect(queryByTestId('ChooseRoomTypePage-PageContent')).not.toBeInTheDocument();
  });
});

describe('getSelectedPMSRoomTypesAndSpecialRequests and getPMSRoomTypeByRoomTypeAndBathroom Methods', () => {
  it('should return selectedPMSRoomTypesAndSpecialRequests when two rooms are selected (DOUBLE and DIS)', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);
    const roomTypeSelections = ['', 'Accessible Double'];
    const accessibleRoomTypeSelections = ['wet', 'wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['DOUBLE', 'WETDBL'],
      selectedSpecialRequests: [['SING'], ['SING', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        mockBasketDetailsStateWithTwoRooms,
        roomAvailability
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible room with wet room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const roomTypeSelections = ['Accessible Double'];
    const accessibleRoomTypeSelections = ['wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['WETDBL'],
      selectedSpecialRequests: [['SING', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        mockBasketDetailsState,
        roomAvailability
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible double room with lowered room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([
      mockBasketDetailsStateWithAccessibleDoubleRoomAndLoweredAndWetRoom,
      jest.fn(),
    ]);
    const roomTypeSelections = ['Accessible Double'];
    const accessibleRoomTypeSelections = ['lowered'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['LOWDBL'],
      selectedSpecialRequests: [['SING', 'LOWB']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        mockBasketDetailsStateWithAccessibleDoubleRoomAndLoweredAndWetRoom,
        roomAvailability
      );

    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible twin room with lowered room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([
      mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
      jest.fn(),
    ]);
    const roomTypeSelections = ['Accessible Twin'];
    const accessibleRoomTypeSelections = ['lowered'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['LOWTWN'],
      selectedSpecialRequests: [['TWIN', 'LOWB']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
        roomAvailability
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible twin room with wet room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([
      mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
      jest.fn(),
    ]);
    const roomTypeSelections = ['Accessible Twin'];
    const accessibleRoomTypeSelections = ['wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['WETTWN'],
      selectedSpecialRequests: [['TWIN', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
        roomAvailability
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible room is subsituted for a DOUBLE room search', () => {
    const roomTypeSelections = ['Accessible Double'];
    const accessibleRoomTypeSelections = ['wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['WETDBL'],
      selectedSpecialRequests: [['SING', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        mockBasketDetailsStateWithAccessibleRoomSubstitutedForDouble,
        roomAvailability
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });
});
