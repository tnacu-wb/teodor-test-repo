/* eslint-disable @typescript-eslint/ban-ts-comment */

/* eslint-disable prettier/prettier */
'use client';

import { Language, SearchRoomType } from '@whitbread-eos/api';
import { useTranslation } from '@whitbread-eos/utils';
import { staticHotelInformationIB, getMultiSearchParamsIB } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useSearchParams, useParams } from 'next/navigation';
import React, { useEffect, useState, MouseEventHandler } from 'react';

/* eslint-disable prettier/prettier */

interface EditSearchTypes {
  location: string;
  adults: number;
  children: number;
  nights: number;
  arrival: Date;
  roomTypeCount: Record<string, number>;
}

interface Props {
  searchIcon: string;
  handleEditClick: MouseEventHandler;
  language: Language;
}

const EditSearch = ({
  searchIcon,
  handleEditClick = () => {
    return;
  },
  language,
}: Readonly<Props>) => {
  const { t } = useTranslation();
  const { slug } = useParams();

  const searchParams = useSearchParams();
  const searchParamsObj: Record<string, string> = {};
  searchParams.forEach((value, key) => {
    searchParamsObj[key] = value;
  });
  const roomTypeTranslations = {
    DB: t('content.global.double'),
    DIS: t('content.global.accessible'),
    FAM: t('content.global.family'),
    SB: t('content.global.single'),
    TWIN: t('content.global.twin'),
  } as Record<string, string>;
  const hotelFullSlug = slug ? `/hotels/${Array.isArray(slug) ? slug.join('/') : slug}` : undefined;

  const [searchData, setSearchData] = useState<EditSearchTypes>({
    location: '',
    adults: 0,
    children: 0,
    nights: 0,
    arrival: new Date(),
    roomTypeCount: {},
  });

  const locationFromUrl = searchParamsObj['searchModel.searchTerm'] ?? '';

  const parseRooms = (rooms: Array<SearchRoomType>) => {
    let totalAdults = 0;
    let totalChildren = 0;
    const roomsCount = {} as Record<string, number>;

    rooms?.map((room) => {
      totalAdults += room.adults;
      totalChildren += room.children;

      if (room.roomType !== undefined) {
        if (roomsCount[room.roomType]) {
          roomsCount[room.roomType] += 1;
        } else {
          roomsCount[room.roomType] = 1;
        }
      }
    });

    setSearchData((prevState) => ({
      ...prevState,
      adults: totalAdults,
      children: totalChildren,
      roomTypeCount: roomsCount,
    }));
  };

  useEffect(() => {
    const getData = async () => {
      if (hotelFullSlug) {
        const hotelInformationSlug = await staticHotelInformationIB(hotelFullSlug, language);
        setSearchData((prevState) => ({
          ...prevState,
          location: hotelInformationSlug?.name ?? '',
        }));
      }
    };
    getData();
  }, [hotelFullSlug, locationFromUrl, language]);

  useEffect(() => {
    if (hotelFullSlug || locationFromUrl) {
      const {
        arrival: arrivalFromUrl,
        numberOfNights,
        rooms: roomsFromUrl,
      } = getMultiSearchParamsIB(searchParamsObj);
      setSearchData((prevState) => ({
        ...prevState,
        location: locationFromUrl,
        nights: numberOfNights,
        arrival: new Date(arrivalFromUrl),
      }));
      parseRooms(roomsFromUrl);
    }
  }, [hotelFullSlug, locationFromUrl]);

  const dateText = `${
    t('content.form.datePicker.weekdaysShort')?.[searchData.arrival.getDay() - 1]
  } ${searchData.arrival.getDate()} ${t('content.form.datePicker.months')?.[
    searchData.arrival.getMonth()
  ]?.slice(0, 3)} ${searchData.arrival.getFullYear()}`;
  const adultsText = `, ${searchData.adults} ${
    searchData.adults === 1 ? t('content.global.adult') : t('content.global.adults')
  }`;
  const childrenText = `, ${searchData.children} ${
    searchData.children === 1 ? t('content.global.child') : t('content.global.children')
  }`;
  const nightsText = `, ${searchData.nights} ${
    searchData.nights === 1 ? t('content.global.night') : t('content.global.nights')
  }`;

  const roomTypeText = Object.entries(searchData.roomTypeCount).map(
    ([type, count]) =>
      `, ${count} ${roomTypeTranslations[type]} ${
        count === 1 ? t('content.global.room') : t('content.global.rooms')
      }`
  );

  return (
    <div className={editSearchContainerStyle} data-testid="Edit-Search-IB">
      <div className={editSearchTopStyle}>
        <Image src={searchIcon} alt={'Search Details Icon'} width={24} height={24} />
        <span className={editSearchLocationStyle} data-testid="Edit-Search-Location">
          {searchData.location}
        </span>
        <button
          className={editSearchButtonStyle}
          data-testid="Edit-Search-Button"
          onClick={handleEditClick as MouseEventHandler}
        >
          {t('content.form.searchEdit')}
        </button>
      </div>
      <span className={editSearchBottomStyle} data-testid="Edit-Search-Details">
        {dateText}
        {adultsText}
        {searchData.children ? childrenText : ''}
        {nightsText}
        {roomTypeText}
      </span>
    </div>
  );
};

export default EditSearch;

const editSearchContainerStyle = 'hidden mobile:flex flex-col p-4 gap-2 mobile:w-full';
const editSearchTopStyle = 'flex gap-4';
const editSearchLocationStyle = 'font-semibold truncate';
const editSearchButtonStyle = 'ml-[auto] underline text-secondaryColor';
const editSearchBottomStyle = 'border-b border-lightGrey3 pb-4';
