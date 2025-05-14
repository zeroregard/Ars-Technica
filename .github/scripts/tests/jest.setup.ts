import { jest } from '@jest/globals';

// Make TypeScript happy with jest types
// You might need to add type definitions if using the global Jest object
type GlobalJestType = typeof jest;
declare global {
  // eslint-disable-next-line no-var
  var jest: GlobalJestType;
} 