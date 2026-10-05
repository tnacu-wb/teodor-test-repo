import type { BoxProps } from '@chakra-ui/react';
import { Box, Flex, Heading } from '@chakra-ui/react';
import styled from '@emotion/styled';
import { Currency } from '@whitbread-eos/api';
import { Input } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';

import { descriptionStyles, headerStyles } from '../styles';

interface Props {
  setValue: Dispatch<SetStateAction<string>>;
  discountValue: string;
  currency: string;
  language: string;
  onDiscountUpdate: () => void;
  isDiscountServerError: boolean;
  discountServerError?: Error;
  isDisabled: boolean;
  validateDiscountValue: (value: string) => boolean;
  discountValidationError: string;
  setDiscountValidationError: Dispatch<SetStateAction<string>>;
}

export default function DiscountSection({
  setValue,
  discountValue,
  currency,
  language,
  onDiscountUpdate,
  isDiscountServerError,
  discountServerError,
  isDisabled,
  validateDiscountValue,
  discountValidationError,
  setDiscountValidationError,
}: Readonly<Props>) {
  const refTextWidth = useRef(null);
  const [width, setWidth] = useState(0);
  const { t } = useTranslation(['common']);

  const hasValidValue = discountValue !== '' && !isNaN(parseInt(discountValue, 10));
  const iseurohotelier = currency !== Currency.GBP && language === 'en';

  useEffect(() => {
    if (refTextWidth.current) {
      setWidth(
        refTextWidth.current
          ? (
              refTextWidth.current as {
                offsetWidth: number;
              }
            ).offsetWidth
          : 0
      );
    }
  });

  useEffect(() => {
    if (isDiscountServerError) {
      const firstError = JSON.parse(JSON.stringify(discountServerError))?.response?.errors[0];
      let discountErrorCode = firstError?.errorInfo?.errCode;
      if (!discountErrorCode) {
        const parsedErrorMessage = JSON.parse(firstError?.message ?? '{}');
        discountErrorCode = parsedErrorMessage?.errCode;
      }

      if (discountErrorCode === 400) {
        setDiscountValidationError(
          isDiscountServerError ? t('ccui.payment.discount.errorMessage') : ''
        );
      } else {
        setDiscountValidationError(
          isDiscountServerError ? t('ccui.payment.discount.genericErrorMessage') : ''
        );
      }
    }
  }, [isDiscountServerError]);

  return (
    <Flex flexDir="column" data-testid="discountSection">
      <Box pb={4}>
        <Heading as="h3" {...headerStyles}>
          {t('ccui.payment.discount.title')}
        </Heading>
      </Box>
      <Box pb={4}>
        <Heading as="h6" {...descriptionStyles}>
          {t('ccui.payment.discount.description')}
        </Heading>
      </Box>
      <CustomBox
        innertextwidth={width}
        currency={currency}
        mb="xl"
        hasvalue={hasValidValue ? 'true' : 'false'}
        data-testid={`discountInput-c${currency}`}
        iseurohotelier={iseurohotelier ? 1 : 0}
        w={{
          mobile: 'full',
          xs: 'full',
          sm: '25.063rem',
          md: '26.25rem',
          lg: '24.5rem',
          xl: '26.25rem',
        }}
      >
        <Input
          name="discount"
          value={discountValue}
          onChange={onDiscountChange}
          placeholderText={t('ccui.payment.discount.placeHolder')}
          error={discountValidationError}
          onBlur={onDiscountUpdate}
          isDisabled={isDisabled}
          maxLength={10}
        />
      </CustomBox>
      <Box as="span" ref={refTextWidth} {...hiddenSpanStyles}>
        {discountValue}
      </Box>
    </Flex>
  );

  /**
   * Handle discount change (value) from the input
   */
  function onDiscountChange(value: string) {
    const isValid = validateDiscountValue(value);

    if (value === '') {
      setDiscountValidationError('');
    }
    setValue(value);

    if (isValid) {
      setValue(value);
    }
  }
}

const CustomBox = styled(Box)<{
  innertextwidth: number;
  currency: string;
  hasvalue: string;
  iseurohotelier: any;
}>`
  ${({ hasvalue, innertextwidth, currency, iseurohotelier }) =>
    hasvalue === 'true' &&
    `
  & > div > div:first-of-type {
    position: relative;
    &:after {
      content: '${currency}';
      position: absolute;
      left: ${
        currency === Currency.GBP || iseurohotelier ? '1.25rem' : `${innertextwidth / 16}rem`
      };
      top: 0;
      height: 100%;
      padding-left: ${currency === Currency.GBP || iseurohotelier ? 0 : `1.25rem`};
      display: flex;
      align-items: center;
    },
  }
  & > div > div > input {
    padding-left: ${currency === Currency.GBP || iseurohotelier ? '1.875rem' : '1rem'}
  }
  `}
`;

const hiddenSpanStyles = {
  visibility: 'hidden',
  w: 'max-content',
} as BoxProps;
