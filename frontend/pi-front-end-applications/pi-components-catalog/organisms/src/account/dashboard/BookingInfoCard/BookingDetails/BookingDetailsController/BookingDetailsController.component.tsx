import { Box, Flex, FlexProps, Link, Text, TextProps } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  AMEND_COOKIE_EXPIRY_MINUTES,
  AMEND_COOKIE_NAME,
  AMEND_FIND_BOOKING,
  AmendCookieData,
  Area,
  BC_RESERVATION_STATUS,
  BOOKING_TYPE,
  BUSINESS_BOOKER_USER_ROLES,
  Channel,
  DpaInfo,
  IdvStatus,
  SOURCE_SYSTEM,
  FIND_BOOKING_COOKIE_NAME_KEY,
  IDV_INITIAL_DATA,
  IDV_STATUS_KEY,
  FindBookingCriteria,
  BookingChannelCriteria,
  Query,
  OfferEnum,
  ANCILLARIES_TABS,
} from '@whitbread-eos/api';
import type { ButtonProps } from '@whitbread-eos/atoms';
import { Button } from '@whitbread-eos/atoms';
import {
  cleanupFindBookingToken,
  useLocalStorage,
  formatDataTestId,
  getIDVPassedStatus,
  getLoggedInUserInfo,
  getSecureTwoURL,
  graphQLRequest,
  useCustomLocale,
  renderSanitizedHtml,
  setCookie,
  getCookie,
  useAuthToken,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useCallback, useEffect, useState } from 'react';

import { BookingHistoryCancelBookingModal, CancelBookingModal } from '../../CancelBookingModal';
import type { IDVDataProps } from '../../IDVModal';
import IDVModal from '../../IDVModal';

export const ANALYTICS_TRACKING_AMEND_PAGE = 'lamendBooking';

