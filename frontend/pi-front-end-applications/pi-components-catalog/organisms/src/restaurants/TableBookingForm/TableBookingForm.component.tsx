//Libraries import
import { Box, Flex, BoxProps, Heading, Divider } from '@chakra-ui/react';
//Custom Import
import {
  TABLE_ENQUIRY,
  TABLE_RESERVATION,
  GET_LOCATIONS_DETAILS,
  GET_PAGE_CONTENT,
} from '@whitbread-eos/api';
import { Notification, LoadingSpinner, Alert } from '@whitbread-eos/atoms';
import { Form, FormProps } from '@whitbread-eos/atoms/restaurants';
import {
  useAppData,
  encodeToBase64,
  BOOKING_REFERENCE_ID,
  setCookie,
  useQueryRequestRestaurants,
  useMutationRequestRestaurants,
} from '@whitbread-eos/utils';
import { getCommonParams } from '@whitbread-eos/utils/restaurants';
import { format } from 'date-fns';
import NextLink from 'next/link';
import { useRouter } from 'next/router';
import { useCallback, useState, SetStateAction } from 'react';

import { tabletBookingDetailsFormConfig } from './formConfig';

interface BookingReference {
  id: string;
}

interface BookingConfimation {
  event?: BookingReference;
  enquiry?: BookingReference;
}
interface Props {
  restaurantBrandNameForAemApi: string;
  location: string;
  subLocation: string;
  restaurantBrandNameForMarketing: string;
  restaurantBrandName: string;
}

