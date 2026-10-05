import { Meta, StoryObj } from '@storybook/react';

import Breadcrumb, { BreadcrumbItemProps } from './Breadcrumb.component';

const breadcrumbItems: BreadcrumbItemProps[] = [
  { url: '#input1', name: 'Input 1' },
  { url: '#input2', name: 'Input 2' },
  { url: '#input3', name: 'Input 3' },
  { url: '#input4', name: 'Input 4' },
  { url: '#input5', name: 'Input 5' },
  { url: '#input6', name: 'Input 6' },
  { url: '#', isCurrentPage: true, name: 'Input 7' },
];

const shortBreadcrumbItems: BreadcrumbItemProps[] = [
  { url: '#home', name: 'Home' },
  { url: '#products', name: 'Products' },
  { url: '#', isCurrentPage: true, name: 'Details' },
];

const singleBreadcrumbItem: BreadcrumbItemProps[] = [
  { url: '#', isCurrentPage: true, name: 'Home' },
];

const longNameBreadcrumbItems: BreadcrumbItemProps[] = [
  { url: '#home', name: 'Home' },
  { url: '#category', name: 'Hotels & Accommodation in the Greater London Area' },
  {
    url: '#',
    isCurrentPage: true,
    name: 'Premier Inn London County Hall hotel — Detailed Information & Booking',
  },
];

const meta: Meta<typeof Breadcrumb> = {
  title: 'Breadcrumb',
  component: Breadcrumb,
  argTypes: {
    items: {
      description:
        'Array of breadcrumb items to display with url, name, and optional isCurrentPage flag',
      control: { type: 'object' },
    },
    size: {
      description: 'Size variant of the breadcrumb (e.g., "sm" for small)',
      control: { type: 'text' },
    },
    variant: {
      description: 'Visual variant of the breadcrumb (e.g., "mobile" for mobile layout)',
      control: { type: 'text' },
    },
    paddingRight: {
      description: 'Custom right padding for the breadcrumb container',
      control: { type: 'text' },
    },
    linkComponent: {
      description: 'Custom link component to use instead of Chakra Link (useful for Next.js Link)',
      control: false,
    },
    openInNewTab: {
      description: 'Whether breadcrumb links should open in a new tab',
      control: { type: 'boolean' },
    },
  },
  parameters: {
    docs: {
      description: {
        component:
          "Navigational breadcrumb trail showing the user's location within the site hierarchy.",
      },
    },
    design: {
      type: 'figma',
      url: 'https://www.figma.com/file/AiVCYlZ4KpoZFJPp6EuvJU/Design-system-2.0?node-id=1463%3A56674',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

// --- Basic Usage ---

export const Default: Story = {
  args: {
    items: breadcrumbItems,
  },
};

export const WithShortPath: Story = {
  args: {
    items: shortBreadcrumbItems,
  },
};

// --- Variants ---

export const MobileVariant: Story = {
  args: {
    items: breadcrumbItems,
    size: 'sm',
    variant: 'mobile',
    paddingRight: '40px',
  },
};

// --- Configuration ---

export const WithLinksOpeningInNewTab: Story = {
  args: {
    items: breadcrumbItems,
    openInNewTab: true,
  },
};

// --- Edge Cases ---

export const WithSingleItem: Story = {
  args: {
    items: singleBreadcrumbItem,
  },
};

export const WithLongNames: Story = {
  args: {
    items: longNameBreadcrumbItems,
  },
};
