import { BoxProps, Text } from '@chakra-ui/react';
import dynamic from 'next/dynamic';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

interface Props {
  title: string;
  testId: string;
  styles?: BoxProps;
}

const MAX_TITLE_LENGTH = 39;

export default function HotelTitle({ title, testId, styles }: Readonly<Props>) {
  if (title.length > MAX_TITLE_LENGTH) {
    return (
      <Tooltip
        {...{ ...styles, fontWeight: 'regular' }}
        data-testid={testId}
        description={title}
        variant="facilities"
      >
        <Text {...styles}>{`${title.slice(0, MAX_TITLE_LENGTH)}...`}</Text>
      </Tooltip>
    );
  }
  return (
    <Text {...styles} data-testid={testId}>
      {title}
    </Text>
  );
}
