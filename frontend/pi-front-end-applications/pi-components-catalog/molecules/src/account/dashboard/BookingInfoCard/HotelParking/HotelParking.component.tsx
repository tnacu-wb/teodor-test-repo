import type { BoxProps } from '@chakra-ui/react';
import { Box, Flex } from '@chakra-ui/react';
import { Icon, Tick24 } from '@whitbread-eos/atoms';
import { renderSanitizedHtml } from '@whitbread-eos/utils';

export interface Props {
  content: string;
}

export default function HotelParkingComponent({ content }: Readonly<Props>) {
  return (
    <Flex mt="md" sx={{ '@media print': { width: '20.8125rem', height: '12.0625rem' } }}>
      <Icon svg={<Tick24 color="var(--chakra-colors-darkGrey2)" />} />
      <Box {...contentStyles} className="formatLinks" data-testid="HotelDetails-ParkingInformation">
        {renderSanitizedHtml(content)}
      </Box>
    </Flex>
  );
}

const contentStyles = {
  fontSize: 'sm',
  fontWeight: 'medium',
  lineHeight: '2',
  color: 'darkGrey2',
  ml: 'sm',
} as BoxProps;
