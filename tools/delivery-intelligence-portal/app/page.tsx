'use client';

import { useState, useEffect, useCallback, useMemo } from 'react';
import { RAW } from '@/lib/rawData';
import { FilterState, ViewId, RawData } from '@/lib/types';
import { Sidebar } from '@/components/Sidebar';
import { TopBar } from '@/components/TopBar';
import { DataImport } from '@/components/DataImport';
import { ExecutiveOverview } from '@/features/executive/ExecutiveOverview';
import { DeliveryPerformance } from '@/features/delivery/DeliveryPerformance';
import { TrendAnalysis } from '@/features/trend/TrendAnalysis';
import { SprintAnalytics } from '@/features/sprint/SprintAnalytics';
import { JiraInsights } from '@/features/jira/JiraInsights';
import { TeamPerformance } from '@/features/team/TeamPerformance';
import { ResourceWorkload } from '@/features/resource/ResourceWorkload';
import { RiskImprovement } from '@/features/risk/RiskImprovement';
import { DataRegister } from '@/features/register/DataRegister';
import { DataQuality } from '@/features/quality/DataQuality';

const VIEWS: ViewId[] = [
  'executive', 'delivery', 'trend', 'sprint', 'jira',
  'team', 'resource', 'risk', 'register', 'quality',
];

export default function HomePage() {
  const [activeView, setActiveView] = useState<ViewId>('executive');
  const [filters, setFilters] = useState<FilterState>({ team: null, sprint: null, vendor: null });
  const [isPresenting, setIsPresenting] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const [showImport, setShowImport] = useState(false);
  const [data, setData] = useState<RawData>(RAW);

  const currentIndex = VIEWS.indexOf(activeView);

  const goNext = useCallback(() => {
    const next = currentIndex + 1;
    if (next < VIEWS.length) setActiveView(VIEWS[next]);
  }, [currentIndex]);

  const goPrev = useCallback(() => {
    const prev = currentIndex - 1;
    if (prev >= 0) setActiveView(VIEWS[prev]);
  }, [currentIndex]);

  useEffect(() => {
    function handleKey(e: KeyboardEvent) {
      if (e.target instanceof HTMLInputElement || e.target instanceof HTMLSelectElement) return;
      switch (e.key) {
        case 'ArrowRight':
          goNext();
          break;
        case 'ArrowLeft':
          goPrev();
          break;
        case 'Escape':
          if (isPresenting) setIsPresenting(false);
          break;
        case 'f':
        case 'F':
          if (!e.ctrlKey && !e.metaKey) setIsPresenting((p) => !p);
          break;
      }
    }
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [goNext, goPrev, isPresenting]);

  const filteredSprints = useMemo(() => {
    let s = data.sprints;
    if (filters.team) s = s.filter((sp) => sp.team === filters.team);
    if (filters.sprint) s = s.filter((sp) => sp.name === filters.sprint);
    if (filters.vendor) s = s.filter((sp) => sp.vendor === filters.vendor);
    return s;
  }, [filters, data]);

  const filteredTeams = useMemo(() => {
    let t = data.teams;
    if (filters.team) t = t.filter((tm) => tm.id === filters.team);
    if (filters.vendor) t = t.filter((tm) => tm.vendor === filters.vendor);
    return t;
  }, [filters, data]);

  const handleImport = useCallback((imported: RawData) => {
    setData(imported);
    setShowImport(false);
    setFilters({ team: null, sprint: null, vendor: null });
  }, []);

  const renderView = () => {
    const props = { sprints: filteredSprints, teams: filteredTeams, config: data.config, raw: data, filters };
    switch (activeView) {
      case 'executive': return <ExecutiveOverview {...props} />;
      case 'delivery': return <DeliveryPerformance {...props} />;
      case 'trend': return <TrendAnalysis {...props} />;
      case 'sprint': return <SprintAnalytics {...props} />;
      case 'jira': return <JiraInsights {...props} />;
      case 'team': return <TeamPerformance {...props} />;
      case 'resource': return <ResourceWorkload {...props} />;
      case 'risk': return <RiskImprovement {...props} />;
      case 'register': return <DataRegister {...props} />;
      case 'quality': return <DataQuality {...props} />;
    }
  };

  return (
    <div className={`flex h-screen overflow-hidden ${isPresenting ? 'bg-ink' : 'bg-bg'}`}>
      {!isPresenting && sidebarOpen && (
        <Sidebar activeView={activeView} onNavigate={setActiveView} onClose={() => setSidebarOpen(false)} />
      )}
      <div className="flex-1 flex flex-col overflow-hidden">
        <TopBar
          activeView={activeView}
          currentIndex={currentIndex}
          totalViews={VIEWS.length}
          onNext={goNext}
          onPrev={goPrev}
          filters={filters}
          setFilters={setFilters}
          isPresenting={isPresenting}
          setIsPresenting={setIsPresenting}
          sidebarOpen={sidebarOpen}
          setSidebarOpen={setSidebarOpen}
          teams={data.teams}
          onOpenImport={() => setShowImport(true)}
        />
        <main className="flex-1 overflow-y-auto p-6 scrollbar-thin">
          {renderView()}
        </main>
      </div>
      {showImport && <DataImport onImport={handleImport} onClose={() => setShowImport(false)} />}
    </div>
  );
}
