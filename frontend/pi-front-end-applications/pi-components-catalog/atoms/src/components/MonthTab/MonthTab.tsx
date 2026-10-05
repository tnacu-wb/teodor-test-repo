import { Text, Box, Flex } from '@chakra-ui/react';
import { formatCurrency, formatPrice } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

interface MonthTabProps {
  language?: string;
  label: string;
  price?: number;
  isActive: boolean;
  currencySymbol?: string;
  onClick: () => void;
  isLoading?: boolean;
  isLowestPriceInMonthTabEnabled?: boolean;
  badgeColor?: string;
}

const SkeletonBox = ({
  width = '50%',
  height = '20px',
  borderRadius = '4px',
}: {
  width?: string;
  height?: string;
  borderRadius?: string;
}) => (
  <Box
    data-testid="skeleton-box"
    width={width}
    height={height}
    borderRadius={borderRadius}
    bg="#EEEEEE"
    position="relative"
    overflow="hidden"
    margin={'0 auto'}
    _before={{
      content: '""',
      position: 'absolute',
      top: 0,
      left: '-100%',
      width: '100%',
      height: '100%',
      background: 'linear-gradient(90deg, transparent, rgba(255,255,255,0.6), transparent)',
      animation: 'shimmer 1.8s infinite ease-in-out',
    }}
    sx={{
      '@keyframes shimmer': {
        '0%': { left: '-100%' },
        '50%': { left: '100%' },
        '100%': { left: '100%' },
      },
    }}
  />
);

const MonthTab: React.FC<MonthTabProps> = ({
  language,
  label,
  price,
  isActive,
  currencySymbol,
  onClick,
  isLoading,
  isLowestPriceInMonthTabEnabled,
  badgeColor,
}) => {
  const { container, badge, bottomHighlight, priceWrapper, labelText } = styles(
    isActive,
    label,
    badgeColor
  );
  const { t } = useTranslation();
  const fromLowestPrice = formatPrice(
    formatCurrency(currencySymbol as string),
    price as number,
    language
  );

  return (
    <Flex {...container} onClick={onClick} data-testid={`month-tab-${label}`}>
      <Text {...labelText}>{label}</Text>

      {isLowestPriceInMonthTabEnabled &&
        (isLoading && isActive ? (
          <SkeletonBox />
        ) : (
          !!price &&
          isActive && (
            <Box {...priceWrapper}>
              <Text as="span">{t('priceFinder.from')}</Text>
              <Text {...badge}>{fromLowestPrice}</Text>
            </Box>
          )
        ))}

      {isActive && <Box {...bottomHighlight} />}
    </Flex>
  );
};

export default MonthTab;

const styles = (isActive: boolean, label: string, badgeColor?: string) => ({
  container: {
    _first: {
      ml: {
        mobile: 0,
        sm: 2,
      },
      mr: {
        mobile: 2,
      },
    },
    as: 'button' as const,
    direction: 'column' as const,
    align: 'center',
    justify: 'center',
    width: { base: '117px', sm: '145px' },
    height: { base: isActive ? '57px' : '48px', sm: isActive ? '72px' : '50px' },
    flexShrink: 0,
    mx: 2,
    position: 'relative' as const,
    bg: isActive ? 'white' : '#e9f2f3',
    color: isActive ? '#02798d' : 'gray.700',
    border: '1px solid #c4cacb',
    borderBottom: isActive ? 'none' : '1px solid #c4cacb',
    borderTopLeftRadius: 'md',
    borderTopRightRadius: 'md',
    borderBottomLeftRadius: '0',
    borderBottomRightRadius: '0',
    zIndex: isActive ? 1 : 0,
    whiteSpace: 'nowrap' as const,
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    fontSize: 'md',
    'data-month-value': label,
    _hover: isActive
      ? {
          bg: 'white',
          cursor: 'pointer',
          zIndex: 2,
        }
      : {
          bg: 'white',
          color: '#02798d',
          cursor: 'pointer',
          height: '57px',
          transition: 'height 0.2s',
        },
  },
  labelText: {
    fontWeight: isActive ? 'bold' : 'medium',
  },
  priceWrapper: {
    mt: 1,
    fontSize: 'xs',
    color: 'var(--chakra-colors-darkGrey1)',
  },
  badge: {
    as: 'span' as const,
    bg: badgeColor || '#fdb913', // Use custom color if provided, otherwise default
    color: '#333333',
    px: 2,
    py: 0.5,
    ml: 1,
    fontWeight: '500',
    lineHeight: '20px',
    borderRadius: 'sm',
    display: 'inline-block',
    fontSize: '13px',
    padding: '0 2px',
    h: '19px',
  },
  bottomHighlight: {
    position: 'absolute' as const,
    bottom: '-1px',
    left: 0,
    right: 0,
    height: '2px',
    bg: 'white',
    zIndex: -1,
    borderTopLeftRadius: '20px',
    borderTopRightRadius: '20px',
  },
});
