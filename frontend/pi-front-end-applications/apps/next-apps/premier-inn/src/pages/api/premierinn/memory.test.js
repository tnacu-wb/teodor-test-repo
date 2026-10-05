import { constants } from 'http2';
import v8 from 'node:v8';

import handler from './memory';

jest.mock('node:v8', () => ({
  getHeapStatistics: jest.fn(),
}));

describe('Memory API Handler', () => {
  let req;
  let res;
  let originalExecArgv;
  let originalNodeOptions;

  beforeEach(() => {
    // Save original values so we can restore them after each test
    originalExecArgv = process.execArgv;
    originalNodeOptions = process.env.NODE_OPTIONS;

    // Mock request and response objects
    req = {
      // If needed, you can set req.method or req.body, but this route doesn't check them
    };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn(),
    };
  });

  afterEach(() => {
    // Restore original values to avoid side effects between tests
    process.execArgv = originalExecArgv;
    process.env.NODE_OPTIONS = originalNodeOptions;
    jest.clearAllMocks();
  });

  it('should respond with 200 and "Not set" if --max-old-space-size is not found', () => {
    process.execArgv = [];

    const mockStats = { heap_size_limit: 123456789, used_heap_size: 123456789 };
    v8.getHeapStatistics.mockReturnValue(mockStats);

    handler(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_OK);
    expect(res.json).toHaveBeenCalledWith({
      heap: 123456789,
      node_opt: originalNodeOptions, // whatever was originally set or undefined
      maxOldSpaceSizeMB: 'Not set',
      heapLimitMB: (123456789 / 1024 / 1024).toFixed(2),
      execArgv: [],
      usedMem: '117.74',
    });
  });

  it('should respond with correct maxOldSpaceSizeMB if --max-old-space-size is present', () => {
    process.execArgv = ['--max-old-space-size=2048'];

    process.env.NODE_OPTIONS = '--some-other-flag';

    // Mock v8 stats
    const mockStats = { heap_size_limit: 987654321, used_heap_size: 123456789 };
    v8.getHeapStatistics.mockReturnValue(mockStats);

    handler(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_OK);
    expect(res.json).toHaveBeenCalledWith({
      heap: 987654321,
      node_opt: '--some-other-flag',
      maxOldSpaceArg: '--max-old-space-size=2048',
      maxOldSpaceSizeMB: '2048 MB',
      heapLimitMB: (987654321 / 1024 / 1024).toFixed(2),
      execArgv: ['--max-old-space-size=2048'],
      usedMem: '117.74',
    });
  });

  it('should correctly reflect the updated heap stats from v8', () => {
    process.execArgv = ['--max-old-space-size=512'];

    const mockStats = { heap_size_limit: 4000000000, used_heap_size: 123456789 }; // ~4GB
    v8.getHeapStatistics.mockReturnValue(mockStats);

    handler(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_OK);
    expect(res.json).toHaveBeenCalledWith({
      heap: 4000000000,
      node_opt: originalNodeOptions,
      maxOldSpaceArg: '--max-old-space-size=512',
      maxOldSpaceSizeMB: '512 MB',
      heapLimitMB: (4000000000 / 1024 / 1024).toFixed(2),
      execArgv: ['--max-old-space-size=512'],
      usedMem: '117.74',
    });
  });
});
