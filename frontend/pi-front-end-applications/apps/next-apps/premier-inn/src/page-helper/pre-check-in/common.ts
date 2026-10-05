import { QueryClient } from '@tanstack/react-query';
import {
  BC_RESERVATION_STATUS,
  AmendConfInput,
  Query,
  FIND_BOOKING_SOURCE_PMS,
  FIND_BOOKING_COOKIE_NAME_KEY,
  GET_PRECHECKIN_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  HotelBrand,
} from '@whitbread-eos/api';
import {
  graphQLRequest,
  getFindBookingToken,
  setCookie,
  isStringValid,
  formatDate,
} from '@whitbread-eos/utils';
import { compareAsc, addDays, isToday } from 'date-fns';
import { NextRouter } from 'next/router';
import { Dispatch, SetStateAction } from 'react';

const ALLOWED_BOOKING_STATUSES = [BC_RESERVATION_STATUS.COMPLETED as string];

export const regFormInit = {
  bookingNumber: '',
  firstName: '',
  lastName: '',
  arrivalDate: '',
  scheduledDate: '',
  address: '',
  city: '',
  country: '',
  postalCode: '',
  dateOfBirth: '',
  nationality: '',
  passport: '',
  dependent: '0',
  dependents: [],
};

export const dependents = [
  { id: '0', label: 0 },
  { id: '1', label: 1 },
  { id: '2', label: 2 },
  { id: '3', label: 3 },
];

export const DATE_FORMAT = 'dd MMMM yyyy';

export type BookingDataType = {
  reservationByIdList: {
    reservationId: string;
    reservationStatus?: string;
    billing?: {
      address?: {
        addressLine1?: string;
        addressLine2?: string;
        addressLine3?: string;
        addressLine4?: string;
        cityName?: string;
        companyName?: string;
        postalCode?: string;
        countryCode?: string;
        country?: string;
        addressType?: string;
      };
      title?: string;
      telephone?: string;
      firstName?: string;
      lastName?: string;
      email?: string;
      landline?: string;
    };
    preCheckInStatus?: boolean;
    deRegCardCompleted?: boolean;
    reservationGuestList: {
      givenName: string;
      surName: string;
      email: string;
      nameTitle: string;
      additionalDetails: {
        dob: string;
        passportNumber: string;
        nationality: string;
      };
      homeAddress: {
        addressType: string;
        addressLine1: string;
        addressLine2?: string;
        addressLine3?: string;
        addressLine4?: string;
        cityName: string;
        countryCode: string;
        postalCode: string;
        addressId: string;
      };
      address: {
        addressType: string;
        addressLine1: string;
        addressLine2?: string;
        addressLine4?: string;
        cityName: string;
        countryCode: string;
        postalCode: string;
      };
      profileId: string;
    }[];
    gdsReferenceNumber: string;
    roomStay: {
      arrivalDate: string;
      departureDate: string;
      childrenNumber: number;
      adultsNumber: number;
      roomType: string;
      roomExtraInfo: {
        roomName: string;
      };
    };
    additionalGuestInfo?: {
      purposeOfStay: string;
    };
  }[];
  hotelId: string;
  hotelName: string;
  bookingFlowId: string;
  rateMessage: string;
  bookingReference: string;
  basketReference: string;
};

export interface Dependent {
  nationality: {
    value: string;
    label: string;
    image: string;
  };
  passport: string;
  firstname: string;
  lastname: string;
  dateofbirth: Date;
}

