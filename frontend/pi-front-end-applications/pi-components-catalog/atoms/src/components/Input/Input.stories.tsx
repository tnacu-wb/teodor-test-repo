import { Center, Stack, Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import React, { ComponentProps, useEffect, useState } from 'react';

import Input from './Input.component';

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

type InputProps = ComponentProps<typeof Input>;

function InputWithState(props: InputProps) {
  const [value, setValue] = useState(props.value ?? '');

  // Sync local state when props.value changes from Storybook controls
  useEffect(() => {
    setValue(props.value ?? '');
  }, [props.value]);

  return (
    <Center>
      <Stack w="500px">
        <Input {...props} value={value} onChange={(val) => setValue(val)} />
      </Stack>
    </Center>
  );
}

function InputAllStates(props: InputProps) {
  const [value, setValue] = useState('');
  return (
    <Center>
      <Stack w="500px" spacing="1.5rem">
        <Text fontWeight="bold">Inactive</Text>
        <Input
          {...props}
          placeholderText="Inactive state"
          value={value}
          onChange={(val) => setValue(val)}
        />
        <Text fontWeight="bold">Activated</Text>
        <Input {...props} value="Activated state" onChange={noop} />
        <Text fontWeight="bold">Focused (click to autofocus)</Text>
        <Input {...props} value="Focused state" onChange={noop} isAutoFocused={true} />
        <Text fontWeight="bold">Disabled</Text>
        <Input {...props} isDisabled placeholderText="Disabled state" value="" onChange={noop} />
      </Stack>
    </Center>
  );
}

const meta: Meta<typeof Input> = {
  title: 'Input',
  component: Input,
  argTypes: {
    name: {
      description: 'HTML name attribute for the input element (used in form submissions)',
      control: 'text',
    },
    label: {
      description: 'Label text displayed above the input field. Append * to indicate required',
      control: 'text',
    },
    placeholderText: {
      description: 'Placeholder text shown when the input is empty',
      control: 'text',
    },
    value: {
      description: 'Current value of the input (controlled component)',
      control: 'text',
    },
    type: {
      description: 'HTML input type (text, password, email, number, etc.)',
      control: 'select',
      options: ['text', 'password', 'email', 'number', 'tel'],
    },
    isDisabled: {
      description: 'Disables the input and prevents user interaction',
      control: 'boolean',
    },
    isAutoFocused: {
      description: 'Automatically focuses the input on mount',
      control: 'boolean',
    },
    helperText: {
      description: 'Hint text displayed below the input to guide the user',
      control: 'text',
    },
    charLimit: {
      description: 'Maximum character count displayed as a counter below the input',
      control: 'number',
    },
    maxLength: {
      description: 'HTML maxlength attribute limiting character input',
      control: 'number',
    },
    error: {
      description: 'Error message text. When set, the input displays in an error state',
      control: 'text',
    },
    useTooltip: {
      description: 'Display the error message inside a tooltip instead of inline text',
      control: 'boolean',
    },
    showIcon: {
      description: 'Show a success/error icon inside the input based on validation state',
      control: 'boolean',
    },
    showRightElement: {
      description: 'Show a show/hide toggle button (useful for password fields)',
      control: 'boolean',
    },
    showLabel: {
      description: 'Force display of the label even when the input is disabled',
      control: 'boolean',
    },
    autoComplete: {
      description: 'HTML autocomplete attribute value',
      control: 'select',
      options: ['on', 'off', 'email', 'name', 'tel'],
    },
    isInputAriaRequired: {
      description: 'Sets aria-required on the input for accessibility',
      control: 'boolean',
    },
    onChange: {
      description: 'Callback fired when the input value changes',
      control: false,
    },
    onBlur: {
      description: 'Callback fired when the input loses focus',
      control: false,
    },
    onClick: {
      description: 'Callback fired when the input is clicked',
      control: false,
    },
    onKeyDown: {
      description: 'Callback fired on keydown events in the input',
      control: false,
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=2050%3A54683',
    },
    docs: {
      description: {
        component:
          'A form input component with support for labels, helper text, character limits, validation states (error/success), tooltip errors, and password visibility toggle. Built on Chakra UI Input with accessibility features.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    name: 'default-input',
    label: 'Full name',
    placeholderText: 'Enter your full name',
  },
  render: (args) => <InputWithState {...args} />,
};

// --- States ---

export const AllStates: Story = {
  args: {
    name: 'states-input',
    label: 'Label',
  },
  render: (args) => <InputAllStates {...args} />,
};

export const DisabledState: Story = {
  args: {
    name: 'disabled-input',
    label: 'Email address',
    placeholderText: 'Cannot edit this field',
    isDisabled: true,
  },
  render: (args) => (
    <Center>
      <Stack w="500px">
        <Input {...args} value="" onChange={noop} />
      </Stack>
    </Center>
  ),
};

export const AutoFocusedState: Story = {
  args: {
    name: 'autofocused-input',
    label: 'Search',
    placeholderText: 'Start typing...',
    isAutoFocused: true,
  },
  render: (args) => <InputWithState {...args} />,
};

// --- Helper Text & Character Limits ---

export const WithHelperText: Story = {
  args: {
    name: 'helper-text-input',
    label: 'Password',
    helperText: 'Must be at least 8 characters',
  },
  render: (args) => <InputWithState {...args} />,
};

export const WithCharacterLimit: Story = {
  args: {
    name: 'charlimit-input',
    label: 'Booking reference',
    charLimit: 10,
    placeholderText: 'e.g. ABC1234567',
  },
  render: (args) => <InputWithState {...args} />,
};

export const WithCharacterLimitAndHelperText: Story = {
  args: {
    name: 'charlimit-helper-input',
    label: 'Promo code',
    charLimit: 10,
    helperText: 'Enter your promotional code',
  },
  render: (args) => <InputWithState {...args} />,
};

// --- Error States ---

export const WithErrorText: Story = {
  args: {
    name: 'error-text-input',
    label: 'Email address',
    error: 'Please enter a valid email address',
    value: 'invalid-email',
  },
  render: (args) => <InputWithState {...args} />,
};

export const WithErrorTextAndCharLimit: Story = {
  args: {
    name: 'error-charlimit-input',
    label: 'Postcode',
    error: 'Postcode is too long',
    charLimit: 8,
    value: 'SW1A 1AA EXTRA',
  },
  render: (args) => <InputWithState {...args} />,
};

export const WithErrorTooltip: Story = {
  args: {
    name: 'error-tooltip-input',
    label: 'Card number',
    error: 'Card number is invalid',
    useTooltip: true,
    value: '1234',
  },
  render: (args) => <InputWithState {...args} />,
};

// --- Validation Icons ---

export const WithSuccessIcon: Story = {
  args: {
    name: 'success-icon-input',
    label: 'Email address',
    showIcon: true,
    value: 'user@example.com',
  },
  render: (args) => (
    <Center>
      <Stack w="500px">
        <Input {...args} onChange={noop} />
      </Stack>
    </Center>
  ),
};

export const WithErrorIcon: Story = {
  args: {
    name: 'error-icon-input',
    label: 'Email address',
    error: 'Invalid email format',
    showIcon: true,
    useTooltip: false,
    value: 'not-an-email',
  },
  render: (args) => (
    <Center>
      <Stack w="500px">
        <Input {...args} onChange={noop} />
      </Stack>
    </Center>
  ),
};

// --- Password Input ---

export const PasswordWithToggle: Story = {
  args: {
    name: 'password-input',
    label: 'Password',
    type: 'password',
    showRightElement: true,
    placeholderText: 'Enter your password',
  },
  render: (args) => <InputWithState {...args} />,
};

// --- Required Field ---

export const RequiredField: Story = {
  args: {
    name: 'required-input',
    label: 'First name *',
    placeholderText: 'Enter your first name',
    isInputAriaRequired: true,
  },
  render: (args) => <InputWithState {...args} />,
};
