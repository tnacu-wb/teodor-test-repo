import { Text } from '@chakra-ui/react';
import { Meta, StoryObj } from '@storybook/react';
import { ComponentProps, useEffect, useState } from 'react';

import Tabs, { TabsOptionsItem } from './Tabs.component';

const meta: Meta<typeof Tabs> = {
  title: 'Tabs',
  component: Tabs,
  argTypes: {
    options: {
      description: 'Array of tab items including label, optional description, and panel content.',
      control: 'object',
    },
    variant: {
      description: 'Visual tab style variant.',
      options: ['sm', 'tabsGroup', 'greyTabsGroup'],
      control: 'radio',
    },
    orientation: {
      description: 'Tab list orientation used for responsive layouts.',
      options: ['vertical', 'horizontal'],
      control: 'radio',
    },
    prefixDataTestId: {
      description: 'Prefix used to generate deterministic data-testid values.',
      control: 'text',
    },
    singleContent: {
      description: 'Single content panel rendered instead of per-tab content.',
      control: false,
    },
    shortMobileLabels: {
      description: 'When enabled, long labels are shortened with tooltip support on mobile.',
      control: 'boolean',
    },
    showRoomInventory: {
      description: 'Displays room inventory count and tooltip details in labels.',
      control: 'boolean',
    },
    styles: {
      description: 'Style overrides for tab, tab list, and tab panel sections.',
      control: 'object',
    },
    labelStyles: {
      description: 'Typography style overrides for selected and unselected tab labels.',
      control: 'object',
    },
    setStartingTab: {
      description: 'Callback fired with the newly selected tab index.',
      control: false,
    },
    startingTab: {
      description: 'Starting tab index used to initialize the selected tab.',
      control: { type: 'number', min: 0, max: 10 },
    },
    tabScrollSize: {
      description: 'Number of tabs advanced when scroll arrows are clicked.',
      control: { type: 'number', min: 1, max: 5 },
    },
    isScrollable: {
      description: 'Enables left and right arrow controls for scrollable tabs.',
      control: 'boolean',
    },
    isMobileView: {
      description: 'Applies mobile room-label rendering behaviour.',
      control: 'boolean',
    },
    hasRoomLabels: {
      description: 'Displays full room labels instead of truncated labels.',
      control: 'boolean',
    },
    index: {
      description: 'Controlled selected tab index.',
      control: { type: 'number', min: 0, max: 10 },
    },
    defaultIndex: {
      description: 'Default selected tab index for uncontrolled usage.',
      control: { type: 'number', min: 0, max: 10 },
    },
    onChange: {
      description: 'Callback fired when the selected tab index changes.',
      control: false,
    },
    sx: {
      description: 'Optional Chakra style-system overrides for root container.',
      control: 'object',
    },
    width: {
      description: 'Width applied to the tabs root container.',
      control: 'text',
    },
    className: {
      description: 'Optional class name passed to the tabs root container.',
      control: 'text',
    },
    id: {
      description: 'Optional id attribute for the tabs root container.',
      control: 'text',
    },
  },
  parameters: {
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=862%3A40523',
    },
    docs: {
      description: {
        component:
          'Tabs presents grouped content in selectable panels with support for mobile labels, scroll controls, and room inventory metadata.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// eslint-disable-next-line @typescript-eslint/no-empty-function
const noop = () => {};

function TabsWithControlledIndex(props: ComponentProps<typeof Tabs>) {
  const [index, setIndex] = useState(props.index ?? 0);

  useEffect(() => {
    if (props.index !== undefined) {
      setIndex(props.index);
    }
  }, [props.index]);

  return (
    <Tabs
      {...props}
      index={index}
      onChange={(nextIndex) => {
        setIndex(nextIndex);
        props.onChange?.(nextIndex);
      }}
    />
  );
}

const contentOptions: TabsOptionsItem[] = [
  {
    index: 0,
    label: 'Breakfast',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 1,
    label: 'Dinner',
    content: (
      <Text>
        Tuck into all your Premier Inn restaurant favourites like chicken link curry or a margherita
        pizza, and there’s even a new mac and cheese for the little ones! Then enjoy a luxury sticky
        toffee pudding or get stuck into a delicious triple chocolate brownie for dessert.
      </Text>
    ),
  },
  {
    index: 2,
    label: 'Meal Deal',
    content: (
      <Text>
        Fancy saving up to 20% with our tempting Meal Deal offer? Enjoy a delicious two course
        dinner plus a selected drink*, then wake up and tuck into our famous unlimited
        all-you-can-eat Premier Inn Breakfast the next day. Plus, up to two under-16s also eat
        breakfast for free when an adult orders a Me al Deal. We’ll confirm at booking if our Meal
        Deal is available at your selected hotel.
      </Text>
    ),
  },
];

const greyTabsOptions: TabsOptionsItem[] = [
  {
    index: 0,
    label: 'Room 1',
    description: 'Standard double',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 1,
    label: 'Room X',
    description: 'Standard double',
    content: (
      <Text>
        Tuck into all your Premier Inn restaurant favourites like chicken link curry or a margherita
        pizza, and there’s even a new mac and cheese for the little ones! Then enjoy a luxury sticky
        toffee pudding or get stuck into a delicious triple chocolate brownie for dessert.
      </Text>
    ),
  },
  {
    index: 2,
    label: 'Room X',
    description: 'Standard double',
    content: (
      <Text>
        Fancy saving up to 20% with our tempting Meal Deal offer? Enjoy a delicious two course
        dinner plus a selected drink*, then wake up and tuck into our famous unlimited
        all-you-can-eat Premier Inn Breakfast the next day. Plus, up to two under-16s also eat
        breakfast for free when an adult orders a Me al Deal. We’ll confirm at booking if our Meal
        Deal is available at your selected hotel.
      </Text>
    ),
  },
  {
    index: 3,
    label: 'Room X',
    content: (
      <Text>
        Fancy saving up to 20% with our tempting Meal Deal offer? Enjoy a delicious two course
        dinner plus a selected drink*, then wake up and tuck into our famous unlimited
        all-you-can-eat Premier Inn Breakfast the next day. Plus, up to two under-16s also eat
        breakfast for free when an adult orders a Me al Deal. We’ll confirm at booking if our Meal
        Deal is available at your selected hotel.
      </Text>
    ),
  },
];

const singleContentTabs: TabsOptionsItem[] = [
  { index: 0, label: 'Room 1', description: 'Standard double' },
  { index: 1, label: 'Room X', description: 'Standard double' },
  { index: 2, label: 'Room X', description: 'Standard double' },
];
const twoTabsOptions: TabsOptionsItem[] = [
  {
    index: 0,
    label: 'My Premier Inn',
  },
  {
    index: 1,
    label: 'Business Booker',
  },
];

const threeTabsOptions: TabsOptionsItem[] = [
  ...twoTabsOptions,
  {
    index: 2,
    label: 'Business Booker',
  },
];

const fourTabsOptions: TabsOptionsItem[] = [
  ...threeTabsOptions,
  {
    index: 3,
    label: 'Business Booker',
  },
];

const fiveTabsOptions: TabsOptionsItem[] = [
  ...fourTabsOptions,
  {
    index: 4,
    label: 'Business Booker',
  },
];

const sixTabsOptions: TabsOptionsItem[] = [
  ...fiveTabsOptions,
  {
    index: 5,
    label: 'Business Booker',
  },
];

const optionSingleContent = {
  options: singleContentTabs,
  singleContent: <div>Single Content Tabs</div>,
};

const baseArgs = {
  options: twoTabsOptions,
  onChange: noop,
};

// --- Basic Usage ---
export const Default: Story = {
  args: {
    ...baseArgs,
  },
};

// --- Variants ---
export const TabsGroupVariant: Story = {
  args: {
    ...Default.args,
    variant: 'tabsGroup',
    options: contentOptions,
  },
};

export const GreyTabsGroupVariant: Story = {
  args: {
    ...Default.args,
    variant: 'greyTabsGroup',
    options: greyTabsOptions,
  },
};

export const VerticalMobileVariant: Story = {
  args: {
    ...Default.args,
    variant: 'sm',
    orientation: 'vertical',
  },
};

// --- Sizes ---
export const SmallSize: Story = {
  args: {
    ...Default.args,
    variant: 'sm',
  },
};

export const LargeSize: Story = {
  args: {
    ...Default.args,
    variant: undefined,
  },
};

// --- States ---
export const ControlledIndexState: Story = {
  args: {
    ...Default.args,
    index: 1,
  },
  render: (args) => <TabsWithControlledIndex {...args} />,
};

export const ScrollableState: Story = {
  args: {
    ...Default.args,
    isScrollable: true,
    tabScrollSize: 2,
    options: sixTabsOptions,
  },
};

// --- Configuration ---
export const WithSingleContent: Story = {
  args: {
    ...Default.args,
    ...optionSingleContent,
    variant: 'greyTabsGroup',
  },
};

export const WithCustomStyles: Story = {
  args: {
    ...Default.args,
    styles: {
      tabList: {
        borderBottom: '1px solid',
        borderColor: 'lightGrey4',
      },
      tab: {
        borderRadius: 'sm',
      },
      tabPanel: {
        p: 'lg',
      },
    },
  },
};

export const WithLabelStyles: Story = {
  args: {
    ...Default.args,
    options: contentOptions,
    labelStyles: {
      selected: {
        textStyle: 'body-s-emphasis',
      },
      unselected: {
        textStyle: 'body-s-regular',
      },
    },
  },
};

// --- Edge Cases ---
export const WithLongContent: Story = {
  args: {
    ...Default.args,
    options: contentOptions,
    shortMobileLabels: true,
  },
};

export const WithEmptyContent: Story = {
  args: {
    ...Default.args,
    options: [],
  },
};

export const WithSpecialCharacters: Story = {
  args: {
    ...Default.args,
    options: [
      { index: 0, label: "Room #1 - King's", content: <Text>Content for tab #1 (£)</Text> },
      { index: 1, label: 'Room & Suite', content: <Text>Business + Leisure %</Text> },
    ],
  },
};
