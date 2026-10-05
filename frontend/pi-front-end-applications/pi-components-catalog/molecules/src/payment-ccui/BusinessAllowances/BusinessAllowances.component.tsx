import { Box, Divider, Text } from '@chakra-ui/react';
import styled from '@emotion/styled';
import {
  BusinessAllowanceCCUItype,
  Currency,
  TOTAL_DINNER_BUDGET_REGEX,
  OTHER_ALLOWANCES_CCUI,
  FS_MEAL_RESTRICTIONS,
} from '@whitbread-eos/api';
import { Checkbox, Input } from '@whitbread-eos/atoms';
import { formatDataTestId, useFeatureSwitch } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

interface Props {
  toggleDinnerAllowance: () => void;
  dinnerAllowance: boolean;
  businessAllowances: BusinessAllowanceCCUItype;
  setBusinessAllowances: Dispatch<SetStateAction<BusinessAllowanceCCUItype>>;
  currency: string;
  language: string;
  setHasError: Dispatch<SetStateAction<boolean>>;
  mealPackagesSelected: boolean;
  availableMealsIds: (string | undefined)[];
  hasAncillariesWifiSelected?: boolean;
}

export type OtherAllowance = {
  type: keyof BusinessAllowanceCCUItype;
  name: string;
  enabled: boolean;
  operaId: string | null;
};

