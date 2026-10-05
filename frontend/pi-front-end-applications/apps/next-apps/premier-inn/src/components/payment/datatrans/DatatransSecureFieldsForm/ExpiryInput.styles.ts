import { InputProps } from '@chakra-ui/react';

/**
 * Base styles for the expiry date <input> shell.
 * Dimensions, border tokens, and colour vars match the iframe shell fields
 * (iframeInputShellStyle) and the cardholder name ChakraInput exactly so all
 * four fields look identical in the normal state.
 */
export const expiryInputStyle = {
  h: '3.5rem',
  borderWidth: '1px',
  borderRadius: 'md',
  borderColor: 'var(--chakra-colors-lightGrey1)',
  color: 'darkGrey1',
  fontSize: 'md',
  px: 'md',
  _hover: { borderColor: 'darkGrey1' },
  _focus: { zIndex: 0, borderWidth: '2px', borderColor: 'primary' },
  _placeholder: {
    color: '#1C1C1C',
    fontFamily: 'Proxima Nova, helvetica, arial, sans-serif',
    fontWeight: '400',
    fontSize: '1rem',
    lineHeight: '150%',
  },
} as InputProps;

/**
 * Error-state variant — border switches to the design-token error colour.
 * Applied when `error` is truthy (mirrors iframeInputShellErrorStyle for the
 * Datatrans iframe fields).
 */
export const expiryInputErrorStyle = {
  ...expiryInputStyle,
  borderWidth: '2px',
  borderColor: 'error',
  errorBorderColor: 'error',
  _hover: { borderColor: 'error' },
  _focus: { zIndex: 0, borderWidth: '2px', borderColor: 'error', boxShadow: 'none' },
  _invalid: { boxShadow: 'none' },
} as InputProps;
