import type { FlexProps } from '@chakra-ui/react';
import { ternaryCondition } from '@whitbread-eos/utils';

export function getSearchStyles(isError?: boolean | undefined, locale?: string | undefined) {
  const searchWrapper: FlexProps = {
    minW: {
      base: 'full',
      md: '45rem',
      xl: '81.75rem',
    },
    h: {
      base: 'var(--chakra-space-4xl)',
      lg: 'var(--chakra-space-6xl)',
    },
    flexWrap: {
      base: 'wrap',
      sm: 'nowrap',
    },
    borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px !important' },
    border: {
      base: 'none',
      sm: '1px solid var(--chakra-colors-lightGrey2)',
    },
    boxShadow: {
      base: 'none',
      sm: '0px 0.125rem 0.75rem var(--chakra-colors-lightGrey2)',
    },
  };

  const locationPickerStyles = {
    w: {
      base: 'full',
      lg: '25.25rem',
      xl: '26.25rem',
    },
    flex: { base: '1 1 auto', lg: 'none' },
    marginBottom: {
      sm: 0,
      xs: 'md',
      mobile: 'md',
    },
  };

  const inputGroupStyles = {
    w: 'full',
    h: {
      base: 'var(--chakra-space-4xl)',
      lg: 'var(--chakra-space-6xl)',
    },
    border: {
      base: '1px solid var(--chakra-colors-lightGrey1)',
      sm: 0,
    },
    borderRadius: {
      base: 'var(--chakra-space-xs)',
      sm: 0,
    },
  };

  const inputElementStyles = {
    borderRight: '1px solid var(--chakra-colors-lightGrey4)',
    borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px 0 0 50px' },
    color: 'darkGrey1',
    _hover: {
      border: '1px solid var(--chakra-colors-darkGrey1)',
      borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px 0 0 50px' },
    },
    _focus: {
      border: '2px solid var(--chakra-colors-primary)',
      cursor: 'auto',
      borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px 0 0 50px' },
    },
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey2)',
      borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px 0 0 50px' },
      fontStyle: 'normal',
    },
  };

  const bookingDatepickerSize = {
    w: {
      base: '49%',
      sm: 'full',
      lg: '19.5rem',
      xl: '19.781rem',
    },
  };

  const datepickerInputElementStyles = {
    h: '100%',
    flex: { base: '1 1 50%', sm: '1 1 auto' },
    mr: { base: 'xs', xs: 'sm', sm: '0' },
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey1)',
    },
  };

  const iconStyles = {
    top: {
      base: 'var(--chakra-space-sm)',
      lg: 'var(--chakra-space-md)',
    },
  };

  const errorMarginBottom = {
    mobile: ternaryCondition(isError, locale === 'de' ? '5rem' : 'sm', 'sm'),
    xs: ternaryCondition(isError, 'md', 'md'),
    sm: '0',
  };

  const errorInputGroupStyles = {
    ...inputGroupStyles,
    ...(isError
      ? {
          borderRadius: 'var(--chakra-radii-base)',
          border: '2px solid var(--chakra-colors-error)',
        }
      : {}),
  };

  const locationErrorGroupStyles = {
    ...(isError
      ? {
          borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px 0 0 50px' },
          border: '2px solid var(--chakra-colors-error)',
        }
      : {}),
  };

  const errorInputElementStyles = {
    ...inputElementStyles,
    ...(isError
      ? {
          border: 'none',
          borderRight: 'none',
          _hover: {
            border: 'none',
          },
          _focus: {
            border: 'none',
          },
          _active: {
            border: 'none',
          },
        }
      : {}),
  };

  const buttonStyles = {
    w: { base: 'full', sm: '3xl', md: '8.875rem', lg: '11.75rem' },
    h: {
      xs: 'var(--chakra-space-4xl)',
      sm: 'var(--chakra-space-2xl)',
      lg: 'var(--chakra-space-4xl)',
    },
    ml: {
      sm: 'sm',
      lg: '0.8rem',
      xl: '3.5631rem',
    },
    mr: {
      sm: 'sm',
    },
    flex: { sm: '1 1 20%', md: '1 1 40%', lg: 'none' },
    borderRadius: { mobile: '0', sm: '50px' },
  };

  const roomPickerSize = { w: { base: '49%', sm: 'full', lg: '18.563rem', xl: '19.781rem' } };

  const roomPickerWrapperStyles = {
    mt: 0,
    h: {
      base: 'var(--chakra-space-4xl)',
      lg: 'var(--chakra-space-6xl)',
    },
  };

  const roomPickerInputElementStyles = {
    w: '100%',
    h: '100%',
    flex: { base: '1 1 50%', sm: '1 1 auto' },
    ml: {
      base: 'xs',
      xs: 'sm',
      sm: '0',
    },
    border: {
      base: '1px solid var(--chakra-colors-lightGrey1)',
      sm: 'none',
    },
    _placeholder: {
      color: 'var(--chakra-colors-darkGrey1)',
    },
  };

  return {
    searchWrapper,
    locationPickerStyles,
    locationErrorGroupStyles,
    inputGroupStyles,
    inputElementStyles,
    iconStyles,
    bookingDatepickerSize,
    datepickerInputElementStyles,
    errorInputGroupStyles,
    errorInputElementStyles,
    errorMarginBottom,
    buttonStyles,
    roomPickerSize,
    roomPickerWrapperStyles,
    roomPickerInputElementStyles,
  };
}
export function getSearchPriceFinderStyles() {
  const searchWrapper: FlexProps = {
    width: 'full',
    maxW: '1228px',
    h: {
      base: '56px',
      md: '72px',
    },
    flexWrap: {
      base: 'nowrap',
    },
    borderRadius: { mobile: 'var(--chakra-space-xs)', sm: '50px' },
    border: {
      base: '1px solid var(--chakra-colors-lightGrey2)',
    },
    justifyContent: 'space-between',
    position: 'relative',
  };

  const locationPickerStyles = {
    w: {
      base: 'full',
    },
  };

  const buttonStyles = {
    position: 'absolute',
    right: '8px',
    w: {
      base: '56px',
      md: '188px',
    },
    h: {
      base: '40px',
      md: '56px',
    },
    borderRadius: { mobile: '0', sm: '50px' },
  };

  const inputGroupStyles = {
    w: 'full',
    h: {
      base: '56px',
      md: '72px',
    },
    borderRadius: {
      base: 'var(--chakra-space-xs)',
      sm: '0',
    },
  };

  return {
    searchWrapper,
    locationPickerStyles,
    inputGroupStyles,
    buttonStyles,
  };
}
