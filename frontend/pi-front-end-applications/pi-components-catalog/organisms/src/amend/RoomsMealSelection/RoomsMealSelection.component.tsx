import type { BoxProps, GridItemProps } from '@chakra-ui/react';
import { Box, Divider, GridItem, Text, useMediaQuery } from '@chakra-ui/react';
import {
  AmendReservation,
  ANCILLARIES_TABS,
  MealItem,
  MealKids,
  Menu,
  Packages,
  SelectedMealsPerRoom,
} from '@whitbread-eos/api';
import { Tabs } from '@whitbread-eos/atoms';
import { Menus } from '@whitbread-eos/molecules';
import {
  adultsMealsSelector,
  childrenMealsSelector,
  formatAssetsUrl,
  formatDataTestId,
  freeBreakfastMaxAllowance,
  getCookie,
  getNightsNumber,
  mealsMapperSelector,
  menusSelector,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useEffect, useState } from 'react';

import MealSelection from '../../ancillaries/MealSelection';

export interface Props {
  rooms: AmendReservation[];
  mealPackagesData: Packages;
  restaurantLogo: string;
  onSaveReservation: (
    prevValue: SelectedMealsPerRoom[],
    newValue: SelectedMealsPerRoom[],
    selectedRoom: number
  ) => void;
  handleOnAddMeal: (mealId: string, isForEntireStay: boolean) => void;
  handleOnRemoveMeal: (mealId: string, isForEntireStay: boolean) => void;
  currentRoom: number;
  saveMealsAllAtOnce: boolean;
}

export default function RoomsMealSelection(props: Readonly<Props>) {
  const {
    rooms,
    mealPackagesData,
    restaurantLogo,
    onSaveReservation,
    handleOnAddMeal,
    handleOnRemoveMeal,
    currentRoom,
    saveMealsAllAtOnce = false,
  } = props;
  const { t } = useTranslation();
  const baseDataTestId = 'RoomsMealSelection';

  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const [selectedRoom, setSelectedRoom] = useState(currentRoom);

  const [startingTab, setStartingTab] = useState(0);
  const [isMobileView] = useMediaQuery('(max-width: 765px)');
  // for AB test - scrollable tabs in mobile ancillaries
  const SCROLLABLE_TABS_SIZE = 2;
  // get cookie for scrollable tabs - AB test
  const hasScrollableTabsCookie = getCookie(ANCILLARIES_TABS.configName) === 'variant';
  const isScrollable = hasScrollableTabsCookie && isMobileView;

  const mealsTabs = rooms.map((room: AmendReservation, index: number) => ({
    index,
    label: `${t('account.dashboard.room')} ${index + 1}`,
    description: room?.roomStay?.roomExtraInfo?.roomName,
  }));

  useEffect(() => {
    const newMeals = mealsMapperSelector(
      mealPackagesData?.meals,
      mealPackagesData?.mealsKids,
      mealPackagesData?.roomSelection
    );

    setSelectedMeals(newMeals);
  }, [mealPackagesData]);

  const firstRoom = rooms[0] || {};

  const noNights = getNightsNumber(
    firstRoom.roomStay?.arrivalDate,
    firstRoom.roomStay?.departureDate
  );

  const bookingInformation = getBookingDetails(rooms, noNights);

  const adultsMeals: MealItem[] = adultsMealsSelector(
    mealPackagesData.meals,
    noNights,
    bookingInformation.totalAdults
  );

  const childrenMeals: MealKids[] = childrenMealsSelector(mealPackagesData?.mealsKids);
  const isAdultHasMealsFree = adultsMeals?.some((mealItem) => mealItem?.isFree ?? false);

  const menus: Menu[] =
    mealPackagesData?.meals && mealPackagesData?.mealsKids
      ? menusSelector(mealPackagesData.meals, mealPackagesData.mealsKids)
      : [];

  const resvHasChildrenBreakfast =
    bookingInformation.totalChildren > 0 &&
    adultsMeals.some((meal: MealItem) => meal.freeBreakfastOption) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0;

  const showFreeFoodKids =
    bookingInformation.children[selectedRoom] > 0 &&
    adultsMeals.some((meal: MealItem) => meal.freeBreakfastOption) &&
    freeBreakfastMaxAllowance(adultsMeals) > 0;

  const entireSelectionTitle = t('upsell.meals.title.allRooms');
  const individualSelectionTitle = t('upsell.meals.title.addMeals');

  const hasMenus = menus?.length > 0;
  return (
    <GridItem {...mealsContainerStyle} data-testid={formatDataTestId(baseDataTestId, 'Meals')}>
      {mealsTabs.length > 1 && rooms.length > 1 ? (
        <>
          {saveMealsAllAtOnce && (
            <>
              <MealSelection
                headingTitle={entireSelectionTitle}
                logoRestaurantUrl={formatAssetsUrl(restaurantLogo)}
                adultsPerRoom={bookingInformation.adults}
                kidsPerRoom={bookingInformation.children}
                adults={bookingInformation.totalAdults}
                nights={bookingInformation.nrNights}
                kids={bookingInformation.totalChildren}
                adultsMeals={adultsMeals}
                childrenMeals={childrenMeals}
                selectedRoom={selectedRoom}
                selectedMeals={selectedMeals}
                setSelectedMeals={setSelectedMeals}
                showFreeFoodKids={resvHasChildrenBreakfast}
                prefixDataTestId={baseDataTestId}
                hasMenus={hasMenus}
                isForEntireStay
                showUpdateMealsButton
                onSaveReservation={onSaveReservation}
                isAdultHasMealsFree={isAdultHasMealsFree}
              />
              <Divider {...dividerStyle} />
              <Text
                {...individualMealsTitle}
                data-testid={formatDataTestId(baseDataTestId, 'Individual-Room-Title')}
              >
                {t('upsell.meals.title.individualRoom')}
              </Text>
            </>
          )}
          <>
            {/* for non AB test and mobile only */}
            {isMobileView && !isScrollable && rooms.length > 7 && (
              <h3>
                {mealsTabs?.[selectedRoom]?.label} - {mealsTabs?.[selectedRoom]?.description}
              </h3>
            )}
            <Box {...multiRoomsContainerStyle}>
              <Tabs
                options={mealsTabs}
                variant="greyTabsGroup"
                index={selectedRoom}
                onChange={setSelectedRoom}
                prefixDataTestId={baseDataTestId}
                shortMobileLabels={true}
                singleContent={renderMultiRoomMealsSelection()}
                setStartingTab={setStartingTab}
                startingTab={startingTab}
                tabScrollSize={SCROLLABLE_TABS_SIZE}
                isScrollable={isScrollable}
                isMobileView={isMobileView}
                hasRoomLabels={true}
              />
            </Box>
          </>
        </>
      ) : (
        <Box {...singleRoomContainerStyle}>{renderMealsSelection(firstRoom.reservationId)}</Box>
      )}
    </GridItem>
  );

  function renderMealsSelection(reservationId: string) {
    const hasMenus = menus?.length > 0;
    return (
      <>
        <MealSelection
          headingTitle={individualSelectionTitle}
          logoRestaurantUrl={formatAssetsUrl(restaurantLogo)}
          adults={bookingInformation.adults[selectedRoom]}
          nights={bookingInformation.nrNights}
          kids={bookingInformation.children[selectedRoom]}
          adultsMeals={adultsMeals}
          childrenMeals={childrenMeals}
          selectedRoom={selectedRoom}
          selectedMeals={selectedMeals}
          setSelectedMeals={setSelectedMeals}
          showFreeFoodKids={showFreeFoodKids}
          prefixDataTestId={baseDataTestId}
          hasMenus={hasMenus}
          onSaveReservation={!saveMealsAllAtOnce ? onSaveReservation : undefined}
          onAddMeal={handleOnAddMeal}
          onRemoveMeal={handleOnRemoveMeal}
          reservationId={reservationId}
          isAdultHasMealsFree={isAdultHasMealsFree}
        />
        {hasMenus && <Menus availableMenus={menus} prefixDataTestId={baseDataTestId} />}
      </>
    );
  }

  function renderMultiRoomMealsSelection() {
    return (
      <Box {...multiRoomMealsSelectionContainerStyle} data-testid="multipleRoom">
        {renderMealsSelection(rooms[selectedRoom].reservationId)}
      </Box>
    );
  }
}

