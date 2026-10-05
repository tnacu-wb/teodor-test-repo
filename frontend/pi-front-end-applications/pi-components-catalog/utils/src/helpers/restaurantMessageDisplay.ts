import type { Restaurant, AncillaryCloseout, Items } from '@whitbread-eos/api';

import { isAncillaryCloseoutItemActiveForStay } from '../selectors/ancillaries';

export interface RestaurantMessageDisplayParams {
  restaurant: Restaurant | null | undefined;
  ancillaryCloseoutData: AncillaryCloseoutWithRestaurantCopy | null | undefined;
  arrivalDate: string | null | undefined;
  departureDate: string | null | undefined;
}

export interface RestaurantMessageDisplay {
  hasCustomRestaurantCopy: boolean;
  showRestaurantMessage: boolean;
  displayTitle: string;
  displayDescription: string;
}

// noMealsHeading/noMealsMessage live per-item until the generated Items type includes them
export type ItemsWithRestaurantCopy = Items & {
  noMealsHeading?: string | null;
  noMealsMessage?: string | null;
};

export type AncillaryCloseoutWithRestaurantCopy = Omit<AncillaryCloseout, 'items'> & {
  items?: Array<ItemsWithRestaurantCopy | null> | null;
};

/**
 * Computes restaurant message display logic with custom closeout copy support.
 * Prioritizes new custom fields (noMealsHeading/noMealsMessage) over legacy restaurant fields.
 * Caller is responsible for applying translation defaults.
 */
export const getRestaurantMessageDisplay = ({
  restaurant,
  ancillaryCloseoutData,
  arrivalDate,
  departureDate,
}: RestaurantMessageDisplayParams): RestaurantMessageDisplay => {
  const hasRestaurantIssue = !!restaurant?.restaurantNotFound || !!restaurant?.noMealsFound;

  // Only items whose closeout window overlaps the guest's stay are eligible for custom copy
  const dateRelevantCloseoutItems =
    arrivalDate && departureDate
      ? ancillaryCloseoutData?.items?.filter(
          (item): item is ItemsWithRestaurantCopy =>
            item != null && isAncillaryCloseoutItemActiveForStay(item, arrivalDate, departureDate)
        )
      : [];

  // First date-relevant item carrying complete custom copy wins; requires BOTH fields non-empty to avoid mixed messaging
  const customCloseoutCopyItem = dateRelevantCloseoutItems?.find(
    (item) => !!item?.noMealsHeading?.trim() && !!item?.noMealsMessage?.trim()
  );
  const hasCustomRestaurantCopy = !!customCloseoutCopyItem;

  // Only show message if there's an actual restaurant issue
  const showRestaurantMessage = hasRestaurantIssue;

  // Compute display text if there's a restaurant issue OR ancillary closeout with custom copy
  const hasDisplayableMessage = hasRestaurantIssue || hasCustomRestaurantCopy;

  // Helper to resolve display text: if both custom fields present use custom,
  // else fall back to legacy (if restaurant issue), else empty
  const getDisplayText = (
    customOption: string | null | undefined,
    legacyOption: string | null | undefined,
    hasCompleteCustom: boolean,
    shouldUseLegacy: boolean
  ): string => {
    // If we have complete custom copy, use this field's custom value
    if (hasCompleteCustom) {
      return customOption?.trim() || '';
    }
    // Otherwise, fall back to legacy if there's a restaurant issue
    if (shouldUseLegacy) {
      return legacyOption?.trim() || '';
    }
    return '';
  };

  const displayTitle = hasDisplayableMessage
    ? getDisplayText(
        customCloseoutCopyItem?.noMealsHeading,
        restaurant?.messageHeader,
        hasCustomRestaurantCopy,
        hasRestaurantIssue
      )
    : '';

  const displayDescription = hasDisplayableMessage
    ? getDisplayText(
        customCloseoutCopyItem?.noMealsMessage,
        restaurant?.messageDescription,
        hasCustomRestaurantCopy,
        hasRestaurantIssue
      )
    : '';

  return {
    hasCustomRestaurantCopy,
    showRestaurantMessage,
    displayTitle,
    displayDescription,
  };
};
