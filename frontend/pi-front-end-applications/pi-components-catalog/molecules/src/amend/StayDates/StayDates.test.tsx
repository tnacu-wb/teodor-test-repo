import { InputGroup } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area, Channel, DATE_TYPE, PageName } from '@whitbread-eos/api';
import { PromotionsNotification } from '@whitbread-eos/atoms';
import { useFeatureToggle } from '@whitbread-eos/utils';
import { add, format } from 'date-fns';

import { act, fireEvent, render, waitFor } from '../../utils/test-utils';
import { getPromotionsInformation } from '../utilities';
import { mockedStayDatesParams } from '../utilities/mockResponse';
import StayDates, { amendStayDatesPromo, handleNightsDropdownUtility } from './StayDates.component';

const mockedOnAmendStayDates = jest.fn();
const mockedOnAmendStayDatesPromo = jest.fn();
const mockPromoResponse = {
  promotionsInformation: {
    showPromo: true,
    isWithinPromoWindow: true,
    promotionCode: 'ST20RU',
    landingPage: '',
    promoBannerColour: '#511E62',
    promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
    promoBannerTitle: "<span style='color: #FDB913;'><b>Summer Sale: 20% off</b></span>",
    promoBannerSubtitle:
      '<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promoAmendMessage: '<b>Your booking includes a promotion.</b> Cancel this booking and rebook.',
  },
};

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  PromotionsNotification: jest.fn(() => <div data-testid="mock-promotions-notification" />),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn().mockResolvedValue(mockPromoResponse),
  useFeatureToggle: jest.fn(),
  getGQLClient: jest.fn(),
  getNightsNumber: jest.fn(() => 1),
  isSameDate: jest.fn(() => false),
}));
(useFeatureToggle as jest.Mock).mockReturnValue({
  FT_PI_PROMO_CODE_LANDING_PAGE: false,
});
jest.mock('../utilities', () => ({
  ...jest.requireActual('../utilities'),
  getPromotionsInformation: jest.fn(),
  handleCheckDateIsSame: jest.fn(),
}));

const mockedProps = {
  data: {
    ...mockedStayDatesParams.data,
    originalDepartureDate: mockedStayDatesParams.data.departureDate,
  },
  labels: mockedStayDatesParams.labels,
  baseDataTestId: 'amend',
  language: 'en',
  onAmendStayDates: mockedOnAmendStayDates,
  promoStayData: {
    promoBookingInfo: { promotionCode: 'PROMO123', ratePlanCode: null },
    showPromo: true,
    isWithinPromoWindow: true,
    promotionCode: 'PROMO123',
    landingPage: '',
    promoBannerColour: '#511E62',
    promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
    promoBannerTitle: 'Test Promo',
    promoBannerSubtitle: 'Test Subtitle',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promoAmendMessage: null,
  },
  setPromoStayData: mockedOnAmendStayDatesPromo,
  isCancellable: true,
  isLoading: false,
  variant: Area.PI,
  promotionsData: mockedStayDatesParams.promotionsData,
  country: 'gb',
  brand: 'pi',
  basketReference: 'BASKET123',
  showPromoNotification: false,
  setShowPromoNotification: jest.fn(),
  isPromoCodeLandingPageEnabled: false,
  channel: Channel.Pi,
};

