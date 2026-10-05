import { Box, Flex, FlexProps, Heading, Image, Link, Text } from '@chakra-ui/react';
import { DlpItem, ToDoItem } from '@whitbread-eos/api';
import { Card } from '@whitbread-eos/atoms';
import { formatAssetsUrl, formatDataTestId, useScreenSize } from '@whitbread-eos/utils';

interface Props {
  title: string;
  items: DlpItem[] | ToDoItem[];
  baseDataTestId: string;
}

const MAX_CARDS_NUMBER = 4;
const MIN_CARDS_NUMBER = 2;

export default function DestinationCarousel({ title, items, baseDataTestId }: Readonly<Props>) {
  const { isLessThanXl } = useScreenSize();

  const isFadeEffect = items.length > MAX_CARDS_NUMBER || isLessThanXl;

  if (items.length < MIN_CARDS_NUMBER) return;

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Heading as="h2" {...headingStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
        {title}
      </Heading>
      <Flex
        {...{
          ...destinationCarouselContainerStyles,
        }}
        sx={{
          ...(isFadeEffect && {
            '&::after': { ...fadeEffectOverlayStyles },
          }),
        }}
        data-testid={formatDataTestId(baseDataTestId, 'List')}
      >
        {items.map((item: DlpItem | ToDoItem) => (
          <DestinationCard
            key={item.title}
            destination={item}
            data-testid={formatDataTestId(baseDataTestId, 'Card')}
            cardLength={items.length}
          />
        ))}
      </Flex>
    </Box>
  );
}

function DestinationCard({ destination, cardLength }: any) {
  const { wrapperStyles, cardContainerStyles, imageStyle } = getCardStyle(cardLength);

  return (
    <Box {...wrapperStyles}>
      <Link href={destination.link} {...linkStyles}>
        <Card {...cardContainerStyles}>
          <Image
            alt={destination.title}
            src={formatAssetsUrl(destination.picture)}
            {...imageStyle}
          />
          <Text {...cardTitleStyles}>{destination.title}</Text>
        </Card>
      </Link>
    </Box>
  );
}

function getCardStyle(cardLength: number) {
  const baseStyles = {
    wrapperStyles: {
      w: { mobile: '19.313rem', sm: '18.375rem', xl: '19.313rem' },
    },
    cardContainerStyles: {
      bgColor: 'baseWhite',
      display: 'flex',
      direction: 'column',
      minH: '11.125rem',
      border: '1px solid var(--chakra-colors-lightGrey4)',
      borderRadius: 'var(--chakra-space-radiusSmall)',
      boxShadow: 'none',
      padding: '0',
      w: { mobile: '19.313rem', sm: '18.375rem', xl: '19.313rem' },
      _hover: {
        cursor: 'pointer',
        boxShadow: '0 0 var(--chakra-space-sm) 0 var(--chakra-colors-lightGrey2)',
        transitionProperty: 'border, box-shadow',
        transitionDuration: '0s, 0s',
        transitionTimingFunction: 'ease, ease',
        transitionDelay: '0s, 0s',
      },
    } as FlexProps,
    imageStyle: {
      borderTopRadius: 'var(--chakra-space-radiusSmall)',
      width: '309px',
      height: '178px',
    },
  };

  switch (cardLength) {
    case 2:
      return {
        wrapperStyles: {
          ...baseStyles.wrapperStyles,
          w: { mobile: '19.313rem', sm: '40.125rem' },
        },
        cardContainerStyles: {
          ...baseStyles.cardContainerStyles,
          minH: { mobile: '11.125rem', sm: '21.563rem' },
          w: { mobile: '19.313rem', sm: '40.125rem' },
        } as FlexProps,
        imageStyle: {
          ...baseStyles.imageStyle,
          width: '642px',
          height: { mobile: '178px', sm: '345px' },
        },
      };

    case 3:
      return {
        wrapperStyles: {
          ...baseStyles.wrapperStyles,
          w: { mobile: '19.313rem', sm: '26.25rem' },
        },
        cardContainerStyles: {
          ...baseStyles.cardContainerStyles,
          minH: { mobile: '11.125rem', sm: '18.25rem' },
          w: { mobile: '19.313rem', sm: '26.25rem' },
        } as FlexProps,
        imageStyle: {
          ...baseStyles.imageStyle,
          width: '420px',
          height: { mobile: '178px', sm: '242px' },
        },
      };

    case 4:
    default:
      return {
        ...baseStyles,
      };
  }
}

const headingStyle = {
  fontSize: '1.8rem',
  fontWeight: '900',
  lineHeight: '2rem',
  color: 'tertiary',
  marginBottom: 'var(--chakra-space-lg)',
};

const linkStyles = {
  cursor: 'default',
  _hover: {
    textDecoration: 'none',
  },
  _focus: {
    textDecoration: 'none',
    boxShadow: 'none',
  },
  _visited: {
    textDecoration: 'none',
  },
  _link: {
    textDecoration: 'none',
  },
  _active: {
    textDecoration: 'none',
  },
};

const destinationCarouselContainerStyles = {
  overflowY: 'auto',
  pb: '2xl',
  gap: 'lg',
  flexWrap: 'nowrap',
  position: 'relative',
  top: 0,
  left: 0,
} as FlexProps;

const fadeEffectOverlayStyles = {
  content: '"content"',
  color: 'transparent',
  bottom: '0',
  right: '0',
  position: 'sticky',
  width: '50px',
  background: 'linear-gradient(-90deg, #FFFFFF 0%, rgba(255, 255, 255, 0))',
};

const cardTitleStyles = {
  p: 'md',
  fontWeight: 700,
  fontSize: 'md',
};