export const formatBookingData = (bookingData: BookingDataType) => {
  const { bookingReference, reservationByIdList, hotelId, hotelName } = bookingData;
  const noOfRooms = reservationByIdList?.length;

  const roomsData = reservationByIdList?.map((room, index) => {
    const { arrivalDate, departureDate, childrenNumber, adultsNumber, roomExtraInfo } =
      room.roomStay || {};
    const {
      givenName: firstName,
      surName: lastName,
      additionalDetails,
    } = room?.reservationGuestList[0] || {};

    const address = room?.reservationGuestList[0]?.homeAddress || {};

    const roomName = roomExtraInfo?.roomName;

    const {
      dob: dateOfBirth,
      passportNumber: passport,
      nationality: nationalityCode,
    } = additionalDetails || {};

    const {
      addressLine1,
      addressLine4,
      cityName,
      countryCode: country,
      postalCode,
      addressId,
    } = address || {};

    const city = addressLine4 || cityName;

    let dependentsList: any = [];

    room?.reservationGuestList?.slice(1).forEach((dependent) => {
      const { givenName: firstname, surName: lastname, additionalDetails } = dependent || {};

      const {
        dob: dateofbirth,
        passportNumber: passport,
        nationality: nationalityCode,
      } = additionalDetails || {};

      dependentsList.push({
        firstname,
        lastname,
        dateofbirth: dateofbirth && new Date(dateofbirth),
        nationality: { value: nationalityCode, label: '' },
        passport,
      });
    });
    const noOfDependents = dependentsList.length
      ? dependentsList.length
      : adultsNumber + childrenNumber - 1;

    if (!dependentsList.length && noOfDependents) {
      dependentsList = new Array(noOfDependents).fill({}); //to create empty dependent fields
    }

    return replaceNullsWithEmptyStrings({
      ...regFormInit,
      noOfRooms,
      roomNo: index,
      reservationId: room.reservationId,
      reservationStatus: room?.reservationStatus,
      bookingReference,
      preCheckInStatus: room.preCheckInStatus || false,
      deRegCardCompleted: room.deRegCardCompleted || false,
      hotelId,
      hotelName,
      firstName,
      lastName,
      arrivalDate: arrivalDate && new Date(arrivalDate),
      departureDate: departureDate && new Date(departureDate),
      roomName,
      childrenNumber,
      adultsNumber,
      address: addressLine1,
      city,
      country,
      postalCode,
      dateOfBirth: dateOfBirth && new Date(dateOfBirth),
      nationality: { value: nationalityCode, label: '' },
      passport,
      dependent: noOfDependents ? noOfDependents.toString() : '0',
      dependents: dependentsList,
      addressId,
    });
  });
  return roomsData;
};
export interface ErrorType extends Error {
  response?: {
    status?: number;
    data?: any;
  };
}

export type GenericObject = {
  [key: string]: string | number | boolean | object | undefined;
};

export const replaceNullsWithEmptyStrings = (obj: GenericObject): GenericObject => {
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      const value = obj[key];
      if (Array.isArray(value)) {
        obj[key] = value.map((item) => {
          if (item === null || item === undefined) return '';
          if (typeof item === 'object') return replaceNullsWithEmptyStrings(item as GenericObject);

          return item;
        });
      }
      if (
        value === null ||
        value === undefined ||
        (typeof value === 'string' && value.includes('null'))
      ) {
        obj[key] = '';
      }
    }
  }
  return obj;
};

export interface SpinnersStatusType {
  pageSpinnerStatus: boolean;
  componentSpinnerStatus: boolean;
  roomBookingErrorStatus: boolean;
  reservationErrorStatus: boolean;
}

export interface Reservation {
  basketReference: string;
  hotelId: string;
  reasonForStay?: string;
  sendEmailConfirmation: boolean;
  sendEmailInvoice: boolean;
  preCheckIn: boolean;
  title: string;
  acceptFutureMailing: boolean;
  emailAddress?: string;
  firstName?: string;
  lastName?: string;
  stayingGuests: StayingGuest[];
  companyName?: string;
  addressType?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  cityName?: string;
  postalCode?: string;
  mobile?: string;
  landline?: string;
  language?: string;
  countryCode?: string;
  addressId?: string;
}

export interface StayingGuest {
  sameAsBooker: boolean;
  stayingGuestDetails: StayingGuestDetails;
  reservationId: string;
  isAccompanyingGuest: boolean;
}

export interface StayingGuestDetails {
  title?: string;
  firstName?: string;
  lastName?: string;
  profileId?: string;
  additionalDetails?: {
    dob?: string;
    passportNumber?: string;
    nationality?: string;
  };
  address?: {
    addressType?: string;
    addressLine1?: string;
    addressLine2?: string;
    addressLine3?: string;
    addressLine4?: string;
    countryCode?: string;
    postalCode?: string;
    cityName?: string;
    addressId?: string;
  };
}

