import type { Meta, StoryObj } from '@storybook/react';
import { useState } from 'react';

import { ExpandableTableRow } from './expandable-table-row';
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from './table';

const iconPixel = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';

const meta: Meta<typeof Table> = {
  title: 'Shadcn/Table',
  component: Table,
  argTypes: {
    className: {
      description: 'Optional class names for table element.',
      control: 'text',
    },
    children: {
      description: 'Header/body/footer table composition.',
      control: false,
    },
  },
  parameters: {
    docs: {
      description: {
        component: 'Table primitives plus expandable row helper for rich data displays.',
      },
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  render: () => (
    <Table>
      <TableCaption>Company traveller rates</TableCaption>
      <TableHeader>
        <TableRow>
          <TableHead>Hotel</TableHead>
          <TableHead>Nightly Rate</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <TableRow>
          <TableCell>Premier Inn London City</TableCell>
          <TableCell>£145</TableCell>
        </TableRow>
        <TableRow>
          <TableCell>Premier Inn Manchester Central</TableCell>
          <TableCell>£132</TableCell>
        </TableRow>
      </TableBody>
    </Table>
  ),
};

const ExpandableRowStory = () => {
  const [expanded, setExpanded] = useState(false);

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Hotel</TableHead>
          <TableHead>Rate</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        <ExpandableTableRow
          expanded={expanded}
          onTableRowExpanded={setExpanded}
          expandIcon={iconPixel}
          collapseIcon={iconPixel}
          testId="expandable-rate-row"
          expandableContent={
            <div className="p-2 text-sm">Breakfast included and free cancellation.</div>
          }
        >
          <TableCell>Premier Inn Birmingham</TableCell>
          <TableCell>£118</TableCell>
        </ExpandableTableRow>
      </TableBody>
    </Table>
  );
};

export const ExpandableRow: Story = {
  render: () => <ExpandableRowStory />,
};
