import { Box, Divider, Text } from '@chakra-ui/react';
import styled from '@emotion/styled';
import { BusinessAllowance, Currency } from '@whitbread-eos/api';
import { Checkbox, Input, FORM_VALIDATIONS } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  useCustomLocale,
  formatCurrency,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';

interface Props {
  toggleDinnerAllowance: () => void;
  dinnerAllowance: boolean;
  paymentHasError?: boolean;
  businessAllowances: BusinessAllowance;
  setBusinessAllowances: Dispatch<SetStateAction<BusinessAllowance>>;
  businessAllowancesSections?: {
    amountDisabled?: boolean;
    allowDinner?: boolean;
    allowAlcohol?: boolean;
    allowCarParking?: boolean;
    allowWiFi?: boolean;
    amount?: number;
  };
  reservationDetails?: {
    currency: string;
  };
  hasBusinessAllowanceErrorState: [boolean, Dispatch<SetStateAction<boolean>>];
  hasAncillariesWifiSelected?: boolean;
}

export default function BusinessAllowances({
  toggleDinnerAllowance,
  dinnerAllowance,
  paymentHasError,
  businessAllowances,
  setBusinessAllowances,
  businessAllowancesSections = {
    amountDisabled: false,
    allowDinner: true,
    allowAlcohol: true,
    allowCarParking: true,
    allowWiFi: true,
    amount: 0,
  },
  reservationDetails = {
    currency: Currency.GBP,
  },
  hasBusinessAllowanceErrorState,
  hasAncillariesWifiSelected,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();
  const getTypographyProps = useSemanticTypography();
  const baseDataTestId = 'BusinessAllowances';
  const inputTypographyStyles = {
    inputElementStyles: getTypographyProps({}, inputFieldSemanticTypography),
  };

  const setDinnerBudget = (val: any) => {
    const inputRegex = new RegExp(/^\d{0,3}$/g);
    if (inputRegex.test(val)) {
      validateDinnerAllowance(val);
      setBusinessAllowances({
        ...businessAllowances,
        totalDinnerBudgetPersonNight: val.replace(/^0+/, ''),
      });
    }
  };

  const { totalDinnerBudgetPersonNight } = businessAllowances;
  const [hasBusinessAllowanceError, setHasBusinessAllowanceError] = hasBusinessAllowanceErrorState;
  const refTextWidth = useRef(null);
  const [width, setWidth] = useState(0);
  const currency = reservationDetails.currency === 'GBP' ? Currency.GBP : Currency.EUR;
  const hasDinnerAllowanceValidValue =
    totalDinnerBudgetPersonNight !== '' && !isNaN(parseInt(totalDinnerBudgetPersonNight, 10));
  const isEuroHotelUkSite = currency !== Currency.GBP && language === 'en';

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
  }, []);

  const validateDinnerAllowance = (dinnerAllowance: number) => {
    const isInvalidDinnerAllowance =
      dinnerAllowance < 1 ||
      dinnerAllowance > 999 ||
      !FORM_VALIDATIONS.DINNER_ALLOWANCE.MATCHES.test(dinnerAllowance.toString());
    setHasBusinessAllowanceError(isInvalidDinnerAllowance);
  };

  const resetDinnerAllowanceHandler = () => {
    setHasBusinessAllowanceError(false);
    if (!dinnerAllowance) {
      businessAllowances.totalDinnerBudgetPersonNight = businessAllowancesSections?.amount;
    }
  };

  useEffect(() => {
    if (paymentHasError && totalDinnerBudgetPersonNight < 1) {
      setHasBusinessAllowanceError(true);
    } else {
      setHasBusinessAllowanceError(false);
    }
  }, [paymentHasError]);

  return (
    <Box
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      {...businessAllowancesWrapperStyle}
    >
      <Text
        data-testid={formatDataTestId(baseDataTestId, 'Title')}
        {...titleLayoutStyles}
        {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
      >
        {t('booking.bac.businessAllowances')}
      </Text>

      {businessAllowancesSections.allowDinner && (
        <>
          <Box border="1px" borderColor="lightGrey4">
            <Checkbox
              data-testid={formatDataTestId(baseDataTestId, 'ToggleDinnerAllowance')}
              ml="md"
              onChange={() => {
                toggleDinnerAllowance();
                resetDinnerAllowanceHandler();
              }}
              isChecked={dinnerAllowance}
            >
              <Text
                data-testid={formatDataTestId(baseDataTestId, 'ToggleDinnerAllowance-Label')}
                {...checkboxLabelLayoutStyles}
                {...getTypographyProps(
                  checkboxLabelLegacyTypography,
                  checkboxLabelSemanticTypography
                )}
              >
                {t('booking.bac.businessDinnerBudget')}
              </Text>
            </Checkbox>
          </Box>

          {dinnerAllowance && (
            <CustomBox
              {...customBoxStyle}
              innertextwidth={width}
              currency={formatCurrency(reservationDetails.currency)}
              hasvalue={hasDinnerAllowanceValidValue ? 'true' : 'false'}
              data-testid={formatDataTestId(
                baseDataTestId,
                `budget-${formatCurrency(reservationDetails.currency)}`
              )}
              isEuroHotelUkSite={isEuroHotelUkSite}
            >
              <Input
                data-testid={formatDataTestId(baseDataTestId, 'DinnerBudget')}
                label={t('booking.bac.dinnerBudget')}
                placeholderText={t('booking.bac.dinnerBudget')}
                name="totalDinnerBudgetPersonNight"
                value={businessAllowances.totalDinnerBudgetPersonNight?.toString()}
                onChange={(val) => {
                  setDinnerBudget(val);
                }}
                error={hasBusinessAllowanceError && t('booking.bac.dinnerBudget.invalid')}
                isDisabled={businessAllowancesSections.amountDisabled}
                showLabel
                styles={inputTypographyStyles}
              />
            </CustomBox>
          )}
        </>
      )}

      {dinnerAllowance &&
        businessAllowancesSections.allowDinner &&
        businessAllowancesSections.allowAlcohol && (
          <Box border="1px" borderColor="lightGrey4">
            <Checkbox
              data-testid={formatDataTestId(baseDataTestId, 'ToggleAlcoholDinner')}
              ml="md"
              onChange={() => {
                setBusinessAllowances({
                  ...businessAllowances,
                  isAlcoholDinner: !businessAllowances.isAlcoholDinner,
                });
              }}
              isChecked={businessAllowances.isAlcoholDinner}
            >
              <Text
                data-testid={formatDataTestId(baseDataTestId, 'AlcoholLabel')}
                {...checkboxLabelLayoutStyles}
                {...getTypographyProps(
                  checkboxLabelLegacyTypography,
                  checkboxLabelSemanticTypography
                )}
              >
                {t('booking.bac.alcohol')}
              </Text>
            </Checkbox>
          </Box>
        )}

      {(businessAllowancesSections.allowCarParking || businessAllowancesSections.allowWiFi) && (
        <>
          {businessAllowancesSections.allowDinner && (
            <Text
              mt="lg"
              mb="xs"
              data-testid={formatDataTestId(baseDataTestId, 'OtherAllowances')}
              {...getTypographyProps({}, otherAllowancesSemanticTypography)}
            >
              {t('businessAllowances.otherAllowances')}
            </Text>
          )}

          {businessAllowancesSections.allowCarParking && (
            <>
              <Box border="1px" borderColor="lightGrey4">
                <Checkbox
                  data-testid={formatDataTestId(baseDataTestId, 'CarParkingCheckbox')}
                  ml="md"
                  onChange={() =>
                    setBusinessAllowances({
                      ...businessAllowances,
                      carParking: !businessAllowances.carParking,
                    })
                  }
                  isChecked={businessAllowances.carParking}
                >
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, 'CarParkingLabel')}
                    {...checkboxLabelLayoutStyles}
                    {...getTypographyProps(
                      checkboxLabelLegacyTypography,
                      checkboxLabelSemanticTypography
                    )}
                  >{`${t('booking.bac.carParking')}`}</Text>
                </Checkbox>
              </Box>
              <Divider />
            </>
          )}

          {businessAllowancesSections.allowWiFi && !hasAncillariesWifiSelected && (
            <>
              <Box border="1px" borderColor="lightGrey4">
                <Checkbox
                  data-testid={formatDataTestId(baseDataTestId, 'WifiCheckbox')}
                  ml="md"
                  onChange={() =>
                    setBusinessAllowances({
                      ...businessAllowances,
                      wifi: !businessAllowances.wifi,
                    })
                  }
                  isChecked={businessAllowances.wifi}
                >
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, 'WifiLabel')}
                    {...checkboxLabelLayoutStyles}
                    {...getTypographyProps(
                      checkboxLabelLegacyTypography,
                      checkboxLabelSemanticTypography
                    )}
                  >
                    {t('booking.bac.wifi')}
                  </Text>
                </Checkbox>
              </Box>
              <Divider />
            </>
          )}
        </>
      )}
    </Box>
  );
}

