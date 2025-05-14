import { describe, expect, test, jest, beforeEach } from '@jest/globals';
import axios from 'axios';
import { createGithubIssue } from '../src/services/github';
import { Comment } from '../src/types';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

jest.mock('cheerio', () => ({
  load: () => {
    // Return a fake cheerio object with the methods your code expects
    return {
      // Simulate the structure your code expects
      // For example, if your code does $('div.comment'), return an array-like object
      // You may need to adjust this based on your actual implementation
      // Here's a generic example:
      root: () => ({
        find: () => [
          {
            attribs: { id: 'comment-123' },
            children: [],
            // ...add more as needed for your code
          },
          {
            attribs: { id: 'comment-456' },
            children: [],
          }
        ]
      }),
      // If your code uses $ directly, you may need to mock that too
      find: () => [
        {
          attribs: { id: 'comment-123' },
          children: [],
        },
        {
          attribs: { id: 'comment-456' },
          children: [],
        }
      ]
    };
  }
}));

describe('GitHub Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('createGithubIssue', () => {
    test('should create a GitHub issue from a comment', async () => {
      // Mock axios response
      mockedAxios.post.mockResolvedValue({
        status: 201,
        data: { number: 123 }
      } as any);
      
      const comment: Comment = {
        id: '12345',
        author: 'TestUser',
        date: '2023-06-15T12:00:00Z',
        content: 'This is a test bug report'
      };
      
      const result = await createGithubIssue(comment);
      
      expect(result).toBe(true);
      expect(mockedAxios.post).toHaveBeenCalledTimes(1);
      
      // Check that the API call was made with the right data
      const postCallArgs = mockedAxios.post.mock.calls[0];
      const params = postCallArgs[1] as {
        title: string;
        body: string;
        labels: string[];
      };
      
      expect(params.title).toContain('[BUG]');
      expect(params.body).toContain('**commentId:** 12345');
      expect(params.labels).toContain('bug');
      expect(params.labels).toContain('curseforge-comment');
    });
    
    test('should return false when API call fails', async () => {
      // Mock axios error
      mockedAxios.post.mockRejectedValue(new Error('API Error'));
      
      const comment: Comment = {
        id: '12345',
        author: 'TestUser',
        date: '2023-06-15T12:00:00Z',
        content: 'This is a test bug report'
      };
      
      const result = await createGithubIssue(comment);
      
      expect(result).toBe(false);
      expect(mockedAxios.post).toHaveBeenCalledTimes(1);
    });
  });
}); 