import { Box, Button, Flex, FlexProps, Text } from '@chakra-ui/react';
import { NewsletterSignup } from '@whitbread-eos/api';
import { renderSanitizedHtml } from '@whitbread-eos/utils';
import React, { useMemo } from 'react';

interface NewsletterData {
  newsletterSignup: NewsletterSignup;
}
interface Props {
  data: NewsletterData;
  routerPush: any;
}

export default function Newsletter({ data, routerPush }: Readonly<Props>) {
  const hasNewsLetter = useMemo(() => {
    const { newsletterSignup } = data;
    return hasNewsLetterSignupContent(newsletterSignup);
  }, [data]);

  if (!hasNewsLetter) {
    return null;
  }

  const { introViewTitle, introViewText, signUpButtonText } = data.newsletterSignup;

  return (
    <Flex {...newsletterWrapperStyle}>
      <Box p="lg">
        <Flex direction="column">
          <Text {...newsletterTitleStyle} data-testid={'newsletter-title'}>
            {introViewTitle}
          </Text>
          <Box {...newsletterDescriptionStyle} data-testid={'newsletter-description'}>
            {renderSanitizedHtml(introViewText)}
          </Box>
          <Button
            {...newsletterButtonStyle}
            data-testid={'newsletter-link'}
            onClick={() => {
              routerPush('https://www.premierinn.com/de/de/presse.html');
            }}
          >
            {signUpButtonText}
          </Button>
        </Flex>
      </Box>
    </Flex>
  );
}

function hasNewsLetterSignupContent(newsletterSignup: NewsletterSignup) {
  if (!newsletterSignup) {
    return false;
  }
  return Object.values(newsletterSignup).every((value) => {
    if (value !== null) {
      return true;
    }

    return false;
  });
}

const newsletterWrapperStyle = {
  direction: 'column',
  w: 'full',
  bgColor: 'lightPurple',
} as FlexProps;

const newsletterTitleStyle = {
  fontSize: 'md',
  color: 'baseWhite',
  fontWeight: 'medium',
};

const newsletterDescriptionStyle = {
  fontSize: 'sm',
  color: 'baseWhite',
  fontWeight: 'normal',
};

const newsletterButtonStyle = {
  size: 'full',
  variant: 'tertiary',
  mt: 'lg',
};
