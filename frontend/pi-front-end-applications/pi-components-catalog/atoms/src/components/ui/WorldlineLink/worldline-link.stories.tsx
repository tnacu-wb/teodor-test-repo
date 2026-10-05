import type { Meta, StoryObj } from '@storybook/react';

import { WorldlineLink } from './worldline-link';

const meta: Meta<typeof WorldlineLink> = {
  title: 'Shadcn/WorldlineLink',
  component: WorldlineLink,
  argTypes: {
    tetheredGuid: {
      description: 'Tethered customer GUID used for auth handoff.',
      control: 'text',
    },
    worldlinePostUrl: {
      description: 'Worldline POST endpoint URL.',
      control: 'text',
    },
    worldlineRequestedPage: {
      description: 'Requested Worldline destination page key.',
      control: 'text',
    },
    worldlineReturnUrl: {
      description: 'Return URL after payment handoff.',
      control: 'text',
    },
    className: {
      description: 'Optional class name for default button render path.',
      control: 'text',
    },
    formClassName: {
      description: 'Optional class name for wrapping form.',
      control: 'text',
    },
    scheme: {
      description: 'Payment scheme context.',
      options: ['GB', 'DE'],
      control: { type: 'radio' },
    },
    renderButton: {
      description: 'Custom render callback for trigger button.',
      control: false,
    },
    children: {
      description: 'Default button content when renderButton is not supplied.',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Payment handoff form wrapper for Worldline session redirect flows.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    tetheredGuid: 'test-guid-1234',
    worldlinePostUrl: '/worldline/post',
    worldlineRequestedPage: 'checkout',
    worldlineReturnUrl: '/payment/complete',
    children: 'Pay securely',
    scheme: 'GB',
  },
};
