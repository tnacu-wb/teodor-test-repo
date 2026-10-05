import '@testing-library/jest-dom';
import { fireEvent, act, render } from '@testing-library/react';

import { BackButton } from './back-button';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: jest.fn(() => ({
    push: jest.fn(),
  })),
}));

jest.mock('../../../components/revalidate-link', () => ({
  revalidateCacheOnLink: jest.fn(),
}));

const mockProps = {
  text: 'test',
  className: 'flex',
  path: '/test-path',
};

describe('Back Button Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the Back Button', async () => {
    const { getByTestId } = render(<BackButton {...mockProps} />);
    const button = getByTestId('Back-Button');
    expect(button).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(button);
    });
  });
});
