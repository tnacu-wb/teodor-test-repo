import type { Meta, StoryObj } from '@storybook/react';

import { Popover, PopoverContent, PopoverTrigger } from './popover';

const meta: Meta<typeof Popover> = {
  title: 'Shadcn/Popover',
  component: Popover,
  argTypes: {
    open: {
      description: 'Controlled open state for popover root.',
      control: 'boolean',
    },
    defaultOpen: {
      description: 'Initial open state in uncontrolled mode.',
      control: 'boolean',
    },
    modal: {
      description: 'Whether popover behaves as modal layer.',
      control: 'boolean',
    },
    onOpenChange: {
      description: 'Callback fired when popover state changes.',
      control: false,
    },
    children: {
      description: 'Trigger and content elements.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Lightweight anchored overlay for contextual details and helpers.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    defaultOpen: true,
    modal: false,
  },
  render: (args) => (
    <Popover {...args}>
      <PopoverTrigger className="rounded border px-3 py-2">Show details</PopoverTrigger>
      <PopoverContent align="start" sideOffset={8}>
        <p className="text-sm">
          Business rates may vary by company policy and selected travel dates.
        </p>
      </PopoverContent>
    </Popover>
  ),
};
