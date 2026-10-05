import { Box, Checkbox, Flex, Text } from '@chakra-ui/react';
import { SoftBundles, SoftBundle, Currency } from '@whitbread-eos/api';
import { Icon, Info } from '@whitbread-eos/atoms';
import { analytics, formatAssetsUrl, formatPrice } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import { BundleSideDrawer } from '../../SideDrawer';
import { attachmentLink } from '../BundleChoice.component';

type Props = {
  isActive: boolean;
  isDisabled: boolean;
  bundle: SoftBundles;
  onClick: (e: React.MouseEvent<HTMLDivElement>, bundle: any) => void;
  testId: string | number;
  isClassVariant?: boolean;
  currencySymbol: string;
  language: string;
  nights: number;
  adultsNumber: number;
};

export const BundleExtra = ({
  bundle,
  isActive,
  isDisabled,
  onClick,
  testId,
  isClassVariant = true,
  currencySymbol,
  language,
  nights,
  adultsNumber,
}: Readonly<Props>) => {
  const [isBundleSideDrawerVisible, setIsBundleSideDrawerVisible] = useState(false);
  const [bundleSideDrawerPacks, setBundleSideDrawerPacks] = useState<any[]>([]);
  const isIncluded = !bundle.isOptional;

  const isChecked = isActive || isIncluded;

  const currency = currencySymbol === Currency.EUR ? Currency.EUR_NAME : Currency.GBP_NAME;
  const bundleStyle = {
    ...bundleExtraStyle,
    ...(isChecked && checkedBundleStyle),
    ...(isDisabled && disabledBundlestyle),
    ...(isClassVariant && { mt: '0.5rem' }),
  };
  const { t } = useTranslation();

  const bundleDescriptionList = (bundle.softBundleContent ?? []).map((pack: any, index: number) => {
    return (
      <Text
        as="span"
        textDecoration={pack.strikeThrough && 'line-through'}
        key={pack.id}
        fontSize="sm"
        data-testid="Bundle-Extras-Description-List-Item"
      >
        {pack.name ?? pack.description}
        {index < (bundle.softBundleContent?.length ?? 0) - 1 && ', '}
      </Text>
    );
  });

  const bundleFormattedPrice = formatPrice(
    currencySymbol,
    getCorrectBundlePrice(bundle.softBundleContent ?? [], adultsNumber, nights).toFixed(2),
    language
  );

  const handleMealInfoClick = (event: any) => {
    event.stopPropagation();
    analytics.update({
      ...window.analyticsData,
      bundleInfoSelected: true,
      bundleInfo: (bundle.softBundleContent ?? []).map((c) => ({
        id: c.id,
        description: c.description,
        price: (c.price ?? 0) * adultsNumber * nights,
      })),
    });

    setIsBundleSideDrawerVisible(true);
    setBundleSideDrawerPacks(
      bundle?.softBundleContent?.map((extra: SoftBundle) => ({
        name: extra.name,
        description: extra.description,
        price: bundle.isOptional ? extra.price : 0,
        image: formatAssetsUrl(extra?.imageSrc ?? ''),
        links: extra?.attachments?.map((attachment: any) =>
          attachmentLink(attachment.path, attachment.label, `Side-Drawer-Attachment-Link`)
        ),
      })) ?? []
    );
  };

  useEffect(() => {
    analytics.update({
      ...window.analyticsData,
      bundleSelected: isActive,
    });
  }, [isActive]);

  return (
    <>
      <Flex
        {...bundleStyle}
        onClick={(e) => (isDisabled ? null : onClick(e, bundle))}
        data-testid={isIncluded ? `Included-Bundle-${testId}` : `Bundle-${testId}`}
      >
        <Checkbox
          data-testid="Bundle-Checkbox"
          isChecked={isChecked}
          pointerEvents="none"
          alignItems="flex-start"
          sx={{
            '.chakra-checkbox': {
              alignItems: 'flex-start',
            },
          }}
        />
        <Flex
          display="flex"
          direction={isClassVariant ? { sm: 'row', mobile: 'column' } : 'column'}
          alignItems={isClassVariant ? { sm: 'center', mobile: 'initial' } : 'initial'}
          width="100%"
          flexWrap={isClassVariant ? { sm: 'wrap' } : undefined}
          justifyContent={isClassVariant ? undefined : 'space-between'}
        >
          <Flex
            ml="0.75rem"
            display={isClassVariant ? 'inline-block' : undefined}
            flexWrap={isClassVariant ? { md: 'wrap' } : undefined}
            alignItems="center"
          >
            {isClassVariant ? (
              <>
                <Text
                  fontSize="md"
                  fontWeight="semibold"
                  color="darkGrey1"
                  data-testid="Bundle-Add-Extras-Text"
                  display={{ mobile: 'inline-flex', md: undefined }}
                  alignItems={{ mobile: 'center', md: undefined }}
                >
                  {t('hoteldetails.rates.bundles.addExtras')}
                  <Icon svg={<Info />} onClick={(e) => handleMealInfoClick(e)} mx="xs" />
                </Text>
                <Box
                  display="inline"
                  data-testid="Bundle-Extras-Description-List"
                  ml={{ md: 'sm' }}
                >
                  ({bundleDescriptionList})
                </Box>
              </>
            ) : (
              <Box display="inline" data-testid="Bundle-Extras-Description-List">
                {bundleDescriptionList}
              </Box>
            )}
          </Flex>
          {!isIncluded && (
            <Flex
              fontSize="sm"
              fontWeight="semibold"
              color="darkGrey1"
              ml="auto"
              marginTop={isClassVariant ? { md: 0, mobile: '1.031rem' } : '0.5rem'}
            >
              <Text data-testid="Bundle-Extras-Total-Price">
                {`${isClassVariant ? '(' : ''}+${bundleFormattedPrice}${isClassVariant ? ')' : ''}`}
              </Text>
            </Flex>
          )}
        </Flex>
      </Flex>
      <BundleSideDrawer
        visible={isBundleSideDrawerVisible}
        onClose={() => setIsBundleSideDrawerVisible(false)}
        currency={currency}
        language={language}
        numberOfNights={nights}
        packages={bundleSideDrawerPacks}
        adultsNumber={adultsNumber}
      />
    </>
  );
};

