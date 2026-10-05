import { Flex, Text, Collapse, Box, TextProps, FlexProps } from '@chakra-ui/react';
import { ChevronDown24, ChevronUp24 } from '@whitbread-eos/atoms';
import { formatCurrency, formatDataTestId, formatPriceWithDecimal } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState } from 'react';

interface CityTaxProps {
  baseDataTestId?: string;
  cityTaxTotal: number;
  language: string;
  currency: string;
  priceStylesProps?: TextProps;
}

const CityTax: React.FC<CityTaxProps> = ({
  baseDataTestId,
  cityTaxTotal,
  language,
  currency,
  priceStylesProps,
}) => {
  const { t } = useTranslation();
  const [isCityTaxOpen, setIsCityTaxOpen] = useState(false);

  const cityTax = {
    label: t('booking.cityTax.label'),
    price: cityTaxTotal || 0,
  };

  const CollapseContent = {
    title: t('amend.cityTax.title'),
    description: t('amend.cityTax.description'),
  };
  return (
    <>
      <Flex
        {...cityTaxHeaderStyles}
        data-testid={formatDataTestId(baseDataTestId, 'city-tax-header')}
        onClick={() => setIsCityTaxOpen(!isCityTaxOpen)}
        role="button"
        aria-expanded={isCityTaxOpen}
        aria-controls="city-tax-collapse"
        onKeyDown={(event) => {
          if (event.key === 'Enter' || event.key === ' ') {
            event.preventDefault();
            setIsCityTaxOpen((open) => !open);
          }
        }}
        tabIndex={0}
      >
        <Flex {...cityTaxLabelPriceWrapper}>
          <Text
            {...cityTaxLabelStyles}
            data-testid={formatDataTestId(baseDataTestId, 'city-tax-label')}
          >
            {cityTax.label}
          </Text>
          {isCityTaxOpen ? (
            <ChevronUp24
              color="var(--chakra-colors-darkGrey3)"
              data-testid={formatDataTestId(baseDataTestId, 'city-tax-chevron-up')}
            />
          ) : (
            <ChevronDown24
              color="var(--chakra-colors-darkGrey3)"
              data-testid={formatDataTestId(baseDataTestId, 'city-tax-chevron-down')}
            />
          )}
        </Flex>
        <Text
          {...cityTaxPriceStyles}
          {...priceStylesProps}
          data-testid={formatDataTestId(baseDataTestId, 'city-tax-price')}
        >
          {formatPriceWithDecimal(language, formatCurrency(currency), cityTax.price, true)}
        </Text>
      </Flex>
      <Collapse
        id="city-tax-collapse"
        in={isCityTaxOpen}
        data-testid={formatDataTestId(baseDataTestId, 'city-tax-collapse')}
      >
        <Box {...cityTaxCollapseContent}>
          <Text {...cityTaxCollapseTitleStyles}>{CollapseContent.title}</Text>
          <Text {...cityTaxCollapseDescriptionStyles}>{CollapseContent.description}</Text>
        </Box>
      </Collapse>
    </>
  );
};

export default CityTax;

const cityTaxHeaderStyles = {
  width: '100%',
  justifyContent: 'space-between',
  alignItems: 'center',
  cursor: 'pointer',
  userSelect: 'none',
  mb: 'sm',
} as FlexProps;

const cityTaxLabelPriceWrapper = {
  justifyContent: 'flex-start',
  width: '100%',
  alignItems: 'center',
} as FlexProps;

const cityTaxLabelStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '2',
  color: 'darkGrey1',
  textAlign: 'left',
} as TextProps;

const cityTaxPriceStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '2',
  color: 'darkGrey1',
  textAlign: 'right',
  ml: 'sm',
  width: '100%',
} as TextProps;

const cityTaxCollapseContent = {
  mt: 'xs',
  mb: 'xs',
  borderRadius: '0.25rem',
} as FlexProps;

const cityTaxCollapseTitleStyles = {
  fontSize: 'md',
  fontWeight: 'bold',
  lineHeight: '2',
  mb: 'xs',
  color: 'darkGrey1',
} as TextProps;

const cityTaxCollapseDescriptionStyles = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '2',
  color: 'darkGrey1',
} as TextProps;
