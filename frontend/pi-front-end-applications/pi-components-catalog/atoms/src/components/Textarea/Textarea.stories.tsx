import { Meta, StoryObj } from '@storybook/react';

import Textarea from './Textarea.component';

const meta: Meta<typeof Textarea> = {
  title: 'Textarea',
  component: Textarea,
  argTypes: {
    placeholder: {
      description: 'Placeholder text shown when the textarea has no value.',
      control: 'text',
    },
    variant: {
      description: 'Chakra textarea visual style variant.',
      options: ['outline', 'flushed', 'filled', 'unstyled'],
      control: {
        type: 'radio',
      },
    },
    size: {
      description: 'Textarea size token controlling spacing and font metrics.',
      options: ['xs', 'sm', 'md', 'lg'],
      control: {
        type: 'radio',
      },
    },
    error: {
      description: 'Validation error text rendered beneath the textarea when present.',
      control: 'text',
    },
    maxLength: {
      description: 'Maximum number of characters allowed in the textarea value.',
      control: { type: 'number', min: 1, max: 5000 },
    },
    isDisabled: {
      description: 'Prevents user interaction with the textarea when true.',
      control: 'boolean',
    },
    isInvalid: {
      description: 'Applies invalid style state from Chakra form controls.',
      control: 'boolean',
    },
    isReadOnly: {
      description: 'Makes the textarea read-only while still focusable.',
      control: 'boolean',
    },
    isRequired: {
      description: 'Marks the textarea as required for form submission.',
      control: 'boolean',
    },
    name: {
      description: 'Form field name used when submitting textarea content.',
      control: 'text',
    },
    value: {
      description: 'Controlled textarea content value.',
      control: 'text',
    },
    defaultValue: {
      description: 'Initial textarea content for uncontrolled usage.',
      control: 'text',
    },
    rows: {
      description: 'Number of visible text rows in the textarea.',
      control: { type: 'number', min: 1, max: 20 },
    },
    onChange: {
      description: 'Callback fired whenever textarea content changes.',
      control: false,
    },
    onBlur: {
      description: 'Callback fired when the textarea loses focus.',
      control: false,
    },
    onFocus: {
      description: 'Callback fired when the textarea gains focus.',
      control: false,
    },
    sx: {
      description: 'Optional Chakra style-system overrides for textarea and wrapper.',
      control: 'object',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?type=design&node-id=862-40522&mode=design&t=PBxH4RxOkxQAxzjR-0',
    },
    docs: {
      description: {
        component:
          'Textarea captures free-form multi-line input with support for variants, validation errors, and length constraints.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const baseArgs = {
  placeholder: 'Enter text here ...',
  variant: 'outline' as const,
  size: 'md' as const,
  onChange: noop,
};

// --- Basic Usage ---
export const Default: Story = {
  args: {
    ...baseArgs,
  },
};

// --- Variants ---
export const FilledVariant: Story = {
  args: {
    ...Default.args,
    variant: 'filled',
  },
};

export const FlushedVariant: Story = {
  args: {
    ...Default.args,
    variant: 'flushed',
  },
};

// --- Sizes ---
export const SmallSize: Story = {
  args: {
    ...Default.args,
    size: 'sm',
  },
};

export const LargeSize: Story = {
  args: {
    ...Default.args,
    size: 'lg',
  },
};

// --- States ---
export const DisabledState: Story = {
  args: {
    ...Default.args,
    isDisabled: true,
  },
};

export const ReadOnlyState: Story = {
  args: {
    ...Default.args,
    defaultValue: 'This value is read-only text.',
    isReadOnly: true,
  },
};

export const ErrorState: Story = {
  args: {
    ...Default.args,
    error: 'Message is required.',
    isInvalid: true,
  },
};

// --- Configuration ---
export const WithMaxLength: Story = {
  args: {
    ...Default.args,
    maxLength: 120,
    rows: 5,
    name: 'guestMessage',
  },
};

export const WithCustomStyles: Story = {
  args: {
    ...Default.args,
    sx: {
      borderColor: 'primary',
      _focusVisible: {
        borderColor: 'teal.500',
      },
    },
  },
};

// --- Edge Cases ---
export const WithLongContent: Story = {
  args: {
    ...Default.args,
    defaultValue:
      'This is intentionally long textarea content to validate wrapping, spacing, and scrollbar behaviour when users enter extended feedback over multiple sentences.',
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    defaultValue: '',
  },
};

export const WithSpecialCharacters: Story = {
  args: {
    ...Default.args,
    defaultValue:
      "King's Cross & St. Pancras - Room request: quiet zone, late check-in @ 23:30 (£)",
  },
};