describe('StayDates', () => {
  const mockProps = {
    data: {
      hotelName: 'Test Hotel',
      arrivalDate: new Date('2025-10-10'),
      departureDate: new Date('2025-10-12'),
      maxNights: 5,
      maxArrivalDate: 10,
      maxRooms: 10,
      originalArrivalDate: new Date('2025-10-10'),
      originalDepartureDate: new Date('2025-10-12'),
    },
    labels: {
      hotel: 'Hotel',
      arrivalDate: 'Arrival',
      nightsLabel: 'Nights',
      checkOut: 'Check-out',
      nightOption: 'night',
      nightsOption: 'nights',
      yourStayDatesTitle: 'Your Stay Dates',
      numberOfNightsErrorMessage: 'Error',
      invalidNights: 'Invalid',
    },
    country: 'EN',
    brand: 'Premier Inn',
    baseDataTestId: 'test',
    language: 'EN',
    isCancellable: true,
    onAmendStayDates: jest.fn(),
    isLoading: false,
    variant: Area.CCUI,
    basketReference: 'BASKET123',
    promoStayData: {
      promoBookingInfo: { promotionCode: 'PROMO123', ratePlanCode: null },
      showPromo: true,
      isWithinPromoWindow: true,
      promotionCode: 'PROMO123',
      landingPage: '',
      promoBannerColour: '#511E62',
      promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
      promoBannerTitle: 'Test Promo',
      promoBannerSubtitle: 'Test Subtitle',
      promoInvalidMessage: null,
      promoExpiredMessage: null,
      promoAmendMessage: null,
    },
    setPromoStayData: jest.fn(),
    showPromoNotification: true,
    setShowPromoNotification: jest.fn(),
    isPromoCodeLandingPageEnabled: false,
    channel: Channel.Ccui,
  };

  it('renders PromotionsNotification when showPromoNotification is true', () => {
    const { getByTestId } = render(<StayDates {...mockProps} />);

    const promoElement = getByTestId('mock-promotions-notification');
    expect(promoElement).toBeInTheDocument();

    expect(PromotionsNotification).toHaveBeenCalledWith(
      expect.objectContaining({
        page: PageName.AMEND,
        promotionBannerData: mockProps.promoStayData,
        elementName: PageName.STAY_DATES,
      }),
      undefined
    );
  });

  it('does NOT render PromotionsNotification when showPromoNotification is false', () => {
    const { queryByTestId } = render(<StayDates {...mockProps} showPromoNotification={false} />);
    expect(queryByTestId('mock-promotions-notification')).not.toBeInTheDocument();
  });
});

describe('Stay Dates section - Amend page PI', () => {
  it('should render the LoadingSpinner if the data is loading', async () => {
    const { getByTestId } = render(
      <InputGroup>
        <StayDates {...mockedProps} isLoading />{' '}
      </InputGroup>
    );

    await waitFor(() => {
      const loadingSpinner = getByTestId('loading-spinner');
      expect(loadingSpinner).toBeInTheDocument();
    });
  });

  it('should check empty HotelNameInput value', () => {
    const { getByTestId } = render(
      <InputGroup>
        <StayDates {...mockedProps} />
      </InputGroup>
    );
    const input = getByTestId('Amend-StayDates-HotelNameInput');

    act(() => {
      fireEvent.change(input, { target: { value: '' } });
    });

    expect(input.textContent).toBe('');
  });

  it('should change Datepicker value after date selecting', () => {
    const { getByLabelText } = render(
      <InputGroup>
        <StayDates {...mockedProps} />
      </InputGroup>
    );
    const arrivalDateInput = getByLabelText('datepicker-input');
    const newArrival = add(new Date(), { days: 10 });

    act(() => {
      fireEvent.change(arrivalDateInput, {
        target: { value: newArrival },
      });
    });

    expect(arrivalDateInput).toHaveValue(format(newArrival, DATE_TYPE.EEE_DAY_MONTH_YEAR));
  });

  it('should keep the original arrivalDate if the date has not been changed', () => {
    const { getByLabelText } = render(
      <InputGroup>
        <StayDates {...mockedProps} />
      </InputGroup>
    );
    const arrivalDateInput = getByLabelText('datepicker-input');
    const originalArrivalDate = mockedProps.data.originalArrivalDate;

    act(() => {
      fireEvent.change(arrivalDateInput);
    });

    expect(arrivalDateInput).toHaveValue(format(originalArrivalDate, DATE_TYPE.EEE_DAY_MONTH_YEAR));
  });

  it('should change Nights Dropdown value', () => {
    const { getByTestId } = render(
      <InputGroup>
        <StayDates {...mockedProps} />
      </InputGroup>
    );
    const twoNightsOption = getByTestId('DropdownComp-amend-stay-dates-nights-1');

    act(() => {
      fireEvent.click(twoNightsOption);
    });

    expect(twoNightsOption.textContent).toBe('2 nights');
  });
});

