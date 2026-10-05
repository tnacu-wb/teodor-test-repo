/**
 * Object and array helper methods migrated from common-utilities.
 */
export class ObjectHelpers {
  private constructor() {}

  /**
   * Deep compare two values using the legacy strict recursive semantics.
   * @param first first value to compare
   * @param second second value to compare
   * @returns whether values are equivalent
   */
  static isEqual(first: unknown, second: unknown): boolean {
    if (first === second) return true;
    if (!first || !second || typeof first !== 'object' || typeof second !== 'object') return false;
    if (Object.getPrototypeOf(first) !== Object.getPrototypeOf(second)) return false;
    const firstRecord = first as Record<string, unknown>;
    const secondRecord = second as Record<string, unknown>;
    const firstKeys = Object.keys(firstRecord);
    return firstKeys.length === Object.keys(secondRecord).length && firstKeys.every((key) =>
      Object.hasOwn(secondRecord, key) && ObjectHelpers.isEqual(firstRecord[key], secondRecord[key]));
  }

  /**
   * Check whether a value is an array.
   * @param value value to check
   * @returns whether value is an array
   */
  static isArray(value: unknown): value is unknown[] {
    return Array.isArray(value);
  }

  /**
   * Get values from an object or the first object in an array.
   * @param value JSON object or JSON array
   * @returns object values
   */
  static getObjValues(value: Record<string, unknown> | Record<string, unknown>[]): unknown[] {
    return Object.values(Array.isArray(value) ? value[0] ?? {} : value);
  }

  /**
   * Get a key value using the legacy upper-case, lower-case, then exact lookup order.
   * @param value JSON object or JSON array
   * @param key key to search
   * @returns matching value, when present
   */
  static getValueByKey(value: Record<string, unknown> | Record<string, unknown>[], key: string): unknown {
    const record = Array.isArray(value) ? value[0] ?? {} : value;
    return record[key.toUpperCase()] ?? record[key.toLowerCase()] ?? record[key];
  }

  /**
   * Check whether a value exists in a nested JSON object or array.
   * @param value JSON object or JSON array
   * @param expected value to search for
   * @returns whether the value is present
   */
  static isContains(value: unknown, expected: unknown): boolean {
    if (!value || typeof value !== 'object') return value === expected;
    return Object.values(value as Record<string, unknown>).some((child) => ObjectHelpers.isContains(child, expected));
  }

  /**
   * Get an object from a list based on a key-value match.
   * @param values JSON array to search
   * @param keyName key name to match
   * @param valueToSearch expected key value
   * @returns matching object, when present
   */
  static getObjFromList<T extends Record<string, unknown>>(values: T[], keyName: string, valueToSearch: unknown): T | undefined {
    return values.find((value) => Object.hasOwn(value, keyName) && value[keyName] == valueToSearch);
  }

  /**
   * Find nested objects with a property matching a value.
   * @param value object or array to search
   * @param targetProp property name to search
   * @param targetValue expected property value
   * @returns matching objects
   */
  static findObjects(value: unknown, targetProp: string, targetValue: unknown): Record<string, unknown>[] {
    const results: Record<string, unknown>[] = [];
    const visit = (current: unknown): void => {
      if (!current || typeof current !== 'object') return;
      if (Array.isArray(current)) current.forEach(visit);
      else {
        const record = current as Record<string, unknown>;
        if (record[targetProp] === targetValue) results.push(record);
        Object.values(record).forEach(visit);
      }
    };
    visit(value);
    return results;
  }

  /**
   * Return the property name matching a property value.
   * @param value object to inspect
   * @param property property value to find
   * @returns matching property name, when present
   */
  static propName(value: Record<string, unknown>, property: unknown): string | undefined {
    return Object.keys(value).find((key) => value[key] === property);
  }

  /**
   * Convert a promise into an error-or-data tuple.
   * @param promise promise to consume
   * @returns error-or-data tuple
   */
  static async catchEm<T>(promise: Promise<T>): Promise<[Error | null, T?]> {
    try { return [null, await promise]; } catch (error) { return [error as Error]; }
  }

  /**
   * Generate a random integer between the bounds, inclusive.
   * @param min inclusive minimum
   * @param max inclusive maximum
   * @returns random integer
   */
  static generateRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }
}