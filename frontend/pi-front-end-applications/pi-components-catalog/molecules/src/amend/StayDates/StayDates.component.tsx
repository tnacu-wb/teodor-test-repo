import type { FlexProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import { QueryClient, useQueryClient } from '@tanstack/react-query';
import {
  Area,
  DATE_TYPE,
  DatepickerSelectionDate,
  StayDatesLabels,
  StayDatesType,
  CountryCode,
  PageName,
  Channel,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import {
  Datepicker,
  Dropdown,
  DropdownOption,
  LoadingSpinner,
  PromotionsNotification,
} from '@whitbread-eos/atoms';
import {
  getGQLClient,
  getNightsNumber,
  isSameDate,
  ONE_YEAR_IN_DAYS,
  PromotionsInformation,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { add, format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import { useState } from 'react';

import NumberOfNights from '../../search/NumberOfNights';
import HotelNameInput from '../HotelNameInput';
import { getPromotionsInformation } from '../utilities';
import { handleCheckDateIsSame } from '../utilities/helpers';

interface Props {
  data: StayDatesType;
  labels: StayDatesLabels;
  country: string;
  brand: string;
  baseDataTestId: string;
  language: string;
  isCancellable: boolean;
  onAmendStayDates: (newStartDate: Date, newEndDate: Date) => void;
  isLoading?: boolean;
  variant: Area;
  basketReference: string;
  promoStayData: PromotionsInformation | null;
  setPromoStayData: (data: PromotionsInformation | null) => void;
  showPromoNotification: boolean;
  setShowPromoNotification: (show: boolean) => void;
  isPromoCodeLandingPageEnabled: boolean;
  channel: Channel;
}

export default function StayDates({
  data,
  labels,
  baseDataTestId,
  country,
  brand,
  language,
  onAmendStayDates,
  isLoading,
  isCancellable,
  variant,
  basketReference,
  promoStayData,
  setPromoStayData,
  showPromoNotification,
  setShowPromoNotification,
  isPromoCodeLandingPageEnabled,
  channel,
}: Readonly<Props>) {
  const {
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const prefixDataTestId = 'stay-dates';
  const {
    hotelName,
    arrivalDate,
    departureDate,
    maxNights,
    maxArrivalDate,
    originalArrivalDate,
    originalDepartureDate,
  } = data;
  const maxDate = add(new Date(), { days: maxArrivalDate });
  const queryClient = useQueryClient();
  const client = getGQLClient();
  const numberOfNights = getNightsNumber(
    format(arrivalDate, DATE_TYPE.YEAR_MONTH_DAY),
    format(departureDate, DATE_TYPE.YEAR_MONTH_DAY)
  );

  const nightsOptions = getNightsOptions(maxNights, labels);
  const [selectedNightOption, setSelectedNightOption] = useState({
    label: '',
    id: numberOfNights,
  } as DropdownOption);

  const [isPromoLoading, setIsPromoLoading] = useState(false);
  const [arrival, setArrival] = useState(arrivalDate);
  const [nightsInput, setNightsInput] = useState(numberOfNights);
  const nightsValue = variant === Area.CCUI ? Number(nightsInput) : Number(selectedNightOption.id);
  const checkOut = format(
    add(arrival, {
      days: nightsValue,
    }),
    DATE_TYPE.EEE_DAY_MONTH_YEAR,
    {
      locale: language === CountryCode.EN ? enGB : de,
    }
  );
  const noOfNightsError = nightsInput ? labels.numberOfNightsErrorMessage : labels.invalidNights;

  const onAmendStayDatesPromo = (arrival: Date, endDate: Date) => {
    return amendStayDatesPromo({
      arrival,
      endDate,
      originalArrivalDate,
      originalDepartureDate,
      isPromoCodeLandingPageEnabled,
      onAmendStayDates,
      setArrival,
      setPromoStayData,
      setShowPromoNotification,
      setIsPromoLoading,
      country,
      language,
      brand,
      channel,
      basketReference,
      queryClient,
      client,
      isPromotionsInHotelAvailabilityEnabled,
    });
  };

  async function handleNightsDropdown(option: DropdownOption | undefined) {
    await handleNightsDropdownUtility({
      option,
      arrival,
      originalArrivalDate,
      originalDepartureDate,
      country,
      language,
      brand,
      basketReference,
      queryClient,
      client,
      isPromoCodeLandingPageEnabled,
      setSelectedNightOption,
      onAmendStayDates,
      setShowPromoNotification,
      setPromoStayData,
      setIsPromoLoading,
      nightsInput,
      channel,
      isPromotionsInHotelAvailabilityEnabled,
    });
  }

  function handleNightsInput() {
    if (nightsInput > maxNights || nightsInput === numberOfNights || nightsInput === 0) return;

    handleNightsDropdownUtility({
      option: selectedNightOption,
      arrival,
      originalArrivalDate,
      originalDepartureDate,
      country,
      language,
      brand,
      basketReference,
      queryClient,
      client,
      isPromoCodeLandingPageEnabled,
      setSelectedNightOption,
      onAmendStayDates,
      setShowPromoNotification,
      setPromoStayData,
      setIsPromoLoading,
      nightsInput,
      channel,
      isPromotionsInHotelAvailabilityEnabled,
    });
  }

  return (
    <>
      {isLoading || isPromoLoading ? (
        <LoadingSpinner />
      ) : (
        <Box
          className="stay-dates-container"
          data-testid={`${baseDataTestId}-${prefixDataTestId}-section`}
          {...wrapperStyles}
        >
          {showPromoNotification && (
            <PromotionsNotification
              page={PageName.AMEND}
              promotionBannerData={promoStayData as PromotionsInformation}
              elementName={PageName.STAY_DATES}
            />
          )}
          <Box>
            <Text
              data-testid={`${baseDataTestId}-${prefixDataTestId}-hotel-label`}
              {...hotelLabelStyles}
            >
              {labels.hotel}
            </Text>
            <HotelNameInput
              hotelName={hotelName}
              onInputChange={() => {
                // This is a placeholder
                return;
              }}
            />
          </Box>
          <Flex {...datesWrapperStyles}>
            <Box {...arrivalDateWrapperStyles}>
              <Text
                data-testid={`${baseDataTestId}-${prefixDataTestId}-arrival-date-label`}
                {...dateLabelsStyles}
              >
                {labels.arrivalDate}
              </Text>
              <Datepicker
                locale={language}
                dateFormat={DATE_TYPE.EEE_DAY_MONTH_YEAR}
                datepickerStyles={datepickerStyles}
                displayDateFormat={DATE_TYPE.EEE_DAY_MONTH_YEAR}
                minDate={new Date()}
                maxDate={maxDate}
                defaultStartDate={arrival}
                closeCalendarOnSelectDate={true}
                onSelectDates={(date: DatepickerSelectionDate) => {
                  const startDate = (date as Date) || originalArrivalDate;
                  const endDate = add(startDate, { days: nightsValue });
                  setPromoStayData(null);
                  if (isSameDate(startDate, arrival)) return;
                  onAmendStayDatesPromo(startDate, endDate);
                }}
              />
            </Box>
            <Box {...numberOfNightsWrapperStyles}>
              <Text
                data-testid={`${baseDataTestId}-${prefixDataTestId}-nights-label`}
                {...dateLabelsStyles}
              >
                {labels.nightsLabel}
              </Text>
              {variant === Area.CCUI ? (
                <NumberOfNights
                  inputValue={isNaN(nightsInput) ? 0 : nightsInput}
                  handleOnChange={(e) => setNightsInput(Number(e.target.value))}
                  handleOnBlur={() => {
                    handleNightsInput();
                  }}
                  noOfNightsError={noOfNightsError}
                  maxNights={maxNights}
                  key="NumberOfNights"
                  styles={{
                    inputElementStyles: noOfNightsInputElementStyles,
                    iconStyles,
                    tooltipStyles,
                  }}
                  isDisabled={!isCancellable}
                />
              ) : (
                <Dropdown
                  dataTestId={`${baseDataTestId}-${prefixDataTestId}-nights`}
                  options={nightsOptions}
                  selectedId={selectedNightOption?.id.toString()}
                  dropdownStyles={dropdownStyles}
                  disabled={!isCancellable}
                  onChange={handleNightsDropdown}
                  matchWidth
                />
              )}
            </Box>
            <Flex {...departureDateWrapperStyles}>
              <Text
                data-testid={`${baseDataTestId}-${prefixDataTestId}-checkOutLabel`}
                {...dateLabelsStyles}
                fontWeight="400"
                paddingBottom="0"
                paddingRight={{ mobile: 'sm', md: '0' }}
              >
                {labels.checkOut}
              </Text>
              <Text
                data-testid={`${baseDataTestId}-${prefixDataTestId}-checkOutDate`}
                {...dateLabelsStyles}
                paddingBottom="0"
              >
                {checkOut}
              </Text>
            </Flex>
          </Flex>
        </Box>
      )}
    </>
  );
}

export function getNightsOptions(nights: number, labels: StayDatesLabels) {
  const options = [];
  for (let idx = 0; idx < nights && idx < ONE_YEAR_IN_DAYS; idx++) {
    options.push(idx + 1);
  }

  return options.map((element) => ({
    label: `${element} ${getNightsLabel(element, labels)}`,
    id: element,
  }));
}

export function getNightsLabel(nights: number, labels: StayDatesLabels) {
  if (nights === 1) {
    return labels.nightOption;
  }
  return labels.nightsOption;
}

export async function amendStayDatesPromo({
  arrival,
  endDate,
  originalArrivalDate,
  originalDepartureDate,
  isPromoCodeLandingPageEnabled,
  onAmendStayDates,
  setArrival,
  setPromoStayData,
  setShowPromoNotification,
  setIsPromoLoading,
  country,
  language,
  brand,
  channel,
  basketReference,
  queryClient,
  client,
  isPromotionsInHotelAvailabilityEnabled,
}: {
  arrival: Date;
  endDate: Date;
  originalArrivalDate: Date;
  originalDepartureDate: Date;
  isPromoCodeLandingPageEnabled: boolean;
  onAmendStayDates: (start: Date, end: Date) => void;
  setArrival: (date: Date) => void;
  setPromoStayData: (data: any) => void;
  setShowPromoNotification: (show: boolean) => void;
  setIsPromoLoading: (show: boolean) => void;
  country: string;
  language: string;
  brand: string;
  channel: Channel;
  basketReference: string;
  queryClient: any;
  client: any;
  isPromotionsInHotelAvailabilityEnabled: boolean;
}) {
  handleCheckDateIsSame(originalArrivalDate, originalDepartureDate, arrival, endDate);

  if (isPromotionsInHotelAvailabilityEnabled) {
    setArrival(arrival);
    onAmendStayDates(arrival, endDate);
    return;
  }
  const handlePromoStayDates = (res: PromotionsInformation) => {
    if (res?.promoBookingInfo?.promotionCode) {
      setPromoStayData(res);
      if (res?.showPromo && res?.isWithinPromoWindow) {
        onAmendStayDates(arrival, endDate);
        setArrival(arrival);
        setShowPromoNotification(false);
      } else {
        setShowPromoNotification(true);
        setArrival(originalArrivalDate);
      }
    } else {
      onAmendStayDates(arrival, endDate);
      setArrival(arrival);
    }
  };
  if (isPromoCodeLandingPageEnabled) {
    setIsPromoLoading(true);
    try {
      const res = await getPromotionsInformation(
        arrival,
        endDate,
        country,
        language,
        brand,
        channel,
        basketReference,
        queryClient,
        client,
        isPromoCodeLandingPageEnabled
      );
      if (!res) return;
      handlePromoStayDates(res);
    } catch (e) {
      console.log(e);
    } finally {
      setIsPromoLoading(false);
    }
  } else {
    setArrival(arrival);
    onAmendStayDates(arrival, endDate);
  }
}

export async function handleNightsDropdownUtility({
  option,
  arrival,
  originalArrivalDate,
  originalDepartureDate,
  country,
  language,
  brand,
  basketReference,
  queryClient,
  client,
  isPromoCodeLandingPageEnabled,
  setSelectedNightOption,
  onAmendStayDates,
  setShowPromoNotification,
  setPromoStayData,
  setIsPromoLoading,
  nightsInput,
  channel,
  isPromotionsInHotelAvailabilityEnabled,
}: {
  option?: DropdownOption;
  arrival: Date;
  originalArrivalDate: Date;
  originalDepartureDate: Date;
  country: string;
  language: string;
  brand: string;
  channel: Channel;
  basketReference: string;
  queryClient: QueryClient;
  client: any;
  isPromoCodeLandingPageEnabled: boolean;
  setSelectedNightOption: (option: DropdownOption) => void;
  onAmendStayDates: (arrival: Date, endDate: Date) => void;
  setShowPromoNotification: (show: boolean) => void;
  setPromoStayData: (data: any) => void;
  setIsPromoLoading: (loading: boolean) => void;
  nightsInput: number;
  isPromotionsInHotelAvailabilityEnabled: boolean;
}) {
  const isCcui = channel === Channel.Ccui;
  if (!option && !isCcui) return;

  setPromoStayData(null);
  const nightsInputValue = !isCcui ? option?.id : nightsInput;
  const endDate = add(arrival, { days: Number(nightsInputValue) });

  handleCheckDateIsSame(originalArrivalDate, originalDepartureDate, arrival, endDate);

  if (isPromotionsInHotelAvailabilityEnabled) {
    if (option && !isCcui) {
      setSelectedNightOption(option);
    }
    onAmendStayDates(arrival, endDate);
    return;
  }
  const handlePromoCodeStayDates = (res: PromotionsInformation) => {
    if (res?.promoBookingInfo?.promotionCode) {
      setPromoStayData(res);

      if (res?.showPromo && res?.isWithinPromoWindow) {
        option && setSelectedNightOption(option);
        onAmendStayDates(arrival, endDate);
        setShowPromoNotification(false);
      } else {
        setShowPromoNotification(true);
      }
    } else {
      option && setSelectedNightOption(option);
      onAmendStayDates(arrival, endDate);
    }
  };

  if (isPromoCodeLandingPageEnabled) {
    setIsPromoLoading(true);

    try {
      const res = await getPromotionsInformation(
        arrival,
        endDate,
        country,
        language,
        brand,
        channel,
        basketReference,
        queryClient,
        client,
        isPromoCodeLandingPageEnabled
      );

      if (!res) return;
      handlePromoCodeStayDates(res);
    } catch (error) {
      console.error(error);
    } finally {
      setIsPromoLoading(false);
    }
  } else {
    if (option && !isCcui) {
      setSelectedNightOption(option);
    }
    onAmendStayDates(arrival, endDate);
  }
}

const dropdownStyles = {
  menuButtonStyles: {
    borderColor: 'lightGrey1',
    color: 'darkGrey2',
  },
  menuListStyles: {
    h: '12.5rem',
    pt: 0,
    pb: 0,
  },
  wrapperStyles: {
    zIndex: 3,
  },
};

const wrapperStyles = {
  px: 'var(--chakra-space-lg)',
  py: 'var(--chakra-space-2xl)',
  cursor: 'default',
};

const hotelLabelStyles = {
  fontSize: 'md',
  paddingBottom: '0.563rem',
  fontWeight: 'bold',
  lineHeight: '3',
  color: 'darkGrey2',
  marginTop: '1.5rem',
  minW: {
    mobile: '15rem',
    xs: '18.438rem',
  },
};

const datesWrapperStyles = {
  width: '100%',
  paddingTop: 'var(--chakra-space-2xl)',
  flexDirection: {
    mobile: 'column',
    md: 'row',
  },
  minW: {
    mobile: '15rem',
    xs: '18.438rem',
  },
} as FlexProps;
const arrivalDateWrapperStyles = {
  w: {
    mobile: '100%',
    md: '20.25rem',
    lg: '24.75rem',
  },
  marginRight: {
    mobile: 0,
    md: 'var(--chakra-space-lg)',
  },
  marginBottom: {
    mobile: 'var(--chakra-space-lg)',
    md: '0',
  },
};
const numberOfNightsWrapperStyles = {
  w: {
    mobile: '100%',
    md: '9rem',
  },
  marginRight: {
    mobile: 0,
    md: 'var(--chakra-space-lg)',
  },
  marginBottom: {
    mobile: 'var(--chakra-space-lg)',
    md: '0',
  },
};
const departureDateWrapperStyles = {
  alignSelf: {
    mobile: 'flex-start',
    md: 'flex-end',
  },
  flexDirection: {
    mobile: 'row',
    md: 'column',
  },
} as FlexProps;
const dateLabelsStyles = {
  fontSize: 'md',
  fontWeight: 'bold',
  lineHeight: '3',
  color: 'darkGrey2',
  paddingBottom: 'var(--chakra-space-sm)',
};

const datepickerStyles = {
  datepickerInputElementStyles: {
    border: '1px solid',
    borderColor: 'lightGrey1',
    borderRight: '1px solid',
    borderRightColor: 'lightGrey1',
    borderRadius: 'var(--chakra-space-xs)',
    borderTopRightRadius: 'var(--chakra-space-xs)',
    borderBottomRightRadius: 'var(--chakra-space-xs)',
    height: 'var(--chakra-space-4xl)',
    minW: {
      mobile: '15rem',
      xs: '18.438rem',
    },
  },
  inputGroupStyles: {
    height: 'var(--chakra-space-4xl)',
    borderColor: {
      mobile: 'transparent',
    },
  },
  iconStyles: {
    top: 'var(--chakra-space-sm)',
  },
};

const noOfNightsInputElementStyles = {
  border: '1px solid',
  borderColor: 'lightGrey1',
  borderRadius: 'base',
  _hover: { border: '1px solid var(--chakra-colors-darkGrey1)', borderRadius: 'base' },
  _focus: {
    border: '2px solid var(--chakra-colors-primary)',
    cursor: 'auto',
    borderRadius: 'base',
  },
  padding: 'var(--chakra-space-lg) var(--chakra-space-lg) var(--chakra-space-lg) 3.7rem',
  w: 'var(--chakra-space-36)',
  h: 'var(--chakra-space-4xl)',
};

const iconStyles = {
  top: 'var(--chakra-space-sm)',
  left: ' var(--chakra-space-md)',
};

const tooltipStyles = {
  w: '17rem',
  top: '2xl',
  fontSize: 'sm',
  lineHeight: '3',
  fontWeight: 'normal',
};
