'use client';
import { FormEvent, useState } from 'react';
import ResponsePanel, { ResponseState } from './ResponsePanel';
import { EnvironmentOption } from './EnvironmentSelector';
import { PACKAGE_CODES, getPackageConfig } from '@/lib/ohip/packageData';

export default function PackagesForm({ environment }: { environment: EnvironmentOption }) {
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<ResponseState>({ status: 'idle' });
  const [selectedCode, setSelectedCode] = useState('');
  const [search, setSearch] = useState('');
  const [dropdownOpen, setDropdownOpen] = useState(false);

  const pkg = getPackageConfig(selectedCode);

  const filteredPackages = PACKAGE_CODES.filter(
    (p) =>
      search === '' ||
      p.code.toLowerCase().includes(search.toLowerCase()) ||
      p.description.toLowerCase().includes(search.toLowerCase())
  );

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const operation = 'Update Package Code';
    const hotelId = (formData.get('hotelId') as string).trim();
    const packageCode = (formData.get('packageCode') as string).trim();
    const unitPrice = (formData.get('unitPrice') as string).trim();
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
    if (!packageCode || !pkg) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Please select a Package Code.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (!unitPrice) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Unit Price is required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }
    if (!startDate || !endDate) {
      setResponse({
        status: 'error',
        operation,
        data: { error: 'Schedule Start and End dates are required.' },
        timestamp: new Date().toLocaleString(),
      });
      return;
    }

    // Build full payload from package config + user inputs
    const payload = {
      packageCode: {
        header: {
          primaryDetails: {
            description: pkg.description,
            shortDescription: pkg.shortDescription || pkg.description,
          },
          transactionDetails: {
            allowance: false,
            packagePostingRules: {
              transactionCode: { code: '156', type: 'Inclusive' },
            },
          },
          postingAttributes: {
            inventoryItems: pkg.inventoryItems,
            addToRate: pkg.addToRate,
            printSeparateLine: pkg.printSeparateLine,
            sellSeparate: pkg.sellSeparate,
            postNextDay: pkg.postNextDay,
            forecastNextDay: pkg.forecastNextDay,
            webBookable: pkg.webBookable,
            formulaFunctionArguments: [],
            catering: pkg.catering,
            postingRhythm: { type: pkg.postingRhythm || 'EveryNight' },
            priceCalculationRule: pkg.priceCalculationRule || 'FlatRate',
          },
        },
        schedules: [
          {
            newTimeSpan: { startDate, endDate },
            schedulePrices: [{ unitPrice }, { bucket: 'Bucket2' }, { bucket: 'Bucket3' }],
            newMinNights: '1',
            newMaxNights: '999',
            newMinPersons: '1',
            newMaxPersons: '4',
          },
        ],
        hotelId,
        code: packageCode,
        adjustOverlappingRange: true,
      },
    };

    setLoading(true);
    setResponse({ status: 'loading', operation });
    try {
      const res = await fetch('/api/packages', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ hotelId, packageCode, payload, environment }),
      });
      const data = await res.json();
      setResponse({
        status: data.success ? 'success' : 'error',
        operation,
        data: {
          ...data,
          message: data.success
            ? `Package "${packageCode}" (${pkg.description}) updated at hotel "${hotelId}" with price £${unitPrice}`
            : 'Failed to update package',
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
      <h2>Update Package Code</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="pk-hi">Hotel ID</label>
            <input type="text" id="pk-hi" name="hotelId" required placeholder="e.g. HEAPTI" />
          </div>
          <div className="form-group">
            <label htmlFor="pk-code">Package Code</label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                id="pk-code"
                placeholder="Search packages..."
                value={search || (selectedCode ? `${selectedCode} — ${pkg?.description || ''}` : '')}
                onChange={(e) => {
                  setSearch(e.target.value);
                  setSelectedCode('');
                  setDropdownOpen(true);
                }}
                onFocus={() => setDropdownOpen(true)}
                autoComplete="off"
              />
              <input type="hidden" name="packageCode" value={selectedCode} />
              {dropdownOpen && (
                <div
                  style={{
                    position: 'absolute',
                    top: '100%',
                    left: 0,
                    right: 0,
                    maxHeight: '240px',
                    overflowY: 'auto',
                    background: '#fff',
                    border: '1px solid #ddd',
                    borderRadius: '6px',
                    boxShadow: '0 4px 12px rgba(0,0,0,0.1)',
                    zIndex: 50,
                    marginTop: '4px',
                  }}
                >
                  {filteredPackages.length === 0 ? (
                    <div style={{ padding: '10px 12px', fontSize: '13px', color: '#888' }}>
                      No packages match &ldquo;{search}&rdquo;
                    </div>
                  ) : (
                    filteredPackages.map((p) => (
                      <div
                        key={p.code}
                        onClick={() => {
                          setSelectedCode(p.code);
                          setSearch('');
                          setDropdownOpen(false);
                        }}
                        style={{
                          padding: '8px 12px',
                          fontSize: '13px',
                          cursor: 'pointer',
                          borderBottom: '1px solid #f0f0f0',
                          background: selectedCode === p.code ? '#f0f4ff' : 'transparent',
                        }}
                        onMouseEnter={(e) => {
                          (e.target as HTMLElement).style.background = '#f5f5f5';
                        }}
                        onMouseLeave={(e) => {
                          (e.target as HTMLElement).style.background =
                            selectedCode === p.code ? '#f0f4ff' : 'transparent';
                        }}
                      >
                        <strong>{p.code}</strong> — {p.description}
                        <span style={{ float: 'right', fontSize: '11px', color: '#888' }}>£{p.calculatedPrice}</span>
                      </div>
                    ))
                  )}
                </div>
              )}
            </div>
          </div>
        </div>

        {pkg && (
          <div
            style={{
              background: '#f0f4ff',
              padding: '12px 16px',
              borderRadius: '8px',
              marginBottom: '16px',
              fontSize: '13px',
              lineHeight: '1.6',
            }}
          >
            <strong>Auto-filled from package config:</strong>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '4px 16px', marginTop: '6px' }}>
              <span>
                Description: <strong>{pkg.description}</strong>
              </span>
              <span>
                Price Rule: <strong>{pkg.priceCalculationRule}</strong>
              </span>
              <span>
                Posting Rhythm: <strong>{pkg.postingRhythm}</strong>
              </span>
              <span>
                Web Bookable: <strong>{pkg.webBookable ? 'Yes' : 'No'}</strong>
              </span>
              <span>
                Current Price: <strong>£{pkg.calculatedPrice}</strong>
              </span>
              <span>
                Type: <strong>{pkg.group ? 'Group' : 'Individual'}</strong>
              </span>
            </div>
          </div>
        )}

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="pk-price">New Unit Price (£)</label>
            <input
              type="text"
              id="pk-price"
              name="unitPrice"
              required
              placeholder={pkg ? `Current: ${pkg.calculatedPrice}` : 'e.g. 11.99'}
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="pk-sd">Schedule Start Date</label>
            <input type="date" id="pk-sd" name="startDate" required defaultValue="2022-12-01" />
          </div>
          <div className="form-group">
            <label htmlFor="pk-ed">Schedule End Date</label>
            <input type="date" id="pk-ed" name="endDate" required defaultValue="2045-12-21" />
          </div>
        </div>

        <button type="submit" className="btn-primary" disabled={loading || !pkg}>
          {loading ? 'Updating...' : `Update ${selectedCode || 'Package'}`}
        </button>
      </form>
      <ResponsePanel response={response} />
    </section>
  );
}
