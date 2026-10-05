import { Box, useMediaQuery } from '@chakra-ui/react';
import { formatPrice, formatCurrency } from '@whitbread-eos/utils';
import dynamic from 'next/dynamic';
import React, { useState } from 'react';

import { Info } from '../../assets/icons';

const Tooltip = dynamic(
  async () => {
    const { default: Tooltip } = await import('../Tooltip/Tooltip.component');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

interface SubPriceProps {
  currencyCode?: string;
  label?: any;
  price: number;
  language?: string;
  infoIcon?: boolean;
  tooltipContent?: any;
  defaultLayout?: boolean;
  cityTaxStyles?: {
    priceStyle?: object;
    labelStyle?: object;
  };
}

const SubPrice: React.FC<SubPriceProps> = ({
  currencyCode,
  label,
  price,
  language,
  infoIcon,
  tooltipContent,
  defaultLayout = true,
  cityTaxStyles,
}) => {
  const [isMobile] = useMediaQuery('(max-width: 767px)');

  const isTouchDevice = navigator.maxTouchPoints > 0;

  const [isOpen, setIsOpen] = useState(false);
  return (
    <>
      <Box {...styles.wrapperStyles} data-testid="subPrice_text">
        <Box {...styles.roomPriceStyles} {...cityTaxStyles?.labelStyle}>
          {label}
          {infoIcon && (
            <Tooltip
              title={tooltipContent.title}
              description={tooltipContent.description}
              variant="infoGrey"
              placement="bottom-start"
              hasArrow
              closeDelay={1500}
              isOpen={isTouchDevice || isMobile ? isOpen : undefined}
              onClose={() => setIsOpen(false)}
              pointerEvents="auto"
              position="relative"
              sx={{
                ...styles.tooltip,
                '[data-popper-placement="bottom-start"] & [data-popper-arrow-inner],[data-popper-placement="bottom-end"] & [data-popper-arrow-inner]':
                  {
                    bg: 'var(--chakra-colors-white) !important',
                    width: 'var(--chakra-space-xmd) !important',
                    height: 'var(--chakra-space-xmd) !important',
                    borderLeft: '1px solid var(--chakra-colors-primary)',
                    borderTop: '1px solid var(--chakra-colors-primary)',
                    top: '-3px !important',
                  },
                '[data-popper-placement="top-start"] & [data-popper-arrow-inner], [data-popper-placement="top-end"] & [data-popper-arrow-inner]':
                  {
                    bg: 'var(--chakra-colors-white) !important',
                    width: 'var(--chakra-space-xmd) !important',
                    height: 'var(--chakra-space-xmd) !important',
                    borderRight: '1px solid var(--chakra-colors-primary)',
                    borderBottom: '1px solid var(--chakra-colors-primary)',
                    top: '1px !important',
                  },
              }}
              alertElementStyles={{
                padding: '0.5rem 0.25rem 0.5rem 0',
              }}
            >
              <Box
                {...styles.tooltipIcon}
                onClick={(e) => {
                  e.stopPropagation();
                  setIsOpen((prev) => !prev);
                }}
              >
                <Info />
              </Box>
            </Tooltip>
          )}
        </Box>

        {defaultLayout && (
          <Box {...styles.roomPriceStyles} {...cityTaxStyles?.priceStyle}>
            {formatPrice(formatCurrency(currencyCode as string), price.toFixed(2), language)}
          </Box>
        )}
      </Box>

      {!defaultLayout && (
        <Box {...styles.roomPriceStyles} {...cityTaxStyles?.priceStyle} mb={4}>
          {formatPrice(formatCurrency(currencyCode as string), price.toFixed(2), language)}
        </Box>
      )}
    </>
  );
};

export default SubPrice;

const styles = {
  wrapperStyles: {
    display: 'flex',
    justifyContent: 'space-between',
  },
  roomPriceStyles: {
    display: 'inline-block',
  },
  tooltip: {
    bg: 'var(--chakra-colors-white)',
    color: 'var(--chakra-colors-darkGrey1)',
    border: '1px solid var(--chakra-colors-primary)',
    left: {
      mobile: '10px',
      xs: '-7px',
    },
    top: '5px',
    _before: {
      display: 'none',
    },
    width: {
      mobile: 'calc(100% - 20px)',
      xs: 'auto',
    },
  },
  tooltipIcon: {
    display: 'inline-block',
    marginLeft: '4px',
    marginTop: '-2px',
    verticalAlign: 'middle',
    cursor: 'pointer',
  },
};
