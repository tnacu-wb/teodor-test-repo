import type { BoxProps, FlexProps, GridProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Grid, Link, Text } from '@chakra-ui/react';
import {
  FREE_FOOD_OPTIONS,
  FT_PI_BB_CCUI_SHOW_MEALS_FREE,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
} from '@whitbread-eos/api';
import { Info, Icon, PromoTag } from '@whitbread-eos/atoms';
import {
  formatCurrency,
  formatDataTestId,
  formatPrice,
  akamaiImageLoader,
  isIVMEnabled,
  renderSanitizedHtml,
  useFeatureToggle,
  useLocalStorage,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';
import { useState, type ReactNode } from 'react';

import { BundleSideDrawer } from '../../hotel-details/SideDrawer';

type GetMealItemWrapperPropsType = {
  wrapperMealItemStyle: GridProps;
  isMealItemSelected: boolean;
  isMealItemDisabled: boolean;
  prefixDataTestId: string;
};

type FreeKidsMealProps = {
  isMealItemSelected: boolean;
  isMealItemDisabled: boolean;
  freeTag: string[];
  freeFoodKidsStyle: TextProps;
  prefixDataTestId: string;
  t: (key: string) => string;
};

type GridWithTestIdProps = GridProps & {
  'data-testid'?: string;
};

type FreePriceProps = {
  isMealItemSelected: boolean;
  currency?: string;
  basePrice?: number;
  currentLanguage: string;
  numberNights?: number;
  t: (key: string) => string;
  priceTextStyle: TextProps;
  isForEntireStay?: boolean;
};

export interface Props {
  currentLanguage: string | undefined;
  title: string | undefined;
  description: string | undefined;
  price?: number;
  currency?: string;
  numberNights?: number;
  freeBreakfastOption: boolean | undefined;
  imageUrl: string | undefined;
  totalPrice?: number;
  allergyInfoLabel: string | undefined;
  allergyInfoSrc: string | undefined;
  prefixDataTestId?: string;
  isForEntireStay?: boolean;
  controller: ReactNode;
  showFreeFoodKids?: boolean;
  hasPromoMeal?: boolean;
  upsellType?: string;
  isSelected?: boolean;
  isIncluded?: boolean;
  adultsNumber?: number;
  outcomePrice?: number;
  displayPrice?: string;
  isSoftBundlesVisible?: boolean;
  isFree?: boolean | null;
  basePrice?: number | null;
  isAdultHasMealsFree?: boolean | null;
}

export default function MealItem({
  currency,
  numberNights,
  description,
  imageUrl,
  freeBreakfastOption,
  price,
  title,
  allergyInfoSrc,
  totalPrice,
  allergyInfoLabel,
  prefixDataTestId,
  controller,
  currentLanguage = 'en',
  isForEntireStay = false,
  showFreeFoodKids,
  hasPromoMeal = false,
  upsellType = '',
  isSelected = false,
  isIncluded = false,
  adultsNumber = 0,
  outcomePrice = 0, //price calculated by subtracting the currently selected meal
  isSoftBundlesVisible = false,
  isFree = false,
  basePrice = 0,
  isAdultHasMealsFree = false,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);

  const [isBundleSideDrawerVisible, setIsBundleSideDrawerVisible] = useState(false);

  const { [FT_PI_BB_CCUI_SHOW_MEALS_FREE]: isFreeMealDinnerEnabled } = useFeatureToggle();
  const getTypographyProps = useSemanticTypography();

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  const freeTag = basketDetailsState?.rateTags;
  const isMealItemDisabled = isAdultHasMealsFree && !isFree && !isSelected;
  const isMealItemSelected = isAdultHasMealsFree && isSelected;

  const showFreeDinnerLabel =
    isFreeMealDinnerEnabled &&
    upsellType === FREE_FOOD_OPTIONS.DINNER &&
    freeBreakfastOption &&
    showFreeFoodKids;

  const renderBadgeAndPrice = (mobile = false) => {
    const adultsLabel = adultsNumber === 1 ? t('upsell.label.adult') : t('upsell.label.adults');
    const nightLabel = numberNights === 1 ? t('upsell.label.night') : t('upsell.label.nights');

    const displayPrice = formatPrice(
      formatCurrency(currency ?? ''),
      (outcomePrice < 0 ? outcomePrice * -1 : outcomePrice).toFixed(2),
      currentLanguage
    );
    return (
      <>
        {isIncluded && (
          <Flex>
            <Text
              px="0.5rem"
              py="0.25rem"
              mb={{ mobile: 0, sm: '1rem' }}
              backgroundColor="var(--chakra-colors-primary)"
              color="white"
              ml={{ mobile: 0, sm: 'auto' }}
              fontSize="xs"
              borderRadius="var(--chakra-radii-base)"
              fontWeight="semibold"
              data-testid={mobile ? 'SB-Included-Badge-Mobile' : 'SB-Included-Badge'}
            >
              {t('upsell.extras.included')}
            </Text>
          </Flex>
        )}
        {!isSelected && outcomePrice !== 0 && (
          <Flex
            direction={{ mobile: 'row', sm: 'column' }}
            display={{ mobile: 'inline', sm: 'flex' }}
            mb={{ mobile: 0, sm: '0.5rem' }}
            alignItems={{ mobile: 'center', sm: 'initial' }}
          >
            <Text
              fontWeight={{ mobile: 'bold', md: 'semibold' }}
              fontSize="lg"
              ml={{ mobile: 0, sm: 'auto' }}
              display="inline"
              data-testid={mobile ? 'SB-Meal-Outcome-Price-Mobile' : 'SB-Meal-Outcome-Price'}
            >
              {`${outcomePrice < 0 ? '-' : '+'}${displayPrice}`}
            </Text>
            <Text
              display="inline"
              ml={{ mobile: '0.5rem', sm: 'auto' }}
              fontSize="sm"
              fontWeight={{ mobile: 'bold', md: 'normal' }}
              data-testid={
                mobile ? 'SB-Meal-Summary-Price-Info-Mobile' : 'SB-Meal-Summary-Price-Info'
              }
            >{`${t(
              'upsell.label.for'
            )} ${adultsNumber} ${adultsLabel}, ${numberNights} ${nightLabel}`}</Text>
          </Flex>
        )}
      </>
    );
  };

  const renderPriceText = () => {
    if (isMealItemSelected && currency && numberNights) {
      return (
        <FreePrice
          isMealItemSelected={isMealItemSelected}
          currency={currency}
          basePrice={basePrice as number}
          currentLanguage={currentLanguage}
          numberNights={numberNights}
          t={t}
          priceTextStyle={priceTextStyle}
          isForEntireStay={isForEntireStay}
        />
      );
    }
    if (!hasPromoMeal && price && numberNights && currency && totalPrice) {
      return (
        <Text
          {...priceLayoutStyle}
          {...getTypographyProps(priceLegacyTypography, priceSemanticTypography)}
          data-testid={formatDataTestId(prefixDataTestId, 'Price')}
        >
          {renderPrice(currentLanguage, currency, price, totalPrice, numberNights)}
        </Text>
      );
    }

    if (hasPromoMeal) {
      return (
        <Text
          {...priceLayoutStyle}
          {...getTypographyProps(priceLegacyTypography, priceSemanticTypography)}
          data-testid={formatDataTestId(prefixDataTestId, 'Price')}
        >
          {t('config.experiments.breakfastPromoCode.priceText')}
        </Text>
      );
    }

    return null;
  };

  const handleMealInfoClick = () => {
    setIsBundleSideDrawerVisible(true);
  };

  const descriptionBox = (
    <Box
      {...descriptionLayoutStyle}
      pt="xs"
      {...getTypographyProps(descriptionLegacyTypography, descriptionSemanticTypography)}
      data-testid={formatDataTestId(prefixDataTestId, 'Description')}
      className="formatLinks"
    >
      {renderSanitizedHtml(description ?? '')}
    </Box>
  );

  const allergyInfo = (testId = 'Allergy') => {
    return (
      allergyInfoSrc &&
      allergyInfoLabel && (
        <Link href={allergyInfoSrc} isExternal>
          <Text
            {...allergyLayoutStyle(freeBreakfastOption as boolean)}
            {...getTypographyProps(allergyLegacyTypography, allergySemanticTypography)}
            data-testid={formatDataTestId(prefixDataTestId, testId)}
          >
            {allergyInfoLabel}
          </Text>
        </Link>
      )
    );
  };

  const infoBox = (
    <Flex {...containerInfoStyle}>
      {!hasPromoMeal && renderFreeOptionText()}
      {hasPromoMeal && (
        <Text
          {...freeFoodKidsStyle}
          data-testid={formatDataTestId(prefixDataTestId, 'FreeBreakfastPromo')}
        >
          {t('config.experiments.breakfastPromoCode.promotionText')}
        </Text>
      )}
      {allergyInfo()}
    </Flex>
  );

  const softBundleMealVariant = (
    <>
      <Grid
        {...wrapperMealItemStyle}
        borderRadius={4}
        borderColor={
          isSelected ? 'var(--chakra-colors-primary)' : 'var(--chakra-colors-lightGrey3)'
        }
        backgroundColor={isSelected ? 'var(--chakra-colors-primaryShadow)' : 'transparent'}
        data-testid={
          isSelected
            ? formatDataTestId(prefixDataTestId, 'SB-Meal-Item-Wrapper-Selected')
            : formatDataTestId(prefixDataTestId, 'SB-Meal-Item-Wrapper')
        }
      >
        <Flex mb="1rem">
          <Flex
            {...SB_containerImageStyle}
            data-testid={formatDataTestId(prefixDataTestId, 'Image-Wrapper')}
          >
            {imageUrl && (
              <Image
                fill
                style={{ objectFit: 'cover' }}
                alt={t('upsell.menus.iconAltText')}
                src={imageUrl}
                loader={isIVMEnabled() ? akamaiImageLoader : undefined}
              />
            )}
          </Flex>
          <Flex direction="column" display={{ mobile: 'flex', sm: 'none' }} ml="1rem" width="100%">
            <Flex>
              <Text
                {...titleLayoutStyle}
                pt="0"
                mr="1rem"
                {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
                fontWeight="bold"
                data-testid={formatDataTestId(prefixDataTestId, 'Title_SB_Mobile')}
              >
                {title}
              </Text>
              <Icon
                ml="auto"
                svg={<Info />}
                onClick={handleMealInfoClick}
                data-testid="SB-Side-Drawer-Button"
              />
            </Flex>
            <Box mt={{ mobile: '0.5rem', md: '0' }}>{renderBadgeAndPrice(true)}</Box>
          </Flex>
        </Flex>
        <Flex {...SB_containerDescriptionStyle}>
          <Flex flexDir="column" lineHeight="2">
            <Text
              {...titleLayoutStyle}
              {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
              data-testid={formatDataTestId(prefixDataTestId, 'Title_SB')}
            >
              {title}
            </Text>
            {descriptionBox}
          </Flex>
          {infoBox}
        </Flex>
        <Flex {...SB_controllerContainerStyle}>
          <Box display={{ mobile: 'none', sm: 'block' }}>{renderBadgeAndPrice()}</Box>
          {controller}
        </Flex>
      </Grid>
      <BundleSideDrawer
        currency={currency ?? ''}
        language={currentLanguage}
        visible={isBundleSideDrawerVisible}
        onClose={() => setIsBundleSideDrawerVisible(false)}
        numberOfNights={numberNights ?? 1}
        packages={[
          {
            name: title,
            image: imageUrl,
            description: description,
            links: [allergyInfo('Side-Drawer-Allergy-Link')],
            price,
          },
        ]}
        adultsNumber={adultsNumber}
      />
    </>
  );

  return isSoftBundlesVisible ? (
    softBundleMealVariant
  ) : (
    <Grid
      {...getMealItemWrapperProps({
        wrapperMealItemStyle,
        isMealItemSelected,
        isMealItemDisabled,
        prefixDataTestId,
      } as GetMealItemWrapperPropsType)}
    >
      <Box
        {...containerImageStyle}
        data-testid={formatDataTestId(prefixDataTestId, 'Image-Wrapper')}
      >
        {imageUrl && (
          <Image
            fill
            sizes="(max-width: 576px) 576px, (max-width: 768px) 768px, 158px"
            style={{ objectFit: 'cover' }}
            alt={t('upsell.menus.iconAltText')}
            src={imageUrl}
            loader={isIVMEnabled() ? akamaiImageLoader : undefined}
          />
        )}
      </Box>

      <Flex {...containerDescriptionStyle}>
        <Flex flexDir="column" lineHeight="2">
          <Text
            {...titleLayoutStyle}
            {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
            data-testid={formatDataTestId(prefixDataTestId, 'Title')}
          >
            {title}
          </Text>
          {renderPriceText()}
          {descriptionBox}
        </Flex>
        {infoBox}
      </Flex>
      <Box py="md">
        <Box {...delimiterStyle} />
      </Box>
      <Flex {...controllerContainerStyle}>{controller}</Flex>
    </Grid>
  );

  function renderFreeOptionText() {
    const shouldShowPromoTag =
      isMealItemSelected &&
      freeTag &&
      (freeBreakfastOption || showFreeDinnerLabel || showFreeFoodKids);

    return (
      <>
        {shouldShowPromoTag && (
          <PromoTag rateDiscountTags={freeTag} customStyleName="rateItemStyles" />
        )}
        {showFreeDinnerLabel && !isMealItemDisabled && (
          <Text
            {...freeFoodKidsStyle}
            data-testid={formatDataTestId(prefixDataTestId, 'FreeDinnerOptionPromo')}
          >
            {t('upsell.label.promoText.dinner')}
          </Text>
        )}
        {freeBreakfastOption && showFreeFoodKids && !isMealItemDisabled && !showFreeDinnerLabel && (
          <Text
            {...freeFoodKidsStyle}
            textColor={isMealItemSelected ? 'primary' : 'auto'}
            fontWeight={isMealItemSelected ? 'bold' : 'auto'}
            data-testid={formatDataTestId(prefixDataTestId, 'FreeFoodKids')}
          >
            {t('upsell.label.promoText.freeKidsMeal')}
          </Text>
        )}
      </>
    );
  }

  function renderPrice(
    currentLanguage: string,
    currency: string,
    price: number,
    totalPrice: number,
    numberNights: number
  ) {
    const formattedCurrency = formatCurrency(currency);
    const nightLabel = numberNights === 1 ? t('upsell.label.night') : t('upsell.label.nights');
    const pricePerNight = formatPrice(formattedCurrency, price.toFixed(2), currentLanguage);
    const totalPricePerStay = formatPrice(
      formattedCurrency,
      totalPrice.toFixed(2),
      currentLanguage
    );
    if (isForEntireStay) {
      return `${pricePerNight} ${t('upsell.label.per.adult')}/${t(
        'upsell.label.day'
      )} (${totalPricePerStay} ${t('upsell.label.for')} ${numberNights} ${nightLabel} ${t(
        'upsell.label.for'
      )} ${t('upsell.label.all')} ${t('hoteldetails.bookingsummary.rooms')})`;
    } else {
      return `${pricePerNight} ${t('upsell.label.per.adult')}/${t(
        'upsell.label.day'
      )} (${totalPricePerStay} ${t('upsell.label.for')} ${numberNights} ${nightLabel})`;
    }
  }
}

export const FreeKidsMeal = ({
  isMealItemSelected,
  isMealItemDisabled,
  freeTag,
  freeFoodKidsStyle,
  prefixDataTestId,
  t,
}: FreeKidsMealProps) => {
  return (
    <>
      {isMealItemSelected && freeTag && (
        <PromoTag rateDiscountTags={freeTag} customStyleName="rateItemStyles" />
      )}
      {!isMealItemDisabled && (
        <Text
          {...freeFoodKidsStyle}
          textColor={isMealItemSelected ? 'primary' : 'auto'}
          fontWeight={isMealItemSelected ? 'bold' : 'auto'}
          data-testid={formatDataTestId(prefixDataTestId, 'FreeFoodKids')}
        >
          {t('upsell.label.promoText.freeKidsMeal')}
        </Text>
      )}
    </>
  );
};

export const getMealItemWrapperProps = ({
  wrapperMealItemStyle,
  isMealItemSelected,
  isMealItemDisabled,
  prefixDataTestId,
}: GetMealItemWrapperPropsType): GridWithTestIdProps => ({
  ...wrapperMealItemStyle,
  ...(isMealItemSelected && {
    _hover: {
      backgroundColor: 'var(--chakra-colors-primaryShadow)',
      borderColor: 'var(--chakra-colors-primary)',
    },
  }),
  'data-testid': isMealItemSelected
    ? formatDataTestId(prefixDataTestId, 'Meal-Item-Wrapper-Selected')
    : formatDataTestId(prefixDataTestId, 'Meal-Item-Wrapper'),
  opacity: isMealItemDisabled ? 0.5 : 1,
  pointerEvents: isMealItemDisabled ? 'none' : 'auto',
});

export const FreePrice = ({
  currency,
  basePrice,
  currentLanguage,
  numberNights,
  t,
  priceTextStyle,
  isForEntireStay,
}: FreePriceProps) => {
  const formattedCurrency = formatCurrency(currency as string);

  const originalPrice = formatPrice(formattedCurrency, basePrice?.toFixed(2), currentLanguage);

  const zeroPrice = formatPrice(formattedCurrency, (0).toFixed(2), currentLanguage);

  const nightLabel = numberNights === 1 ? t('upsell.label.night') : t('upsell.label.nights');

  return (
    <Text {...priceTextStyle}>
      <Text as="span" textDecoration="line-through" mr="sm" color="darkGrey2">
        {originalPrice}
      </Text>

      <Text as="span" color="primary" fontWeight="bold" mr="xs">
        {zeroPrice}
      </Text>

      <Text as="span">
        {isForEntireStay
          ? ` ${t('upsell.label.per.adult')}/${t('upsell.label.day')} (${zeroPrice} ${t(
              'upsell.label.for'
            )} ${numberNights} ${nightLabel} ${t('upsell.label.for')} ${t('upsell.label.all')} ${t(
              'hoteldetails.bookingsummary.rooms'
            )})`
          : ` ${t('upsell.label.per.adult')}/${t('upsell.label.day')} (${zeroPrice} ${t(
              'upsell.label.for'
            )} ${numberNights} ${nightLabel})`}
      </Text>
    </Text>
  );
};

const allergyLayoutStyle = (freeBreakfastOption: boolean) => {
  return {
    textDecoration: 'underline',
    color: 'btnSecondaryEnabled',
    pt: { mobile: freeBreakfastOption ? '' : 'sm', md: 0 },
  } as TextProps;
};

const allergyLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;

const allergySemanticTypography = {
  textStyle: 'link-s-regular',
} as TextProps;

const containerInfoStyle = {
  direction: { mobile: 'column', md: 'row' },
  align: { mobile: 'flex-start', md: 'center' },
  mt: { mobile: 'sm', xs: 'sm', sm: 'sm', md: 'md' },
  justify: 'flex-start',
  flexWrap: 'wrap',
  rowGap: 'sm',
  columnGap: 'md',
} as FlexProps;

const containerDescriptionStyle = {
  display: { mobile: 'flex', md: 'flex' },
  pl: { mobile: 0, md: 'lg' },
  pr: { mobile: 0, md: '3xl' },
  flexDir: 'column',
  justifyContent: 'space-between',
} as FlexProps;

const SB_containerDescriptionStyle = {
  display: { mobile: 'none', sm: 'flex' },
  pl: { mobile: 0, md: 'lg' },
  pr: { mobile: 0, md: '3xl' },
  flexDir: 'column',
  justifyContent: 'space-between',
} as FlexProps;

const freeFoodKidsStyle = {
  bgColor: 'tooltipInfo',
  fontSize: 'xs',
  lineHeight: '2',
  color: 'darkGrey1',
  fontWeight: 'medium',
  px: 'sm',
  py: 'xs',
} as TextProps;

const descriptionLayoutStyle = {
  color: 'darkGrey2',
} as TextProps;

const descriptionLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;

const descriptionSemanticTypography = {
  textStyle: 'body-s-regular',
} as TextProps;

const priceLayoutStyle = {
  pt: { mobile: 0, xs: 'xs' },
  color: 'darkGrey1',
} as TextProps;

const priceLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'semibold',
} as TextProps;

const priceSemanticTypography = {
  textStyle: 'body-s-emphasis',
} as TextProps;

const priceTextStyle = {
  ...priceLayoutStyle,
  ...priceLegacyTypography,
} as TextProps;

const titleLayoutStyle = {
  as: 'h4',
  pt: { mobile: 'md', md: '0' },
  color: 'darkGrey1',
} as TextProps;

const titleLegacyTypography = {
  fontSize: 'xl',
  lineHeight: '3',
  fontWeight: 'semibold',
} as TextProps;

const titleSemanticTypography = {
  textStyle: 'heading-s',
} as TextProps;

const controllerContainerStyle = {
  alignItems: 'center',
  justifyContent: 'center',
  pl: { mobile: 0, md: 'md' },
} as FlexProps;

const SB_controllerContainerStyle = {
  direction: 'column',
  justifyContent: 'center',
  pl: { mobile: 0, md: 'md' },
} as FlexProps;

const containerImageStyle = {
  w: { mobile: '100%', md: '9.875rem' },
  h: {
    mobile: '8.75rem',
    xs: '10.938rem',
    sm: '17.875rem',
    md: '8.25rem',
  },
  pos: 'relative',
} as BoxProps;

const SB_containerImageStyle = {
  flexShrink: 0,
  w: { mobile: '5.625rem', xs: '10.938rem', sm: '100%', md: '9.875rem' },
  h: {
    mobile: '5.625rem',
    xs: '10.938rem',
    sm: '17.875rem',
    md: '8.25rem',
  },
  pos: 'relative',
} as BoxProps;

const delimiterStyle = {
  w: { mobile: 'full', md: '1px' },
  h: { mobile: '1px', md: 'full' },
  bgColor: 'var(--chakra-colors-lightGrey3)',
  alignSelf: 'center',
} as BoxProps;

const wrapperMealItemStyle = {
  templateColumns: { mobile: 'auto', md: 'auto 1fr auto auto' },
  templateRows: { mobile: 'auto 1fr auto auto', md: 'auto ' },
  border: '1px solid var(--chakra-colors-lightGrey3)',
  p: 'md',
} as GridProps;
