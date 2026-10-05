import type { BoxProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';

import { Error, Info } from '../../assets/icons';
import Icon from '../Icon';

interface Props extends BoxProps {
  infoMessage: string;
  otherStyles?: BoxProps;
  variant?: string;
  messageId?: string;
}

export default function InfoMessage({
  infoMessage,
  otherStyles,
  variant = '',
  messageId,
}: Readonly<Props>) {
  const setIconVariant = (variant: string) => {
    if (variant === 'Info') {
      return <Info />;
    } else {
      return <Error />;
    }
  };

  const isInfoVariant = variant === 'Info';
  const liveRegionRole = isInfoVariant ? 'status' : 'alert';

  return (
    <Flex
      id={messageId}
      role={liveRegionRole}
      // role="alert" has implicit aria-live="assertive", so we override to "polite" for non-urgent errors.
      // role="status" has implicit aria-live="polite", so no override needed for Info variant.
      {...(!isInfoVariant && { 'aria-live': 'polite' })}
      direction="row"
      alignItems="center"
      position={{ base: 'relative', sm: 'absolute' }}
      {...{ ...baseStyles, ...otherStyles }}
    >
      <Icon svg={setIconVariant(variant)} {...iconStyles} />
      <Text ml="sm" mb={0}>
        {infoMessage}
      </Text>
    </Flex>
  );
}

const baseStyles = {
  bgColor: 'tooltipError',
  pl: 'sm',
  marginTop: 'md',
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
  borderRadius: '3',
  overflow: 'none',
  zIndex: '999',
  _before: {
    content: '" "',
    position: 'absolute',
    top: '-0.15rem',
    left: 'xs',
    width: '1.5rem',
    height: '0.75rem',
    zIndex: '-1',
    transform: 'rotate(-45deg)',
    bgColor: 'tooltipError',
    borderRadius: '3',
  },
};

const iconStyles = {
  mt: '-0.15rem',
  ml: 'xs',
};
