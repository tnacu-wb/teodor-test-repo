import { Box, Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import type { HIRateClassification, HIRoomRate } from '@whitbread-eos/api';
import { RadioButton, PromoTag, PencePrice } from '@whitbread-eos/atoms';
import { useCustomLocale, useSemanticTypography } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

interface Props {
  brand: string;
  value: string;
  selectedRoomClassAndRate: string;
  setSelectedRoomClassAndRate: (selectedClassAndRate: string) => void;
  roomRate: HIRoomRate;
  rateClassification: HIRateClassification;
  totalReservationAmount: number;
  totalBaseAmount?: number;
  numberOfNights?: number;
  numberOfUnits?: number;
}

export default function RateItem({
  brand,
  value,
  roomRate,
  rateClassification,
  selectedRoomClassAndRate,
  totalReservationAmount,
  totalBaseAmount,
  setSelectedRoomClassAndRate,
  numberOfNights,
  numberOfUnits,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const getTypographyProps = useSemanticTypography();
  const ratePlanNameTypographyProps = getTypographyProps(
    ratePlanNameLegacyTypography,
    ratePlanNameSemanticTypography
  );
  const isSemanticRatePlanNameTypography = 'textStyle' in ratePlanNameTypographyProps;
  const discountRateProofTypographyProps = getTypographyProps(
    discountRateProofLegacyTypography,
    discountRateProofSemanticTypography
  );
  const isSemanticDiscountRateProofTypography = 'textStyle' in discountRateProofTypographyProps;
  const ratePlanDescriptionTypographyProps = getTypographyProps(
    {},
    ratePlanDescriptionSemanticTypography
  );
  const employeeOfferTypographyProps = getTypographyProps(
    employeeOfferLegacyTypography,
    employeeOfferSemanticTypography
  );
  const rateItemTotalPriceTitleTypographyProps = getTypographyProps(
    rateItemTotalPriceTitleLegacyTypography,
    rateItemTotalPriceTitleSemanticTypography
  );
  const rateItemNrOfRoomsNightTypographyProps = getTypographyProps(
    rateItemNrOfRoomsNightLegacyTypography,
    rateItemNrOfRoomsNightSemanticTypography
  );
  const rateItemBasePriceTypographyProps = getTypographyProps(
    rateItemBasePriceLegacyTypography,
    rateItemBasePriceSemanticTypography
  );

  const { rateName, rateDescription, ratePlanCode, isCorporateDiscountAvailable, rateTags } =
    rateClassification || {};
  const { currencyCode } = roomRate?.roomTypes?.[0]?.rooms?.[0]?.roomPriceBreakdown || {};
  const { language } = useCustomLocale();
  const isBaseAmountAvailable = !!totalBaseAmount;

  const RATE_PLAN_BUSINESS_FLEX = 'BUSIFLEX';

  return (
    <Box w="full">
      <Flex
        {...rateItemCardStyle}
        data-testid="hdp_rateItemCard"
        key={`${rateName}-${ratePlanCode}-${value}`}
      >
        {rateName && rateDescription && (
          <RadioButton
            onChange={(ev) => {
              setSelectedRoomClassAndRate(ev.target.value);
            }}
            value={value}
            isChecked={value === selectedRoomClassAndRate}
            variant="borderless"
            width="full"
          >
            <Box>
              <Text
                {...ratePlanNameLayoutStyles}
                {...ratePlanNameTypographyProps}
                {...(isSemanticRatePlanNameTypography ? { as: 'span' } : { as: 'b' })}
                data-testid="hdp_ratePlanName"
                color={isCorporateDiscountAvailable ? 'primary' : 'initial'}
              >
                {`${
                  brand.toLowerCase() === 'hub' ? `${t('booking.rates.hub.prefix')} ` : ''
                }${rateName}`}
                <PromoTag rateDiscountTags={rateTags} customStyleName="rateItem"></PromoTag>
                {isCorporateDiscountAvailable && (
                  <Text
                    {...discountRateProofLayoutStyles}
                    {...discountRateProofTypographyProps}
                    {...(isSemanticDiscountRateProofTypography ? { as: 'span' } : { as: 'b' })}
                  >
                    : {t(`promotion.discountrate.${ratePlanCode}.idrequired`)}
                  </Text>
                )}
              </Text>
              {roomRate?.cellCode ? (
                <Text
                  {...employeeOfferLayoutStyles}
                  {...employeeOfferTypographyProps}
                  data-testid="hdp_employeeOffer"
                >
                  {t('promotion.EMP01.available')}
                </Text>
              ) : null}
              <Text {...ratePlanDescriptionTypographyProps} data-testid="hdp_ratePlanDescription">
                {rateDescription}
              </Text>
            </Box>
          </RadioButton>
        )}
        <Box textAlign="end" maxW="32" w="full">
          <Flex {...rateItemPriceStyles}>
            {isBaseAmountAvailable && (
              <Text
                {...rateItemBasePriceLayoutStyles}
                {...rateItemBasePriceTypographyProps}
                data-testid="hdp_rateItemBasePrice"
              >
                <PencePrice
                  price={totalBaseAmount}
                  currency={currencyCode}
                  language={language}
                  size="xxs"
                  lineThrough={true}
                />
              </Text>
            )}
            <Text
              {...rateItemTotalPriceStyles(isBaseAmountAvailable)}
              data-testid="hdp_rateItemTotalPrice"
            >
              <PencePrice
                price={totalReservationAmount}
                currency={currencyCode}
                language={language}
                size="xxs"
              />
            </Text>
          </Flex>

          <Text
            {...rateItemTotalPriceTitleTypographyProps}
            data-testid="hdp_rateItemTotalPriceTitle"
          >
            {ratePlanCode === RATE_PLAN_BUSINESS_FLEX
              ? t('hoteldetails.rates.afterPrice')
              : t('hoteldetails.rates.total.price')}
          </Text>
          {!!numberOfNights && !!numberOfUnits && (
            <Box
              display={{
                mobile: 'inline-grid',
                sm: 'block',
              }}
            >
              <Text
                {...rateItemNrOfRoomsNightLayoutStyle}
                {...rateItemNrOfRoomsNightTypographyProps}
                data-testid="hdp_rateItemNrOfRooms"
              >
                {t('hoteldetails.nrOfRooms', { count: numberOfUnits })},
              </Text>
              <Text
                {...rateItemNrOfRoomsNightLayoutStyle}
                {...rateItemNrOfRoomsNightTypographyProps}
                data-testid="hdp_rateItemNrOfNights"
              >
                &nbsp;{t('hoteldetails.nrOfNights', { count: numberOfNights })}
              </Text>
            </Box>
          )}
        </Box>
      </Flex>
    </Box>
  );
}

const rateItemCardStyle = {
  alignItems: 'center',
  justifyContent: 'space-between',
  width: 'full',
  borderRadius: 'md',
  padding: { base: '1', sm: '2' },
  _focusWithin: {
    outline: '2px solid',
    outlineColor: 'var(--chakra-colors-primary)',
    outlineOffset: '2px',
  },
};

const rateItemNrOfRoomsNightLayoutStyle = {
  display: 'inline-block',
  color: 'lightGrey1',
};

const rateItemNrOfRoomsNightLegacyTypography = {
  fontSize: 'xs',
  lineHeight: '2',
};

const rateItemNrOfRoomsNightSemanticTypography = {
  textStyle: 'body-s-regular',
};

const rateItemTotalPriceStyles = (isBaseAmountAvailable: boolean) => {
  let styles: TextProps = {
    fontSize: { base: 'md', sm: 'lg' },
    lineHeight: '3',
    fontWeight: 'semibold',
  };
  if (isBaseAmountAvailable) {
    styles = { ...styles, color: 'primary' };
  }

  return styles;
};

const rateItemPriceStyles: FlexProps = {
  flexDirection: { base: 'column-reverse', md: 'row' },
  justifyContent: 'flex-end',
  gap: { base: '0', md: '2' },
};

const rateItemBasePriceLayoutStyles = {
  color: 'lightGrey1',
  textDecoration: ' line-through',
};

const rateItemBasePriceLegacyTypography = {
  fontSize: { base: 'md', sm: 'lg' },
  lineHeight: '3',
  fontWeight: 'semibold',
};

const rateItemBasePriceSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const rateItemTotalPriceTitleLegacyTypography = {
  fontSize: 'xs',
  lineHeight: '2',
};

const rateItemTotalPriceTitleSemanticTypography = {
  textStyle: 'body-s-regular',
};

const employeeOfferLayoutStyles = {
  color: 'success',
};

const employeeOfferLegacyTypography = {
  fontWeight: 'semibold',
};

const employeeOfferSemanticTypography = {
  textStyle: 'body-m-emphasis',
};

const ratePlanNameLayoutStyles = {
  marginTop: '-1rem',
};

const ratePlanNameLegacyTypography = {
  fontSize: 'lg',
};

const ratePlanNameSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const discountRateProofLayoutStyles = {
  color: 'darkGrey1',
  mb: 'sm',
};

const discountRateProofLegacyTypography = {
  fontWeight: 'semibold',
};

const discountRateProofSemanticTypography = {
  textStyle: 'body-l-emphasis',
};

const ratePlanDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
};