describe('Stay Dates section - Amend page CCUI', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the NumberOfNightsInput instead Dropdown while surfing on CCUI Amend', () => {
    const { getByRole, queryByRole } = render(
      <InputGroup>
        <StayDates {...mockedProps} variant={Area.CCUI} language="de" />
      </InputGroup>
    );

    expect(queryByRole('button', { name: '1 night' })).not.toBeInTheDocument();
    expect(getByRole('spinbutton')).toHaveValue('1');
  });

  it('should change the number of nights value while surfing on CCUI Amend and typing in Input', async () => {
    const { getByRole } = render(
      <InputGroup>
        <StayDates {...mockedProps} variant={Area.CCUI} />
      </InputGroup>
    );
    const numberOfNightsInput = getByRole('spinbutton', { name: 'datepicker-input' });

    await waitFor(() => {
      fireEvent.change(numberOfNightsInput, { target: { value: 8 } });
    });

    expect(numberOfNightsInput).toHaveValue('8');
  });

  it('should change the departure date if the nights have been changed after input blurring', async () => {
    const { getByRole } = render(
      <InputGroup>
        <StayDates {...mockedProps} variant={Area.CCUI} channel={Channel.Ccui} />
      </InputGroup>
    );
    const numberOfNightsInput = getByRole('spinbutton', { name: 'datepicker-input' });
    const arrivalDate = mockedProps.data.arrivalDate;

    await waitFor(() => {
      fireEvent.change(numberOfNightsInput, { target: { value: 6 } });
    });
    fireEvent.blur(numberOfNightsInput);

    const departureDate = add(arrivalDate, { days: 6 });
    expect(mockedOnAmendStayDates).toHaveBeenCalledWith(arrivalDate, departureDate);
  });

  it('should display an errorMessage and not call onAmendStayDates if the nights exceeded the maxNights', async () => {
    const { getByRole, getByText } = render(
      <InputGroup>
        <StayDates {...mockedProps} variant={Area.CCUI} />
      </InputGroup>
    );
    const numberOfNightsInput = getByRole('spinbutton', { name: 'datepicker-input' });

    await waitFor(() => {
      fireEvent.change(numberOfNightsInput, { target: { value: 400 } });
    });
    fireEvent.blur(numberOfNightsInput);

    expect(getByText(mockedProps.labels.numberOfNightsErrorMessage)).toBeInTheDocument();
    expect(mockedOnAmendStayDates).not.toHaveBeenCalled();
  });

  it('should display invalidNights error message if the value is set to 0', async () => {
    const { getByRole, getByText } = render(
      <InputGroup>
        <StayDates {...mockedProps} variant={Area.CCUI} />
      </InputGroup>
    );
    const numberOfNightsInput = getByRole('spinbutton', { name: 'datepicker-input' });

    await waitFor(() => {
      fireEvent.change(numberOfNightsInput, { target: { value: '+' } });
    });
    fireEvent.blur(numberOfNightsInput);

    expect(getByText(mockedProps.labels.invalidNights)).toBeInTheDocument();
  });
});

