import type { Meta, StoryObj } from '@storybook/react';

import { Toast, ToastClose, ToastProvider, ToastViewport } from './toast';

const meta: Meta<typeof Toast> = {
  title: 'Shadcn/Toast',
  component: Toast,
  argTypes: {
    open: {
      description: 'Controls toast visibility.',
      control: 'boolean',
    },
    variant: {
      description: 'Visual variant of the toast container.',
      options: ['default', 'error', 'warning', 'info'],
      control: { type: 'radio' },
    },
    duration: {
      description: 'Auto-dismiss timeout in milliseconds.',
      control: { type: 'number' },
    },
    onOpenChange: {
      description: 'Callback when toast open state changes.',
      control: false,
    },
    children: {
      description: 'Toast content and optional close action.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Toast primitive for transient status messaging with swipe and close support.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    open: true,
    variant: 'default',
    duration: 20000,
  },
  render: (args) => (
    <ToastProvider>
      <Toast {...args}>
        <div className="text-sm">Booking updated successfully.</div>
        <ToastClose />
      </Toast>
      <ToastViewport />
    </ToastProvider>
  ),
};

export const ErrorVariant: Story = {
  args: {
    open: true,
    variant: 'error',
    duration: 20000,
  },
  render: (args) => (
    <ToastProvider>
      <Toast {...args}>
        <div className="text-sm">Payment could not be completed.</div>
        <ToastClose />
      </Toast>
      <ToastViewport />
    </ToastProvider>
  ),
};
