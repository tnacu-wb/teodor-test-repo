import type { Meta, StoryObj } from '@storybook/react';

import { Skeleton } from './skeleton';

const meta: Meta<typeof Skeleton> = {
  title: 'Shadcn/Skeleton',
  component: Skeleton,
  argTypes: {
    className: {
      description: 'Utility classes defining skeleton size and shape.',
      control: 'text',
    },
    children: {
      description: 'Optional content (normally omitted for skeletons).',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Loading placeholder block used during async content fetches.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const CardRows: Story = {
  render: () => (
    <div className="space-y-3 w-full max-w-[360px] rounded-md border border-lightGrey3 bg-baseWhite p-4">
      <Skeleton className="h-6 w-40 bg-lightGrey3" />
      <Skeleton className="h-4 w-full bg-lightGrey3" />
      <Skeleton className="h-4 w-4/5 bg-lightGrey3" />
    </div>
  ),
};

export const AvatarAndText: Story = {
  render: () => (
    <div className="flex w-full max-w-[360px] items-center gap-3 rounded-md border border-lightGrey3 bg-baseWhite p-4">
      <Skeleton className="h-12 w-12 rounded-full bg-lightGrey3" />
      <div className="space-y-2 w-full">
        <Skeleton className="h-4 w-40 bg-lightGrey3" />
        <Skeleton className="h-4 w-28 bg-lightGrey3" />
      </div>
    </div>
  ),
};