export interface HomeAddressType {
  addressType?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  countryCode?: string;
  postalCode?: string;
  cityName?: string;
  addressId?: string;
}

export enum SUBMIT_TYPE {
  SAVE = 'save',
  SUBMIT = 'submit',
}

export interface FormDetails {
  firstName: string;
  lastName: string;
  roomNo: number;
  reservationId: string;
  dependents: Dependent[];
  passport: string;
  nationality: { value: string; label: string };
  dateOfBirth: Date;
  city: string;
  address: string;
  country: string;
  postalCode: string;
}

export const formatAdditionalInformation = (
  dateOfBirth: Date,
  passport: string,
  nationality: { value: string }
) => {
  const additionalDetails: any = {};
  if (dateOfBirth) additionalDetails.dob = formatDate(dateOfBirth.toString(), 'yyyy-MM-dd');
  if (passport) additionalDetails.passportNumber = passport;
  if (nationality?.value) additionalDetails.nationality = nationality.value;
  return additionalDetails;
};

export const formatStayingGuests = (
  bookingConfirmation: BookingDataType,
  formDetails: FormDetails
) => {
  const {
    firstName,
    lastName,
    roomNo,
    reservationId,
    dependents,
    passport,
    nationality,
    dateOfBirth,
    city,
    address,
    country,
    postalCode,
  } = formDetails;
  if (bookingConfirmation) {
    const { billing, reservationGuestList } = bookingConfirmation.reservationByIdList[roomNo];
    const { nameTitle, profileId, homeAddress } = reservationGuestList[0] as {
      nameTitle: string;
      profileId: string;
      homeAddress: HomeAddressType;
    };

    const stayingGuests: StayingGuest[] = [
      {
        stayingGuestDetails: {
          title: nameTitle,
          firstName,
          lastName,
          profileId,
          address: {
            addressLine1: address,
            addressLine2: homeAddress?.addressLine2,
            addressLine3: homeAddress?.addressLine3,
            cityName: city,
            countryCode: country,
            postalCode,
            addressType: homeAddress?.addressType || 'HOME',
            addressId: homeAddress?.addressId,
          },
        },
        sameAsBooker: billing?.lastName === formDetails.lastName,
        reservationId,
        isAccompanyingGuest: false,
      },
    ];

    const sanitizePassport = (passport: string) => (passport?.includes('XXXXX') ? '' : passport);

    if (passport || nationality?.value || dateOfBirth) {
      const additionalDetails = formatAdditionalInformation(
        dateOfBirth,
        sanitizePassport(passport),
        nationality
      );

      stayingGuests[0].stayingGuestDetails = {
        ...stayingGuests[0].stayingGuestDetails,
        additionalDetails,
      };
    }

    dependents.forEach(
      ({ firstname, lastname, nationality, dateofbirth, passport }, dependentIndex) => {
        const { profileId } = reservationGuestList[dependentIndex + 1] || {};
        const guest: StayingGuest = {
          stayingGuestDetails: {
            firstName: firstname,
            lastName: lastname,
          },
          sameAsBooker: false,
          reservationId,
          isAccompanyingGuest: true,
        };
        if (profileId) guest.stayingGuestDetails.profileId = profileId;

        if (passport || nationality?.value || dateofbirth) {
          const additionalDetails = formatAdditionalInformation(
            dateofbirth,
            sanitizePassport(passport),
            nationality
          );

          guest.stayingGuestDetails = {
            ...guest.stayingGuestDetails,
            additionalDetails,
          };
        }
        stayingGuests.push(guest);
      }
    );

    return stayingGuests;
  } else return [];
};

export enum pageStatus {
  EXPIRED = 'EXPIRED',
}

