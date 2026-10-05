import '@testing-library/jest-dom';
import React from 'react';

import getCostCentresColumns, {
  codeHeaderStyle,
  nameHeaderStyle,
  statusHeaderStyle,
  actionsHeaderStyle,
} from './cost-centres-columns';

describe('getCostCentresColumns', () => {
  const renderRowStatus = jest.fn(() => <div>status</div>);
  const renderActions = jest.fn((code: string) => <button>{code}</button>);
  const t = jest.fn((key: string) => `translated:${key}`);

  it('returns columns with correct ids and labels', () => {
    const columns = getCostCentresColumns({ renderRowStatus, renderActions, t });
    expect(columns).toHaveLength(4);
    expect(columns[0].id).toBe('costCentreCode');
    expect(columns[0].label).toBe('translated:costCentreMgmt.columns.code');
    expect(columns[1].id).toBe('costCentreName');
    expect(columns[1].label).toBe('translated:costCentreMgmt.columns.name');
    expect(columns[2].id).toBe('status');
    expect(columns[2].label).toBe('translated:costCentreMgmt.columns.status');
    expect(columns[3].id).toBe('cardAction');
    expect(columns[3].label).toBe('');
  });

  it('applies correct headerClassName and className', () => {
    const columns = getCostCentresColumns({ renderRowStatus, renderActions, t });
    expect(columns[0].headerClassName).toBe(codeHeaderStyle);
    expect(columns[0].className).toBe(codeHeaderStyle);
    expect(columns[1].headerClassName).toBe(nameHeaderStyle);
    expect(columns[1].className).toBe(nameHeaderStyle);
    expect(columns[2].headerClassName).toBe(statusHeaderStyle);
    expect(columns[2].className).toBe(statusHeaderStyle);
    expect(columns[3].headerClassName).toBe(actionsHeaderStyle);
    expect(columns[3].className).toBe('truncate');
  });

  it('calls renderRowStatus in status column render', () => {
    const columns = getCostCentresColumns({ renderRowStatus, renderActions, t });
    renderRowStatus.mockClear();
    const cell = {} as any;
    const row = {} as any;
    if (columns[2].render) {
      columns[2].render(cell, row);
    }
    expect(renderRowStatus).toHaveBeenCalled();
  });

  it('calls renderActions with correct code in cardAction column render', () => {
    const columns = getCostCentresColumns({ renderRowStatus, renderActions, t });
    renderActions.mockClear();
    const row = { costCentreCode: 'CC123' };
    if (columns[3].render) {
      columns[3].render({}, row);
    }
    expect(renderActions).toHaveBeenCalledWith('CC123');
  });
});
