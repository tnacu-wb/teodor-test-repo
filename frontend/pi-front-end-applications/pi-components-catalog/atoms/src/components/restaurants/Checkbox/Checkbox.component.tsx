import { Box, Checkbox as CheckboxChakra, CheckboxProps } from '@chakra-ui/react';
import React from 'react';

interface Props extends CheckboxProps {
  variant?: string;
  children?: React.ReactNode;
  isChecked?: boolean;
  isDisabled?: boolean;
}

export default function Checkbox(props: Props) {
  return (
    <Box data-testid="box-wrapper" {...checkboxStyle(props.variant === 'border')}>
      <CheckboxChakra
        id={props.name}
        {...props}
        pos="relative"
        spacing="sm"
        alignItems="flex-start"
      >
        {props.children}
      </CheckboxChakra>
    </Box>
  );
}

const checkboxStyle = (isBorder: boolean) => ({
  border: isBorder ? `1px solid var(--chakra-colors-lightGrey4)` : 0,
  w: 'auto',
  bg: isBorder ? 'lightGrey5' : '',
  p: isBorder ? 'md' : '0',
  justifyContent: 'center',
});
