import type { Meta, StoryObj } from '@storybook/react';

import { Input } from '../Input';
import { Label } from './label';

const meta: Meta<typeof Label> = {
  title: 'Shadcn/Label',
  component: Label,
  argTypes: {
    children: {
      description: 'Visible label text content.',
      control: 'text',
    },
    htmlFor: {
      description: 'Associated form control id.',
      control: 'text',
    },
    className: {
      description: 'Additional class names for label element.',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Form label primitive built on Radix label for accessible controls.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    children: 'Email Address',
    htmlFor: 'label-story-input',
  },
  render: (args) => (
    <div className="grid gap-2 w-[300px]">
      <Label {...args} />
      <Input id="label-story-input" placeholder="name@company.com" hasLabels={false} />
    </div>
  ),
};
