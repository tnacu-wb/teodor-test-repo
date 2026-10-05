import { Box, Flex, FlexProps, Heading, Text } from '@chakra-ui/react';
import { formatAssetsUrl, formatDataTestId, renderSanitizedHtml } from '@whitbread-eos/utils';
import Image from 'next/image';

interface Props {
  data: any; // type to be added after BE integration;
}

const baseDataTestId = 'WhyUs';

export default function WhyUs({ data }: Readonly<Props>) {
  const { title, description, picture, whyItems } = data;
  const itemsStyle = whyItems.length === 4 ? fourStyle : sixStyle;

  return (
    <Flex {...sectionStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
      <Box {...leftStyle}>
        <Heading as="h2" {...headingStyle} data-testid={formatDataTestId(baseDataTestId, 'Title')}>
          {title}
        </Heading>
        <Text data-testid={formatDataTestId(baseDataTestId, 'Description')}>{description}</Text>
        {whyItems.length > 0 && (
          <Box {...itemsStyle}>
            {whyItems.map((item: any) => (
              <WhyUsBox key={`why-us-box-${item.itemTitle}`} item={item} />
            ))}
          </Box>
        )}
      </Box>
      <Box {...pictureStyle}>
        <Image
          data-testid={formatDataTestId(baseDataTestId, 'Image')}
          src={formatAssetsUrl(picture)}
          alt={title}
          style={imageStyle}
          width={700}
          height={600}
          objectFit="contain"
        />
      </Box>
    </Flex>
  );
}

interface BoxProps {
  item: any; // type to be added after BE integration;
}

function WhyUsBox({ item }: Readonly<BoxProps>) {
  return (
    <Box {...itemStyle} data-testid={formatDataTestId(baseDataTestId, 'Item')}>
      <Image
        width={32}
        height={32}
        alt={item?.itemTitle ?? ''}
        src={formatAssetsUrl(item?.itemIcon as string)}
      />
      <Heading as="h4" {...itemTitleStyle}>
        {item.itemTitle}
      </Heading>
      <Box className="formatLinks">{renderSanitizedHtml(item.itemDescription)}</Box>
    </Box>
  );
}

const sectionStyle = {
  justifyContent: 'space-between',
  flexDirection: {
    mobile: 'column-reverse',
    xs: 'column-reverse',
    sm: 'column',
    md: 'column',
    lg: 'row',
    xl: 'row',
  },
  align: 'flex-end',
  p: 'var(--chakra-space-xl) 0',
  alignItems: 'flex-start',
  gap: '1.5rem',
} as FlexProps;

const itemStyle = {
  display: 'flex',
  flexDirection: 'column',
} as FlexProps;

const itemTitleStyle = {
  fontSize: '1.1rem',
  fontWeight: '700',
  lineHeight: '1.3rem',
  color: 'tertiary',
  m: 'var(--chakra-space-sm) 0',
};

const leftStyle = {
  display: 'flex',
  flexDirection: 'column',
  flex: 1,
} as FlexProps;

const sixStyle = {
  display: 'grid',
  gridTemplateColumns: { mobile: 'repeat(1, minmax(0, 1fr))', sm: 'repeat(3, minmax(0, 1fr))' },
  gap: 6,
  mt: 'var(--chakra-space-md)',
};

const fourStyle = {
  display: 'grid',
  gridTemplateColumns: { mobile: 'repeat(1, minmax(0, 1fr))', sm: 'repeat(2, minmax(0, 1fr))' },
  gap: 6,
  mt: 'var(--chakra-space-md)',
};

const pictureStyle = {
  width: '100%',
  flex: 1,
  borderRadius: 'md',
  overflow: 'hidden',
  position: 'relative',
} as FlexProps;

const imageStyle = {
  width: '100%',
  height: '100%',
};

const headingStyle = {
  fontSize: '1.8rem',
  fontWeight: '900',
  lineHeight: '2rem',
  color: 'tertiary',
  marginBottom: 'var(--chakra-space-md)',
};
