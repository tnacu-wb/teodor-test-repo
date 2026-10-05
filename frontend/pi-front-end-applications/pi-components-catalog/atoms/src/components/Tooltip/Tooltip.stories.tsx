import { Meta, StoryObj } from '@storybook/react';

import {
  Alert as AlertIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
  Success as SuccessIcon,
  WhiteAlert,
  WhiteError,
  WhiteInfo,
  WhiteSuccess,
} from '../../assets/icons';
import Tooltip from './Tooltip.component';

const meta: Meta<typeof Tooltip> = {
  title: 'Tooltip',
  component: Tooltip,
  argTypes: {
    title: {
      description: 'Optional title displayed above the tooltip description.',
      control: 'text',
    },
    description: {
      description: 'Main tooltip message content shown inside the tooltip body.',
      control: 'text',
    },
    variant: {
      description: 'Visual style variant applied to the tooltip.',
      options: [
        'alert',
        'error',
        'info',
        'infoGrey',
        'standard',
        'facilities',
        'inlineAlert',
        'inlineError',
        'inlineInfo',
        'inlineSuccess',
      ],
      control: 'select',
    },
    children: {
      description: 'Trigger element text used to open the tooltip on interaction.',
      control: 'text',
    },
    svg: {
      description: 'Optional custom icon rendered before tooltip content.',
      control: false,
    },
    alertElementStyles: {
      description: 'Custom style overrides for the tooltip content wrapper.',
      control: 'object',
    },
    closeDelay: {
      description: 'Delay in milliseconds before the tooltip closes.',
      control: { type: 'number', min: 0, max: 5000 },
    },
    hasArrow: {
      description: 'Displays the tooltip arrow indicator when true.',
      control: 'boolean',
    },
    isDisabled: {
      description: 'Disables tooltip behavior when true.',
      control: 'boolean',
    },
    placement: {
      description: 'Placement of the tooltip relative to its trigger element.',
      control: 'select',
      options: ['auto', 'top', 'right', 'bottom', 'left'],
    },
    openDelay: {
      description: 'Delay in milliseconds before the tooltip opens.',
      control: { type: 'number', min: 0, max: 5000 },
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1255%3A35552',
    },
    docs: {
      description: {
        component:
          'Tooltip communicates contextual guidance, status, and validation messages for form fields and inline UI hints.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---
export const Default: Story = {
  args: {
    description: 'An info message on a text field',
    variant: 'info',
    children: 'Tooltip component',
    svg: <WhiteInfo />,
  },
};

// --- Variants ---
export const AlertVariant: Story = {
  args: {
    description: 'An alert message on a text field',
    variant: 'alert',
    children: 'Tooltip component',
    svg: <WhiteAlert />,
  },
};

export const ErrorVariant: Story = {
  args: {
    description: 'An error message on a text field',
    variant: 'error',
    children: 'Tooltip component',
    svg: <WhiteError />,
  },
};

export const InfoVariant: Story = {
  args: {
    description: 'An info message on a text field',
    variant: 'info',
    children: 'Tooltip component',
    svg: <WhiteInfo />,
  },
};

export const InfoGreyVariant: Story = {
  args: {
    description: 'An info message on a text field',
    variant: 'infoGrey',
    children: 'Tooltip component',
    svg: <InfoIcon />,
  },
};

export const SuccessVariant: Story = {
  args: {
    description: 'A success message on a text field',
    variant: 'success',
    children: 'Tooltip component',
    svg: <WhiteSuccess />,
  },
};

// --- States ---
export const DisabledState: Story = {
  args: {
    ...Default.args,
    isDisabled: true,
  },
};

export const InLineAlertState: Story = {
  args: {
    ...AlertVariant.args,
    variant: 'inlineAlert',
    svg: <AlertIcon />,
  },
};

export const InLineErrorState: Story = {
  args: {
    ...ErrorVariant.args,
    variant: 'inlineError',
    svg: <ErrorIcon />,
  },
};

export const InLineInfoState: Story = {
  args: {
    ...InfoVariant.args,
    variant: 'inlineInfo',
    svg: <InfoIcon />,
  },
};

export const InLineSuccessState: Story = {
  args: {
    ...SuccessVariant.args,
    variant: 'inlineSuccess',
    svg: <SuccessIcon />,
  },
};

// --- Configuration ---
export const WithTitle: Story = {
  args: {
    ...Default.args,
    title: 'Notification title',
  },
};

export const WithCustomStyles: Story = {
  args: {
    ...Default.args,
    alertElementStyles: {
      borderRadius: 'md',
      paddingY: 2,
    },
  },
};

export const FacilitiesVariant: Story = {
  args: {
    description: 'Air conditioning',
    children: 'Tooltip component',
    variant: 'facilities',
  },
};

// --- Edge Cases ---
export const WithLongContent: Story = {
  args: {
    description:
      'Visa and Mastercard have this 3-digit number on the back next to the signature panel. This message demonstrates long tooltip content wrapping behavior.',
    children: 'Tooltip component',
    variant: 'standard',
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    description: '',
  },
};

export const WithSpecialCharacters: Story = {
  args: {
    ...Default.args,
    description: 'Room cost includes VAT, Wi-Fi, and breakfast: GBP 89.99 - 100% refundable!',
  },
};
