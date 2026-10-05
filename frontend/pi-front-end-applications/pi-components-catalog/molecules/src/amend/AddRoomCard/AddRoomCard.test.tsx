import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area } from '@whitbread-eos/api';

import { render, userEvent } from '../../utils/test-utils';
import { getAmendPromotionsInfo } from '../utilities';
import {
  mockedHotelAvailabilityParams,
  mockedRoomRules,
  mockedRoomsAndGuestsLabels,
} from '../utilities/mockResponse';
import AddRoomCard from './AddRoomCard.component';

jest.mock('../utilities', () => ({
  ...jest.requireActual('../utilities'),
  getAmendPromotionsInfo: jest.fn(),
}));

const mockGetAmendPromotionsInfo = getAmendPromotionsInfo as jest.Mock;

const openModal = jest.fn();
const closeModal = jest.fn();
const setPromoRoomsData = jest.fn();
const setIsAddRoomActionInProgress = jest.fn();

const props = {
  roomRules: mockedRoomRules,
  labels: mockedRoomsAndGuestsLabels,
  language: 'en',
  baseDataTestId: 'amend',
  hotelAvailabilityParams: mockedHotelAvailabilityParams,
  onSaveNewRoom: jest.fn(),
  variant: Area.PI,
  brand: 'pi',
  channel: 'WEB',
  hotelCountry: 'United Kingdom (the)',
  setPromoRoomsData,
  isModalOpen: false,
  openModal,
  closeModal,
  isPromoCodeLandingPageEnabled: false,
  isAddRoomActionInProgress: false,
  setIsAddRoomActionInProgress,
};

const mockPromoResponse = {
  promotionsInformation: {
    showPromo: false,
    isWithinPromoWindow: null,
    promotionCode: null,
    landingPage: '',
    promoBannerColour: '#511E62',
    promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
    promoBannerTitle: "<span style='color: #FDB913;'><b>Summer Sale: 10% off</b></span>",
    promoBannerSubtitle:
      '<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promoAmendMessage: '<b>Your booking includes a promotion.</b> Cancel this booking and rebook.',
    promoBookingInfo: {
      promotionCode: '',
    },
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useGetDiscountRateComapnyId: () => jest.fn(),
  graphQLRequest: jest.fn().mockResolvedValue(mockPromoResponse),
}));

const Component = () => {
  const baseProps = {
    roomRules: mockedRoomRules,
    labels: mockedRoomsAndGuestsLabels,
    language: 'en',
    baseDataTestId: 'amend',
    hotelAvailabilityParams: mockedHotelAvailabilityParams,
    onSaveNewRoom: jest.fn(),
    variant: Area.PI,
    brand: 'pi',
    channel: 'WEB',
    hotelCountry: 'United Kingdom (the)',
    setPromoRoomsData: jest.fn(),
    isModalOpen: false,
    openModal: jest.fn(),
    closeModal: jest.fn(),
    isPromoCodeLandingPageEnabled: false,
    isAddRoomActionInProgress: false,
    setIsAddRoomActionInProgress: jest.fn(),
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <AddRoomCard {...baseProps} />
    </QueryClientProvider>
  );
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => jest.fn(),
}));

describe('AddRoomCard', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    openModal.mockClear();
  });

  it('should display the modal after pressing the card', async () => {
    const { getByText, getByRole } = render(<Component />);
    const addARoomCard = getByText('+ Add a room');
    expect(addARoomCard).toBeInTheDocument();
    await userEvent.click(addARoomCard);
    await (() => {
      expect(getByRole('dialog')).toBeVisible();
    });
  });

  it('should close the modal after pressing the Cancel button', async () => {
    const { getByText, getByRole } = render(<Component />);
    const addARoomCard = getByText('+ Add a room');

    await userEvent.click(addARoomCard);
    await (() => {
      const closeBtn = getByRole('button', { name: 'Cancel' });
      userEvent.click(closeBtn);
      expect(getByRole('dialog')).not.toBeVisible();
    });
  });

  it('should call setPromoRoomsData with promotion info', async () => {
    mockGetAmendPromotionsInfo.mockResolvedValue({
      showPromo: false,
      promoBookingInfo: {
        promotionCode: '',
      },
    });

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(setPromoRoomsData).toHaveBeenCalledWith(
      expect.objectContaining({
        showPromo: false,
      })
    );
  });

  it('should open modal when no promotion exists', async () => {
    mockGetAmendPromotionsInfo.mockResolvedValue({
      showPromo: false,
      promoBookingInfo: {
        promotionCode: '',
      },
    });

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(openModal).toHaveBeenCalled();
  });

  it('should not open modal when promo code exists', async () => {
    mockGetAmendPromotionsInfo.mockResolvedValue({
      showPromo: true,
      promoBookingInfo: {
        promotionCode: 'SAVE10',
      },
    });

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(openModal).not.toHaveBeenCalled();
  });

  it('should handle getAmendPromotionsInfo error', async () => {
    const consoleSpy = jest.spyOn(console, 'log').mockImplementation(() => {});

    mockGetAmendPromotionsInfo.mockRejectedValue(new Error('promotion error'));

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(consoleSpy).toHaveBeenCalled();

    consoleSpy.mockRestore();
  });

  it('should call getAmendPromotionsInfo with expected parameters', async () => {
    mockGetAmendPromotionsInfo.mockResolvedValue({
      showPromo: false,
      promoBookingInfo: {
        promotionCode: '',
      },
    });

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(mockGetAmendPromotionsInfo).toHaveBeenCalledWith(
      expect.objectContaining({
        hotelId: mockedHotelAvailabilityParams.hotelId,
        arrival: mockedHotelAvailabilityParams.arrival,
        departure: mockedHotelAvailabilityParams.departure,
        brand: 'pi',
        language: 'en',
      })
    );
  });

  it('should prevent click when add room action is already in progress', async () => {
    mockGetAmendPromotionsInfo.mockResolvedValue({
      showPromo: false,
      promoBookingInfo: { promotionCode: '' },
    });

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} isAddRoomActionInProgress={true} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(mockGetAmendPromotionsInfo).not.toHaveBeenCalled();
    expect(openModal).not.toHaveBeenCalled();
    expect(setIsAddRoomActionInProgress).not.toHaveBeenCalled();
  });

  it('should toggle add room action progress around API call', async () => {
    mockGetAmendPromotionsInfo.mockResolvedValue({
      showPromo: false,
      promoBookingInfo: { promotionCode: '' },
    });

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <AddRoomCard {...props} />
      </QueryClientProvider>
    );

    await userEvent.click(getByTestId('add-room-card-amend'));

    expect(setIsAddRoomActionInProgress).toHaveBeenNthCalledWith(1, true);
    expect(setIsAddRoomActionInProgress).toHaveBeenLastCalledWith(false);
  });
});