export interface BookType {
  reservationStatus: string;
  departureDate: Date | string;
  arrivalDate: Date | string;
}
export const isValidBookingStatus = (bookingData: BookType[]) => {
  return bookingData.some((booking) => {
    const arrivalDate =
      typeof booking.arrivalDate === 'string' ? new Date(booking.arrivalDate) : booking.arrivalDate;
    const departureDate =
      typeof booking.departureDate === 'string'
        ? new Date(booking.departureDate)
        : booking.departureDate;
    const isArrivalToday = isToday(arrivalDate);
    const today = new Date();
    const maxDate = addDays(today, 7); // arrival should be within or equal to 7 days
    const isReservedWithin7Days = compareAsc(arrivalDate, maxDate) !== 1;
    const isDepartureFuture = compareAsc(departureDate, new Date()) === 1;
    const isAllowedStatus = ALLOWED_BOOKING_STATUSES.includes(booking.reservationStatus);
    return (isArrivalToday || isReservedWithin7Days) && isDepartureFuture && isAllowedStatus;
  });
};

export const formatRegistration = (
  bookingConfirmation: BookingDataType,
  formDetails: any,
  currentLang: string
) => {
  if (
    bookingConfirmation &&
    Object.keys(bookingConfirmation).length &&
    formDetails &&
    bookingConfirmation?.basketReference
  ) {
    const { roomNo, address, city, postalCode, country } = formDetails;
    const { billing, additionalGuestInfo } = bookingConfirmation.reservationByIdList[roomNo];

    const homeAddress =
      bookingConfirmation.reservationByIdList?.[roomNo]?.reservationGuestList?.[0]?.homeAddress ??
      null;

    const reservation: Reservation = {
      basketReference: bookingConfirmation.basketReference,
      hotelId: bookingConfirmation?.hotelId,
      reasonForStay: additionalGuestInfo?.purposeOfStay,
      sendEmailConfirmation: false,
      sendEmailInvoice: false,
      preCheckIn: true,
      title: billing?.title as string,
      acceptFutureMailing: false,
      emailAddress: billing?.email,
      firstName: billing?.firstName,
      lastName: billing?.lastName,
      stayingGuests: formatStayingGuests(bookingConfirmation, formDetails as any),
      companyName: billing?.address?.companyName,
      addressType: homeAddress?.addressType || 'HOME',
      addressLine1: address,
      addressLine2: homeAddress?.addressLine2,
      addressLine3: homeAddress?.addressLine3,
      addressLine4: city,
      postalCode,
      mobile: billing?.telephone,
      landline: billing?.landline,
      language: currentLang,
      countryCode: country,
      addressId: homeAddress?.addressId,
    };

    return reservation;
  }
  return false;
};

const queryClient = new QueryClient();

export const fetchBookingConfirmation = (confirmationInput: {
  basketReference: string;
  language: string;
  country: string;
}) => {
  return queryClient.fetchQuery({
    queryKey: [
      'getBookingConfirmation',
      confirmationInput.basketReference,
      confirmationInput.language,
      confirmationInput.country,
    ],
    queryFn: () => graphQLRequest(GET_PRECHECKIN_BOOKING_INFORMATION, { ...confirmationInput }),
  });
};

export const isGermanHotel = async (hotelId: string, country: string, currentLang: string) => {
  const hotelInfo = await getHotelDetails(hotelId as string, country, currentLang);
  const formattedHotelAddress = formatHotelAddress({
    address: hotelInfo?.hotelInformation?.address,
    brand: hotelInfo?.hotelInformation?.brand,
  });

  return {
    isGerman: [HotelBrand.PID].includes(hotelInfo?.hotelInformation?.brand),
    hotelAddress: formattedHotelAddress,
  };
};

export const getHotelDetails = async (hotelId: string, country: string, language: string) => {
  return queryClient.fetchQuery({
    queryKey: ['GetHotelInformation', hotelId, country, language],
    queryFn: () => graphQLRequest(GET_HOTEL_INFORMATION, { hotelId, language, country }),
  });
};

