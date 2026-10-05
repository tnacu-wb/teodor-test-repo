import { Image, Box, Text } from '@chakra-ui/react';
import {
  Area,
  PaymentMethod,
  paymentOptions,
  GOOGLE,
  APPLE,
  PAYPAL_PAYMENT,
  GET_PAYMENT_METHODS_QUERY,
  PaymentOption,
  DEFAULT_SELECTED_PAYMENT_DETAIL,
  paymentOptions as PaymentType,
} from '@whitbread-eos/api';
import { Tabs } from '@whitbread-eos/atoms';
import {
  getAuthCookie,
  useCustomLocale,
  formatAssetsUrl,
  useQueryRequest,
  formatDataTestId,
  isUrl,
  sortImagesByOrder,
} from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import { Dispatch, SetStateAction, useEffect } from 'react';

import PaymentTypeContainer from '../PaymentType';

interface DynamicObject {
  [key: string]: string;
}

interface Props {
  handlePaymentTypeSection: Dispatch<SetStateAction<PaymentMethod>>;
  selectedPaymentType: PaymentMethod;
  setSelectedPaymentDetail: Dispatch<SetStateAction<PaymentOption>>;
  t: (id: string) => string;
  userType?: string;
  variant?: Area;
  selectedPaymentDetail: {
    type: string;
    order: number;
    enabled: boolean;
  };
}

const baseDataTestId = 'PaymentDetailsTabs';
export default function PaymentDetailsTabs({
  handlePaymentTypeSection,
  selectedPaymentDetail,
  selectedPaymentType,
  setSelectedPaymentDetail,
  t,
  userType,
  variant,
}: Readonly<Props>) {
  const router = useRouter();
  const { query } = router;
  const { language, country } = useCustomLocale();
  const idTokenCookie = getAuthCookie();

  const setAsPayOnArrival = () => {
    const payOnArrivalOptionAvailable = selectedPaymentType?.paymentOptions?.find(
      (m: PaymentOption) => m?.type === paymentOptions.PAY_ON_ARRIVAL && m?.enabled
    );

    if (payOnArrivalOptionAvailable) {
      setSelectedPaymentDetail(payOnArrivalOptionAvailable);
    }
  };

  useEffect(() => {
    if (selectedPaymentDetail.type === 'default') {
      setAsPayOnArrival();
    }
  }, [selectedPaymentType]);

  const { data } = useQueryRequest(
    ['getPaymentMethods', language, country, query.reservationId],
    GET_PAYMENT_METHODS_QUERY,
    {
      basketReference: query.reservationId,
      language,
      country,
      userType: idTokenCookie ? userType : undefined,
      ...(variant && variant === Area.PI && { clientChannel: variant.toUpperCase() }),
    },
    {
      cacheTime: 0,
    },
    idTokenCookie
  );

  const onlyPaynowAvailable = data?.paymentMethods?.every((payment: PaymentMethod) => {
    return payment.paymentOptions?.every(
      (option) => !(option.type === PaymentType.PAY_ON_ARRIVAL && option.enabled)
    );
  });

  useEffect(() => {
    if (onlyPaynowAvailable && selectedPaymentDetail?.type !== PaymentType.PAY_NOW) {
      const payNow = selectedPaymentType.paymentOptions?.find(
        (paymentOption) => paymentOption.type === PaymentType.PAY_NOW
      );
      if (payNow) {
        setSelectedPaymentDetail(payNow);
      }
    }
  }, [onlyPaynowAvailable, selectedPaymentType]);

  if (onlyPaynowAvailable) {
    return (
      <PaymentTypeContainer
        selectedPaymentDetail={selectedPaymentDetail}
        selectedPaymentType={selectedPaymentType}
        onPaymentTypeClick={handlePaymentTypeSection}
        userType={userType}
        variant={variant}
        isUsedWithTabs={true}
        onlyShowPayNow={onlyPaynowAvailable}
      />
    );
  }

  const paymentTypesImages = () => {
    return data
      ? data?.paymentMethods?.reduce(
          (
            acc: {
              payNow: DynamicObject;
              payOnArrival: DynamicObject;
            },
            payment: PaymentMethod
          ) => {
            let payNowEnabled = false;
            let payOnArrivalEnabled = false;

            payment?.paymentOptions?.forEach((option) => {
              if (option.type === paymentOptions.PAY_NOW && option.enabled) {
                payNowEnabled = true;
              }

              if (option.type === paymentOptions.PAY_ON_ARRIVAL && option.enabled) {
                payOnArrivalEnabled = true;
              }
            });

            if (payNowEnabled) {
              if (
                (payment?.name === PAYPAL_PAYMENT ||
                  payment?.name === GOOGLE ||
                  payment?.name === APPLE) &&
                payment.type &&
                payment.logoSrc
              ) {
                acc.payNow[payment.type] = payment.logoSrc;
              } else {
                payment?.acceptedCardTypes?.forEach((card) => {
                  if (!acc.payNow[card.type] && card.type && card.logoSrc) {
                    acc.payNow[card.type] = card.logoSrc;
                  }
                });
              }
            }
            if (payOnArrivalEnabled) {
              if (
                (payment?.name === PAYPAL_PAYMENT ||
                  payment?.name === GOOGLE ||
                  payment?.name === APPLE) &&
                payment.type &&
                payment.logoSrc
              ) {
                acc.payNow[payment.type] = payment.logoSrc;
              } else {
                payment?.acceptedCardTypes?.forEach((card) => {
                  if (!acc.payOnArrival[card.type] && card.type && card.logoSrc) {
                    acc.payOnArrival[card.type] = card.logoSrc;
                  }
                });
              }
            }

            return acc;
          },
          { payNow: {}, payOnArrival: {} }
        )
      : { payNow: {}, payOnArrival: {} };
  };

  // Sort payment images in the tab design
  const sortedImageList = {
    payNow: sortImagesByOrder(paymentTypesImages().payNow),
    payOnArrival: sortImagesByOrder(paymentTypesImages().payOnArrival),
  };

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Container')}>
      <Text {...titleStyle} data-testid={formatDataTestId(baseDataTestId, 'Header')}>
        {t('paymentOptions.header')}
      </Text>
      <Text {...textStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        {t('paymentOptions.title')}
      </Text>
      <Tabs
        variant="greyTabsGroup"
        sx={{ ...tabsStyles() }}
        onChange={(i) => {
          const paymentOption =
            selectedPaymentType?.paymentOptions?.[i] ?? DEFAULT_SELECTED_PAYMENT_DETAIL;
          setSelectedPaymentDetail(paymentOption);
        }}
        defaultIndex={1}
        styles={{ tab, tabList }}
        options={[
          {
            index: 0,
            label: t(`ccui.paymentOption.payNow`),
            images: renderImages(sortedImageList.payNow),
            content: (
              <PaymentTypeContainer
                selectedPaymentDetail={selectedPaymentDetail}
                selectedPaymentType={selectedPaymentType}
                onPaymentTypeClick={handlePaymentTypeSection}
                userType={userType}
                variant={variant}
                isUsedWithTabs={true}
              />
            ),
          },
          {
            index: 1,
            label: t(`ccui.paymentOption.payOnArrival`),
            images: renderImages(sortedImageList.payOnArrival),
            content: (
              <PaymentTypeContainer
                selectedPaymentDetail={selectedPaymentDetail}
                selectedPaymentType={selectedPaymentType}
                onPaymentTypeClick={handlePaymentTypeSection}
                userType={userType}
                variant={variant}
                isUsedWithTabs={true}
              />
            ),
          },
        ]}
      />
    </Box>
  );
}

