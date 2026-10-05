import type { Meta, StoryObj } from '@storybook/react';

import { Button } from './button';

const meta: Meta<typeof Button> = {
  title: 'Shadcn/Button',
  component: Button,
  argTypes: {
    children: {
      description: 'Visible button label or custom content.',
      control: 'text',
    },
    variant: {
      description: 'Visual style variant token.',
      options: Object.keys({
        default: true,
        alternativeDefault: true,
        destructive: true,
        outline: true,
        secondary: true,
        ghost: true,
        link: true,
        grey: true,
        defaultCalendar: true,
        downloadButton: true,
        calendarButton: true,
        inputCalendarButton: true,
        roomOccupancyButton: true,
        footerButtons: true,
        desktopSearchButton: true,
        dialogDefault: true,
        dialogOutline: true,
        dialogDestructive: true,
        findAddressButton: true,
        newAddressButton: true,
        editButton: true,
        saveUpdatesButton: true,
        buttonBGWhiteBorderSecondary: true,
        cardLinkButton: true,
        truncateWithEllipsisButton: true,
        disabled: true,
      }),
      control: { type: 'select' },
    },
    size: {
      description: 'Sizing token used by the component style variants.',
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
    asChild: {
      description: 'Renders child element instead of a native button.',
      control: 'boolean',
    },
    disabled: {
      description: 'Disables interactions and applies disabled styles.',
      control: 'boolean',
    },
    type: {
      description: 'Native HTML button type.',
      options: ['button', 'submit', 'reset'],
      control: { type: 'radio' },
    },
    onClick: {
      description: 'Click handler for button interaction.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Shadcn-style action button with broad business-booker variant and size token support.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const noop = () => undefined;

export const Default: Story = {
  args: {
    children: 'Continue',
    variant: 'default',
    size: 'default',
    disabled: false,
    type: 'button',
    onClick: noop,
  },
};

export const OutlineVariant: Story = {
  args: {
    ...Default.args,
    children: 'View Details',
    variant: 'outline',
  },
};

export const LinkVariant: Story = {
  args: {
    ...Default.args,
    children: 'Learn more',
    variant: 'link',
    size: 'default',
  },
};

export const DialogDestructive: Story = {
  args: {
    ...Default.args,
    children: 'Delete booking',
    variant: 'dialogDestructive',
    size: 'lg',
  },
};

export const DisabledState: Story = {
  args: {
    ...Default.args,
    children: 'Processing',
    variant: 'default',
    disabled: true,
  },
};

export const IconSize: Story = {
  args: {
    ...Default.args,
    children: '+',
    variant: 'calendarButton',
    size: 'icon',
  },
};
