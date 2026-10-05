import type {
  BoxProps,
  FlexProps,
  HeadingProps,
  TextProps,
  ButtonProps,
  StyleProps,
} from '@chakra-ui/react';
import { Box, Flex, Heading, Text } from '@chakra-ui/react';
import type { MealItemExtension, MealKids, SelectedMealsPerRoom } from '@whitbread-eos/api';
import { FT_PI_BB_CCUI_SHOW_MEALS_FREE, FREE_FOOD_OPTIONS } from '@whitbread-eos/api';
import { AddSubtract, Button, ChangeArrows, Icon } from '@whitbread-eos/atoms';
import { MealItem } from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  freeBreakfastMaxSelectedAllowance,
  useCustomLocale,
  akamaiImageLoader,
  isIVMEnabled,
  useFeatureToggle,
  analytics,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import type { Dispatch, SetStateAction } from 'react';
import { useCallback, useEffect, useState } from 'react';

export interface Props {
  logoRestaurantUrl: string;
  adults: number;
  kids: number;
  nights: number;
  adultsMeals: MealItemExtension[];
  childrenMeals: MealKids[];
  selectedRoom: number;
  selectedMeals: SelectedMealsPerRoom[];
  setSelectedMeals: Dispatch<SetStateAction<SelectedMealsPerRoom[]>>;
  showFreeFoodKids: boolean;
  prefixDataTestId?: string;
  adultsPerRoom?: number[];
  kidsPerRoom?: number[];
  isForEntireStay?: boolean;
  headingTitle: string;
  hasMenus?: boolean;
  onSaveReservation?: (
    prevValue: SelectedMealsPerRoom[],
    newValue: SelectedMealsPerRoom[],
    selectedRoom: number
  ) => void;
  onAddMeal?: (value: string, isForEntireStay: boolean) => void;
  onRemoveMeal?: (value: string, isForEntireStay: boolean) => void;
  reservationId?: string;
  notification?: any;
  showUpdateMealsButton?: boolean;
  hasPromoMeal?: boolean;
  isSoftBundlesVisible?: boolean;
  softBundleIncludedMeal?: MealItemExtension;
  isAdultHasMealsFree?: boolean | null;
}

interface MealSelectionCountProps {
  value: number;
  label: string;
}

export function MealSelectionCount({ value, label }: Readonly<MealSelectionCountProps>) {
  return (
    <Flex direction="column" alignItems="center" justifyContent="center">
      <Text fontSize="2xl" fontWeight="medium" lineHeight="4" color="darkGrey1">
        {value}
      </Text>

      <Text fontSize="md" lineHeight="3" fontWeight="medium" color="darkGrey1">
        {label}
      </Text>
    </Flex>
  );
}

export function handleOnAddAdultMealsSelections(
  meal: MealItemExtension,
  selectedMeals: SelectedMealsPerRoom[],
  adultsMeals: MealItemExtension[],
  adultsPerRoom: number[],
  childrenMeals: MealItemExtension[],
  setSelectedMeals: Dispatch<SetStateAction<SelectedMealsPerRoom[]>>
) {
  const newSelectedMealsState: SelectedMealsPerRoom[] = [];

  selectedMeals?.forEach((room: SelectedMealsPerRoom, index: number) => {
    const currentAdultMealIds = room?.adults ?? [];

    const newMealTypes = Array.isArray(meal.upsellType)
      ? meal.upsellType
      : (meal.upsellType?.split(',').map((s) => s.trim()) ?? []);

    const filteredAdultMeals = currentAdultMealIds.filter((id) => {
      const existing = adultsMeals.find((m) => m.id === id);
      if (!existing) return true;

      const existingTypes = Array.isArray(existing.upsellType)
        ? existing.upsellType
        : (existing.upsellType?.split(',').map((s) => s.trim()) ?? []);

      return !existingTypes.some((type) => newMealTypes.includes(type));
    });
    const updatedAdultMeals = [...filteredAdultMeals, ...Array(adultsPerRoom[index]).fill(meal.id)];

    const currentChildrenMealIds = room.children ?? [];

    const mappedChildMealTypes = newMealTypes
      .map((type) => FREE_FOOD_OPTIONS[type.toUpperCase() as keyof typeof FREE_FOOD_OPTIONS])
      .filter(Boolean);

    const cleanedChildrenMeals = currentChildrenMealIds.filter((id) => {
      const meal = childrenMeals.find((m) => m.id === id);
      if (!meal) return true;

      const childTypes = Array.isArray(meal.upsellType)
        ? meal.upsellType.map((s) => s.trim())
        : (meal.upsellType?.split(',').map((s) => s.trim()) ?? []);

      const hasNonConflictingType = childTypes.some((type) => !mappedChildMealTypes.includes(type));

      return hasNonConflictingType;
    });

    const additions: string[] = [];

    mappedChildMealTypes.forEach((mappedType) => {
      const match = childrenMeals.find((childMeal) => {
        const types = Array.isArray(childMeal.upsellType)
          ? childMeal.upsellType
          : (childMeal.upsellType?.split(',').map((s) => s.trim()) ?? []);
        return types.includes(mappedType);
      });

      if (match) {
        const count = room.children?.length || 0;
        additions.push(...Array(count).fill(match.id));
      }
    });

    const updatedChildrenMeals = [...cleanedChildrenMeals, ...additions];

    newSelectedMealsState.push({
      reservationId: room.reservationId,
      adults: updatedAdultMeals,
      children: updatedChildrenMeals,
    });
  });

  setSelectedMeals(newSelectedMealsState);
}

