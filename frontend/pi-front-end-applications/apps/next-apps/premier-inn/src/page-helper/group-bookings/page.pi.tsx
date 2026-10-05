import { Box, Container, Flex, Heading } from '@chakra-ui/react';
import {
  CREATE_GROUP_BOOKING,
  CreateGroupBookingCriteria,
  PageName,
  SearchBrandType,
  StaticHotelType,
} from '@whitbread-eos/api';
import { FormProps, getLogoByBrand, Notification, Alert } from '@whitbread-eos/atoms';
import { SEO as Seo, BackToPage } from '@whitbread-eos/molecules';
import {
  formatDate,
  renderSanitizedHtml,
  useCustomLocale,
  useMutationRequest,
  useRestQueryRequest,
  analytics,
  getNightsNumber,
} from '@whitbread-eos/utils';
import { add } from 'date-fns';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import dynamic from 'next/dynamic';
import { SetStateAction, useCallback, useState, useEffect } from 'react';

import { GroupBookingFormConfig } from './Config';
import { BREAKFAST, MEALDEAL } from './Config/GroupBookingFormConfig';
import GroupBookingsConfirmation from './Confirmation/GroupBookingsConfirmation';

type AutocompleteHotelType = {
  value: string;
  component: string;
};

interface GroupBookingsPageProps {
  defaultValues?: FormProps['defaultValues'];
}

const FormWithAccordian = dynamic(
  async () => {
    const { FormWithAccordian } = await import('@whitbread-eos/atoms');
    return { default: FormWithAccordian };
  },
  {
    ssr: false,
  }
);

