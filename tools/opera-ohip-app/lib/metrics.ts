/**
 * Lightweight in-process metrics collection.
 * Exposes counters and histograms via /api/metrics in Prometheus text format.
 */

interface Counter {
  value: number;
  labels: Record<string, number>;
}

interface HistogramBucket {
  count: number;
  sum: number;
  buckets: Record<number, number>;
}

const counters: Record<string, Counter> = {};
const histograms: Record<string, HistogramBucket> = {};

const DEFAULT_BUCKETS = [0.01, 0.05, 0.1, 0.25, 0.5, 1, 2.5, 5, 10];

export function incrementCounter(name: string, labels?: Record<string, string>) {
  if (!counters[name]) {
    counters[name] = { value: 0, labels: {} };
  }
  counters[name].value++;

  if (labels) {
    const key = Object.entries(labels)
      .map(([k, v]) => `${k}="${v}"`)
      .join(',');
    counters[name].labels[key] = (counters[name].labels[key] ?? 0) + 1;
  }
}

export function observeHistogram(name: string, value: number) {
  if (!histograms[name]) {
    histograms[name] = {
      count: 0,
      sum: 0,
      buckets: Object.fromEntries(DEFAULT_BUCKETS.map((b) => [b, 0])),
    };
  }
  histograms[name].count++;
  histograms[name].sum += value;
  for (const bucket of DEFAULT_BUCKETS) {
    if (value <= bucket) {
      histograms[name].buckets[bucket]++;
    }
  }
}

/**
 * Serializes all collected metrics in Prometheus exposition format.
 */
export function serializeMetrics(): string {
  const lines: string[] = [];

  for (const [name, counter] of Object.entries(counters)) {
    lines.push(`# TYPE ${name} counter`);
    if (Object.keys(counter.labels).length > 0) {
      for (const [labelSet, value] of Object.entries(counter.labels)) {
        lines.push(`${name}{${labelSet}} ${value}`);
      }
    } else {
      lines.push(`${name} ${counter.value}`);
    }
  }

  for (const [name, hist] of Object.entries(histograms)) {
    lines.push(`# TYPE ${name} histogram`);
    for (const [bucket, count] of Object.entries(hist.buckets)) {
      lines.push(`${name}_bucket{le="${bucket}"} ${count}`);
    }
    lines.push(`${name}_bucket{le="+Inf"} ${hist.count}`);
    lines.push(`${name}_sum ${hist.sum}`);
    lines.push(`${name}_count ${hist.count}`);
  }

  return lines.join('\n') + '\n';
}
