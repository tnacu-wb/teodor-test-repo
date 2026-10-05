import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import { mockTabItems } from '../HotelRooms/HotelRooms.test';
import { HotelRoomContent } from './HotelRoomContent.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockedProps = {
  isLessThanSm: false,
  isLessThanMd: false,
  isLessThanLg: false,
};

describe('HotelRoomContent', () => {
  it('should render HotelRoomContent', () => {
    const { getByText } = render(<HotelRoomContent tabItems={mockTabItems} {...mockedProps} />);
    expect(getByText('Standard double')).toBeInTheDocument();
    expect(getByText('Premier Plus double')).toBeInTheDocument();
    expect(getByText('Standard accessible')).toBeInTheDocument();
  });
});