export default function GroupBookingsPage({ defaultValues }: GroupBookingsPageProps) {
  const { t } = useTranslation();

  const [accordionIndex, setAccordionIndex] = useState<number>(0);
  const [hotels, setHotels] = useState<AutocompleteHotelType[]>([]);
  const [contactName, setContactName] = useState<string>('');
  const [contactEmail, setContactEmail] = useState<string>('');

  // hide minimum 10 rooms notification intially
  const [islessThanMinCount, setIslessThanMinCount] = useState<boolean>(false);
  const { language: currentLang, country: currentCountry } = useCustomLocale();
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const [formValues, setFormValues] = useState<FormProps['defaultValues']>(
    defaultValues ?? {
      title: '',
      firstName: '',
      lastName: '',
      phoneNumber: '',
      emailAddress: '',
      packageType: 'BREAKFAST',
      isPackageTypeBf: true,
      packageTypeCheckbox: true,
      hotels: '',
      isSchoolOrYouth: false,
      isAccessibleRoom: false,
      isTravellingWithChild: false,
      comments: '',
      singleOccupancy: 0,
      doubleOccupancy: 0,
      twinRooms: 0,
      familyOf21A1C: 0,
      familyOf32A1C: 0,
      familyOf31A2C: 0,
      familyOf42A2C: 0,
      accessibleSingle: 0,
      accessibleDouble: 0,
      accessibleTwin: 0,
      RoomTotalCount: 0,
    }
  );

  // Hotels list for autocomplete
  const {
    data: hotelsData,
    isSuccess: hotelsRequestSuccess,
    isError: hotelsRequestError,
  } = useRestQueryRequest(
    ['groupBookingHotels', currentLang, currentCountry],
    'GET',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/hotels?country=${currentCountry}&language=${currentLang}`,
    undefined,
    { staleTime: 1000 * 60 * 5 }
  );

  const {
    mutation: createGroupBookingMutation,
    isLoading: createGroupBookingLoading,
    isSuccess: createGroupBookingSuccess,
    isError: createGroupBookingError,
    data: createGroupBookingData,
    error: createGroupBookingErrorData,
  } = useMutationRequest(CREATE_GROUP_BOOKING, true);

  const showConfirmationScreen = !createGroupBookingLoading && createGroupBookingSuccess;
  let analyticsData = { validation: '', gbf: {} };

  useEffect(() => {
    if (createGroupBookingError) {
      const defaultErrorMsg = t('groupBooking.confirmation.error');
      let errorMsg = '';
      if (createGroupBookingErrorData && 'response' in createGroupBookingErrorData) {
        errorMsg =
          (createGroupBookingErrorData?.response as { errors?: { message: string }[] })?.errors?.[0]
            ?.message || defaultErrorMsg;
      }
      analytics.update({
        gbf: {
          ...window?.analyticsData?.gbf,
          validation: errorMsg ? errorMsg : defaultErrorMsg,
        },
      });
      window.__satelliteLoaded && window._satellite.track('groupFormError');
    }
  }, [createGroupBookingError]);

  useEffect(() => {
    if (hotelsRequestSuccess && hotelsData) {
      const formattedHotelList = hotelsData.hotels.map(
        ({ title, brand, code }: StaticHotelType) => ({
          value: title,
          brand,
          component: getLogoByBrand(brand as SearchBrandType, code),
        })
      );
      setHotels(formattedHotelList);
    }
  }, [hotelsRequestSuccess, hotelsData]);

  const accordionItemsLastIndex = 2;

  // open next toggle, except for last accordion item's submit
  const openNextAccordian = () => {
    if (accordionIndex < accordionItemsLastIndex) {
      const nextAccordionIndex = accordionIndex + 1;
      handleAccordionToggle(nextAccordionIndex);
    }
  };

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setFormValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setFormValues]
  );

  const groupBookingsForm = (
    <Flex flexDirection="column" alignItems="left" data-testid="GroupBookingsPage-Form">
      <Heading as="h1" data-testid="GroupBookingsPage-page-title" {...headingStyles}>
        {t('groupBooking.page.title')}
      </Heading>
      <Box {...descriptionStyles}>{renderSanitizedHtml(t('groupBooking.page.description'))}</Box>
      <FormWithAccordian
        {...GroupBookingFormConfig({
          getFormState,
          defaultValues: formValues,
          openNextAccordian,
          onSubmit,
          baseTestId: 'GroupBookingsPage',
          t,
          currentLang,
          handleAccordionToggle,
          accordionIndex,
          hotels,
          islessThanMinCount,
          createGroupBookingLoading,
          createGroupBookingError,
        })}
      />
    </Flex>
  );

  const groupBookingsConfirmation = (
    <GroupBookingsConfirmation
      contactName={contactName}
      contactEmail={contactEmail}
      caseNumber={createGroupBookingData?.createGroupBooking?.ticketNumber ?? ''}
    />
  );

  return (
    <Box data-testid="GroupBookingsPage-Wrapper">
      <Container {...containerStyles}>
        <Box mt={{ base: 'md', xs: 'lg', sm: '3xl', lg: 'xl' }}>
          <Seo page={PageName.HOME} />

          {showConfirmationScreen
            ? groupBookingsConfirmation
            : !hotelsRequestError && groupBookingsForm}

          {hotelsRequestError && !hotelsData && (
            <Notification
              prefixDataTestId="GroupBookingsPage-hotels-list-error"
              status="error"
              variant="error"
              svg={<Alert />}
              isInnerHTML
              description={t('groupBooking.bookingDetails.bookingDetails.hotelSelection.listError')}
              wrapperStyles={{ mt: 'var(--chakra-space-lg)' }}
            />
          )}
        </Box>
      </Container>
      {!showConfirmationScreen && (
        <BackToPage
          goBack={() => (window.location.href = `/${currentCountry}/${currentLang}/home.html`)}
          linkText={t('groupBooking.cancelAndReturnHome.button.label')}
        />
      )}
    </Box>
  );

  function onSubmit(data: any) {
    const incompleteBookingAnalytics = () => {
      return analytics.update({
        ...analyticsData?.gbf,
        validation: 'Incomplete Fields Group Form Booking',
      });
    };
    if (!data) {
      incompleteBookingAnalytics();
      return;
    }

    if (data?.RoomTotalCount < 10) {
      // prevent form submission
      setIslessThanMinCount(true);
      incompleteBookingAnalytics();
      return;
    }

    const selectedHotel = getSelectedHotel(data.hotels);
    const hotelCode = selectedHotel?.code;
    const arrivalDate = data.datepicker?.[0]
      ? formatDate(data.datepicker[0].toISOString(), 'yyyy-MM-dd')
      : '';

    const departureDate = data.datepicker?.[1]
      ? formatDate(data.datepicker[1].toISOString(), 'yyyy-MM-dd')
      : data.datepicker?.[0]
        ? formatDate(add(data.datepicker[0], { days: 1 }).toISOString(), 'yyyy-MM-dd')
        : '';

    const createGroupBookingCriteria: CreateGroupBookingCriteria = {
      title: data.title,
      firstName: data.firstName,
      lastName: data.lastName,
      emailAddress: data.emailAddress,
      phoneNumber: data.phoneNumber,
      bookerType: data.BookerType,
      purposeOfStay: data.purposeOfStay,
      isSchoolOrYouth: data.isSchoolOrYouth,
      reasonForVisit: data.reasonForVisit,
      isPackageTypeBf:
        selectedHotel?.brand === 'PID' ? data.packageTypeCheckbox : data.packageType === BREAKFAST,
      isPackageTypeMealDeal: data.packageType === MEALDEAL,
      hotelName: selectedHotel?.title,
      hotelBrand: selectedHotel?.brand,
      arrivalDate,
      departureDate,
      singleOccupancy: data.singleOccupancy,
      doubleOccupancy: data.doubleOccupancy,
      twinRooms: data.twinRooms,
      isTravellingWithChild: data.isTravellingWithChild,
      isAccessibleRoom: data.isAccessibleRoom,
      familyOf21A1C: data.familyOf21A1C,
      familyOf32A1C: data.familyOf32A1C,
      familyOf31A2C: data.familyOf31A2C,
      familyOf42A2C: data.familyOf42A2C,
      accessibleSingle: data.accessibleSingle,
      accessibleDouble: data.accessibleDouble,
      accessibleTwin: data.accessibleTwin,
      additionalInformation: data.comments,
      language: currentLang,
    };

    const { companyName, reasonForVisitOther } = data;
    if (companyName) {
      createGroupBookingCriteria['companyName'] = companyName;
    }
    if (reasonForVisitOther) {
      createGroupBookingCriteria['reasonForVisitOther'] = reasonForVisitOther;
    }

    createGroupBookingMutation.mutate({ hotelCode, createGroupBookingCriteria });
    setIslessThanMinCount(false);
    setContactName(data.firstName);
    setContactEmail(data.emailAddress);

    analyticsData = {
      gbf: {
        booker: data.BookerType,
        typeOfStay: data.purposeOfStay,
        schoolGroup: data.isSchoolOrYouth,
        reasonForVisit: data.reasonForVisit,
        hotelSelected: selectedHotel?.code || '',
        checkInDate: arrivalDate ? formatDate(arrivalDate, 'dd/MM/yyyy') : '',
        checkOutDate: departureDate ? formatDate(departureDate, 'dd/MM/yyyy') : '',
        noOfNights:
          getNightsNumber(
            formatDate(arrivalDate, 'yyyy-MM-dd'),
            formatDate(departureDate, 'yyyy-MM-dd')
          ) ?? 0,
        packageType: data.packageType,
        childrenStaying: data.isTravellingWithChild,
        accessibleRoomRequired: data.isAccessibleRoom,
        totalRooms: data.RoomTotalCount,
        commentsSubmitted: data.comments ? true : false,
        singleOccupancy: data.singleOccupancy,
        doubleOccupancy: data.doubleOccupancy,
        twinOccupancy: data.twinRooms,
        familyOf21A1C: data.familyOf21A1C,
        familyOf32A1C: data.familyOf32A1C,
        familyOf31A2C: data.familyOf31A2C,
        familyOf42A2C: data.familyOf42A2C,
        accessibleSingle: data.accessibleSingle,
        accessibleDouble: data.accessibleDouble,
        accessibleTwin: data.accessibleTwin,
        validation: '', // submit error analytics
      },
      validation: '', // incomplete Fields Group Form Booking analytics
    };

    analytics.update({ gbf: analyticsData.gbf });
  }

  function handleAccordionToggle(accordionIndex: number) {
    setAccordionIndex(accordionIndex);
    const element = document.getElementById(`accordion-button-heading-${accordionIndex}`);
    if (element) {
      setTimeout(() => {
        element.scrollIntoView({
          behavior: 'smooth',
        });
      }, 50);
    }
  }

  function getSelectedHotel(hotelName: string): StaticHotelType | undefined {
    return hotelsData?.hotels?.find((hotel: StaticHotelType) => hotel.title === hotelName.trim());
  }
}

const containerStyles = {
  maxWidth: 'var(--chakra-sizes-lg)',
  marginBottom: '2xl',
};

const headingStyles = {
  fontWeight: 'semibold',
  fontSize: '3xl',
  lineHeight: '4',
  color: 'darkGrey1',
};

const descriptionStyles = {
  color: 'darkGrey1',
  mt: '2xl',
  mb: '2xl',
  sx: {
    b: {
      fontWeight: 'semibold',
    },
  },
};
