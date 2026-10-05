import type { Meta, StoryObj } from '@storybook/react';

import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from './dropdown-menu';

const meta: Meta<typeof DropdownMenu> = {
  title: 'Shadcn/Dropdown',
  component: DropdownMenu,
  argTypes: {
    open: {
      description: 'Controlled open state for dropdown root.',
      control: 'boolean',
    },
    defaultOpen: {
      description: 'Initial open state for uncontrolled usage.',
      control: 'boolean',
    },
    modal: {
      description: 'Whether focus outside is trapped while open.',
      control: 'boolean',
    },
    onOpenChange: {
      description: 'Callback when open state changes.',
      control: false,
    },
    children: {
      description: 'Trigger and dropdown content elements.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Context menu/dropdown primitives for command and preference lists.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    defaultOpen: true,
    modal: true,
  },
  render: (args) => (
    <DropdownMenu {...args}>
      <DropdownMenuTrigger className="rounded border px-3 py-2">Open menu</DropdownMenuTrigger>
      <DropdownMenuContent className="w-56">
        <DropdownMenuLabel>Booking actions</DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <DropdownMenuItem>View itinerary</DropdownMenuItem>
          <DropdownMenuItem>Download invoice</DropdownMenuItem>
          <DropdownMenuCheckboxItem checked indicatorPosition="right">
            Email receipt
          </DropdownMenuCheckboxItem>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuSub>
          <DropdownMenuSubTrigger>More options</DropdownMenuSubTrigger>
          <DropdownMenuSubContent>
            <DropdownMenuItem>Help center</DropdownMenuItem>
            <DropdownMenuItem>Contact support</DropdownMenuItem>
          </DropdownMenuSubContent>
        </DropdownMenuSub>
      </DropdownMenuContent>
    </DropdownMenu>
  ),
};
