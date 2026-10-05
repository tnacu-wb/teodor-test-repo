import type { Meta, StoryObj } from '@storybook/react';

import { Button } from '../Button';
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from './drawer';

const meta: Meta<typeof Drawer> = {
  title: 'Shadcn/Drawer',
  component: Drawer,
  argTypes: {
    defaultOpen: {
      description: 'Initial open state for uncontrolled usage.',
      control: 'boolean',
    },
    shouldScaleBackground: {
      description: 'Scales page background while drawer is visible.',
      control: 'boolean',
    },
    modal: {
      description: 'Whether interaction outside drawer is blocked.',
      control: 'boolean',
    },
    onOpenChange: {
      description: 'Callback when drawer open state changes.',
      control: false,
    },
    children: {
      description: 'Drawer trigger and content tree.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Bottom-sheet style panel for mobile-first supplementary flows.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    defaultOpen: true,
    shouldScaleBackground: true,
    modal: true,
  },
  render: (args) => (
    <Drawer {...args}>
      <DrawerTrigger asChild>
        <Button>Open drawer</Button>
      </DrawerTrigger>
      <DrawerContent>
        <DrawerHeader>
          <DrawerTitle>Filter results</DrawerTitle>
          <DrawerDescription>Refine hotels by distance, price and amenities.</DrawerDescription>
        </DrawerHeader>
        <DrawerFooter>
          <Button variant="dialogDefault">Apply filters</Button>
          <Button variant="dialogOutline">Reset</Button>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  ),
};

export const WithoutCloseButton: Story = {
  render: () => (
    <Drawer defaultOpen>
      <DrawerContent hasCloseButton={false}>
        <DrawerHeader>
          <DrawerTitle>Persistent panel</DrawerTitle>
          <DrawerDescription>Use action button to exit this drawer.</DrawerDescription>
        </DrawerHeader>
        <DrawerFooter>
          <Button>Done</Button>
        </DrawerFooter>
      </DrawerContent>
    </Drawer>
  ),
};
