'use client';
import { FormEvent, useState } from 'react';
import ResponsePanel, { ResponseState } from './ResponsePanel';
import { EnvironmentOption } from './EnvironmentSelector';

const DEFAULT_ROOM_TYPES =
  'DOUBLE,ZPLDBL,TWINRM,FMTRPL,FMTHRE,FMQUAD,FMFOUR,DBLDBL,FMBUNK,FMTRSC,SINGLE,BRFDBL,BRFZPL,BRFTWN,LOWDBL,WETDBL,LOWTWN,WETTWN,ACCSGL,PPLDBL,PDBZPL,PFAMIL,PPDWET,PPDLOW,DBLWIN,DBLNWD,BIGWIN,BIGNWD,BIGZPL,ACCWIN,ACCNWD,WINCMB,NWDCMB,WINSPL,NWDSPL,EXTDBL,EXDZPL,EXDLOW,EXDWET';

export default function RatePlanForm({ environment }: { environment: EnvironmentOption }) {
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<ResponseState>({ status: 'idle' });

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const form = e.currentTarget;
    const formData = new FormData(form);
    const operation = 'Update Daily Rates';
    const hotelId = (formData.get('hotelId') as string).trim();
    const ratePlanCode = (formData.get('ratePlanCode') as string).trim();
    const roomTypes = (formData.get('roomTypes') as string)
      .trim()
      .split(',')
      .map((rt) => rt.trim())
      .filter(Boolean);
    const startDate = formData.get('startDate') as string;
    const endDate = formData.get('endDate') as string;
    const onePersonRate = formData.get('onePersonRate') as string;
    const twoPersonRate = formData.get('twoPersonRate') as string;
    const extraPersonRate = formData.get('extraPersonRate') as string;
    const rateMode = formData.get('rateMode') as string;

    if (!hotelId || !ratePlanCode) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Hotel ID and Rate Plan Code are required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (
      hotelId.toUpperCase().includes('RATE') ||
      hotelId.toUpperCase().includes('FLEX') ||
      hotelId.toUpperCase().includes('RACK')
    ) {
      setResponse({
        status: 'error',
        operation,
        data: {
          error: `"${hotelId}" looks like a Rate Plan Code. Hotel ID should be the property code (e.g. FRAMTI, HEAPTI).`,
        },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (!startDate || !endDate) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Start Date and End Date are required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (!onePersonRate) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'One Person Rate is required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }

    const payload = {
      dailyRateScheduleRange: {
        hotelId,
        ratePlanCode,
        roomTypes,
        roomClasses: [],
        dateRange: {
          timeSpan: { startDate, endDate },
          sunday: formData.get('sun') === 'on',
          monday: formData.get('mon') === 'on',
          tuesday: formData.get('tue') === 'on',
          wednesday: formData.get('wed') === 'on',
          thursday: formData.get('thu') === 'on',
          friday: formData.get('fri') === 'on',
          saturday: formData.get('sat') === 'on',
        },
        incrementFlag: rateMode === 'increment',
        rateAmounts: {
          onePersonRate,
          ...(twoPersonRate && { twoPersonRate }),
          ...(extraPersonRate && { extraPersonRate }),
          overrideFloorAmount: formData.get('overrideFloor') === 'on',
        },
      },
    };

    setLoading(true);
    setResponse({ status: 'loading', operation });
    try {
      const res = await fetch('/api/rate-plan', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ hotelId, ratePlanCode, payload, environment }),
      });
      const data = await res.json();
      setResponse({
        status: data.success ? 'success' : 'error',
        operation,
        data: {
          ...data,
          message: data.success
            ? `Rate plan "${ratePlanCode}" updated for hotel "${hotelId}"`
            : `Failed to update rate plan "${ratePlanCode}" for hotel "${hotelId}"`,
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
      <h2>Update Daily Rates</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="rp-hotel-id">Hotel ID (e.g. HEAPTI, FRAMTI)</label>
            <input type="text" id="rp-hotel-id" name="hotelId" required placeholder="Enter Hotel ID first" />
          </div>
          <div className="form-group">
            <label htmlFor="rp-rpc">Rate Plan Code (e.g. FLEXRATE, RACK)</label>
            <input type="text" id="rp-rpc" name="ratePlanCode" required placeholder="Enter Rate Plan Code" />
          </div>
        </div>
        <div className="form-group">
          <label htmlFor="rp-rt">Room Types (comma-separated)</label>
          <textarea id="rp-rt" name="roomTypes" rows={3} required defaultValue={DEFAULT_ROOM_TYPES} />
        </div>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="rp-sd">Start Date</label>
            <input type="date" id="rp-sd" name="startDate" required />
          </div>
          <div className="form-group">
            <label htmlFor="rp-ed">End Date</label>
            <input type="date" id="rp-ed" name="endDate" required />
          </div>
        </div>
        <p className="field-note">
          \u26A0\uFE0F End date must not exceed the rate plan&apos;s pricing schedule limit in Opera Cloud.
        </p>
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
        <fieldset>
          <legend>Rate Amounts</legend>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="rp-1p">One Person Rate</label>
              <input type="number" id="rp-1p" name="onePersonRate" required placeholder="110" />
            </div>
            <div className="form-group">
              <label htmlFor="rp-2p">Two Person Rate</label>
              <input type="number" id="rp-2p" name="twoPersonRate" placeholder="Leave empty for null" />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="rp-ep">Extra Person Rate</label>
              <input type="number" id="rp-ep" name="extraPersonRate" placeholder="Leave empty for null" />
            </div>
            <div className="form-group">
              <label>
                <input type="checkbox" name="overrideFloor" /> Override Floor Amount
              </label>
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="rp-rm">Rate Update Mode</label>
            <select id="rp-rm" name="rateMode">
              <option value="override">Override (replace existing rates)</option>
              <option value="increment">Increment (add to existing rates)</option>
            </select>
          </div>
        </fieldset>
        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Updating...' : 'Update Rate Plan'}
        </button>
      </form>
      <ResponsePanel response={response} />
    </section>
  );
}
