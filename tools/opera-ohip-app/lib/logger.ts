/**
 * Structured JSON logger for production observability.
 * Outputs JSON lines that can be parsed by log aggregators (CloudWatch, Datadog, etc.).
 */

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

interface LogEntry {
  timestamp: string;
  level: LogLevel;
  message: string;
  [key: string]: unknown;
}

function createEntry(level: LogLevel, message: string, meta?: Record<string, unknown>): LogEntry {
  return {
    timestamp: new Date().toISOString(),
    level,
    message,
    ...meta,
  };
}

export const logger = {
  debug(message: string, meta?: Record<string, unknown>) {
    if (process.env.LOG_LEVEL === 'debug') {
      process.stdout.write(JSON.stringify(createEntry('debug', message, meta)) + '\n');
    }
  },
  info(message: string, meta?: Record<string, unknown>) {
    process.stdout.write(JSON.stringify(createEntry('info', message, meta)) + '\n');
  },
  warn(message: string, meta?: Record<string, unknown>) {
    process.stderr.write(JSON.stringify(createEntry('warn', message, meta)) + '\n');
  },
  error(message: string, meta?: Record<string, unknown>) {
    process.stderr.write(JSON.stringify(createEntry('error', message, meta)) + '\n');
  },
};
