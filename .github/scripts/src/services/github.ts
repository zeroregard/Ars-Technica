import axios from 'axios';
import config from '../config';
import { Comment, IssueParams } from '../types';
import * as logger from '../utils/logger';

/**
 * Fetch all GitHub issues and extract comment IDs
 */
export async function getGithubIssues(): Promise<Set<string>> {
  const commentIds = new Set<string>();
  let page = 1;
  
  try {
    while (true) {
      logger.debug(`Fetching GitHub issues page ${page}...`);
      
      const response = await axios.get(
        `https://api.github.com/repos/${config.REPO_OWNER}/${config.REPO_NAME}/issues`,
        {
          params: {
            state: 'all',
            per_page: 100,
            page: page
          },
          headers: {
            'Accept': 'application/vnd.github.v3+json',
            'Authorization': `token ${config.GITHUB_TOKEN}`
          }
        }
      );
      
      const issues = response.data;
      
      if (!issues || issues.length === 0) {
        break;
      }
      
      // Extract comment IDs from issues
      for (const issue of issues) {
        const body = issue.body || '';
        const commentIdMatch = body.match(/commentId: (\d+)/);
        
        if (commentIdMatch) {
          commentIds.add(commentIdMatch[1]);
          logger.debug(`Found existing issue #${issue.number} for comment ID ${commentIdMatch[1]}`);
        }
      }
      
      logger.debug(`Processed ${issues.length} issues on page ${page}`);
      
      page++;
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(`Error fetching GitHub issues: ${error.message}`, error);
      if (error.response) {
        logger.error(`API Response: ${error.response.status}`, error);
      }
    } else {
      logger.error('Unexpected error fetching GitHub issues', error instanceof Error ? error : new Error(String(error)));
    }
  }
  
  return commentIds;
}

/**
 * Create a GitHub issue for a bug report comment
 */
export async function createGithubIssue(comment: Comment): Promise<boolean> {
  try {
    // Create a title from the first line or first 50 characters
    let title = comment.content.split('\n')[0].substring(0, 50).trim();
    if (!title) {
      title = 'Potential bug reported in Curseforge comment';
    }
    
    const body = `Bug reported in Curseforge comment

**Comment Author:** ${comment.author}
**Comment Date:** ${comment.date}
**commentId:** ${comment.id}

**Comment Content:**
${comment.content}

---
*This issue was automatically created from a Curseforge comment.*`;
    
    const issueParams: IssueParams = {
      title: `[BUG] ${title}`,
      body,
      labels: ['bug', 'curseforge-comment']
    };
    
    logger.debug(`Creating GitHub issue for comment ${comment.id} with title: ${issueParams.title}`);
    
    const response = await axios.post(
      `https://api.github.com/repos/${config.REPO_OWNER}/${config.REPO_NAME}/issues`,
      issueParams,
      {
        headers: {
          'Accept': 'application/vnd.github.v3+json',
          'Authorization': `token ${config.GITHUB_TOKEN}`
        }
      }
    );
    
    if (response.status === 201) {
      const issueNumber = response.data.number;
      const issueUrl = response.data.html_url;
      
      logger.success(`Created GitHub issue #${issueNumber} for comment ${comment.id}: ${issueUrl}`);
      return true;
    } else {
      logger.error(`Unexpected response creating GitHub issue: ${response.status}`);
      return false;
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(`Error creating GitHub issue for comment ${comment.id}: ${error.message}`, error);
      if (error.response) {
        logger.error(`API Response: ${error.response.status}`, error);
      }
    } else {
      logger.error(`Error creating GitHub issue for comment ${comment.id}`, error instanceof Error ? error : new Error(String(error)));
    }
    return false;
  }
} 