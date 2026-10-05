'use client';

import type { ResponseState } from '@/lib/types';
export type { ResponseState } from '@/lib/types';

export default function ResponsePanel({ response }: { response: ResponseState }) {
  const getStatusClass = () => {
    switch (response.status) {
      case 'success':
        return 'response-status success';
      case 'error':
        return 'response-status error';
      case 'loading':
        return 'response-status inprogress';
      default:
        return 'response-status';
    }
  };
  const getStatusText = () => {
    switch (response.status) {
      case 'success':
        return `\u2713 Success \u2014 ${response.operation} completed at ${response.timestamp}`;
      case 'error':
        return `\u2717 Error \u2014 ${response.operation} failed at ${response.timestamp}`;
      case 'loading':
        return `\u23F3 In Progress \u2014 ${response.operation}`;
      default:
        return '';
    }
  };
  const formatBody = () => {
    if (response.status === 'idle') return 'No requests sent yet.';
    if (response.status === 'loading') return 'Processing request... Please wait.';
    let output = `\u2500\u2500 ${response.operation} \u2500\u2500\nTimestamp: ${response.timestamp}\nStatus: ${response.status === 'success' ? 'SUCCESS' : 'FAILED'}\n\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\n\n`;
    if (response.data?.error) {
      output +=
        'Error Details:\n' +
        (typeof response.data.error === 'object' ? JSON.stringify(response.data.error, null, 2) : response.data.error) +
        '\n';
    }
    if (response.data?.data) {
      output += 'Response Data:\n' + JSON.stringify(response.data.data, null, 2) + '\n';
    }
    if (response.data?.message) {
      output += `\n${response.data.message}\n`;
    }
    if (response.data?.dateRange) {
      const dr = response.data.dateRange as { startDate?: string; endDate?: string };
      output += `\nDate Range: ${dr.startDate} to ${dr.endDate}\n`;
    }
    if (response.data?.errors && Array.isArray(response.data.errors) && response.data.errors.length > 0) {
      output += '\nFailed Dates:\n';
      response.data.errors.forEach((e: { date?: string; error?: unknown }) => {
        output += `  \u2022 ${e.date}: ${typeof e.error === 'object' ? JSON.stringify(e.error) : e.error}\n`;
      });
    }
    if (response.data?.payload) {
      output += '\nRequest Payload:\n' + JSON.stringify(response.data.payload, null, 2) + '\n';
    }
    return output;
  };
  return (
    <div className="response-panel" aria-live="polite">
      <h3>API Response</h3>
      {response.status !== 'idle' && <div className={getStatusClass()}>{getStatusText()}</div>}
      <pre className="response-body">{formatBody()}</pre>
    </div>
  );
}
