import type {
  AmendReservation,
  ReservationById,
  BCReservationListItem,
  GuestCountUpdateRateCode,
  InfoItem,
  MealsSelectionDetailsPerRoom,
  PackageSelection,
  MealItem,
  MealKids,
  Menu,
  PrivacyPolicy,
  SecurityInfoItem,
  RoomSelection,
  SelectedMealsPerRoom,
  UniqueMealsCounter,
  AncillaryFilterData,
  Items,
  MealItemExtension,
} from '@whitbread-eos/api';
import {
  CITYTAX_PACKAGE,
  SILENT_SUBSTITUTION_STORAGE_KEY,
  SilentSubstitutionLocalStorage,
  DATE_TYPE,
  SelectedExtrasPackage,
  RoomPackageSelection,
  EARLY_CHECKIN_IDS,
  LATE_CHECKOUT_IDS,
  VALID_EXTRAS_IDS,
  PackagesSelection,
} from '@whitbread-eos/api';
import { isAfter, isBefore, isValid, isWithinInterval, parse } from 'date-fns';

import { formatAssetsUrl, filterAncillaryCloseOutData } from '../formatters';
import { isStringValid } from '../validators';

export const validateMealAssetsSrc = (meal: MealItem | MealKids): string => {
  const { allergyInfoSrc } = meal;
  if (typeof allergyInfoSrc !== 'undefined' && isStringValid(allergyInfoSrc)) {
    return allergyInfoSrc.startsWith('http') ? allergyInfoSrc : formatAssetsUrl(allergyInfoSrc);
  }
  return '';
};

export const enhanceMeal = (meal: MealItem | MealKids) => ({
  ...meal,
  allergyInfoSrc: validateMealAssetsSrc(meal),
  imageSrc: isStringValid(meal.imageSrc) ? formatAssetsUrl(meal.imageSrc ?? '') : '',
});

export const enhancePrivacyPolicyMoreInfoItem = (
  moreInfo: SecurityInfoItem[]
): SecurityInfoItem[] =>
  moreInfo
    ? moreInfo.map((moreInfoItem: SecurityInfoItem) => ({
        ...moreInfoItem,
        image: formatAssetsUrl(moreInfoItem.image ?? ''),
      }))
    : [];

export const sortMealsByOrderOrName = (
  firstMeal: MealItem | MealKids,
  secondMeal: MealItem | MealKids
) => {
  if (
    firstMeal.order === secondMeal.order ||
    firstMeal.order === null ||
    secondMeal.order === null
  ) {
    return firstMeal.name && secondMeal.name
      ? firstMeal.name.toUpperCase() > secondMeal.name.toUpperCase()
        ? 1
        : -1
      : null!;
  } else return firstMeal.order && secondMeal.order ? firstMeal.order - secondMeal.order : null!;
};

export const adultsMealsSelector = (
  adultsMeals: MealItem[] | undefined,
  noNights: number,
  noTotalAdults = 0
): MealItemExtension[] => {
  if (!adultsMeals) return [];

  const mappedMeals = adultsMeals
    .map((meal: MealItemExtension) => ({
      ...meal,
      ...enhanceMeal(meal),
      totalPrice: meal?.price ? meal?.price * noNights : 0,
      totalPriceForEntireStay: meal?.price ? meal?.price * noNights * noTotalAdults : 0,
    }))
    .sort(sortMealsByOrderOrName);

  const hasFreeMeal = mappedMeals?.some((meal) => meal?.isFree);

  if (!hasFreeMeal) return mappedMeals;

  return [
    ...mappedMeals.filter((meal) => meal?.isFree),
    ...mappedMeals.filter((meal) => !meal?.isFree),
  ];
};

export const freeBreakfastMaxAllowance = (adultsMeals: MealItem[]): number =>
  adultsMeals.reduce(
    (acc: number, current: MealItem) =>
      current?.freeBreakfastOption && current?.freeBreakfastMaxPerMeal
        ? acc + current?.freeBreakfastMaxPerMeal
        : acc,
    0
  );

export const freeBreakfastMaxSelectedAllowance = (
  adultsMealsSelection: string[],
  adultsMeals: MealItem[]
): number => {
  const enhancedSelectedMeals = adultsMealsSelection
    ? adultsMealsSelection.map((meal) => adultsMeals.filter((item) => item.id === meal)[0])
    : [];

  return freeBreakfastMaxAllowance(enhancedSelectedMeals);
};

