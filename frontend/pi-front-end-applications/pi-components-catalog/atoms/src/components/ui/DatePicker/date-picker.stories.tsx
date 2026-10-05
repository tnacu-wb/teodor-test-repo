import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';
import type { DateRange } from 'react-day-picker';

import { DatePickerWithRange } from './date-picker';

const iconPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

const formLabels = {
  calendarIcon: iconPixel,
  datePicker: {
    months: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    weekdaysShort: ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'],
  },
};

const icons = {
  'icon.chevron.left': iconPixel,
  'icon.chevron.right': iconPixel,
};

const meta: Meta<typeof DatePickerWithRange> = {
  title: 'Shadcn/DatePicker',
  component: DatePickerWithRange,
  argTypes: {
    locale: {
      description: 'Locale code used for date formatting.',
      control: 'text',
    },
    mobile: {
      description: 'Switches component to modal/mobile behavior.',
      control: 'boolean',
    },
    dateFromUrl: {
      description: 'Initial selected range from URL.',
      control: false,
    },
    showError: {
      description: 'Shows error styling when true.',
      control: 'boolean',
    },
    onDateChange: {
      description: 'Called when range changes.',
      control: false,
    },
    setShowError: {
      description: 'Setter for error state.',
      control: false,
    },
    onOpenChange: {
      description: 'Called when picker popover/modal opens or closes.',
      control: false,
    },
    formLabels: {
      description: 'Localized labels and month/day names.',
      control: false,
    },
    icons: {
      description: 'Icon source map for internal controls.',
      control: false,
    },
    className: {
      description: 'Container className attributes passed to root.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          'Business Booker date-range picker built from Popover, Calendar and Dialog pieces.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const DateRangeStory = () => {
  const [range, setRange] = useState<DateRange | undefined>({
    from: new Date(),
    to: new Date(Date.now() + 24 * 60 * 60 * 1000),
  });
  const [showError, setShowError] = useState(false);

  return (
    <DatePickerWithRange
      className={{}}
      locale="en"
      formLabels={formLabels}
      icons={icons}
      mobile={false}
      dateFromUrl={range}
      onDateChange={setRange}
      showError={showError}
      setShowError={setShowError}
      onOpenChange={() => undefined}
    />
  );
};

export const Default: Story = {
  render: () => <DateRangeStory />,
};
