import type { Meta, StoryObj } from '@storybook/react';

import { Alert, AlertDescription, AlertTitle } from './alert';

const meta: Meta<typeof Alert> = {
  title: 'Shadcn/Alert',
  component: Alert,
  argTypes: {
    variant: {
      description: 'Visual status variant.',
      options: ['default', 'destructive', 'red', 'success', 'amber', 'blue'],
      control: { type: 'radio' },
    },
    className: {
      description: 'Optional additional class names for root element.',
      control: 'text',
    },
    children: {
      description: 'Alert content, usually title and description.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Status message container with semantic color variants and structured content.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const BaseTemplate = ({
  variant = 'default',
  title = 'Heads up',
  description = 'Your next action is required.',
}: {
  variant?: 'default' | 'destructive' | 'red' | 'success' | 'amber' | 'blue';
  title: string;
  description: string;
}) => (
  <Alert variant={variant}>
    <AlertTitle>{title}</AlertTitle>
    <AlertDescription>{description}</AlertDescription>
  </Alert>
);

export const Default: Story = {
  args: {
    variant: 'default',
  },
  render: (args) => (
    <BaseTemplate
      variant={args.variant ?? undefined}
      title="Payment details"
      description="Double-check your card information before continuing."
    />
  ),
};

export const SuccessVariant: Story = {
  args: {
    variant: 'success',
  },
  render: (args) => (
    <BaseTemplate
      variant={args.variant ?? undefined}
      title="Saved"
      description="Your traveller profile has been updated successfully."
    />
  ),
};

export const WarningVariant: Story = {
  args: {
    variant: 'amber',
  },
  render: (args) => (
    <BaseTemplate
      variant={args.variant ?? undefined}
      title="Payment expires soon"
      description="Hold this rate by completing checkout in the next 8 minutes."
    />
  ),
};

export const ErrorVariant: Story = {
  args: {
    variant: 'red',
  },
  render: (args) => (
    <BaseTemplate
      variant={args.variant ?? undefined}
      title="Could not process"
      description="We were unable to verify your security code. Try again."
    />
  ),
};
