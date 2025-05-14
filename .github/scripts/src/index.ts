import * as fs from 'fs';
import * as path from 'path';
import { getGithubIssues, createGithubIssue } from './services/github';
import { fetchCurseforgeComments } from './services/curseforge';
import { isBugReport } from './services/analyzer';

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
    console.log('Fetching existing GitHub issues...');
    const existingCommentIds = await getGithubIssues();
    console.log(`Found ${existingCommentIds.size} existing comments in GitHub issues`);
    
    // Fetch comments from CurseForge
    console.log('Fetching comments from CurseForge...');
    const allComments = await fetchCurseforgeComments();
    console.log(`Fetched ${allComments.length} comments from CurseForge`);
    
    // Process each comment
    for (const comment of allComments) {
      console.log(`Processing comment ${comment.id}...`);
      
      // Skip if already processed
      if (existingCommentIds.has(comment.id)) {
        console.log(`Comment ${comment.id} already processed. Skipping.`);
        continue;
      }
      
      // Check if it's a bug report
      console.log(`Checking if comment ${comment.id} is a bug report...`);
      if (await isBugReport(comment.content)) {
        console.log(`Comment ${comment.id} appears to be a bug report. Creating GitHub issue...`);
        await createGithubIssue(comment);
      } else {
        console.log(`Comment ${comment.id} does not appear to be a bug report. Skipping.`);
      }
      
      // Add a small delay to avoid hitting API rate limits
      await new Promise(resolve => setTimeout(resolve, 1000));
    }
    
    console.log('Finished processing comments.');
  } catch (error) {
    console.error(`Error in main process: ${error instanceof Error ? error.message : String(error)}`);
    process.exit(1);
  }
} 