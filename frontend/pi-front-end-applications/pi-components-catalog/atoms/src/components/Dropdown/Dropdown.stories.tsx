import { Meta, StoryObj } from '@storybook/react';

import BritishFlagRegular from '../../assets/icons/BritishFlagRegular';
import OneAdult from '../../assets/icons/OneAdult';
import Icon from '../Icon';
import Dropdown from './Dropdown.component';

const options = [
  {
    id: 1,
    label: 'Option 1',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 2,
    label: 'Option 2',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 3,
    label: 'Option 3',
  },
  {
    id: 4,
    label: 'Option 4',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 5,
    label: 'Option 5',
  },
  {
    id: 6,
    label: 'Option 6',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
  {
    id: 7,
    label: 'Option 7',
    icon: <Icon svg={<BritishFlagRegular />} />,
  },
];

const meta: Meta<typeof Dropdown> = {
  title: 'Dropdown',
  component: Dropdown,
  argTypes: {
    variant: {
      description: 'Visual style variant of the dropdown',
      options: ['default', 'error'],
      control: {
        type: 'radio',
      },
    },
    placeholder: {
      description: 'Placeholder text shown when no option is selected',
      control: {
        type: 'text',
      },
    },
    label: {
      description: 'Label text displayed above the dropdown',
      control: {
        type: 'text',
      },
    },
    disabled: {
      description: 'Disables the dropdown and prevents interaction',
      control: {
        type: 'boolean',
      },
    },
    hasError: {
      description: 'Shows error state styling and status icon',
      control: {
        type: 'boolean',
      },
    },
    showStatusIcon: {
      description: 'Displays success or error status icon',
      control: {
        type: 'boolean',
      },
    },
    skipChevron: {
      description: 'Hides the chevron icon',
      control: {
        type: 'boolean',
      },
    },
    icon: {
      description: 'Icon displayed on the left side of the dropdown',
      control: false,
    },
    options: {
      description: 'Array of available options to select from',
      control: {
        type: 'object',
      },
    },
    selectedId: {
      description: 'ID of the currently selected option',
      control: {
        type: 'text',
      },
    },
    isOpenMenu: {
      description: 'Controls whether the menu is open',
      control: {
        type: 'boolean',
      },
    },
    matchWidth: {
      description: 'Menu width matches button width',
      control: {
        type: 'boolean',
      },
    },
    onChange: {
      description: 'Callback fired when an option is selected',
      control: false,
    },
    onDisplayContent: {
      description: 'Callback fired when menu open/close state changes',
      control: false,
    },
    onBlur: {
      description: 'Callback fired when dropdown loses focus',
      control: false,
    },
    dropdownStyles: {
      description: 'Style overrides for various dropdown elements',
      control: 'object',
    },
    dataTestId: {
      description: 'Custom data-testid attribute for testing',
      control: 'text',
    },
    className: {
      description: 'Additional CSS class name applied to the dropdown',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Select-style dropdown menu with optional icon, label, status indicators, and error state.',
      },
    },
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=2050%3A54684',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    variant: 'default',
    options,
  },
};

export const WithPlaceholder: Story = {
  args: {
    ...Default.args,
    placeholder: 'Select an option',
  },
};

export const WithLabel: Story = {
  args: {
    ...WithPlaceholder.args,
    label: 'Label',
  },
};

// --- States ---

export const DisabledState: Story = {
  args: {
    ...WithPlaceholder.args,
    disabled: true,
  },
};

export const DisabledWithLabel: Story = {
  args: {
    ...WithLabel.args,
    disabled: true,
  },
};

// --- Configuration ---

export const WithIcon: Story = {
  args: {
    variant: 'default',
    icon: <Icon svg={<OneAdult />} />,
    options,
  },
};

export const WithIconDisabled: Story = {
  args: {
    ...WithIcon.args,
    disabled: true,
  },
};

export const WithIconAndLabel: Story = {
  args: {
    ...WithIcon.args,
    label: 'Guests',
  },
};

export const WithIconAndLabelDisabled: Story = {
  args: {
    ...WithIconAndLabel.args,
    disabled: true,
  },
};

export const WithIconAndPlaceholder: Story = {
  args: {
    ...WithPlaceholder.args,
    icon: <Icon svg={<OneAdult />} />,
  },
};

// --- Variants ---

export const ErrorVariant: Story = {
  args: {
    variant: 'error',
    placeholder: 'Error state',
    options,
  },
};

export const ErrorVariantWithLabel: Story = {
  args: {
    ...ErrorVariant.args,
    label: 'Label',
  },
};

export const ErrorVariantWithIcon: Story = {
  args: {
    ...ErrorVariant.args,
    icon: <Icon svg={<OneAdult />} />,
  },
};

export const WithStatusIconSuccess: Story = {
  args: {
    placeholder: 'Selection made',
    options,
    showStatusIcon: true,
    hasError: false,
    disabled: false,
    selectedId: '1',
  },
};

export const WithStatusIconError: Story = {
  args: {
    ...WithStatusIconSuccess.args,
    hasError: true,
    selectedId: undefined,
  },
};

// --- Edge Cases ---

export const WithSingleOption: Story = {
  args: {
    ...Default.args,
    options: [{ id: 1, label: 'Only option' }],
  },
};

export const WithLongOptionLabels: Story = {
  args: {
    ...Default.args,
    placeholder: 'Select a room type',
    options: [
      { id: 1, label: 'Premier Plus Double Room with City View and Breakfast Included' },
      { id: 2, label: 'Standard Twin Room with Parking and Late Check-out' },
      { id: 3, label: 'Family Room with Extra Bed for Child Under 16' },
    ],
  },
};
