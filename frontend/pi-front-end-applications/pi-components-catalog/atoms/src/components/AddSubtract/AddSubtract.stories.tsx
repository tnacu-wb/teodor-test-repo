import { Center } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import React, { ComponentProps, useState } from 'react';

import AddSubtract from './AddSubtract.component';

function AddSubtractWithState(props: ComponentProps<typeof AddSubtract>) {
  const [value, setValue] = useState(props.value ?? 0);
  return (
    <AddSubtract
      {...props}
      value={value}
      onPlus={() => {
        setValue((prev) => prev + 1);
        props.onPlus?.();
      }}
      onSubtract={() => {
        setValue((prev) => prev - 1);
        props.onSubtract?.();
      }}
      handleInputChange={(val: number) => setValue(val)}
    />
  );
}

const meta: Meta<typeof AddSubtract> = {
  title: 'AddSubtract',
  component: AddSubtract,
  argTypes: {
    value: {
      description: 'Current numeric value displayed in the component',
      control: 'number',
    },
    label: {
      description: 'Optional label text displayed below the controls',
      control: 'text',
    },
    isPlusDisable: {
      description: 'Disable the increment (plus) button',
      control: 'boolean',
    },
    isSubtractDisable: {
      description: 'Disable the decrement (subtract) button',
      control: 'boolean',
    },
    isPlusHidden: {
      description: 'Hide the increment (plus) button',
      control: 'boolean',
    },
    isSubtractHidden: {
      description: 'Hide the decrement (subtract) button',
      control: 'boolean',
    },
    isEditable: {
      description: 'Allow direct editing of the value via input field',
      control: 'boolean',
    },
    maxLength: {
      description: 'Maximum length for edited input value (when isEditable is true)',
      control: 'number',
    },
    prefixDataTestId: {
      description: 'Prefix for test identifiers applied to buttons and value display',
      control: 'text',
    },
    onPlus: {
      description: 'Callback fired when the increment button is clicked',
      control: false,
    },
    onSubtract: {
      description: 'Callback fired when the decrement button is clicked',
      control: false,
    },
    handleInputChange: {
      description: 'Callback fired when the input value changes (when isEditable is true)',
      control: false,
    },
  },
  render: (args) => <AddSubtractWithState {...args} />,
  decorators: [
    (Story) => (
      <Center>
        <Story />
      </Center>
    ),
  ],
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1463%3A56674',
    },
    docs: {
      description: {
        component:
          'An increment/decrement control component for managing numeric quantities. Supports disabled states, editable input, and optional labels.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

export const WithLabel: Story = {
  args: {
    label: 'Adults',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

export const WithQuantityLabel: Story = {
  args: {
    label: 'Rooms',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

// --- States ---

export const WithDisabledPlusButton: Story = {
  args: {
    label: 'Max reached',
    isPlusDisable: true,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

export const WithDisabledSubtractButton: Story = {
  args: {
    label: 'Min reached',
    isPlusDisable: false,
    isSubtractDisable: true,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

export const WithBothButtonsDisabled: Story = {
  args: {
    label: 'Locked',
    isPlusDisable: true,
    isSubtractDisable: true,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

// --- Variants ---

export const WithHiddenPlusButton: Story = {
  args: {
    label: 'Subtract only',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: true,
    isSubtractHidden: false,
    isEditable: false,
  },
};

export const WithHiddenSubtractButton: Story = {
  args: {
    label: 'Add only',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: true,
    isEditable: false,
  },
};

export const WithBothButtonsHidden: Story = {
  args: {
    label: 'Display only',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: true,
    isSubtractHidden: true,
    isEditable: false,
  },
};

// --- Configuration ---

export const EditableMode: Story = {
  args: {
    label: 'Direct edit',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: true,
  },
};

export const EditableWithMaxLength: Story = {
  args: {
    label: 'Limited input',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: true,
    maxLength: 3,
  },
};

export const WithTestIdentifier: Story = {
  args: {
    label: 'Test tracking',
    prefixDataTestId: 'guest-count',
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};

// --- Edge Cases ---

export const CompactNoLabel: Story = {
  args: {
    isPlusDisable: false,
    isSubtractDisable: false,
    isPlusHidden: false,
    isSubtractHidden: false,
    isEditable: false,
  },
};
