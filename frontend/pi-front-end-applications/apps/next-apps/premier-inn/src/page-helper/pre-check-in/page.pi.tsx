import type { StyleProps, BoxProps, TextProps } from '@chakra-ui/react';
import { Text, Container, Box, Flex } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  PageName,
  FindBookingCriteria,
  FIND_BOOKING,
  Query,
  CREATE_PRECHECKIN_RESERVATION_GUEST,
  BookingConfirmation,
  ATTACH_FILE_TO_RESERVATION,
  PRE_CHECK_IN_STATUS,
  FT_MOBILE_PREREGISTERED_REPURPOSE,
} from '@whitbread-eos/api';
import {
  FormProps,
  DetailsPanel,
  Notification,
  Success,
  Info,
  Error,
  Alert,
} from '@whitbread-eos/atoms';
import {
  SEO,
  PreCheckInReviewModal,
  PageLoader,
  RoomCard,
  PreCheckInBackButton,
  PreCheckInSuccessModal,
} from '@whitbread-eos/molecules';
import {
  cleanupFindBookingToken,
  useCustomLocale,
  graphQLRequest,
  useMutationRequest,
  getFindBookingToken,
  renderSanitizedHtml,
  GLOBALS,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { SetStateAction, useCallback, useEffect, useState, useRef } from 'react';

import {
  regFormInit,
  dependents,
  DATE_FORMAT,
  SpinnersStatusType,
  GenericObject,
  BookingDataType,
  SUBMIT_TYPE,
  handleBooking,
  pageStatus,
  handleSuccessfulFindBooking,
  goToHomePage,
  scrollToElement,
  errorStatusObject,
  handleSave,
} from './common';
import {
  preCheckinFormConfig,
  preCheckinBookingDetailsFormConfig,
} from './piFormConfig/preCheckinFormConfig';

const Form = dynamic(
  async () => {
    const { Form } = await import('@whitbread-eos/atoms');
    return { default: Form };
  },
  {
    ssr: false,
  }
);

export default function PreCheckInPage() {
  const [, setIsLocationRequired] = useState(false);
  const { t } = useTranslation();
  const { query, push, pathname } = useRouter();
  const { language: currentLang, country } = useCustomLocale();
  const queryClient = new QueryClient();
  const rooms = useRef<any>([]);
  const [bookingConfirmation, setBookingConfirmation] = useState<BookingDataType>();
  const [pageLoaderStatus, setPageLoaderStatus] = useState(false);
  const [roomBookingError, setRoomBookingError] = useState(false);
  const [componentLoaderStatus, setComponentLoaderStatus] = useState(false);
  const [displayPersonalDetailsSection, setDisplayPersonalDetailsSection] = useState(false);
  const [isReviewModalOpen, setIsReviewModalOpen] = useState(false);
  const [isPreCheckInSuccessModalOpen, setIsPreCheckInSuccessModalOpen] = useState(false);
  const [formDetails, setFormDetails] = useState<FormProps['defaultValues']>({
    ...regFormInit,
    country,
  });
  const [displayMultiRoomSection, setDisplayMultiRoomSection] = useState(false);
  const [svGstSucessAlert, setSvGstSucessAlert] = useState(false);
  const [svGstFailureAlert, setSvGstFailureAlert] = useState(false);
  const [submitType, setSubmitType] = useState<SUBMIT_TYPE>(SUBMIT_TYPE.SAVE);
  const [reservationStatusError, setReservationStatusError] = useState<boolean>(false);
  const [hotelAddress, setHotelAddress] = useState<string>('');

  const { [FT_MOBILE_PREREGISTERED_REPURPOSE]: isMobilePreRegisteredRepurposeEnabled } =
    useFeatureToggle();

  const {
    mutation: svGstMutation,
    isLoading: svGstIsLoading,
    isError: svGstIsError,
    isSuccess: svGstIsSuccess,
  } = useMutationRequest(CREATE_PRECHECKIN_RESERVATION_GUEST);

  const { mutation: attachFileMutation } = useMutationRequest(ATTACH_FILE_TO_RESERVATION);
  const { mutation: preCheckInStatusMutation } = useMutationRequest(PRE_CHECK_IN_STATUS);
  useEffect(() => {
    if (svGstIsLoading) setIsReviewModalOpen(false);
  }, [svGstIsLoading]);

  useEffect(() => {
    if (svGstIsSuccess && submitType === SUBMIT_TYPE.SAVE) {
      setSvGstSucessAlert(true);
      scrollToElement('reg-card-save-success-notification');
    }
  }, [svGstIsSuccess]);

  useEffect(() => {
    //close the success message automatically
    if (svGstSucessAlert) setTimeout(() => setSvGstSucessAlert(false), 5000);
  }, [svGstSucessAlert]);

  useEffect(() => {
    if (svGstIsError) {
      setSvGstFailureAlert(true);
      scrollToElement('reg-card-save-error-notification');
    }
  }, [svGstIsError]);

  const handleSpinnersStatus = ({
    pageSpinnerStatus = false,
    roomBookingErrorStatus = false,
    reservationErrorStatus = false,
  }: SpinnersStatusType): void => {
    setPageLoaderStatus(pageSpinnerStatus);
    setComponentLoaderStatus(!componentLoaderStatus);
    setRoomBookingError(roomBookingErrorStatus);
    setReservationStatusError(reservationErrorStatus);
  };

  const onSubmitBookingDetails = (data: typeof formDetails) => {
    handleSpinnersStatus(errorStatusObject);
    cleanupFindBookingToken();

    const findBookingCriteria: FindBookingCriteria = {
      country,
      language: currentLang,
      arrivalDate: format(new Date(data.arrivalDate as string), 'yyyy-MM-dd'),
      lastName: data.surname as string,
      resNo: String(data.bookingNumber).toUpperCase(),
    };
    queryClient
      .fetchQuery({
        queryKey: [
          'FindBooking',
          findBookingCriteria.country,
          findBookingCriteria.language,
          findBookingCriteria.arrivalDate,
          findBookingCriteria.lastName,
          findBookingCriteria.resNo,
        ],
        queryFn: () => graphQLRequest(FIND_BOOKING, { findBookingCriteria }),
      })
      .then((response: Query) => {
        handleSuccessfulFindBooking(
          response,
          currentLang,
          country,
          setIsPreCheckInSuccessModalOpen,
          handleSpinnersStatus,
          errorStatusObject,
          setReservationStatusError,
          pathname,
          push,
          setHotelAddress,
          isMobilePreRegisteredRepurposeEnabled
        );
      })
      .catch(() => {
        handleSpinnersStatus({
          ...errorStatusObject,
          roomBookingErrorStatus: true,
        });
      });
  };

  const onSubmit = async (data: typeof formDetails, event: any) => {
    event.preventDefault();
    const formData = data?.country === '' ? { ...data, country: country.toUpperCase() } : data;
    setFormDetails(formData);

    if (submitType === SUBMIT_TYPE.SUBMIT) {
      if ((formData as any)?.nationality?.value === GLOBALS.localeUpper.DE) {
        await handleSave({
          data: formData,
          type: 'submit',
          handleRedirect,
          bookingConfirmation,
          currentLang,
          country,
          svGstMutation,
          attachFileMutation,
          preCheckInStatusMutation,
          setSvGstSucessAlert,
          setIsPreCheckInSuccessModalOpen,
          setSvGstFailureAlert,
          regCardUrl: '',
          setBookingConfirmation,
        });
      } else {
        setIsReviewModalOpen(true);
        const { bookingReference } = getFindBookingToken();
        if (!bookingReference) {
          handleRedirect();
          return;
        }
      }
    } else
      handleSave({
        data,
        type: 'save',
        handleRedirect,
        bookingConfirmation,
        currentLang,
        country,
        svGstMutation,
        attachFileMutation,
        preCheckInStatusMutation,
        setSvGstSucessAlert,
        setIsPreCheckInSuccessModalOpen,
        setSvGstFailureAlert,
        setBookingConfirmation,
      });
  };

  const handleRedirect = () =>
    push(`/${country}/${currentLang}${pathname}/?status=${pageStatus.EXPIRED}`);

  useEffect(() => {
    handleBooking({
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
    });
  }, [query?.bookingReference, query?.reservationId]);

  useEffect(() => {
    setDisplayPersonalDetailsSection(true);
    const guestRoomDetails = rooms?.current?.find(
      (room: { reservationId: string; preCheckInStatus: boolean; deRegCardCompleted: boolean }) => {
        if (
          (isMobilePreRegisteredRepurposeEnabled
            ? room?.deRegCardCompleted === false
            : room?.preCheckInStatus === false) &&
          room?.reservationId === query?.reservationId
        ) {
          return room;
        }
      }
    );
    setFormDetails(guestRoomDetails);
  }, [query.reservationId, rooms?.current, setDisplayPersonalDetailsSection, setFormDetails]);

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setFormDetails(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setFormDetails]
  );

  const baseDataTestId = 'PreCheckInPage';

  const bookingSearchFormSection = !query?.bookingReference && (
    <>
      <Form
        {...preCheckinBookingDetailsFormConfig({
          getFormState,
          defaultValues: formDetails,
          onSubmit: onSubmitBookingDetails,
          baseDataTestId,
          currentLang,
          t,
          bookingReferenceError: roomBookingError,
        })}
      />
      <PreCheckInSuccessModal
        isOpen={isPreCheckInSuccessModalOpen}
        onClose={() => setIsPreCheckInSuccessModalOpen(false)}
        messageTag={'precheckin.room.completemsg'}
      />
    </>
  );

  const { bookingReference, hotelName, arrivalDate, departureDate } =
    rooms.current.length > 1 ? rooms.current[0] : formDetails || {};
  const BookingDetailsHeaderSectionData = {
    title: `${t('precheckin.yourbooking')}`,
    rows: [
      {
        key: `${t('precheckin.yourbooking.details.bookingreference')}`,
        value: `${bookingReference}`,
      },
      {
        key: `${t('precheckin.yourbooking.details.hotelname')}`,
        value: `${hotelName}`,
      },
      {
        key: `${t('precheckin.yourbooking.details.arrivaldate')}`,
        value: arrivalDate ? format(new Date(arrivalDate as string), DATE_FORMAT) : '',
      },
      {
        key: `${t('precheckin.yourbooking.details.departuredate')}`,
        value: departureDate ? format(new Date(departureDate as string), DATE_FORMAT) : '',
      },
    ],
  };

  const bookingSummarySection = query?.bookingReference && bookingConfirmation?.basketReference && (
    <>
      <Text {...subHeadingStyles}>{t('precheckin.subheading')}</Text>
      {hotelName && (
        <DetailsPanel data={BookingDetailsHeaderSectionData} stylesObject={stylesObject} />
      )}
      {svGstSucessAlert && (
        <Notification
          status="success"
          variant="success"
          svg={<Success />}
          data-testid={`${baseDataTestId}-reg-card-save-success-notification`}
          id="reg-card-save-success-notification"
          showCloseButton
          onClick={() => setSvGstSucessAlert(false)}
          description={t('precheckin.savedetails.success')}
        />
      )}
      {svGstFailureAlert && (
        <Notification
          variant="error"
          status="error"
          svg={<Error />}
          data-testid={`${baseDataTestId}-reg-card-save-error-notification`}
          showCloseButton
          id="reg-card-save-error-notification"
          onClick={() => setSvGstFailureAlert(false)}
          description={t('precheckin.savedetails.error')}
        />
      )}
    </>
  );

  const handleScaSuccess = async (regCardUrl: string) => {
    await handleSave({
      data: formDetails,
      type: 'submit',
      handleRedirect,
      bookingConfirmation,
      currentLang,
      country,
      svGstMutation,
      attachFileMutation,
      preCheckInStatusMutation,
      setSvGstSucessAlert,
      setIsPreCheckInSuccessModalOpen,
      setSvGstFailureAlert,
      regCardUrl,
      setBookingConfirmation,
    });
  };

  const personalDetailsFormSection = query?.bookingReference &&
    displayPersonalDetailsSection &&
    formDetails?.firstName &&
    (!rooms.current.length || query?.reservationId) && (
      <>
        <Form
          {...preCheckinFormConfig({
            getFormState,
            defaultValues: formDetails,
            onSubmit,
            baseDataTestId,
            currentLang,
            t,
            dependents,
            setSubmitType,
            submitType,
            setIsLocationRequired,
            isMobilePreRegisteredRepurposeEnabled,
          })}
        />
        <PreCheckInReviewModal
          isOpen={isReviewModalOpen}
          onClose={() => setIsReviewModalOpen(false)}
          data={formDetails}
          bookingConfirmation={bookingConfirmation as unknown as BookingConfirmation}
          handleScaSuccess={handleScaSuccess}
          hotelAddress={hotelAddress}
        />
        <PreCheckInSuccessModal
          isOpen={isPreCheckInSuccessModalOpen}
          onClose={() =>
            goToHomePage(`/${country}/${currentLang}/home.html`, setIsPreCheckInSuccessModalOpen)
          }
          messageTag={'precheckin.complete.msg'}
          overlayStyles={{ backgroundColor: 'var(--chakra-colors-darkGrey2)' }}
        />
      </>
    );

  const multiRoomsSection = displayMultiRoomSection &&
    query?.bookingReference &&
    !query?.reservationId &&
    !query?.status && (
      <Box mb="lg">
        {rooms.current.length > 1 &&
          rooms.current.map((room: GenericObject, roomIndex: number) => (
            <RoomCard
              room={room}
              key={room.reservationId as string}
              roomIndex={roomIndex}
              testid={baseDataTestId}
              setURLParamToReservationId={() =>
                push(
                  `/${country}/${currentLang}${pathname}/?bookingReference=${bookingReference}&reservationId=${room.reservationId}`
                )
              }
              preCheckInStatus={
                isMobilePreRegisteredRepurposeEnabled
                  ? (room.deRegCardCompleted as boolean)
                  : (room.preCheckInStatus as boolean)
              }
            />
          ))}
      </Box>
    );

  const speedUpCheckInMessageSection = !query?.bookingReference && (
    <Notification
      status="info"
      variant="infoGrey"
      svg={<Info color="var(--chakra-colors-darkGrey2)" style={{ marginTop: '8px' }} />}
      prefixDataTestId={`${baseDataTestId}-predescription`}
      description={
        <Box sx={sxStyles}>
          {renderSanitizedHtml(t('precheckin.bookingdetails.predescription'))}
        </Box>
      }
      wrapperStyles={{ border: 0, p: 0, m: 0, mt: 'lg' }}
    />
  );

  const cookieExpiredMessage = query?.status === pageStatus.EXPIRED && !query?.bookingReference && (
    <Notification
      variant="alert"
      status="info"
      svg={<Alert />}
      data-testid={`${baseDataTestId}-reg-card-cookie-expiry-notification`}
      id="reg-card-cookie-expiry-notification"
      onClick={() => setSvGstFailureAlert(false)}
      description={t('precheckin.session.expiredmsg')}
    />
  );

  const reservationStatusErrorSection = reservationStatusError && (
    <Notification
      svg={<Error />}
      status="error"
      description={t('precheckin.cancelledbooking.errormsg')}
      variant="error"
      prefixDataTestId={`${baseDataTestId}-reservationStatusError`}
    />
  );

  return (
    <>
      {!!pageLoaderStatus && <PageLoader text={t('searchresults.list.hotel.loading')} />}
      {!!currentLang && !pageLoaderStatus && (
        <Container {...containerStyle}>
          <SEO page={PageName.REGISTER} />
          <Text {...headingStyles}>{t('precheckin.title')}</Text>
          {!query?.bookingReference && (
            <Flex direction="column" gap="xl" {...subBookingDetailsContainerStyles}>
              {speedUpCheckInMessageSection}
              {cookieExpiredMessage}
              {reservationStatusErrorSection}
              {bookingSearchFormSection}
            </Flex>
          )}
          {query?.bookingReference && (
            <Flex direction="column" gap="xl" {...subPersonalDetailsContainerStyles}>
              {bookingSummarySection}
              {personalDetailsFormSection}
              {multiRoomsSection}
              {!!query?.bookingReference && bookingConfirmation?.basketReference && (
                <Box mt="-xl">
                  <PreCheckInBackButton />
                </Box>
              )}
            </Flex>
          )}
        </Container>
      )}
    </>
  );
}

