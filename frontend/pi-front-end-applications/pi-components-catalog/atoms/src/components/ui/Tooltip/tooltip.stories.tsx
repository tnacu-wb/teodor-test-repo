import type { Meta, StoryObj } from '@storybook/react';

import { ErrorTooltip, InfoTooltip } from './tooltip';

const meta: Meta<typeof InfoTooltip> = {
  title: 'Shadcn/Tooltip',
  component: InfoTooltip,
  argTypes: {
    content: {
      description: 'Tooltip content text or node.',
      control: 'text',
    },
    open: {
      description: 'Controls tooltip visibility.',
      control: 'boolean',
    },
    mobile: {
      description: 'Uses mobile inline tooltip behavior.',
      control: 'boolean',
    },
    hoverVariant: {
      description: 'Enables hover-driven behavior.',
      control: 'boolean',
    },
    className: {
      description: 'Optional class names for tooltip content container.',
      control: 'text',
    },
    arrowClassName: {
      description: 'Optional class names for tooltip arrow.',
      control: 'text',
    },
    children: {
      description: 'Trigger element shown inline.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Info and error tooltips for validation and contextual assistance.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    content: 'Use your company travel email to unlock negotiated rates.',
    open: true,
    mobile: false,
    hoverVariant: false,
    className: 'mobile:!flex',
  },
  render: (args) => (
    <InfoTooltip {...args}>
      <button type="button">Show help</button>
    </InfoTooltip>
  ),
};

export const HoverVariant: Story = {
  args: {
    ...Default.args,
    hoverVariant: true,
    open: false,
  },
  render: (args) => (
    <InfoTooltip {...args}>
      <button type="button">Hover for details</button>
    </InfoTooltip>
  ),
};

export const MobileInline: Story = {
  args: {
    ...Default.args,
    mobile: true,
    open: true,
  },
  render: (args) => (
    <InfoTooltip {...args}>
      <button type="button">Mobile detail</button>
    </InfoTooltip>
  ),
};

export const ErrorTooltipVariant: Story = {
  render: () => (
    <ErrorTooltip
      open
      mobile={false}
      content="Card security code is invalid."
      errorId="payment-cvv-error"
      className="mobile:!flex"
    >
      <button type="button">Trigger error</button>
    </ErrorTooltip>
  ),
};
