import { Box, FlexProps, Radio, RadioProps, ResponsiveValue } from '@chakra-ui/react';
import { ListIndex } from '@whitbread-eos/api';
import { ReactElement } from 'react';

interface Props extends RadioProps {
  isChecked?: boolean;
  children?: ReactElement;
  listIndex?: ListIndex;
  type?: string;
  variant?: 'borderless';
  width?: string;
  padding?: ResponsiveValue<string | number>;
  flexDirection?: FlexProps['flexDirection'];
  alignItems?: FlexProps['alignItems'];
  withOutline?: boolean;
  borderColorChecked?: string;
  backgroundColorChecked?: string;
}

function getBorderRadius(listIndex: ListIndex) {
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

  return 0;
}

const radioButtonStyle = (
  isBorderless: boolean,
  isChecked: boolean | undefined,
  listIndex: ListIndex,
  padding?: ResponsiveValue<string | number>,
  borderColorChecked?: string,
  backgroundColorChecked?: string,
  withOutline = false
) => {
  if (withOutline) {
    return {
      outline: isChecked ? '4px solid var(--chakra-colors-primary)' : '',
      outlineOffset: '-4px',
      borderWidth: getBorderWidth(isBorderless, false),
      borderColor: 'lightGrey1',
      borderRadius: getBorderRadius(listIndex),
      p: isBorderless || padding ? '0' : 'md',
    };
  } else
    return {
      borderWidth: getBorderWidth(isBorderless, isChecked),
      borderBottomWidth: getBorderBottomWidth(isBorderless, isChecked, listIndex),
      borderStyle: 'solid',
      borderColor: isChecked ? (borderColorChecked ?? 'primary') : 'lightGrey1',
      backgroundColor: isChecked ? (backgroundColorChecked ?? 'transparent') : 'transparent',
      borderRadius: getBorderRadius(listIndex),
      p: isBorderless || padding ? '0' : 'md',
    };
};

export default function RadioButton(props: Readonly<Props>) {
  return (
    <Box
      data-testid={props.type ? `radio-box-wrapper_${props.type}` : 'radio-box-wrapper'}
      {...radioButtonStyle(
        props.variant === 'borderless',
        props.isChecked,
        props.listIndex,
        props.padding,
        props.borderColorChecked,
        props.backgroundColorChecked,
        props.withOutline ?? false
      )}
      width={props.width}
    >
      <Radio
        data-testid={props.type ? `radio-box-inside_${props.type}` : 'radio-box-inside'}
        alignItems="flex-start"
        width="full"
        {...props}
      >
        {props.children}
      </Radio>
    </Box>
  );
}
