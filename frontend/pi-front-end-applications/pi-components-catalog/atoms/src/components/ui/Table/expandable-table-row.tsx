'use client';

import Image from 'next/image';
import { ComponentProps, ReactElement } from 'react';

import { TableCell, TableRow } from './table';

type Props = {
  children: ReactElement<typeof TableCell>[];
  expandableContent: ReactElement;
  expanded: boolean;
  expandIcon: string;
  collapseIcon: string;
  testId?: string;
  onTableRowExpanded?: (value: boolean) => void;
};

export function ExpandableTableRow({
  children,
  expandableContent,
  expanded,
  expandIcon,
  collapseIcon,
  testId,
  onTableRowExpanded,
}: Props) {
  if (!children.length) {
    children = [children] as unknown as ReactElement<typeof TableCell>[];
  }
  const handleOnExpandedClick = () => {
    onTableRowExpanded && onTableRowExpanded(!expanded);
  };

  const modifiedChildren = children.map((child, index) => {
    const props = child.props as ComponentProps<typeof TableCell>;

    return (
      <TableCell {...props} key={index}>
        <div className={cellStyle}>
          <div>{props.children}</div>
          {index === children.length - 1 && (
            <Image
              data-testid="Expand-Collapse-Icon"
              src={expanded ? collapseIcon : expandIcon}
              alt={expanded ? 'collapse' : 'expand'}
              width={24}
              height={24}
            />
          )}
        </div>
      </TableCell>
    );
  });

  return (
    <>
      <TableRow data-testid={testId} onClick={() => handleOnExpandedClick()} className={rowStyle}>
        {modifiedChildren}
      </TableRow>
      {expanded && (
        <TableRow data-testid={`${testId}-expanded`}>
          <TableCell colSpan={children.length} className={expandableContentStyle}>
            {expandableContent}
          </TableCell>
        </TableRow>
      )}
    </>
  );
}

const rowStyle = 'cursor-pointer';
const cellStyle = 'flex justify-between items-center';
const expandableContentStyle = 'bg-lightGrey4';
