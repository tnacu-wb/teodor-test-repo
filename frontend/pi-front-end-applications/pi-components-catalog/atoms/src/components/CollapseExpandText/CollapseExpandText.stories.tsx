import { Box } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';

import CollapseExpandText from './CollapseExpandText.component';

const meta: Meta<typeof CollapseExpandText> = {
  title: 'CollapseExpandText',
  component: CollapseExpandText,
  argTypes: {
    startingHeight: {
      description: 'Initial collapsed height in pixels before the content is expanded.',
      control: {
        type: 'number',
        min: 20,
      },
    },
    noOfLines: {
      description: 'Maximum number of text lines shown before truncation.',
      control: {
        type: 'number',
        min: 1,
      },
    },
    expandButtonText: {
      description: 'Label shown on the expand action link.',
      control: 'text',
    },
    collapseButtonText: {
      description: 'Label shown on the collapse action link.',
      control: 'text',
    },
    baseTestId: {
      description: 'Base value used to generate data-testid attributes.',
      control: 'text',
    },
    contentText: {
      description: 'Body content rendered inside the collapse area.',
      control: 'text',
    },
    isFadeEffect: {
      description: 'Shows a gradient fade overlay when content is truncated.',
      control: 'boolean',
    },
    isHtml: {
      description: 'Renders contentText as sanitized HTML content.',
      control: 'boolean',
    },
  },
  decorators: [
    (Story) => (
      <Box width="500px" border="1px solid grey" padding="8px" borderRadius="6px">
        <Story />
      </Box>
    ),
  ],
  parameters: {
    docs: {
      description: {
        component:
          'Expandable text block that truncates content by height or line count with toggle controls.',
      },
    },
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

const defaultArgs: Story['args'] = {
  startingHeight: 85,
  noOfLines: 3,
  expandButtonText: 'See more',
  collapseButtonText: 'See less',
  baseTestId: 'CollapseExpandText',
  contentText:
    'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.',
};

// --- Basic Usage ---

export const Default: Story = {
  args: {
    ...defaultArgs,
  },
};

// --- Configuration ---

export const WithHtmlContent: Story = {
  args: {
    ...defaultArgs,
    isHtml: true,
    contentText:
      '<p>Enjoy our <strong>Premier Plus</strong> rooms with <a href="https://www.premierinn.com">more details</a> and upgraded amenities.</p>',
  },
};

export const WithFadeEffect: Story = {
  args: {
    ...defaultArgs,
    isFadeEffect: true,
    contentText:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent non consequat velit. Mauris a diam nibh. Etiam volutpat erat ut turpis laoreet, non faucibus odio placerat. Vivamus posuere purus ut feugiat suscipit. Nunc ullamcorper arcu id leo pharetra, sed feugiat neque iaculis. Donec viverra urna ac nisl vulputate congue.',
  },
};

export const WithCustomToggleLabels: Story = {
  args: {
    ...defaultArgs,
    expandButtonText: 'Read full description',
    collapseButtonText: 'Hide description',
    noOfLines: 2,
    startingHeight: 70,
  },
};

// --- Edge Cases ---

export const WithShortContent: Story = {
  args: {
    ...defaultArgs,
    contentText: 'Short text that does not need truncation.',
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...defaultArgs,
    contentText: '',
  },
};
