import type { Meta, StoryObj } from '@storybook/react';

import { Accordion, AccordionContent, AccordionItem, AccordionTrigger } from './accordion';

const meta: Meta<typeof Accordion> = {
  title: 'Shadcn/Accordion',
  component: Accordion,
  argTypes: {
    type: {
      description: 'Allows single or multiple expanded items.',
      options: ['single', 'multiple'],
      control: { type: 'radio' },
    },
    collapsible: {
      description: 'Allows closing an open item for single mode.',
      control: 'boolean',
    },
    defaultValue: {
      description: 'Initial expanded item value(s).',
      control: false,
    },
    disabled: {
      description: 'Disables all accordion interaction.',
      control: 'boolean',
    },
    children: {
      description: 'Accordion item content tree.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Expandable disclosure pattern for grouped booking information.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    type: 'single',
    collapsible: true,
    defaultValue: 'item-1',
    disabled: false,
  },
  render: (args) => (
    <Accordion {...args} className="w-[520px]">
      <AccordionItem value="item-1">
        <AccordionTrigger>Cancellation policy</AccordionTrigger>
        <AccordionContent>
          Free cancellation is available until 1 day before check-in.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-2">
        <AccordionTrigger>Parking</AccordionTrigger>
        <AccordionContent>
          On-site parking is available for guests at selected hotels.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-3">
        <AccordionTrigger>Breakfast</AccordionTrigger>
        <AccordionContent>Breakfast can be added during checkout for all guests.</AccordionContent>
      </AccordionItem>
    </Accordion>
  ),
};

export const MultipleOpen: Story = {
  render: () => (
    <Accordion type="multiple" defaultValue={['item-a', 'item-b']} className="w-[520px]">
      <AccordionItem value="item-a">
        <AccordionTrigger>Company rates</AccordionTrigger>
        <AccordionContent>Corporate travellers can access negotiated room rates.</AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-b">
        <AccordionTrigger>Invoice options</AccordionTrigger>
        <AccordionContent>
          Digital invoices are available on the confirmation page.
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  ),
};
