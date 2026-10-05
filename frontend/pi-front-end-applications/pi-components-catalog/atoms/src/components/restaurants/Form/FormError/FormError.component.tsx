import { Box, BoxProps, Text } from '@chakra-ui/react';

import { FormErrorProps } from '../formTypes';

export default function FormError({ errors, name }: FormErrorProps) {
  return errors?.[name]?.message?.length ? (
    <Box {...{ ...formErrorStyles }}>
      <Box {...errorMessageStyles}>
        <Text {...{ ...textStyles }}>{errors?.[name]?.message}</Text>
      </Box>
    </Box>
  ) : null;
}

const formErrorStyles = {
  height: 'var(--chakra-space-lg)',
  position: 'relative',
} as BoxProps;

const errorMessageStyles = {
  position: 'absolute',
} as BoxProps;

const textStyles = {
  color: 'error',
  fontSize: 'xs',
  px: 'sm',
  marginTop: 'sm',
  whiteSpace: 'nowrap',
} as BoxProps;
