import type { BoxProps, TextProps } from '@chakra-ui/react';
import { Flex, Text, Box } from '@chakra-ui/react';
import type { BookingSummaryUpgradeToFlexProps } from '@whitbread-eos/api';
import { CancelPolicy } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import BookingSummaryUpgradeToFlex from '../../common/BookingSummary/BookingSummaryUpgradeToFlex/BookingSummaryUpgradeToFlex';

interface Props {
  rateDescription?: string;
  updateToFlex?: BookingSummaryUpgradeToFlexProps | null;
  rate?: string;
  hideUpgradeToFlex?: boolean;
}

export default function CancellationPolicy({
  rateDescription,
  updateToFlex,
  rate,
  hideUpgradeToFlex = false,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const rateText = t('booking.cancellation.policy.description').replace(
    '{rate}',
    `<strong>${rate}</strong>`
  );
  return (
    <Flex data-testid="cancellationPolicy" {...cancelBoxStyle}>
      <CancelPolicy style={{ display: 'inline' }} />
      <Text as="h5" {...cancelHeaderStyle}>
        {t('booking.cancellation.policy.title')}
      </Text>
      <Text as="div" {...cancelTextStyle}>
        {renderSanitizedHtml(rateText)} {rateDescription}
      </Text>
      {updateToFlex?.showUpgradeToFlex && !hideUpgradeToFlex && (
        <>
          <Box {...dividerStyle}>
            <hr />
          </Box>
          <BookingSummaryUpgradeToFlex {...updateToFlex} t={t} />
        </>
      )}
    </Flex>
  );
}

const cancelBoxStyle = {
  display: {
    lg: 'none',
    mobile: 'flex',
  },
  backgroundColor: 'lightGrey5',
  border: '1px solid var(--chakra-colors-lightGrey2)',
  borderRadius: 'md',
  mt: 'lg',
  padding: 'md',
  flexDirection: 'row',
  flexWrap: 'wrap',
  alignItems: 'center',
} as BoxProps;
const cancelHeaderStyle = {
  color: 'var(--chakra-colors-tertiary)',
  fontWeight: 'var(--chakra-fontWeights-bold)',
  display: 'inline-flex',
  ml: 'sm',
} as TextProps;
const cancelTextStyle = {
  mt: 'md',
  mb: 'md',
} as TextProps;

const dividerStyle = {
  width: '100%',
  mb: 'md',
} as BoxProps;
