import { Box, BoxProps, Flex, FlexProps, Image, Link, StyleProps, Text } from '@chakra-ui/react';
import { type AuthenticationLabels } from '@whitbread-eos/api';
import { formatAssetsUrl, formatDataTestId, useCustomLocale } from '@whitbread-eos/utils';

export interface Props {
  labels: AuthenticationLabels;
  styles?: BoxProps;
}

export default function LoginFormFooter({ labels, styles }: Readonly<Props>) {
  const { country } = useCustomLocale();
  const baseDataTestId = 'businessBookerLoginFooter';
  return (
    <Flex
      data-testid={formatDataTestId(baseDataTestId, 'businessAccountCardContainer')}
      {...containerStyle}
    >
      {country === 'gb' && (
        <>
          <Box
            position="relative"
            {...styles}
            data-testid={formatDataTestId(baseDataTestId, 'businessAccountCardImg')}
          >
            <Image
              src={formatAssetsUrl(labels?.login?.businessAccountCard?.banner?.imagePath)}
              objectFit="fill"
              maxWidth="64px"
            />
          </Box>
          <Text pl="2" data-testid={formatDataTestId(baseDataTestId, 'businessAccountCardText')}>
            {labels?.login?.businessAccountCard?.textBody}
          </Text>
          <Link
            data-testid={formatDataTestId(baseDataTestId, 'logInSignUpLink')}
            href={labels?.businessAccountCardRedirectPath}
            {...linkStyles}
          >
            {labels?.login?.businessAccountCard?.buttonLabel}
          </Link>
        </>
      )}
    </Flex>
  );
}

const linkStyles = {
  display: 'block',
  fontStyle: 'normal',
  fontWeight: 'normal',
  fontSize: 'md',
  textDecoration: 'underline',
  lineHeight: 'var(--chakra-lineHeights-3)',
  pl: 'sm',
  color: 'btnSecondaryEnabled',
} as StyleProps;

const containerStyle = {
  minHeight: '5rem',
  align: 'center',
  justify: 'center',
  paddingX: '1rem',
  paddingY: '1.75rem',
  backgroundColor: 'lightGrey5',
} as FlexProps;
