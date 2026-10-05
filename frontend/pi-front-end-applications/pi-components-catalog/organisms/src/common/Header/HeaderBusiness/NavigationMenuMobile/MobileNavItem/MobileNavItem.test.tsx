import { render } from '@testing-library/react';

import { mobileHeaderLabels } from '../../mockResponse';
import MobileNavItem, { type Props } from './MobileNavItem.component';

const mockToggle = jest.fn();
const mockIsLogoutButton = jest.fn();

const mockProps: Props = {
  navigationLabels: mobileHeaderLabels,
  navTitleLabel: 'Bookings',
  baseDataTestId: 'BusinessNavMobile',
  toggleSideNav: mockToggle,
  isLogoutButton: mockIsLogoutButton,
};

describe('BusinessNavMenu', () => {
  it('should render a <MobileNavItem> with set props ', function () {
    const { getByTestId } = render(<MobileNavItem {...mockProps} />);
    expect(getByTestId('BusinessNavMobile-Bookings-MobileLink')).toBeInTheDocument();
  });
});
