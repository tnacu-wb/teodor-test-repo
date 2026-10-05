import { register } from './instrumentation';

const mockInitNode = jest.fn();
jest.mock('./instrumentation-node', () => ({ initNode: mockInitNode }));

describe('instrumentation register', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    delete process.env.NEXT_RUNTIME;
  });

  afterEach(() => {
    delete process.env.NEXT_RUNTIME;
  });

  it('imports instrumentation-node when NEXT_RUNTIME is nodejs', async () => {
    process.env.NEXT_RUNTIME = 'nodejs';
    await register();

    expect(mockInitNode).toHaveBeenCalledTimes(1);
  });

  it('does not import instrumentation-node when NEXT_RUNTIME is edge', async () => {
    process.env.NEXT_RUNTIME = 'edge';
    await register();

    expect(mockInitNode).not.toHaveBeenCalled();
  });

  it('does not import instrumentation-node when NEXT_RUNTIME is not set', async () => {
    await register();

    expect(mockInitNode).not.toHaveBeenCalled();
  });

  it('does not import instrumentation-node when NEXT_RUNTIME is not exactly nodejs', async () => {
    process.env.NEXT_RUNTIME = 'node';
    await register();

    expect(mockInitNode).not.toHaveBeenCalled();
  });

  it('propagates error when initNode throws', async () => {
    process.env.NEXT_RUNTIME = 'nodejs';
    mockInitNode.mockRejectedValueOnce(new Error('init failed'));

    await expect(register()).rejects.toThrow('init failed');
    expect(mockInitNode).toHaveBeenCalledTimes(1);
  });
});