export interface HandleBookingType {
  query: { bookingReference?: string };
  handleSpinnersStatus: HandleSpinnersStatusType;
  errorStatusObject: ErrorStatusObjectType;
  currentLang: string;
  country: string;
  setBookingConfirmation: React.Dispatch<React.SetStateAction<any>>;
  rooms: { current?: GenericObject[] };
  setDisplayMultiRoomSection: StateVariableBooleanType;
  setFormDetails: React.Dispatch<React.SetStateAction<any>>;
  setDisplayPersonalDetailsSection: StateVariableBooleanType;
  handleRedirect: () => void;
  setHotelAddress: StateVariableStringType;
  isMobilePreRegisteredRepurposeEnabled: boolean;
}
interface Address {
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  postalCode?: string;
}
export interface GetHotelAddressParams {
  address?: Address | null;
  brand?: string;
}

export const formatHotelAddress = ({ address, brand = '' }: GetHotelAddressParams): string => {
  const {
    addressLine1 = '',
    addressLine2 = '',
    addressLine3 = '',
    postalCode = '',
  } = address || {};

  return brand === 'PID'
    ? [addressLine1, postalCode, addressLine2, addressLine3].filter(Boolean).join(', ')
    : [addressLine1, addressLine2, addressLine3, postalCode].filter(Boolean).join(', ');
};

export const handleBooking = ({
  query,
  handleSpinnersStatus,
  errorStatusObject,
  currentLang,
  country,
  setBookingConfirmation,
  rooms,
  setDisplayMultiRoomSection,
  setFormDetails,
  setDisplayPersonalDetailsSection,
  handleRedirect,
  setHotelAddress,
  isMobilePreRegisteredRepurposeEnabled,
}: HandleBookingType) => {
  if (query.bookingReference) {
    handleSpinnersStatus({
      ...errorStatusObject,
      pageSpinnerStatus: true,
    });
    let confirmationInput: AmendConfInput | null = null;

    const bookingReference = query.bookingReference ? String(query.bookingReference) : '';
    const { basketReference } = getFindBookingToken();

    if (bookingReference && basketReference) {
      confirmationInput = {
        basketReference,
        bookingReference,
        language: currentLang,
        country,
      };
      fetchBookingConfirmation(confirmationInput)
        .then(async (response: Query) => {
          const { bookingConfirmation } = response || {};

          setBookingConfirmation(bookingConfirmation as BookingDataType);

          const isItGermanHotel = await isGermanHotel(
            bookingConfirmation?.hotelId as string,
            country,
            currentLang
          );

          const bookingConfirmationData = formatBookingData(bookingConfirmation as BookingDataType);

          const filteredBookings = bookingConfirmationData.map(
            ({ reservationStatus, departureDate, arrivalDate }) => ({
              reservationStatus,
              departureDate,
              arrivalDate,
            })
          );

          const isValidBooking = isValidBookingStatus(filteredBookings as BookType[]);

          if (
            bookingConfirmation &&
            bookingConfirmation.bookingReference &&
            isItGermanHotel.isGerman &&
            isValidBooking
          ) {
            setHotelAddress(isItGermanHotel.hotelAddress);

            handleSpinnersStatus(errorStatusObject);

            if (bookingConfirmationData?.length > 1) {
              rooms.current = bookingConfirmationData;
              setDisplayMultiRoomSection(true);
            } else if (
              isMobilePreRegisteredRepurposeEnabled
                ? bookingConfirmationData[0]?.deRegCardCompleted === false
                : bookingConfirmationData[0]?.preCheckInStatus === false
            ) {
              setFormDetails({ country, ...bookingConfirmationData[0] });
              setDisplayPersonalDetailsSection(true);
            }
          } else {
            handleSpinnersStatus({ ...errorStatusObject, roomBookingErrorStatus: true });
          }
        })
        .catch(() => {
          handleSpinnersStatus({ ...errorStatusObject, roomBookingErrorStatus: true });
        });
    } else {
      handleSpinnersStatus({ ...errorStatusObject, roomBookingErrorStatus: false });
      handleRedirect();
    }
  }
};

export const checkPreCheckInStatusForSingleRoom = (
  bookingConfirmation: BookingDataType,
  isMobilePreRegisteredRepurposeEnabled: boolean
) => {
  if (
    bookingConfirmation?.reservationByIdList?.length === 1 &&
    (isMobilePreRegisteredRepurposeEnabled
      ? bookingConfirmation?.reservationByIdList[0]?.deRegCardCompleted
      : bookingConfirmation?.reservationByIdList[0]?.preCheckInStatus)
  ) {
    return true;
  }
  return false;
};

