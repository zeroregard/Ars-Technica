import { describe, expect, test, jest, beforeEach } from '@jest/globals';
import axios from 'axios';
import { createGithubIssue } from '../src/services/github';
import { Comment } from '../src/types';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

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
      });
      
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
      // Type assertion to safely access the parameters
      const params = postCallArgs[1] as {
        title: string;
        body: string;
        labels: string[];
      };
      
      expect(params.title).toContain('[BUG]');
      expect(params.body).toContain('commentId: 12345');
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