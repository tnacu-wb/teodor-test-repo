import { Box, Flex, FlexProps, Heading, Image, Text } from '@chakra-ui/react';
import { Promo } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms';
import { formatAssetsUrl, formatDataTestId } from '@whitbread-eos/utils';

interface Props {
  data: Promo[];
  baseDataTestId: string;
}

const MAX_NUMBER_OF_CARDS = 2;

export default function TravelGuides({ data, baseDataTestId }: Readonly<Props>) {
  const sortedCards = data?.sort((a: Promo, b: Promo) => (a.order ?? 0) - (b.order ?? 0));

  return (
    <Flex
      data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
      {...travelGuidesContainerStyles}
      w={data.length < MAX_NUMBER_OF_CARDS ? { mobile: 'full', xl: 'calc(50% - 12px)' } : undefined}
    >
      {sortedCards?.map((item) => (
        <Flex
          key={item.title}
          data-testid={formatDataTestId(baseDataTestId, 'Card')}
          {...cardStyles}
          w={
            data.length < MAX_NUMBER_OF_CARDS
              ? { mobile: 'full', lg: 'calc(50% - 12px)', xl: 'full' }
              : { mobile: 'full' }
          }
        >
          <Flex justifyContent="space-between" flexDirection="column" {...cardInnerStyles}>
            <Box>
              <Heading
                as="h4"
                data-testid={formatDataTestId(baseDataTestId, 'Title')}
                {...titleStyles}
              >
                {item.title}
              </Heading>
              <Text
                {...descriptionStyles}
                data-testid={formatDataTestId(baseDataTestId, 'Description')}
              >
                {item.description}
              </Text>
            </Box>
            <Button
              variant="primary"
              size="sm"
              formTarget={item.linkTarget}
              data-testid={formatDataTestId(baseDataTestId, 'Button')}
              onClick={() => {
                window.location.href = item.linkUrl ?? '';
              }}
              {...buttonStyles}
            >
              {item.linkText}
            </Button>
          </Flex>
          <Box
            {...imageStyles}
            data-testid={formatDataTestId(baseDataTestId, 'Image')}
            {...cardInnerStyles}
          >
            <Image
              src={formatAssetsUrl(item.picture ?? '')}
              alt={item.title}
              borderTopRadius="base"
              {...imageStyle}
            />
          </Box>
        </Flex>
      ))}
    </Flex>
  );
}

const travelGuidesContainerStyles = {
  flexDirection: {
    mobile: 'column',
    sm: 'column',
    lg: 'row',
  },
  mb: '2xl',
  gap: 'lg',
  minH: { mobile: '292px', lg: '0' },
} as FlexProps;

const cardStyles = {
  flexDirection: {
    mobile: 'column-reverse',
    sm: 'row',
  },
  borderWidth: '1px',
  borderRadius: 'lg',
  borderColor: 'var(--chakra-colors-lightGrey4)',
  padding: { mobile: 'md', lg: 'var(--chakra-space-md) var(--chakra-space-lg)' },
  gap: 'lg',
} as FlexProps;

const titleStyles = {
  color: 'tertiary',
  fontSize: 'var(--chakra-fontSizes-3xxl)',
  fontFamily: 'var(--chakra-fonts-header)',
  fontWeight: 'var(--chakra-fontWeights-black)',
  lineHeight: { mobile: '2.75rem' },
  mb: { mobile: 0, lg: 'md' },
  mt: { mobile: '0' },
};

const buttonStyles = {
  padding: 'var(--chakra-space-sm) var(--chakra-space-md)',
  mb: 'md',
  w: 'full',
  h: 'auto',
  whiteSpace: 'normal',
  mt: { mobile: 'md', lg: '0' },
};

const descriptionStyles = {
  mb: { lg: '20px' },
};

const imageStyles = {
  minW: { mobile: '16rem', sm: '13.563rem' },
  height: { mobile: '13.75rem', sm: '15.375rem', md: '16.25rem' },
  bgColor: 'lightGrey4',
  borderRadius: 'base',
};

const imageStyle = {
  width: '100%',
  height: '100%',
  objectFit: 'cover' as const,
};

const cardInnerStyles = {
  width: {
    mobile: 'full',
    lg: '50%',
  },
} as FlexProps;
