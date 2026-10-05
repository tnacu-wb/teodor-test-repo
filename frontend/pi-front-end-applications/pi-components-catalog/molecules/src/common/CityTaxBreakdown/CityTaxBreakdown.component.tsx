import { Box, Text } from '@chakra-ui/react';
import { ChevronDown24, ChevronUp24 } from '@whitbread-eos/atoms';
import { formatPrice, formatCurrency, renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useState } from 'react';

interface CityTaxBreakdownProps {
  currencyCode?: string;
  totalCityTaxAmount?: number;
  language?: string;
  cityTaxStyles?: {
    priceStyle?: object;
    labelStyle?: object;
  };
}

const CityTaxBreakdown: React.FC<CityTaxBreakdownProps> = ({
  currencyCode,
  totalCityTaxAmount,
  language,
  cityTaxStyles,
}) => {
  const { t } = useTranslation(['common']);
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <Box {...styles.wrapperStyles} data-testid="city-tax-breakdown">
        <Box
          as="button"
          type="button"
          cursor="pointer"
          {...styles.roomPriceStyles}
          {...cityTaxStyles?.labelStyle}
          aria-expanded={isOpen}
          onClick={() => setIsOpen((prev) => !prev)}
        >
          {t('booking.cityTax.label')}
          <Box display="inline-block" verticalAlign="-7px" as="span">
            {isOpen ? <ChevronUp24 /> : <ChevronDown24 />}
          </Box>
        </Box>

        <Box {...styles.roomPriceStyles} {...cityTaxStyles?.priceStyle}>
          {formatPrice(
            formatCurrency(currencyCode as string),
            totalCityTaxAmount && totalCityTaxAmount.toFixed(2),
            language
          )}
        </Box>
      </Box>
      {isOpen && (
        <>
          <Box>
            <Text {...styles.accordionTitle}>{t('booking.cityTax.tooltip.title')}</Text>
            <p>{renderSanitizedHtml(t('booking.cityTax.tooltip.description'))}</p>
          </Box>
        </>
      )}
    </>
  );
};

export default CityTaxBreakdown;

const styles = {
  wrapperStyles: {
    display: 'flex',
    justifyContent: 'space-between',
  },
  roomPriceStyles: {
    display: 'inline-block',
  },
  accordionTitle: {
    fontWeight: '600',
  },
};
