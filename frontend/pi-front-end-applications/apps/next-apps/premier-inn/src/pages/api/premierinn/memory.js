import v8 from 'node:v8';

export default function handler(req, res) {
  const maxOldSpaceArg = process.execArgv.find((a) => a.includes('--max-old-space-size'));
  let maxOldSpaceSizeMB = 'Not set';
  if (maxOldSpaceArg) {
    const [, size] = maxOldSpaceArg.split('=');
    maxOldSpaceSizeMB = size + ' MB';
  }

  const stats = v8.getHeapStatistics();
  const heapLimitMB = (stats.heap_size_limit / 1024 / 1024).toFixed(2);

  res.status(200).json({
    heap: stats.heap_size_limit,
    usedMem: (stats.used_heap_size / 1024 / 1024).toFixed(2),
    node_opt: process.env.NODE_OPTIONS,
    maxOldSpaceSizeMB,
    maxOldSpaceArg,
    heapLimitMB,
    execArgv: process.execArgv, // For debugging
  });
}
