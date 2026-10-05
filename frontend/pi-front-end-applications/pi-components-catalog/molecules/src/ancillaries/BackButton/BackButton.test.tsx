import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import BackButton from './BackButton.component';

const mockClick = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    back: mockClick,
  }),
}));

describe('<BackButton/>', () => {
  it('should render a <BackButton/> ', () => {
    const { getByTestId } = render(<BackButton />);
    const button = getByTestId('BackToHDPButton');
    expect(button).toBeInTheDocument();
    fireEvent.click(button);
    expect(mockClick).toBeCalled();
  });
});