export const childrenMealsSelector = (childrenMeals: MealKids[] | undefined): MealKids[] =>
  childrenMeals
    ? childrenMeals
        .map((meal: MealKids) => ({
          ...meal,
          ...enhanceMeal(meal),
        }))
        .sort(sortMealsByOrderOrName)
    : [];

export const menusSelector = (adultMeals: MealItem[], mealsKids: MealKids[]): Menu[] => {
  const formattedMenus = [
    ...adultMeals.filter((meal: MealItem) => meal.menu !== null),
    ...mealsKids.filter((meal: MealKids) => meal.menu !== null),
  ];
  return formattedMenus
    .filter(
      (meal, index, arrOfMeals) =>
        arrOfMeals.findIndex((item) => item?.menu?.menuSrc === meal?.menu?.menuSrc) === index
    )
    .map((item) => ({
      name: item?.menu?.name,
      menuSrc: formatAssetsUrl(item?.menu?.menuSrc ?? ''),
    }));
};

export const securityNoticeMoreInfoDataSelector = (
  privacyPolicy: PrivacyPolicy
): PrivacyPolicy => ({
  ...privacyPolicy,
  moreInfo: enhancePrivacyPolicyMoreInfoItem(privacyPolicy.moreInfo ?? []),
  linkSrc: isStringValid(privacyPolicy?.linkSrc)
    ? formatAssetsUrl(privacyPolicy.linkSrc ?? '')
    : '',
});

export const enhanceSelectedMeals = (
  roomMealsSelections: SelectedMealsPerRoom,
  adultsMeals: MealItem[],
  childrenMeals: MealKids[]
): MealsSelectionDetailsPerRoom => ({
  adultsMeals: adultsMeals
    ? adultsMeals
        .filter((meal) => roomMealsSelections?.adults.includes(meal.id ?? ''))
        .map((meal: MealItem) => ({
          title: meal.name,
          id: meal.id,
          price: meal?.price ?? 0,
          noSelections: roomMealsSelections.adults.filter((sel) => sel === meal.id).length,
        }))
    : [],
  childrenMeals: childrenMeals
    ? childrenMeals
        .filter((meal) => roomMealsSelections?.children.includes(meal.id ?? ''))
        .map((meal) => ({
          title: meal.name,
          id: meal.id,
          noSelections: roomMealsSelections.children.filter((sel) => sel === meal.id).length,
        }))
    : [],
});

export const selectedMealsPerRoomSelector = (
  selectedMeals: SelectedMealsPerRoom[],
  adultsMeals: MealItem[],
  childrenMeals: MealKids[]
): MealsSelectionDetailsPerRoom[] =>
  selectedMeals && selectedMeals.length > 0
    ? selectedMeals.map((roomMealsSelection) =>
        enhanceSelectedMeals(roomMealsSelection, adultsMeals, childrenMeals)
      )
    : [];

export const roomInformationSelector = (
  reservationListItems: ReservationById[] | AmendReservation[],
  selectedMeals: SelectedMealsPerRoom[],
  adultsMeals: MealItem[],
  childrenMeals: MealKids[],
  selectedExtrasList?: RoomPackageSelection[]
) =>
  reservationListItems && reservationListItems.length > 0
    ? reservationListItems.map(({ roomStay, reservationId }, index) => {
        const selectedExtrasFound = selectedExtrasList?.find(
          (extrasItem: RoomPackageSelection) => extrasItem.reservationId === reservationId
        );

        return {
          nrAdults: roomStay?.adultsNumber ?? 0,
          nrChildren: roomStay?.childrenNumber ?? 0,
          roomType: roomStay?.roomExtraInfo?.roomType,
          roomName: roomStay?.roomExtraInfo?.roomName,
          selectedMeals: enhanceSelectedMeals(selectedMeals[index], adultsMeals, childrenMeals),
          selectedExtrasList: selectedExtrasFound,
          accessibleRoom: {
            isAccessible: !!roomStay?.accessibleRoom?.isAccessible,
            phoneNumber: roomStay?.accessibleRoom?.phoneNumber ?? '',
          },
          ...(reservationId ? { reservationId } : {}),
        };
      })
    : [];

