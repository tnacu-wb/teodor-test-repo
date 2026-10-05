import type { BoxProps, FlexProps, TextProps } from '@chakra-ui/react';
import { Box, Flex, Text } from '@chakra-ui/react';
import { WIFI_IDS, SelectedExtrasPackage } from '@whitbread-eos/api';
import { Button, PromoTag } from '@whitbread-eos/atoms';
import {
  isStringValid,
  renderSanitizedHtml,
  formatAssetsUrl,
  formatCurrency,
  formatDataTestId,
  formatPrice,
  useCustomLocale,
  addOrRemoveExtrasRoom,
  addOrRemoveAllRoomsExtras,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import Image from 'next/image';

interface Props {
  currency?: string;
  description?: string;
  id?: string;
  imageSrc?: string;
  name?: string;
  order?: number;
  price?: number;
  isRemovable?: boolean;
  isAvailable?: boolean;
  selectedRoom: number;
  selectedExtrasList: SelectedExtrasPackage[] | undefined;
  handleSelectedExtrasList?: (updatedExtrasItemsList: SelectedExtrasPackage[] | undefined) => void;
  noNights: number;
  allRooms?: boolean;
  numberOfRooms?: number;
  isFree?: boolean | null;
  promoText?: string | null;
}

interface FreeTagComponentProps {
  isFree: boolean;
  descriptionTextStyle: BoxProps;
  prefixDataTestId: string;
  formattedName: string;
  freeTag: string;
}

export const FreeTagComponent = ({
  isFree,
  descriptionTextStyle,
  prefixDataTestId,
  formattedName,
  freeTag,
}: FreeTagComponentProps) => {
  if (!isFree) return null;

  return (
    <Box
      {...descriptionTextStyle}
      pt="xs"
      data-testid={formatDataTestId(prefixDataTestId, `Free-tag-${formattedName}`)}
    >
      <PromoTag rateDiscountTags={[freeTag]} customStyleName="rateItemStyles" />
    </Box>
  );
};

export default function ExtrasItemComponent({
  currency,
  description,
  id,
  imageSrc,
  name,
  price,
  handleSelectedExtrasList,
  isRemovable,
  isAvailable,
  selectedExtrasList,
  selectedRoom,
  noNights,
  allRooms,
  numberOfRooms,
  isFree = false,
  promoText = '',
}: Readonly<Props>) {
  const { language } = useCustomLocale();
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const prefixDataTestId = `Extras-Item${allRooms ? '-allRooms' : ''}`;
  const freeTag = isFree ? promoText : '';

  const allRoomsExtrasPrice = allRooms && price && numberOfRooms ? price * numberOfRooms : price;

  const formattedCurrency = formatCurrency(currency as string);
  const formattedPrice = formatPrice(formattedCurrency, allRoomsExtrasPrice?.toFixed(2), language);
  const formattedDayPrice = formatPrice(
    formattedCurrency,
    ((price as number) / noNights).toFixed(0)
  );
  const formattedName = name?.replace(' ', '-');

  const isUnavailable = allRooms ? !isAvailable : !isRemovable && !isAvailable;
  const buttonType = isRemovable ? t('upsell.extras.remove') : t('upsell.extras.add');
  const buttonName = isUnavailable ? t('hoteldetails.rates.notavailable') : buttonType;

  const buttonVariant = isRemovable && !isUnavailable ? 'tertiary' : 'secondary';

  const extrasImage = isStringValid(imageSrc as string) ? formatAssetsUrl(imageSrc as string) : '';

  let unavailableColor = 'darkGrey1';
  if (isFree) {
    unavailableColor = 'primary';
  } else if (isUnavailable) {
    unavailableColor = 'lightGrey1';
  }
  const shouldButtonDisplay = !isFree && Boolean(handleSelectedExtrasList);

  return (
    <Flex
      {...containerStyle}
      {...getFreeStyles(isFree as boolean)}
      data-testid={formatDataTestId(prefixDataTestId, `Wrapper-${formattedName}`)}
    >
      <Flex {...wrapperImageTitle}>
        {imageSrc && (
          <Box
            {...containerImageStyle}
            data-testid={formatDataTestId(prefixDataTestId, `Image-${formattedName}`)}
          >
            {
              <Image
                style={{ height: 48 }}
                width={48}
                height={48}
                sizes="48px"
                alt="Extras image"
                src={extrasImage}
              />
            }
          </Box>
        )}
        <Flex {...containerDescriptionStyle}>
          <Text
            {...titleLayoutStyle}
            {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
            data-testid={formatDataTestId(prefixDataTestId, `Title-${formattedName}`)}
          >
            {name}
          </Text>
          {WIFI_IDS.includes(id as (typeof WIFI_IDS)[number]) && (
            <Text
              {...subtitleLayoutStyle}
              {...getTypographyProps(subtitleLegacyTypography, subtitleSemanticTypography)}
              data-testid={formatDataTestId(prefixDataTestId, `Subtitle-${formattedName}`)}
            >
              {formattedDayPrice} {t('ancillaries.extras.perRoomPerDay')}
            </Text>
          )}
          <Box
            {...descriptionTextStyle}
            pt="xs"
            {...getTypographyProps(descriptionLegacyTypography, descriptionSemanticTypography)}
            data-testid={formatDataTestId(prefixDataTestId, `Description-${formattedName}`)}
          >
            {renderSanitizedHtml(description ?? '')}
          </Box>
          <FreeTagComponent
            isFree={isFree as boolean}
            descriptionTextStyle={descriptionTextStyle}
            prefixDataTestId={prefixDataTestId}
            formattedName={formattedName as string}
            freeTag={freeTag as string}
          />
        </Flex>
      </Flex>
      <Flex {...buttonAndPriceContainerStyle}>
        <Text
          color={unavailableColor}
          mb={allRooms ? '0' : 'sm'}
          {...getTypographyProps(priceLegacyTypography, priceSemanticTypography)}
          data-testid={formatDataTestId(prefixDataTestId, `Price-${formattedName}`)}
        >
          {formattedPrice}
        </Text>
        {allRooms && (
          <Text
            {...allRoomsTextStyle}
            data-testid={formatDataTestId(prefixDataTestId, `TotalAllRoomsLabel-${formattedName}`)}
          >
            {t('ancillaries.extras.total.text')}
          </Text>
        )}
        {shouldButtonDisplay && (
          <Button
            {...buttonAddStyle}
            variant={buttonVariant}
            data-testid={formatDataTestId(prefixDataTestId, `ButtonAdd-${formattedName}`)}
            onClick={() =>
              handleSelectedExtrasList?.(
                allRooms
                  ? addOrRemoveAllRoomsExtras(
                      id as string,
                      price as number,
                      selectedExtrasList,
                      isRemovable
                    )
                  : addOrRemoveExtrasRoom(
                      id as string,
                      price as number,
                      selectedExtrasList,
                      selectedRoom
                    )
              )
            }
            isDisabled={isUnavailable}
          >
            <Text
              as="span"
              {...getTypographyProps(buttonAddLegacyTypography, buttonAddSemanticTypography)}
            >
              {buttonName}
            </Text>
          </Button>
        )}
      </Flex>
    </Flex>
  );
}

const getFreeStyles = (isFree: boolean): FlexProps => ({
  _hover: isFree
    ? {
        backgroundColor: 'var(--chakra-colors-primaryShadow)',
        borderColor: 'var(--chakra-colors-primary)',
      }
    : {},
});

const containerDescriptionStyle = {
  pl: { mobile: 0, md: 'lg' },
  pr: { mobile: 0, md: '3xl', sm: 'xs' },
  flexDir: 'column',
  justifyContent: 'center',
} as FlexProps;

const descriptionTextStyle = {
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

const containerStyle = {
  justifyContent: 'center',
  alignItems: 'center',
  flexDirection: { mobile: 'column', sm: 'row' },
  border: '1px solid var(--chakra-colors-lightGrey3)',
  padding: 'md',
  mt: 'xl',
} as FlexProps;

const priceLegacyTypography = {
  fontSize: 'md',
  lineHeight: '1.375rem',
  fontWeight: 'semibold',
} as TextProps;

const priceSemanticTypography = {
  textStyle: 'heading-s',
} as TextProps;

const titleLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const titleLegacyTypography = {
  fontSize: '1.25rem',
  lineHeight: '1.5rem',
  fontWeight: 'semibold',
} as TextProps;

const titleSemanticTypography = {
  textStyle: 'heading-s',
} as TextProps;

const subtitleLayoutStyle = {
  color: 'darkGrey1',
} as TextProps;

const subtitleLegacyTypography = {
  fontSize: '0.875rem',
  lineHeight: '1.05rem',
  fontWeight: 'semibold',
} as TextProps;

const subtitleSemanticTypography = {
  textStyle: 'body-s-emphasis',
} as TextProps;

const buttonAndPriceContainerStyle = {
  flexDirection: { mobile: 'row', sm: 'column' },
  alignItems: { mobile: 'center', sm: 'flex-end' },
  alignSelf: 'stretch',
  justifyContent: { mobile: 'space-between', sm: 'center' },
  mt: { mobile: '.5rem', sm: 'xs' },
  paddingLeft: { mobile: '1rem', sm: 0 },
  paddingRight: { mobile: '1rem', sm: 0 },
} as FlexProps;

const buttonAddStyle = { width: '8rem', height: '2.5rem' };

const buttonAddLegacyTypography = {} as TextProps;

const buttonAddSemanticTypography = {
  textStyle: 'label-l',
} as TextProps;

const wrapperImageTitle = {
  w: { mobile: '100%' },
  gap: 'lg',
  flex: '1 0 0',
  flexDirection: { mobile: 'column', sm: 'row' },
} as FlexProps;

const containerImageStyle = {
  position: 'relative',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  w: { mobile: 'auto', sm: '6.5rem' },
  h: { mobile: '6rem', sm: '6.5rem' },
  backgroundColor: 'lightGrey5',
} as BoxProps;

const allRoomsTextStyle = {
  fontSize: 'xxs',
  lineHeight: 'lg',
  mb: 'sm',
} as TextProps;