function getBookingDetails(rooms: AmendReservation[], noNights: number) {
  const noAdults: number[] = [];
  const noChildren: number[] = [];
  let totalAdults = 0;
  let totalChildren = 0;

  rooms.forEach((room: AmendReservation) => {
    totalAdults += room.roomStay.adultsNumber;
    noAdults.push(room.roomStay.adultsNumber);

    totalChildren += room.roomStay.childrenNumber;
    noChildren.push(room.roomStay.childrenNumber);
  });

  return {
    adults: noAdults,
    children: noChildren,
    nrNights: noNights,
    totalChildren: totalChildren,
    totalAdults: totalAdults,
  };
}

const dividerStyle = {
  my: '3xl',
  borderColor: 'lightGrey1',
};

const individualMealsTitle = {
  mb: 'xl',
  fontWeight: 'semibold',
  fontSize: { mobile: 'xl', sm: '2xl' },
  lineHeight: { mobile: '3', sm: '4' },
  color: 'var(--chakra-colors-darkGrey1)',
};

const singleRoomContainerStyle = {
  mt: 0,
  pt: 0,
} as BoxProps;

const multiRoomsContainerStyle = {
  border: '1px solid var(--chakra-colors-lightGrey2)',
  boxShadow: '0 0 var(--chakra-space-xmd) var(--chakra-colors-lightGrey4)',
  borderRadius: '0 0 3px 3px',
  borderTop: 'none',
} as BoxProps;

const multiRoomMealsSelectionContainerStyle = {
  px: { mobile: 'md', sm: 'lg' },
  pt: { mobile: 'lg', sm: 'xl', lg: '3xl' },
  pb: { mobile: 'md', sm: 'lg' },
} as BoxProps;

const mealsContainerStyle = {
  px: 'var(--chakra-space-lg)',
  py: 'var(--chakra-space-2xl)',
} as GridItemProps;
