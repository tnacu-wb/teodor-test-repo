import { handleRefHeightChange } from './handleRefHeightChange';

describe('handleRefHeightChange Method', () => {
  it('should call setHeight function if node has clientHeight truthy value', () => {
    const setHeightSpy = jest.fn();
    const mockNode = { clientHeight: 100 } as HTMLDivElement;
    handleRefHeightChange(mockNode, setHeightSpy);
    expect(setHeightSpy).toHaveBeenCalled();
  });

  it('should not call setHeight function if node has clientHeight falsy value', () => {
    const setHeightSpy = jest.fn();
    const mockNode = { clientHeight: 0 } as HTMLDivElement;
    handleRefHeightChange(mockNode, setHeightSpy);
    expect(setHeightSpy).not.toHaveBeenCalled();
  });
});
