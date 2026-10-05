import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { Label } from '../Label';
import { RadioGroup, RadioGroupItem } from './radio-group';

const meta: Meta<typeof RadioGroup> = {
  title: 'Shadcn/RadioGroup',
  component: RadioGroup,
  argTypes: {
    value: {
      description: 'Currently selected item value.',
      control: 'text',
    },
    defaultValue: {
      description: 'Initial selected item for uncontrolled mode.',
      control: 'text',
    },
    name: {
      description: 'Native form name for radio group.',
      control: 'text',
    },
    hasError: {
      description: 'Applies error outline to the group wrapper.',
      control: 'boolean',
    },
    disabled: {
      description: 'Disables all options in the group.',
      control: 'boolean',
    },
    onValueChange: {
      description: 'Callback when selected option changes.',
      control: false,
    },
    children: {
      description: 'Radio item and label composition.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Single-choice input group built on Radix radio primitives.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const ControlledRadioGroup = ({ hasError = false }: { hasError?: boolean }) => {
  const [value, setValue] = useState('standard');

  return (
    <RadioGroup value={value} onValueChange={setValue} hasError={hasError} className="gap-4">
      <div className="flex items-center gap-3">
        <RadioGroupItem value="standard" id="standard-rate" />
        <Label htmlFor="standard-rate">Standard Rate</Label>
      </div>
      <div className="flex items-center gap-3">
        <RadioGroupItem value="flex" id="flex-rate" />
        <Label htmlFor="flex-rate">Flexible Rate</Label>
      </div>
    </RadioGroup>
  );
};

export const Default: Story = {
  render: () => <ControlledRadioGroup />,
};

export const ErrorState: Story = {
  render: () => <ControlledRadioGroup hasError />,
};