export type StateVariableBooleanType = React.Dispatch<React.SetStateAction<boolean>>;
export type StateVariableStringType = React.Dispatch<React.SetStateAction<string>>;
export type PushType = NextRouter['push'];
export type HandleSpinnersStatusType = (status: SpinnersStatusType) => void;
export type ErrorStatusObjectType = {
  pageSpinnerStatus: boolean;
  componentSpinnerStatus: boolean;
  roomBookingErrorStatus: boolean;
  reservationErrorStatus: boolean;
};

export const setBookingCookie = (
  cookieName: string,
  cookieValue: object,
  minutesTillExpiry: string
) => {
  if (typeof window !== 'undefined' && cookieName) {
    window.localStorage.setItem(FIND_BOOKING_COOKIE_NAME_KEY, cookieName);
    setCookie(cookieName, window.btoa(JSON.stringify(cookieValue)), Number(minutesTillExpiry));
  }
};

export const handleFindBookingError = (
  errorStatusObject: ErrorStatusObjectType,
  handleSpinnersStatus: HandleSpinnersStatusType
) => {
  handleSpinnersStatus({
    ...errorStatusObject,
    roomBookingErrorStatus: true,
  });
};

export const handleSuccessfulFindBooking = async (
  response: Query,
  currentLang: string,
  country: string,
  setIsPreCheckInSuccessModalOpen: StateVariableBooleanType,
  handleSpinnersStatus: HandleSpinnersStatusType,
  errorStatusObject: ErrorStatusObjectType,
  setReservationStatusError: StateVariableBooleanType,
  pathname: string,
  push: PushType,
  setHotelAddress: StateVariableStringType,
  isMobilePreRegisteredRepurposeEnabled: boolean
) => {
  if (!response?.findBooking) return;

  const { ref, basketReference, cookieName, token, sourcePms, redirectBase, minutesTillExpiry } =
    response.findBooking;
  if (sourcePms === FIND_BOOKING_SOURCE_PMS.OPERA && ref && basketReference) {
    const cookieValue = { token, basketReference, bookingReference: ref };

    setBookingCookie(cookieName as string, cookieValue, minutesTillExpiry as string);

    if (isStringValid(redirectBase)) {
      fetchBookingConfirmation({ basketReference, language: currentLang, country })
        .then(async (response: Query) => {
          const { bookingConfirmation } = response || {};

          if (bookingConfirmation !== undefined) {
            const isItGermanHotel = await isGermanHotel(
              bookingConfirmation?.hotelId as string,
              country,
              currentLang
            );

            if (isItGermanHotel.isGerman) {
              setHotelAddress(isItGermanHotel.hotelAddress);

              if (
                checkPreCheckInStatusForSingleRoom(
                  bookingConfirmation as BookingDataType,
                  isMobilePreRegisteredRepurposeEnabled
                )
              ) {
                setIsPreCheckInSuccessModalOpen(true);
              } else {
                const bookingConfirmationData = formatBookingData(
                  bookingConfirmation as BookingDataType
                );
                const filteredBookings = bookingConfirmationData.map(
                  ({ reservationStatus, departureDate, arrivalDate }) => ({
                    reservationStatus,
                    departureDate,
                    arrivalDate,
                  })
                );
                if (!isValidBookingStatus(filteredBookings as BookType[])) {
                  handleSpinnersStatus({ ...errorStatusObject, reservationErrorStatus: true });
                  setReservationStatusError(true);
                } else {
                  await push(`/${country}/${currentLang}${pathname}?bookingReference=${ref}`);
                }
              }
            } else {
              handleSpinnersStatus({ ...errorStatusObject, reservationErrorStatus: true });
              setReservationStatusError(true);
            }
          }
        })
        .catch(() => {
          handleFindBookingError(errorStatusObject, handleSpinnersStatus);
        });
    }
  } else {
    handleFindBookingError(errorStatusObject, handleSpinnersStatus);
  }
};

