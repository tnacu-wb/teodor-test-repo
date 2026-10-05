import { Icon, Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import { useEffect, useState } from 'react';

import Checkbox from './Checkbox.component';

const meta: Meta<typeof Checkbox> = {
  title: 'Checkbox',
  component: Checkbox,
  argTypes: {
    isChecked: {
      description: 'Controls whether the checkbox is selected.',
      control: 'boolean',
    },
    isDisabled: {
      description: 'Disables interaction and applies disabled styling.',
      control: 'boolean',
    },
    isReadOnly: {
      description: 'Makes the checkbox non-editable while still focusable.',
      control: 'boolean',
    },
    isIndeterminate: {
      description: 'Shows the partially selected state.',
      control: 'boolean',
    },
    isInvalid: {
      description: 'Marks the field as invalid for form validation states.',
      control: 'boolean',
    },
    variant: {
      description: 'Applies custom atoms variant styling.',
      options: ['default', 'border'],
      control: {
        type: 'radio',
      },
    },
    size: {
      description: 'Adjusts checkbox control size.',
      options: ['sm', 'md', 'lg'],
      control: {
        type: 'radio',
      },
    },
    colorScheme: {
      description: 'Chakra color scheme used for selected state.',
      control: 'text',
    },
    showIcon: {
      description: 'Reserved prop for icon visibility behavior.',
      control: 'boolean',
    },
    area: {
      description: 'Reserved metadata prop for analytics and instrumentation.',
      control: 'text',
    },
    checkboxWrapperStyles: {
      description: 'Style overrides for the wrapper container.',
      control: 'object',
    },
    children: {
      description: 'Label content displayed to the right of the checkbox.',
      control: 'text',
    },
    onChange: {
      description: 'Callback fired when the checked state changes.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Controlled checkbox input with support for variants, validation states, and custom label content.',
      },
    },
  },
};

export default meta;

type Story = StoryObj<typeof meta>;

const StatefulCheckbox = (args: NonNullable<Story['args']>) => {
  const [checked, setChecked] = useState<boolean>(Boolean(args.isChecked));

  useEffect(() => {
    setChecked(Boolean(args.isChecked));
  }, [args.isChecked]);

  return (
    <Checkbox
      {...args}
      isChecked={checked}
      onChange={(event) => {
        setChecked(event.target.checked);
        args.onChange?.(event);
      }}
    />
  );
};

const baseArgs: Story['args'] = {
  isChecked: false,
  children: 'Receive booking updates by email',
};

// --- Basic Usage ---

export const Default: Story = {
  args: {
    ...baseArgs,
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

export const CheckedState: Story = {
  args: {
    ...baseArgs,
    isChecked: true,
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

// --- Variants ---

export const BorderVariant: Story = {
  args: {
    ...baseArgs,
    variant: 'border',
    children:
      'I have read, understand and accept the Terms and Conditions. Cancellations must be made within 24 hours after booking.',
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

export const WithIconInLabel: Story = {
  args: {
    ...baseArgs,
    isChecked: true,
    children: (
      <Text>
        Label <Icon color="var(--chakra-colors-infoBorder)" data-testid="checkbox-icon" />
      </Text>
    ),
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

// --- Sizes ---

export const SmallSize: Story = {
  args: {
    ...baseArgs,
    size: 'sm',
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

export const MediumSize: Story = {
  args: {
    ...baseArgs,
    size: 'md',
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

export const LargeSize: Story = {
  args: {
    ...baseArgs,
    size: 'lg',
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};

// --- States ---

export const DisabledState: Story = {
  args: {
    ...baseArgs,
    isDisabled: true,
  },
};

export const ReadOnlyState: Story = {
  args: {
    ...baseArgs,
    isChecked: true,
    isReadOnly: true,
  },
};

export const IndeterminateState: Story = {
  args: {
    ...baseArgs,
    isIndeterminate: true,
  },
  render: (args) => <Checkbox {...args} />,
};

export const InvalidState: Story = {
  args: {
    ...baseArgs,
    isInvalid: true,
  },
};

// --- Edge Cases ---

export const WithLongLabelContent: Story = {
  args: {
    ...baseArgs,
    children: (
      <Text m="0 var(--chakra-space-sm)">
        This booking preference label is intentionally long to validate wrapping behavior,
        accessibility focus handling, and the click target alignment across multiple lines.
      </Text>
    ),
  },
  render: (args) => <StatefulCheckbox {...(args ?? baseArgs)} />,
};
