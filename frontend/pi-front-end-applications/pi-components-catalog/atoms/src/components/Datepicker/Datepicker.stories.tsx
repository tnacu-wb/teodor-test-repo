import { Meta, StoryObj } from '@storybook/react';

import Datepicker from './Datepicker.component';

const meta: Meta<typeof Datepicker> = {
  title: 'Datepicker',
  component: Datepicker,
  argTypes: {
    minDate: {
      description: 'Minimum date that can be selected in the datepicker',
      control: {
        type: 'date',
      },
    },
    maxDate: {
      description: 'Maximum date that can be selected in the datepicker',
      control: {
        type: 'date',
      },
    },
    defaultStartDate: {
      description: 'Default start date when range selection is enabled',
      control: {
        type: 'date',
      },
    },
    defaultEndDate: {
      description: 'Default end date when range selection is enabled',
      control: {
        type: 'date',
      },
    },
    inputPlaceholder: {
      description: 'Placeholder text displayed in the input field',
      control: {
        type: 'text',
      },
    },
    dateFormat: {
      description: 'Format string for displaying selected dates (e.g., "dd MMM yyyy")',
      control: {
        type: 'text',
      },
    },
    displayDateFormat: {
      description: 'Format string for display purposes',
      control: {
        type: 'text',
      },
    },
    labels: {
      description: 'Labels for buttons and predefined date options',
      control: {
        type: 'object',
      },
    },
    locale: {
      description: 'Locale for date formatting (e.g., "en-GB", "de")',
      control: {
        type: 'text',
      },
    },
    isDisabled: {
      description: 'Disables the datepicker input and calendar',
      control: {
        type: 'boolean',
      },
    },
    hasFooter: {
      description: 'Shows footer with Reset and Done buttons',
      control: {
        type: 'boolean',
      },
    },
    selectsRange: {
      description: 'Enables date range selection mode',
      control: {
        type: 'boolean',
      },
    },
    isError: {
      description: 'Shows error state styling',
      control: {
        type: 'boolean',
      },
    },
    closeCalendarOnSelectDate: {
      description: 'Automatically closes calendar after selecting a date',
      control: {
        type: 'boolean',
      },
    },
    datepickerStyles: {
      description: 'Custom styling for datepicker elements',
      control: {
        type: 'object',
      },
    },
    onInputChange: {
      description: 'Callback fired when input value changes',
      control: false,
    },
    onSelectDates: {
      description: 'Callback fired when dates are selected',
      control: false,
    },
    onReset: {
      description: 'Callback fired when Reset button is clicked',
      control: false,
    },
    onDone: {
      description: 'Callback fired when Done button is clicked',
      control: false,
    },
    isDatePickerFocus: {
      description: 'Controls whether the datepicker is focused',
      control: {
        type: 'boolean',
      },
    },
    displayDatesNotification: {
      description: 'Shows "Check In | Check Out" labels instead of dates',
      control: {
        type: 'boolean',
      },
    },
    disableFlip: {
      description:
        'Prevents the calendar popper from flipping position, using fixed strategy instead',
      control: {
        type: 'boolean',
      },
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Date picker input with calendar popup, supporting single and range selection with locale formatting.',
      },
    },
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=2145%3A56165',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    minDate: new Date(),
    inputPlaceholder: 'Today',
    dateFormat: 'dd MMM yyyy',
    displayDateFormat: 'dd MMM yyyy',
    datepickerStyles: {
      inputGroupStyles: {},
      datepickerInputElementStyles: {},
      iconStyles: {
        top: 'var(--chakra-space-sm)',
      },
    },
    labels: {
      resetButtonLabel: 'Reset',
      doneButtonLabel: 'Done',
      todayLabel: 'Today',
      tomorrowLabel: 'Tomorrow',
      checkoutLabel: 'Check out',
    },
    onInputChange: (date: Date | string) => date,
  },
};

// --- Configuration ---

export const WithFooter: Story = {
  args: {
    ...Default.args,
    hasFooter: true,
    labels: {
      todayLabel: 'Today',
      tomorrowLabel: 'Tomorrow',
      checkoutLabel: 'Check out',
      resetButtonLabel: 'Reset',
      doneButtonLabel: 'Done',
    },
    onReset: () => 'reset',
    onDone: () => 'done',
  },
};

export const WithRangeSelection: Story = {
  args: {
    ...WithFooter.args,
    selectsRange: true,
    inputPlaceholder: 'Today | Tomorrow',
  },
};

export const WithDefaultDates: Story = {
  args: {
    ...WithRangeSelection.args,
    defaultStartDate: new Date(),
    defaultEndDate: new Date(new Date().getTime() + 6 * 24 * 60 * 60 * 1000),
  },
};

// --- States ---

export const DisabledState: Story = {
  args: {
    ...Default.args,
    isDisabled: true,
  },
};

export const ErrorState: Story = {
  args: {
    ...Default.args,
    isError: true,
  },
};

// --- Edge Cases ---

export const WithCustomPlaceholder: Story = {
  args: {
    ...Default.args,
    inputPlaceholder: 'Select checkout date',
  },
};
