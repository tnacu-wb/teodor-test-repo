import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { usePathname } from 'next/navigation';

import { InnBLink } from '~components/innBusiness/InnBLink';

const mockProps = {
  href: '/de-de/hompeage',
};

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(),
}));
const mockUsePathname = usePathname as jest.Mock;

describe('InnBLink Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InnBLink component', () => {
    const { getByTestId } = render(
      <InnBLink data-testid="my-link-id" {...mockProps}>
        {'my-link'}
      </InnBLink>
    );

    expect(getByTestId('my-link-id')).toBeInTheDocument();
  });

  it('renders a -Link- when current path and href have the same locale TYPE (IB EN to IB EN)', () => {
    mockUsePathname.mockReturnValue('/en-gb/homepage');
    const { container } = render(<InnBLink href="/en-gb/homepage">About</InnBLink>);

    expect(container.querySelector('[data-type="anchor"]')).not.toBeInTheDocument();
  });

  it('renders a -Link- when current path and href have the same locale TYPE (IB EN to IB DE)', () => {
    mockUsePathname.mockReturnValue('/en-gb/homepage');
    const { container } = render(<InnBLink href="/de-de/homepage">About</InnBLink>);

    expect(container.querySelector('[data-type="anchor"]')).not.toBeInTheDocument();
  });

  it('renders an -a- tag when current path and href have different locale TYPE (ib EN to bb EN)', () => {
    mockUsePathname.mockReturnValue('/en-gb/homepage');
    const { container } = render(<InnBLink href="/gb/en/homepage">About</InnBLink>);

    expect(container.querySelector('[data-type="anchor"]')).toBeInTheDocument();
  });

  it('renders an -a- tag when current path and href have different locale TYPE (ib EN to bb DE)', () => {
    mockUsePathname.mockReturnValue('/en-gb/homepage');
    const { container } = render(<InnBLink href="/de/de/homepage">About</InnBLink>);

    expect(container.querySelector('[data-type="anchor"]')).toBeInTheDocument();
  });
});
