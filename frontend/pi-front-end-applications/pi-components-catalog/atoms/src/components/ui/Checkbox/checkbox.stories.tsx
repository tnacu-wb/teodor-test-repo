import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { Checkbox } from './checkbox';

const meta: Meta<typeof Checkbox> = {
  title: 'Shadcn/Checkbox',
  component: Checkbox,
  argTypes: {
    checked: {
      description: 'Controlled checked state.',
      control: 'boolean',
    },
    disabled: {
      description: 'Disables interaction when true.',
      control: 'boolean',
    },
    required: {
      description: 'Marks field as required for form semantics.',
      control: 'boolean',
    },
    onCheckedChange: {
      description: 'Called when checked state changes.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Radix checkbox primitive styled for Business Booker forms.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

type ControlledCheckboxProps = {
  checked: boolean;
  disabled: boolean;
  required: boolean;
};

const ControlledCheckbox = ({ checked, ...args }: ControlledCheckboxProps) => {
  const [value, setValue] = useState(checked);

  return (
    <label className="inline-flex items-center gap-2">
      <Checkbox {...args} checked={value} onCheckedChange={(next) => setValue(Boolean(next))} />
      <span className="text-sm">Email me booking reminders</span>
    </label>
  );
};

export const Default: Story = {
  args: {
    checked: false,
    disabled: false,
    required: false,
  },
  render: (args) => <ControlledCheckbox {...(args as ControlledCheckboxProps)} />,
};

export const CheckedState: Story = {
  args: {
    checked: true,
    disabled: false,
    required: false,
  },
  render: (args) => <ControlledCheckbox {...(args as ControlledCheckboxProps)} />,
};

export const DisabledState: Story = {
  args: {
    checked: true,
    disabled: true,
    required: false,
  },
  render: (args) => <ControlledCheckbox {...(args as ControlledCheckboxProps)} />,
};
