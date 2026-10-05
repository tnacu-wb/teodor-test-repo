import type { Meta, StoryObj } from '@storybook/react';

import { Button } from '../Button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from './dialog';

const meta: Meta<typeof Dialog> = {
  title: 'Shadcn/Dialog',
  component: Dialog,
  argTypes: {
    open: {
      description: 'Controlled open state for the dialog root.',
      control: 'boolean',
    },
    defaultOpen: {
      description: 'Initial open state for uncontrolled usage.',
      control: 'boolean',
    },
    modal: {
      description: 'Whether interaction outside is blocked.',
      control: 'boolean',
    },
    onOpenChange: {
      description: 'Callback fired when open state changes.',
      control: false,
    },
    children: {
      description: 'Dialog trigger and content tree.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Modal dialog shell for confirmation and blocking interactions.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    defaultOpen: true,
    modal: true,
  },
  render: (args) => (
    <Dialog {...args}>
      <DialogTrigger asChild>
        <Button>Open dialog</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Confirm cancellation</DialogTitle>
          <DialogDescription>
            You can cancel this booking until 18:00 on the day before check-in.
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button variant="dialogOutline">Keep booking</Button>
          <Button variant="dialogDestructive">Cancel booking</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  ),
};

export const WithoutCloseButton: Story = {
  render: () => (
    <Dialog defaultOpen>
      <DialogContent hasCloseButton={false}>
        <DialogHeader>
          <DialogTitle>Read-only notice</DialogTitle>
          <DialogDescription>Close via your in-flow action buttons.</DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button>Done</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  ),
};
