import {
  FT_PI_DISCOUNT_RATE,
  type Company,
  type DatepickerSelectionDate,
  type ObjKeyAccessType,
  type PromotionOffer,
  type ScreenSize,
  type SearchAEMTranslationsType,
  type SearchPartialTranslationsType,
  type SearchRequestParamsType,
  type SearchRoomCodes,
  type SearchRoomOccupancyLimitationsType,
  type SearchRoomType,
  type SearchStayRulesResponseType,
  type SearchSuggestions,
} from '@whitbread-eos/api';
import { CompanySearch, NumberOfNights, Promotion, Search } from '@whitbread-eos/molecules';
import { useFeatureToggle } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useState } from 'react';

interface Props {
  AEMTranslations: SearchAEMTranslationsType;
  locale?: string;
  screenSize: ScreenSize;
  suggestions: SearchSuggestions;
  defaultLocation?: string;
  defaultRooms?: SearchRoomType[];
  ARRdd?: number;
  ARRmm?: number;
  ROOMS?: number;
  ARRyyyy?: number;
  NIGHTS?: number;
  onLocationInputChange?: (value: string | undefined) => void;
  onSetErrorMessage?: (value: boolean) => void;
  showErrorMessage?: { location: boolean };
  noOfNightsError: string;
  onLocationInputClear?: () => void;
  onInputLocationFocus?: (value: string | undefined) => void;
  dataStayRules: SearchStayRulesResponseType;
  dataRoomOccupancyLimitations: SearchRoomOccupancyLimitationsType;
  partialTranslations: SearchPartialTranslationsType;
  roomCodes: SearchRoomCodes;
  searchStyles?: any;
  handleButtonClick: (queryParams: SearchRequestParamsType | undefined) => void | Promise<any>;
  onIsSearchActive?: (param: boolean) => void;
  isSearchActive: boolean;
  mappedRoomLabels: ObjKeyAccessType;
  startDate: Date | null;
  endDate: Date | null;
  handleChangeNumberOfNights: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onSelectDates: (props: DatepickerSelectionDate) => void;
  errorField?: string;
  errorMessage?: string;
  savedNights: number;
  isDatepickerError?: boolean;
  onOccupancyChange?: () => void;
  channel?: string;
  isDatePickerFocus?: boolean;
  hideErrorForMinNights?: boolean;
  displayDatesNotification?: boolean;
  contractRateCompanyState: [Company | null, Dispatch<SetStateAction<Company | null>>];
  isNegotiatedRatesByCompIdEnabled?: boolean;
  isBarrierFreeLabelEnabled?: boolean;
  isActiveMatchedOffer?: boolean;
  matchedOffer?: PromotionOffer;
}

export default function SearchComponent({
  locale,
  onLocationInputChange,
  onInputLocationFocus,
  onSetErrorMessage,
  showErrorMessage,
  noOfNightsError,
  suggestions,
  defaultLocation,
  defaultRooms,
  ARRdd,
  ARRmm,
  ARRyyyy,
  ROOMS,
  NIGHTS,
  AEMTranslations,
  screenSize,
  dataStayRules,
  dataRoomOccupancyLimitations,
  partialTranslations,
  roomCodes,
  handleButtonClick,
  searchStyles,
  isSearchActive,
  onIsSearchActive,
  mappedRoomLabels,
  startDate,
  endDate,
  handleChangeNumberOfNights,
  onSelectDates,
  errorField,
  errorMessage,
  savedNights,
  isDatepickerError,
  onLocationInputClear,
  onOccupancyChange,
  channel,
  isDatePickerFocus,
  hideErrorForMinNights,
  displayDatesNotification,
  contractRateCompanyState,
  isNegotiatedRatesByCompIdEnabled = false,
  isActiveMatchedOffer,
  isBarrierFreeLabelEnabled,
  matchedOffer,
}: Readonly<Props>) {
  const { isLessThanSm } = screenSize;
  const [searchDisabled, setSearchDisabled] = useState(false);
  const { [FT_PI_DISCOUNT_RATE]: isDiscountRateEnabled } = useFeatureToggle();

  const companyNameField = (
    <CompanySearch
      showCompanyIdInput={isNegotiatedRatesByCompIdEnabled}
      key="CompanyName"
      setSearchDisabled={setSearchDisabled}
      contractRateCompanyState={contractRateCompanyState}
    />
  );

  return (
    <Search
      {...{
        locale,
        defaultLocation,
        defaultRooms,
        ARRdd,
        ARRmm,
        ARRyyyy,
        ROOMS,
        NIGHTS,
        onLocationInputChange,
        onLocationInputClear,
        onInputLocationFocus,
        onSetErrorMessage,
        showErrorMessage,
        suggestions,
        dataStayRules,
        nrOfNights: savedNights,
        dataRoomOccupancyLimitations,
        partialTranslations,
        roomCodes,
        handleButtonClick,
        AEMTranslations,
        screenSize,
        searchStyles,
        isSearchActive,
        onIsSearchActive,
        mappedRoomLabels,
        startDate,
        endDate,
        onSelectDates,
        errorField,
        errorMessage,
        isDatepickerError,
        onOccupancyChange,
        channel,
        isDatePickerFocus,
        displayDatesNotification,
        searchDisabled,
        isActiveMatchedOffer,
        matchedOffer,
        isDiscountRateEnabled,
        isBarrierFreeLabelEnabled,
      }}
      numberOfNightsComponent={
        <NumberOfNights
          inputPlaceholder={AEMTranslations.numberOfNightsPlaceholder ?? ''}
          inputValue={savedNights}
          handleOnChange={handleChangeNumberOfNights}
          noOfNightsError={noOfNightsError}
          isLessThanSm={isLessThanSm}
          maxNights={dataStayRules.maxNightsLimitation.maxNights}
          key="NumberOfNights"
          hideErrorForMinNights={hideErrorForMinNights}
        />
      }
      companyNameComponent={companyNameField}
      promotionComponent={
        <Promotion
          inputPlaceholder={AEMTranslations.promotionCategoryPlaceholder ?? ''}
          key="Promotion"
        />
      }
    />
  );
}
