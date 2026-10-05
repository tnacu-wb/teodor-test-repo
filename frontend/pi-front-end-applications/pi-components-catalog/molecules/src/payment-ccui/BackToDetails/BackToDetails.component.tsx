import { Box, Center, Flex, Text } from '@chakra-ui/react';
import { ChevronLeft } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  goBack: () => void;
  prefixDataTestId?: string;
  isPaymentsErrorPage?: boolean;
}

export default function BackToDetails({
  goBack,
  prefixDataTestId,
  isPaymentsErrorPage,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const baseTestId = formatDataTestId(prefixDataTestId, 'backToDetails');

  return (
    <Box mt="xl">
      {<Flex data-testid={`${baseTestId}_leisure`}>{renderBackToDetailsText()}</Flex>}
    </Box>
  );
  function handleClick() {
    goBack();
  }

  function renderBackToDetailsText() {
    return (
      <Flex data-testid={`${baseTestId}_back-entire`} {...backToDetailsStyle} onClick={handleClick}>
        <Center data-testid={`${baseTestId}_back-arrow`} {...chevronLeftStyle}>
          <ChevronLeft />
        </Center>
        {isPaymentsErrorPage && (
          <Text data-testid={`${baseTestId}_back-text`} {...backToDetailsTextStyle}>
            {t('ccui.paymentErrorPage.hyperlink.backToPaymentsPage')}
          </Text>
        )}
        {!isPaymentsErrorPage && (
          <Text data-testid={`${baseTestId}_back-text`} {...backToDetailsTextStyle}>
            {t('booking.submitBox.backToYourDetails')}
          </Text>
        )}
      </Flex>
    );
  }
}

const chevronLeftStyle = {
  mx: 'sm',
  h: '1.5rem',
  w: '1.5rem',
};
const backToDetailsStyle = {
  mb: 'xl',
  alignItems: 'flex-start',
  cursor: 'pointer',
};
const backToDetailsTextStyle = {
  lineHeight: '3',
  fontWeight: 'semibold',
  fontSize: 'md',
};
