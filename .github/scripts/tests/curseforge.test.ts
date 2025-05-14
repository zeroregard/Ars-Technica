import { describe, expect, test, jest, beforeEach } from '@jest/globals';
import axios from 'axios';
import * as cheerio from 'cheerio';
import { fetchCurseforgeComments } from '../src/services/curseforge';

// Mock axios
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

describe('Curseforge Service', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  describe('fetchCurseforgeComments', () => {
    test('should fetch and parse comments from Curseforge', async () => {
      // Mock HTML response for first page with comments
      const mockHtml = `
        <section class="comments-section">
          <li class="comment" id="comment-123">
            <div class="comment__author"><a>TestUser1</a></div>
            <time datetime="2023-06-01T10:00:00Z"></time>
            <div class="comment__body">This is a test comment</div>
          </li>
          <li class="comment" id="comment-456">
            <div class="comment__author"><a>TestUser2</a></div>
            <time datetime="2023-06-02T11:00:00Z"></time>
            <div class="comment__body">Another test comment</div>
          </li>
          <a class="pagination-item">Next</a>
        </section>
      `;
      
      // Mock HTML response for second page with no "Next" link
      const mockHtmlPage2 = `
        <section class="comments-section">
          <li class="comment" id="comment-789">
            <div class="comment__author"><a>TestUser3</a></div>
            <time datetime="2023-06-03T12:00:00Z"></time>
            <div class="comment__body">Last test comment</div>
          </li>
        </section>
      `;
      
      // Setup axios to return different responses for different pages
      // Using any type to bypass TypeScript's strict typing for this test
      (mockedAxios.get as any).mockImplementation((url: string) => {
        if (url.includes('page=1')) {
          return Promise.resolve({ data: mockHtml, status: 200 });
        } else if (url.includes('page=2')) {
          return Promise.resolve({ data: mockHtmlPage2, status: 200 });
        }
        return Promise.reject(new Error('Unexpected URL'));
      });
      
      const comments = await fetchCurseforgeComments();
      
      expect(comments).toHaveLength(3);
      expect(comments[0].id).toBe('123');
      expect(comments[0].author).toBe('TestUser1');
      expect(comments[0].content).toBe('This is a test comment');
      
      expect(comments[1].id).toBe('456');
      expect(comments[2].id).toBe('789');
      
      expect(mockedAxios.get).toHaveBeenCalledTimes(2);
    });
    
    test('should handle empty comments section', async () => {
      // Mock empty HTML response
      mockedAxios.get.mockResolvedValue({
        data: '<div>No comments found</div>',
        status: 200
      });
      
      const comments = await fetchCurseforgeComments();
      
      expect(comments).toHaveLength(0);
      expect(mockedAxios.get).toHaveBeenCalledTimes(1);
    });
    
    test('should handle API errors', async () => {
      // Mock axios error
      mockedAxios.get.mockRejectedValue(new Error('Network Error'));
      
      const comments = await fetchCurseforgeComments();
      
      expect(comments).toHaveLength(0);
      expect(mockedAxios.get).toHaveBeenCalledTimes(1);
    });
  });
}); 