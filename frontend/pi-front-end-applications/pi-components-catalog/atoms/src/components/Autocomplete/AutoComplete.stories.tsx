import { Meta, StoryObj } from '@storybook/react';

import { ChevronDown, ChevronRight, SearchIcon } from '../../assets/icons';
import AutoComp from './AutoComplete.component';

const meta: Meta<typeof AutoComp> = {
  title: 'AutoComplete',
  component: AutoComp,
  argTypes: {
    items: {
      description: 'Array of items to display in the autocomplete list',
      control: 'object',
    },
    inputPlaceholder: {
      description: 'Placeholder text displayed in the input field',
      control: 'text',
    },
    inputSelectedValue: {
      description: 'Currently selected value to display in the input',
      control: 'text',
    },
    multiSelectable: {
      description: 'Allow multiple items to be selected',
      control: 'boolean',
    },
    openListOnFocus: {
      description: 'Open the dropdown list when input receives focus',
      control: 'boolean',
    },
    disableInternalFilter: {
      description: 'Disable built-in filtering of items based on input value',
      control: 'boolean',
    },
    hasListDivider: {
      description: 'Show divider lines between grouped items',
      control: 'boolean',
    },
    isRequired: {
      description: 'Mark the input field as required',
      control: 'boolean',
    },
    showElements: {
      description: 'Show/hide rendered elements from the component',
      control: 'boolean',
    },
    hasItemObject: {
      description: 'Items contain custom component objects',
      control: 'boolean',
    },
    isPriceFinder: {
      description: 'Enable price finder specific styling/behavior',
      control: 'boolean',
    },
    ariaInvalid: {
      description: 'Mark the input as invalid for accessibility',
      control: 'boolean',
    },
    onChange: {
      description: 'Callback fired when a value is selected',
      control: false,
    },
    onSelectOption: {
      description: 'Callback fired when an option is selected',
      control: false,
    },
    onInputChange: {
      description: 'Callback fired when the input value changes',
      control: false,
    },
    onBlurInput: {
      description: 'Callback fired when input loses focus',
      control: false,
    },
    onFocusInput: {
      description: 'Callback fired when input receives focus',
      control: false,
    },
    setInputHasFocus: {
      description: 'Callback to update focus state of the input',
      control: false,
    },
    wrapperStyles: {
      description: 'Chakra Box props applied to the wrapper container',
      control: 'object',
    },
    inputStyles: {
      description: 'Chakra Input props applied to the input field',
      control: 'object',
    },
    icons: {
      description: 'Custom icons for left and right side of input',
      control: false,
    },
    autocompleteStyles: {
      description: 'Custom styling configuration for the autocomplete component',
      control: 'object',
    },
    listStyles: {
      description: 'Chakra PopoverContent props applied to the dropdown list',
      control: 'object',
    },
    dataTestId: {
      description: 'Test identifier for automated testing',
      control: 'text',
    },
    ariaDescribedBy: {
      description: 'ID of element describing the input for accessibility',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'A customizable autocomplete/dropdown component with support for filtering, grouping, and multi-selection.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const baseItems = [{ value: 'Nigeria' }, { value: 'Japan' }, { value: 'India' }];

// --- Basic Usage ---

export const Default: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Select a country...',
    onChange: noop,
    wrapperStyles: { w: '50%' },
  },
};

export const WithCustomPlaceholder: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Click me!',
    onChange: noop,
    wrapperStyles: { w: '50%' },
  },
};

// --- Variants ---

export const WithToggleIcons: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Select a country...',
    onChange: noop,
    wrapperStyles: { w: '50%' },
    icons: {
      right: ({ isOpen }: { isOpen?: boolean }) => (isOpen ? <ChevronRight /> : <ChevronDown />),
    },
  },
};

export const WithLeftSearchIcon: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Search countries...',
    onChange: noop,
    wrapperStyles: { w: '50%' },
    icons: {
      left: () => <SearchIcon />,
    },
  },
};

export const WithGroupedItems: Story = {
  args: {
    items: [
      { value: 'Nigeria', group: 'Africa' },
      { value: 'Japan', group: 'Asia' },
      { value: 'India', group: 'Asia' },
    ],
    inputPlaceholder: 'Select a country...',
    onChange: noop,
    wrapperStyles: { w: '50%' },
  },
};

export const MultiSelectMode: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Select multiple countries...',
    onChange: noop,
    multiSelectable: true,
    wrapperStyles: { w: '50%' },
  },
};

// --- Configuration ---

export const DisabledFilter: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Filtering disabled...',
    onChange: noop,
    disableInternalFilter: true,
    wrapperStyles: { w: '50%' },
  },
};

export const OpenOnFocusDisabled: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'List opens on typing...',
    onChange: noop,
    openListOnFocus: false,
    wrapperStyles: { w: '50%' },
  },
};

export const WithoutDividers: Story = {
  args: {
    items: [
      { value: 'Nigeria', group: 'Africa' },
      { value: 'Japan', group: 'Asia' },
      { value: 'India', group: 'Asia' },
    ],
    inputPlaceholder: 'Select a country...',
    onChange: noop,
    hasListDivider: false,
    wrapperStyles: { w: '50%' },
  },
};

// --- States ---

export const Required: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Required field',
    onChange: noop,
    isRequired: true,
    wrapperStyles: { w: '50%' },
  },
};

export const AriaInvalidState: Story = {
  args: {
    items: baseItems,
    inputPlaceholder: 'Invalid field',
    onChange: noop,
    ariaInvalid: true,
    wrapperStyles: { w: '50%' },
  },
};

// --- Edge Cases ---

export const WithEmptyItems: Story = {
  args: {
    items: [],
    inputPlaceholder: 'No options available',
    onChange: noop,
    wrapperStyles: { w: '50%' },
  },
};

export const WithManyItems: Story = {
  args: {
    items: Array.from({ length: 50 }, (_, i) => ({ value: `Option ${i + 1}` })),
    inputPlaceholder: 'Select from many...',
    onChange: noop,
    wrapperStyles: { w: '50%' },
  },
};
