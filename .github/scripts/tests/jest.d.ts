// Global Jest type declarations for TypeScript files
import 'jest';

// Extend global namespace to avoid lint errors in tests
declare global {
  const describe: jest.Describe;
  const expect: jest.Expect;
  const test: jest.It;
  const beforeEach: jest.Lifecycle;
  const afterEach: jest.Lifecycle;
} 