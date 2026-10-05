import { Box, Radio, RadioProps } from '@chakra-ui/react';
import { ListIndex } from '@whitbread-eos/api';
import { ReactElement } from 'react';

interface Props extends RadioProps {
  isChecked?: boolean;
  children?: ReactElement;
  listIndex?: ListIndex;
  type?: string;
  variant?: 'borderless';
  width?: string;
  maximumWidth?: string;
}

function getBorderRadiusHorizontal(listIndex: ListIndex) {
  switch (listIndex) {
    case 0:
      return 'var(--chakra-space-1) 0 0 var(--chakra-space-1)';
    case 'last':
      return '0  var(--chakra-space-1) var(--chakra-space-1) 0';
    case 'left':
      return 'var(--chakra-space-1) 0 0 var(--chakra-space-1)';
    case undefined:
      return '4';
    default:
      return '0';
  }
}

function getBorderRadiusVertical(listIndex: ListIndex) {
  switch (listIndex) {
    case 0:
      return 'var(--chakra-space-1) var(--chakra-space-1) 0 0';
    case 'last':
      return '0 0 var(--chakra-space-1) var(--chakra-space-1)';
    case 'left':
      return 'var(--chakra-space-1) 0 0 var(--chakra-space-1)';
    case undefined:
      return '4';
    default:
      return '0';
  }
}

function getBorderWidth(isBorderless: boolean, isChecked: boolean | undefined) {
  if (isBorderless) {
    return 0;
  }

  if (isChecked) {
    return '2px';
  }

  return '1px';
}
function getBorderBottomWidth(
  isBorderless: boolean,
  isChecked: boolean | undefined,
  listIndex: ListIndex
) {
  if (isBorderless) {
    return 0;
  }

  if (isChecked) {
    return '2px';
  }

  if ([undefined, 0, 'last', 'left'].includes(listIndex)) {
    return '1px';
  }

  return '1px';
}

const radioButtonStyle = (
  isBorderless: boolean,
  isChecked: boolean | undefined,
  listIndex: ListIndex
) => ({
  borderWidth: getBorderWidth(isBorderless, isChecked),
  borderBottomWidth: { base: getBorderBottomWidth(isBorderless, isChecked, listIndex) },
  borderStyle: 'solid',
  borderColor: isChecked ? 'primary' : 'lightGrey3',
  borderRadius: {
    base: getBorderRadiusVertical(listIndex),
    md: getBorderRadiusHorizontal(listIndex),
  },
  p: isBorderless ? '0' : 'md',
  background: isChecked ? 'lightGrey5' : 'baseWhite',
});

export default function PaymentRadioButton(props: Readonly<Props>) {
  return (
    <Box
      data-testid={props.type ? `radio-box-wrapper_${props.type}` : 'radio-box-wrapper'}
      {...radioButtonStyle(props.variant === 'borderless', props.isChecked, props.listIndex)}
      w="full"
      maxW={{ base: '100%', lg: props.maximumWidth }}
    >
      <Radio
        data-testid={props.type ? `radio-box-inside_${props.type}` : 'radio-box-inside'}
        alignItems="flex-start"
        width="full"
        variant="payment"
        {...props}
      >
        {props.children}
      </Radio>
    </Box>
  );
}
