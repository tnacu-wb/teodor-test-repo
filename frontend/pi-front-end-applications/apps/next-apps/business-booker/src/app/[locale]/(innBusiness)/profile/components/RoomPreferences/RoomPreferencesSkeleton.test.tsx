import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import RoomPreferencesSkeleton from './RoomPreferencesSkeleton';

describe('RoomPreferencesSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render RoomPreferencesSkeleton component', async () => {
    const { getByTestId } = render(<RoomPreferencesSkeleton />);

    expect(getByTestId('RoomPreferencesSkeleton')).toBeInTheDocument();
  });
});
