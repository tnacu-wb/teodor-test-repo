import {
  Area,
  GET_PAYMENT_METHODS_QUERY,
  PaymentOption,
  PaymentMethod,
  PaymentMethods,
  AcceptedCardType,
} from '@whitbread-eos/api';
import { PaymentType } from '@whitbread-eos/molecules';
import {
  analytics,
  formatAssetsUrl,
  useAuthToken,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { Dispatch, SetStateAction, useEffect } from 'react';

interface Props {
  onPaymentTypeClick: Dispatch<SetStateAction<PaymentMethod>>;
  selectedPaymentType: PaymentMethod;
  selectedPaymentDetail: PaymentOption;
  userType?: string;
  variant?: Area;
  amendBasketReference?: string;
  disabledCardOptions?: string[];
  amendPaymentCard?: PaymentMethod;
  isUsedWithTabs?: boolean;
  onlyShowPayNow?: boolean;
  isPaymentRedesignEnabled?: boolean;
}

export default function PaymentTypeContainer({
  selectedPaymentDetail,
  selectedPaymentType,
  onPaymentTypeClick,
  userType,
  variant,
  amendBasketReference,
  disabledCardOptions,
  amendPaymentCard,
  isUsedWithTabs,
  onlyShowPayNow,
  isPaymentRedesignEnabled,
}: Readonly<Props>) {
  const router = useRouter();
  const { query } = router;
  const { language, country } = useCustomLocale();
  const { token: authToken, isLoading: isAuthTokenLoading } = useAuthToken();
  const paymentMethodsQueryInput = {
    basketReference: query.reservationId ?? amendBasketReference,
    language,
    country,
    userType: authToken ? userType : undefined,
    ...(variant && variant === Area.PI && { clientChannel: variant.toUpperCase() }),
  };

  const {
    data,
    isError,
    error,
    isLoading,
  }: {
    data: PaymentMethods;
    isError: boolean;
    error: unknown;
    isLoading: boolean;
  } = useQueryRequest(
    ['getPaymentMethods', language, country, query.reservationId ?? amendBasketReference],
    GET_PAYMENT_METHODS_QUERY,
    {
      ...paymentMethodsQueryInput,
    },
    {
      gcTime: 0,
      enabled: !isAuthTokenLoading,
    },
    authToken
  );

  // assign default analyticsData values
  useEffect(() => {
    analytics.update({
      paymentCards: '0',
      paymentCardTypes: '',
    });
  }, []);

  useEffect(() => {
    if (!isLoading && data?.paymentMethods) {
      const validCards = data?.paymentMethods.filter(
        (method: PaymentMethod) => method.card && method.enabled
      );

      const paymentCardTypes = validCards
        .map((method: PaymentMethod) => method.card?.type ?? '')
        .filter(
          (type: string, index: number, self: string[]) => type && self.indexOf(type) === index
        )
        .join(',');

      analytics.update({
        paymentCards: `${validCards.length}`,
        paymentCardTypes,
      });
    }
  }, [isLoading, data]);

  data?.paymentMethods.forEach((method: PaymentMethod) =>
    method.acceptedCardTypes?.forEach((cardType: AcceptedCardType) => {
      cardType && (cardType.logoSrc = formatAssetsUrl(cardType?.logoSrc as string));
    })
  );

  const { t } = useTranslation(['common']);

  return data ? (
    <PaymentType
      {...{
        data: data,
        isLoading,
        isError,
        error,
        t,
      }}
      selectedPaymentDetail={selectedPaymentDetail}
      selectedPaymentType={selectedPaymentType}
      onPaymentTypeClick={onPaymentTypeClick}
      variant={variant}
      disabledOptions={disabledCardOptions}
      amendPaymentCard={amendPaymentCard}
      isUsedWithTabs={isUsedWithTabs}
      onlyShowPayNow={onlyShowPayNow}
      isPaymentRedesignEnabled={isPaymentRedesignEnabled}
    />
  ) : null;
}
