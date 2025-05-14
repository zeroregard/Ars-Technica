/**
 * Logger utility for GitHub Actions workflow
 * Ensures logs are properly displayed in GitHub Actions UI
 */

const PREFIX = '🔄 CurseForge Processor';

/**
 * Log an informational message
 */
export function info(message: string): void {
  // Regular console log with prefix
  console.log(`${PREFIX}: ${message}`);
  
  // GitHub Actions specific notice command for better visibility
  console.log(`::notice::${message}`);
}

/**
 * Log a warning message
 */
export function warn(message: string): void {
  // Regular console warning with prefix
  console.warn(`${PREFIX} WARNING: ${message}`);
  
  // GitHub Actions specific warning command for better visibility
  console.log(`::warning::${message}`);
}

/**
 * Log an error message
 */
export function error(message: string, err?: Error): void {
  // Regular console error with prefix
  console.error(`${PREFIX} ERROR: ${message}`);
  if (err) {
    console.error(`${PREFIX} ERROR DETAILS: ${err.message}`);
    console.error(`${PREFIX} STACK: ${err.stack}`);
  }
  
  // GitHub Actions specific error command for better visibility
  console.log(`::error::${message}${err ? ` - ${err.message}` : ''}`);
}

/**
 * Log a success message
 */
export function success(message: string): void {
  console.log(`${PREFIX} SUCCESS: ${message}`);
  
  // GitHub Actions has no specific success command, but we can use notice
  console.log(`::notice::✅ ${message}`);
}

/**
 * Log a debug message (only shown when debug mode is enabled)
 */
export function debug(message: string): void {
  if (process.env.DEBUG === 'true') {
    console.log(`${PREFIX} DEBUG: ${message}`);
    
    // GitHub Actions specific debug command
    console.log(`::debug::${message}`);
  }
}

/**
 * Ensure all logs are flushed before exiting
 */
export function flushAndExit(exitCode: number = 0): void {
  // Use setTimeout to ensure all logs are flushed before exiting
  setTimeout(() => {
    process.exit(exitCode);
  }, 500);
} 