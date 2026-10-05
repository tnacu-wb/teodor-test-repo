import { Box, BoxProps } from '@chakra-ui/react';
import {
  Area,
  GET_PAYMENT_TYPE_OPTS_QUERY_CCUI,
  PaymentOption,
  PaymentMethod,
  AcceptedCardType,
  PaymentMethodsAvailable,
} from '@whitbread-eos/api';
import { formatAssetsUrl, useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { Dispatch, SetStateAction, useMemo } from 'react';

import { PaymentType } from '../../payment';

interface Props {
  onPaymentTypeClick: Dispatch<SetStateAction<PaymentMethod>>;
  selectedPaymentType: PaymentMethod;
  selectedPaymentDetail: PaymentOption;
  disabledOptions?: string[];
  hiddenPaymentMethodTypes?: Array<{ name?: string; type: string }>;
  styles?: {
    containerStyles?: BoxProps;
  };
  initialPaymentType?: string;
  isFromChangePaymentBIC?: boolean;
}

export default function PaymentTypeContainer({
  selectedPaymentDetail,
  selectedPaymentType,
  onPaymentTypeClick,
  disabledOptions,
  hiddenPaymentMethodTypes,
  initialPaymentType,
  styles,
  isFromChangePaymentBIC,
}: Readonly<Props>) {
  const router = useRouter();
  const { query } = router;
  const { language, country } = useCustomLocale();
  const basketRef = query.reservationId ?? query.basketReference;

  const { data, isError, error, isLoading } = useQueryRequest(
    ['getPaymentMethodsCCUI', language, country, basketRef],
    GET_PAYMENT_TYPE_OPTS_QUERY_CCUI,
    {
      basketReference: basketRef,
      language,
      country,
      changePaymentBIC: isFromChangePaymentBIC,
    }
  );

  const formatedData = { paymentMethods: data?.paymentCcuiMethods };

  const paymentOptions = useMemo(
    () =>
      JSON.parse(JSON.stringify(formatedData?.paymentMethods || []))?.reduce(
        (previous: any, current: any) => {
          (current.paymentOptions || [])?.forEach((option: any) => {
            const existing = previous?.find((c: any) => c?.type === option?.type);
            if (existing) {
              existing.enabled = existing.enabled || option.enabled;
            } else {
              previous = [...previous, option];
            }
          });
          return previous;
        },
        []
      ),
    [formatedData?.paymentMethods]
  );

  !isLoading &&
    formatedData?.paymentMethods?.forEach((method: PaymentMethodsAvailable) => {
      method.acceptedCardTypes?.forEach((cardType: AcceptedCardType) => {
        cardType && (cardType.logoSrc = formatAssetsUrl(cardType?.logoSrc as string));
      });
      method.paymentOptionsAvailable = paymentOptions;
    });
  const { t } = useTranslation(['common']);

  if (data)
    return (
      <Box data-testid="paymentTypeContainer_withData">
        <PaymentType
          {...{
            data: formatedData,
            isLoading,
            isError,
            error,
            t,
          }}
          selectedPaymentDetail={selectedPaymentDetail}
          selectedPaymentType={selectedPaymentType}
          onPaymentTypeClick={onPaymentTypeClick}
          disabledOptions={disabledOptions}
          hiddenPaymentMethodTypes={hiddenPaymentMethodTypes}
          variant={Area.CCUI}
          styles={styles}
          initialPaymentType={initialPaymentType}
        />
      </Box>
    );
  else return <Box data-testid="paymentTypeContainer_noData"></Box>;
}