const subPersonalDetailsContainerStyles = {
  pos: 'relative',
  w: { base: '100%', md: '60.96rem' }, //68.02
  mt: '0',
  boxSizing: 'border-box',
} as BoxProps;

const subBookingDetailsContainerStyles = {
  pos: 'relative',
  w: { base: '100%', md: '70.88rem' },
  boxSizing: 'border-box',
} as BoxProps;

const sxStyles = {
  pt: 0,
  mt: 0,
  fontWeight: 'normal',
  lineHeight: 3,
  color: 'var(--chakra-colors-darkGrey2)',
  p: { pt: 'sm' },
  a: {
    color: 'var(--chakra-colors-darkGrey3)',
    fontWeight: 'semibold',
    textDecoration: 'underline',
  },
  fontSize: 'md',
} as StyleProps;

const containerStyle = {
  minH: '60em',
  maxW: '100vw',
  paddingInlineStart: {
    mobile: '0px',
  },
  paddingInlineEnd: {
    mobile: '0px',
  },
} as StyleProps;

const headingStyles = {
  fontFamily: 'header',
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '3',
  color: 'baseBlack',
  mt: '4.38rem',
} as StyleProps;

const stylesObject = {
  wrapper: {
    pos: 'relative',
    pl: 'xl',
    w: { base: '100%', md: '40em' },
  } as BoxProps,
  title: {
    fontSize: 'xl',
    fontWeight: 'bold',
    mb: 'md',
    lineHeight: 'var(--chakra-sizes-8)',
  } as StyleProps,
  rowKey: {
    fontSize: 'md',
    fontWeight: 'semibold',
    color: 'darkGrey1',
    lineHeight: 'var(--chakra-sizes-6)',
    width: '14.55rem',
  } as StyleProps,
  rowValue: {
    fontSize: 'md',
    fontWeight: 'normal',
    color: 'darkGrey1',
    lineHeight: 'var(--chakra-sizes-6)',
  } as StyleProps,
};

const subHeadingStyles = {
  fontWeight: 'normal',
  lineHeight: '3',
  color: 'darkGrey2',
} as TextProps;