export const numberOfSelectionsPerRoomSelector = (selectedMeals: SelectedMealsPerRoom[]) => {
  return selectedMeals.map((room: SelectedMealsPerRoom) => {
    const adultsMeals = room.adults.reduce((acc: UniqueMealsCounter, value) => {
      return { ...acc, [value]: (acc[value as keyof UniqueMealsCounter] || 0) + 1 };
    }, {});

    const childrenMeals = room.children.reduce((acc: UniqueMealsCounter, value) => {
      return { ...acc, [value]: (acc[value as keyof UniqueMealsCounter] || 0) + 1 };
    }, {});
    const reservationId = room.reservationId;

    const adultsMealList = Object.keys(adultsMeals).map((key) => ({
      id: key,
      noOfSelections: adultsMeals[key],
    }));

    const childrenMealsList = Object.keys(childrenMeals).map((key) => ({
      id: key,
      noOfSelections: childrenMeals[key],
    }));

    return {
      reservationId: reservationId,
      packagesSelection: [...adultsMealList, ...childrenMealsList],
    };
  });
};

export const mealsMapperSelector = (
  adultsMeals: MealItemExtension[] | undefined,
  childrenMeals: MealKids[] | undefined,
  roomSelections: RoomSelection[] | undefined,
  isAdultHasMealsFree?: boolean | null,
  listGuests?: GuestCountUpdateRateCode
) => {
  const finalList: SelectedMealsPerRoom[] = [];
  const adultsIds = adultsMeals?.map((meal) => meal.id);
  const childrenIds = childrenMeals?.map((meal) => meal.id);

  roomSelections?.forEach((room: RoomSelection, roomIndex: number) => {
    const mealDistribution: SelectedMealsPerRoom = {
      adults: [],
      children: [],
      reservationId: room?.reservationId,
    };

    const hasChildren = (listGuests?.childrenNumber?.[roomIndex] ?? 0) > 0;

    if (isAdultHasMealsFree && listGuests) {
      room?.packagesSelection?.forEach((pcks: PackageSelection) => {
        const count = pcks.noOfSelections ?? 0;

        if (adultsIds?.includes(pcks.id)) {
          mealDistribution.adults = Array(count).fill(pcks.id).concat(mealDistribution.adults);

          const meal = adultsMeals?.find((m) => m.id === pcks.id);

          if (hasChildren && meal?.freeBreakfastOption && meal.freeBreakfastCode) {
            const isValidChild = childrenIds?.includes(meal.freeBreakfastCode);

            if (isValidChild) {
              const childCount = listGuests.childrenNumber?.[roomIndex] ?? 0;

              mealDistribution.children = Array(childCount)
                .fill(meal.freeBreakfastCode)
                .concat(mealDistribution.children);
            }
          }
        } else if (childrenIds?.includes(pcks.id)) {
          if (hasChildren) {
            mealDistribution.children = Array(count)
              .fill(pcks.id)
              .concat(mealDistribution.children);
          }
        }
      });
    } else if (room.packagesSelection && room.packagesSelection.length > 0) {
      room?.packagesSelection.forEach((pcks: PackageSelection) => {
        const count = pcks.noOfSelections ?? 0;

        if (adultsIds?.includes(pcks.id)) {
          mealDistribution.adults = Array(count).fill(pcks.id).concat(mealDistribution.adults);
        } else if (childrenIds?.includes(pcks.id)) {
          mealDistribution.children = Array(count).fill(pcks.id).concat(mealDistribution.children);
        }
      });
    }

    finalList.push(mealDistribution);
  });

  return finalList;
};

export const autocompleteMeals = (
  adultsMeals: MealItem[],
  childrenMeals: MealKids[],
  listGuests: GuestCountUpdateRateCode,
  prefUserMealId: string
) => {
  const finalList: SelectedMealsPerRoom[] = [];
  const prefMeal: MealItem | undefined = adultsMeals.find((meal) => meal.id === prefUserMealId);

  const associateChildMeal = childrenMeals.find((meal) => meal.id === prefMeal?.freeBreakfastCode);

  listGuests.adultsNumber.forEach((adults, index) => {
    finalList.push({
      adults: !prefMeal ? [] : Array(adults).fill(prefMeal?.id),
      children:
        prefMeal?.freeBreakfastOption && associateChildMeal
          ? Array(listGuests.childrenNumber[index]).fill(associateChildMeal.id)
          : [],
    });
  });

  return finalList;
};

