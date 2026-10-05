import '@testing-library/jest-dom';

import getPaymentsColumns from './payments-columns';

describe('getPaymentsColumns', () => {
  const renderRowStatus = jest.fn();
  const renderRowAmount = jest.fn();
  const t = jest.fn((key: string) => key);

  it('should return an array of columns with correct ids and labels', () => {
    const columns = getPaymentsColumns({ renderRowStatus, renderRowAmount, t });

    expect(Array.isArray(columns)).toBe(true);
    expect(columns).toHaveLength(4);

    expect(columns[0].id).toBe('paymentDate');
    expect(columns[0].label).toBe('statementsInvoicesPayments.payments.table.header.date');
    expect(columns[1].id).toBe('paymentDescription');
    expect(columns[1].label).toBe('statementsInvoicesPayments.payments.table.header.description');
    expect(columns[2].id).toBe('paymentFailed');
    expect(columns[2].label).toBe('statementsInvoicesPayments.payments.table.header.status');
    expect(columns[3].id).toBe('paymentValue');
    expect(columns[3].label).toBe('statementsInvoicesPayments.payments.table.header.value');
  });

  it('should call renderRowStatus for the Status column render function', () => {
    const columns = getPaymentsColumns({ renderRowStatus, renderRowAmount, t });
    const row = { some: 'row' } as any;
    const cell = { some: 'cell' } as any;

    columns[2].render!(cell, row);

    expect(renderRowStatus).toHaveBeenCalledWith(row, 'paymentFailed');
  });

  it('should call renderRowAmount for the Value column render function', () => {
    const columns = getPaymentsColumns({ renderRowStatus, renderRowAmount, t });
    const row = { some: 'row' } as any;
    const cell = { some: 'cell' } as any;

    columns[3].render!(cell, row);

    expect(renderRowAmount).toHaveBeenCalledWith(row, 'paymentValue');
  });

  it('should have correct className and headerClassName for each column', () => {
    const columns = getPaymentsColumns({ renderRowStatus, renderRowAmount, t });

    expect(columns[0].headerClassName).toBe('mobile:w-full w-[13%] box-content');
    expect(columns[0].className).toBe('mobile:w-full w-[13%] box-content');
    expect(columns[1].headerClassName).toBe('mobile:hidden w-[30%]');
    expect(columns[1].className).toBe('mobile:hidden w-[30%]');
    expect(columns[2].headerClassName).toBe('mobile:w-[100px] w-[27%] box-content');
    expect(columns[3].headerClassName).toBe('mobile:w-full w-[30%] text-right');
    expect(columns[3].className).toBe('mobile:w-full w-[30%] text-right');
  });
});
