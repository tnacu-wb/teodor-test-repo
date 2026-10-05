import { Meta, StoryObj } from '@storybook/react';

import Switcher from './Switcher.component';

const meta: Meta<typeof Switcher> = {
  title: 'Switcher',
  component: Switcher,
  argTypes: {
    id: {
      description: 'Optional id attribute applied to the switch input element.',
      control: 'text',
    },
    name: {
      description: 'Form field name used when posting switch values.',
      control: 'text',
    },
    value: {
      description: 'Submitted value associated with the switch input.',
      control: 'text',
    },
    isChecked: {
      description: 'Initial checked state rendered by the switcher component.',
      control: 'boolean',
    },
    defaultChecked: {
      description: 'Uncontrolled default checked state applied on first render.',
      control: 'boolean',
    },
    variant: {
      description: 'Visual variant token used by the switch theme styles.',
      control: 'text',
    },
    isDisabled: {
      description: 'Disables the switch and prevents interaction when true.',
      control: 'boolean',
    },
    isFocusable: {
      description: 'Keeps disabled switch focusable for keyboard navigation.',
      control: 'boolean',
    },
    isInvalid: {
      description: 'Marks the switch as invalid for form validation styling.',
      control: 'boolean',
    },
    isReadOnly: {
      description: 'Renders a read-only switch that cannot be toggled by users.',
      control: 'boolean',
    },
    isRequired: {
      description: 'Marks the switch as required for form submission.',
      control: 'boolean',
    },
    colorScheme: {
      description: 'Chakra color scheme used by the switch thumb and track.',
      control: 'text',
    },
    size: {
      description: 'Predefined switch size token.',
      control: 'radio',
      options: ['sm', 'md', 'lg'],
    },
    spacing: {
      description: 'Spacing between switch and optional label content.',
      control: 'text',
    },
    onChange: {
      description: 'Callback fired when checked state changes.',
      control: false,
    },
    onBlur: {
      description: 'Callback fired when the switch input loses focus.',
      control: false,
    },
    onFocus: {
      description: 'Callback fired when the switch input receives focus.',
      control: false,
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40523',
    },
    docs: {
      description: {
        component:
          'Switcher is a toggle control built on Chakra Switch with support for controlled form states and validation flags.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const baseArgs = {
  isChecked: false,
  variant: 'focusless' as const,
  onChange: noop,
};

// --- Basic Usage ---
export const Default: Story = {
  args: {
    ...baseArgs,
  },
};

// --- Variants ---
export const FocuslessVariant: Story = {
  args: {
    ...Default.args,
    variant: 'focusless',
  },
};

export const DefaultVariant: Story = {
  args: {
    ...Default.args,
    variant: undefined,
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
export const CheckedState: Story = {
  args: {
    ...Default.args,
    isChecked: true,
  },
};

export const DisabledState: Story = {
  args: {
    ...Default.args,
    isDisabled: true,
  },
};

export const InvalidState: Story = {
  args: {
    ...Default.args,
    isInvalid: true,
  },
};

// --- Configuration ---
export const RequiredConfiguration: Story = {
  args: {
    ...Default.args,
    name: 'termsAccepted',
    id: 'terms-switch',
    isRequired: true,
  },
};

// --- Edge Cases ---
export const WithLongValue: Story = {
  args: {
    ...Default.args,
    value: 'very-long-switch-value-used-for-form-submission-edge-case-testing',
  },
};

export const ReadOnlyCheckedState: Story = {
  args: {
    ...Default.args,
    isChecked: true,
    isReadOnly: true,
  },
};
