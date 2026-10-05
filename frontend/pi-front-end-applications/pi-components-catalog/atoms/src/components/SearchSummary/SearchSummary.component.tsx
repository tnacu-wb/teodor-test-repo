import { Box, BoxProps, Divider, Text, TextProps } from '@chakra-ui/react';
import { useMemo } from 'react';

import { SearchIcon } from '../../assets/icons';
import Icon from '../../components/Icon';

export interface SearchSummaryProps {
  handleEdit: () => void;
  isSummaryActive: boolean;
  location: string;
  dateInterval: string;
  roomSummary: string;
  numberOfNightsSummary?: string;
  promotionCategorySummary?: string;
  contractRateSummary?: string;
  isLessThanSm: boolean;
  isLessThanMd: boolean;
  isLessThanLg: boolean;
  editText: string;
  style?: {
    containerStyle?: BoxProps;
  };
}

interface HandleSummaryInfoBoxMobileParams {
  dateInterval: string;
  roomSummary: string;
  numberOfNightsSummary: string | undefined;
  promotionCategorySummary: string | undefined;
  contractRateSummary: string | undefined;
  isLessThanLg: boolean;
  handleEdit: () => void;
  editText: string;
}

export default function SearchSummary({
  handleEdit,
  isSummaryActive,
  location,
  dateInterval,
  roomSummary,
  numberOfNightsSummary,
  promotionCategorySummary,
  contractRateSummary,
  editText,
  isLessThanMd,
  isLessThanSm,
  isLessThanLg,
  style,
}: Readonly<SearchSummaryProps>) {
  const showCanEdit = useMemo(() => isLessThanMd || isLessThanSm, [isLessThanMd, isLessThanSm]);

  const hoverObject = showCanEdit ? { _hover: {} } : {};

  return isSummaryActive ? (
    <>
      <Box
        role={!showCanEdit ? 'group' : ''}
        data-testid="search-summary-container"
        {...styledSearchSummaryContainer}
        {...hoverObject}
        {...style?.containerStyle}
        onClick={(e) => {
          e.preventDefault();
          if (!showCanEdit) {
            handleEdit();
          }
        }}
      >
        <Box {...styledSearchIcon}>
          <Icon svg={<SearchIcon data-testid="search-icon" />} />
        </Box>
        {location && (
          <Box {...styledSummaryInfoBox} {...styledLocation}>
            <Text {...styledText} data-testid="search-summary-location">
              {location}
            </Text>
            {(isLessThanSm || (isLessThanLg && !roomSummary)) && (
              <Text
                {...styledEditText}
                data-testid="search-summary-edit-link"
                onClick={(e) => {
                  e.preventDefault();
                  handleEdit();
                }}
              >
                {editText}
              </Text>
            )}
          </Box>
        )}
        {!isLessThanSm
          ? handleSummaryInfoBoxMobile({
              dateInterval,
              roomSummary,
              numberOfNightsSummary,
              promotionCategorySummary,
              contractRateSummary,
              isLessThanLg,
              handleEdit,
              editText,
            })
          : dateInterval &&
            roomSummary && (
              <Box
                {...{ ...styledSummaryInfoBox, ...styledSummaryInfoBoxMobile }}
                data-testid="search-summary-date-rooms"
              >
                {`${dateInterval}, ${roomSummary}`}
              </Box>
            )}
      </Box>
      <Divider marginTop="-1px" />
    </>
  ) : null;
}

