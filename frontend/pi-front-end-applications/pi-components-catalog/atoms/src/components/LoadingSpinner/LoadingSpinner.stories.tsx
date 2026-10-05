import { Meta, StoryObj } from '@storybook/react';

import LoadingSpinner from './LoadingSpinner.component';

const meta: Meta<typeof LoadingSpinner> = {
  title: 'LoadingSpinner',
  component: LoadingSpinner,
  argTypes: {
    loadingText: {
      description: 'Optional text displayed below the spinner to indicate loading status',
      control: 'text',
    },
    wrapperStyle: {
      description: 'Chakra UI BoxProps applied to the spinner wrapper Flex container',
      control: 'object',
    },
    svgProps: {
      description: 'SVG element props passed directly to the spinner SVG',
      control: 'object',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=7148%3A77445',
    },
    docs: {
      description: {
        component:
          'An animated SVG spinner indicating a loading state. Supports optional loading text and custom wrapper styling.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {};

// --- Configuration ---

export const WithLoadingText: Story = {
  args: {
    loadingText: 'This is a loading text',
  },
};

// --- Sizes ---

export const SmallSize: Story = {
  args: {
    svgProps: { width: '40px', height: '40px' },
  },
};

export const LargeSize: Story = {
  args: {
    svgProps: { width: '120px', height: '120px' },
  },
};

// --- Edge Cases ---

export const WithCustomWrapperStyle: Story = {
  args: {
    loadingText: 'Loading your booking...',
    wrapperStyle: { bg: 'gray.100', p: '4', borderRadius: 'md' },
  },
};
