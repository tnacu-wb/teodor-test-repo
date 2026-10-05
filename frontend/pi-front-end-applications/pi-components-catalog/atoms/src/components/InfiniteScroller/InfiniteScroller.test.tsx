import { Box, Text } from '@chakra-ui/react';
import { render } from '@testing-library/react';

import InfiniteScroller from './InfiniteScroller.component';

const defaultProps = {
  loader: <Text>Loading...</Text>,
  dataLength: 2,
  hasMore: true,
  next: jest.fn(),
};

describe('InfiniteScroller', () => {
  it('should render the component', () => {
    const { getByText } = render(
      <InfiniteScroller {...defaultProps}>
        <Box key={0} border="1px solid var(--chakra-colors-primary)" m="var(--chakra-space-xs)">
          Element - 1
        </Box>
      </InfiniteScroller>
    );
    expect(getByText('Element - 1')).toBeInTheDocument();
  });
});