export default function BusinessAllowancesCCUI({
  toggleDinnerAllowance,
  dinnerAllowance,
  businessAllowances,
  setBusinessAllowances,
  currency,
  language,
  setHasError,
  mealPackagesSelected,
  availableMealsIds = [],
  hasAncillariesWifiSelected,
}: Readonly<Props>) {
  const activateMealSelectedRestrictions = useFeatureSwitch({
    featureSwitchKey: FS_MEAL_RESTRICTIONS,
    fallbackValue: false,
  });

  const mealAvailable = (id: string | null) => {
    if (activateMealSelectedRestrictions && mealPackagesSelected) {
      return false;
    }
    if (id) {
      return availableMealsIds.includes(id);
    }
    return true;
  };

  const otherAllowancesArray: OtherAllowance[] = Object.values(OTHER_ALLOWANCES_CCUI).map(
    (allowance) => {
      let enabled = !!allowance?.operaId === false || mealAvailable(allowance?.operaId);
      if (allowance.type === 'ultimateWifi' && hasAncillariesWifiSelected) {
        enabled = false;
      }
      return {
        ...allowance,
        type: allowance.type as keyof BusinessAllowanceCCUItype,
        enabled,
      };
    }
  );

  const { t } = useTranslation(['common']);

  const baseDataTestId = 'BusinessAllowances';
  const refTextWidth = useRef(null);
  const [width, setWidth] = useState(0);
  const [errorBudgetInput, setErrorBudgetInput] = useState<string>('');

  const totalDinnerBudget = String(businessAllowances.totalDinnerBudgetPersonNight);
  const hasValidValue = totalDinnerBudget !== '' && !isNaN(parseInt(totalDinnerBudget, 10));

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
  }, []);

  function onBudgetInputChange(totalDinnerBudgetPersonNight: string) {
    validateBudgetInput(
      totalDinnerBudgetPersonNight,
      t('ccui.businessAllowances.inputTitle.invalidMessage'),
      setErrorBudgetInput
    );
    setBusinessAllowances((prevState) => ({
      ...prevState,
      totalDinnerBudgetPersonNight,
    }));
  }

  function validateBudgetInput(
    budget: string,
    invalidMessage: string,
    handleError: React.Dispatch<React.SetStateAction<string>>
  ) {
    if (!TOTAL_DINNER_BUDGET_REGEX.test(String(budget))) {
      handleError(invalidMessage);
      setHasError(true);
    } else {
      handleError('');
      setHasError(false);
    }
  }

  return (
    <Box
      mt={16}
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      {...businessAllowancesWrapperStyle}
    >
      <Text data-testid={formatDataTestId(baseDataTestId, 'Title')} {...titleStyle}>
        {t('ccui.businessAllowances.title')}
      </Text>

      <Box {...boxStyle}>
        <Checkbox
          data-testid={formatDataTestId(baseDataTestId, 'ToggleDinnerAllowance')}
          ml="md"
          onChange={toggleDinnerAllowance}
          isChecked={dinnerAllowance}
        >
          <Text
            data-testid={formatDataTestId(baseDataTestId, 'ToggleDinnerAllowance-Label')}
            {...checkboxLabelStyle}
          >
            {t('ccui.businessAllowances.primaryAllowances.dinner')}
          </Text>
        </Checkbox>

        <Divider {...dividerStyles} />

        <Checkbox
          data-testid={formatDataTestId(baseDataTestId, 'ToggleAlcoholAllowance')}
          ml="md"
          onChange={() =>
            setBusinessAllowances({
              ...businessAllowances,
              isAlcoholDinner: !businessAllowances.isAlcoholDinner,
            })
          }
          isChecked={businessAllowances.isAlcoholDinner}
          isDisabled={!dinnerAllowance}
        >
          <Text
            data-testid={formatDataTestId(baseDataTestId, 'ToggleAlcoholAllowance-Label')}
            {...checkboxLabelStyle}
          >
            {t('ccui.businessAllowances.primaryAllowances.alcohol')}
          </Text>
        </Checkbox>
      </Box>

      <CustomBox
        {...customBoxStyle}
        innertextwidth={width}
        currency={currency}
        hasvalue={hasValidValue ? 'true' : 'false'}
        data-testid={formatDataTestId(baseDataTestId, `budget-${currency}`)}
        iseurohotelier={iseurohotelier ? 1 : 0}
      >
        <Input
          data-testid={formatDataTestId(baseDataTestId, 'DinnerBudget')}
          label={t('ccui.businessAllowances.inputTitle')}
          placeholderText={t('ccui.businessAllowances.inputTitle.placeholder')}
          name="totalDinnerBudgetPersonNight"
          value={businessAllowances.totalDinnerBudgetPersonNight?.toString()}
          isDisabled={!dinnerAllowance}
          error={errorBudgetInput}
          onChange={onBudgetInputChange}
        />
      </CustomBox>
      <Text mb={1} data-testid={formatDataTestId(baseDataTestId, 'OtherAllowances')}>
        {t('businessAllowances.otherAllowances')}
      </Text>

      <Box data-testid={formatDataTestId(baseDataTestId, `OtherAllowances_Box`)} {...boxStyle}>
        {otherAllowancesArray.map((allowances, index) => (
          <Box key={uuidv4()}>
            <Checkbox
              key={`${allowances.type}_${uuidv4()}`}
              data-testid={formatDataTestId(baseDataTestId, `${allowances.type}_Checkbox`)}
              ml="md"
              onChange={() =>
                setBusinessAllowances({
                  ...businessAllowances,
                  [allowances.type]: !businessAllowances[allowances.type],
                })
              }
              isChecked={!!businessAllowances[allowances.type]}
              isDisabled={!allowances.enabled}
            >
              <Text
                data-testid={formatDataTestId(baseDataTestId, `${allowances.type}_Label`)}
                {...checkboxLabelStyle}
              >
                {t(`ccui.businessAllowances.otherAllowances.${allowances.type}`)}
              </Text>
            </Checkbox>
            {index < 4 && <Divider {...dividerStyles} />}
          </Box>
        ))}
      </Box>
    </Box>
  );
}

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

const businessAllowancesWrapperStyle = {
  mb: '2xl',
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '27.563rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
};

const boxStyle = {
  border: '1px',
  borderColor: 'lightGrey4',
};

const titleStyle = { fontSize: 'xl', fontWeight: 'semibold', mb: 'xl', lineHeight: 3 };

const checkboxLabelStyle = {
  fontSize: 'md',
  lineHeight: '3',
  mx: ' sm',
};
const dividerStyles = {
  borderColor: 'lightGrey2',
  my: 'sm',
};

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
    padding-left: 1.875rem;
  }
  `}
`;
