import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { Input } from './input';

const meta: Meta<typeof Input> = {
  title: 'Shadcn/Input',
  component: Input,
  argTypes: {
    id: {
      description: 'Unique id used to generate input and label test ids.',
      control: 'text',
    },
    placeholder: {
      description: 'Placeholder and floating-label text.',
      control: 'text',
    },
    value: {
      description: 'Current input value.',
      control: 'text',
    },
    type: {
      description: 'Native input type.',
      options: ['text', 'email', 'password', 'tel'],
      control: { type: 'radio' },
    },
    disabled: {
      description: 'Disables the input when true.',
      control: 'boolean',
    },
    hasLabels: {
      description: 'Whether floating label behavior is enabled.',
      control: 'boolean',
    },
    error: {
      description: 'Error flag used for label color styling.',
      control: 'text',
    },
    onChange: {
      description: 'Input change callback (value-based in this implementation).',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Business-booker text input with floating label and optional error styling.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

type ControlledInputStoryProps = {
  id: string;
  placeholder: string;
  value?: string;
  type: 'text' | 'email' | 'password' | 'tel';
  disabled: boolean;
  hasLabels: boolean;
  error?: string;
};

const ControlledInputStory = ({ value = '', ...args }: ControlledInputStoryProps) => {
  const [currentValue, setCurrentValue] = useState(value);

  return (
    <Input
      {...args}
      value={currentValue}
      onChange={(nextValue) => setCurrentValue(String(nextValue ?? ''))}
    />
  );
};

export const Default: Story = {
  args: {
    id: 'email',
    placeholder: 'Email address',
    value: '',
    type: 'email',
    disabled: false,
    hasLabels: true,
    error: '',
  },
  render: (args) => <ControlledInputStory {...(args as ControlledInputStoryProps)} />,
};

export const Prefilled: Story = {
  args: {
    ...Default.args,
    id: 'name',
    placeholder: 'Full name',
    type: 'text',
    value: 'Alex Johnson',
  },
  render: (args) => <ControlledInputStory {...(args as ControlledInputStoryProps)} />,
};

export const DisabledState: Story = {
  args: {
    ...Default.args,
    value: 'readonly@premierinn.com',
    disabled: true,
  },
  render: (args) => <ControlledInputStory {...(args as ControlledInputStoryProps)} />,
};

export const WithoutFloatingLabel: Story = {
  args: {
    ...Default.args,
    hasLabels: false,
  },
  render: (args) => <ControlledInputStory {...(args as ControlledInputStoryProps)} />,
};
