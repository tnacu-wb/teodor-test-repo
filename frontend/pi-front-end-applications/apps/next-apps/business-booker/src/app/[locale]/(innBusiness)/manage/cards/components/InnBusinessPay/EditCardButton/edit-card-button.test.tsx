import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/react';

import { EditButton } from './edit-card-button';

const mockProps = {
  href: '/cards',
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

jest.mock('next/cache', () => ({
  revalidatePath: (path: string) => {
    return path;
  },
}));

describe('EditButton Card Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render EditButton component', async () => {
    const { getByTestId } = render(<EditButton {...mockProps} />);

    const button = getByTestId('Edit-WL-Card-Button');
    expect(button).toBeInTheDocument();

    await waitFor(() => {
      fireEvent.click(button);
    });
  });
});
