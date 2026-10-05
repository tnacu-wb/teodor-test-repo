import { Box, Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import React, { useState } from 'react';

import InfiniteScroller from './InfiniteScroller.component';

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const appendItems = (
  setItems: React.Dispatch<React.SetStateAction<string[]>>,
  batchSize: number
) => {
  setItems((prev) => [
    ...prev,
    ...Array.from({ length: batchSize }, (_, i) => `item-${prev.length + i}`),
  ]);
};

const generateItems = (count: number) =>
  Array.from({ length: count }, (_, i) => (
    <Box
      key={`static-item-${i}`}
      border="1px solid var(--chakra-colors-primary)"
      m="var(--chakra-space-xs)"
      p="sm"
    >
      Element - #{i}
    </Box>
  ));

const generateInitialIds = (count: number) => Array.from({ length: count }, (_, i) => `item-${i}`);

function DefaultScroller() {
  const [items, setItems] = useState(generateInitialIds(50));
  const fetchItems = () => setTimeout(() => appendItems(setItems, 10), 500);
  const hasMore = items.length < 100;

  return (
    <Box w="55%" m="auto">
      <InfiniteScroller
        loader={<Text>Loading ...</Text>}
        endMessage={<Text>You have reached the end!</Text>}
        dataLength={items.length}
        hasMore={hasMore}
        next={fetchItems}
      >
        {items.map((id, index) => (
          <Box
            key={id}
            border="1px solid var(--chakra-colors-primary)"
            m="var(--chakra-space-xs)"
            p="sm"
          >
            Element - #{index}
          </Box>
        ))}
      </InfiniteScroller>
    </Box>
  );
}

function CustomThresholdScroller() {
  const [items, setItems] = useState(generateInitialIds(30));
  const fetchItems = () => setTimeout(() => appendItems(setItems, 10), 500);
  const hasMore = items.length < 80;

  return (
    <Box w="55%" m="auto">
      <InfiniteScroller
        loader={<Text>Loading ...</Text>}
        endMessage={<Text>All loaded!</Text>}
        dataLength={items.length}
        hasMore={hasMore}
        next={fetchItems}
        scrollThreshold={0.5}
      >
        {items.map((id, index) => (
          <Box
            key={id}
            border="1px solid var(--chakra-colors-primary)"
            m="var(--chakra-space-xs)"
            p="sm"
          >
            Item #{index}
          </Box>
        ))}
      </InfiniteScroller>
    </Box>
  );
}

function ScrollableTargetScroller() {
  const [items, setItems] = useState(generateInitialIds(20));
  const fetchItems = () => setTimeout(() => appendItems(setItems, 10), 300);
  const hasMore = items.length < 60;

  return (
    <Box w="55%" m="auto" id="scrollable-container" h="400px" overflow="auto">
      <InfiniteScroller
        loader={<Text>Loading ...</Text>}
        endMessage={<Text>No more items.</Text>}
        dataLength={items.length}
        hasMore={hasMore}
        next={fetchItems}
        scrollableTarget="scrollable-container"
      >
        {items.map((id, index) => (
          <Box
            key={id}
            border="1px solid var(--chakra-colors-primary)"
            m="var(--chakra-space-xs)"
            p="sm"
          >
            Scrollable item #{index}
          </Box>
        ))}
      </InfiniteScroller>
    </Box>
  );
}

const meta: Meta<typeof InfiniteScroller> = {
  title: 'InfiniteScroller',
  component: InfiniteScroller,
  argTypes: {
    loader: {
      description: 'Element displayed while loading more items',
      control: false,
    },
    dataLength: {
      description: 'Current total number of items loaded',
      control: 'number',
    },
    hasMore: {
      description: 'Whether more items are available to load',
      control: 'boolean',
    },
    next: {
      description: 'Callback invoked to fetch more items when scroll threshold is reached',
      control: false,
    },
    scrollThreshold: {
      description: 'Scroll position threshold (0-1) at which next() is triggered',
      control: { type: 'number', min: 0, max: 1, step: 0.1 },
    },
    endMessage: {
      description: 'Element displayed when all items have been loaded',
      control: false,
    },
    scrollableTarget: {
      description: 'ID of the scrollable parent element (if not window)',
      control: 'text',
    },
    style: {
      description: 'Inline style object applied to the scroll container',
      control: 'object',
    },
    children: {
      description: 'Content rendered inside the scroller',
      control: false,
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1109%3A13793',
    },
    docs: {
      description: {
        component:
          'A wrapper around react-infinite-scroll-component that loads more content as the user scrolls down.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    loader: <Text>Loading ...</Text>,
    endMessage: <Text>You have reached the end!</Text>,
    dataLength: 50,
    hasMore: true,
  },
  render: () => <DefaultScroller />,
};

// --- Configuration ---

export const WithCustomScrollThreshold: Story = {
  args: {
    loader: <Text>Loading ...</Text>,
    endMessage: <Text>All loaded!</Text>,
    dataLength: 30,
    hasMore: true,
    scrollThreshold: 0.5,
  },
  render: () => <CustomThresholdScroller />,
};

export const WithScrollableTarget: Story = {
  args: {
    loader: <Text>Loading ...</Text>,
    endMessage: <Text>No more items.</Text>,
    dataLength: 20,
    hasMore: true,
    scrollableTarget: 'scrollable-container',
  },
  render: () => <ScrollableTargetScroller />,
};

// --- States ---

export const AllItemsLoadedState: Story = {
  args: {
    loader: <Text>Loading ...</Text>,
    endMessage: <Text fontWeight="bold">You have seen it all!</Text>,
    dataLength: 10,
    hasMore: false,
    next: noop,
    children: generateItems(10),
  },
  decorators: [
    (Story) => (
      <Box w="55%" m="auto">
        <Story />
      </Box>
    ),
  ],
};

// --- Edge Cases ---

export const WithSingleItem: Story = {
  args: {
    loader: <Text>Loading ...</Text>,
    endMessage: <Text>That is all.</Text>,
    dataLength: 1,
    hasMore: false,
    next: noop,
    children: generateItems(1),
  },
  decorators: [
    (Story) => (
      <Box w="55%" m="auto">
        <Story />
      </Box>
    ),
  ],
};

export const WithManyItems: Story = {
  args: {
    loader: <Text>Loading ...</Text>,
    endMessage: <Text>End of list.</Text>,
    dataLength: 100,
    hasMore: false,
    next: noop,
    children: generateItems(100),
  },
  decorators: [
    (Story) => (
      <Box w="55%" m="auto">
        <Story />
      </Box>
    ),
  ],
};
