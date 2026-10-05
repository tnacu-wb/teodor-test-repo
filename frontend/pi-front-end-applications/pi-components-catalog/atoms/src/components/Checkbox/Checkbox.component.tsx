import { Box, BoxProps, Checkbox as CheckboxChakra, CheckboxProps } from '@chakra-ui/react';
import * as React from 'react';

interface Props extends CheckboxProps {
  showIcon?: boolean;
  area?: string;
  variant?: string;
  children?: React.ReactNode;
  isChecked?: boolean;
  isDisabled?: boolean;
  checkboxWrapperStyles?: BoxProps;
}

export default function Checkbox(props: Readonly<Props>) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { showIcon, area, ...rest } = props; // NOSONAR

  return (
    <Box
      data-testid="box-wrapper"
      {...checkboxStyle(props.variant === 'border')}
      {...props.checkboxWrapperStyles}
    >
      <CheckboxChakra spacing="sm" alignItems="flex-start" {...rest}>
        {props.children}
      </CheckboxChakra>
    </Box>
  );
}

const checkboxStyle = (isBorder: boolean) => ({
  border: isBorder ? `1px solid var(--chakra-colors-lightGrey4)` : 0,
  w: '100%',
  bg: isBorder ? 'lightGrey5' : '',
  p: isBorder ? 'md' : '0',
  justifyContent: 'center',
  my: 'sm',
});
