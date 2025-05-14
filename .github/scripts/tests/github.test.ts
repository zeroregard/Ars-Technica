import { describe, expect, test, jest, beforeEach } from '@jest/globals';
import axios from 'axios';
import type { Mocked } from 'jest-mock';
import { createGithubIssue } from '../src/services/github';
import { Comment } from '../src/types';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as Mocked<typeof axios>;

jest.mock('cheerio', () => {
  // Create a wrapper that can be called like $(element)
  // to match Cheerio's API
  const $ = (element: any) => {
    return {
      attr: (name: string) => {
        if (name === 'id') return element.id;
        if (name === 'datetime') return element.datetime;
        return undefined;
      },
      find: (selector: string) => {
        if (selector === 'div.comment__author a') return { text: () => element.author };
        if (selector === 'div.comment__body') return { text: () => element.content };
        if (selector === 'time') return { attr: () => element.datetime };
        return { text: () => '', attr: () => undefined };
      },
      text: () => element.text || ''
    };
  };
  
  return {
    load: (html: string) => {
      if (html.includes('comment-123')) {
        // Mock data for both comments
        const comments = [
          { id: 'comment-123', author: 'TestUser1', datetime: '2023-06-01T10:00:00Z', content: 'Test comment 1' },
          { id: 'comment-456', author: 'TestUser2', datetime: '2023-06-02T11:00:00Z', content: 'Test comment 2' }
        ];
        
        // Return the cheerio-like selector function
        const selector = (sel: string) => {
          if (sel === 'section.comments-section') {
            return {
              length: 1,
              find: (childSel: string) => {
                if (childSel === 'li.comment') {
                  return {
                    length: comments.length,
                    each: (callback: (i: number, el: any) => void) => {
                      // Here's the key part - we wrap each element in our $ function
                      // before passing it to the callback
                      comments.forEach((comment, i) => {
                        callback(i, $(comment));
                      });
                    }
                  };
                }
                return { length: 0, each: () => {} };
              }
            };
          }
          if (sel === 'a.pagination-item') {
            return { length: 0, filter: () => ({ length: 0 }) };
          }
          return { length: 0 };
        };
        
        // Add the $ function to the selector function, like Cheerio does
        selector.$ = $;
        return selector;
      }
      
      // Empty data case
      const emptySelector = () => ({ length: 0, find: () => ({ length: 0, each: () => {} }) });
      emptySelector.$ = $;
      return emptySelector;
    }
  };
});

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