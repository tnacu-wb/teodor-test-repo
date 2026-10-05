import type { DividerProps, FlexProps, GridProps } from '@chakra-ui/react';
import { Box, Divider, Grid, Text } from '@chakra-ui/react';
import { type BookingSummaryDataProps } from '@whitbread-eos/api';
import { Card, Info, Notification, PromoTag, SubPrice } from '@whitbread-eos/atoms';
import {
  BookingSummaryHotelDetailsInfo,
  BookingSummaryRateInformation,
  BookingSummaryRoomInformation,
  BookingSummaryStayDatesInformation,
  BookingSummaryTotalCost,
  BookingSummaryUpgradeToFlex,
} from '@whitbread-eos/molecules';
import {
  formatCurrency,
  formatDataTestId,
  formatPrice,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';

export interface Props {
  bookingSummaryData: BookingSummaryDataProps;
  totalCostAmount: number;
  t: (x: string, y?: { [key: string]: string }) => string;
  language: string | undefined;
  prefixDataTestId?: string;
  currencyCode: string;
  isDiscountApplied?: boolean;
  taxesMessage?: string;
  isExtrasDisplayed?: boolean;
  isSoftBundlesVisible?: boolean;
  isCityTaxBreakdownEnabled?: boolean;
}

export default function BookingSummaryCard({
  bookingSummaryData,
  totalCostAmount,
  t,
  language,
  prefixDataTestId,
  currencyCode,
  isDiscountApplied,
  taxesMessage,
  isExtrasDisplayed,
  isSoftBundlesVisible,
  isCityTaxBreakdownEnabled,
}: Readonly<Props>) {
  const getTypographyProps = useSemanticTypography();
  const preselectedMealNotificationTypographyProps = getTypographyProps(
    preselectedMealNotificationLegacyTypography,
    preselectedMealNotificationSemanticTypography
  );
  const preselectedMealNotificationDescriptionTextStyle =
    'textStyle' in preselectedMealNotificationTypographyProps
      ? (preselectedMealNotificationTypographyProps.textStyle as string)
      : undefined;
  const {
    hotelInformation = null,
    totalCost = null,
    rateInformation = null,
    stayDatesInformation = null,
    roomInformation = null,
    updateToFlex = null,
  } = bookingSummaryData;

  const rateTags = bookingSummaryData?.rateInformation?.rateTags;

  const cityTaxStyles = {
    labelStyle: {
      fontWeight: '600',
    },
  };

  const cityTax = {
    label: t('booking.cityTax.label'),
    price: bookingSummaryData?.cityTaxTotal || 0,
  };

  const tooltipContent = {
    title: t('booking.cityTax.tooltip.title'),
    description: renderSanitizedHtml(t('booking.cityTax.tooltip.description')),
  };

  return (
    <Card {...bookingSummaryCardStyle} data-testid={formatDataTestId(prefixDataTestId, 'Wrapper')}>
      {hotelInformation && (
        <BookingSummaryHotelDetailsInfo prefixDataTestId={prefixDataTestId} {...hotelInformation} />
      )}
      {(totalCost || rateInformation) && (
        <>
          <Divider {...dividerStyles} />
          <Grid {...costsWrapperStyle}>
            {isCityTaxBreakdownEnabled && !!bookingSummaryData.cityTaxTotal && (
              <SubPrice
                currencyCode={currencyCode}
                language={language}
                label={cityTax.label}
                price={cityTax.price}
                infoIcon={true}
                tooltipContent={tooltipContent}
                cityTaxStyles={cityTaxStyles}
                defaultLayout={false}
              />
            )}

            {totalCost && (
              <BookingSummaryTotalCost
                {...totalCost}
                t={t}
                language={language}
                totalCostAmount={totalCostAmount}
                prefixDataTestId={prefixDataTestId}
                currency={currencyCode}
                isDiscountApplied={isDiscountApplied}
                taxesMessage={taxesMessage}
              />
            )}
            {rateTags && rateTags.length > 0 && (
              <Box data-testid="hdp_discountPromoTag">
                <PromoTag
                  rateDiscountTags={rateTags}
                  customStyleName="BookingSummaryCard"
                ></PromoTag>
              </Box>
            )}
            {totalCost && Boolean(totalCost?.donations) && language === 'en' && (
              <Text
                mt="md"
                fontSize="xs"
                fontWeight="normal"
                data-testid={formatDataTestId(prefixDataTestId, 'Donations')}
              >
                {t('booking.summary.donationTextStart')}{' '}
                <strong>
                  {formatPrice(
                    formatCurrency(totalCost?.currency),
                    totalCost?.donations?.toFixed(2),
                    language
                  )}{' '}
                  {t('booking.summary.donationTextMiddle')}
                </strong>{' '}
                {t('booking.summary.donationTextEnd')}
              </Text>
            )}
          </Grid>
          <Grid>
            {rateInformation && (
              <>
                <Divider {...dividerStyles} />
                <Grid {...rateWrapperStyle}>
                  <BookingSummaryRateInformation
                    prefixDataTestId={prefixDataTestId}
                    t={t}
                    brand={hotelInformation?.hotelBrand}
                    {...rateInformation}
                  />
                  {!isSoftBundlesVisible && updateToFlex?.showUpgradeToFlex && (
                    <Box {...cancelPolicyStyles}>
                      <BookingSummaryUpgradeToFlex {...updateToFlex} t={t} />
                    </Box>
                  )}
                </Grid>
              </>
            )}
          </Grid>

          {stayDatesInformation && (
            <>
              <Divider {...dividerStyles} />
              <BookingSummaryStayDatesInformation
                language={language}
                prefixDataTestId={prefixDataTestId}
                t={t}
                {...stayDatesInformation}
              />
            </>
          )}
          {roomInformation && (
            <BookingSummaryRoomInformation
              t={t}
              roomInformation={roomInformation}
              prefixDataTestId={prefixDataTestId}
              isExtrasDisplayed={isExtrasDisplayed}
            />
          )}
          {bookingSummaryData.showAutocompleteMealsNotification && (
            <Box
              mt="lg"
              data-testid={formatDataTestId(prefixDataTestId, 'PreselectedMealNotification')}
            >
              <Notification
                status="info"
                description={t('upsell.notification.preselectedMeal')}
                variant="info"
                maxW="full"
                svg={<Info />}
                descriptionTextStyle={preselectedMealNotificationDescriptionTextStyle}
              />
            </Box>
          )}
        </>
      )}
    </Card>
  );
}

const preselectedMealNotificationLegacyTypography = {};

const preselectedMealNotificationSemanticTypography = {
  textStyle: 'body-s-regular',
};

const bookingSummaryCardStyle = {
  direction: 'column',
  mt: { mobile: 'md', lg: 0 },
  padding: 'md',
  py: { lg: 'lg' },
} as FlexProps;

const costsWrapperStyle = {
  gridTemplateColumns: 'auto',
  direction: 'column',
  py: { mobile: 'md', lg: 'lg' },
} as GridProps;

const dividerStyles = {
  borderColor: 'lightGrey4',
  opacity: '1',
} as DividerProps;

const rateWrapperStyle = {
  gridTemplateColumns: 'auto',
  py: { mobile: 'md', lg: 'lg' },
} as GridProps;

const cancelPolicyStyles = {
  backgroundColor: 'lightGrey5',
  borderRadius: 'md',
  padding: 'md',
  mt: 'md',
  border: '1px solid var(--chakra-colors-lightGrey4)',
  width: 'fit-content',
} as FlexProps;