const businessAllowancesWrapperStyle = {
  mb: '2xl',
  w: { mobile: 'full', xs: 'full', sm: '25.063rem', md: '27.563rem', xl: '26.25rem' },
};

const titleLayoutStyles = {
  mb: 'xl',
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
  fontSize: 'md',
  lineHeight: '3',
};

const checkboxLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const inputFieldSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const otherAllowancesSemanticTypography = {
  textStyle: 'title-m-emphasis',
} as const;

const customBoxStyle = {
  maxW: '26.25rem',
  my: 'lg',
  mt: 'xl',
  mb: 'xl',
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '26.25rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
};

const CustomBox = styled(Box)<{
  innertextwidth: number;
  currency: string;
  hasvalue: string;
  isEuroHotelUkSite: boolean;
}>`
  ${({ hasvalue, innertextwidth, currency, isEuroHotelUkSite }) =>
    hasvalue === 'true' &&
    `
  & > div > div:first-of-type {
    position: relative;
    &:after {
      content: '${currency}';
      position: absolute;
      left: ${
        currency === Currency.GBP || isEuroHotelUkSite ? '1.25rem' : `${innertextwidth / 16}rem`
      };
      top: 0;
      height: 100%;
      padding-left: ${currency === Currency.GBP || isEuroHotelUkSite ? 0 : `1.25rem`};
      display: flex;
      align-items: center;
    },
  }
  & > div > div > input {
    padding-left: 1.875rem;
  }
  `}
`;