export const goToHomePage = (
  path: string,
  setIsPreCheckInSuccessModalOpen: StateVariableBooleanType
) => {
  setIsPreCheckInSuccessModalOpen(false);
  if (typeof window !== 'undefined') {
    window.location.href = path;
  }
};

export const scrollToElement = (elementId: string) =>
  setTimeout(() => {
    const element = document.getElementById(elementId);
    if (element)
      element.scrollIntoView({
        block: 'nearest',
        behavior: 'smooth',
      });
  }, 200);

export const errorStatusObject = {
  pageSpinnerStatus: false,
  componentSpinnerStatus: false,
  roomBookingErrorStatus: false,
  reservationErrorStatus: false,
};

export type SvGstMutationType = {
  mutateAsync: (reservation: any) => Promise<any>;
};

export interface PageStatus {
  EXPIRED: string;
}

export type HandleRedirectType = () => void;

export const handleSave = async ({
  data,
  type,
  handleRedirect,
  bookingConfirmation,
  currentLang,
  country,
  svGstMutation,
  attachFileMutation,
  setSvGstSucessAlert,
  preCheckInStatusMutation,
  setIsPreCheckInSuccessModalOpen,
  setSvGstFailureAlert,
  regCardUrl,
  setBookingConfirmation,
}: {
  data: any;
  type: 'save' | 'submit';
  handleRedirect: HandleRedirectType;
  bookingConfirmation: any;
  currentLang: string;
  country: string;
  svGstMutation: SvGstMutationType;
  attachFileMutation: SvGstMutationType;
  preCheckInStatusMutation: SvGstMutationType;
  setSvGstSucessAlert: StateVariableBooleanType;
  setIsPreCheckInSuccessModalOpen: StateVariableBooleanType;
  setSvGstFailureAlert: StateVariableBooleanType;
  regCardUrl?: string;
  setBookingConfirmation: Dispatch<SetStateAction<any>>;
}) => {
  try {
    const { bookingReference, basketReference } = getFindBookingToken();
    if (!bookingReference) {
      handleRedirect();
      return;
    }
    const reservation = formatRegistration(
      bookingConfirmation as BookingDataType,
      data as any,
      currentLang as string
    );
    if (reservation) await svGstMutation.mutateAsync(reservation);
    if (type === 'save') {
      setSvGstSucessAlert(true);
      scrollToElement('reg-card-save-success-notification');
      const confirmationInput = {
        basketReference,
        bookingReference,
        language: currentLang,
        country,
      };
      fetchBookingConfirmation(confirmationInput).then((res: Query) => {
        const { bookingConfirmation } = res || {};

        setBookingConfirmation(bookingConfirmation as BookingDataType);
      });
    } else {
      const profileId = (reservation as any)?.stayingGuests?.[0]?.stayingGuestDetails?.profileId;

      if (regCardUrl)
        await attachFileMutation.mutateAsync(
          generateFileAttachment({
            reservationId: data.reservationId,
            hotelId: data.hotelId,
            profileId,
            url: regCardUrl,
          })
        );

      await preCheckInStatusMutation.mutateAsync({
        arrivalTime: data.arrivalDate,
        reservationId: data.reservationId,
        hotelId: data.hotelId,
      });
      setIsPreCheckInSuccessModalOpen(true);
    }
  } catch {
    setSvGstFailureAlert(true);
    scrollToElement('reg-card-save-error-notification');
  }
};

export const generateFileAttachment = ({
  reservationId,
  hotelId,
  profileId,
  url,
}: {
  reservationId: string;
  hotelId: string;
  profileId: string;
  url?: string;
}) => {
  const array = new Uint32Array(1);
  crypto.getRandomValues(array);

  const fileId = String(Math.floor((array[0] / 2 ** 32) * 1000000)).padStart(6, '0');

  const fileName = `REG_RES${reservationId}_ID${fileId}_P${profileId}.pdf`;
  const fileAttachment = {
    reservationId,
    hotelId,
    fileAttachment: url,
    global: true,
    overwriteExistingFile: true,
    description: 'Pre-check-in registration card',
    fileName,
  };
  return fileAttachment;
};
