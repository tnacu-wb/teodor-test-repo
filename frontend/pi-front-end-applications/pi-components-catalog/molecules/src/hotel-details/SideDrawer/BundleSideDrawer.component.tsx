import { CheckIcon } from '@chakra-ui/icons';
import { Flex, FlexProps, Box, Text, Image, ImageProps, ResponsiveValue } from '@chakra-ui/react';
import { ExternalLink, Dismiss, Icon } from '@whitbread-eos/atoms';
import { formatCurrency, formatPrice, renderSanitizedHtml } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React from 'react';

type Pack = {
  name?: string;
  price?: number;
  links?: Array<React.ReactNode>;
  description?: string;
  image?: string;
};

type Props = {
  packages: Array<Pack>;
  visible: boolean;
  onClose: () => void;
  numberOfNights: number;
  currency: string;
  language: string;
  adultsNumber: number;
};

export const BundleSideDrawer = ({
  packages,
  visible,
  onClose,
  numberOfNights,
  currency,
  language,
  adultsNumber,
}: Readonly<Props>) => {
  const isSinglePackage = packages?.length === 1;

  const linksContainer = (links: Array<React.ReactNode> | undefined) => {
    return (
      !!links?.length && (
        <Flex
          flexDirection={{ mobile: 'column', sm: 'row' }}
          gap="1rem"
          mt="1rem"
          data-testid="Side-Drawer-Attachment-Links"
          pt={{ mobile: 'sm', md: 0 }}
        >
          {links.map((link) => (
            <Flex>
              {link}
              <Icon ml="4px" display="flex" alignItems="center" svg={<ExternalLink />} />
            </Flex>
          ))}
        </Flex>
      )
    );
  };

  const { t } = useTranslation(['common']);
  const nightLabel = numberOfNights === 1 ? t('upsell.label.night') : t('upsell.label.nights');

  const packagesContent = () => {
    const returnSinglePackage = () => {
      const singlePackage = packages?.[0];
      const formattedCurrency = formatCurrency(currency);
      const packagePrice = singlePackage?.price ?? 0;
      const pricePerNight = formatPrice(formattedCurrency, packagePrice.toFixed(2), language);
      const totalPriceForAllNights = formatPrice(
        formattedCurrency,
        (packagePrice * numberOfNights * (adultsNumber ?? 1)).toFixed(2),
        language
      );

      return (
        <>
          <Image
            src={singlePackage?.image}
            alt={singlePackage?.name}
            {...headerImageContainerStyle}
            data-testid="Side-Drawer-Single-Image"
          />
          <Flex {...singleContentContainer}>
            <Flex display="flex">
              <Text {...singleExtraTitleStyle} data-testid="Side-Drawer-Single-Name">
                {singlePackage?.name}
              </Text>
              {!!packagePrice && (
                <Flex display="flex" flexDirection="column" marginLeft="auto">
                  <Text
                    textAlign="right"
                    fontWeight="bold"
                    color="var(--chakra-colors-darkGrey1)"
                    data-testid="Side-Drawer-Single-Price"
                  >
                    {pricePerNight}
                  </Text>
                  <Text
                    textAlign="right"
                    fontWeight="semibold"
                    color="var(--chakra-colors-darkGrey1)"
                    data-testid="Side-Drawer-Single-Per-Text"
                  >
                    {`${t('upsell.label.per.adult')}/${t('upsell.label.day')}`}
                  </Text>
                  <Text
                    textAlign="right"
                    fontWeight="semibold"
                    color="var(--chakra-colors-darkGrey2)"
                    data-testid="Side-Drawer-Single-Total-Price"
                  >
                    {`${totalPriceForAllNights} ${t(
                      'upsell.label.for'
                    )} ${numberOfNights} ${nightLabel}`}
                  </Text>
                </Flex>
              )}
            </Flex>
            {linksContainer(singlePackage?.links)}
            <Box marginTop="1.5rem" data-testid="Side-Drawer-Single-Description">
              {renderSanitizedHtml(singlePackage?.description ?? '')}
            </Box>
          </Flex>
        </>
      );
    };

    return isSinglePackage ? (
      returnSinglePackage()
    ) : (
      <Flex {...multipleExtrasDrawerContainer}>
        <Text
          fontWeight="semibold"
          color="var(--chakra-colors-darkGrey1)"
          fontSize={{ mobile: 'xl', sm: '2xl' }}
          marginRight="3rem"
          lineHeight="1.5rem"
          data-testid="Side-Drawer-Multiple-Title"
        >
          {'Extras package'}
        </Text>
        <Flex {...multipleExtrasListContainer}>
          <Box {...cornerTriangle} key="checkTriangle">
            <CheckIcon {...checkIconStyle} />
          </Box>
          {packages?.map((extra: Pack, index: number) => {
            const isLastInList = index === packages?.length - 1;
            return (
              <>
                <Flex
                  display="flex"
                  flexDirection={{ mobile: 'column', sm: 'row' }}
                  key={extra?.name}
                  data-testid="Side-Drawer-Multiple-Item-Wrapper"
                >
                  <Image
                    src={extra?.image}
                    {...extraImageStyle}
                    data-testid="Side-Drawer-Multiple-Item-Image"
                  />
                  <Flex
                    flexDirection="column"
                    marginLeft={{ mobile: '0', sm: '1.5rem' }}
                    marginTop={{ mobile: '1.5rem', sm: '0' }}
                  >
                    <Text
                      fontWeight="bold"
                      fontSize="xl"
                      color="var(--chakra-colors-darkGrey1)"
                      data-testid="Side-Drawer-Multiple-Item-Name"
                    >
                      {extra?.name}
                    </Text>
                    <Box data-testid="Side-Drawer-Multiple-Item-Description">
                      {renderSanitizedHtml(extra?.description ?? '')}
                    </Box>
                    {linksContainer(extra?.links)}
                  </Flex>
                </Flex>
                {!isLastInList && <Box {...separatorStyle} key={`${extra?.name}-separator`} />}
              </>
            );
          })}
        </Flex>
      </Flex>
    );
  };

  return (
    visible && (
      <>
        <Flex
          className="drawer-overlay"
          {...overlayStyle}
          onClick={onClose}
          data-testid="Side-Drawer-Backdrop"
        />
        <Flex
          className="side-drawer"
          {...drawerStyle}
          right={{ mobile: 0, sm: '0' }}
          left={{ mobile: 0, sm: 'unset' }}
          bottom={{ mobile: '0', sm: 0 }}
          top={{ mobile: 'unset', sm: 0 }}
          width={{ mobile: '100%', sm: isSinglePackage ? 'unset' : '50rem' }}
          maxWidth="100%"
          height={{ mobile: '90dvh', sm: '100dvh' }}
          data-testid="Side-Drawer-Container"
        >
          <Box {...closeButtonStyle} onClick={onClose} data-testid="Dismiss-Side-Drawer">
            <Icon svg={<Dismiss transform="scale(1.1)" />} />
          </Box>

          {packagesContent()}
        </Flex>
      </>
    )
  );
};

