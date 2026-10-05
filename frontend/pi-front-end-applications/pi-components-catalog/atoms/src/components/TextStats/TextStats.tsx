import { Box, Flex } from '@chakra-ui/react';

export interface TextStat {
  count: number | undefined;
  text: string;
}

interface TextStatsProps {
  stats: TextStat[];
}

export default function TextStats({ stats }: Readonly<TextStatsProps>) {
  const items = stats.map((stat: TextStat, index: number) => {
    const showDivider = index !== 0;
    const testId = stat.text.split(' ').join('').toLowerCase();
    return (
      <Flex
        alignItems="center"
        key={`textStat-${index * Math.random()}`}
        data-testid={`textstats-element-${testId}`}
      >
        {showDivider && <Box as="span" {...verticalDividerStyle} />}
        <Box as="span" {...statTextStyle}>
          {stat.count} {stat.text}
        </Box>
      </Flex>
    );
  });
  return (
    <Flex direction="row" flexWrap="wrap" data-testid="textstats-summary">
      {items}
    </Flex>
  );
}

const statTextStyle = {
  fontSize: {
    mobile: 'xs',
    sm: 'md',
  },
  lineHeight: {
    mobile: '2',
    sm: '3',
  },
  color: 'darkGrey1',
  fontWeight: {
    mobile: 'medium',
    sm: 'normal',
  },
  mr: {
    mobile: '0.375rem',
    sm: 'sm',
  },
};

const verticalDividerStyle = {
  mr: {
    mobile: '0.375rem',
    sm: 'sm',
  },
  width: '1px',
  height: '0.75rem',
  backgroundColor: 'darkGrey1',
};
