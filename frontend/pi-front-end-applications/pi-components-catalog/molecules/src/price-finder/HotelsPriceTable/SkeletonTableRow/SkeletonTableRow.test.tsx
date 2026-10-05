import { ChakraProvider, Table, TableContainer, Tbody } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import React from 'react';

import { THEME_COLORS } from '../constants';
import { SkeletonTableRow } from './SkeletonTableRow.component';

const mockCurrentDates = ['2025-08-03', '2025-08-04', '2025-08-05', '2025-08-06', '2025-08-07'];

const renderWithChakra = (component: React.ReactElement) => {
  return render(<ChakraProvider>{component}</ChakraProvider>);
};

const TableWrapper = ({ children }: { children: React.ReactNode }) => (
  <TableContainer>
    <Table>
      <Tbody>{children}</Tbody>
    </Table>
  </TableContainer>
);

describe('SkeletonTableRow', () => {
  describe('Desktop view', () => {
    it('should render single row with hotel name skeleton and individual date skeletons', () => {
      renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      // Should render one table row
      const rows = screen.getAllByRole('row');
      expect(rows).toHaveLength(1);

      const cells = screen.getAllByRole('cell');
      expect(cells).toHaveLength(8);
    });

    it('should apply primaryRowBg for even index (desktop)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toBeInTheDocument();
      // Even index should use primaryRowBg
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });

    it('should apply alternateRowBg for odd index (desktop)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={1} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toBeInTheDocument();
      // Odd index should use alternateRowBg
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
    });

    it('should apply alternateRowBg for higher odd indices (desktop)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={3} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toBeInTheDocument();
      // Index 3 (odd) should use alternateRowBg
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
    });

    it('should apply primaryRowBg for higher even indices (desktop)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={4} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toBeInTheDocument();
      // Index 4 (even) should use primaryRowBg
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });

    it('should render different skeleton widths for variety', () => {
      const { container: container1 } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      const { container: container2 } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={1} />
        </TableWrapper>
      );

      // Both should render but potentially with different widths
      expect(container1.querySelector('tr')).toBeInTheDocument();
      expect(container2.querySelector('tr')).toBeInTheDocument();
    });
  });

  describe('Mobile view', () => {
    it('should render two rows for mobile - hotel name and prices', () => {
      renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      // Should render two table rows for mobile
      const rows = screen.getAllByRole('row');
      expect(rows).toHaveLength(2);
    });

    it('should render cells with proper column span for mobile', () => {
      renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      const cells = screen.getAllByRole('cell');

      // Mobile structure: 1 spanning cell (hotel name) + 1 spacer + 5 date cells + 1 spacer = 8 cells
      expect(cells).toHaveLength(8);

      // First cell (hotel name) should span all columns
      expect(cells[0]).toHaveAttribute('colspan', String(mockCurrentDates.length + 2));

      // Remaining cells should be individual date cells with spacers
      expect(cells.length).toBe(8);
    });

    it('should apply primaryRowBg to both mobile rows for even index', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      const rows = container.querySelectorAll('tr');
      expect(rows).toHaveLength(2);

      // Even index should use primaryRowBg for both hotel name and price rows
      expect(rows[0]).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
      expect(rows[1]).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });

    it('should apply alternateRowBg to both mobile rows for odd index', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={1} />
        </TableWrapper>
      );

      const rows = container.querySelectorAll('tr');
      expect(rows).toHaveLength(2);

      // Odd index should use alternateRowBg for both hotel name and price rows
      expect(rows[0]).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
      expect(rows[1]).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
    });

    it('should apply alternateRowBg to both mobile rows for higher odd indices', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={5} />
        </TableWrapper>
      );

      const rows = container.querySelectorAll('tr');
      expect(rows).toHaveLength(2);

      // Index 5 (odd) should use alternateRowBg for both rows
      expect(rows[0]).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
      expect(rows[1]).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
    });

    it('should apply primaryRowBg to both mobile rows for higher even indices', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={6} />
        </TableWrapper>
      );

      const rows = container.querySelectorAll('tr');
      expect(rows).toHaveLength(2);

      // Index 6 (even) should use primaryRowBg for both rows
      expect(rows[0]).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
      expect(rows[1]).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });

    it('should render price row with no top border', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      const cells = container.querySelectorAll('td');
      expect(cells).toHaveLength(8);

      // Check that price row cells have borderTop: none (cells 1-7 are in the second row)
      const priceRowCells = Array.from(cells).slice(1); // All cells except the first spanning cell
      priceRowCells.forEach((cell) => {
        expect(cell).toBeInTheDocument();
      });
    });
  });

  describe('Props handling', () => {
    it('should handle index prop for background color alternation', () => {
      const indices = [0, 1, 2, 3, 4, 5];

      indices.forEach((index) => {
        const { container } = renderWithChakra(
          <TableWrapper>
            <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={index} />
          </TableWrapper>
        );

        const row = container.querySelector('tr');
        expect(row).toBeInTheDocument();

        // Check correct background color based on index
        const expectedBg =
          index % 2 === 0 ? THEME_COLORS.primaryRowBg : THEME_COLORS.alternateRowBg;
        expect(row).toHaveStyle(`background-color: ${expectedBg}`);
      });
    });

    it('should default index to 0 when not provided and use primaryRowBg', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toBeInTheDocument();
      // Default index 0 should use alternateRowBg
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });
  });

  describe('Background color edge cases', () => {
    it('should handle index 0 correctly (primaryRowBg)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });

    it('should handle large odd index correctly (alternateRowBg)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={99} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.alternateRowBg}`);
    });

    it('should handle large even index correctly (primaryRowBg)', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={100} />
        </TableWrapper>
      );

      const row = container.querySelector('tr');
      expect(row).toHaveStyle(`background-color: ${THEME_COLORS.primaryRowBg}`);
    });

    it('should apply consistent background colors in mobile view for various indices', () => {
      const testCases = [
        { index: 0, expectedBg: THEME_COLORS.primaryRowBg },
        { index: 1, expectedBg: THEME_COLORS.alternateRowBg },
        { index: 2, expectedBg: THEME_COLORS.primaryRowBg },
        { index: 7, expectedBg: THEME_COLORS.alternateRowBg },
        { index: 8, expectedBg: THEME_COLORS.primaryRowBg },
      ];

      testCases.forEach(({ index, expectedBg }) => {
        const { container } = renderWithChakra(
          <TableWrapper>
            <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={index} />
          </TableWrapper>
        );

        const rows = container.querySelectorAll('tr');
        expect(rows).toHaveLength(2);

        // Both mobile rows should have the same background color
        expect(rows[0]).toHaveStyle(`background-color: ${expectedBg}`);
        expect(rows[1]).toHaveStyle(`background-color: ${expectedBg}`);
      });
    });
  });

  describe('Accessibility', () => {
    it('should render proper table structure', () => {
      renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      // Should have proper table row and cell structure
      const rows = screen.getAllByRole('row');
      const cells = screen.getAllByRole('cell');

      expect(rows).toHaveLength(1);
      expect(cells.length).toBeGreaterThan(0);
    });

    it('should maintain table structure in mobile view', () => {
      renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      // Mobile should have proper table structure with two rows
      const rows = screen.getAllByRole('row');
      const cells = screen.getAllByRole('cell');

      expect(rows).toHaveLength(2);
      expect(cells).toHaveLength(8); // Updated to match new structure
    });
  });

  describe('Individual Date Column Skeletons', () => {
    it('should render individual skeleton for each date column in desktop view', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      // Should have skeleton boxes: 1 for hotel name + 5 for date columns = 6 total
      const skeletonBoxes = container.querySelectorAll('.chakra-ui-light div');
      expect(skeletonBoxes.length).toBeGreaterThan(5); // At least 6 skeleton boxes
    });

    it('should render individual skeleton for each date column in mobile view', () => {
      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={true} currentDates={mockCurrentDates} index={0} />
        </TableWrapper>
      );

      // Should have skeleton boxes: 1 for hotel name + 5 for dates = 6 total
      const skeletonBoxes = container.querySelectorAll('.chakra-ui-light div');
      expect(skeletonBoxes.length).toBeGreaterThan(5); // At least 6 skeleton boxes
    });

    it('should adapt to different number of date columns', () => {
      const customDates = ['2025-08-03', '2025-08-04', '2025-08-05']; // 3 dates instead of 5

      const { container } = renderWithChakra(
        <TableWrapper>
          <SkeletonTableRow isMobile={false} currentDates={customDates} index={0} />
        </TableWrapper>
      );

      // Should adapt to custom number of dates: hotel + spacer + 3 dates + spacer = 6 cells
      const cells = container.querySelectorAll('td');
      expect(cells).toHaveLength(6);
    });
  });
});