function TableBookingForm({
  location,
  subLocation,
  restaurantBrandNameForAemApi,
  restaurantBrandName,
}: Readonly<Props>) {
  const appData = useAppData();
  const router = useRouter();
  const [isEnquiry, setIsEnquiry] = useState<boolean>(false);
  const [isMenuOptionAvailable, setIsMenuOptionAvailable] = useState<boolean>(false);

  const {
    data: pageContentData,
    isLoading: pageContentIsLoading,
    isError: isPageContentError,
  }: { data: any; isLoading: boolean; isError: boolean } = useQueryRequestRestaurants(
    'getPageContent',
    GET_PAGE_CONTENT
  );
  //now response  is in form of map so here convert the response into json
  const formContentData = pageContentData?.label?.reduce((prev: any, current: any) => {
    prev[current.key] = current.value;
    return prev;
  }, {});

  const commonParams = getCommonParams(restaurantBrandNameForAemApi, location, subLocation);

  const { data: pageData }: { data: any; isLoading: boolean } = useQueryRequestRestaurants(
    ['getLocationDetails', ...Object.values(commonParams)],
    GET_LOCATIONS_DETAILS,
    commonParams
  );
  const locationData = pageData?.locations;
  const {
    title = '',
    contactInfo = '',
    googleMapURL = '',
  } = locationData?.length ? locationData[0] : {};

  const mutationQueryFn = {
    onSuccess: async (data: BookingConfimation) => {
      const { enquiry, event } = data;
      const bookingRefId = enquiry ? enquiry?.id : event?.id;
      if (bookingRefId) {
        setCookie(BOOKING_REFERENCE_ID, encodeToBase64(bookingRefId + restaurantBrandName), 30);
        window.__satelliteLoaded && window._satellite.track('tableBookingComplete');
        await router.push(
          `/gb/en/restaurants${location ? `/${location}` : ''}${
            subLocation ? `/${subLocation}` : ''
          }/booking-confirmation?brandName=${restaurantBrandName}&${
            event ? 'event' : 'enquiry'
          }=${bookingRefId}`
        );
      }
    },
  };
  const {
    mutation: tbMutation,
    isLoading: tbIsLoading,
    isError: tbIsError,
    error: tbError,
  }: {
    mutation: any;
    isLoading: boolean;
    isError: boolean;
    error: any;
  } = useMutationRequestRestaurants(isEnquiry ? TABLE_ENQUIRY : TABLE_RESERVATION, undefined, {
    ...mutationQueryFn,
  });
  const continueTableBooking = useCallback(
    (data: any) => {
      const highchair = data.highchair
        ? `${formContentData?.['reservationform.special.request.highchair.msg']} is ${data.highchair}`
        : '';
      const wheelchair = data.wheelchair
        ? `${formContentData?.['reservationform.special.request.wheelchair.msg']} is ${data.wheelchair}`
        : '';
      const specialRequest = data.specialRequest
        ? `${formContentData?.['reservationform.special.request.otherspecialrequest.msg']} is ${data.specialRequest}`
        : '';
      let combinedSpecialRequest = [highchair, wheelchair, specialRequest]
        .filter(Boolean)
        .join(', ');

      // If no fields had data, set combinedVariable to null or an empty string
      if (!combinedSpecialRequest) {
        combinedSpecialRequest = '';
      }

      tbMutation.mutate({
        adults: data.adultsByEnquiry ? parseInt(data.adultsByEnquiry, 10) : data.adults,
        children: data.childrenByEnquiry ? parseInt(data.childrenByEnquiry, 10) : data.children,
        date: data.date,
        menuIds: data.menuId ? [data.menuId] : [],
        specialRequest: combinedSpecialRequest,
        emailAddress: data.emailAddress,
        firstname: data.firstname,
        lastname: data.lastname,
        occasionId: appData?.occasionId,
        siteId: appData?.siteId,
        telephoneNumber: data.telephoneNumber,
        time: data.time,
        turnTimeMinutes: 10,
        consentStatement: Boolean(data.consent),
        email: Boolean(data.consent),
        phone: Boolean(data.consent),
        postal: Boolean(data.consent),
        privacyStatement: Boolean(data.privacyStatement),
        profiling: Boolean(data.consent),
        pushNotification: Boolean(data.consent),
        sms: Boolean(data.consent),
        termsAndConditions: Boolean(data.privacyStatement),
      });
    },
    [tbMutation, tbIsLoading]
  );

  const onSubmit = (data: any) => {
    if (data) {
      continueTableBooking(data);
    }
  };

  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    siteId: '',
    occasionId: '',
    firstname: '',
    lastname: '',
    emailAddress: '',
    telephoneNumber: '',
    adults: 0,
    children: 0,
    adultsByEnquiry: '',
    childrenByEnquiry: '',
    highchair: 0,
    wheelchair: false,
    specialRequest: '',
    date: format(new Date(), 'yyyy/MM/dd'),
    menuId: '',
    time: '',
    consent: false,
    privacyStatement: false,
  });

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );
  const baseDataTestId = 'TableBooking';

  if (tbIsLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner
          loadingText={
            isEnquiry
              ? formContentData?.['reservationform.enquiry.loader.msg']
              : formContentData?.['reservationform.booking.loader.msg']
          }
        />
      </Flex>
    );
  }

  if (pageContentIsLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner />
      </Flex>
    );
  }

  if (isPageContentError) {
    return (
      <Flex>
        <Notification
          description="Something went wrong"
          wrapperStyles={{
            display: 'flex',
            justifyContent: 'center',
          }}
          variant="alert"
          status="error"
          maxW="full"
          svg={<Alert />}
        />
      </Flex>
    );
  }

  return (
    <Box>
      <Flex justify={{ base: 'flex-start', sm: 'center' }} direction="column">
        <Heading
          className={`form-heading-title ${restaurantBrandName}-form-heading-title`}
          fontSize="50px"
          w="full"
        >
          {formContentData?.['reservationform.booking.heading']}
        </Heading>
        <Flex
          w="full"
          direction="column"
          align="center"
          fontSize="16px"
          bg="white"
          mb="30px"
          boxShadow={'0 0 29px 5px rgba(0, 0, 0, 0.2)'}
        >
          {tbIsError && (
            <Flex>
              <Notification
                description={
                  tbError?.response?.errors[0].message &&
                  (isEnquiry
                    ? formContentData?.['reservationform.enquiry.failure.error']
                    : formContentData?.['reservationform.booking.failure.error'])
                }
                wrapperStyles={{
                  display: 'flex',
                  justifyContent: 'center',
                }}
                variant="alert"
                status="error"
                maxW="full"
                svg={<Alert />}
              />
            </Flex>
          )}
          <Flex px="1rem" w={{ base: 'full', sm: '500px' }} my="48px" flexDirection="column">
            <Flex direction="column" align="center">
              <Heading
                as="h1"
                fontSize="xl"
                fontWeight="bold"
                lineHeight="3"
                textStyle="heading2"
                color="#511E62"
              >
                {title}
              </Heading>
              <Flex my="md">
                <NextLink
                  href={googleMapURL}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ marginRight: '10px', textDecoration: 'underline' }}
                >
                  {formContentData?.['reservationform.booking.direction']}
                </NextLink>
                <NextLink
                  href={`tel:${contactInfo}`}
                  rel="noopener noreferrer"
                  style={{ textDecoration: 'underline' }}
                >
                  {contactInfo}
                </NextLink>
              </Flex>
            </Flex>
            <Divider mb="15px" />
            <Box>
              <Form
                {...tabletBookingDetailsFormConfig({
                  getFormState,
                  defaultValues,
                  onSubmit,
                  baseDataTestId,
                  formContentData,
                  isMenuOptionAvailable,
                  isAltStyle: true,
                })}
                childrenToggle={formContentData?.['reservationform.children.toggle.button']}
                returnToHome={formContentData?.['reservationform.booking.returntohome']}
                optionalText={formContentData?.['reservationform.optional.label']}
                specialRequestLabel={
                  formContentData?.['reservationform.additional.requirement.label']
                }
                locationName={location}
                subLocationName={subLocation}
                isEnquiry={isEnquiry}
                setIsEnquiry={setIsEnquiry}
                enquiryHeading={formContentData?.['reservationform.enquiry.heading']}
                enquirySubheading={formContentData?.['reservationform.enquiry.subheading']}
                setIsMenuOptionAvailable={setIsMenuOptionAvailable}
              />
            </Box>
          </Flex>
        </Flex>
      </Flex>
    </Box>
  );
}
export default TableBookingForm;

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 1,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;
