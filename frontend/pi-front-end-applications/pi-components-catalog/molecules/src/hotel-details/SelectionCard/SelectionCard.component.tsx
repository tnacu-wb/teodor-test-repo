import { Button, Flex, FlexProps, Text } from '@chakra-ui/react';
import { Currency, HIAEMroomType } from '@whitbread-eos/api';
import { Icon, Tick24 } from '@whitbread-eos/atoms';
import { formatAssetsUrl, formatDataTestId } from '@whitbread-eos/utils';
import Image from 'next/image';

interface Props {
  data: HIAEMroomType;
  activePmsRoomType: string;
  onHandleClick: (option: string | string[]) => void;
  price: { currency: string; amount: number };
}

export default function SelectionCard({
  data,
  activePmsRoomType,
  onHandleClick,
  price,
}: Readonly<Props>) {
  const prefixDataTestId = 'Room-Selection-Rate-Card';
  const { roomTypeCode, roomLabel, roomDescription, roomImage } = data;
  const isActiveChoice = roomTypeCode.includes(activePmsRoomType);
  const buttonVariant = isActiveChoice ? 'secondary' : 'tertiary';

  return (
    <Flex
      {...{
        ...cardWrapperStyle,
        border: isActiveChoice
          ? '2px solid var(--chakra-colors-primary)'
          : '1px solid var(--chakra-colors-lightGrey4)',
      }}
    >
      <Flex p={{ mobile: '0.625rem', md: isActiveChoice ? '0.625rem' : '0.688rem' }}>
        <Flex {...imageContainerStyle}>
          <Image
            src={formatAssetsUrl(roomImage)}
            alt="Standard room"
            fill
            sizes="(max-width: 575px) 140px, (min-width: 576px) 199px"
            style={{ objectFit: 'cover' }}
          />
        </Flex>
        <Flex {...textContainerStyle}>
          <Text {...roomClassTextStyle}>{roomLabel}</Text>
          <Text {...descriptionTextStyle}>{roomDescription}</Text>
        </Flex>
      </Flex>
      <Flex
        {...{
          ...pricePanelStyle,
          gap: isActiveChoice ? '0.313rem' : 'sm',
          backgroundColor: isActiveChoice ? '#C5E6EC' : '#DDDDDD66',
          p: isActiveChoice
            ? {
                mobile: '0.5rem var(--chakra-space-xmd)',
                md: '0.6rem var(--chakra-space-xmd)',
              }
            : { mobile: '0.625rem var(--chakra-space-xmd)', lg: 'var(--chakra-space-xmd)' },
        }}
      >
        <Flex
          direction={{ mobile: 'row', lg: 'column' }}
          gap="0.375rem"
          alignItems="baseline"
          justifyContent="center"
          margin="auto"
        >
          <Text {...nightsNumberTextStyle}>1 night</Text>
          <Text {...priceTextStyle}>
            {price.currency === Currency.EUR_NAME ? Currency.EUR : Currency.GBP} {price.amount}
          </Text>
        </Flex>
        <Button
          {...buttonAddStyle}
          size={{ mobile: 'full' }}
          variant={buttonVariant}
          data-testid={formatDataTestId(prefixDataTestId, 'SelectRoomButton')}
          onClick={() => onHandleClick(roomTypeCode)}
          isActive={isActiveChoice}
        >
          {isActiveChoice ? (
            <>
              <Icon svg={<Tick24 color="var(--chakra-colors-white)" />} />
              <Text ml="sm">Selected</Text>
            </>
          ) : (
            'Select room'
          )}
          {/* TODO: Ticket to AEM for labels*/}
        </Button>
      </Flex>
    </Flex>
  );
}

const cardWrapperStyle = {
  maxW: { mobile: 'full', lg: '57rem' },
  justifyContent: 'space-between',
  h: { mobile: '9.938rem', md: '12.438rem', lg: '8.5rem' },
  borderRadius: '10px',
  flexDirection: { mobile: 'column', lg: 'row' },
  backgroundColor: 'white',
} as FlexProps;

const imageContainerStyle = {
  boxSizing: 'border-box',
  position: 'relative',
  minW: { mobile: '8.75rem', md: '12.813rem' },
  minH: { mobile: '4.938rem', md: '7.188rem' },
} as FlexProps;

const textContainerStyle = {
  direction: 'column',
  justifyContent: 'center',
  p: {
    mobile: 'var(--chakra-space-xs) 0.938rem',
    md: 'var(--chakra-space-xs) 0.563rem var(--chakra-space-xs) 1.25rem',
  },
} as FlexProps;

const roomClassTextStyle = {
  fontWeight: 'bold',
  fontSize: { mobile: '0.938rem', sm: 'lg' },
  color: 'tertiary',
  textDecoration: 'underline',
};

const descriptionTextStyle = {
  display: { mobile: 'none', md: 'block' },
  fontSize: 'xs',
  lineHeight: '140%',
  maxW: '30.688rem',
  mt: 'xmd',
};

const pricePanelStyle = {
  direction: { mobile: 'row', lg: 'column' },
  alignItems: 'center',
  justifyContent: { mobile: 'flex-end', xs: 'space-between' },
  p: {
    mobile: '0.5rem var(--chakra-space-xmd)',
    lg: '0.625rem',
  },
  borderBottomRadius: { mobile: '0.625rem', lg: '0' },
  borderRightRadius: { lg: '0.625rem' },
  width: { lg: '10.625rem' },
} as FlexProps;

const nightsNumberTextStyle = {
  fontSize: { mobile: '0.688rem', md: 'xs' },
  fontWeight: 'normal',
};

const priceTextStyle = {
  fontWeight: 'bold',
  fontSize: { mobile: 'md', md: 'lg' },
};

const buttonAddStyle = {
  maxW: { mobile: '7.531rem', sm: '14.281rem', md: '18.656rem', lg: '8.25rem' },
  maxHeight: '2.5rem',
  padding: { md: 'var(--chakra-space-xmd) var(--chakra-space-xl)' },
  ml: { mobile: 'md', xs: '0' },
};
