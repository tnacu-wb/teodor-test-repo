import {
  GET_HOTEL_INVENTORY_QUERY,
  GET_ROOM_TYPE_INFORMATION_QUERY,
  HIAEMroomTypesInfo,
  HIHotelInventory,
  HIHotelInventoryResponse,
  HIRoomTypeInfoResponse,
} from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest, useStaticHotelInformation } from '@whitbread-eos/utils';

import HotelRoomsComponent from './HotelRooms.component';

interface Props {
  isPremierInn: boolean;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  isLessThanLg: boolean | undefined;
  arrival?: string;
  departure?: string;
  isDisplayRates?: boolean;
}

function PremierInnHotelRooms({
  isPremierInn,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  isDisplayRates,
}: Readonly<Props>) {
  const { roomConfiguration, isLoading, isError, error, brand } = useStaticHotelInformation();

  return (
    <HotelRoomsComponent
      {...{
        isLoading,
        isError,
        error,
        data: roomConfiguration,
        isPremierInn,
        isLessThanSm,
        isLessThanMd,
        isLessThanLg,
        isDisplayRates,
        brand,
      }}
    />
  );
}

function CCUIHotelRooms({
  isPremierInn,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  arrival: dateRangeStart,
  departure: dateRangeEnd,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();

  const { roomConfiguration, hotelId, brand, isLoading, isError, error } =
    useStaticHotelInformation();

  // dateRangeStart and dateRangeEnd can be undefined if the user accesses HDP from
  // an external link without any query params in URL
  // in this case we are stoping the query from being called
  const queryEnabled = dateRangeStart !== undefined && dateRangeEnd !== undefined;

  const {
    isLoading: isLoadingHotelInventory,
    isError: isErrorHotelInventory,
    data: dataHotelInventory,
    error: errorHotelInventory,
  } = useQueryRequest(
    ['getHotelInventory', hotelId, dateRangeEnd, dateRangeStart],
    GET_HOTEL_INVENTORY_QUERY,
    {
      hotelId,
      dateRangeEnd,
      dateRangeStart,
    },
    {
      gcTime: 0,
      enabled: !!queryEnabled,
    }
  );

  const hotelInventoryResponse: HIHotelInventoryResponse = {
    isLoadingHotelInventory,
    isErrorHotelInventory,
    dataHotelInventory: dataHotelInventory as HIHotelInventory,
    errorHotelInventory,
  };

  const {
    isLoading: isLoadingRoomTypeInformation,
    isError: isErrorRoomTypeInformation,
    data: dataRoomTypeInformation,
    error: errorRoomTypeInformation,
  } = useQueryRequest(
    ['getRoomTypeInformation', language, country, brand, hotelId],
    GET_ROOM_TYPE_INFORMATION_QUERY,
    {
      language,
      country,
      brand,
      hotelId,
    }
  );

  const roomTypeInformationResponse: HIRoomTypeInfoResponse = {
    isLoadingRoomTypeInformation,
    isErrorRoomTypeInformation,
    dataRoomTypeInformation: dataRoomTypeInformation as HIAEMroomTypesInfo,
    errorRoomTypeInformation,
  };

  return (
    <HotelRoomsComponent
      {...{
        isLoading,
        isError,
        error,
        data: roomConfiguration,
        hotelInventoryResponse,
        roomTypeInformationResponse,
        isPremierInn,
        isLessThanSm,
        isLessThanMd,
        isLessThanLg,
        brand,
      }}
    />
  );
}

export default function HotelRoomsContainer({
  isPremierInn,
  isLessThanSm,
  isLessThanMd,
  isLessThanLg,
  arrival,
  departure,
  isDisplayRates,
}: Props) {
  return isPremierInn ? (
    <PremierInnHotelRooms
      {...{
        isPremierInn,
        isLessThanSm,
        isLessThanMd,
        isLessThanLg,
        isDisplayRates,
      }}
    />
  ) : (
    <CCUIHotelRooms
      {...{
        isPremierInn,
        isLessThanSm,
        isLessThanMd,
        isLessThanLg,
        arrival,
        departure,
      }}
    />
  );
}
