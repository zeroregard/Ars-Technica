const axios = require('axios');
const config = require('../config');

/**
 * Fetch all GitHub issues and extract comment IDs
 */
async function getGithubIssues() {
  const commentIds = new Set();
  let page = 1;
  
  try {
    while (true) {
      console.log(`Fetching GitHub issues page ${page}...`);
      
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
        }
      }
      
      page++;
    }
  } catch (error) {
    console.error(`Error fetching GitHub issues: ${error.message}`);
    if (error.response) {
      console.error(`Status: ${error.response.status}`);
      console.error(error.response.data);
    }
  }
  
  return commentIds;
}

/**
 * Create a GitHub issue for a bug report comment
 */
async function createGithubIssue(comment) {
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
    
    const response = await axios.post(
      `https://api.github.com/repos/${config.REPO_OWNER}/${config.REPO_NAME}/issues`,
      {
        title: `[BUG] ${title}`,
        body,
        labels: ['bug', 'curseforge-comment']
      },
      {
        headers: {
          'Accept': 'application/vnd.github.v3+json',
          'Authorization': `token ${config.GITHUB_TOKEN}`
        }
      }
    );
    
    if (response.status === 201) {
      console.log(`Successfully created GitHub issue for comment ${comment.id}`);
      return true;
    } else {
      console.error(`Unexpected response creating GitHub issue: ${response.status}`);
      console.error(response.data);
      return false;
    }
  } catch (error) {
    console.error(`Error creating GitHub issue: ${error.message}`);
    if (error.response) {
      console.error(`Status: ${error.response.status}`);
      console.error(error.response.data);
    }
    return false;
  }
}

module.exports = {
  getGithubIssues,
  createGithubIssue
}; 