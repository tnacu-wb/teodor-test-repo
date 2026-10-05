'use client';

import {
  FormInnB,
  SearchRequestParamsType,
  SearchRoomType,
  SearchPropertyType,
  SearchPlaceType,
} from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { getQueryParams } from '@whitbread-eos/utils/server';
import { useState, useEffect } from 'react';
import { DateRange } from 'react-day-picker';

interface Props {
  handleButtonClick: (queryParams: SearchRequestParamsType | undefined) => void | Promise<any>;
  rooms: Record<string, SearchRoomType>;
  location: SearchPropertyType | SearchPlaceType | Record<string, never>;
  selectedDate: DateRange | undefined;
  mobile?: boolean;
  formLabels: FormInnB | Record<string, never>;
  dateFromUrl: DateRange | undefined;
}

const SearchButton = ({
  handleButtonClick,
  rooms,
  location,
  selectedDate,
  dateFromUrl,
}: Readonly<Props>) => {
  const { t } = useTranslation();
  const [dateForQueryParams, setDateForQueryParams] = useState<DateRange | undefined>();

  const queryParams = getQueryParams(dateForQueryParams, location, rooms);
  useEffect(() => {
    if (!selectedDate?.from && !selectedDate?.to) {
      setDateForQueryParams(dateFromUrl);
      return;
    }
    setDateForQueryParams(selectedDate);
  }, [selectedDate, dateFromUrl]);

  return (
    <Button
      data-testid="IB-Search-Button"
      className={searchButtonStyle}
      onClick={() => handleButtonClick(queryParams)}
    >
      {t('content.form.search')}
    </Button>
  );
};

export default SearchButton;

const searchButtonStyle = `h-[2.5rem] mobile:h-14 mobile:w-full text-lg font-semibold rounded text-white bg-primaryColor disabled:bg-lightGrey3 disabled:text-lightGrey1 hover:bg-primaryColor/90`;