export const calculateTotalCostRoomSelection = (
  adultsMeals: MealItem[],
  roomSelections: RoomSelection[],
  noOfNights: number
) => {
  let totalCost = 0;

  roomSelections?.forEach((room: RoomSelection) => {
    if (room.packagesSelection && room.packagesSelection?.length > 0) {
      room.packagesSelection.forEach((pcks: PackageSelection) => {
        const meal = adultsMeals?.find((meal) => meal.id === pcks.id);
        const mealPrice = meal ? meal.price : 0;
        if (mealPrice && pcks?.noOfSelections) {
          totalCost += mealPrice * pcks?.noOfSelections * noOfNights;
        }
      });
    }
  });

  return Number(totalCost.toFixed(2));
};

export const formatImportantNotes = (
  importantMessages: InfoItem[],
  arrivalDate: string,
  departureDate: string
) => {
  const sortedNotesByPriority = importantMessages?.sort((a: InfoItem, b: InfoItem) =>
    a?.priority && b?.priority && a?.priority > b?.priority ? 1 : -1
  );

  return sortedNotesByPriority
    .filter((note) => {
      const newStartDate = parse(note?.startDate ?? '', DATE_TYPE.DD_MM_YYYY, new Date());
      const newEndDate = parse(note?.endDate ?? '', DATE_TYPE.DD_MM_YYYY, new Date());
      const arrival = parse(arrivalDate, DATE_TYPE.YEAR_MONTH_DAY, new Date());
      const departure = parse(departureDate, DATE_TYPE.YEAR_MONTH_DAY, new Date());
      return (
        (newStartDate <= arrival && arrival <= newEndDate) ||
        (newStartDate <= departure && departure <= newEndDate) ||
        (arrival <= newStartDate && departure >= newEndDate)
      );
    })
    .map((item) => (item.htmlText?.trim() ? item.htmlText : item.text))
    .join('<br/>');
};

export const getImportantMessages = (
  importantMessages: Array<InfoItem | null | undefined> | null | undefined,
  arrivalDate: string | undefined,
  departureDate: string | undefined
): string[] => {
  const validImportantMessages = (importantMessages ?? []).filter(
    (item): item is InfoItem => !!item && !item.hideOnBookingFlow
  );

  if (!arrivalDate || !departureDate) return [];

  const messages = formatImportantNotes(validImportantMessages, arrivalDate, departureDate);

  return messages.length > 0 ? [messages] : [];
};

export const getUniqueRoomProperties = (
  rooms?: (ReservationById | BCReservationListItem)[],
  property: 'roomType' | 'roomName' = 'roomType'
) => {
  return rooms
    ?.map(
      (room?: ReservationById | BCReservationListItem) => room?.roomStay?.roomExtraInfo?.[property]
    )
    .filter((value, index, self) => self.indexOf(value) === index)
    .join();
};

export const checkIfExistPreselection = (roomSelections: RoomSelection[]) => {
  return roomSelections?.some(
    (room) => room?.packagesSelection && room?.packagesSelection?.length > 0
  );
};

export const shouldDisplayAutocompleteNotification = (
  listGuests: GuestCountUpdateRateCode,
  selectedMeals: SelectedMealsPerRoom[],
  prefMealId: { adultMeal: string; childMeal: string }
) => {
  return selectedMeals.every((roomSelected: SelectedMealsPerRoom, indexSel: number) => {
    const prefAdultMealForEntire =
      roomSelected.adults.filter((meal: string) => meal === prefMealId.adultMeal).length ===
      listGuests.adultsNumber[indexSel];

    const prefChildrenMealsForEntire =
      listGuests.childrenNumber[indexSel] > 0 && isStringValid(prefMealId.childMeal)
        ? roomSelected.children.filter((meal: string) => meal === prefMealId.childMeal).length ===
          listGuests.childrenNumber[indexSel]
        : true;

    return prefAdultMealForEntire && prefChildrenMealsForEntire;
  });
};

export const transformBartIdToOperaId = (bartId: string, meals: MealItem[]) => {
  return meals.find((meal) => meal.bartId === bartId)?.id ?? '';
};

export const isCityTaxAvailable = (
  roomSelection: RoomSelection[] | undefined = [],
  isLeisure = true
): boolean =>
  isLeisure && roomSelection && roomSelection.length > 0
    ? roomSelection.some((room: RoomSelection) =>
        room.packagesSelection
          ? room.packagesSelection.some(
              (pck: PackageSelection | undefined) =>
                pck?.id === CITYTAX_PACKAGE && (pck?.noOfSelections as number) > 0
            )
          : false
      )
    : false;

