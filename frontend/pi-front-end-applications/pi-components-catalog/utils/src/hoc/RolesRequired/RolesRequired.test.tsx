import { render } from '../../utils/test-utils';
import RolesRequired from './RolesRequired.component';

const mockProps = {
  userRole: 'SUPER',
  requiredRoles: ['SUPER'],
};

describe('RolesRequired Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render children', () => {
    const { getByText } = render(
      <RolesRequired {...mockProps}>
        <span>RENDERED CHILDREN</span>
      </RolesRequired>
    );
    expect(getByText('RENDERED CHILDREN')).toBeTruthy();
  });

  it('should NOT render children', () => {
    mockProps.requiredRoles = ['BOOKER'];
    const { queryByText } = render(
      <RolesRequired {...mockProps}>
        <span>RENDERED CHILDREN</span>
      </RolesRequired>
    );
    expect(queryByText('RENDERED CHILDREN')).not.toBeTruthy();
  });
});
