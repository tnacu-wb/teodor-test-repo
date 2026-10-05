import type { BoxProps, StyleProps } from '@chakra-ui/react';
import { Box, Button, Flex, Text } from '@chakra-ui/react';
import { GET_COUNTRIES, INITIATE_PRE_CHECKIN_SCA, SITE_LEISURE } from '@whitbread-eos/api';
import { DetailsPanel, ModalVariants } from '@whitbread-eos/atoms';
import {
  getFindBookingToken,
  getSortedCountriesByCurrentLang,
  useCustomLocale,
  useIPageSubmission,
  useMutationRequest,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { format, isDate } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { CSSProperties, useEffect, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

import { DATE_FORMAT } from '../utils/constants';
import { generateRegCard } from './RegCard';
import ScaCard from './ScaCard';
import SignatureSection from './Signature/SignatureSection';
import {
  generateDependentsData,
  handleIframeHeight,
  handleRedirect,
  isCanvasEmpty,
} from './common';
import { Nationality, ReviewModalProps } from './types';

function PreCheckInReviewModal({
  isOpen,
  onClose,
  data,
  handleScaSuccess,
  hotelAddress,
}: Readonly<ReviewModalProps>) {
  const { t } = useTranslation();
  const { isPaymentComplete, transactionId, setIsPaymentComplete, paymentStatus } =
    useIPageSubmission();
  const { language, country } = useCustomLocale();
  const { token, basketReference } = getFindBookingToken();
  const { push, pathname } = useRouter();
  const [nationalities, setNationalities] = useState<Nationality[]>([]);

  const [signatureTabIndex, setSignatureTabIndex] = useState(0);
  const [scaOpen, setScaOpen] = useState(false);
  const canvasRef = useRef<HTMLCanvasElement>(null) as React.MutableRefObject<HTMLCanvasElement>;
  const typedCanvasRef = useRef<HTMLCanvasElement>(
    null
  ) as React.MutableRefObject<HTMLCanvasElement>;
  const [signatureUrl, setSignatureUrl] = useState<string>();
  const [showSignatureEmptyError, setShowSignatureEmptyError] = useState(false);
  const [scaVerificationErrorVisible, setScaVerificationErrorVisible] = useState(false);
  const [scaErrorVisible, setScaErrorVisible] = useState(false);

  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', country, language, SITE_LEISURE],
    GET_COUNTRIES,
    {
      country,
      language,
      site: SITE_LEISURE,
    }
  );

  const {
    mutation: initiatePaymentMutation,
    isSuccess,
    data: paymentData,
    isError: paymentError,
    isLoading: paymentLoading,
  } = useMutationRequest(INITIATE_PRE_CHECKIN_SCA, true);

  useEffect(() => {
    if (showSignatureEmptyError) setTimeout(() => setShowSignatureEmptyError(false), 5000);
  }, [showSignatureEmptyError]);

  useEffect(() => {
    if (scaOpen && (paymentError || scaVerificationErrorVisible)) {
      setScaErrorVisible(true);
      setTimeout(() => {
        setScaVerificationErrorVisible(false);
        setScaErrorVisible(false);
      }, 5000);
    }
  }, [paymentError, scaVerificationErrorVisible, scaOpen]);

  useEffect(() => {
    if (countriesRequestSuccess && !nationalities.length) {
      const sortedCountries = getSortedCountriesByCurrentLang(
        countriesData?.countries?.countries,
        language
      );
      setNationalities(
        sortedCountries.map(({ nationality, countryCode, countryName, flagSrc }) => ({
          value: countryCode,
          label: nationality ?? '',
          image: flagSrc,
          countryName,
        }))
      );
    }
  }, [countriesRequestSuccess, countriesData, language, nationalities]);

  useEffect(() => {
    if (isPaymentComplete)
      if (paymentStatus === 'FAILURE') {
        setScaVerificationErrorVisible(true);
        handleRetrySCAVerification();
      } else if (paymentStatus === 'SUCCESS') handleSCASucessfulVerification();
  }, [isPaymentComplete, paymentStatus]);

  const handleClose = () => {
    setScaOpen(false);
    setSignatureUrl(undefined);
    onClose();
  };

  const handleSubmit = async () =>
    await handleScaSuccess(
      await generateRegCard({
        t,
        transactionid: transactionId as unknown as string,
        data,
        signatureUrl,
        hotelAddress,
        nationalities,
      })
    );

  const handleSCASucessfulVerification = async () => {
    await handleSubmit();
    handleClose();
  };

  useEffect(() => {
    handleIframeHeight(isSuccess);
  }, [isSuccess]);

  if (!data) return null;

  const getFormattedDate = (date?: Date) => {
    return date && isDate(date) ? format(date, DATE_FORMAT) : '';
  };

  const generateBookingData = () => {
    const { bookingReference = '', hotelName = '', arrivalDate, departureDate } = data;
    return {
      title: t('precheckin.yourbooking'),
      rows: [
        { key: t('precheckin.yourbooking.details.bookingreference'), value: bookingReference },
        { key: t('precheckin.yourbooking.details.hotelname'), value: hotelName },
        { key: t('precheckin.regcard.hoteladdress'), value: hotelAddress },
        {
          key: t('precheckin.yourbooking.details.arrivaldate'),
          value: getFormattedDate(arrivalDate),
        },
        {
          key: t('precheckin.yourbooking.details.departuredate'),
          value: getFormattedDate(departureDate),
        },
      ],
    };
  };

  const generateLeadGuest = () => {
    const {
      firstName = '',
      lastName = '',
      address = '',
      postalCode = '',
      city = '',
      dateOfBirth,
      nationality = { label: '', value: '' },
      passport = '',
      country = '',
      noOfRooms = 0,
      roomNo = 0,
    } = data;

    const formattedDateOfBirth = getFormattedDate(dateOfBirth);
    const nationalityLabel =
      nationalities.find(({ value }) => value === nationality?.value)?.label ?? '';

    const countryLabel = nationalities.find(({ value }) => value === country)?.countryName ?? '';

    return {
      title:
        noOfRooms > 1
          ? `${t('precheckin.leadguest.title')} ${roomNo + 1}`
          : t('precheckin.yourdetails.title'),
      rows: [
        { key: t('precheckin.regcard.firstname'), value: firstName },
        { key: t('precheckin.regcard.lastname'), value: lastName },
        { key: t('precheckin.regcard.homeaddress'), value: address },
        { key: t('precheckin.regcard.postcode'), value: postalCode },
        { key: t('precheckin.regcard.city'), value: city },
        { key: t('precheckin.regcard.country'), value: countryLabel },
        { key: t('precheckin.additionalfields.dateofbirth'), value: formattedDateOfBirth },
        { key: t('precheckin.additionalfields.nationalities'), value: nationalityLabel },
        { key: t('precheckin.details.passport'), value: passport },
      ].filter((item) => {
        if (item.key === t('precheckin.details.passport')) {
          return !!item.value; // Include only if passport value is present
        }
        return true;
      }),
    };
  };

  const handlePayment = async () => {
    const signRef = signatureTabIndex ? typedCanvasRef : canvasRef;
    if ((signRef?.current && !isCanvasEmpty(signRef.current)) || signatureUrl) {
      const signatureUrl = signRef.current?.toDataURL();
      if (signatureUrl) setSignatureUrl(signatureUrl);
      if (showSignatureEmptyError) setShowSignatureEmptyError(false);
      setScaOpen(true);
      if (token && basketReference) {
        try {
          initiatePaymentMutation.mutate({
            requestId: uuidv4(),
            environment: window.location.origin,
            language,
            country,
            bookingReference: data.bookingReference,
          });
        } catch (e) {
          handleClose();
        }
      } else {
        handleClose();
        handleRedirect(`/${country}/${language}${pathname}/?status=EXPIRED`, push);
      }
    } else {
      setShowSignatureEmptyError(true);
    }
  };

  const handleRetrySCAVerification = () => {
    setIsPaymentComplete(false);
    handlePayment();
  };

  const reviewBookingData = {
    yourBooking: generateBookingData(),
    leadGuest: generateLeadGuest(),
    dependentsData: generateDependentsData(nationalities, t, data?.dependents),
  };

  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={handleClose}
      variant="data"
      variantProps={{
        title: scaOpen ? t('precheckin.SCA.title') : t('precheckin.regcard.title'),
        delimiter: false,
        sizeSm: 'lg',
        externalTitleStying: {
          textAlign: 'center',
          justifyContent: 'center',
          pt: 'md',
          pb: 'sm',
          fontSize: '2xl',
          fontWeight: 'bold',
        },
      }}
      updatedWidth={{ sm: '100%', md: '68%' }}
    >
      {!scaOpen ? (
        <Box
          alignItems="left"
          flexWrap="wrap"
          padding="xl"
          paddingTop="0"
          marginBottom="0"
          data-testid="PreCheckInReviewModal-wrapper"
        >
          <Flex justifyContent="left" flexDirection="column">
            <Box style={itemStyle} data-testid="PreCheckInReviewModal-yourBooking">
              <DetailsPanel data={reviewBookingData.yourBooking} stylesObject={stylesObject} />
            </Box>
            <Box style={itemStyle} data-testid="PreCheckInReviewModal-leadGuest">
              <DetailsPanel
                data={reviewBookingData.leadGuest}
                stylesObject={extendedStylesObject}
              />
            </Box>
            {!!reviewBookingData.dependentsData.length && (
              <Box style={itemStyle} data-testid="PreCheckInReviewModal-additionalGuestWrapper">
                <Box {...titleWrapper} data-testid="PreCheckInReviewModal-additionalGuestTitle">
                  <Text {...titleStyle}>{t('precheckin.additionalguests.title')}</Text>
                </Box>

                {reviewBookingData.dependentsData.map((additionalGuest) => (
                  <DetailsPanel
                    data={additionalGuest}
                    stylesObject={guestStylesObject}
                    key={`additionalGuest-${additionalGuest.title}`}
                  />
                ))}
              </Box>
            )}
            <SignatureSection
              tabIndex={signatureTabIndex}
              setTabIndex={setSignatureTabIndex}
              typedCanvasRef={typedCanvasRef}
              canvasRef={canvasRef}
              showSignatureEmptyError={showSignatureEmptyError}
              setShowSignatureEmptyError={setShowSignatureEmptyError}
            />
          </Flex>
          <Box>
            <Flex {...buttonsFlexWrapper} data-testid="PreCheckInReviewModal-buttonsWrapper">
              <Button
                size="sm"
                variant="outline"
                colorScheme="teal"
                data-testid="PreCheckInReviewModal-editButton"
                onClick={handleClose}
                width={{ base: '100%', md: '20%' }}
                marginBottom="md"
              >
                {t('precheckin.details.edit')}
              </Button>
              <Button
                size="sm"
                variant="primary"
                data-testid="PreCheckInReviewModal-submitButton"
                width={{ base: '100%', md: '20%' }}
                onClick={handlePayment}
                marginBottom="md"
              >
                {t('precheckin.details.submit')}
              </Button>
            </Flex>
          </Box>
        </Box>
      ) : (
        <ScaCard
          scaErrorVisible={scaErrorVisible}
          isSuccess={isSuccess}
          paymentLoading={paymentLoading}
          paymentData={paymentData}
        />
      )}
    </ModalVariants>
  );
}

