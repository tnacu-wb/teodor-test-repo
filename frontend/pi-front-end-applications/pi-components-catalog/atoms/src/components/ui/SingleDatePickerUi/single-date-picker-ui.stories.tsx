import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { SingleDatePickerUi } from './single-date-picker-ui';

const iconPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

const meta: Meta<typeof SingleDatePickerUi> = {
  title: 'Shadcn/SingleDatePickerUi',
  component: SingleDatePickerUi,
  argTypes: {
    locale: {
      description: 'Locale code used for date display.',
      control: 'text',
    },
    selectedDay: {
      description: 'Currently selected day.',
      control: false,
    },
    currentMonth: {
      description: 'Current month displayed by calendar.',
      control: false,
    },
    fromMonth: {
      description: 'Minimum month user can navigate to.',
      control: false,
    },
    toMonth: {
      description: 'Maximum month user can navigate to.',
      control: false,
    },
    onDateChange: {
      description: 'Selection callback for a new date.',
      control: false,
    },
    setCurrentMonth: {
      description: 'Setter callback for month changes.',
      control: false,
    },
    icons: {
      description: 'Icon map used by trigger and calendar controls.',
      control: false,
    },
    placeholder: {
      description: 'Placeholder shown before a date is selected.',
      control: 'text',
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Single-day picker UI with popover calendar and reset/done controls.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

const icons = {
  'icon.input.datepicker': iconPixel,
  'icon.chevron.down': iconPixel,
  'icon.chevron.left': iconPixel,
  'icon.chevron.right': iconPixel,
};

const SingleDateStory = () => {
  const [selected, setSelected] = useState<Date | undefined>(new Date());
  const [currentMonth, setCurrentMonth] = useState<Date | undefined>(new Date());

  return (
    <SingleDatePickerUi
      fromMonth={new Date()}
      toMonth={new Date(new Date().getFullYear(), new Date().getMonth() + 6, 1)}
      selectedDay={selected}
      currentMonth={currentMonth}
      setCurrentMonth={setCurrentMonth}
      onDateChange={setSelected}
      locale="en"
      icons={icons}
      placeholder="Select date"
    />
  );
};

export const Default: Story = {
  render: () => <SingleDateStory />,
};
