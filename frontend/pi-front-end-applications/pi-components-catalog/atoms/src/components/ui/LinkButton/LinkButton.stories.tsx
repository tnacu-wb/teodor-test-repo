import type { Meta, StoryObj } from '@storybook/react';

import LinkButton, { variantDescriptor } from './LinkButton';

const meta: Meta<typeof LinkButton> = {
  title: 'Shadcn/LinkButton',
  component: LinkButton,
  argTypes: {
    children: {
      description: 'Link button label or content.',
      control: 'text',
    },
    href: {
      description: 'Navigation target URL.',
      control: 'text',
    },
    variant: {
      description: 'Visual style variant token.',
      options: Object.keys(variantDescriptor),
      control: { type: 'select' },
    },
    size: {
      description: 'Size token for spacing and layout.',
      options: [
        'default',
        'sm',
        'lg',
        'icon',
        'defaultCalendar',
        'downloadButton',
        'calendarButton',
        'inputCalendarButton',
        'roomOccupancyButton',
        'footerButtons',
        'desktopSearchButton',
        'findAddressButton',
        'newAddressButton',
        'editButton',
        'buttonBGWhiteBorderSecondary',
      ],
      control: { type: 'select' },
    },
    className: {
      description: 'Additional class names applied to the link element.',
      control: 'text',
    },
    prefetch: {
      description: 'Next.js prefetch behavior for target route.',
      control: 'boolean',
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Next.js link rendered with button style variants for navigation CTAs.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    href: '/book',
    children: 'Continue to checkout',
    variant: 'default',
    size: 'default',
    prefetch: false,
  },
};

export const OutlineVariant: Story = {
  args: {
    ...Default.args,
    variant: 'outline',
    children: 'View booking details',
  },
};

export const LinkVariant: Story = {
  args: {
    ...Default.args,
    variant: 'link',
    children: 'Read policy',
  },
};