function handleSummaryInfoBoxMobile({
  dateInterval,
  roomSummary,
  numberOfNightsSummary,
  promotionCategorySummary,
  contractRateSummary,
  isLessThanLg,
  handleEdit,
  editText,
}: HandleSummaryInfoBoxMobileParams) {
  return (
    <>
      {dateInterval && (
        <Box
          data-testid="search-summary-dates"
          {...styledSummaryInfoBox}
          {...styledSummaryInfoBoxMobile}
        >
          <Text {...styledText}>{dateInterval}</Text>
        </Box>
      )}
      {numberOfNightsSummary && (
        <Box
          data-testid="search-summary-number-of-nights"
          {...styledSummaryInfoBox}
          {...styledSummaryInfoBoxMobile}
        >
          <Text {...styledText}>{numberOfNightsSummary}</Text>
        </Box>
      )}
      {roomSummary && (
        <Box
          data-testid="search-summary-rooms"
          {...styledSummaryInfoBox}
          {...styledSummaryInfoBoxMobile}
        >
          <Text {...styledText}>{roomSummary}</Text>
          {isLessThanLg && (
            <Text
              {...styledEditText}
              onClick={(e) => {
                e.preventDefault();
                handleEdit();
              }}
            >
              {editText}
            </Text>
          )}
        </Box>
      )}
      {promotionCategorySummary && (
        <Box
          data-testid="search-summary-promotion-category"
          {...styledSummaryInfoBox}
          {...styledSummaryInfoBoxMobile}
        >
          <Text {...styledText} {...styledTextInactive}>
            {promotionCategorySummary}
          </Text>
        </Box>
      )}
      {contractRateSummary && (
        <Box
          data-testid="search-summary-contract-rate"
          {...styledSummaryInfoBox}
          {...styledSummaryInfoBoxMobile}
        >
          <Text {...styledText}>{contractRateSummary}</Text>
        </Box>
      )}
    </>
  );
}

const styledSearchSummaryContainer = {
  width: '100%',
  display: 'flex',
  flexDirection: {
    mobile: 'initial',
    sm: 'row',
  },
  flexWrap: {
    mobile: 'wrap',
    sm: 'initial',
  },
  padding: {
    lg: '1.5rem 0',
    md: 'var(--chakra-space-md) 0',
    mobile: '1rem 0',
  },
  alignItems: 'center',
  border: '1px solid transparent',
  cursor: 'pointer',
  height: {
    sm: 'var(--chakra-space-4xl)',
    md: 'var(--chakra-space-6xl)',
  },
  _hover: {
    boxShadow: '0px 0px 8px var(--chakra-colors-lightGrey4)',
    padding: '0',
    border: '1px solid var(--chakra-colors-lightGrey4)',
  },
} as BoxProps;
const styledSearchIcon = {
  display: 'flex',
  paddingLeft: {
    sm: 'var(--chakra-space-md)',
    mobile: '0',
  },
  _groupHover: {
    padding: {
      lg: 'var(--chakra-space-lg) 0 var(--chakra-space-lg) var(--chakra-space-md)',
      md: 'var(--chakra-space-md) 0 var(--chakra-space-md) var(--chakra-space-md)',
    },
  },
};

const styledLocation = {
  fontWeight: {
    mobile: 'semibold',
    sm: 'normal',
  },
  flex: {
    sm: 'initial',
    mobile: '1 1 calc(100% - 37px)',
  },
  _after: {
    content: '""',
  },
};
const styledSummaryInfoBox = {
  display: 'flex',
  overflow: 'hidden',
  borderWidth: {
    sm: '0 1px 0 0',
    mobile: '0 0 0 0',
  },
  borderColor: 'lightGrey4',
  padding: {
    lg: '0 var(--chakra-space-lg)',
    sm: '0 var(--chakra-space-md)',
    mobile: '0 var(--chakra-space-xmd)',
  },
  _last: {
    borderRight: 'none',
    flex: {
      md: '1',
      mobile: 'initial',
    },
  },
  _groupHover: {
    padding: {
      lg: 'var(--chakra-space-lg)',
      sm: 'var(--chakra-space-md)',
    },
    borderWidth: '0 1px 0 0',
    borderColor: 'lightGrey4',
    _last: {
      borderRight: '0',
    },
  },
} as BoxProps;

const styledSummaryInfoBoxMobile = {
  _notLast: {
    _after: {
      content: {
        sm: '""',
        mobile: '","',
      },
    },
  },
  padding: {
    mobile: '0.25rem 0.125rem',
    lg: '0 var(--chakra-space-lg)',
    sm: '0 var(--chakra-space-md)',
  },
};

const styledEditText = {
  display: 'inline-flex',
  marginLeft: 'var(--chakra-space-md)',
  textDecoration: 'underline',
  color: 'darkGrey3',
  fontWeight: 'normal',
  fontSize: 'sm',
} as TextProps;

const styledText = {
  whiteSpace: 'nowrap',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  padding: '0',
} as TextProps;
const styledTextInactive = {
  color: 'lightGrey4',
};
