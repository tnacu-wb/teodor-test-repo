import { Box, Flex, FlexProps, Heading } from '@chakra-ui/react';
import {
  HIAEMroomType,
  HIAvailabilityRates,
  HIRateClassification,
  HIRoom,
  UserChoice,
} from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

import BedDropdown from '../BedDropdown/BedDropdown.component';
import { RateNotificationsProps } from '../Notifications/RateNotifications/RateNotifications.component';
import OfferPicker from '../OfferPicker/OfferPicker.component';

export interface DropdownChangeEvent {
  id: string;
  icon: React.ReactElement;
  label: string;
  code?: string;
}

interface Props extends RateNotificationsProps {
  data: {
    hotelAvailability: HIAvailabilityRates;
    ratesInformation: HIAEMroomType[];
    rateClassifications: HIRateClassification[];
  };
  userChoice: UserChoice[];
  setUserChoice: (choice: any) => void;
  activeRate: string;
  setActiveRate: (rate: string) => void;
}

export default function ChoiceArchitecture({
  data,
  userChoice,
  setUserChoice,
  activeRate,
  setActiveRate,
  currentClassRoomTypes,
  isNonSilentSubstituNotificPerRoomClassEnabled,
  brand,
  roomTypeInformationResponse,
  hasAccessibleRoom,
  accessibilityInfo,
  cot,
  specialRoomLimitMessage,
}: Readonly<Props>) {
  const baseDataTestId = 'ChoiceArchitecture';
  const hotelAvailability = data?.hotelAvailability.hotelAvailability;
  const { t } = useTranslation();

  const [openRoomNumber, setOpenRoomNumber] = useState(1);
  const activeClass = hotelAvailability?.roomRates.find((rate) => rate.ratePlanCode === activeRate);
  const availableRoomTypes = new Set(
    activeClass?.roomTypes[openRoomNumber - 1]?.rooms.map((room) => room.pmsRoomType)
  );

  const onHandleClick = (option: string | string[]) => {
    setUserChoice((prevState: UserChoice[]) =>
      prevState.map((choice: UserChoice) =>
        choice.roomNumber === openRoomNumber
          ? {
              ...choice,
              pmsRoomType: Array.isArray(option)
                ? option.find((code) => availableRoomTypes.has(code))
                : option,
            }
          : choice
      )
    );
  };

  const handleRateClick = (rate: string) => {
    setActiveRate(rate);
  };

  const onHandleChange = (e: DropdownChangeEvent | null) => {
    if (!e) return;
    setUserChoice((prevState: UserChoice[]) =>
      updateUserChoice(prevState, e, openRoomNumber, activeClass?.roomTypes)
    );
  };

  return (
    <Flex direction="column" maxW="63.25rem">
      <OfferPicker
        data={data}
        activeRate={activeRate}
        handleRateClick={handleRateClick}
        brand={brand}
      />
      <Flex {...bedSectionStyle} data-testid={formatDataTestId(baseDataTestId, 'bedSection')}>
        <Heading
          as="h3"
          data-testid={formatDataTestId(baseDataTestId, 'heading')}
          {...sectionHeadingStyle}
        >
          <Flex {...stepNumberStyle} data-testid={formatDataTestId(baseDataTestId, 'stepNumber')}>
            2
          </Flex>
          {t('hoteldetails.rates.grid.selectRoom')}
        </Heading>
        {activeClass?.roomTypes?.map((room) => {
          return (
            <BedDropdown
              data={data}
              activeRate={activeRate}
              key={room.roomNumber}
              room={room}
              openRoomNumber={openRoomNumber}
              setOpenRoomNumber={setOpenRoomNumber}
              userChoice={userChoice}
              onHandleClick={onHandleClick}
              onHandleChange={onHandleChange}
              accessibilityInfo={accessibilityInfo}
              brand={brand}
              cot={cot}
              hasAccessibleRoom={hasAccessibleRoom}
              isNonSilentSubstituNotificPerRoomClassEnabled={
                isNonSilentSubstituNotificPerRoomClassEnabled
              }
              roomTypeInformationResponse={roomTypeInformationResponse}
              specialRoomLimitMessage={specialRoomLimitMessage as unknown as typeof Box}
              currentClassRoomTypes={currentClassRoomTypes}
            />
          );
        })}
      </Flex>
    </Flex>
  );
}
export const updateUserChoice = (
  prevState: UserChoice[],
  event: DropdownChangeEvent,
  openRoomNumber: number,
  activeClassRooms: any[] | undefined
): UserChoice[] => {
  return prevState.map((choice: UserChoice) =>
    choice.roomNumber === openRoomNumber
      ? {
          ...choice,
          pmsRoomType:
            activeClassRooms?.[choice.roomNumber - 1]?.rooms.find(
              (room: HIRoom) => room.roomType === event.code
            )?.pmsRoomType ?? 'DOUBLE',
          roomType: {
            id: String(event.id),
            icon: event.icon,
            label: String(event.label),
            code: event.code ?? '',
          },
        }
      : choice
  );
};

const sectionHeadingStyle = {
  display: 'inline-flex',
  fontSize: { mobile: 'xl', md: '2xl' },
  fontWeight: 'bold',
  fontFamily: 'header',
  alignItems: 'center',
  mb: 'md',
  mt: { mobile: '0.625rem', md: '0' },
  px: { mobile: '0.625rem', md: '0' },
};

const stepNumberStyle = {
  p: { mobile: '0.625rem 0.625rem', md: '0.188rem 0.125rem' },
  w: '1.438rem',
  h: '1.5rem',
  bg: 'lightGrey4',
  alignItems: 'center',
  justifyContent: 'center',
  borderRadius: '50%',
  fontWeight: 'semibold',
  fontSize: 'md',
  mr: '0.688rem',
};

const bedSectionStyle = {
  maxW: '63.25rem',
  p: { mobile: '0', sm: '0.625rem', md: '1.625rem' },
  mt: '0.938rem',
  border: '4px solid var(--chakra-colors-primary)',
  borderRadius: '10px',
  direction: 'column',
} as FlexProps;
