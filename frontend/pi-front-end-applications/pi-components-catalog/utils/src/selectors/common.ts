import type { BFStep, Address, HotelInformation } from '@whitbread-eos/api';
import {
  ReservationById,
  BOOKING_STEPS_MAPPING,
  BookingSummaryHotelInformationProps,
  GuestCountUpdateRateCode,
} from '@whitbread-eos/api';

import { isStringValid } from '../validators';

export const hotelInformationSelector = (
  hotelInformation: HotelInformation
): BookingSummaryHotelInformationProps | null => {
  if (!hotelInformation) {
    return null;
  }

  return {
    hotelName: hotelInformation.name,
    hotelCountry: hotelInformation.address?.country,
    hotelAddress:
      hotelInformation.address && hotelInformation.brand !== 'PID'
        ? Object.keys(hotelInformation.address)
            .filter((key) => key !== 'country')
            .reduce((prev: string[], current: string) => {
              if (isStringValid(hotelInformation.address[current as keyof Address] ?? ''))
                prev.push(hotelInformation.address[current as keyof Address] ?? '');
              return prev;
            }, [])
        : formatDEAddress(hotelInformation.address),
    hotelBrand: hotelInformation.brand,
  };
};

export const enhanceCheckoutStepList = (listSteps: BFStep[], activePage: string) => {
  const lastStep: BFStep = listSteps.slice(-1)[0]; //the step that will not be shown
  const filteredSteps = listSteps.slice(0);

  return {
    steps: filteredSteps.slice(0, -1).map((step: BFStep) => ({
      id: Number(step.step),
      title: step?.title,
    })),
    activeStep:
      activePage === lastStep?.id
        ? filteredSteps.length
        : filteredSteps.findIndex((step: BFStep) => {
            return BOOKING_STEPS_MAPPING[step.id] === activePage;
          }) + 1,
  };
};

export const bookingGuestCount = (reservationList: ReservationById[]): GuestCountUpdateRateCode =>
  reservationList.reduce(
    (acc: GuestCountUpdateRateCode, current: ReservationById) => {
      acc.adultsNumber.push(current?.roomStay?.adultsNumber ?? 0);
      acc.childrenNumber.push(current?.roomStay?.childrenNumber ?? 0);
      return acc;
    },
    { adultsNumber: [], childrenNumber: [] }
  );
const formatDEAddress = (address: Address) => {
  if (!address) {
    return [];
  }
  const formattedAddress: string[] = [];
  if (isStringValid(address['addressLine1'])) {
    formattedAddress.push(address['addressLine1']);
  }
  if (isStringValid(address['postalCode']) && isStringValid(address['addressLine2'])) {
    formattedAddress.push(address['postalCode'] + ' ' + address['addressLine2']);
  } else if (isStringValid(address['postalCode'])) {
    formattedAddress.push(address['postalCode']);
  } else if (isStringValid(address['addressLine2'])) {
    formattedAddress.push(address['addressLine2']);
  }
  if (isStringValid(address['addressLine3'])) {
    formattedAddress.push(address['addressLine3']);
  }
  return formattedAddress;
};