describe('amendStayDatesPromo utility', () => {
  const mockedOnAmendStayDates = jest.fn();
  const mockedSetArrival = jest.fn();
  const mockedSetPromoStayData = jest.fn();
  const mockedSetShowPromoNotification = jest.fn();
  const mockSetIsPromoLoading = jest.fn();
  const baseArgs = {
    arrival: new Date('2025-10-10'),
    endDate: new Date('2025-10-12'),
    originalArrivalDate: new Date('2025-10-08'),
    originalDepartureDate: new Date('2025-10-10'),
    onAmendStayDates: mockedOnAmendStayDates,
    setArrival: mockedSetArrival,
    setPromoStayData: mockedSetPromoStayData,
    setShowPromoNotification: mockedSetShowPromoNotification,
    setIsPromoLoading: mockSetIsPromoLoading,
    country: 'GB',
    language: 'en',
    brand: 'pi',
    basketReference: 'BASKET123',
    queryClient: {} as QueryClient,
    client: {},
    channel: Channel.Pi,
  };
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls onAmendStayDates directly if promo feature is disabled', async () => {
    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: false,
    });

    expect(mockedOnAmendStayDates).toHaveBeenCalledWith(baseArgs.arrival, baseArgs.endDate);
    expect(mockedSetArrival).toHaveBeenCalledWith(baseArgs.arrival);
    expect(getPromotionsInformation).not.toHaveBeenCalled();
  });

  it('returns early when getPromotionsInformation returns null', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce(null);

    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: true,
    });

    expect(mockSetIsPromoLoading).toHaveBeenNthCalledWith(1, true);
    expect(getPromotionsInformation).toHaveBeenCalled();
    expect(mockSetIsPromoLoading).toHaveBeenLastCalledWith(false);
  });

  it('should amend stay dates directly when promotions in hotel availability feature is enabled', async () => {
    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: true,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    expect(mockedSetArrival).toHaveBeenCalledWith(baseArgs.arrival);

    expect(mockedOnAmendStayDates).toHaveBeenCalledWith(baseArgs.arrival, baseArgs.endDate);

    expect(getPromotionsInformation).not.toHaveBeenCalled();

    expect(mockSetIsPromoLoading).not.toHaveBeenCalled();
  });

  it('handles promo available within window (showPromo + isWithinPromoWindow)', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce({
      promoBookingInfo: { promotionCode: 'SAVE20' },
      showPromo: true,
      isWithinPromoWindow: true,
    });

    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: true,
    });

    expect(mockedSetPromoStayData).toHaveBeenCalled();
    expect(mockedOnAmendStayDates).toHaveBeenCalled();
    expect(mockedSetArrival).toHaveBeenCalledWith(baseArgs.arrival);
    expect(mockedSetShowPromoNotification).toHaveBeenCalledWith(false);
  });

  it('handles promo not within window (showPromo = false)', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce({
      promoBookingInfo: { promotionCode: 'SAVE20' },
      showPromo: false,
      isWithinPromoWindow: false,
    });

    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: true,
    });

    expect(mockedSetPromoStayData).toHaveBeenCalled();
    expect(mockedSetShowPromoNotification).toHaveBeenCalledWith(true);
    expect(mockedSetArrival).toHaveBeenCalledWith(baseArgs.originalArrivalDate);
  });

  it('handles case where promoBookingInfo has no promotionCode', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce({
      promoBookingInfo: {},
    });

    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: true,
    });

    expect(mockedOnAmendStayDates).toHaveBeenCalledWith(baseArgs.arrival, baseArgs.endDate);
    expect(mockedSetArrival).toHaveBeenCalledWith(baseArgs.arrival);
  });

  it('logs error and still calls setIsPromoLoading(false) when getPromotionsInformation throws', async () => {
    (getPromotionsInformation as jest.Mock).mockRejectedValueOnce(new Error('Network error'));

    await amendStayDatesPromo({
      ...baseArgs,
      isPromoCodeLandingPageEnabled: true,
    });

    expect(mockSetIsPromoLoading).toHaveBeenLastCalledWith(false);
  });
});

