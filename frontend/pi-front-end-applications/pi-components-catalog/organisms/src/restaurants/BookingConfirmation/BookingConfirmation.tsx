import { Flex, Box, Heading, Text, BoxProps, Divider } from '@chakra-ui/react';
import {
  GET_ENQUIRY_INFO_BY_ID,
  GET_EVENT_INFO_BY_ID,
  GET_LOCATIONS_DETAILS,
  GET_PAGE_CONTENT,
  AnalyticsData,
} from '@whitbread-eos/api';
import { LoadingSpinner, Notification, Alert } from '@whitbread-eos/atoms';
import { useQueryRequestRestaurants } from '@whitbread-eos/utils';
import { formatDateToString, getCommonParams } from '@whitbread-eos/utils/restaurants';
import NextLink from 'next/link';
import { useEffect } from 'react';

import { Event, Enquiry } from './types';

declare global {
  interface Window {
    analyticsData: AnalyticsData;
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

interface Props {
  eventId: string;
  enquiryId: string;
  restaurantBrandNameForAemApi: string;
  location: string;
  subLocation: string;
}

const BookingConfirmation = (props: Props) => {
  const { eventId, enquiryId, location, subLocation, restaurantBrandNameForAemApi } = props;
  const queryKey = eventId ? 'GetEventById' : 'GetEnquiryById';
  const queryFn = eventId ? GET_EVENT_INFO_BY_ID : GET_ENQUIRY_INFO_BY_ID;
  const queryVariables = eventId ? { id: eventId } : { id: enquiryId };

  const {
    data: pageContentData,
    isLoading: pageContentIsLoading,
  }: { data: any; isLoading: boolean } = useQueryRequestRestaurants(
    ['getPageContent'],
    GET_PAGE_CONTENT
  );

  const formContentData = pageContentData?.label.reduce((prev: any, current: any) => {
    prev[current.key] = current.value;
    return prev;
  }, {});

  const {
    data,
    isError,
    error,
    isLoading,
  }: { data: any; isError: boolean; isLoading: boolean; error: any } = useQueryRequestRestaurants(
    [queryKey],
    queryFn,
    queryVariables
  );

  const commonParams = getCommonParams(restaurantBrandNameForAemApi, location, subLocation);

  const { data: pageData, isLoading: pageDataIsLoading }: { data: any; isLoading: boolean } =
    useQueryRequestRestaurants(
      ['getLocationDetails', ...Object.values(commonParams)],
      GET_LOCATIONS_DETAILS,
      commonParams
    );
  const { title, contactInfo, googleMapURL } = pageData?.locations[0] || {};
  const confirmationValues = eventId ? (data as Event)?.eventById : (data as Enquiry)?.enquiryById;

  const {
    adults,
    bookingReference,
    date,
    emailAddress,
    firstname,
    lastname,
    telephoneNumber,
    time,
    children,
  } = confirmationValues || {};

  // useEffect(() => {
  //   analytics.update({
  //     bookingReference,
  //   });
  // }, [bookingReference]);

  useEffect(() => {
    window.__satelliteLoaded && window._satellite.track('restaurantBookingConfirmed');
  }, []);

  if (isLoading || pageContentIsLoading || pageDataIsLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner />
      </Flex>
    );
  }

  return (
    <Box width="100%" mt="48px">
      <Flex
        direction="column"
        justify={{ base: 'flex-start', sm: 'center' }}
        align={{ base: 'flex-start', sm: 'center' }}
        px={{ base: '0px', sm: '17px', md: '140px' }}
        mb="48px"
      >
        {isError && (
          <Flex>
            <Notification
              description={
                error?.response?.errors[0].message &&
                (enquiryId
                  ? formContentData['reservationform.enquiry.details.failure']
                  : formContentData['reservationform.booking.details.failure'])
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
        <Box
          fontSize="36px"
          display="flex"
          flexDirection="column"
          textAlign="center"
          width="100%"
          color="white"
          mb={'30px'}
          fontWeight={700}
        >
          {enquiryId
            ? formContentData['reservationform.enquiry.confirmation.msg']
            : formContentData['reservationform.booking.confirmation.msg']}
        </Box>

        <Flex
          direction="column"
          align="center"
          fontSize="16px"
          bg="white"
          width={{ base: '100%', sm: '534px', md: '642px' }}
        >
          <Flex px="16px" width={{ base: '100%', md: '480px' }} my="48px" flexDirection="column">
            <Flex mx={[5]} direction="column" align="center">
              <Heading
                as="h1"
                fontSize="xl"
                fontWeight="bold"
                lineHeight="3"
                textStyle="heading2"
                textTransform="capitalize"
              >
                {title}
              </Heading>
              <Flex my="xlg">
                <NextLink
                  href={googleMapURL}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ marginRight: '10px', textDecoration: 'underline' }}
                >
                  {formContentData['reservationform.booking.direction']}
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
            <Text
              mt="md"
              fontWeight={700}
              textTransform="capitalize"
              className="sessioncamhidetext"
            >
              {firstname} {lastname}
            </Text>
            <Text className="sessioncamhidetext">{telephoneNumber}</Text>
            <Box mt="md">
              <Text>
                {enquiryId
                  ? formContentData['reservationform.booking.enquiry.reference']
                  : formContentData['reservationform.booking.reference.label']}
              </Text>
              <Text>{bookingReference}</Text>
            </Box>
            <Box mt="md">
              <Text>
                {!!adults && `${adults} adult${adults !== 1 ? 's' : ''}`}, {children} children
              </Text>
              <Text>
                {date && `${formatDateToString(date)} - `} {time}
              </Text>
            </Box>
            <Box mt="md">
              <Text>
                {enquiryId
                  ? formContentData['reservationform.enquiry.greeting2.msg']
                  : formContentData['reservationform.booking.greeting.text.label']}
              </Text>
              <Text className="sessioncamhidetext">
                {enquiryId
                  ? formContentData['reservationform.enquiry.info.msg']
                  : formContentData['reservationform.booking.mailsender.text.label']}{' '}
                <a
                  className="sessioncamhidetext"
                  style={{ textDecoration: 'underline' }}
                  href={`mailto:${emailAddress}`}
                >
                  {emailAddress}
                </a>
              </Text>
            </Box>

            {/* 
            Hiding back to restaurants link
            <NextLink
              href={`/en-gb/locations${location ? `/${location}` : ''}${
                subLocation ? `/${subLocation}` : ''
              }`}
              className="back-to-home"
            >
              {formContentData['reservationform.booking.returntohome']}
            </NextLink> */}
          </Flex>
        </Flex>
      </Flex>
    </Box>
  );
};

export default BookingConfirmation;

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