const renderImages = (imageObject: DynamicObject) => {
  let hasMoreThanSix = false;
  const keys = Object.keys(imageObject);
  let numberOfColumns = keys.length;
  if (keys.length > 6) {
    hasMoreThanSix = true;
  }

  let images = keys.map((key) => (
    <Image
      {...imgStyle}
      src={isUrl(imageObject[key]) ? imageObject[key] : formatAssetsUrl(imageObject[key])}
      data-testid={formatDataTestId(baseDataTestId, `Image-${key}`)}
    />
  ));

  if (hasMoreThanSix) {
    images = images.slice(0, 5);
    numberOfColumns = 6;
  }

  return (
    <Box
      {...imgContainerStyles(numberOfColumns)}
      data-testid={formatDataTestId(baseDataTestId, 'ImageContainer')}
    >
      {images}
      {hasMoreThanSix && (
        <Box {...imgStyle} {...placeholderImage}>
          + {keys.length - 5}
        </Box>
      )}
    </Box>
  );
};

const tabsStyles = () => ({
  border: '1px solid #D7D8D6',
  mb: '4rem',
  width: {
    mobile: '18rem',
    xs: '21.5rem',
    sm: '33.75rem',
    md: '45rem',
    lg: '50.5rem',
    xl: '54rem',
  },
  '.chakra-tabs__tab-panel': {
    px: { mobile: '1rem', xs: '1rem', sm: '1rem', md: '1.5rem', lg: '3rem', xl: '3rem' },
    py: { mobile: '1.5rem', xs: '1.5rem', sm: '1.5rem', md: '2rem', lg: '3rem', xl: '3rem' },
  },
});

const imgContainerStyles = (columns: number) => ({
  display: 'grid',
  gridAutoFlow: 'rows',
  gridTemplateColumns: { mobile: 'repeat(3, 1fr)', md: `repeat(${columns}, 1fr)` },
  columnGap: '0.75rem',
  rowGap: { mobile: '1rem', md: 'unset' },
  paddingTop: '1rem',
});

const imgStyle = {
  w: { mobile: '1.5rem', md: '2.5rem' },
  h: { mobile: '1rem', md: '1.5rem' },
};

const placeholderImage = {
  bg: 'darkGrey2',
  color: 'baseWhite',
  display: 'flex',
  justifyContent: 'center',
  fontSize: { mobile: '0.75rem', md: '1rem' },
};

const tabList = {
  height: { mobile: '7rem', md: '5.5rem' },
};

const tab = {
  height: { mobile: '7rem', md: '5.5rem' },
  paddingTop: '0.5rem',
  alignItems: 'start',
  _selected: {
    height: { mobile: '7rem', md: '5.5rem' },
    bg: 'baseWhite',
  },
};

const titleStyle = {
  fontSize: '3xxl',
  fontWeight: 'medium',
  mb: 'md',
  color: 'darkGrey1',
  lineHeight: '2rem',
};

const textStyle = {
  color: 'darkGrey1',
  mb: '4rem',
};
