import { Box } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';

import InfoMessage from './InfoMessage.component';

const meta: Meta<typeof InfoMessage> = {
  title: 'InfoMessage',
  component: InfoMessage,
  decorators: [
    (Story) => (
      <Box position="relative" p="4rem 2rem">
        <Story />
      </Box>
    ),
  ],
  argTypes: {
    infoMessage: {
      description: 'The message text displayed inside the info/error tooltip',
      control: 'text',
    },
    variant: {
      description:
        'Visual variant of the message. "Info" shows an info icon; any other value (or empty) shows an error icon',
      control: 'radio',
      options: ['', 'Info'],
    },
    messageId: {
      description: 'HTML id attribute for the message container (useful for aria-describedby)',
      control: 'text',
    },
    otherStyles: {
      description: 'Additional Chakra UI BoxProps to override default positioning and styling',
      control: 'object',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=6800%3A77627',
    },
    docs: {
      description: {
        component:
          'A tooltip-style message component used to display inline validation errors or informational messages beneath form fields. Supports error (default) and info variants with appropriate ARIA roles.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    infoMessage: 'This field is required',
  },
};

// --- Variants ---

export const ErrorVariant: Story = {
  args: {
    infoMessage: 'Please enter a valid email address',
  },
};

export const InfoVariant: Story = {
  args: {
    infoMessage: 'Your booking reference will be sent to this email',
    variant: 'Info',
  },
};

// --- Content Variations ---

export const WithShortMessage: Story = {
  args: {
    infoMessage: 'Required',
  },
};

export const WithLongMessage: Story = {
  args: {
    infoMessage:
      'The postcode you entered does not match any known addresses. Please check and try again or enter your address manually.',
  },
};

export const WithSpecialCharacters: Story = {
  args: {
    infoMessage: 'Name cannot contain characters: < > & " \' /',
  },
};

// --- Configuration ---

export const WithCustomId: Story = {
  args: {
    infoMessage: 'This message has a custom HTML id',
    messageId: 'email-error-message',
  },
};

export const WithCustomStyles: Story = {
  args: {
    infoMessage: 'Styled with custom positioning',
    otherStyles: {
      position: 'relative',
      bgColor: 'orange.100',
      borderRadius: '8px',
    },
  },
};
