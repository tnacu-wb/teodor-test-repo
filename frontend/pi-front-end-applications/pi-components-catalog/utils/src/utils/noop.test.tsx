import noop from './noop';

describe('noop function', () => {
  it('does nothing', () => {
    // Mock console methods to ensure they are not called
    const consoleLogSpy = jest.spyOn(console, 'log');

    noop();
    expect(consoleLogSpy).not.toHaveBeenCalled();
    consoleLogSpy.mockRestore();
  });
});
