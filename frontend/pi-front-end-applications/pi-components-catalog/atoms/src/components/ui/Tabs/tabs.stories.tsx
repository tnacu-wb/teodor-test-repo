import type { Meta, StoryObj } from '@storybook/react';

import { StaticTabsTrigger, Tabs, TabsContent, TabsList, TabsTrigger } from './tabs';

const meta: Meta<typeof Tabs> = {
  title: 'Shadcn/Tabs',
  component: Tabs,
  argTypes: {
    defaultValue: {
      description: 'Initial active tab value.',
      control: 'text',
    },
    orientation: {
      description: 'Tabs axis orientation.',
      options: ['horizontal', 'vertical'],
      control: { type: 'radio' },
    },
    dir: {
      description: 'Text direction for keyboard navigation.',
      options: ['ltr', 'rtl'],
      control: { type: 'radio' },
    },
    activationMode: {
      description: 'Whether focus automatically activates tabs.',
      options: ['automatic', 'manual'],
      control: { type: 'radio' },
    },
    value: {
      description: 'Controlled active tab value.',
      control: 'text',
    },
    onValueChange: {
      description: 'Triggered when active tab changes.',
      control: false,
    },
    children: {
      description: 'Tab list and content elements.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Tabbed navigation shell for segmented content sections.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    defaultValue: 'overview',
    orientation: 'horizontal',
    dir: 'ltr',
    activationMode: 'automatic',
  },
  render: (args) => (
    <Tabs {...args} className="w-[420px]">
      <TabsList className="w-full">
        <TabsTrigger value="overview">Overview</TabsTrigger>
        <TabsTrigger value="price">Price</TabsTrigger>
      </TabsList>
      <TabsContent value="overview">Room summary, policies and amenities.</TabsContent>
      <TabsContent value="price">Nightly rates and cancellation options.</TabsContent>
    </Tabs>
  ),
};

export const ThreeTabs: Story = {
  render: () => (
    <Tabs defaultValue="details" className="w-[520px]">
      <TabsList className="w-full">
        <TabsTrigger value="details">Details</TabsTrigger>
        <TabsTrigger value="guests">Guests</TabsTrigger>
        <TabsTrigger value="payment">Payment</TabsTrigger>
      </TabsList>
      <TabsContent value="details">Hotel details section.</TabsContent>
      <TabsContent value="guests">Guest details section.</TabsContent>
      <TabsContent value="payment">Payment section.</TabsContent>
    </Tabs>
  ),
};

export const StaticTrigger: Story = {
  render: () => (
    <div className="w-[420px] border-b border-lightGrey4">
      <div className="inline-flex">
        <StaticTabsTrigger active>Upcoming</StaticTabsTrigger>
        <StaticTabsTrigger>Past stays</StaticTabsTrigger>
      </div>
    </div>
  ),
};
