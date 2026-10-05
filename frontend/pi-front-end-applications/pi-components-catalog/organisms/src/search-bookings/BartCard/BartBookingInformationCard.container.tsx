import type { FlexProps, TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import {
  BART_BOOKING_INFORMATION,
  BartBookingInformation,
  BartBookingDataPackages,
  BartPackage,
  BartHotelDetails,
  GET_HOTEL_INFORMATION,
  GET_ROOM_TYPE_INFORMATION_QUERY,
} from '@whitbread-eos/api';
import { Card } from '@whitbread-eos/atoms';
import { BartHotelDetailsComponent } from '@whitbread-eos/molecules';
import { hotelInformationSelector, useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import BartBookingCardDetails from './BartBookingInformationCard.component';

interface Props {
  bookerLastName?: string;
  arrivalDate?: string;
  bookingReference: string;
  bartId?: string | null;
  sourcePms: string;
  t: (id: string) => string;
}

export default function BartBookingInformationCard({
  bookingReference,
  bookerLastName,
  arrivalDate,
  sourcePms,
  bartId,
  t,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const baseDataTestId = 'BartCard-Container';
  const [roomTypes, setRoomTypes] = useState([]);
  const [hotelData, setHotelData] = useState<BartHotelDetails>({
    hotelInformation: {
      brand: '',
      galleryImages: [{ alt: '', thumbnailSrc: '' }],
      links: { detailsPage: '' },
      parkingDescription: '',
      address: '',
    },
  });
  const [bookingData, setBookingData] = useState<BartBookingInformation>({
    bookingType: '',
    rateName: '',
    rooms: [],
    rateMessage: '',
    totalCost: { amount: 0, currencyCode: '' },
    paymentOption: '',
    balanceOutstanding: { amount: 0, currencyCode: '' },
    donations: { amount: 0, currencyCode: '' },
    prepaidAmount: { amount: 0, currencyCode: '' },
    prepaid: false,
    rateDescription: '',
    hasCityTax: false,
  });

  const getExtrasBart = (packages: BartPackage[], id: string) => {
    type keyIn = keyof BartPackage;
    type keyOut = keyof BartBookingDataPackages;
    const sorted: BartBookingDataPackages[] = [];
    packages.forEach((pack) => {
      if (
        sorted.some((val) => {
          return val[id as keyOut] == pack[id as keyIn];
        })
      ) {
        sorted.forEach((sp) => {
          if (sp[id as keyOut] === pack[id as keyIn]) {
            sp['days']++;
          }
        });
      } else {
        const obj: BartBookingDataPackages = {
          id: '',
          qty: 0,
          name: '',
          cost: { amount: 0, currencyCode: '' },
          days: 0,
        };
        obj.id = pack[id as keyIn] as string;
        obj.qty = pack.quantity;
        obj.name = pack.name;
        obj.cost = pack.unitCost;
        obj.days = 1;
        sorted.push(obj);
      }
    });
    return sorted;
  };

  const {
    isLoading: roomInformationLoading,
    isError: roomInformationIsError,
    error: roomInformationError,
    data: roomInformationData,
  } = useQueryRequest(
    ['GetRoomTypeInformation', country, language],
    GET_ROOM_TYPE_INFORMATION_QUERY,
    {
      brand: 'PI',
      country,
      language,
    }
  );

  const {
    isLoading: hotelIsLoading,
    isError: hotelIsError,
    error: hotelError,
    data: hotelDataRaw,
  } = useQueryRequest(['GetHotelInformation', bartId, country, language], GET_HOTEL_INFORMATION, {
    hotelId: bartId,
    country,
    language,
  });

  const {
    isLoading: bookingDataIsLoading,
    isError: bookingDataIsError,
    error: bookingDataError,
    data: bookingDataRaw,
  } = useQueryRequest(
    ['GetBartBookingInformation', bookingReference, bookerLastName, arrivalDate, country, language],
    BART_BOOKING_INFORMATION,
    {
      bookingReference: bookingReference,
      bookerLastName,
      arrivalDate,
      country,
      language,
    },
    {
      enabled: !!roomTypes,
    }
  );
  useEffect(() => {
    if (roomInformationData) {
      setRoomTypes(roomInformationData.roomTypeInformation.roomTypes);
    }
  }, [roomInformationData]);

  useEffect(() => {
    if (hotelDataRaw) {
      const data = {
        ...hotelDataRaw,
        hotelInformation: {
          ...hotelDataRaw.hotelInformation,
          address: hotelInformationSelector(hotelDataRaw?.hotelInformation)?.hotelAddress.join(
            ', '
          ),
        },
      };
      setHotelData(data);
    }
  }, [hotelDataRaw]);

  useEffect(() => {
    if (bookingDataRaw) {
      const data = bookingDataRaw.bartBookingInformation;
      setBookingData({
        bookingType: data.bookingType,
        rateName: data.rateName,
        rooms: data.rooms,
        rateMessage: data.rateMessage,
        totalCost: data.totalCost,
        donations: data.donations,
        balanceOutstanding: data.balanceOutstanding,
        prepaidAmount: data.prepaidAmount,
        paymentOption: data.paymentOption,
        prepaid: data?.prepaid ?? false,
        rateDescription: data?.rateDescription ?? '',
        hasCityTax: data.hasCityTax,
      });
    }
  }, [bookingDataRaw]);

  return (
    <Card {...cardWrapperStyle}>
      <Flex {...hotelDetailsWrapperStyle}>
        <Flex {...bookingReferenceContainerStyle}>
          <Text {...bookingReferenceTitleStyle}>{t('booking.confirmation.bookingReference')}</Text>
          <Text {...bookingReferenceStyle}>{bookingReference}</Text>
        </Flex>
        <BartHotelDetailsComponent
          {...{
            data: hotelData,
            error: hotelError,
            isError: hotelIsError,
            isLoading: hotelIsLoading,
          }}
          t={t}
        />
      </Flex>
      <BartBookingCardDetails
        roomTypes={roomTypes}
        getExtrasBart={getExtrasBart}
        bookingData={bookingData}
        isLoading={roomInformationLoading || bookingDataIsLoading}
        isError={roomInformationIsError || bookingDataIsError}
        error={roomInformationError || bookingDataError}
        sourcePms={sourcePms}
        baseDataTestId={baseDataTestId}
        bartId={bartId}
      />
    </Card>
  );
}

const hotelDetailsWrapperStyle = {
  flexDirection: 'row',
} as FlexProps;

const cardWrapperStyle = {
  boxShadow: 'none',
  justifyContent: 'space-between',
  flexDirection: 'row',
  padding: 'lg',
} as FlexProps;

const bookingReferenceStyle = {
  color: 'darkGrey2',
  fontSize: '3xl',
  fontWeight: 'normal',
  lineHeight: '1',
} as TextProps;

const bookingReferenceTitleStyle = {
  color: 'primary',
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'normal',
  mt: 'sm',
} as TextProps;

const bookingReferenceContainerStyle = {
  rowGap: 'sm',
  flexDirection: 'column',
  mt: 'md',
} as FlexProps;