export function handleOnAddChildrenMealsSelections(
  meal: MealItemExtension,
  selectedMeals: SelectedMealsPerRoom[],
  kidsPerRoom: number[],
  setSelectedMeals: Dispatch<SetStateAction<SelectedMealsPerRoom[]>>
) {
  const newSelectedMealsState: SelectedMealsPerRoom[] = selectedMeals?.map(
    (room: SelectedMealsPerRoom, index: number) => {
      const existingChildrenMeals = room?.children ?? [];

      // Only add if not already selected
      const updatedChildren = existingChildrenMeals?.includes(meal?.id as string)
        ? existingChildrenMeals
        : [...existingChildrenMeals, ...Array(kidsPerRoom[index]).fill(meal.id)];

      return {
        adults: room?.adults ?? [],
        children: updatedChildren,
        reservationId: room?.reservationId,
      };
    }
  );

  setSelectedMeals(newSelectedMealsState);
}

export const normalizeUpsellType = (input?: string | string[]) => {
  if (!input) return [];
  return Array.isArray(input) ? input : input.split(',').map((s) => s.trim());
};

export function handleRemoveAdultMealsSelections(
  meal: MealItemExtension,
  selectedMeals: SelectedMealsPerRoom[],
  setSelectedMeals: Dispatch<SetStateAction<SelectedMealsPerRoom[]>>
) {
  const normalizeFreeOptions = (input?: string | string[]) => {
    if (!input) return [];
    return Array.isArray(input) ? input : [input];
  };

  const mealIdToRemove = meal.id;
  const childMealIdsToRemove = new Set(normalizeFreeOptions(meal?.freeBreakfastCode));

  const newSelectedMealsState: SelectedMealsPerRoom[] = selectedMeals.map((room) => {
    // Remove only the selected adult meal ID (not all same-type meals)
    const filteredAdults = (room?.adults ?? []).filter((id) => id !== mealIdToRemove);

    // Remove any children meals mapped via freeBreakfastCode
    const filteredChildren = (room?.children ?? []).filter((id) => !childMealIdsToRemove.has(id));

    return {
      reservationId: room.reservationId,
      adults: filteredAdults,
      children: filteredChildren,
    };
  });

  setSelectedMeals(newSelectedMealsState);
}

export function handleRemoveChildrenMealsSelections(
  meal: MealItemExtension,
  selectedMeals: SelectedMealsPerRoom[],
  setSelectedMeals: Dispatch<SetStateAction<SelectedMealsPerRoom[]>>
) {
  const newSelectedMealsState: SelectedMealsPerRoom[] = selectedMeals?.map(
    (room: SelectedMealsPerRoom) => {
      const updatedChildren = (room?.children ?? []).filter((id: string) => id !== meal.id);

      return {
        adults: room?.adults ?? [],
        children: updatedChildren,
        reservationId: room?.reservationId,
      };
    }
  );

  setSelectedMeals(newSelectedMealsState);
}

export function isAdultMealForEntireStaySelections(
  mealId: string,
  adultsMeals: MealItemExtension[],
  selectedMeals: SelectedMealsPerRoom[]
): boolean {
  const mealToCheck = adultsMeals?.find((m: any) => m.id === mealId);
  if (!mealToCheck) return false;

  const mealTypes = normalizeUpsellType(mealToCheck?.upsellType); // e.g. ["breakfast"]

  return selectedMeals.some((room: SelectedMealsPerRoom) => {
    const selectedAdultMealIds = room?.adults ?? [];
    const selectedMealsForRoom = selectedAdultMealIds
      .map((id: any) => adultsMeals?.find((m: any) => m.id === id))
      .filter(Boolean) as MealItemExtension[];

    return mealTypes?.every((type) => {
      return selectedMealsForRoom?.some((selected) => {
        const selectedTypes = normalizeUpsellType(selected?.upsellType);
        return selected?.id === mealId && selectedTypes?.includes(type);
      });
    });
  });
}

export function isAvailableAdultMealsForEntireStay(
  mealItem: MealItemExtension,
  selectedMeals: SelectedMealsPerRoom[],
  adultsMeals: MealItemExtension[]
): boolean {
  if (!mealItem?.upsellType || (mealItem?.upsellType && !mealItem?.upsellType?.length)) {
    return true;
  }
  const mealTypesToCheck = normalizeUpsellType(mealItem?.upsellType);

  // Return false (don't disable) if nothing selected yet
  const hasAnyMeal = selectedMeals?.some(
    (room: SelectedMealsPerRoom) => (room?.adults ?? []).length > 0
  );
  if (!hasAnyMeal) return false;

  // Disable if ANY upsellType (e.g. 'dinner') is already present in ALL rooms
  const typeExistsInAllRooms = mealTypesToCheck?.some((type) =>
    selectedMeals?.every((room: SelectedMealsPerRoom) =>
      (room?.adults ?? []).some((id: string) => {
        const selectedMeal = adultsMeals?.find((m: any) => m.id === id);
        const types = normalizeUpsellType(selectedMeal?.upsellType);
        return types?.includes(type);
      })
    )
  );

  return typeExistsInAllRooms; // true means disable
}