export default PreCheckInReviewModal;

const itemStyle: CSSProperties = {
  borderBottom: '1px solid var(--chakra-colors-lightGrey4)',
  paddingTop: 'var(--chakra-sizes-8)',
  paddingBottom: 'var(--chakra-sizes-8)',
};

const stylesObject = {
  wrapper: {
    pos: 'relative',
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

const extendedStylesObject = {
  ...stylesObject,
  rowKey: { ...stylesObject.rowKey, width: '17.5rem' },
};

const guestStylesObject = {
  ...extendedStylesObject,
  title: {
    fontSize: 'xl',
    fontWeight: 'semibold',
    color: 'darkGrey1',
    lineHeight: 'var(--chakra-sizes-8)',
    pb: 'md',
    pt: 'sm',
  } as StyleProps,
  wrapper: {
    ...stylesObject.wrapper,
    pb: 'sm',
    pt: 'sm',
  } as BoxProps,
};

const titleStyle = {
  fontSize: 'xl',
  fontWeight: 'bold',
};

const titleWrapper = {
  pos: 'relative',
} as BoxProps;

const buttonsFlexWrapper = {
  justifyContent: { base: 'center', md: 'flex-end' },
  flexDirection: { base: 'column', md: 'row' },
  gap: { base: 2, md: 4 },
  width: { base: '100%', md: 'auto' },
  mt: 'xl',
} as BoxProps;
