import * as fs from 'fs';
import * as path from 'path';
import { getGithubIssues, createGithubIssue } from './services/github';
import { fetchCurseforgeComments } from './services/curseforge';
import { isBugReport } from './services/analyzer';
import * as logger from './utils/logger';

/**
 * Main function to process comments
 */
export async function main(): Promise<void> {
  try {
    // Create scripts directory if it doesn't exist
    const scriptsDir = path.dirname(__filename);
    if (!fs.existsSync(scriptsDir)) {
      fs.mkdirSync(scriptsDir, { recursive: true });
    }
    
    // Get existing GitHub issues with comment IDs
    logger.info('Starting CurseForge comments processing workflow');
    logger.info('Fetching existing GitHub issues...');
    const existingCommentIds = await getGithubIssues();
    logger.info(`Found ${existingCommentIds.size} existing comments in GitHub issues`);
    
    // Fetch comments from CurseForge
    logger.info('Fetching comments from CurseForge...');
    const allComments = await fetchCurseforgeComments();
    logger.info(`Fetched ${allComments.length} comments from CurseForge`);
    
    // Process each comment
    let processedCount = 0;
    let bugReportCount = 0;
    
    for (const comment of allComments) {
      logger.debug(`Processing comment ${comment.id} from ${comment.author}...`);
      
      // Skip if already processed
      if (existingCommentIds.has(comment.id)) {
        logger.debug(`Comment ${comment.id} already processed. Skipping.`);
        continue;
      }
      
      // Check if it's a bug report
      logger.debug(`Checking if comment ${comment.id} is a bug report...`);
      if (await isBugReport(comment.content)) {
        logger.info(`Found bug report in comment ${comment.id} by ${comment.author}. Creating GitHub issue...`);
        const success = await createGithubIssue(comment);
        if (success) {
          bugReportCount++;
          logger.success(`Created GitHub issue for bug report in comment ${comment.id}`);
        } else {
          logger.error(`Failed to create GitHub issue for comment ${comment.id}`);
        }
      } else {
        logger.debug(`Comment ${comment.id} does not appear to be a bug report. Skipping.`);
      }
      
      processedCount++;
      
      // Add a small delay to avoid hitting API rate limits
      await new Promise(resolve => setTimeout(resolve, 1000));
    }
    
    logger.success(`Finished processing ${processedCount} comments. Created ${bugReportCount} new GitHub issues for bug reports.`);
    
    // Ensure logs are flushed before exiting
    logger.flushAndExit(0);
  } catch (error) {
    logger.error('Error in main process', error instanceof Error ? error : new Error(String(error)));
    logger.flushAndExit(1);
  }
}

// Call main function if this is the entry point
if (require.main === module) {
  main().catch(error => {
    logger.error('Unhandled exception in main function', error instanceof Error ? error : new Error(String(error)));
    logger.flushAndExit(1);
  });
} 