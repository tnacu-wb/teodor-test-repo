import '@testing-library/jest-dom';

import { truncateLabel } from './truncateLabel';

describe('truncateLabel Method', () => {
  it('should return truncated label ', function () {
    const label = 'Room 1';
    expect(truncateLabel(label, 7)).toEqual('Room...');
  });
});
