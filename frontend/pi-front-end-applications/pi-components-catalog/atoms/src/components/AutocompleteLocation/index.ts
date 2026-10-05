import type { BoxProps, InputProps, PopoverContentProps } from '@chakra-ui/react';

import AutocompleteLocation from './AutocompleteLocation.component';

export default AutocompleteLocation;

export interface AutocompleteStyleProps {
  errorInputGroupStyles?: BoxProps;
  errorInputElementStyles?: BoxProps;
  errorMarginBottom?: { sm: string; xs: string; mobile: string };
  alertStyles?: BoxProps;
  locationPickerStyles?: BoxProps;
  wrapperStyles?: BoxProps;
  inputStyles?: InputProps;
  inputGroupStyles?: BoxProps;
  inputElementStyles?: BoxProps;
  listStyles?: PopoverContentProps;
  locationErrorGroupStyles?: BoxProps;
}
