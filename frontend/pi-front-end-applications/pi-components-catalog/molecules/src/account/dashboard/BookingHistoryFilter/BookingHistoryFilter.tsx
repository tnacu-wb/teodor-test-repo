import { Box, Flex, Text } from '@chakra-ui/react';
import { ScreenSizeValues } from '@whitbread-eos/api';
import { Button, datepickerDate, Input, SingleDatePicker } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  hasValidCharacters,
  isAlphabetic,
  isNumber,
  useCustomLocale,
} from '@whitbread-eos/utils';
import { add, format, sub } from 'date-fns';
import { useSearchParams } from 'next/navigation';
import { useState, useLayoutEffect } from 'react';

import { DATE_FORMAT } from '../../../utils/constants';
import {
  buttonsWrapperStyles,
  clearButton,
  clearTextStyle,
  containerStyles,
  containerWithErrorStyles,
  datepickerInputStyle,
  inputElementStyles,
  inputIconStyles,
  inputStyles,
  inputWithErrorStyles,
  paragraphStyles,
  searchInputStyles,
  setFindButtonStyles,
} from './BookingHistoryFilter.style';

export interface BookingHistoryFilterParams {
  filterValue: string;
  filterType: string;
}

interface Props {
  t: (id: string) => string;
  onClear: () => void;
  onFind: (params: BookingHistoryFilterParams) => void;
  screenSize?: ScreenSizeValues;
  baseTestId: string;
}

enum FILTER_OPTIONS {
  ARRIVAL = 'ARRIVAL_DATE',
  SURNAME = 'NAME',
  BOOKING_REFERENCE = 'CONFIRM_NUMBER',
}

const CHARS_LIMIT = 30;

export default function BookingHistoryFilter({
  t,
  onClear,
  onFind,
  baseTestId,
  screenSize = {
    isLessThanMobile: false,
    isLessThanXs: false,
    isLessThanSm: false,
    isLessThanMd: false,
    isLessThanLg: false,
    isLessThanXl: false,
  },
}: Readonly<Props>) {
  const maxDate = add(new Date(), { days: 365 });
  const minDate = sub(new Date(), {
    days: 90,
  });
  const { isLessThanLg } = screenSize;
  const searchParams = useSearchParams();
  const bookingReference: string | null = searchParams?.get('reference') ?? null;

  const [date, setDate] = useState<Date | null>(null);

  const [inputValue, setInputValue] = useState<string>('');

  const [inputError, setInputError] = useState<string>('');

  const [searchWasMade, setSearchWasMade] = useState<boolean>(false);

  const isFindEnabled: boolean = (inputValue && inputValue.length > 0) || date !== null;

  const isDatepickerEnabled: boolean = inputValue.length <= 0;

  const isBookingRefInputEnabled: boolean = date === null;

  const { language } = useCustomLocale();

  useLayoutEffect(() => {
    if (bookingReference) {
      setInputValue(bookingReference);
      setSearchWasMade(true);
      onFind({ filterValue: bookingReference, filterType: FILTER_OPTIONS.BOOKING_REFERENCE });
    }
  }, [bookingReference]);

  const onInputChange = (value: string) => {
    if (inputError) {
      setInputError('');
    }
    setInputValue(value);
  };

  const triggerFind = (): void => {
    const trimmedInputValue = inputValue.trim();
    if (date !== null) {
      onFind({ filterValue: format(date, 'yyyy-MM-dd'), filterType: FILTER_OPTIONS.ARRIVAL });
    } else if (isAlphabetic(trimmedInputValue)) {
      onFind({
        filterValue: trimmedInputValue,
        filterType: FILTER_OPTIONS.SURNAME,
      });
    } else {
      onFind({ filterValue: trimmedInputValue, filterType: FILTER_OPTIONS.BOOKING_REFERENCE });
    }
  };

  const handleFind = () => {
    const trimmedInputvalue = inputValue.trim();

    if (inputValue && trimmedInputvalue.length > CHARS_LIMIT) {
      setInputError(t('dashboard.bookings.maximumCharactersPermitted'));
    } else if (inputValue && isNumber(trimmedInputvalue)) {
      setInputError(t('dashboard.bookings.invalidBookingReference'));
    } else if (inputValue && !hasValidCharacters(trimmedInputvalue)) {
      setInputError(t('dashboard.bookings.invalidCharacters'));
    } else {
      setSearchWasMade(true);
      triggerFind();
    }
  };

  const handleClear = () => {
    if (searchWasMade) {
      onClear();
    }
    setInputValue('');
    setInputError('');
    setDate(null);
  };

  const containerWrapperStyles =
    !isLessThanLg && inputError
      ? { ...containerStyles, ...containerWithErrorStyles }
      : { ...containerStyles };

  const searchInputWrapperStyles =
    isLessThanLg && inputError
      ? { ...searchInputStyles, ...inputWithErrorStyles }
      : { ...searchInputStyles };

  return (
    <>
      <Text as="h6" {...paragraphStyles} data-testid="filters-description">
        {t('dashboard.bookings.searchBarText')}
      </Text>

      <Flex flexDirection={'row'} flexWrap={'wrap'} {...containerWrapperStyles}>
        <Box {...searchInputWrapperStyles}>
          <Input
            type="text"
            name="bookingFilter"
            onChange={onInputChange}
            placeholderText={t('dashboard.bookings.bookingQuery')}
            label={t('dashboard.bookings.bookingQuery')}
            error={inputError}
            value={inputValue}
            isDisabled={!isBookingRefInputEnabled}
            data-testid={formatDataTestId(baseTestId, 'bookingReferenceOrSurname')}
            styles={{ inputElementStyles }}
            isInputAriaRequired={true}
          />
        </Box>
        <Box {...datepickerInputStyle}>
          <SingleDatePicker
            minDate={minDate}
            maxDate={maxDate}
            inputPlaceholder={t('dashboard.bookings.filterDatePlaceholder')}
            name={'bookingHistoryDatepicker'}
            inputLabel={''}
            datepickerStyles={{
              inputGroupStyles: {},
              datepickerInputElementStyles: inputStyles,
              bookingDatepickerSize: {},
              iconStyles: inputIconStyles,
            }}
            defaultStartDate={date ?? null}
            displayDateFormat={DATE_FORMAT}
            dateFormat={DATE_FORMAT}
            onSelectDate={(date: datepickerDate) => {
              setDate(date);
            }}
            labels={{
              todayLabel: '',
              tomorrowLabel: '',
            }}
            skipFormatRules={true}
            locale={language}
            data-testid={formatDataTestId(baseTestId, 'datePicker')}
            isDisabled={!isDatepickerEnabled}
          />
        </Box>
        <Box {...buttonsWrapperStyles}>
          <Button
            variant="primary"
            size="md"
            onClick={handleFind}
            isDisabled={!isFindEnabled}
            data-testid={formatDataTestId(baseTestId, 'findButton')}
            {...setFindButtonStyles(language)}
          >
            {t('dashboard.bookings.findButton')}
          </Button>

          <Box title="clear-search-button" {...clearButton}>
            <Button
              variant="secondary"
              {...clearTextStyle}
              onClick={() => handleClear()}
              data-testid={formatDataTestId(baseTestId, 'clearButton')}
            >
              {t('dashboard.bookings.clearSearchButton')}
            </Button>
          </Box>
        </Box>
      </Flex>
    </>
  );
}
