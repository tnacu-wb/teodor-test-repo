export class PipelineContext {
  private data: Map<string, any> = new Map();

  // Set a value in the context
  set(key: string, value: any): void {
    this.data.set(key, value);
  }

  // Get a value from the context
  get(key: string): any {
    return this.data.get(key);
  }

  // Check if a key exists in the context
  has(key: string): boolean {
    return this.data.has(key);
  }
}
