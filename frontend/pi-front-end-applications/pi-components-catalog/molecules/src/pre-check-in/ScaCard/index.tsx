import { Box, Flex, Text } from '@chakra-ui/react';
import { LoadingSpinner, Notification, Error, IframeEmbed } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

import { onIframeLoad } from '../common';

interface Props {
  scaErrorVisible: boolean;
  paymentLoading: boolean;
  paymentData: any;
  isSuccess: boolean;
}

const ScaCard = ({ scaErrorVisible, paymentLoading, paymentData, isSuccess }: Props) => {
  const { t } = useTranslation();

  return (
    <Box pt="lg" height="100%">
      <Box px="3xl">
        <Text
          width={{ base: '100%' }}
          fontSize="medium"
          textAlign="left"
          mb="md"
          data-testid="pre-checkin-sca-description"
        >
          {t('precheckin.SCA.description')}
        </Text>

        {scaErrorVisible && (
          <Notification
            svg={<Error />}
            status="error"
            description={t('precheckin.SCA.error')}
            variant="error"
            width={{ base: '50%', md: '75%', sm: '100%' }}
            prefixDataTestId="reg-card-sca-error"
            wrapperStyles={{ mb: 'sm', mt: 'md' }}
          />
        )}

        {paymentLoading && (
          <Flex height="450px" justifyContent="center" alignItems="center">
            <LoadingSpinner />
          </Flex>
        )}
      </Box>

      {isSuccess && (
        <Box
          height={`calc(100% - ${scaErrorVisible ? '200px' : '100px'})`}
          overflow="scroll"
          px="xl"
        >
          <IframeEmbed
            iframeId="paymentFrame"
            iframeContent={paymentData?.authorizeCard?.paymentRedirect}
            onIframeLoad={onIframeLoad}
          />
        </Box>
      )}
    </Box>
  );
};

export default ScaCard;