export interface Props {
  manageBookingData: {
    isCancellable: boolean;
    isAmendable: boolean;
    isRuleCompliant: boolean;
    aemLabelKey: string;
  };
  refetchManageBooking: () => void;
  basketReference: string | null;
  bookingReference: string;
  area?: Area;
  bookingStatus: string;
  bookingType?: string;
  idvData?: IDVDataProps;
  setIdvData?: (value: IDVDataProps) => void;
  defaultDataFromBooking?: any;
  dpaInfo?: DpaInfo;
  setDpaInfo?: (value: DpaInfo) => void;
  inputValues?: any;
  skipBookingRequest?: boolean;
  hotelName?: string;
  bookedFor?: string;
  arrivalDate?: string;
  noNights?: number;
  noOfRooms?: number;
  hotelId?: string;
  bookedBy?: string;
  bookingChannel?: BookingChannelCriteria;
  sourceSystem?: string;
  guestSurname?: string;
  isAmendSuccessful?: boolean;
  rateType: string;
  bookingSurname?: string;
  isAmendPage?: boolean;
  operaConfNumber?: string;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

export default function BookingDetailsController({
  manageBookingData,
  area = 'pi' as Area.PI,
  refetchManageBooking,
  basketReference,
  bookingReference,
  bookingType,
  bookingStatus,
  idvData,
  setIdvData,
  defaultDataFromBooking,
  dpaInfo,
  setDpaInfo,
  inputValues,
  skipBookingRequest,
  hotelName,
  bookedFor,
  arrivalDate,
  noOfRooms,
  noNights,
  hotelId,
  bookedBy,
  bookingChannel,
  sourceSystem,
  guestSurname,
  isAmendSuccessful,
  rateType,
  bookingSurname,
  isAmendPage,
  operaConfNumber,
  isRemovePIIDataFromLocalStorageEnabled = false,
}: Readonly<Props>) {
  const router = useRouter();
  const { isAmendable, isCancellable, isRuleCompliant, aemLabelKey } = manageBookingData;

  const [isCancelableModalVisible, setIsCancelableModalVisible] = useState(false);
  const [isIdvModalVisible, setIsIdvModalVisible] = useState(false);
  const [idvStatus, setIdvStatus] = useLocalStorage<IdvStatus>(IDV_STATUS_KEY, IDV_INITIAL_DATA);
  const { t } = useTranslation(['common']);
  const { language, country } = useCustomLocale();
  const idvPassed =
    (dpaInfo && getIDVPassedStatus(dpaInfo)) ||
    (idvStatus.passed && idvStatus.bookingReference === bookingReference);
  const baseDataTestId = 'BookingDetailsController';

  const { token } = useAuthToken();
  const { accessLevel, sessionId, profile } = getLoggedInUserInfo(token);

  const { reservationId } = router.query;
  useEffect(() => {
    setIsCancelableModalVisible(false);
  }, []);

  const handleCancelBooking = useCallback(() => {
    if (isCancellable) {
      setIsCancelableModalVisible(true);
    }
  }, [isCancellable]);

  const handleIDVModalClose = (dpaPassed: boolean, dpaOverride: boolean) => {
    setIsIdvModalVisible(false);
    if (setDpaInfo) {
      setDpaInfo({ dpaPassed: dpaPassed, dpaOverride: dpaOverride });
    }
    if (setIdvData && idvData) {
      setIdvData({
        ...idvData,
        dpaStatus: { ...idvData?.dpaStatus, dpaPassed: dpaPassed, dpaOverride: dpaOverride },
      });
    }
    if (dpaPassed || dpaOverride) {
      setIdvStatus({ bookingReference, passed: true });
    }
  };

  const handleReuseDetails = () => {
    if (reservationId) {
      if (isRemovePIIDataFromLocalStorageEnabled) {
        try {
          window.localStorage.setItem(
            'reUseReservation',
            JSON.stringify({ id: basketReference?.toString() ?? '' })
          );
        } catch (error) {
          // eslint-disable-next-line no-console
          console.log(error);
        }
      } else if (defaultDataFromBooking) {
        const countryCode = country?.toLocaleUpperCase();
        const fallbackCityName =
          defaultDataFromBooking?.cityName || defaultDataFromBooking?.addressLine4 || '';
        try {
          window.localStorage.setItem(
            'formDetails',
            JSON.stringify({
              ...defaultDataFromBooking,
              cityName: fallbackCityName,
              country: countryCode,
              countryCode,
              basketReferenceId: reservationId,
              leadGuest: undefined,
              updated: true,
            })
          );
        } catch (error) {
          // eslint-disable-next-line no-console
          console.log(error);
        }
      }
      window.location.href = `/${country}/${language}/guest-details?reservationId=${reservationId}`;
    }
  };

  const operaConfNumberExists = !!operaConfNumber;
  const bookingChecks =
    bookingType === BOOKING_TYPE.UPCOMING && rateType !== OfferEnum.EMPLOYEE_RATE_CODE;

  return (
    <>
      {renderControllers()}
      {renderModal()}
    </>
  );

  function renderControllers() {
    const contactUsRedirectUrl =
      area === Area.PI
        ? t('dashboard.bookings.contactUsButton.url')
        : t('dashboard.bookings.contactUsButtonBB.url');

    return (
      <>
        {aemLabelKey && (
          <Text data-testid="BookingDetailsControllerReasonLabel" {...reasonLabelStyle}>
            {renderSanitizedHtml(
              t(aemLabelKey).replace(
                /<a /g,
                `<a style="color: #511E62; text-decoration: underline"`
              )
            )}
          </Text>
        )}
        <Flex
          {...buttonsContainerStyle}
          marginTop={
            bookingStatus !== BC_RESERVATION_STATUS.CANCELLED && isAmendSuccessful
              ? '0'
              : buttonsContainerStyle.mt
          }
          sx={{ '@media print': { display: 'none' } }}
          data-testid="BookingDetailsControllerContainer"
        >
          {area === Area.CCUI && (
            <Flex {...(isAmendPage && { w: '100%', justifyContent: 'flex-end' })}>
              {!isAmendPage && (
                <Box>
                  <Button
                    onClick={() => {
                      setIsIdvModalVisible(true);
                      setIdvStatus(IDV_INITIAL_DATA);
                    }}
                    {...idAndVButtonStyle(language)}
                    data-testid={formatDataTestId(baseDataTestId, 'CtaButton')}
                  >
                    {t('ccui.idv.ctaButton')}
                  </Button>
                </Box>
              )}
              {operaConfNumberExists ? (
                <>
                  {(bookingChecks || isAmendPage) && (
                    <>
                      {isCancellable && (
                        <Box {...buttonContainerStyle}>
                          <Button
                            {...cancelButtonStyle(language, Area.CCUI, isAmendPage)}
                            onClick={handleCancelBooking}
                            isDisabled={!idvPassed || !isCancellable}
                            data-testid={formatDataTestId(baseDataTestId, 'CancelButton')}
                          >
                            {t('dashboard.bookings.cancelButton')}
                          </Button>
                        </Box>
                      )}
                      {isAmendable && (
                        <Box {...buttonContainerStyle}>
                          <Button
                            onClick={() => handleAmendBooking()}
                            isDisabled={!idvPassed || !isAmendable}
                            {...amendButtonStyle(language, Area.CCUI, isAmendPage)}
                            data-testid={formatDataTestId(baseDataTestId, 'AmendButton')}
                          >
                            {t('dashboard.bookings.amendButton')}
                          </Button>
                        </Box>
                      )}
                    </>
                  )}
                </>
              ) : (
                <>
                  {(bookingChecks || isAmendPage) && (
                    <>
                      <Box {...buttonContainerStyle}>
                        <Button
                          {...cancelButtonStyle(language, Area.CCUI, isAmendPage)}
                          onClick={handleCancelBooking}
                          isDisabled={!idvPassed || !isCancellable}
                          data-testid={formatDataTestId(baseDataTestId, 'CancelButton')}
                        >
                          {t('dashboard.bookings.cancelButton')}
                        </Button>
                      </Box>
                      <Box {...buttonContainerStyle}>
                        <Button
                          onClick={() => handleAmendBooking()}
                          isDisabled={!idvPassed || !isAmendable}
                          {...amendButtonStyle(language, Area.CCUI, isAmendPage)}
                          data-testid={formatDataTestId(baseDataTestId, 'AmendButton')}
                        >
                          {t('dashboard.bookings.amendButton')}
                        </Button>
                      </Box>
                    </>
                  )}
                </>
              )}
            </Flex>
          )}

          {[Area.PI, Area.BB].includes(area) &&
            bookingStatus !== BC_RESERVATION_STATUS.CANCELLED &&
            !isAmendSuccessful && (
              <>
                {!isRuleCompliant ? (
                  <Box {...buttonContainerStyle}>
                    <Link href={contactUsRedirectUrl} isExternal>
                      <Button
                        {...amendButtonStyle(language, Area.PI)}
                        data-testid={formatDataTestId(baseDataTestId, 'ContactUsButton')}
                      >
                        {t('dashboard.bookings.contactUsButton')}
                      </Button>
                    </Link>
                  </Box>
                ) : (
                  <Flex
                    direction={{ mobile: 'column', lg: 'row' }}
                    gap={{ mobile: 'md', lg: 'lg' }}
                    flexWrap="wrap"
                    justifyContent="flex-end"
                  >
                    {isCancellable && (
                      <Button
                        {...cancelButtonStyle(language, Area.PI)}
                        onClick={handleCancelBooking}
                        isDisabled={!isCancellable}
                        data-testid={formatDataTestId(baseDataTestId, 'CancelButton')}
                      >
                        {t('dashboard.bookings.cancelButton')}
                      </Button>
                    )}
                    {isAmendable && accessLevel !== BUSINESS_BOOKER_USER_ROLES.STAYER && (
                      <Button
                        onClick={() => handleAmendBooking()}
                        {...amendButtonStyle(language, Area.PI)}
                        data-testid={formatDataTestId(baseDataTestId, 'AmendButton')}
                      >
                        {t('dashboard.bookings.amendButton')}
                      </Button>
                    )}
                  </Flex>
                )}
              </>
            )}
        </Flex>
      </>
    );
  }

  function renderModal() {
    return (
      <>
        {idvData && setIdvData && (
          <IDVModal
            isVisible={isIdvModalVisible}
            onClose={handleIDVModalClose}
            onReuseDetails={reservationId ? handleReuseDetails : undefined}
            data={idvData}
            setData={setIdvData}
            t={t}
            inputValues={inputValues}
          />
        )}
        {skipBookingRequest ? (
          bookingChannel && (
            <BookingHistoryCancelBookingModal
              area={area}
              refetchManageBooking={refetchManageBooking}
              isModalVisible={isCancelableModalVisible}
              onModalClose={() => setIsCancelableModalVisible(false)}
              basketReference={basketReference}
              bookingReference={bookingReference}
              hotelName={hotelName as string}
              bookedFor={bookedFor as string}
              arrivalDate={arrivalDate as string}
              noOfRooms={noOfRooms as number}
              noNights={noNights as number}
              hotelId={hotelId as string}
              bookedBy={bookedBy as string}
              bookingChannel={bookingChannel}
            />
          )
        ) : (
          <CancelBookingModal
            area={area}
            refetchManageBooking={refetchManageBooking}
            isModalVisible={isCancelableModalVisible}
            onModalClose={() => setIsCancelableModalVisible(false)}
            basketReference={basketReference ?? ''}
            bookingReference={bookingReference}
          />
        )}
      </>
    );
  }

  function setOperaBookingCookie(findBookingResponse: Query) {
    if (!findBookingResponse?.findBooking) {
      return;
    }
    const { ref, basketReference, cookieName, token, minutesTillExpiry } =
      findBookingResponse.findBooking;
    const cookieValue = { token, basketReference, bookingReference: ref };
    if (typeof window !== 'undefined') {
      window.localStorage.setItem(FIND_BOOKING_COOKIE_NAME_KEY, cookieName ?? '');
    }
    setCookie(
      cookieName ?? '',
      window.btoa(JSON.stringify(cookieValue)),
      Number(minutesTillExpiry)
    );
  }

  function getCookieData() {
    return {
      reservationId: bookingReference,
      arrivalDate: arrivalDate ?? '',
      surname: guestSurname ?? '',
      bookingChannel: area === Area.BB ? 'CBT' : 'WEB',
      ...(area === Area.BB
        ? {
            employeeId: profile.employeeId,
            sessionId: sessionId,
            companyId: profile.companyId,
            userlevel: accessLevel,
          }
        : {}),
    };
  }

  function handleAmendBooking() {
    const isSecureAmend = sourceSystem === SOURCE_SYSTEM.BART;
    window.__satelliteLoaded && window._satellite.track(ANALYTICS_TRACKING_AMEND_PAGE);
    const surName = bookingSurname ?? defaultDataFromBooking?.leadGuest[0].lastName;

    if (isSecureAmend) {
      const cookieData: AmendCookieData = getCookieData();

      setCookie(
        AMEND_COOKIE_NAME,
        Buffer.from(JSON.stringify(cookieData)).toString('base64'),
        AMEND_COOKIE_EXPIRY_MINUTES,
        '/',
        ''
      );
      const amendUrl =
        area === Area.BB
          ? `${getSecureTwoURL()}/${country}/${language}/business-booker/amend/business/details.html`
          : `${getSecureTwoURL()}/${country}/${language}/amend/leisure/details.html`;
      return router.push(amendUrl);
    } else if (arrivalDate && surName) {
      // set cookie for use in A/B test for ancillaries page - scrollable tabs in mobile
      if (typeof window !== 'undefined') {
        // get cookie for scrollable tabs if it exists, and not for ccui
        const hasScrollableTabsCookie = getCookie(ANCILLARIES_TABS.cookieName);
        if (window?.piConfig?.ancillaries && !hasScrollableTabsCookie && area !== Area.CCUI) {
          const { mode } = window.piConfig.ancillaries;
          mode?.length &&
            setCookie(
              ANCILLARIES_TABS.cookieName,
              ANCILLARIES_TABS.mode,
              ANCILLARIES_TABS.expiryInMinutes
            );
        }
      }
      cleanupFindBookingToken();
      const queryClient = new QueryClient();
      const findBookingCriteria: FindBookingCriteria = {
        country,
        language,
        arrivalDate: arrivalDate,
        lastName: surName,
        resNo: bookingReference,
        bookingChannel: bookingChannel ?? {
          channel: area.toUpperCase() as Channel,
          subchannel: 'WEB',
          language: language.toUpperCase(),
        },
      };

      queryClient
        .fetchQuery({
          queryKey: [
            'amendFindBooking',
            findBookingCriteria.country,
            findBookingCriteria.language,
            findBookingCriteria.arrivalDate,
            findBookingCriteria.lastName,
            findBookingCriteria.resNo,
            findBookingCriteria.bookingChannel,
          ],
          queryFn: () => graphQLRequest(AMEND_FIND_BOOKING, { ...findBookingCriteria }),
        })
        .then((response: Query) => {
          setOperaBookingCookie(response);
          const BBUrlPrefix = area === Area.BB ? '/business-booker' : '';

          return router.push(
            `/${country}/${language}${BBUrlPrefix}/amend/details.html?bookingReference=${bookingReference}`
          );
        })
        .catch((error) => {
          console.log(error);
        });
    }
  }
}

const buttonsContainerStyle = {
  justifyContent: 'flex-end',
  mt: 'xl',
  direction: { mobile: 'column', lg: 'row' },
} as FlexProps;

const ccuiButtonWidth = (language: string) =>
  ({
    mobile: '100%',
    xl: '12.125rem',
    lg: language === 'de' ? '11.25rem' : '10rem',
  }) as unknown as ButtonProps;

const piButtonWidth = () =>
  ({
    mobile: '100%',
    lg: '15.5rem',
  }) as unknown as ButtonProps;

const idAndVButtonStyle = (language: string) =>
  ({
    ...ccuiButtonWidth(language),
    fontSize: 'lg',
    fontWeight: 'semibold',
    lineHeight: '3',
    size: 'sm',
    variant: 'primary',
  }) as unknown as ButtonProps;

const cancelButtonStyle = (language: string, area: string, isAmendPage?: boolean) =>
  ({
    size: 'sm',
    variant: 'tertiary',
    w:
      area === Area.CCUI && !isAmendPage
        ? { ...ccuiButtonWidth(language) }
        : { ...piButtonWidth() },
  }) as unknown as ButtonProps;

const buttonContainerStyle = {
  pl: {
    mobile: 0,
    lg: 'lg',
  },
  pt: {
    mobile: 'md',
    lg: '0',
  },
};

const amendButtonStyle = (language: string, area: string, isAmendPage?: boolean) =>
  ({
    fontSize: 'lg',
    fontWeight: 'semibold',
    lineHeight: '3',
    size: 'sm',
    variant: 'secondary',
    w:
      area === Area.CCUI && !isAmendPage
        ? { ...ccuiButtonWidth(language) }
        : { ...piButtonWidth() },
  }) as unknown as ButtonProps;

const reasonLabelStyle = {
  mt: { mobile: 'xs', xl: 'sm' },
  as: 'h6',
  color: 'darkGrey2',
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  textAlign: 'right',
  whiteSpace: 'normal',
  pl: { mobile: 'md', xs: '5xl', sm: 0 },
} as TextProps;
