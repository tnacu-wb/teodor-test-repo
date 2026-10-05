import { Box, Flex, FlexProps, Heading } from '@chakra-ui/react';
import { HeroSectionData } from '@whitbread-eos/api';
import { Breadcrumb } from '@whitbread-eos/atoms';
import { formatAssetsUrl, formatDataTestId, useScreenSize } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import Image from 'next/image';

const CollapseExpandText = dynamic(
  async () => {
    const { CollapseExpandText } = await import('@whitbread-eos/atoms');
    return { default: CollapseExpandText };
  },
  {
    ssr: false,
  }
);

interface Props {
  data: HeroSectionData;
}

export default function HeroSection({ data }: Readonly<Props>) {
  const baseDataTestId = 'DLPHeroSection';
  const { t } = useTranslation();
  const { title, description, picture } = data;
  const { isLessThanLg } = useScreenSize();
  const breadcrumbList = data.breadcrumbs.map((breadcrumb) => ({
    name: breadcrumb.title,
    url: breadcrumb.link,
    isCurrentPage: breadcrumb.link === '',
  }));

  return (
    <Flex {...heroSectionStyle}>
      <Flex
        flexDirection="column"
        {...textContainerStyle}
        w={{ mobile: 'full', md: !picture ? '54rem' : '50%' }}
      >
        <Box
          {...breadcrumbStyles}
          display={{ mobile: !(picture && description) ? 'block' : 'none', md: 'block' }}
        >
          <Breadcrumb items={breadcrumbList} />
        </Box>
        <Heading as="h1" data-testid={formatDataTestId(baseDataTestId, 'Title')} {...titleStyle}>
          {title}
        </Heading>
        {description && getHeroDescription(description.toString(), baseDataTestId, isLessThanLg, t)}
      </Flex>
      {picture && description && (
        <Box {...pictureStyle}>
          <Flex
            flexDirection="column"
            {...{ ...breadcrumbStyles }}
            display={{
              mobile: 'block',
              md: 'none',
            }}
          >
            <Breadcrumb items={breadcrumbList} />
          </Flex>
          <Image
            src={formatAssetsUrl(picture)}
            alt={title}
            width={700}
            height={600}
            sizes="(max-width: 374px) 342px, (min-width: 375px) and (max-width: 575px) 543px, (min-width: 576px) and (max-width: 767px) 700px, (min-width: 768px) 624px"
            objectFit="contain"
            style={{ borderRadius: 'var(--chakra-space-sm)' }}
            data-testid={formatDataTestId(baseDataTestId, 'Picture')}
            priority={true}
          />
        </Box>
      )}
    </Flex>
  );
}

export function getHeroDescription(
  description: string,
  baseDataTestId: string,
  isLessThanLg: boolean | undefined,
  t: (x: string, y?: { [key: string]: string }) => string
) {
  if (isLessThanLg || description.toString().length > 200) {
    return (
      <CollapseExpandText
        startingHeight={185}
        noOfLines={7}
        baseTestId={baseDataTestId}
        expandButtonText={t('hoteldetails.readmore')}
        collapseButtonText={t('hoteldetails.readless')}
        contentText={description}
        isFadeEffect
        isHtml={true}
      />
    );
  }
  return description;
}

const heroSectionStyle = {
  justifyContent: 'space-between',
  flexDirection: { mobile: 'column', md: 'row' },
  align: 'flex-start',
} as FlexProps;

const textContainerStyle = {
  pr: { mobile: '0', md: 'lg' },
  order: { mobile: 2, md: 1 },
};

const breadcrumbStyles = {
  py: 'lg',
  display: {
    mobile: 'none',
    md: 'block',
  },
};

const titleStyle = {
  color: 'tertiary',
  fontSize: { mobile: '2.5rem', md: '3.5rem' },
  fontFamily: 'var(--chakra-fonts-header)',
  fontWeight: 'var(--chakra-fontWeights-black)',
  letterSpacing: { mobile: '0', md: '-1px' },
  lineHeight: { mobile: '2.75rem' },
  mb: { mobile: 0, lg: 'md' },
  mt: { mobile: 'md', lg: '5xl' },
};

const pictureStyle = {
  w: { mobile: 'full', md: '50%' },
  mt: { mobile: 'md', sm: 0 },
  order: { mobile: 1, md: 2 },
  overflow: 'hidden',
  alignSelf: 'baseline',
};
