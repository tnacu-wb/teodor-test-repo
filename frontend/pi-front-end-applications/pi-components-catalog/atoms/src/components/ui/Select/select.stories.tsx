import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { Select } from './select';

const iconPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

const options = [
  { value: 'asc', displayValue: 'Price low to high' },
  { value: 'desc', displayValue: 'Price high to low' },
  { value: 'distance', displayValue: 'Closest first' },
];

const meta: Meta<typeof Select> = {
  title: 'Shadcn/Select',
  component: Select,
  argTypes: {
    value: {
      description: 'Selected option value.',
      control: 'text',
    },
    label: {
      description: 'Floating label text.',
      control: 'text',
    },
    testId: {
      description: 'Base test id used for nested select elements.',
      control: 'text',
    },
    triggerIcon: {
      description: 'Optional icon shown at trigger start.',
      control: 'text',
    },
    triggerContent: {
      description: 'Optional content rendered in the trigger (if supported).',
      control: 'text',
    },
    arrowIcon: {
      description: 'Chevron icon shown at trigger end.',
      control: 'text',
    },
    options: {
      description: 'Available select options.',
      control: 'object',
    },
    showValueInstead: {
      description: 'Render raw value instead of displayValue.',
      control: 'boolean',
    },
    onOptionChange: {
      description: 'Callback fired when an option is selected.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Custom popover-based select with keyboard support and floating label.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

type ControlledSelectArgs = {
  value: string | number;
  triggerIcon: string;
  triggerContent: string;
  label: string;
  arrowIcon: string;
  testId: string;
  options: { value: string | number; displayValue: string }[];
  showValueInstead: boolean;
};

const ControlledSelect = (args: ControlledSelectArgs) => {
  const [value, setValue] = useState(args.value);

  return <Select {...args} value={value} onOptionChange={setValue} />;
};

export const Default: Story = {
  args: {
    value: 'asc',
    triggerIcon: iconPixel,
    triggerContent: '',
    label: 'Sort results',
    arrowIcon: iconPixel,
    testId: 'sort',
    options,
    showValueInstead: false,
  },
  render: (args) => <ControlledSelect {...(args as ControlledSelectArgs)} />,
};

export const ShowRawValue: Story = {
  args: {
    ...Default.args,
    showValueInstead: true,
  },
  render: (args) => <ControlledSelect {...(args as ControlledSelectArgs)} />,
};