export function sortMealsByReservationId(
  mealsInfo: RoomSelection[],
  reservations: AmendReservation[] | BCReservationListItem[]
) {
  const orderedMeals: RoomSelection[] = [];
  reservations.forEach((reservation) => {
    const mealPackage = mealsInfo.find((meal) => meal.reservationId === reservation.reservationId);
    mealPackage && orderedMeals.push(mealPackage);
  });

  return orderedMeals;
}

export function isAncillaryCloseoutItemActiveForStay(
  ancillaryCloseoutItem: Items,
  bookingArrivalDate: string,
  bookingDepartureDate: string
): boolean {
  //Parse arrival,departure dates to be in same format as Interval dates
  const arrivalDate = parse(bookingArrivalDate, DATE_TYPE.YEAR_MONTH_DAY, new Date());
  const departureDate = parse(bookingDepartureDate, DATE_TYPE.YEAR_MONTH_DAY, new Date());
  const closeoutStartDate = parse(
    ancillaryCloseoutItem.startDate ?? '',
    DATE_TYPE.DD_MM_YYYY,
    new Date()
  );
  const closeoutEndDate = parse(
    ancillaryCloseoutItem.endDate ?? '',
    DATE_TYPE.DD_MM_YYYY,
    new Date()
  );

  if (
    !isValid(arrivalDate) ||
    !isValid(departureDate) ||
    !isValid(closeoutStartDate) ||
    !isValid(closeoutEndDate) ||
    !isBefore(closeoutStartDate, closeoutEndDate)
  ) {
    return false;
  }

  //Checks if Check-in date is between a closeout interval
  const ischeckinDateBeweenCloseout = isWithinInterval(arrivalDate, {
    start: closeoutStartDate,
    end: closeoutEndDate,
  });

  //Checks if Check-out date is between a closeout interval
  const ischeckoutDateBeweenCloseout = isWithinInterval(departureDate, {
    start: closeoutStartDate,
    end: closeoutEndDate,
  });

  //Checks if closeout interval is between Check-in and Check-out
  const isCloseoutBeweenBookingInterval =
    isAfter(closeoutStartDate, arrivalDate) && isBefore(closeoutEndDate, departureDate);

  return (
    ischeckinDateBeweenCloseout || ischeckoutDateBeweenCloseout || isCloseoutBeweenBookingInterval
  );
}

export function filterPackagesByAncillariesCloseOut(ancillaryFilterData: AncillaryFilterData) {
  const ancillaryData: Items[] = filterAncillaryCloseOutData(
    ancillaryFilterData.ancillaryCloseoutData
  );

  const packageCodeSet = new Set();
  ancillaryData?.map((ancillaryCloseoutItem: Items) => {
    if (
      isAncillaryCloseoutItemActiveForStay(
        ancillaryCloseoutItem,
        ancillaryFilterData.arrivalDate,
        ancillaryFilterData.departureDate
      )
    ) {
      const packageCodes = ancillaryCloseoutItem?.upsellCodes?.split(',');
      packageCodes?.forEach((item: string) => {
        packageCodeSet.add(item.trim());
      });
    }
  });

  const filteredAdultsMeals = ancillaryFilterData.adultsMeals.filter(
    (meal: MealItemExtension) => !packageCodeSet.has(meal.id)
  );
  const filteredChildrenMeals = ancillaryFilterData.childrenMeals.filter(
    (meal: MealKids) => !packageCodeSet.has(meal.id)
  );
  const filteredClosedOutMeals = ancillaryFilterData.adultsMeals.filter((meal: MealItemExtension) =>
    packageCodeSet.has(meal.id)
  );

  return {
    filteredAdultsMeals,
    filteredChildrenMeals,
    filteredClosedOutMeals,
  };
}

export function getCurrentReservationStorageData(
  reservationId: string
): SilentSubstitutionLocalStorage | null {
  if (typeof window !== 'undefined') {
    const roomStorageData = window.localStorage.getItem(SILENT_SUBSTITUTION_STORAGE_KEY);
    const parsedRoomStorageData = roomStorageData && JSON.parse(roomStorageData);

    return parsedRoomStorageData ? parsedRoomStorageData[reservationId] : null;
  }

  return null;
}

