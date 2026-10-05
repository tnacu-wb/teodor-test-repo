import type {
  BCReservationListItem,
  MealsSelection,
  MealsSelectionDetailsPerRoom,
} from '@whitbread-eos/api';

import { ternaryCondition } from '../validators';

export const getRoomGroups = (
  rez: BCReservationListItem,
  t: (x: string, y?: { [key: string]: string }) => string
) => {
  const adults = rez?.roomStay?.adultsNumber;
  const children = rez?.roomStay?.childrenNumber;
  const isAdults = Boolean(adults);
  const isChildren = Boolean(children);
  return (
    ternaryCondition(
      isAdults,
      ternaryCondition(
        adults > 1,
        `${adults} ${t('account.dashboard.adults')}`,
        `${adults} ${t('account.dashboard.adult')}`
      ),
      ''
    ) +
    (isAdults && isChildren ? ', ' : '') +
    ternaryCondition(
      isChildren,
      ternaryCondition(
        children > 1,
        `${children} ${t('account.dashboard.children')}`,
        `${children} ${t('account.dashboard.child')}`
      ),
      ''
    )
  );
};

export const getRoomTotalprice = (rez: BCReservationListItem) => {
  return rez?.roomStay?.roomPrice + getRoomExtrasTotal(rez);
};

export const getRoomExtrasTotal = (rez: BCReservationListItem) => {
  let extrasTotal = 0;
  rez?.reservationPackageList?.forEach((pkg) => {
    const noPackages =
      !pkg.description.includes('Charity') &&
      !pkg.description.includes('City') &&
      !pkg.description.includes('Early Check In') &&
      !pkg.description.includes('Late Check Out 2pm');

    const extrasPackages =
      !pkg.description.includes('Charity') &&
      !pkg.description.includes('City') &&
      (pkg.description.includes('Early Check In') ||
        pkg.description.includes('Late Check Out 2pm'));

    return (extrasTotal += noPackages
      ? pkg.computedPrice * rez?.roomStay?.ratesPerNight?.length
      : extrasPackages
        ? pkg.computedPrice
        : 0);
  });
  return isNaN(extrasTotal) ? 0 : extrasTotal;
};

export const getMealPrice = (mealsSelections?: MealsSelectionDetailsPerRoom) => {
  const { adultsMeals = [], childrenMeals = [] } = ternaryCondition(
    mealsSelections !== undefined,
    mealsSelections,
    {}
  );
  let mealTotal = 0;
  adultsMeals?.forEach((meal: MealsSelection) => {
    const { noSelections, price } = meal;
    mealTotal += Number(noSelections) * ternaryCondition(price !== undefined, price, 0);
  });
  childrenMeals?.forEach((meal: MealsSelection) => {
    const { noSelections, price } = meal;
    mealTotal += Number(noSelections) * ternaryCondition(price !== undefined, price, 0);
  });
  return mealTotal;
};

export const getAdultMealDescription = (
  adultMeals: MealsSelection[],
  t: (x: string, y?: { [key: string]: string }) => string
) => {
  const adultMealDescription: string[] = [];
  adultMeals?.forEach((meal: MealsSelection) => {
    const { noSelections, title } = meal;
    adultMealDescription.push(
      `${noSelections} ${ternaryCondition(
        Number(noSelections) > 1,
        t('account.dashboard.adults'),
        t('account.dashboard.adult')
      )} ${title}`
    );
  });
  return adultMealDescription;
};

export const getChildrenMealDescription = (
  childrenMeals: MealsSelection[],
  t: (x: string, y?: { [key: string]: string }) => string
) => {
  const childrenMealDescription: string[] = [];
  childrenMeals?.forEach((meal: MealsSelection) => {
    const { noSelections, title } = meal;
    childrenMealDescription.push(
      `${noSelections} ${ternaryCondition(
        Number(noSelections) > 1,
        t('account.dashboard.children'),
        t('account.dashboard.child')
      )} ${title}`
    );
  });
  return childrenMealDescription;
};
