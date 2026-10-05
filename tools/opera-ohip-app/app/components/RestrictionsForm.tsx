'use client';
import { FormEvent, useState } from 'react';
import ResponsePanel, { ResponseState } from './ResponsePanel';
import { EnvironmentOption } from './EnvironmentSelector';

export default function RestrictionsForm({ environment }: { environment: EnvironmentOption }) {
  const [loading, setLoading] = useState(false);
  const [progress, setProgress] = useState('');
  const [response, setResponse] = useState<ResponseState>({ status: 'idle' });

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const operation = 'Clear Hotel Restrictions';
    const hotelId = (formData.get('hotelId') as string).trim();
    const startDate = formData.get('startDate') as string;
    const endDate = formData.get('endDate') as string;

    if (!hotelId) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Hotel ID is required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (!startDate || !endDate) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Start and End Date are required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }

    const dates: string[] = [];
    const current = new Date(startDate);
    const end = new Date(endDate);
    while (current <= end) {
      dates.push(current.toISOString().split('T')[0]);
      current.setDate(current.getDate() + 1);
    }
    if (dates.length === 0) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'End date must be on or after start date.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }

    setLoading(true);
    setResponse({ status: 'loading', operation });
    const results = { success: 0, failed: 0, errors: [] as Array<{ date: string; error: string }> };

    for (let i = 0; i < dates.length; i++) {
      setProgress(`Processing ${i + 1} of ${dates.length}: ${dates[i]}`);
      try {
        const res = await fetch('/api/restrictions', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ hotelId, payload: { hotelId, date: dates[i] }, environment }),
        });
        const data = await res.json();
        if (data.success) {
          results.success++;
        } else {
          results.failed++;
          results.errors.push({ date: dates[i], error: data.error });
        }
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : 'Unknown error';
        results.failed++;
        results.errors.push({ date: dates[i], error: message });
      }
    }

    setProgress('');
    setLoading(false);
    setResponse({
      status: results.failed === 0 ? 'success' : 'error',
      operation,
      data: {
        message: `Cleared: ${results.success} succeeded, ${results.failed} failed out of ${dates.length} days`,
        dateRange: { startDate, endDate },
        ...(results.errors.length > 0 && { errors: results.errors }),
      },
      timestamp: new Date().toLocaleString(),
    });
  }

  return (
    <section className="tab-panel active" role="tabpanel">
      <h2>Clear Hotel Restrictions</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="rs-hi">Hotel ID</label>
          <input type="text" id="rs-hi" name="hotelId" required placeholder="e.g. HOTEL1" />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="rs-sd">Start Date</label>
            <input type="date" id="rs-sd" name="startDate" required />
          </div>
          <div className="form-group">
            <label htmlFor="rs-ed">End Date</label>
            <input type="date" id="rs-ed" name="endDate" required />
          </div>
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Clearing...' : 'Clear Restrictions'}
        </button>
        {progress && <p className="progress-text">{progress}</p>}
      </form>
      <ResponsePanel response={response} />
    </section>
  );
}