export const getCorrectBundlePrice = (bundle: SoftBundle[], adults: number, nights: number) => {
  const calculatePackPrice = (pack: SoftBundle): number => {
    const basePrice = pack?.price ?? 0;

    switch (pack.id) {
      case 'BFADBF':
      case 'BBIB':
        return basePrice * adults * nights;
      case 'FI24HR':
        return basePrice * nights;
      default:
        return pack?.strikeThrough ? 0 : basePrice;
    }
  };

  return bundle.reduce((total, pack) => total + calculatePackPrice(pack), 0);
};

const bundleExtraStyle = {
  width: 'full',
  height: 'full',
  backgroundColor: 'lightGrey5',
  p: '1.5rem',
  pt: { mobile: '1.125rem', md: '1.5rem' },
  pb: { mobile: 'xmd', md: '1.5rem' },
  px: { mobile: 'xmd', md: 'lg' },
  mt: '0.5rem',
  borderRadius: '4px',
  sx: {
    backgroundImage: `
    repeating-linear-gradient(
      0deg,
      var(--chakra-colors-lightGrey1),
      var(--chakra-colors-lightGrey1) 5px,
      transparent 5px,
      transparent 10px,
      var(--chakra-colors-lightGrey1) 10px
    ),
    repeating-linear-gradient(
      90deg,
      var(--chakra-colors-lightGrey1),
      var(--chakra-colors-lightGrey1) 5px,
      transparent 5px,
      transparent 10px,
      var(--chakra-colors-lightGrey1) 10px
    ),
    repeating-linear-gradient(
      180deg,
      var(--chakra-colors-lightGrey1),
      var(--chakra-colors-lightGrey1) 5px,
      transparent 5px,
      transparent 10px,
      var(--chakra-colors-lightGrey1) 10px
    ),
    repeating-linear-gradient(
      270deg,
      var(--chakra-colors-lightGrey1),
      var(--chakra-colors-lightGrey1) 5px,
      transparent 5px,
      transparent 10px,
      var(--chakra-colors-lightGrey1) 10px
    )
  `,
    backgroundSize: '1px 100%, 100% 1px, 1px 100% , 100% 1px',
    backgroundPosition: '0 0, 0 0, 100% 0, 0 100%',
    backgroundRepeat: 'no-repeat',
  },
  cursor: 'pointer',
};

const checkedBundleStyle = {
  outline: '1px solid var(--chakra-colors-primary)',
  sx: {
    backgroundImage: 'none',
    backgroundSize: 0,
    backgroundPosition: '0 0, 0 0, 100% 0, 0 100%',
    backgroundRepeat: 'no-repeat',
  },
  backgroundColor: 'primaryShadow',
};

const disabledBundlestyle = {
  opacity: '0.5',
};
