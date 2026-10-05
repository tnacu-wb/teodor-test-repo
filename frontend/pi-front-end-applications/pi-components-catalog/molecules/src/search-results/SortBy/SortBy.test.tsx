import '@testing-library/jest-dom';

import { ButtonProps } from '../../../../atoms/dist/components/ui/Button';
import { render } from '../../utils/test-utils';
import SortBy from './SortBy.component';

const sharedButtonProps = {
  w: '100%',
  height: '40px',
  borderRadius: '0.75rem',
  display: 'flex !important',
  alignItems: 'center',
  justifyContent: 'center',
  gap: '0',
  px: '1rem',
  '& .chakra-icon': {
    marginInlineEnd: '0 !important',
    marginInlineStart: '0 !important',
  },
  fontSize: 'var(--chakra-fontSizes-md)',
} as Partial<ButtonProps>;

const baseProps = {
  labels: {
    sortByRecommended: 'Recommended',
    sortByDistance: 'Distance',
    sortByPrice: 'Price',
  },
  selectedOption: 'DISTANCE',
  onChange: jest.fn(),
  buttonProps: sharedButtonProps,
};

describe('Sort by component', () => {
  it('should render the sort by component with Distance as default value', () => {
    const { getByRole } = render(<SortBy {...baseProps} />);
    const button = getByRole('button');
    expect(button).toHaveTextContent('Distance');
  });
  it('should render the sort by component with Price as selected value', () => {
    const { getByRole } = render(<SortBy {...baseProps} selectedOption="PRICE" />);
    const button = getByRole('button');
    expect(button).toHaveTextContent('Price');
  });
  it('should render recommended option when enabled new order is enabled in pi', () => {
    const { getByRole } = render(
      <SortBy {...baseProps} selectedOption="RECOMMENDATION" isPiSortOrderDropdownEnabled={true} />
    );
    const button = getByRole('button');
    expect(button).toHaveTextContent('Recommended');
  });

  it('should render recommended option when enabled new order is enabled in bb', () => {
    const { getByRole } = render(<SortBy {...baseProps} isBbSortOrderDropdownEnabled={true} />);
    const button = getByRole('button');
    expect(button).toHaveTextContent('Distance');
  });

  it('should render the sort by component with Distance without buttonProps', () => {
    const { getByRole } = render(<SortBy {...baseProps} buttonProps={undefined} />);
    const button = getByRole('button');
    expect(button).toHaveTextContent('Distance');
  });
});
