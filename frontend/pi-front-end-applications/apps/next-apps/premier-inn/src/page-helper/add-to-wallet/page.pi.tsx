import { Box } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { FT_PI_ADD_TO_WALLET } from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import {
  isIOSDevice,
  decryptCryptString,
  useFeatureToggle,
  formatReservationNumberInQuery,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import type { NextRouter } from 'next/router';
import { useEffect, useState } from 'react';

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
}

export function PIPageContent({ router }: Readonly<Props>) {
  const [linkExpired, setLinkExpired] = useState(false);
  const [supportedDevice, setSupportedDevice] = useState(false);
  const [decryptedParams, setDecryptedParams] = useState<string>('');
  const [decodedReferrer, setDecodedReferrer] = useState<string>('');
  const { referrer } = router.query;
  const { t } = useTranslation(['common']);

  const { [FT_PI_ADD_TO_WALLET]: isAddToWalletEnabled } = useFeatureToggle();

  useEffect(() => {
    if (!router.isReady || !referrer) return;
    const fixedReferrer = (referrer as string).replaceAll(/ /g, '+');
    const decodedReferrerValue = decodeURIComponent(fixedReferrer);
    setDecodedReferrer(decodedReferrerValue);
    const data = decryptCryptString(
      decodedReferrerValue,
      process.env.NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION!
    );

    setDecryptedParams(data);
  }, [router.isReady, referrer]);

  useEffect(() => {
    if (!isAddToWalletEnabled) return;
    const cleanedParams = formatReservationNumberInQuery(decryptedParams);
    const params = new URLSearchParams(cleanedParams);
    const arrivalDate = params.get('arrivalDate');

    setSupportedDevice(!isIOSDevice());

    if (isIOSDevice() && arrivalDate) {
      if (isValidArrivalDate(arrivalDate)) {
        router.push('/api/addtowallet/apple?referrer=' + encodeURIComponent(decodedReferrer));
        setSupportedDevice(false);
      } else {
        setLinkExpired(true);
      }
    }
  }, [decryptedParams, decodedReferrer, router, isAddToWalletEnabled]);

  return (
    <>
      {isAddToWalletEnabled && (
        <Box {...styles.container}>
          {(linkExpired || supportedDevice) && (
            <Notification
              maxWidth="full"
              variant="error"
              status="warning"
              description={
                linkExpired
                  ? t('error.wallet.add.link.expired')
                  : t('error.wallet.add.device.notSupported')
              }
              svg={<Info />}
            />
          )}
        </Box>
      )}
    </>
  );
}

export default function AddToWalletPagePi({ queryClient, router }: Readonly<Props>) {
  return (
    <QueryClientProvider client={queryClient}>
      <PIPageContent queryClient={queryClient} router={router} />
    </QueryClientProvider>
  );
}

export const isValidArrivalDate = (arrivalDate: string) => {
  const today = new Date();
  const checkin = new Date(arrivalDate);

  today.setHours(0, 0, 0, 0);
  checkin.setHours(0, 0, 0, 0);

  const diffInDays = Math.floor((checkin.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));

  return diffInDays === 1 || diffInDays === 0;
};

const styles = {
  container: {
    width: '100vw',
    px: {
      mobile: 4,
      md: 4,
      lg: 0,
    },
    py: {
      mobile: 4,
    },
    maxW: {
      lg: '1226px',
    },
    position: 'relative',
  } as const,
};
