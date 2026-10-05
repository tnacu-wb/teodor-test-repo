import type { Meta, StoryObj } from '@storybook/react';

import { Notification } from './notification';

// SVG data URIs for notification icons
const iconSuccess =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2216%22 height=%2216%22 viewBox=%220 0 16 16%22 fill=%22none%22 stroke=%22%231C8754%22 stroke-width=%222%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22%3E%3Cpolyline points=%223 8 7 12 13 4%22%3E%3C/polyline%3E%3C/svg%3E';

const iconWarning =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2216%22 height=%2216%22 viewBox=%220 0 16 16%22 fill=%22%23E67E22%22 stroke=%22%23E67E22%22 stroke-width=%221.5%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22%3E%3Cpath d=%22M8 1.5L14.5 13H1.5Z%22 fill=%22none%22%3E%3C/path%3E%3Crect x=%227.25%22 y=%226%22 width=%221.5%22 height=%224%22 rx=%220.5%22%3E%3C/rect%3E%3Ccircle cx=%228%22 cy=%2211.5%22 r=%220.75%22%3E%3C/circle%3E%3C/svg%3E';

const iconError =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2216%22 height=%2216%22 viewBox=%220 0 16 16%22 fill=%22none%22 stroke=%22%23D90941%22 stroke-width=%222%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22%3E%3Ccircle cx=%228%22 cy=%228%22 r=%226.5%22%3E%3C/circle%3E%3Cline x1=%225.5%22 y1=%225.5%22 x2=%2210.5%22 y2=%2210.5%22%3E%3C/line%3E%3Cline x1=%2210.5%22 y1=%225.5%22 x2=%225.5%22 y2=%2210.5%22%3E%3C/line%3E%3C/svg%3E';

const iconInfo =
  'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 width=%2216%22 height=%2216%22 viewBox=%220 0 16 16%22 fill=%22none%22 stroke=%22%23007FAB%22 stroke-width=%222%22 stroke-linecap=%22round%22 stroke-linejoin=%22round%22%3E%3Ccircle cx=%228%22 cy=%228%22 r=%226.5%22%3E%3C/circle%3E%3Cline x1=%228%22 y1=%225%22 x2=%228%22 y2=%225.01%22 stroke-width=%222.5%22%3E%3C/line%3E%3Cline x1=%228%22 y1=%227%22 x2=%228%22 y2=%2211%22%3E%3C/line%3E%3C/svg%3E';

const meta: Meta<typeof Notification> = {
  title: 'Shadcn/Notification',
  component: Notification,
  argTypes: {
    title: {
      description: 'Notification heading text.',
      control: 'text',
    },
    message: {
      description: 'Supporting message text.',
      control: 'text',
    },
    icon: {
      description: 'Icon source URL rendered beside text.',
      control: 'text',
    },
    type: {
      description: 'Notification tone variant.',
      options: ['default', 'error', 'warning', 'info'],
      control: { type: 'radio' },
    },
    className: {
      description: 'Additional class names for root wrapper.',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Inline status notice component for success, warning, info and error messaging.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    title: 'Booking saved',
    message: 'Your company booking preference has been saved.',
    icon: iconSuccess,
    type: 'default',
  },
};

export const Warning: Story = {
  args: {
    title: 'Action needed',
    message: 'Complete payment in the next 10 minutes to keep this rate.',
    icon: iconWarning,
    type: 'warning',
  },
};

export const Error: Story = {
  args: {
    title: 'Payment failed',
    message: 'We were unable to process your card. Try another method.',
    icon: iconError,
    type: 'error',
  },
};

export const Info: Story = {
  args: {
    title: 'Informational notice',
    message: 'You have a new message in your account dashboard.',
    icon: iconInfo,
    type: 'info',
  },
};
