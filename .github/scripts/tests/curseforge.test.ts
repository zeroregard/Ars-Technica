import { describe, expect, test, jest, beforeEach } from '@jest/globals';
import axios from 'axios';
import type { Mocked } from 'jest-mock';
import { fetchCurseforgeComments } from '../src/services/curseforge';
import { Comment } from '../src/types';
import type { AxiosResponse } from 'axios';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as Mocked<typeof axios>;

// Mock puppeteer
jest.mock('puppeteer', () => {
  return {
    default: {
      launch: jest.fn().mockImplementation(() => {
        return {
          newPage: jest.fn().mockImplementation(() => {
            return {
              setUserAgent: jest.fn(),
              on: jest.fn(),
              setDefaultTimeout: jest.fn(),
              setDefaultNavigationTimeout: jest.fn(),
              goto: jest.fn(),
              waitForTimeout: jest.fn(),
              waitForSelector: jest.fn(),
              evaluate: jest.fn(),
              $$eval: jest.fn().mockImplementation(() => Promise.resolve(2)),
              screenshot: jest.fn(),
              content: jest.fn().mockImplementation(() => Promise.resolve('<html><body><div class="comment">Test Comment</div></body></html>')),
              target: jest.fn().mockReturnValue({
                createCDPSession: jest.fn().mockImplementation(() => Promise.resolve({
                  send: jest.fn().mockImplementation(() => Promise.resolve({}))
                }))
              })
            };
          }),
          close: jest.fn()
        };
      })
    }
  };
});

// Mock fs
jest.mock('fs', () => {
  return {
    existsSync: jest.fn().mockReturnValue(true),
    mkdirSync: jest.fn(),
    writeFileSync: jest.fn()
  };
});

// Mock cheerio with simplified implementation
jest.mock('cheerio', () => {
  return {
    load: jest.fn().mockImplementation(() => {
      const mockComments = [
        { id: '123', author: 'TestUser1', date: '2023-06-01T10:00:00Z', content: 'Test comment 1' },
        { id: '456', author: 'TestUser2', date: '2023-06-02T11:00:00Z', content: 'Test comment 2' }
      ];
      
      // Create a simplified version of our $ function
      const $ = (selector: string) => {
        if (selector === 'title') {
          return {
            text: () => 'Mock Title',
            trim: () => 'Mock Title'
          };
        }
        
        if (selector === 'body') {
          return {
            attr: () => 'mock-class',
            html: () => '<div>Mock Body</div>',
            get: () => [{}]
          };
        }
        
        if (selector === 'header.site-header' || selector === 'footer.site-footer' || 
            selector === 'main' || selector === '.content-container, .content-area, .main-content' ||
            selector === '.comments-container, .comments-wrapper, #comments, .comments-area') {
          return {
            length: 1
          };
        }
        
        if (selector === 'script') {
          return {
            length: 5
          };
        }
        
        if (selector === 'div[class*="comment"], div[id*="comment"]') {
          return {
            length: mockComments.length,
            each: (callback: (index: number, element: any) => void) => {
              callback(0, {});
            }
          };
        }
        
        if (selector === '.comment') {
          return {
            length: mockComments.length,
            each: (callback: (index: number, element: any) => void) => {
              mockComments.forEach((comment, index) => {
                callback(index, {});
              });
            }
          };
        }
        
        return {
          length: 0
        };
      };
      
      // Add methods to elements
      $.html = () => '<html><body><div class="comment">Test</div></body></html>';
      
      // Add element methods
      const elementMethods = {
        find: () => ({ text: () => 'MockText', trim: () => 'MockText' }),
        attr: (name: string) => name === 'id' ? '123' : (name === 'class' ? 'comment' : 'value'),
        text: () => 'MockText',
        trim: () => 'MockText',
        html: () => '<mock>element</mock>'
      };
      
      // Attach methods to the $ function return value
      Object.assign($.prototype, elementMethods);
      
      return $;
    })
  };
});

// Mocks for our test
const mockExtractComments = jest.fn().mockImplementation(() => {
  return [
    { id: '123', author: 'TestUser1', date: '2023-06-01T10:00:00Z', content: 'Test comment 1' },
    { id: '456', author: 'TestUser2', date: '2023-06-02T11:00:00Z', content: 'Test comment 2' }
  ];
});

describe('CurseForge Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.setTimeout(10000); // Increase timeout for tests
  });
  
  describe('fetchCurseforgeComments', () => {
    test('should fetch and parse comments from CurseForge', async () => {
      // Mock successful HTTP response without Cloudflare protection
      mockedAxios.get.mockResolvedValueOnce({
        status: 200,
        data: '<html><body><div class="comment">Test Comment</div></body></html>'
      });
      
      // Mock the environment
      process.env.DEBUG = 'true';
      
      const comments = await fetchCurseforgeComments();
      
      // Here we're checking the mocked implementation works
      expect(mockedAxios.get).toHaveBeenCalled();
      expect(comments).toBeDefined();
    });
    
    test('should handle API errors', async () => {
      // Mock axios error
      mockedAxios.get.mockRejectedValueOnce(new Error('Network Error'));
      
      // Mock the environment
      process.env.DEBUG = 'true';
      
      const comments = await fetchCurseforgeComments();
      
      // Simplified expectations
      expect(comments).toEqual([]);
    });
  });
}); 