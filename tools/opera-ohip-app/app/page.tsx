'use client';

import { useState } from 'react';
import EnvironmentSelector, { EnvironmentOption } from './components/EnvironmentSelector';
import RatePlanForm from './components/RatePlanForm';
import RestrictionsForm from './components/RestrictionsForm';
import SellLimitsForm from './components/SellLimitsForm';
import PackagesForm from './components/PackagesForm';

export default function Home() {
  const [activeTab, setActiveTab] = useState('rate-plan');
  const [environment, setEnvironment] = useState<EnvironmentOption>('UAT');

  const tabs = [
    { id: 'rate-plan', label: 'Update Daily Rates' },
    { id: 'restrictions', label: 'Clear Hotel Restrictions' },
    { id: 'sell-limits', label: 'Update Hotel Availability / Sell Limits' },
    { id: 'packages', label: 'Update Package Code' },
  ];

  return (
    <>
      <header>
        <h1>Digital Opera Test Data Update</h1>
        <p>Update Rate Plans, Clear Restrictions, Set Sell Limits and Update Packages</p>
      </header>
      <main>
        <EnvironmentSelector selected={environment} onChange={setEnvironment} />
        <nav className="tabs" role="tablist" aria-label="API Operations">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              className={`tab ${activeTab === tab.id ? 'active' : ''}`}
              role="tab"
              aria-selected={activeTab === tab.id}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </nav>
        {activeTab === 'rate-plan' && <RatePlanForm environment={environment} />}
        {activeTab === 'restrictions' && <RestrictionsForm environment={environment} />}
        {activeTab === 'sell-limits' && <SellLimitsForm environment={environment} />}
        {activeTab === 'packages' && <PackagesForm environment={environment} />}
      </main>
    </>
  );
}