describe('handleNightsDropdownUtility', () => {
  const mockSetSelectedNightOption = jest.fn();
  const mockOnAmendStayDates = jest.fn();
  const mockSetShowPromoNotification = jest.fn();
  const mockSetPromoStayData = jest.fn();
  const mockSetIsPromoLoading = jest.fn();
  const mockQueryClient = {} as QueryClient;
  const mockClient = {};

  const baseParams = {
    arrival: new Date('2025-10-15'),
    originalArrivalDate: new Date('2025-10-15'),
    originalDepartureDate: new Date('2025-10-17'),
    country: 'GB',
    language: 'en',
    brand: 'pi',
    basketReference: 'BASKET123',
    queryClient: mockQueryClient,
    client: mockClient,
    option: { id: 2, label: '2 nights' } as any,
    setSelectedNightOption: mockSetSelectedNightOption,
    onAmendStayDates: mockOnAmendStayDates,
    setShowPromoNotification: mockSetShowPromoNotification,
    setPromoStayData: mockSetPromoStayData,
    setIsPromoLoading: mockSetIsPromoLoading,
    channel: Channel.Pi,
    nightsInput: 2,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should do nothing if option is undefined', async () => {
    await handleNightsDropdownUtility({
      ...baseParams,
      option: undefined,
      isPromoCodeLandingPageEnabled: false,
    });
    expect(mockSetSelectedNightOption).not.toHaveBeenCalled();
    expect(mockOnAmendStayDates).not.toHaveBeenCalled();
  });

  it('should call onAmendStayDates directly when promo is disabled', async () => {
    await handleNightsDropdownUtility({ ...baseParams, isPromoCodeLandingPageEnabled: false });

    const expectedEndDate = add(baseParams.arrival, { days: 2 });
    expect(mockSetSelectedNightOption).toHaveBeenCalledWith(baseParams.option);
    expect(mockOnAmendStayDates).toHaveBeenCalledWith(baseParams.arrival, expectedEndDate);
  });

  it('should handle promo available and within promo window', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBookingInfo: { promotionCode: 'SAVE20' },
      showPromo: true,
      isWithinPromoWindow: true,
    });

    await handleNightsDropdownUtility({ ...baseParams, isPromoCodeLandingPageEnabled: true });

    const expectedEndDate = add(baseParams.arrival, { days: 2 });
    expect(mockSetIsPromoLoading).toHaveBeenCalledWith(true);
    expect(mockSetPromoStayData).toHaveBeenCalled();
    expect(mockOnAmendStayDates).toHaveBeenCalledWith(baseParams.arrival, expectedEndDate);
    expect(mockSetShowPromoNotification).toHaveBeenCalledWith(false);
    expect(mockSetIsPromoLoading).toHaveBeenCalledWith(false);
  });

  it('should amend nights directly when promotions in hotel availability feature is enabled', async () => {
    await handleNightsDropdownUtility({
      ...baseParams,
      isPromoCodeLandingPageEnabled: true,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    const expectedEndDate = add(baseParams.arrival, {
      days: Number(baseParams.option.id),
    });

    expect(mockSetSelectedNightOption).toHaveBeenCalledWith(baseParams.option);

    expect(mockOnAmendStayDates).toHaveBeenCalledWith(baseParams.arrival, expectedEndDate);

    expect(getPromotionsInformation).not.toHaveBeenCalled();

    expect(mockSetIsPromoLoading).not.toHaveBeenCalled();
  });

  it('should not set selected night option for CCUI when feature flag is enabled', async () => {
    await handleNightsDropdownUtility({
      ...baseParams,
      channel: Channel.Ccui,
      nightsInput: 3,
      isPromoCodeLandingPageEnabled: true,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    const expectedEndDate = add(baseParams.arrival, {
      days: 3,
    });

    expect(mockSetSelectedNightOption).not.toHaveBeenCalled();

    expect(mockOnAmendStayDates).toHaveBeenCalledWith(baseParams.arrival, expectedEndDate);

    expect(getPromotionsInformation).not.toHaveBeenCalled();
  });

  it('should show promo notification if outside promo window and clear promo', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBookingInfo: { promotionCode: 'SAVE20' },
      showPromo: false,
      isWithinPromoWindow: false,
    });

    await handleNightsDropdownUtility({ ...baseParams, isPromoCodeLandingPageEnabled: true });

    expect(mockSetShowPromoNotification).toHaveBeenCalledWith(true);

    jest.runAllTimers();
    expect(mockSetPromoStayData).toHaveBeenCalledTimes(2);
  });

  it('should call onAmendStayDates if no promoBookingInfo', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({});

    await handleNightsDropdownUtility({ ...baseParams, isPromoCodeLandingPageEnabled: true });

    const expectedEndDate = add(baseParams.arrival, { days: 2 });
    expect(mockSetSelectedNightOption).toHaveBeenCalledWith(baseParams.option);
    expect(mockOnAmendStayDates).toHaveBeenCalledWith(baseParams.arrival, expectedEndDate);
    expect(mockSetIsPromoLoading).toHaveBeenCalledWith(false);
  });

  it('should handle errors and still reset loading state', async () => {
    (getPromotionsInformation as jest.Mock).mockRejectedValue(new Error('Network error'));

    await handleNightsDropdownUtility({ ...baseParams, isPromoCodeLandingPageEnabled: true });

    expect(mockSetIsPromoLoading).toHaveBeenCalledWith(false);
  });
});
