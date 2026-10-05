import { Flex, FlexProps, Text, Box } from '@chakra-ui/react';
import { ChevronLeft } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import PageLoader from '../../amend/PageLoader';

const BackButton = () => {
  const { t } = useTranslation();
  const { query, events, back } = useRouter();
  const [pageLoaderStatus, setPageLoaderStatus] = useState<boolean>(false);
  const queryLength = Object.keys(query)?.length;

  const handleBackButtonClick = () => {
    back();
  };

  useEffect(() => {
    const handleRouteChangeStart = () => {
      setPageLoaderStatus(true);
    };

    const handleRouteChangeComplete = () => {
      setPageLoaderStatus(false);
    };

    events.on('routeChangeStart', handleRouteChangeStart);
    events.on('routeChangeComplete', handleRouteChangeComplete);
    events.on('routeChangeError', handleRouteChangeComplete);

    return () => {
      events.off('routeChangeStart', handleRouteChangeStart);
      events.off('routeChangeComplete', handleRouteChangeComplete);
      events.off('routeChangeError', handleRouteChangeComplete);
    };
  }, [events]);

  return (
    <>
      {pageLoaderStatus && <PageLoader text={t('searchresults.list.hotel.loading')} />}
      {queryLength > 0 && (
        <Box w="fit-content" mb={'lg'} onClick={handleBackButtonClick}>
          <Flex {...backBtnStyles}>
            <ChevronLeft />
            <Text data-testid="reg-form-back-btn">{t('precheckin.backbutton')}</Text>
          </Flex>
        </Box>
      )}
    </>
  );
};

export default BackButton;

const backBtnStyles = {
  cursor: 'pointer',
  alignItems: 'center',
  gap: '3',
  w: 'fit-content',
} as FlexProps;
