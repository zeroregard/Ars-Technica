/**
 * Interface for a Curseforge comment
 */
export interface Comment {
  id: string;
  author: string;
  date: string;
  content: string;
}

/**
 * Interface for configuration
 */
export interface Config {
  GITHUB_REPOSITORY: string;
  REPO_OWNER: string;
  REPO_NAME: string;
  GITHUB_TOKEN: string | undefined;
  OPENAI_API_KEY: string | undefined;
  CURSEFORGE_URL: string;
}

/**
 * OpenAI chat completion request message
 */
export interface ChatMessage {
  role: 'system' | 'user' | 'assistant';
  content: string;
}

/**
 * GitHub issue creation parameters
 */
export interface IssueParams {
  title: string;
  body: string;
  labels: string[];
} 