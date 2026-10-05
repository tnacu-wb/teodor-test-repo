import { Box, Text } from '@chakra-ui/react';
import { Checkbox } from '@whitbread-eos/atoms';
import { formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface MarketingEmailProps {
  handleMarketingOptin: (optin: boolean) => void;
  testIdPrefix: string;
}
export default function MarketingEmail({
  handleMarketingOptin,
  testIdPrefix = '',
}: Readonly<MarketingEmailProps>) {
  const testId = formatDataTestId(testIdPrefix, 'MarketingEmail');
  const { t } = useTranslation();

  return (
    <Box data-testid={formatDataTestId(testId, 'Container')}>
      <Text {...headerStyle} data-testid={formatDataTestId(testId, 'Header')}>
        {t('booking.pib.marketingEmail.title')}
      </Text>
      <Box
        {...descriptionStyle}
        data-testid={formatDataTestId(testId, 'Description')}
        className="formatLinks"
      >
        {renderSanitizedHtml(t('booking.pib.marketingEmail.description'))}
      </Box>
      <Checkbox
        name="MarketingEmailCheckbox"
        data-testid={formatDataTestId(testId, 'CheckboxContainer')}
        onChange={(e) => handleMarketingOptin(e.target.checked)}
      >
        <Text {...descriptionStyle} data-testid={formatDataTestId(testId, 'CheckboxText')}>
          {renderSanitizedHtml(t('booking.pib.marketingEmail.checkbox'))}
        </Text>
      </Checkbox>
    </Box>
  );
}

const headerStyle = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
  mb: 'md',
};

const descriptionStyle = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
  color: 'darkGrey1',
  mb: 'md',
};