export default function MealSelection({
  adults = 0,
  kids = 0,
  nights,
  adultsMeals,
  childrenMeals,
  selectedRoom,
  selectedMeals,
  setSelectedMeals,
  showFreeFoodKids,
  prefixDataTestId,
  isForEntireStay = false,
  headingTitle,
  adultsPerRoom = [],
  kidsPerRoom = [],
  logoRestaurantUrl,
  hasMenus,
  onSaveReservation,
  onAddMeal,
  onRemoveMeal,
  reservationId,
  notification,
  showUpdateMealsButton = false,
  hasPromoMeal,
  isSoftBundlesVisible = false,
  softBundleIncludedMeal,
  isAdultHasMealsFree = false,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language: currentLang } = useCustomLocale();

  const baseDataTestId = formatDataTestId(prefixDataTestId, 'Meals');
  const [selectedRoomMeals, setSelectedRoomMeals] = useState<SelectedMealsPerRoom>({
    adults: [],
    children: [],
    reservationId: '',
  });

  const hasMealIncludedInSoftBundle = softBundleIncludedMeal !== undefined;
  const shouldDisplaySoftBundleMealVariant = isSoftBundlesVisible && hasMealIncludedInSoftBundle;
  const { [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: isshowKidsMealsFreeFlag } = useFeatureToggle();
  const getTypographyProps = useSemanticTypography();

  const availableAdultsMeals = adults - selectedRoomMeals?.adults?.length;
  const selectedMealPrice =
    adultsMeals?.find((meal) => meal?.id === selectedMeals?.[0]?.adults?.[0])?.price || 0;

  function getUpsellTypeCountsByIds(ids: string[]): { breakfast: number; dinner: number } {
    const counts = {
      breakfast: 0,
      dinner: 0,
    };

    ids?.forEach((id) => {
      const meal = adultsMeals?.find((m) => m?.id === id);
      if (meal?.upsellType) {
        meal?.upsellType?.split(',').forEach((type) => {
          const trimmedType = type?.trim().toLowerCase();
          if (trimmedType === FREE_FOOD_OPTIONS.BREAKFAST) {
            counts.breakfast += 1;
          } else if (trimmedType === FREE_FOOD_OPTIONS.DINNER) {
            counts.dinner += 1;
          }
        });
      }
    });

    return counts;
  }

  const availableAdultsMealSelections = useCallback(
    (mealItem: MealItemExtension) => {
      const currentUpsellTypeArray = mealItem?.upsellType?.split(',').map((type) => type?.trim());
      const selectedUpsellTypes = getUpsellTypeCountsByIds(selectedRoomMeals?.adults);

      if (
        (currentUpsellTypeArray?.includes(FREE_FOOD_OPTIONS.BREAKFAST) &&
          selectedUpsellTypes?.breakfast >= adults) ||
        (currentUpsellTypeArray?.includes(FREE_FOOD_OPTIONS.DINNER) &&
          selectedUpsellTypes?.dinner >= adults)
      ) {
        return 0;
      } else if (selectedUpsellTypes?.breakfast < adults || selectedUpsellTypes?.dinner < adults) {
        return adults;
      } else {
        return adults - selectedRoomMeals?.adults?.length;
      }
    },
    [selectedMeals, selectedRoom, selectedRoomMeals?.adults, selectedRoomMeals?.adults?.length]
  );

  const maxAllowChildrenSelection = freeBreakfastMaxSelectedAllowance(
    selectedRoomMeals?.adults,
    adultsMeals
  );

  const maxChildrenNoSelections =
    kids > maxAllowChildrenSelection ? maxAllowChildrenSelection : kids;

  const isChildMealSelectionAvailable =
    adultsMeals.some(
      (meal) =>
        meal.freeBreakfastOption && selectedRoomMeals?.adults.some((id: string) => id === meal.id)
    ) && maxAllowChildrenSelection > 0;

  useEffect(() => {
    setSelectedRoomMeals(selectedMeals[selectedRoom]);
  }, [selectedMeals, selectedRoom, setSelectedRoomMeals]);

  const availableChildrenMeals = isChildMealSelectionAvailable
    ? maxChildrenNoSelections - selectedRoomMeals.children.length
    : 0;

  const availableChildrenMealSelections = useCallback(
    (mealItem: MealItemExtension) => {
      const matchingAdultMeals = adultsMeals.filter(
        (adultMealItem: MealItemExtension) =>
          adultMealItem.freeBreakfastOption === true &&
          adultMealItem.freeBreakfastCode === mealItem?.id
      );
      if (matchingAdultMeals.length === 0) {
        return 0;
      }
      // Count unique upsellTypes in selected adult meals
      const seenUpsellTypes = new Set<string>();

      const totalAdultFreeMealsSelected =
        selectedRoomMeals?.adults?.reduce((count, item) => {
          const matchedAdult = matchingAdultMeals.find((adultMeal) => adultMeal.id === item);

          if (
            matchedAdult?.upsellType &&
            !seenUpsellTypes.has(matchedAdult?.freeBreakfastCode as string)
          ) {
            seenUpsellTypes.add(matchedAdult?.freeBreakfastCode as string);
            return count + 1;
          }
          return count;
        }, 0) || 0;

      const totalFreeChildMealsAllowed = totalAdultFreeMealsSelected * kids;

      const totalChildFreeMealsSelected = selectedRoomMeals?.children?.reduce(
        (count, item) => (item === mealItem.id ? count + 1 : count),
        0
      );
      const remainingFreeChildMeals = totalFreeChildMealsAllowed - totalChildFreeMealsSelected;

      return Math.max(remainingFreeChildMeals, 0);
    },
    [
      adultsMeals,
      adults,
      kids,
      selectedRoomMeals?.children,
      selectedRoomMeals?.adults,
      selectedRoom,
      childrenMeals,
    ]
  );

  const updateSelectedMeals = useCallback(
    (newRoomState: SelectedMealsPerRoom): void => {
      const newSelectedMealsState = JSON.parse(JSON.stringify(selectedMeals));
      newSelectedMealsState[selectedRoom] = newRoomState;
      setSelectedMeals(newSelectedMealsState);
      onSaveReservation?.(selectedMeals, newSelectedMealsState, selectedRoom);
    },
    [setSelectedMeals, selectedRoom, selectedMeals, onSaveReservation]
  );

  const updateMealSelection = useCallback(() => {
    onSaveReservation?.([], selectedMeals, selectedRoom);
  }, [onSaveReservation, selectedMeals, selectedRoom]);

  //<editor-fold desc="Button handlers" defaultstate="collapsed">

  const handleChangeSoftBundleMeal = useCallback(
    (meal: MealItemExtension) => {
      const newSelectedMealsState: SelectedMealsPerRoom[] = [];
      selectedMeals.forEach((room, index) => {
        analytics.update({
          ...window.analyticsData,
          upgradeBundle: selectedMeals[index].adults.find(() => meal.id ?? ''),
        });
        newSelectedMealsState.push({
          adults: selectedMeals[index].adults.map(() => meal.id ?? ''),
          children: meal.freeBreakfastOption ? selectedMeals[index].children : [],
          reservationId: room.reservationId,
        });
      });
      setSelectedMeals(newSelectedMealsState);
      analytics.update({
        ...window.analyticsData,
        upgradeBundleSelected: true,
      });
    },
    [selectedMeals]
  );

  const handleOnAddAdultMeals = useCallback(
    (meal: MealItemExtension) => {
      const newSelectedMealsState: SelectedMealsPerRoom[] = [];
      selectedMeals.forEach((room, index) => {
        newSelectedMealsState.push({
          adults: Array(adultsPerRoom[index]).fill(meal.id),
          children: meal.freeBreakfastOption ? selectedMeals[index].children : [],
          reservationId: room.reservationId,
        });
      });
      setSelectedMeals(newSelectedMealsState);
    },
    [selectedMeals, selectedRoomMeals]
  );

  const handleOnAddChildrenMeals = useCallback(
    (meal: MealKids) => {
      const newSelectedMealsState: SelectedMealsPerRoom[] = [];

      selectedMeals.forEach((room, index) => {
        newSelectedMealsState.push({
          adults: selectedMeals[index].adults,
          children: Array(kidsPerRoom[index]).fill(meal.id),
          reservationId: room.reservationId,
        });
      });

      setSelectedMeals(newSelectedMealsState);
    },
    [selectedMeals, selectedRoomMeals]
  );

  const handleRemoveAdultMeals = useCallback(() => {
    const newSelectedMealsState: SelectedMealsPerRoom[] = [];

    selectedMeals.forEach((room) => {
      newSelectedMealsState.push({
        adults: [],
        children: [],
        reservationId: room.reservationId,
      });
    });

    setSelectedMeals(newSelectedMealsState);
  }, [selectedMeals, selectedRoomMeals]);

  const handleRemoveChildrenMeals = useCallback(() => {
    const newSelectedMealsState: SelectedMealsPerRoom[] = [];

    selectedMeals.forEach((room) => {
      newSelectedMealsState.push({
        adults: room.adults,
        children: [],
        reservationId: room.reservationId,
      });
    });

    setSelectedMeals(newSelectedMealsState);
  }, [selectedMeals, selectedRoomMeals]);

  const isAvailableChildrenMealsForEntire = selectedMeals.some(
    (room: SelectedMealsPerRoom, index) => {
      return (
        freeBreakfastMaxSelectedAllowance(room?.adults, adultsMeals) === 0 &&
        kidsPerRoom[index] !== 0
      );
    }
  );

  function hasFreeBreakfastCode(ids: string[], mealId: string): boolean {
    const codes = ids
      .map((id) => adultsMeals.find((meal) => meal.id === id)?.freeBreakfastCode)
      .filter((code) => code); // Remove empty/undefined codes

    const uniqueCodes = new Set(codes);
    return uniqueCodes.has(mealId);
  }

  const isAvailableChildrenMealsForEntireStay = (mealId: string) => {
    const isAvailableChildrenMealsForEntire = selectedMeals.some(
      (room: SelectedMealsPerRoom, index) => {
        return !hasFreeBreakfastCode(room?.adults, mealId) && kidsPerRoom[index] !== 0;
      }
    );
    return isAvailableChildrenMealsForEntire;
  };

  const isAdultMealForEntireStay = (mealId: string) => {
    return selectedMeals.every(
      (room: SelectedMealsPerRoom, index: number) =>
        room.adults.filter((meal) => meal === mealId).length >= adultsPerRoom[index]
    );
  };

  const isChildrenMealForEntireStay = (mealId: string) => {
    return selectedMeals.every(
      (room: SelectedMealsPerRoom, index: number) =>
        room.children.filter((meal) => meal === mealId).length >= kidsPerRoom[index]
    );
  };
  //</editor-fold>

  //<editor-fold desc="AddSubstract handlers" defaultstate="collapsed">
  const handleOnSubtractForAdults = useCallback(
    (mealId: string) => {
      const index = selectedRoomMeals.adults.indexOf(mealId);
      const newAdultsArray = [
        ...selectedRoomMeals.adults.slice(0, index),
        ...selectedRoomMeals.adults.slice(index + 1, selectedRoomMeals.adults.length + 1),
      ];
      const maxAllowance = freeBreakfastMaxSelectedAllowance(newAdultsArray, adultsMeals);

      const newChildrenArray = selectedRoomMeals.children.slice(0, maxAllowance);
      const newRoomState = {
        adults: newAdultsArray,
        children: newChildrenArray,
        reservationId: reservationId,
      };
      updateSelectedMeals(newRoomState);
    },
    [adultsMeals, selectedRoomMeals, updateSelectedMeals]
  );

  function removeMealIds(mealId: string): string[] {
    let removed = 0;
    return selectedRoomMeals?.children.filter((id) => {
      if (id === mealId && removed < kids) {
        removed++;
        return false;
      }
      return true;
    });
  }

  const handleOnSubtractForAdultSelections = useCallback(
    (mealItem: MealItemExtension) => {
      const index = selectedRoomMeals.adults.indexOf(mealItem?.id as string);

      const newAdultsArray = [
        ...selectedRoomMeals.adults.slice(0, index),
        ...selectedRoomMeals.adults.slice(index + 1),
      ];

      // Check if any other adult meals with same freeBreakfastCode exist
      const stillMappedAdultMealsExist = newAdultsArray.some((adultMealId) => {
        const adultMeal = adultsMeals.find((meal) => meal.id === adultMealId);
        return (
          adultMeal?.freeBreakfastOption === true &&
          adultMeal?.freeBreakfastCode === mealItem?.freeBreakfastCode
        );
      });

      // Only remove kids meals if no adult meals left with same freeBreakfastCode
      const newChildrenArray =
        mealItem?.freeBreakfastOption === true && !stillMappedAdultMealsExist
          ? removeMealIds(mealItem?.freeBreakfastCode as string)
          : selectedRoomMeals?.children;

      const newRoomState = {
        adults: newAdultsArray,
        children: newChildrenArray,
        reservationId: reservationId,
      };

      updateSelectedMeals(newRoomState);
    },
    [adultsMeals, selectedRoomMeals, updateSelectedMeals]
  );

  const handleOnPlusForAdults = useCallback(
    (mealId: string) => {
      const newRoomState = {
        children: [...selectedRoomMeals.children],
        adults: [...selectedRoomMeals.adults, mealId],
        reservationId: reservationId,
      };
      updateSelectedMeals(newRoomState);
    },
    [selectedRoomMeals, updateSelectedMeals]
  );

  function removeIdOnce(mealId: string): string[] {
    const index = selectedRoomMeals.children.indexOf(mealId);
    if (index === -1) return selectedRoomMeals.children; // id not found, return original array
    return [
      ...selectedRoomMeals.children.slice(0, index),
      ...selectedRoomMeals.children.slice(index + 1),
    ];
  }

  const handleOnSubtractForChildren = useCallback(
    (mealId: string) => {
      const index = selectedRoomMeals.children.indexOf(mealId);
      const newRoomState = {
        ...selectedRoomMeals,
        children: isshowKidsMealsFreeFlag
          ? removeIdOnce(mealId)
          : [
              ...selectedRoomMeals.children.slice(0, index),
              ...selectedRoomMeals.children.slice(index + 1, selectedRoomMeals.children.length + 1),
            ],
        reservationId: reservationId,
      };
      updateSelectedMeals(newRoomState);
    },
    [selectedRoomMeals, updateSelectedMeals]
  );

  const handleOnPlusForChildren = useCallback(
    (mealId: string) => {
      const newRoomState = {
        ...selectedRoomMeals,
        children: [...selectedRoomMeals.children, mealId],
        reservationId: reservationId,
      };
      updateSelectedMeals(newRoomState);
    },
    [selectedRoomMeals, updateSelectedMeals]
  );

  const noOfSelectedAdultsMeals = useCallback(
    (mealId: string) => selectedRoomMeals?.adults.filter((item) => item === mealId).length,
    [selectedRoomMeals]
  );

  const noOfSelectedChildrensMeals = useCallback(
    (mealId: string) => selectedRoomMeals?.children.filter((item) => item === mealId).length,
    [selectedRoomMeals]
  );

  //</editor-fold>

  const getAdultsPluralLabel = useCallback(
    (value: number) => (value === 1 ? t('upsell.label.adult') : t('upsell.label.adults')),
    [t]
  );

  const getChildrenPluralLabel = useCallback(
    (value: number) => (value === 1 ? t('upsell.label.child') : t('upsell.label.children')),
    [t]
  );

  const getNightsPluralLabel = nights > 1 ? t('upsell.label.nights') : t('upsell.label.night');
  if (!adultsMeals?.length) {
    return <Box>{notification}</Box>;
  }

  return (
    <Box>
      <Flex {...logoWrapperStyle} data-testid={formatDataTestId(baseDataTestId, 'Heading-Wrapper')}>
        <Text
          {...mealsTitleLayoutStyle}
          {...getTypographyProps(mealsTitleLegacyTypography, mealsTitleSemanticTypography)}
          data-testid={formatDataTestId(baseDataTestId, 'Heading-Title')}
        >
          {headingTitle}
        </Text>

        <Flex direction="column" alignItems={{ mobile: 'flex-start', sm: 'flex-end' }}>
          <Box {...boxImageStyle}>
            {logoRestaurantUrl && (
              <Image
                alt=""
                src={logoRestaurantUrl}
                fill
                style={{ objectFit: 'contain' }}
                data-testid={formatDataTestId(baseDataTestId, 'Heading-RestaurantLogo')}
                loader={isIVMEnabled() ? akamaiImageLoader : undefined}
                role="img"
              />
            )}
          </Box>
          {hasMenus && (
            <Text
              as="button"
              type="button"
              {...seeMenusLinkLayoutStyle}
              {...getTypographyProps(seeMenusLinkLegacyTypography, seeMenusLinkSemanticTypography)}
              onClick={scrollToMenus}
              data-testid={formatDataTestId(baseDataTestId, 'Heading-ScrollToMenus')}
            >
              {t('upsell.meals.see.menus')}
            </Text>
          )}
        </Flex>
      </Flex>
      {showUpdateMealsButton && (
        <Button
          onClick={updateMealSelection}
          width={250}
          size="md"
          variant="secondary"
          marginBottom="lg"
          data-testid={formatDataTestId(baseDataTestId, 'Update-Meal-Selection')}
        >
          {t('ccui.amend.updateMealSelection')}
        </Button>
      )}
      {notification}
      <Flex
        {...reservationInfoStyle}
        data-testid={formatDataTestId(baseDataTestId, 'Adults-Heading-Wrapper')}
      >
        <Heading
          {...h6HeadingLayoutStyle}
          data-testid={formatDataTestId(baseDataTestId, 'Adults-Heading-Title')}
        >
          <Text {...getTypographyProps(h6HeadingLegacyTypography, h6HeadingSemanticTypography)}>
            {t('upsell.meals.adult.title')}
          </Text>
        </Heading>
        <Text
          {...headingValueLayoutStyle}
          {...getTypographyProps(headingValueLegacyTypography, headingValueSemanticTypography)}
          data-testid={formatDataTestId(baseDataTestId, 'Adults-Heading-Values')}
        >{`(${adults} ${getAdultsPluralLabel(adults)}, ${nights} ${getNightsPluralLabel})`}</Text>
      </Flex>
      {adultsMeals.map((mealItem: MealItemExtension) => {
        let controller = null;
        //this bellow should be enough since soft bundles apply the same meal to everyone (may need changes for multiple rooms if it will be the case)
        const isSelected = selectedMeals?.[selectedRoom]?.adults.includes(mealItem.id ?? '');
        const isMealIncludedInSoftBundle = softBundleIncludedMeal?.id === mealItem.id;
        const outcomePrice = ((mealItem?.price ?? 0) - selectedMealPrice) * adults * nights;

        if (shouldDisplaySoftBundleMealVariant) {
          controller = renderSoftBundlesController(mealItem, isSelected, outcomePrice);
          analytics.update({
            ...window.analyticsData,
            bundleRevenue: outcomePrice.toString(),
          });
        } else if (isForEntireStay) {
          const isMealForEntireStay = isshowKidsMealsFreeFlag
            ? isAdultMealForEntireStaySelections(mealItem.id ?? '', adultsMeals, selectedMeals)
            : isAdultMealForEntireStay(mealItem.id ?? '');

          const totalAdults = adultsPerRoom.reduce((sum, count) => sum + count, 0);

          controller = isAdultHasMealsFree ? (
            <MealSelectionCount
              value={isMealForEntireStay ? totalAdults : 0}
              label={getAdultsPluralLabel(totalAdults)}
            />
          ) : (
            renderButtonController({
              labelController: isMealForEntireStay
                ? t('upsell.extras.remove')
                : t('upsell.extras.add'),

              onClick: () => {
                if (isMealForEntireStay) {
                  if (isshowKidsMealsFreeFlag) {
                    handleRemoveAdultMealsSelections(mealItem, selectedMeals, setSelectedMeals);
                  } else {
                    handleRemoveAdultMeals();
                  }
                } else if (isshowKidsMealsFreeFlag) {
                  handleOnAddAdultMealsSelections(
                    mealItem,
                    selectedMeals,
                    adultsMeals,
                    adultsPerRoom,
                    childrenMeals,
                    setSelectedMeals
                  );
                } else {
                  handleOnAddAdultMeals(mealItem);
                }
              },
              dataTestId: formatDataTestId(baseDataTestId, 'Adults-MealItem-Button'),
              isDisabled: isshowKidsMealsFreeFlag
                ? !isMealForEntireStay
                  ? isAvailableAdultMealsForEntireStay(mealItem, selectedMeals, adultsMeals)
                  : false
                : false,
            })
          );
        } else {
          controller = renderAddSubtractController({
            prefixDataTestId: formatDataTestId(baseDataTestId, 'Adults-MealItem'),
            mealId: mealItem.id ?? '',
            labelController: getAdultsPluralLabel(noOfSelectedAdultsMeals(mealItem.id ?? '')),
            onSubtract: () => {
              isshowKidsMealsFreeFlag
                ? handleOnSubtractForAdultSelections(mealItem)
                : handleOnSubtractForAdults(mealItem.id ?? '');
              onRemoveMeal?.(mealItem.id ?? '', isForEntireStay);
            },
            onPlus: () => {
              handleOnPlusForAdults(mealItem.id ?? '');
              onAddMeal?.(mealItem.id ?? '', isForEntireStay);
            },
            availableMeals: isshowKidsMealsFreeFlag
              ? availableAdultsMealSelections(mealItem)
              : availableAdultsMeals,
            numberMeals: noOfSelectedAdultsMeals(mealItem.id ?? ''),
            hasPromoMeal,
          });
        }
        return (
          <Box
            mt="md"
            key={mealItem.id}
            data-testid={formatDataTestId(baseDataTestId, 'Adults-MealItem-Wrapper')}
          >
            <MealItem
              showFreeFoodKids={showFreeFoodKids}
              freeBreakfastOption={mealItem.freeBreakfastOption}
              allergyInfoSrc={mealItem.allergyInfoSrc}
              imageUrl={mealItem.imageSrc}
              prefixDataTestId={formatDataTestId(baseDataTestId, 'Adults-MealItem')}
              title={mealItem.name}
              price={mealItem.price}
              currentLanguage={currentLang}
              currency={mealItem.currency}
              numberNights={nights}
              description={mealItem.description}
              isForEntireStay={isForEntireStay}
              totalPrice={isForEntireStay ? mealItem.totalPriceForEntireStay : mealItem.totalPrice}
              allergyInfoLabel={mealItem.allergyInfoLabel}
              controller={controller}
              hasPromoMeal={hasPromoMeal}
              upsellType={mealItem?.upsellType ?? ''}
              isSelected={isSelected}
              isIncluded={isMealIncludedInSoftBundle}
              adultsNumber={adults}
              outcomePrice={outcomePrice}
              isSoftBundlesVisible={shouldDisplaySoftBundleMealVariant}
              isFree={mealItem?.isFree}
              basePrice={mealItem?.basePrice}
              isAdultHasMealsFree={isAdultHasMealsFree}
            />
          </Box>
        );
      })}

      {showFreeFoodKids && childrenMeals.length > 0 && (
        <>
          <Flex
            {...childrenReservationInfoStyle}
            data-testid={formatDataTestId(baseDataTestId, 'Children-Heading-Wrapper')}
          >
            <Heading
              {...h6HeadingLayoutStyle}
              data-testid={formatDataTestId(baseDataTestId, 'Children-Heading-Title')}
            >
              <Text
                as="span"
                {...getTypographyProps(h6HeadingLegacyTypography, h6HeadingSemanticTypography)}
              >
                {t('upsell.meals.child.title')}
              </Text>
            </Heading>
            <Text
              {...headingValueLayoutStyle}
              {...getTypographyProps(headingValueLegacyTypography, headingValueSemanticTypography)}
              mt="0.75rem"
              data-testid={formatDataTestId(baseDataTestId, 'Children-Heading-Values')}
            >{`(${kids} ${getChildrenPluralLabel(kids)}, ${nights} ${getNightsPluralLabel})`}</Text>
          </Flex>
          {childrenMeals.map((mealItem: MealKids) => {
            let controller = null;

            if (isForEntireStay) {
              const isMealForEntireStay = isChildrenMealForEntireStay(mealItem.id ?? '');
              const totalChildren = kidsPerRoom.reduce((sum, count) => sum + count, 0);
              controller = isAdultHasMealsFree ? (
                <MealSelectionCount
                  value={isMealForEntireStay ? totalChildren : 0}
                  label={getChildrenPluralLabel(totalChildren)}
                />
              ) : (
                renderButtonController({
                  labelController: isMealForEntireStay
                    ? t('upsell.extras.remove')
                    : t('upsell.extras.add'),
                  onClick: () => {
                    const isFree = isshowKidsMealsFreeFlag;

                    if (isMealForEntireStay) {
                      return isFree
                        ? handleRemoveChildrenMealsSelections(
                            mealItem,
                            selectedMeals,
                            setSelectedMeals
                          )
                        : handleRemoveChildrenMeals();
                    }

                    return isFree
                      ? handleOnAddChildrenMealsSelections(
                          mealItem,
                          selectedMeals,
                          kidsPerRoom,
                          setSelectedMeals
                        )
                      : handleOnAddChildrenMeals(mealItem);
                  },
                  isDisabled: isshowKidsMealsFreeFlag
                    ? isAvailableChildrenMealsForEntireStay(mealItem?.id as string)
                    : isAvailableChildrenMealsForEntire,
                  dataTestId: formatDataTestId(baseDataTestId, 'Children-MealItem-Button'),
                })
              );
            } else {
              controller = renderAddSubtractController({
                prefixDataTestId: formatDataTestId(baseDataTestId, 'Children-MealItem'),
                mealId: mealItem.id ?? '',
                labelController: getChildrenPluralLabel(
                  noOfSelectedChildrensMeals(mealItem.id ?? '')
                ),
                onSubtract: () => {
                  handleOnSubtractForChildren(mealItem.id ?? '');
                  onRemoveMeal?.(mealItem.id ?? '', isForEntireStay);
                },
                onPlus: () => {
                  handleOnPlusForChildren(mealItem.id ?? '');
                  onAddMeal?.(mealItem.id ?? '', isForEntireStay);
                },
                availableMeals: isshowKidsMealsFreeFlag
                  ? availableChildrenMealSelections(mealItem)
                  : availableChildrenMeals,
                numberMeals: noOfSelectedChildrensMeals(mealItem.id ?? ''),
              });
            }

            const selectedAdultIds = selectedRoomMeals?.adults ?? [];

            const isLinkedToSelectedAdult = !!(
              isAdultHasMealsFree &&
              adultsMeals?.some(
                (adult) =>
                  adult?.freeBreakfastCode === mealItem.id &&
                  selectedAdultIds.includes(adult?.id as string)
              )
            );

            return (
              <Box
                mt="md"
                key={mealItem.id}
                data-testid={formatDataTestId(baseDataTestId, 'Children-MealItem-Wrapper')}
              >
                <MealItem
                  showFreeFoodKids={showFreeFoodKids}
                  freeBreakfastOption={false}
                  allergyInfoSrc={mealItem.allergyInfoSrc}
                  allergyInfoLabel={mealItem.allergyInfoLabel}
                  imageUrl={mealItem.imageSrc}
                  title={mealItem.name}
                  currentLanguage={currentLang}
                  description={mealItem.description}
                  prefixDataTestId={formatDataTestId(baseDataTestId, 'Children-MealItem')}
                  controller={controller}
                  isAdultHasMealsFree={isAdultHasMealsFree}
                  isSelected={isLinkedToSelectedAdult}
                />
              </Box>
            );
          })}
        </>
      )}
    </Box>
  );

  function renderSoftBundlesController(
    mealItem: MealItemExtension,
    isSelected: boolean,
    outcomePrice: number
  ) {
    return (
      <Flex direction="column" alignItems="flex-end">
        {renderButtonController({
          labelController: isSelected ? (
            t('upsell.extras.added')
          ) : outcomePrice <= 0 ? (
            <Flex>
              <Icon svg={<ChangeArrows color="var(--chakra-colors-baseWhite)" />} />
              <Text ml="0.5rem">{t('upsell.extras.change')}</Text>
            </Flex>
          ) : (
            <Flex>
              <Icon svg={<ChangeArrows color="var(--chakra-colors-baseWhite)" />} />
              <Text ml="0.5rem">{t('upsell.extras.upgrade')}</Text>
            </Flex>
          ),
          onClick: () => {
            if (isSelected) {
              return;
            }
            handleChangeSoftBundleMeal(mealItem);
          },
          dataTestId: isSelected ? 'selected-meal-soft-bundle' : 'add-meal-soft-bundle',
          variant: isSelected ? 'tertiary' : 'secondary',
          style: {
            width: { mobile: '100%', md: '32' },
            _focus: { boxShadow: 'none', bgColor: 'baseWhite' },
            _active: { boxShadow: 'none', bgColor: 'baseWhite' },
            cursor: isSelected ? 'default' : 'pointer',
          } as ButtonProps,
        })}
      </Flex>
    );
  }

  //<editor-fold desc="Render Button Controoler" defaultstate="collapsed">
  function renderButtonController({
    labelController,
    onClick,
    isDisabled = false,
    dataTestId,
    variant = 'secondary',
    style,
  }: {
    labelController: string | React.JSX.Element;
    onClick: () => void;
    isDisabled?: boolean;
    dataTestId: string;
    variant?: 'secondary' | 'tertiary';
    style?: StyleProps;
  }) {
    return (
      <Button
        onClick={onClick}
        width={32}
        variant={variant}
        size="sm"
        isDisabled={isDisabled}
        data-testid={dataTestId}
        {...style}
      >
        {labelController}
      </Button>
    );
  }

  //</editor-fold>

  //<editor-fold desc="Render Add Subtract Controoler" defaultstate="collapsed">

  function renderAddSubtractController({
    prefixDataTestId,
    labelController,
    onPlus,
    onSubtract,
    availableMeals,
    numberMeals,
    hasPromoMeal = false,
  }: {
    prefixDataTestId: string;
    mealId: string;
    labelController: string;
    onPlus: () => void;
    onSubtract: () => void;
    availableMeals: number;
    numberMeals: number;
    hasPromoMeal?: boolean;
  }) {
    const isPlusButtonHidden = hasPromoMeal || (isAdultHasMealsFree as boolean);
    const isSubtractButtonHidden = hasPromoMeal || (isAdultHasMealsFree as boolean);

    return (
      <AddSubtract
        prefixDataTestId={formatDataTestId(prefixDataTestId, 'AddSubtractControls')}
        onSubtract={onSubtract}
        onPlus={onPlus}
        isSubtractDisable={numberMeals === 0}
        value={numberMeals}
        label={labelController}
        isPlusDisable={availableMeals <= 0}
        isPlusHidden={isPlusButtonHidden}
        isSubtractHidden={isSubtractButtonHidden}
      />
    );
  }

  //</editor-fold>
}

export function scrollToMenus() {
  const element = document.getElementById('menus');
  if (element) {
    element.scrollIntoView({
      block: 'start',
      behavior: 'smooth',
    });
  }
}

//<editor-fold desc="Style" defaultstate="collapsed">

const boxImageStyle = {
  mt: { mobile: 'md', sm: 0 },
  position: 'relative',
  w: '6.75rem',
  h: '3rem',
} as BoxProps;

const mealsTitleLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const mealsTitleLegacyTypography = {
  fontWeight: 'semibold',
  fontSize: { mobile: 'xl', sm: '2xl' },
  lineHeight: { mobile: '3', sm: '4' },
} as TextProps;

const mealsTitleSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const logoWrapperStyle = {
  flexDir: { mobile: 'column', sm: 'row' },
  justifyContent: { mobile: 'center', sm: 'space-between' },
  alignItems: 'flex-start',
} as FlexProps;

const h6HeadingLayoutStyle = {
  as: 'h6',
  pr: 'sm',
  color: 'darkGrey1',
} as HeadingProps;

const h6HeadingLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'semibold',
  lineHeight: '3',
  fontFamily: 'body',
} as HeadingProps;

const h6HeadingSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as HeadingProps;

const headingValueLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const headingValueLegacyTypography = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '2',
  fontFamily: 'body',
} as TextProps;

const headingValueSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const reservationInfoStyle = {
  mt: { mobile: 'lg', sm: 'md', md: '0' },
  mb: 'md',
  direction: 'row',
  alignItems: 'center',
  flexWrap: 'wrap',
} as FlexProps;

const childrenReservationInfoStyle = {
  ...reservationInfoStyle,
  mt: { mobile: 'xl', sm: '3xl', md: 'xl' },
} as FlexProps;

const seeMenusLinkLayoutStyle = {
  textDecoration: 'underline',
  color: 'btnSecondaryEnabled',
  pt: 'xs',
  cursor: 'pointer',
  whiteSpace: 'nowrap',
  background: 'none',
  border: 'none',
  padding: '0',
} as TextProps;

const seeMenusLinkLegacyTypography = {
  fontSize: { mobile: 'sm', md: 'md' },
  lineHeight: { mobile: '2', md: '3' },
  fontWeight: 'normal',
} as TextProps;

const seeMenusLinkSemanticTypography = {
  textStyle: 'link-m-regular',
} as TextProps;
//</editor-fold>
