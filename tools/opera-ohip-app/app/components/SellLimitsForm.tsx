'use client';
import { FormEvent, useState } from 'react';
import ResponsePanel, { ResponseState } from './ResponsePanel';
import { EnvironmentOption } from './EnvironmentSelector';

const ROOM_CLASS_VALUES = [
  { value: 'ST', label: 'ST - Standard' },
  { value: 'PP', label: 'PP - Premier Plus' },
  { value: 'BG', label: 'BG - Bigger' },
  { value: 'SV', label: 'SV - Superior' },
  { value: 'PV', label: 'PV - Premier View' },
  { value: 'BV', label: 'BV - Bigger View' },
  { value: 'SE', label: 'SE - Suite/Executive' },
];

export default function SellLimitsForm({ environment }: { environment: EnvironmentOption }) {
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<ResponseState>({ status: 'idle' });
  const [codeCategory, setCodeCategory] = useState('RoomType');

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const operation = 'Update Hotel Availability / Sell Limits';
    const hotelId = (formData.get('hotelId') as string).trim();
    const codeCategoryVal = (formData.get('codeCategory') as string).trim();
    const codeValue = (formData.get('codeValue') as string).trim();
    const actionType = formData.get('actionType') as string;
    const startDate = formData.get('startDate') as string;
    const endDate = formData.get('endDate') as string;
    const amount = formData.get('amount') as string;
    const flatOrPercentage = formData.get('flatOrPercentage') as string;

    if (!hotelId) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Hotel ID is required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (!codeCategoryVal || !codeValue) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Code Category and Code Value are required.' },
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
    if (!amount) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Amount is required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }

    const payload = {
      sellLimitsByDateRange: [
        {
          sellLimitDateRanges: [
            {
              actionType,
              startDate,
              endDate,
              sunday: formData.get('sun') === 'on',
              monday: formData.get('mon') === 'on',
              tuesday: formData.get('tue') === 'on',
              wednesday: formData.get('wed') === 'on',
              thursday: formData.get('thu') === 'on',
              friday: formData.get('fri') === 'on',
              saturday: formData.get('sat') === 'on',
              amount,
              flatOrPercentage,
            },
          ],
          hotelId,
          codeCategory: codeCategoryVal,
          codeValue,
        },
      ],
    };

    setLoading(true);
    setResponse({ status: 'loading', operation });
    try {
      const res = await fetch('/api/sell-limits', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ hotelId, payload, environment }),
      });
      const data = await res.json();
      setResponse({
        status: data.success ? 'success' : 'error',
        operation,
        data: {
          ...data,
          message: data.success
            ? `Sell limits updated for "${codeValue}" (${codeCategoryVal}) at hotel "${hotelId}"`
            : `Failed to update sell limits`,
          payload,
        },
        timestamp: new Date().toLocaleString(),
      });
    } catch (err: unknown) {
      const message = err instanceof Error ? err.message : 'Unknown error';
      setResponse({
        status: 'error',
        operation,
        data: { error: message, payload },
        timestamp: new Date().toLocaleString(),
      });
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="tab-panel active" role="tabpanel">
      <h2>Update Hotel Availability / Sell Limits</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="sl-hi">Hotel ID</label>
            <input type="text" id="sl-hi" name="hotelId" required placeholder="e.g. HOTEL1" />
          </div>
          <div className="form-group">
            <label htmlFor="sl-cc">Code Category</label>
            <select
              id="sl-cc"
              name="codeCategory"
              required
              value={codeCategory}
              onChange={(e) => setCodeCategory(e.target.value)}
            >
              <option value="RoomType">Room Type</option>
              <option value="roomClass">Room Class</option>
            </select>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="sl-cv">Code Value</label>
            {codeCategory === 'roomClass' ? (
              <select id="sl-cv" name="codeValue" required>
                <option value="">-- Select Room Class --</option>
                {ROOM_CLASS_VALUES.map((rc) => (
                  <option key={rc.value} value={rc.value}>
                    {rc.label}
                  </option>
                ))}
              </select>
            ) : (
              <input type="text" id="sl-cv" name="codeValue" required placeholder="e.g. FMTRPL, FMTACC, DOUBLE" />
            )}
          </div>
          <div className="form-group">
            <label htmlFor="sl-at">Action Type</label>
            <select id="sl-at" name="actionType" required>
              <option value="SET_AVAILABLE">SET_AVAILABLE</option>
              <option value="SET_SOLD">SET_SOLD</option>
            </select>
          </div>
        </div>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="sl-sd">Start Date</label>
            <input type="date" id="sl-sd" name="startDate" required />
          </div>
          <div className="form-group">
            <label htmlFor="sl-ed">End Date</label>
            <input type="date" id="sl-ed" name="endDate" required />
          </div>
        </div>
        <fieldset>
          <legend>Days of Week</legend>
          <div className="days-row">
            <label>
              <input type="checkbox" name="sun" defaultChecked /> Sun
            </label>
            <label>
              <input type="checkbox" name="mon" defaultChecked /> Mon
            </label>
            <label>
              <input type="checkbox" name="tue" defaultChecked /> Tue
            </label>
            <label>
              <input type="checkbox" name="wed" defaultChecked /> Wed
            </label>
            <label>
              <input type="checkbox" name="thu" defaultChecked /> Thu
            </label>
            <label>
              <input type="checkbox" name="fri" defaultChecked /> Fri
            </label>
            <label>
              <input type="checkbox" name="sat" defaultChecked /> Sat
            </label>
          </div>
        </fieldset>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="sl-am">Amount</label>
            <input type="number" id="sl-am" name="amount" required placeholder="71" />
          </div>
          <div className="form-group">
            <label htmlFor="sl-fp">Flat or Percentage</label>
            <select id="sl-fp" name="flatOrPercentage" required>
              <option value="F">Flat (F)</option>
              <option value="P">Percentage (P)</option>
            </select>
          </div>
        </div>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Updating...' : 'Update Sell Limits'}
        </button>
      </form>
      <ResponsePanel response={response} />
    </section>
  );
}
