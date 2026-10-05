import { Meta, StoryObj } from '@storybook/react';
import { ComponentProps, useState } from 'react';

import { Accessible24, Alert, Error, Info, Success } from '../../assets/icons';
import { semanticTextStyles } from '../../theme/adapters/semanticTypography';
import Notification from './Notification.component';

type NotificationProps = ComponentProps<typeof Notification>;

function NotificationWithDismissState(props: NotificationProps) {
  const [isClosed, setIsClosed] = useState(props.isClosed ?? false);

  return (
    <Notification
      {...props}
      isClosed={isClosed}
      onClick={props.onClick ?? (() => setIsClosed((prev) => !prev))}
    />
  );
}

const meta: Meta<typeof Notification> = {
  title: 'Notification',
  component: Notification,
  argTypes: {
    status: {
      description: 'Semantic status used for styling and live region behaviour',
      options: ['info', 'warning', 'success', 'error'],
      control: 'select',
    },
    title: {
      description: 'Optional heading text displayed at the top of the notification',
      control: 'text',
    },
    description: {
      description: 'Main notification message body content',
      control: 'text',
    },
    variant: {
      description: 'Visual variant token used by the alert style config',
      options: ['alert', 'error', 'info', 'infoGrey', 'success', 'accessible'],
      control: 'select',
    },
    onClick: {
      description: 'Optional callback triggered on notification container click',
      control: false,
    },
    showCloseButton: {
      description: 'Shows close affordance when click handler is provided',
      control: 'boolean',
    },
    isClosed: {
      description: 'Hides the notification when true',
      control: 'boolean',
    },
    svg: {
      description: 'Icon rendered at the start of the notification',
      control: false,
    },
    prefixDataTestId: {
      description: 'Optional prefix applied to generated data-testid attributes',
      control: 'text',
    },
    isInnerHTML: {
      description: 'Renders title and description using sanitised HTML when enabled',
      control: 'boolean',
    },
    wrapperStyles: {
      description: 'Style overrides applied to the root notification wrapper',
      control: 'object',
    },
    descriptionTextStyle: {
      description: 'Semantic text style token applied to description text',
      options: Object.keys(semanticTextStyles),
      control: 'select',
    },
    descriptionStrongTextStyle: {
      description: 'Semantic text style token applied to description strong tags',
      options: Object.keys(semanticTextStyles),
      control: 'select',
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
          'Inline notification component for status, warning, error and accessibility messages with optional dismiss behaviour.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const baseArgs: NotificationProps = {
  title: 'Notification title',
  description: 'Notification message',
  status: 'warning',
  variant: 'alert',
  svg: <Alert />,
};

// --- Basic Usage ---

export const Default: Story = {
  args: baseArgs,
};

// --- Variants ---

export const ErrorVariant: Story = {
  args: {
    ...Default.args,
    status: 'error',
    variant: 'error',
    svg: <Error />,
  },
};

export const InfoVariant: Story = {
  args: {
    ...Default.args,
    status: 'info',
    variant: 'info',
    svg: <Info />,
  },
};

export const InfoGreyVariant: Story = {
  args: {
    ...Default.args,
    status: 'info',
    variant: 'infoGrey',
    svg: <Info />,
  },
};

export const SuccessVariant: Story = {
  args: {
    ...Default.args,
    status: 'success',
    variant: 'success',
    svg: <Success />,
  },
};

export const AccessibleVariant: Story = {
  args: {
    ...Default.args,
    status: 'info',
    variant: 'accessible',
    svg: <Accessible24 />,
  },
};

// --- States ---

export const DismissibleState: Story = {
  args: {
    ...Default.args,
    showCloseButton: true,
  },
  render: (args) => <NotificationWithDismissState {...args} />,
};

export const ClosedState: Story = {
  args: {
    ...Default.args,
    isClosed: true,
    showCloseButton: true,
  },
};

// --- Configuration ---

export const WithPrefixDataTestId: Story = {
  args: {
    ...Default.args,
    prefixDataTestId: 'StorybookNotification',
  },
};

export const WithCustomStyles: Story = {
  args: {
    ...Default.args,
    wrapperStyles: {
      borderWidth: '2px',
      borderColor: 'darkGrey2',
      borderStyle: 'dashed',
    },
  },
};

export const WithSemanticDescriptionTypography: Story = {
  args: {
    ...Default.args,
    description: 'Notification message with strong emphasis',
    descriptionTextStyle: 'body-s-regular',
    descriptionStrongTextStyle: 'body-s-emphasis',
  },
};

// --- Edge Cases ---

export const WithLongContent: Story = {
  args: {
    ...Default.args,
    description:
      'This is a longer notification message intended to validate line wrapping behaviour and ensure text remains readable across various viewport sizes.',
  },
};

export const WithArrayDescription: Story = {
  args: {
    ...Default.args,
    description: ['First line of content', 'Second line of content'],
  },
};

export const WithEmptyTitle: Story = {
  args: {
    ...Default.args,
    title: '',
  },
};
