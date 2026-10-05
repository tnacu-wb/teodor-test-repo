import type {
  HIRoomClassCode,
  HIAvailabilityRates,
  HIRoomRate,
  HIRoomType,
  Channel,
  HIRateClassification,
  HIRoomTypeInfoResponse,
  RoomOffer,
} from '@whitbread-eos/api';
import { FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE, FT_PI_HDP_URGENCY_BANNER } from '@whitbread-eos/api';
import { RateCard } from '@whitbread-eos/molecules';
import {
  getAvailableRoomTypes,
  getRoomClassByRoomClassCode,
  getRateClassification,
  getRoomRatesThatMatchRoomClassifications,
  useCustomLocale,
  getRoomClassByCodeAndType,
  useFeatureToggle,
  useStaticHotelInformation,
  analytics,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  roomClassCodes: HIRoomClassCode[];
  data: HIAvailabilityRates;
  allRoomTypes: HIRoomType[];
  brand: string;
  channel: Channel;
  selectedRoomClassAndRate: string;
  setSelectedRoomClassAndRate: (selectedClassAndRate: string) => void;
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  isLessThanSm: boolean | undefined;
  isLessThanMd: boolean | undefined;
  basketData: {
    hotelId: string;
    arrival: string;
    departure: string;
    numberOfUnits: number;
    numberOfNights: number;
  };
}

export default function RateCards({
  roomClassCodes,
  data,
  brand,
  channel,
  selectedRoomClassAndRate,
  setSelectedRoomClassAndRate,
  roomTypeInformationResponse,
  isLessThanSm,
  isLessThanMd,
  basketData,
  allRoomTypes,
}: Readonly<Props>) {
  const { language } = useCustomLocale();
  const { t } = useTranslation(['common']);
  const roomRates = data?.hotelAvailability?.roomRates || [];
  const roomTypes = getAvailableRoomTypes(allRoomTypes);

  const {
    [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: isPremPlusAccFeatureFlag,
    [FT_PI_HDP_URGENCY_BANNER]: isUrgencyBannerEnabled,
  } = useFeatureToggle();
  const { roomClassConfiguration, roomConfiguration } = useStaticHotelInformation();
  const renderAdditionalRateCardInfo = isPremPlusAccFeatureFlag
    ? roomTypes?.length === 1
    : !(roomTypes?.length > 1 || (basketData?.numberOfUnits > 1 && roomClassCodes?.length > 1));

  const roomsOffered: RoomOffer[] = [];
  if (typeof window !== 'undefined') {
    analytics.update({ roomsOffered: roomsOffered });
  }
  return (
    <>
      {roomClassCodes?.map((roomClassCode) => {
        const byRoomClassCode = (roomRate: HIRoomRate): boolean => {
          return roomRate?.roomTypes?.every((roomType) =>
            roomType?.rooms?.some((room) => room?.roomClass === roomClassCode)
          );
        };
        const roomRatesByRoomClassCode = roomRates?.filter(byRoomClassCode);
        const roomClass = getRoomClassByRoomClassCode(roomClassCode, language);
        const rateClassifications = roomRates?.map((roomRate) =>
          getRateClassification(roomRate?.ratePlanCode, data?.ratesInformation?.rateClassifications)
        );
        const matchingRoom = roomRatesByRoomClassCode?.[0]?.roomTypes?.[0]?.rooms?.find(
          (room) => room?.roomClass === roomClassCode
        );
        const pmsRoomType = matchingRoom?.pmsRoomType ?? '';

        if (!pmsRoomType) return;
        const roomType = roomRatesByRoomClassCode?.[0]?.roomTypes?.[0]?.roomType || '';
        const numberOfRoomsAvailable = matchingRoom?.numberOfRoomsAvailable;
        const roomOfferData = {
          roomClass: roomClassCode,

          roomType: getRoomClassByCodeAndType(
            roomClass,
            roomClassCode,
            roomType,
            roomTypes,
            t,
            isPremPlusAccFeatureFlag,
            roomClassConfiguration ?? []
          ),

          roomsLeft:
            numberOfRoomsAvailable == null
              ? null
              : numberOfRoomsAvailable > 10
                ? null
                : numberOfRoomsAvailable,

          position: roomsOffered.length + 1,

          rates: roomRatesByRoomClassCode.map((roomRate) => {
            const rateClassification = rateClassifications.find(
              (el) =>
                el?.rateClassification === roomRate?.ratePlanCode ||
                el?.ratePlanCode === roomRate?.ratePlanCode
            );

            return {
              rateName: rateClassification?.rateName ?? '',
              rateCode: roomRate?.ratePlanCode ?? '',
              price: roomRate?.roomTypes
                ?.map(
                  (roomType) =>
                    roomType.rooms.filter((room) => room?.roomClass === roomClassCode)[0]
                      ?.roomPriceBreakdown?.totalNetAmount
                )
                ?.reduce((sum, pricePerRoomType) => sum + (pricePerRoomType ?? 0), 0),
              currency:
                roomRate?.roomTypes?.[0]?.rooms?.[0]?.roomPriceBreakdown?.currencyCode ?? '',
            };
          }),

          isSubstitution: matchingRoom?.isSubstitution ?? false,
          substitutionCode: matchingRoom?.substitution ?? null,
        };
        roomsOffered.push(roomOfferData);

        return (
          <RateCard
            allRoomsStaticDetails={roomConfiguration}
            brand={brand}
            channel={channel}
            key={roomClass}
            roomClassCode={roomClassCode}
            roomClass={getRoomClassByCodeAndType(
              roomClass,
              roomClassCode,
              roomType,
              roomTypes,
              t,
              isPremPlusAccFeatureFlag,
              roomClassConfiguration ?? []
            )}
            pmsRoomType={pmsRoomType}
            roomRates={roomRates}
            rateClassifications={rateClassifications as HIRateClassification[]}
            selectedRoomClassAndRate={selectedRoomClassAndRate}
            setSelectedRoomClassAndRate={setSelectedRoomClassAndRate}
            roomTypeInformationResponse={roomTypeInformationResponse}
            isLessThanSm={isLessThanSm}
            isLessThanMd={isLessThanMd}
            isUrgencyBannerEnabled={isUrgencyBannerEnabled}
            numberOfNights={basketData.numberOfNights}
            numberOfUnits={basketData.numberOfUnits}
            roomTypes={roomTypes}
            showAdditionalInfo={renderAdditionalRateCardInfo}
            numberOfRoomsAvailable={numberOfRoomsAvailable}
            roomRatesThatMatchRoomClassifications={getRoomRatesThatMatchRoomClassifications(
              rateClassifications,
              roomRatesByRoomClassCode
            )}
          />
        );
      })}
    </>
  );
}