const checkIconStyle = {
  position: 'absolute' as ResponsiveValue<'absolute'>,
  top: '10px',
  right: '10px',
  color: 'var(--chakra-colors-white)',
};

const cornerTriangle = {
  position: 'absolute' as ResponsiveValue<'absolute'>,
  top: '-1px',
  right: '-1px',
  width: '4rem',
  height: '4rem',
  backgroundColor: 'var(--chakra-colors-tertiary)',
  clipPath: 'polygon(0 0, 100% 0, 100% 100%)',
  borderTopRightRadius: '0.5rem',
} as const;

const separatorStyle = {
  height: '1px',
  width: '100%',
  my: '1.5rem',
  backgroundColor: 'var(--chakra-colors-lightGrey3)',
};

const extraImageStyle = {
  width: { sm: '9.063rem', mobile: '100%' },
  height: { sm: '9.375rem', mobile: '7.813rem' },
  objectFit: 'cover',
} as ImageProps;

const multipleExtrasListContainer = {
  position: 'relative',
  display: 'flex',
  flexDirection: 'column',
  width: '100%',
  border: '3px solid var(--chakra-colors-tertiary)',
  padding: '1.5rem',
  borderRadius: '0.5rem',
  marginTop: '1rem',
} as FlexProps;

const multipleExtrasDrawerContainer = {
  display: 'flex',
  flexDirection: 'column',
  px: {
    mobile: '1rem',
    sm: '1.25rem',
    md: '1.5rem',
    lg: '1.75rem',
    xl: '4.125rem',
  },
  pb: '1rem',
  paddingTop: '2rem',
  overflow: 'auto',
} as FlexProps;

const closeButtonStyle = {
  position: 'absolute',
  right: {
    mobile: '1rem',
    sm: '1.25rem',
    md: '1.5rem',
    lg: '1.75rem',
    xl: '4.125rem',
  },
  top: '2rem',
  cursor: 'pointer',
  borderRadius: '100%',
  backgroundColor: '#fff',
  width: '1.5rem',
  height: '1.5rem',
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  zIndex: 100,
} as FlexProps;

const overlayStyle = {
  position: 'fixed',
  top: 0,
  right: 0,
  bottom: 0,
  left: 0,
  backgroundColor: '#333333',
  zIndex: 998,
  opacity: '75%',
} as FlexProps;

const drawerStyle = {
  position: 'fixed',
  height: '100dvh',
  flexDirection: 'column',
  backgroundColor: '#fff',
  transition: 'all 0.3s ease-in-out',
  zIndex: 999,
  borderRadius: { mobile: '1rem 1rem 0 0', sm: '0' },
} as FlexProps;

const headerImageContainerStyle = {
  display: 'flex',
  height: '200px',
  borderRadius: { mobile: '1rem 1rem 0 0', sm: '0' },
  objectFit: 'cover',
  width: '100%',
} as ImageProps;

const singleContentContainer = {
  display: 'flex',
  flexDirection: 'column',
  px: {
    mobile: '1rem',
    sm: '1.25rem',
    md: '1.5rem',
    lg: '1.75rem',
    xl: '4.125rem',
  },
  width: {
    sm: '22.25rem',
  },
  boxSizing: 'content-box',
  paddingTop: '2rem',
  border: '1px solid var(--chakra-colors-lightGrey3)',
  flexGrow: 1,
  overflow: 'auto',
} as FlexProps;

const singleExtraTitleStyle = {
  fontWeight: 'semibold',
  color: 'var(--chakra-colors-darkGrey1)',
};
