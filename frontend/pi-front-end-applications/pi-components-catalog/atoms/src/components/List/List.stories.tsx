import { CheckIcon, StarIcon, InfoIcon } from '@chakra-ui/icons';
import { Meta, StoryObj } from '@storybook/react';

import List from './List.component';

const meta: Meta<typeof List> = {
  title: 'List',
  component: List,
  argTypes: {
    cols: {
      description: 'Number of columns to display list items in (uses CSS Grid)',
      control: { type: 'number', min: 1, max: 4 },
    },
    icon: {
      description: 'Optional React node icon displayed to the left of each list item',
      control: false,
    },
    children: {
      description: 'Array of strings or React nodes to render as list items',
      control: false,
    },
    gap: {
      description: 'Gap between grid items (Chakra UI spacing value)',
      control: 'number',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1795%3A52893',
    },
    docs: {
      description: {
        component:
          'A flexible grid-based list component for displaying items in single or multi-column layouts. Supports custom icons beside each item and inherits Chakra UI Grid props for fine-grained layout control.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const hotelAmenities = [
  'Luggage facilities',
  'Air conditioning',
  '40 inch Smart TV',
  'Family rooms',
  'Premier Plus rooms',
  'Costa Coffee',
  'Lift access',
  'Accessible',
  'Free Wi-Fi',
];

const shortList = ['Free Wi-Fi', 'Parking', 'Restaurant'];

// --- Basic Usage ---

export const Default: Story = {
  render: () => <List>{hotelAmenities}</List>,
};

export const SingleColumn: Story = {
  args: {
    cols: 1,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

// --- Column Layouts ---

export const TwoColumns: Story = {
  args: {
    cols: 2,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

export const ThreeColumns: Story = {
  args: {
    cols: 3,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

// --- With Icons ---

export const WithCheckIcon: Story = {
  args: {
    icon: <CheckIcon w={4} h={4} color="green.500" />,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

export const WithStarIcon: Story = {
  args: {
    icon: <StarIcon w={4} h={4} color="yellow.500" />,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

export const WithInfoIcon: Story = {
  args: {
    icon: <InfoIcon w={4} h={4} color="blue.500" />,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

export const WithIconAndTwoColumns: Story = {
  args: {
    cols: 2,
    icon: <CheckIcon w={4} h={4} color="green.500" />,
  },
  render: (args) => <List {...args}>{hotelAmenities}</List>,
};

// --- Edge Cases ---

export const WithShortList: Story = {
  render: () => <List>{shortList}</List>,
};

export const WithSingleItem: Story = {
  render: () => <List>{['Only one item in this list']}</List>,
};

export const WithLongTextItems: Story = {
  render: () => (
    <List cols={2}>
      {[
        'This is a very long amenity description that might wrap to multiple lines in the grid',
        'Short item',
        'Another amenity with medium-length text content',
        'Brief',
      ]}
    </List>
  ),
};

export const WithoutIcon: Story = {
  render: () => <List>{hotelAmenities}</List>,
};
