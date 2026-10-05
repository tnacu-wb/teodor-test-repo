import { Box, Button, Flex, Text, TextProps } from '@chakra-ui/react';
import { useCustomLocale, analytics } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useEffect } from 'react';

interface GroupBookingsConfirmation {
  contactName: string;
  contactEmail: string;
  caseNumber: string;
}

const GroupBookingsConfirmation = ({
  contactName,
  contactEmail,
  caseNumber,
}: GroupBookingsConfirmation) => {
  const { t } = useTranslation();
  const { language: currentLang, country: currentCountry } = useCustomLocale();

  useEffect(() => {
    analytics.update({
      pageName: 'Premier Inn: Group Form Booking Complete',
      gbf: {
        ...(window.analyticsData?.gbf ?? {}),
        caseID: caseNumber,
        validation: '',
      },
      validation: '',
    });
    window.__satelliteLoaded && window._satellite.track('groupFormSubmissionComplete');
  }, []);

  return (
    <Flex gap="5xl" flexDirection="column" data-testid="GroupBookingsConfirmation-Wrapper">
      <Box color="darkGrey1">
        <Box mb="sm" data-testid="GroupBookingsConfirmation-Title">
          <Text {...titleStyles}>
            {t('groupBooking.confirmation.title')}
            <Text {...contactNameStyles}>{contactName}</Text>
          </Text>
        </Box>
        <Box mb="xl" data-testid="GroupBookingsConfirmation-CaseNumber">
          <Text {...subTitleStyles}>
            {t('groupBooking.confirmation.caseNumber')}
            <Text {...caseNumberStyles}>{caseNumber}</Text>
          </Text>
        </Box>
        <Box mb="md" data-testid="GroupBookingsConfirmation-Email">
          <Text>
            {t('groupBooking.confirmation.email')}
            <Text {...emailStyles}>{contactEmail}</Text>
          </Text>
        </Box>
        <Box data-testid="GroupBookingsConfirmation-Content">
          <Text>{t('groupBooking.confirmation.content')}</Text>
        </Box>
      </Box>
      <Button
        size={{ base: 'full', xs: 'md' }}
        variant="secondary"
        data-testid="GroupBookingsConfirmation-BackToHome"
        onClick={() => {
          window.location.href = `${origin}/${currentCountry}/${currentLang}/home.html`;
        }}
      >
        {t('groupBooking.confirmation.backToHome')}
      </Button>
    </Flex>
  );
};

const titleStyles = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  lineHeight: '5',
} as TextProps;

const contactNameStyles = {
  ...titleStyles,
  display: 'inline',
  fontWeight: 'bold',
} as TextProps;

const subTitleStyles = {
  fontSize: 'xl',
  lineHeight: '3',
} as TextProps;

const caseNumberStyles = {
  ...subTitleStyles,
  display: 'inline',
  fontWeight: 'bold',
} as TextProps;

const emailStyles = {
  fontWeight: 'bold',
  display: 'inline',
} as TextProps;

export default GroupBookingsConfirmation;
