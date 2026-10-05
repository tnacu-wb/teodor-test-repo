import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { BASKET_DETAILS_STATE_INITIAL_VALUE, Channel, Claims } from '@whitbread-eos/api';
import { getBathRoomSelectedPMSRoomTypesAndSpecialRequests } from '@whitbread-eos/utils';
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
} from '~mocks/hotel-details';
import { fireEvent, render, waitFor } from '~utils/test-utils';

import ChooseBathroomPage from './page.ccui';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file

const mockCustomLocale = jest.fn();
const mockUseLocalStorage = jest.fn();
const mockSetBasketDetailsCookie = jest.fn();
const queryClient = new ReactQuery.QueryClient();

const translateFn = (key: string) => {
  switch (key) {
    case 'accessible.double':
      return 'Accessible Double';
    case 'accessible.twin':
      return 'Accessible Twin';
    default:
      return key;
  }
};

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
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

const mockRouter = {
  push: jest.fn(),
  asPath: '/en/hotels/choose-bathroom',
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  AccessibleBathroomOptions: () => <div />,
  Basket: () => <div />,
  BackButton: () => <div />,
  AccessibleGallery: () => <div />,
  ChooseRoomContinueBtn: () => <div data-testid="ChooseBathroomPage-ContinueButton" />,
  RoomChoiceGallery: () => <div />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Info: () => <div />,
  Notification: () => <div />,
  BritishFlagRounded: () => <div />,
  GermanFlagRounded: () => <div />,
  Icon: () => <div />,
}));

const setAnalyticsUser = jest.fn();

const mockProps = {
  channel: 'CCUI' as Channel,
  visualDisplayContext,
  queryClient,
  router: mockRouter as any,
  user: 'agent' as unknown as Claims,
  setAnalyticsUser: setAnalyticsUser,
};

describe('Page CCUI Choose Bathroom', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render Choose Bathroom CCUI page', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { getByTestId } = render(<ChooseBathroomPage {...mockProps} />);
    expect(getByTestId('ChooseBathroomPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('ChooseBathroomPage-PageContent')).toBeInTheDocument();
  });

  it('should render Choose Bathroom CCUI page with no basket reference', () => {
    mockMutationRequest.isSuccess = true;
    mockMutationRequest.isError = false;
    mockMutationRequest.data = { createReservation: { basketReference: undefined } };
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { container } = render(<ChooseBathroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should navigate to ancillaries page when bookReservationData is returned', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = true;
    mockMutationRequest.isError = false;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    render(<ChooseBathroomPage {...mockProps} />);
    mockRouter.push('/gb/en/booking-a1/ancillaries?reservationId=123');
    expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/booking-a1/ancillaries?reservationId=123');
  });

  it('should render Choose Bathroom CCUI page with wet double', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories[0].code = 'WETDBL';
    const { container } = render(<ChooseBathroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Bathroom CCUI page with wet twin', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories[0].code = 'WETTWN';
    const { container } = render(<ChooseBathroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Bathroom CCUI page with low twin', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories[0].code = 'LOWTWN';
    const { container } = render(<ChooseBathroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render Choose Bathroom CCUI page with no hotel inventory data', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = false;
    mockMutationRequest.isError = true;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    mockHotelInventory.data.hotelInventory.roomTypeInventories = [];
    const { container } = render(<ChooseBathroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should render page and ChooseRoomContinue button and click on it', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { getByTestId } = render(<ChooseBathroomPage {...mockProps} />);
    const button = getByTestId('ChooseBathroomPage-ContinueButton');
    expect(button).toBeInTheDocument();
    fireEvent.click(button);

    waitFor(() => {
      expect(mockRouter.push).toHaveBeenCalledWith(
        '/gb/en/booking-a1/ancillaries?reservationId=123'
      );
    });
  });

  it('should render empty Choose Bathroom CCUI page', () => {
    mockUseLocalStorage.mockReturnValue([BASKET_DETAILS_STATE_INITIAL_VALUE, jest.fn()]);
    const { queryByTestId } = render(<ChooseBathroomPage {...mockProps} />);
    expect(queryByTestId('ChooseBathroomPage-PageContent')).not.toBeInTheDocument();
  });
});

describe('getSelectedPMSRoomTypesAndSpecialRequests and getPMSRoomTypeByRoomTypeAndBathroom Methods', () => {
  it('should return selectedPMSRoomTypesAndSpecialRequests when two rooms are selected (DOUBLE and DIS)', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);
    const roomTypeSelections = ['', 'Accessible Double'];
    const bathroomSelections = ['wet', 'wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['DOUBLE', 'WETDBL'],
      selectedSpecialRequests: [['SING'], ['SING', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        translateFn,
        true,
        roomTypeSelections,
        bathroomSelections,
        mockBasketDetailsStateWithTwoRooms,
        'en'
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible room with wet room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = ['wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['WETDBL'],
      selectedSpecialRequests: [['SING', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        translateFn,
        true,
        roomTypeSelections,
        bathroomSelections,
        mockBasketDetailsState,
        'en'
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible double room with lowered room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([
      mockBasketDetailsStateWithAccessibleDoubleRoomAndLoweredAndWetRoom,
      jest.fn(),
    ]);
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = ['lowered'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['LOWDBL'],
      selectedSpecialRequests: [['SING', 'LOWB']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        translateFn,
        true,
        roomTypeSelections,
        bathroomSelections,
        mockBasketDetailsStateWithAccessibleDoubleRoomAndLoweredAndWetRoom,
        'en'
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible twin room with lowered room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([
      mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
      jest.fn(),
    ]);
    const roomTypeSelections = ['Accessible Twin'];
    const bathroomSelections = ['lowered'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['LOWTWN'],
      selectedSpecialRequests: [['TWIN', 'LOWB']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        translateFn,
        true,
        roomTypeSelections,
        bathroomSelections,
        mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
        'en'
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible twin room with wet room bathroom is selected', () => {
    mockUseLocalStorage.mockReturnValue([
      mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
      jest.fn(),
    ]);
    const roomTypeSelections = ['Accessible Twin'];
    const bathroomSelections = ['wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['WETTWN'],
      selectedSpecialRequests: [['TWIN', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        translateFn,
        true,
        roomTypeSelections,
        bathroomSelections,
        mockBasketDetailsStateWithAccessibleTwinRoomAndLoweredAndWetRoom,
        'en'
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });

  it('should return selectedPMSRoomTypesAndSpecialRequests when accessible room is subsituted for a DOUBLE room search', () => {
    const roomTypeSelections = ['Accessible Double'];
    const bathroomSelections = ['wet'];
    const expectedResponse = {
      selectedPMSRoomTypes: ['WETDBL'],
      selectedSpecialRequests: [['SING', 'WETR']],
    };
    const selectedPMSRoomTypesAndSpecialRequests =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        translateFn,
        true,
        roomTypeSelections,
        bathroomSelections,
        mockBasketDetailsStateWithAccessibleRoomSubstitutedForDouble,
        'en'
      );
    expect(selectedPMSRoomTypesAndSpecialRequests).toEqual(expectedResponse);
  });
});
