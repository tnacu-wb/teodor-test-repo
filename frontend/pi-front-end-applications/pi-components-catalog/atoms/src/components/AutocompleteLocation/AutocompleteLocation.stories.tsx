import { Meta, StoryObj } from '@storybook/react';

import { FrenchFlagRegular } from '../../assets/icons';
import AutocompleteLocation from './AutocompleteLocation.component';

const meta: Meta<typeof AutocompleteLocation> = {
  title: 'AutocompleteLocation',
  component: AutocompleteLocation,
  argTypes: {
    items: {
      description: 'Array of location items to display, with optional country flags',
      control: 'object',
    },
    inputPlaceholder: {
      description: 'Placeholder text displayed in the input field',
      control: 'text',
    },
    inputSelectedValue: {
      description: 'Currently selected location value',
      control: 'text',
    },
    hasClearIcon: {
      description: 'Show icon to clear the selected value',
      control: 'boolean',
    },
    openListOnFocus: {
      description: 'Open the dropdown list when input receives focus',
      control: 'boolean',
    },
    disableInternalFilter: {
      description: 'Disable built-in filtering based on input value',
      control: 'boolean',
    },
    hasListDivider: {
      description: 'Show divider lines between grouped locations',
      control: 'boolean',
    },
    isRequired: {
      description: 'Mark the input field as required',
      control: 'boolean',
    },
    showElements: {
      description: 'Show location icon in the input field',
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
      description: 'Callback fired when a location value is selected',
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
    onClearInput: {
      description: 'Callback fired when the clear icon is clicked',
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
    autocompleteStyles: {
      description: 'Custom styling configuration for the autocomplete',
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
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=2145%3A56165',
    },
    docs: {
      description: {
        component:
          'Location-aware autocomplete component with country/region support and optional flag icons. Built on top of the Autocomplete component with added location-specific features.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const baseLocationItems = [
  {
    value: 'Nigeria',
    group: 'Africa',
    component: (
      <FrenchFlagRegular style={{ marginRight: '0.5rem', position: 'relative', top: '-5px' }} />
    ),
  },
  {
    value: 'Japan',
    group: 'Asia',
    component: (
      <FrenchFlagRegular style={{ marginRight: '0.5rem', position: 'relative', top: '-5px' }} />
    ),
  },
  {
    value: 'India',
    group: 'Asia',
    component: (
      <FrenchFlagRegular style={{ marginRight: '0.5rem', position: 'relative', top: '-5px' }} />
    ),
  },
];

const mockAutocompleteStyles = {
  inputStyles: {},
  wrapperStyles: { w: '50%' },
  listStyles: {},
};

// --- Basic Usage ---

export const Default: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

// --- Variants ---

export const WithLocationIcon: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    showElements: true,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

export const WithClearIcon: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    hasClearIcon: true,
    showElements: true,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

// --- Configuration ---

export const DisabledFilter: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    disableInternalFilter: true,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

export const OpenOnTyping: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    openListOnFocus: false,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

export const WithoutDividers: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    hasListDivider: false,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

// --- States ---

export const Required: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel (required)',
    onChange: noop,
    isRequired: true,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

export const AriaInvalidState: Story = {
  args: {
    items: baseLocationItems,
    inputPlaceholder: 'Enter place, postcode or hotel',
    onChange: noop,
    ariaInvalid: true,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

// --- Edge Cases ---

export const WithEmptyItems: Story = {
  args: {
    items: [],
    inputPlaceholder: 'No locations available',
    onChange: noop,
    autocompleteStyles: mockAutocompleteStyles,
  },
};

export const WithManyItems: Story = {
  args: {
    items: Array.from({ length: 30 }, (_, i) => ({
      value: `Location ${i + 1}`,
      group: i < 10 ? 'Region A' : i < 20 ? 'Region B' : 'Region C',
    })),
    inputPlaceholder: 'Select from many locations...',
    onChange: noop,
    autocompleteStyles: mockAutocompleteStyles,
  },
};
