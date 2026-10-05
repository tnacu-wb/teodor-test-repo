import { Meta, StoryObj } from '@storybook/react';

import SearchSummary from './SearchSummary.component';

const meta: Meta<typeof SearchSummary> = {
  title: 'SearchSummary',
  component: SearchSummary,
  argTypes: {
    handleEdit: {
      description: 'Callback fired when the user clicks the edit action.',
      control: false,
    },
    isSummaryActive: {
      description: 'Controls whether the summary is visible.',
      control: 'boolean',
    },
    location: {
      description: 'Location summary shown at the start of the search summary.',
      control: 'text',
    },
    dateInterval: {
      description: 'Date range text displayed in the summary.',
      control: 'text',
    },
    roomSummary: {
      description: 'Room and guest summary text.',
      control: 'text',
    },
    numberOfNightsSummary: {
      description: 'Optional number of nights text displayed as an additional summary chip.',
      control: 'text',
    },
    promotionCategorySummary: {
      description: 'Optional promotion category summary text.',
      control: 'text',
    },
    contractRateSummary: {
      description: 'Optional contract rate summary text.',
      control: 'text',
    },
    isLessThanSm: {
      description: 'Applies mobile-specific compact rendering behaviour.',
      control: 'boolean',
    },
    isLessThanMd: {
      description: 'Applies tablet-sized rendering behaviour.',
      control: 'boolean',
    },
    isLessThanLg: {
      description: 'Applies narrow desktop layout behaviour.',
      control: 'boolean',
    },
    editText: {
      description: 'Text displayed for the edit action link.',
      control: 'text',
    },
    style: {
      description: 'Optional style overrides for summary container customisation.',
      control: 'object',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1201%3A24082',
    },
    docs: {
      description: {
        component:
          'SearchSummary provides a compact recap of location, dates, rooms, and optional booking attributes with an edit action.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

const defaultProps = {
  isSummaryActive: true,
  location: 'Manchester, Uk',
  dateInterval: '21 Aug - 23 Aug',
  roomSummary: '1 adult, 1 double room',
  editText: 'Edit',
  isLessThanMd: false,
  isLessThanSm: false,
  isLessThanLg: false,
  handleEdit: noop,
};

// --- Basic Usage ---
export const Default: Story = {
  args: {
    ...defaultProps,
  },
};

// --- Variants ---
export const WithPromotionCategoryVariant: Story = {
  args: {
    ...Default.args,
    numberOfNightsSummary: '365',
    promotionCategorySummary: 'Promotion category',
    contractRateSummary: 'Contract rate',
  },
};

// --- Sizes ---
export const MediumViewportSize: Story = {
  args: {
    ...Default.args,
    isLessThanMd: true,
  },
};

export const SmallViewportSize: Story = {
  args: {
    ...Default.args,
    isLessThanSm: true,
    isLessThanMd: true,
    isLessThanLg: true,
  },
};

// --- States ---
export const InactiveState: Story = {
  args: {
    ...Default.args,
    isSummaryActive: false,
  },
};

// --- Configuration ---
export const WithCustomStyles: Story = {
  args: {
    ...Default.args,
    style: {
      containerStyle: {
        borderColor: 'primary',
        borderWidth: '1px',
      },
    },
  },
};

// --- Edge Cases ---
export const WithLongContent: Story = {
  args: {
    ...Default.args,
    location: 'Manchester City Centre - Very Long Hotel Name For Overflow Testing, United Kingdom',
    roomSummary: '2 adults, 2 children, 2 rooms with breakfast and late checkout',
    promotionCategorySummary: 'Corporate Flexible Rate With Extended Cancellation Window',
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    location: '',
    dateInterval: '',
    roomSummary: '',
  },
};

export const WithSpecialCharacters: Story = {
  args: {
    ...Default.args,
    location: "London - King's Cross & St. Pancras",
    roomSummary: '1 adult | 1 room + breakfast (£)',
    promotionCategorySummary: 'Promo: Summer Escape % Off',
  },
};
