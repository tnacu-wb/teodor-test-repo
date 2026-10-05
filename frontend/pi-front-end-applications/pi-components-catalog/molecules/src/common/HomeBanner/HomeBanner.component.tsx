import { ArrowDownIcon } from '@chakra-ui/icons';
import { Box, Heading, Text } from '@chakra-ui/react';
import { getStaticContent, SITE_LEISURE } from '@whitbread-eos/api';
import { Icon } from '@whitbread-eos/atoms';
import { formatAssetsUrl, useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import Image from 'next/image';
import { ReactNode } from 'react';

interface HomeBannerProps {
  searchComponent: ReactNode;
}

export default function HomeBanner({ searchComponent }: Readonly<HomeBannerProps>) {
  const { language, country } = useCustomLocale();

  const query = getStaticContent(false);
  const { data: staticContent } = useQueryRequest(['GetStaticContent', language, country], query, {
    country,
    language,
    site: SITE_LEISURE,
    businessBooker: false,
  });

  const heroData = staticContent?.headerInformation?.content?.hero;
  const backgroundImageUrl = heroData?.backgroundImage || null;
  const textShadow = '0px 2px 12px var(--chakra-colors-darkGrey1)';

  const heroWrapperStyles = {
    mt: { base: '-0.5rem' },
    pt: 0,
    mb: 0,
    width: '100%',
    position: 'relative' as const,
    overflow: 'visible',
  };

  const homeBannerStyles = {
    wrapper: {
      position: 'relative' as const,
      width: '100vw',
      height: '100%',
      minHeight: { base: '480px', sm: '380px', md: '455px' },
      maxHeight: { base: 'auto', sm: 'auto', md: '470px' },
      marginLeft: 'calc(-50vw + 50%)',
      overflow: 'visible',
      backgroundColor: heroData?.backgroundColor ? `#${heroData.backgroundColor}` : '#5B2E66',
      backgroundImage: backgroundImageUrl
        ? undefined
        : 'linear-gradient(135deg, #5B2E66 0%, #7B4E7E 50%, #9B6E96 100%)',
      display: 'flex',
      flexDirection: 'column' as const,
      justifyContent: { base: 'space-between' },
      pt: { base: '0.5rem', sm: '2rem', md: '2.5rem' },
      pb: { base: '0.5rem', sm: '1.5rem', md: '2rem' },
      mt: 0,
    },
    imageContainer: {
      position: 'absolute' as const,
      top: 0,
      left: 0,
      right: 0,
      bottom: { base: 'auto', sm: 0 },
      width: '100%',
      height: { base: '183px', sm: '100%' },
      zIndex: 0,
      overflow: 'hidden',
    },
    innerWrapper: {
      position: 'relative' as const,
      zIndex: 1,
      width: '100%',
      maxW: { base: '100%', lg: '1340px' },
      mx: 'auto',
      px: 0,
      display: 'flex',
      flexDirection: 'column' as const,
      justifyContent: { base: 'center', sm: 'space-between' },
      alignItems: { base: 'center', sm: 'stretch' },
      flex: 1,
      gap: { base: 4, sm: 0 },
    },
    contentContainer: {
      display: 'flex',
      flexDirection: 'column' as const,
      justifyContent: 'center',
      alignItems: { base: 'center', sm: 'flex-start' },
      flex: { base: 0, sm: 1 },
      pl: { base: 4, sm: 4, md: 6, lg: 8 },
      py: { base: '0.5rem', sm: '2rem', md: '3rem' },
      width: { base: '100%', sm: '75%', lg: '60%' },
      minHeight: { base: '183px', sm: 'auto' },
    },
    title: {
      color: heroData?.headingFontColor ? `#${heroData.headingFontColor}` : 'white',
      fontSize: { base: '1.8125rem', sm: '2.25rem', lg: '3rem' },
      fontWeight: (heroData?.headingFontWeight as string) || '700',
      fontFamily: 'Proxima Nova Sans, sans-serif',
      textAlign: { base: 'center', sm: 'left' } as const,
      textShadow: heroData?.headingFontShadow ? textShadow : 'none',
      mb: { base: 2, md: 3 },
      lineHeight: { base: '1.2', md: '1.1' },
      letterSpacing: '-0.02em',
    },
    subtitle: {
      color: heroData?.subHeadingFontColor ? `#${heroData.subHeadingFontColor}` : 'white',
      fontSize: { base: '1rem', sm: '1.4375rem', lg: '1.8125rem' },
      fontWeight: (heroData?.subHeadingFontWeight as any) || '400',
      fontFamily: 'Proxima Nova Sans, sans-serif',
      textAlign: { base: 'center', sm: 'left' } as const,
      textShadow: heroData?.subHeadingFontShadow ? textShadow : 'none',
      maxWidth: { base: '100%', md: '1000px' },
      lineHeight: '1.2',
    },
    searchWrapper: {
      width: '100%',
      maxWidth: {
        base: '100%',
        lg: 'var(--chakra-space-breakpoint-lg)',
        xl: 'var(--chakra-space-breakpoint-xl)',
      },
      px: {
        base: '1rem',
        sm: '1.25rem',
        md: '1.5rem',
        lg: '1.75rem',
      },
      mx: 'auto',
      mt: { base: 0, sm: 'auto' },
    },
    bottomCaptionWrapper: {
      display: 'flex',
      flexDirection: 'column' as const,
      alignItems: 'center',
      mt: { mobile: '-100px', xs: '-50px', sm: 6 },
      gap: 2,
    },
    bottomCaptionText: {
      color: { base: 'black', sm: 'white' },
      fontSize: { base: '0.875rem', sm: '1.25rem' },
      fontWeight: '550',
      fontFamily: 'Proxima Nova Sans, sans-serif',
      textAlign: 'center' as const,
      lineHeight: '1.5',
      textShadow: { base: 'none', sm: undefined },
    },
    bottomCaptionArrow: {
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
    },
  };

  return (
    <Box {...heroWrapperStyles}>
      <Box {...homeBannerStyles.wrapper} data-testid="home-banner">
        {backgroundImageUrl && (
          <Box {...homeBannerStyles.imageContainer}>
            <Image
              src={formatAssetsUrl(backgroundImageUrl)}
              alt="Home Banner Background"
              fill
              fetchPriority="high"
              style={{ objectFit: 'cover', objectPosition: 'center' }}
              data-testid="home-banner-image"
              sizes="100vw"
            />
          </Box>
        )}

        <Box {...homeBannerStyles.innerWrapper}>
          <Box {...homeBannerStyles.contentContainer}>
            <Heading as="h1" {...homeBannerStyles.title} data-testid="home-banner-title">
              {heroData?.headingTextShort || heroData?.headingTextLong}
            </Heading>
            {(heroData?.subHeadingShow !== false || !heroData) && (
              <Text {...homeBannerStyles.subtitle} data-testid="home-banner-subtitle">
                {heroData?.subHeadingText}
              </Text>
            )}
          </Box>

          <Box
            {...homeBannerStyles.searchWrapper}
            mb={heroData?.bottomCaptionShow ? 0 : 'auto'}
            sx={{
              '& *': {
                boxShadow: 'none !important',
                marginBottom: heroData?.bottomCaptionShow ? 0 : undefined,
              },
            }}
            data-testid="home-banner-searchWrapper"
          >
            {searchComponent}
          </Box>

          {heroData?.bottomCaptionShow && (
            <Box {...homeBannerStyles.bottomCaptionWrapper}>
              <Text
                {...homeBannerStyles.bottomCaptionText}
                textShadow={{
                  base: 'none',
                  sm: heroData?.bottomCaptionShadow ? textShadow : 'none',
                }}
                data-testid="home-banner-bottomCaptionText"
              >
                {heroData?.bottomCaptionText}
              </Text>
              {heroData?.bottomCaptionArrowShow && (
                <Box
                  {...homeBannerStyles.bottomCaptionArrow}
                  data-testid="home-banner-bottomCaptionArrow"
                >
                  <Icon color={{ base: 'black', sm: 'white' }} as={ArrowDownIcon} boxSize={5} />
                </Box>
              )}
            </Box>
          )}
        </Box>
      </Box>
    </Box>
  );
}
