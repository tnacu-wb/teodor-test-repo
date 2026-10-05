import { StyleProps } from '@chakra-ui/react';

export const resultRowCellRedesignStyle = (isExpanded: boolean) => {
  return {
    backgroundColor: isExpanded ? 'lightGrey5' : 'transparent',
    borderBottom: isExpanded ? 'none' : 'var(--chakra-borders-1px)',
    borderColor: 'var(--chakra-colors-lightGrey2)',
    whiteSpace: 'normal',
    fontWeight: '600',
    fontSize: {
      lg: 'md',
      md: 'md',
      mobile: 'sm',
    },
    color: 'var(--chakra-colors-darkGrey1)',
    lineHeight: {
      lg: '120%',
      sm: '3',
      mobile: '2',
    },
    padding: {
      mobile: 'var(--chakra-space-3) var(--chakra-space-4)',
      lg: 'var(--chakra-space-6) var(--chakra-space-3) var(--chakra-space-6)',
    },
    verticalAlign: 'top',
    _hover: {
      cursor: 'pointer',
    },
    sx: {
      '& .booking-reference': {
        fontWeight: '400',
        color: 'var(--chakra-colors-darkGrey2)',
        lineHeight: '140%',
      },
      '& .hotel-name': {
        fontWeight: '700',
        color: 'var(--chakra-colors-darkGrey3)',
        lineHeight: '120%',
        fontSize: {
          mobile: 'md',
          lg: 'lg',
        },
      },
      '& .nights': {
        fontWeight: '400',
        color: 'var(--chakra-colors-darkGrey2)',
        lineHeight: '140%',
        fontSize: {
          mobile: 'xxs',
          md: 'xs',
          lg: 'sm',
        },
      },
      '& .booking-status': {
        fontWeight: '600',
        color: 'var(--chakra-colors-darkGrey1)',
        lineHeight: '140%',
        whiteSpace: 'nowrap',
        fontSize: {
          mobile: '11px',
          lg: '13px',
        },
        borderRadius: '16px',
        padding: '2px 8px',
        '&--checked_in': { color: '#333333', backgroundColor: '#FEEFD9' },
        '&--future': { color: '#1C8754', backgroundColor: '#E5F2F6' },
        '&--past': { color: '#58595B', backgroundColor: '#DDDDDD' },
        '&--cancelled': { color: '#D90941', backgroundColor: '#FBE6EC' },
      },
    },
  };
};

export const resultRowCellStyle = (isExpanded: boolean): StyleProps => {
  return {
    backgroundColor: isExpanded ? 'lightGrey5' : 'transparent',
    borderBottom: isExpanded ? 'none' : 'var(--chakra-borders-1px)',
    borderColor: 'var(--chakra-colors-lightGrey2)',
    whiteSpace: 'normal',
    fontSize: {
      lg: 'lg',
      md: 'md',
      mobile: 'sm',
    },
    lineHeight: {
      sm: '3',
      mobile: '2',
    },
    padding: {
      xl: 'var(--chakra-space-12) var(--chakra-space-5) var(--chakra-space-6)',
      lg: 'var(--chakra-space-12) var(--chakra-space-4) var(--chakra-space-6)',
      md: 'var(--chakra-space-12) var(--chakra-space-4) var(--chakra-space-6)',
      sm: 'var(--chakra-space-12) var(--chakra-space-1) var(--chakra-space-6)',
      xs: 'var(--chakra-space-8) var(--chakra-space-1) var(--chakra-space-6)',
      mobile: 'var(--chakra-space-10) var(--chakra-space-1) var(--chakra-space-6)',
    },
    verticalAlign: 'top',
  };
};

export const resultContainerStyle = {
  mt: '3xl',
} as StyleProps;

export const tableStyle = (isFixedLayout: boolean) => {
  return {
    tableLayout: isFixedLayout ? 'fixed' : 'auto',
  } as React.CSSProperties;
};

export const headerTableStyle = {
  backgroundColor: 'lightGrey5',
} as StyleProps;

export const headerResultsTextStyle = {
  fontWeight: '500',
  fontSize: {
    lg: 'lg',
    md: 'md',
    mobile: 'sm',
  },
  lineHeight: {
    sm: '3',
    mobile: '2',
  },
  color: 'baseBlack',
  textTransform: 'none',
  fontFamily: 'header',
  letterSpacing: 'initial',
  padding: {
    xl: 'var(--chakra-space-10) var(--chakra-space-5) var(--chakra-space-4)',
    lg: 'var(--chakra-space-6) var(--chakra-space-4) var(--chakra-space-4)',
    md: 'var(--chakra-space-6) var(--chakra-space-4) var(--chakra-space-4)',
    sm: 'var(--chakra-space-6) var(--chakra-space-1) var(--chakra-space-4)',
    xs: 'var(--chakra-space-6) var(--chakra-space-1) var(--chakra-space-4)',
    mobile: 'var(--chakra-space-6) var(--chakra-space-1) var(--chakra-space-4)',
  },
  whiteSpace: 'normal',
  borderBottom: 'var(--chakra-borders-1px)',
  borderColor: 'var(--chakra-colors-lightGrey2)',
  verticalAlign: 'top',
} as StyleProps;

export const headerResultsTextRedesignStyle = {
  fontWeight: '600',
  fontSize: {
    mobile: 'xs',
    lg: 'sm',
  },
  lineHeight: '120%',
  color: 'var(--chakra-colors-darkGrey2)',
  textTransform: 'none',
  fontFamily: 'Proxima Nova Sans',
  letterSpacing: 'initial',
  padding: {
    mobile: 'var(--chakra-space-3) var(--chakra-space-4)',
    lg: 'var(--chakra-space-4) var(--chakra-space-3) var(--chakra-space-4)',
  },
  whiteSpace: 'normal',
  borderBottom: 'var(--chakra-borders-1px)',
  borderColor: 'var(--chakra-colors-lightGrey2)',
  verticalAlign: 'top',
} as StyleProps;

export const linkStyles = {
  display: 'flex',
  alignItems: 'center',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
  fontSize: {
    md: 'md',
    mobile: 'sm',
  },
} as StyleProps;

export const loadMoreStyles = {
  w: '260px',
  margin: {
    xl: 'var(--chakra-space-12) auto var(--chakra-space-8)',
    lg: 'var(--chakra-space-12) auto var(--chakra-space-8)',
    md: 'var(--chakra-space-12) auto var(--chakra-space-8)',
    sm: 'var(--chakra-space-12) auto var(--chakra-space-8)',
    xs: 'var(--chakra-space-8) auto var(--chakra-space-8)',
    mobile: 'var(--chakra-space-8) auto var(--chakra-space-8)',
  },
} as StyleProps;

export const cardWrapperStyle = {
  backgroundColor: 'white',
  padding: {
    mobile: 'var(--chakra-space-4)',
    lg: 'var(--chakra-space-6)',
  },
  border: '1px solid var(--chakra-colors-lightGrey3)',
  borderRadius: '8px',
} as StyleProps;

export const cardRowStyle = {
  backgroundColor: 'lightGrey5',
  padding: {
    mobile: '0 var(--chakra-space-2) var(--chakra-space-2)',
    lg: '0 var(--chakra-space-3) var(--chakra-space-4)',
  },
} as StyleProps;
