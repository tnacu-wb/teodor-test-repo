import { Box, Flex, RadioGroup, Text } from '@chakra-ui/react';
import type { PaymentOption } from '@whitbread-eos/api';
import { RadioButton } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  billingAddress: string;
  selectedPaymentDetail: PaymentOption;
}

interface BillingAddressOption {
  value: string;
  label: string;
  testId: string;
  isChecked: boolean;
  isDisabled?: boolean;
}

const CURRENT_ADDRESS_VALUE = 'CurrentAddress';
const DIFFERENT_ADDRESS_VALUE = 'DifferentAddress';
const PAY_ON_ARRIVAL = 'PAY_ON_ARRIVAL';

export default function AmendBillingAddress({
  billingAddress,
  selectedPaymentDetail,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const baseDataTestId = 'AmendBillingAddress';
  const options: BillingAddressOption[] = getBillingAddressOptions();
  const selectedOption = options.find((o) => o.isChecked);
  const isSectionDisabled = selectedPaymentDetail.type === PAY_ON_ARRIVAL;

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'section')} {...containerStyle}>
      <Text data-testid={formatDataTestId(baseDataTestId, 'title')} {...titleStyle}>
        {t('billingAddress.title')}
      </Text>
      <RadioGroup value={isSectionDisabled ? '' : selectedOption?.value}>
        {options.map((option, index) => {
          const isLastOption = index === Number(options.length) - 1;
          return (
            <RadioButton
              key={option.value}
              name={option.value}
              value={option.value}
              data-testid={option.testId}
              isChecked={option.isChecked && !isSectionDisabled}
              isDisabled={option.isDisabled ?? isSectionDisabled}
              listIndex={isLastOption ? 'last' : index}
            >
              {option.value === CURRENT_ADDRESS_VALUE ? (
                renderCurrentBillingAddressOption(option)
              ) : (
                <Text>{option.label}</Text>
              )}
            </RadioButton>
          );
        })}
      </RadioGroup>
    </Box>
  );

  function renderCurrentBillingAddressOption(option: BillingAddressOption) {
    return (
      <Flex direction="column">
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-UseCurrentAdress-Title')}
          {...addressStyle}
          fontWeight={option.isChecked ? 'semibold' : 'normal'}
        >
          {t('billingAddress.current')}
        </Text>
        {billingAddress && (
          <Text
            className="sessioncamhidetext assist-no-show"
            data-testid={formatDataTestId(baseDataTestId, 'BillingAddress-UseCurrentAdress-Label')}
            {...addressStyle}
          >
            {billingAddress}
          </Text>
        )}
      </Flex>
    );
  }
  function getBillingAddressOptions() {
    return [
      {
        value: CURRENT_ADDRESS_VALUE,
        label: t('billingAddress.current'),
        testId: formatDataTestId(baseDataTestId, 'CurrentAddress'),
        isChecked: true,
      },
      {
        value: DIFFERENT_ADDRESS_VALUE,
        label: t('billingAddress.different'),
        testId: formatDataTestId(baseDataTestId, 'DifferentAddress'),
        isChecked: false,
        isDisabled: true,
      },
    ];
  }
}
const addressStyle = {
  fontSize: 'md',
  lineHeight: '3',
};
const containerStyle = {
  w: { mobile: 'full', xs: 'full', sm: '25.063rem', md: '27.563rem', xl: '26.25rem' },
  mb: '5xl',
};
const titleStyle = { mb: 'xl', fontSize: '2xl', fontWeight: 'semibold', lineHeight: '4' };
