import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { Switch } from './switch';

const meta: Meta<typeof Switch> = {
  title: 'Shadcn/Switch',
  component: Switch,
  argTypes: {
    checked: {
      description: 'Controlled switch state.',
      control: 'boolean',
    },
    disabled: {
      description: 'Disables interaction when true.',
      control: 'boolean',
    },
    required: {
      description: 'Marks switch input as required for form semantics.',
      control: 'boolean',
    },
    onCheckedChange: {
      description: 'Callback fired when state changes.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Business-booker styled on/off switch built on Radix primitives.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

type ControlledSwitchProps = {
  checked: boolean;
  disabled: boolean;
  required: boolean;
};

const ControlledSwitch = ({ checked, ...args }: ControlledSwitchProps) => {
  const [value, setValue] = useState(checked);

  return (
    <div className="inline-flex items-center gap-3">
      <Switch {...args} checked={value} onCheckedChange={setValue} />
      <span className="text-sm">Enable booking reminders</span>
    </div>
  );
};

export const Default: Story = {
  args: {
    checked: false,
    disabled: false,
    required: false,
  },
  render: (args) => <ControlledSwitch {...(args as ControlledSwitchProps)} />,
};

export const CheckedState: Story = {
  args: {
    checked: true,
    disabled: false,
    required: false,
  },
  render: (args) => <ControlledSwitch {...(args as ControlledSwitchProps)} />,
};

export const DisabledState: Story = {
  args: {
    checked: true,
    disabled: true,
    required: false,
  },
  render: (args) => <ControlledSwitch {...(args as ControlledSwitchProps)} />,
};
