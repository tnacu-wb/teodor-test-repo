import { Box, Text } from '@chakra-ui/react';
import { useSemanticTypography } from '@whitbread-eos/utils';

import { FormErrorProps } from '../formTypes';

export default function FormError({ errors, name, extraStyles }: Readonly<FormErrorProps>) {
  const getTypographyProps = useSemanticTypography();
  if (errors?.[name]?.message?.length) {
    const errorTypographyProps = getTypographyProps(
      { fontSize: 'xs' },
      { textStyle: 'body-s-regular' }
    );
    const textStyles = {
      ...errorTypographyProps,
      color: 'error',
      marginLeft: 'md',
      marginTop: 'sm',
      whiteSpace: 'nowrap',
    } as const;
    return (
      <Box {...{ ...formErrorStyles, ...extraStyles?.containerStyle }}>
        <Box {...errorMessageStyles}>
          <Text {...{ ...textStyles, ...extraStyles?.textStyle }}>{errors?.[name]?.message}</Text>
        </Box>
      </Box>
    );
  }
  return null;
}

const formErrorStyles = {
  height: 'var(--chakra-space-lg)',
  position: 'relative',
} as const;
const errorMessageStyles = {
  position: 'absolute',
} as const;