export const addReservationNumber = (
  roomSelections: RoomSelection[],
  reservationByIdList: ReservationById[],
  selectedExtrasList?: SelectedExtrasPackage[]
) => {
  const roomPackagesSelection: RoomSelection[] = [];

  if (reservationByIdList.length) {
    roomSelections.forEach((selection, index) => {
      selectedExtrasList?.map((extras) => {
        if (extras?.reservationId === reservationByIdList[index]?.reservationId) {
          extras?.packagesList?.forEach((extrasPackage: string) => {
            selection?.packagesSelection?.push({
              id: extrasPackage,
              noOfSelections: 1,
            });
          });
        }
      });

      const selectionPerRoom: RoomSelection = {
        ...selection,
        reservationId: reservationByIdList[index]?.reservationId ?? '',
      };
      return roomPackagesSelection.push(selectionPerRoom);
    });
  }
  return roomPackagesSelection;
};

export const extrasPackagesMapperSelector = (roomSelections: RoomSelection[] | undefined) => {
  const extrasPackagesListPerRooms = roomSelections?.map((room: RoomSelection) => {
    const extrasPackagesPerRoom: SelectedExtrasPackage = {
      packagesList: [],
      reservationId: room?.reservationId,
      price: 0,
      previousEciSelection: 0,
      previousLcoSelection: 0,
    };

    room?.packagesSelection?.forEach((selectedPackage: PackagesSelection) => {
      const id = selectedPackage?.id;
      if (!id) return;

      if (VALID_EXTRAS_IDS.includes(id as (typeof VALID_EXTRAS_IDS)[number])) {
        if (!extrasPackagesPerRoom?.packagesList?.includes(id)) {
          extrasPackagesPerRoom.packagesList.push(id);
        }

        if (EARLY_CHECKIN_IDS.includes(id as (typeof EARLY_CHECKIN_IDS)[number])) {
          extrasPackagesPerRoom.previousEciSelection = 1;
        }

        if (LATE_CHECKOUT_IDS.includes(id as (typeof LATE_CHECKOUT_IDS)[number])) {
          extrasPackagesPerRoom.previousLcoSelection = 1;
        }
      }
    });

    return extrasPackagesPerRoom;
  });

  return extrasPackagesListPerRooms;
};

export function addOrRemoveExtrasRoom(
  extrasId: string,
  price: number,
  selectedExtrasList: SelectedExtrasPackage[] | undefined,
  selectedRoom: number
) {
  const canBeRemoved = selectedExtrasList?.[selectedRoom]?.packagesList.includes(extrasId);
  const reservationIdPerRoom = selectedExtrasList?.[selectedRoom]?.reservationId?.toString();
  const updatedExtrasItemsList = selectedExtrasList?.map((extrasItem) => {
    if (extrasItem.reservationId === reservationIdPerRoom && !canBeRemoved) {
      extrasItem.packagesList.push(extrasId);
      extrasItem.price = extrasItem.price + price;
    }
    if (extrasItem.reservationId === reservationIdPerRoom && canBeRemoved) {
      const indexOfExtrasPackage = extrasItem.packagesList.indexOf(extrasId);
      extrasItem.packagesList.splice(indexOfExtrasPackage, 1);
      extrasItem.price = extrasItem.price - price;
    }

    return extrasItem;
  });

  return updatedExtrasItemsList;
}

export function addOrRemoveAllRoomsExtras(
  extrasId: string,
  price: number,
  selectedExtrasList: SelectedExtrasPackage[] | undefined,
  isRemovable?: boolean
) {
  const updatedExtrasItemsList: SelectedExtrasPackage[] | undefined = [];
  selectedExtrasList?.forEach((extrasItem) => {
    const canBeRemoved = extrasItem?.packagesList.includes(extrasId);

    if (!isRemovable && !canBeRemoved) {
      extrasItem.packagesList.push(extrasId);
      extrasItem.price = extrasItem.price + price;
    }
    if (isRemovable && canBeRemoved) {
      const indexOfExtrasPackage = extrasItem.packagesList.indexOf(extrasId);
      extrasItem.packagesList.splice(indexOfExtrasPackage, 1);
      extrasItem.price = extrasItem.price - price;
    }

    updatedExtrasItemsList.push(extrasItem);
  });

  return updatedExtrasItemsList;
}

export function roomPackageSelection(selectedExtrasList: SelectedExtrasPackage[] | undefined) {
  const packageSelection: RoomPackageSelection[] = [];

  selectedExtrasList?.map((room) => {
    const roomExtras = room?.packagesList?.map((extras: string) => {
      return {
        id: extras,
        noOfSelections: 1,
      };
    });

    return packageSelection.push({
      packagesSelection: roomExtras,
      price: room?.price,
      reservationId: room?.reservationId,
    });
  });

  return packageSelection;
}
