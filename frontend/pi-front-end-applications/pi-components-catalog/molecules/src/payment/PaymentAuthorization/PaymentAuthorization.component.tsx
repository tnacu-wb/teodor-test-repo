import { Box, Text } from '@chakra-ui/react';
import { Checkbox, Input } from '@whitbread-eos/atoms';
import { formatDataTestId, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState, Dispatch, SetStateAction, useEffect } from 'react';

interface Props {
  togglePaymentAuth: () => void;
  paymentAuth: boolean;
  password: string;
  onChangePassword: Dispatch<SetStateAction<string>>;
  isPaymentAuthError?: boolean;
}

export default function PaymentAuthorization({
  togglePaymentAuth,
  paymentAuth,
  password,
  onChangePassword,
  isPaymentAuthError,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();
  const [showError, setShowError] = useState(false);
  const baseDataTestId = 'PaymentAuth';

  const inputTypographyStyles = {
    inputElementStyles: getTypographyProps({}, passwordInputSemanticTypography),
  };

  useEffect(() => {
    if (isPaymentAuthError) {
      setShowError(isPaymentAuthError);
    }
  }, [isPaymentAuthError]);

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Container')} {...paymentAuthWrapperStyle}>
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'PaymentAuthTitle')}
        {...titleLayoutStyles}
        {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
      >
        {t('booking.bac.paymentAuthorisation')}
      </Text>

      <Checkbox
        data-testid={formatDataTestId(baseDataTestId, 'CheckboxInfo')}
        onChange={togglePaymentAuth}
        isChecked={paymentAuth}
      >
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'CheckboxLabel')}
          {...checkboxLabelLayoutStyles}
          {...getTypographyProps(checkboxLabelLegacyTypography, checkboxLabelSemanticTypography)}
        >
          {t('cardNotPresent.info')}
        </Text>
      </Checkbox>

      {paymentAuth && (
        <Box maxW="var(--chakra-space-breakpoint-m)" mt="md">
          <Input
            data-testid={formatDataTestId(baseDataTestId, 'MemorableWord')}
            label={t('cardNotPresent.memorableWord')}
            placeholderText={t('cardNotPresent.memorableWord')}
            type="password"
            name="password"
            value={password}
            error={showError && t('cardNotPresent.memorableWordRequired')}
            useTooltip={showError}
            styles={inputTypographyStyles}
            onChange={(val) => onChangePassword(val)}
            onBlur={() => validatePassword()}
          />
        </Box>
      )}
    </Box>
  );

  function validatePassword() {
    if (password.length === 0) {
      setShowError(true);
    } else {
      setShowError(false);
    }
  }
}

const paymentAuthWrapperStyle = {
  mb: '5xl',
  w: { mobile: 'full', xs: 'full', md: '40.125rem' },
};

const titleLayoutStyles = {
  mb: 'md',
  color: 'darkGrey1',
};

const titleLegacyTypography = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
};

const titleSemanticTypography = {
  textStyle: 'title-m-emphasis',
} as const;

const checkboxLabelLayoutStyles = {
  mx: 'sm',
  color: 'darkGrey1',
};

const checkboxLabelLegacyTypography = {
  lineHeight: '3',
};

const checkboxLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const passwordInputSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;
