import { describe, expect, test, jest, beforeEach } from '@jest/globals';
import axios from 'axios';
import type { Mocked } from 'jest-mock';
import { fetchCurseforgeComments } from '../src/services/curseforge';
import { Comment } from '../src/types';
import type { AxiosResponse } from 'axios';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as Mocked<typeof axios>;

// Mock cheerio
jest.mock('cheerio', () => {
  return {
    load: () => {
      // Create fake comment data
      const mockComments = [
        { id: '123', author: 'TestUser1', date: '2023-06-01T10:00:00Z', content: 'Test comment 1' },
        { id: '456', author: 'TestUser2', date: '2023-06-02T11:00:00Z', content: 'Test comment 2' }
      ];
      
      // This is our fake cheerio function that acts like the $ in the code
      const $ = (selector: any) => {
        // When an element is passed (like in the .each callback)
        if (typeof selector === 'object' && selector !== null) {
          return {
            attr: (name: string) => {
              if (name === 'id') return `comment-${selector.id}`;
              return selector.date;
            },
            find: (childSelector: string) => {
              if (childSelector === 'div.comment__author a') {
                return { text: () => selector.author, trim: () => selector.author };
              }
              if (childSelector === 'div.comment__body') {
                return { text: () => selector.content, trim: () => selector.content };
              }
              if (childSelector === 'time') {
                return { attr: () => selector.date };
              }
              return { text: () => '', trim: () => '', attr: () => '' };
            },
            text: () => selector.content || ''
          };
        }
        
        // String selectors for initially finding elements
        if (selector === 'section.comments-section') {
          return {
            length: 1,
            find: () => ({
              length: mockComments.length,
              each: (callback: (index: number, element: any) => void) => {
                mockComments.forEach((comment, index) => {
                  callback(index, comment); // Pass the raw comment object
                })
              }
            })
          };
        }
        
        if (selector === 'a.pagination-item') {
          return {
            length: 0,
            filter: () => ({ length: 0 })
          };
        }
        
        return { length: 0 };
      };
      
      // Add other needed methods to the $ function
      return $;
    }
  };
});

describe('CurseForge Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  describe('fetchCurseforgeComments', () => {
    test('should fetch and parse comments from CurseForge', async () => {
      // Mock axios response for the page
      (mockedAxios.get as any).mockResolvedValue({
        status: 200,
        data: '<html><body><section class="comments-section">Comments content</section></body></html>'
      });
      
      const comments = await fetchCurseforgeComments();
      
      expect(comments).toHaveLength(2);
      expect(comments[0].id).toBe('123');
      expect(comments[0].author).toBe('TestUser1');
      expect(comments[1].id).toBe('456');
      expect(comments[1].author).toBe('TestUser2');
    });
    
    test('should handle API errors', async () => {
      // Mock axios error
      mockedAxios.get.mockRejectedValue(new Error('Network Error'));
      
      const comments = await fetchCurseforgeComments();
      
      expect(comments).toHaveLength(0);
    });
  });
}); 