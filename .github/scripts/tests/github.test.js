const axios = require('axios');
const { createGithubIssue } = require('../src/services/github');

// Mock axios
jest.mock('axios');

describe('GitHub Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  describe('createGithubIssue', () => {
    test('should create a GitHub issue from a comment', async () => {
      // Mock axios response
      axios.post.mockResolvedValue({
        status: 201,
        data: { number: 123 }
      });
      
      const comment = {
        id: '12345',
        author: 'TestUser',
        date: '2023-06-15T12:00:00Z',
        content: 'This is a test bug report'
      };
      
      const result = await createGithubIssue(comment);
      
      expect(result).toBe(true);
      expect(axios.post).toHaveBeenCalledTimes(1);
      
      // Check that the API call was made with the right data
      const postCallArgs = axios.post.mock.calls[0];
      expect(postCallArgs[1].title).toContain('[BUG]');
      expect(postCallArgs[1].body).toContain('**commentId:** 12345');
      expect(postCallArgs[1].labels).toContain('bug');
      expect(postCallArgs[1].labels).toContain('curseforge-comment');
    });
    
    test('should return false when API call fails', async () => {
      // Mock axios error
      axios.post.mockRejectedValue(new Error('API Error'));
      
      const comment = {
        id: '12345',
        author: 'TestUser',
        date: '2023-06-15T12:00:00Z',
        content: 'This is a test bug report'
      };
      
      const result = await createGithubIssue(comment);
      
      expect(result).toBe(false);
      expect(axios.post).toHaveBeenCalledTimes(1);
    });
  });
}); 