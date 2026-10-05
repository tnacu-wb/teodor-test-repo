import type { BoxProps, GridItemProps } from '@chakra-ui/react';
import { Box, GridItem } from '@chakra-ui/react';
import { AmendReservation, Packages, SelectedExtrasPackage } from '@whitbread-eos/api';
import { Tabs } from '@whitbread-eos/atoms';
import {
  extrasPackagesMapperSelector,
  formatDataTestId,
  getNightsNumber,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useEffect, useState } from 'react';

import { ExtrasSection } from '../../ancillaries';

export interface Props {
  rooms: AmendReservation[];
  mealPackagesData: Packages;
  currentRoom: number;
}

export default function RoomsExtrasSelection(props: Readonly<Props>) {
  const { rooms, mealPackagesData, currentRoom } = props;
  const { t } = useTranslation();
  const baseDataTestId = 'RoomsExtrasSelection';

  const [selectedRoom, setSelectedRoom] = useState(currentRoom);
  const [selectedExtrasList, setSelectedExtrasList] = useState<SelectedExtrasPackage[] | undefined>(
    []
  );

  const tabs = rooms.map((room: AmendReservation, index: number) => ({
    index,
    label: `${t('account.dashboard.room')} ${index + 1}`,
    description: room?.roomStay?.roomExtraInfo?.roomName,
  }));

  useEffect(() => {
    if (mealPackagesData?.roomSelection) {
      setSelectedExtrasList(extrasPackagesMapperSelector(mealPackagesData.roomSelection));
    }
  }, [mealPackagesData]);

  const firstRoom = rooms[0] || {};

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  return (
    <GridItem {...extrasContainerStyle} data-testid={formatDataTestId(baseDataTestId, 'Extras')}>
      {tabs.length > 1 && rooms.length > 1 ? (
        <Box {...multiRoomsStyle}>
          <Tabs
            options={tabs}
            variant="greyTabsGroup"
            index={selectedRoom}
            onChange={setSelectedRoom}
            prefixDataTestId={baseDataTestId}
            shortMobileLabels={true}
            singleContent={renderMultiRoomMealsSelection()}
          />
        </Box>
      ) : (
        <Box {...singleRoomStyle}>{renderExtrasSelection()}</Box>
      )}
    </GridItem>
  );

  function renderExtrasSelection() {
    return (
      <ExtrasSection
        extrasDetailsList={mealPackagesData?.extrasItems}
        selectedRoom={selectedRoom}
        selectedExtrasList={selectedExtrasList}
        noNights={noNights}
        isAmend={true}
      />
    );
  }

  function renderMultiRoomMealsSelection() {
    return (
      <Box {...multiRoomExtrasSelectionStyle} data-testid="multipleRoom">
        {renderExtrasSelection()}
      </Box>
    );
  }
}

const singleRoomStyle = {
  pt: 0,
  mt: 0,
} as BoxProps;

const multiRoomsStyle = {
  boxShadow: '0 0 var(--chakra-space-xmd) var(--chakra-colors-lightGrey4)',
  borderRadius: '0 0 3px 3px',
  borderTop: 'none',
  border: '1px solid var(--chakra-colors-lightGrey2)',
} as BoxProps;

const multiRoomExtrasSelectionStyle = {
  pt: { mobile: 'lg', sm: 'xl', lg: '3xl' },
  px: { mobile: 'md', sm: 'lg' },
  pb: { mobile: 'md', sm: 'lg' },
} as BoxProps;

const extrasContainerStyle = {
  py: 'var(--chakra-space-2xl)',
  px: 'var(--chakra-space-lg)',
} as GridItemProps;
