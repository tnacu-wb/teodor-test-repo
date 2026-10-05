import type { Meta, StoryObj } from '@storybook/react';

import {
  Pagination,
  PaginationButton,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from './pagination';

const iconPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

const meta: Meta<typeof Pagination> = {
  title: 'Shadcn/Pagination',
  component: Pagination,
  argTypes: {
    className: {
      description: 'Additional class names for navigation wrapper.',
      control: 'text',
    },
    children: {
      description: 'Pagination item composition.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Pagination primitives for search and listing views.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: () => (
    <Pagination>
      <PaginationContent>
        <PaginationItem>
          <PaginationPrevious icon={iconPixel} aria-label="Previous page" />
        </PaginationItem>
        <PaginationItem>
          <PaginationButton isActive>1</PaginationButton>
        </PaginationItem>
        <PaginationItem>
          <PaginationButton>2</PaginationButton>
        </PaginationItem>
        <PaginationItem>
          <PaginationEllipsis />
        </PaginationItem>
        <PaginationItem>
          <PaginationButton>8</PaginationButton>
        </PaginationItem>
        <PaginationItem>
          <PaginationNext icon={iconPixel} aria-label="Next page" />
        </PaginationItem>
      </PaginationContent>
    </Pagination>
  ),
};
