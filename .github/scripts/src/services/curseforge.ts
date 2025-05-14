import axios from 'axios';
import * as cheerio from 'cheerio';
import config from '../config';
import { Comment } from '../types';

/**
 * Fetch comments from CurseForge
 */
export async function fetchCurseforgeComments(): Promise<Comment[]> {
  const allComments: Comment[] = [];
  let page = 1;
  
  try {
    while (true) {
      console.log(`Fetching page ${page} of comments...`);
      
      const response = await axios.get(`${config.CURSEFORGE_URL}?page=${page}`);
      const $ = cheerio.load(response.data);
      
      const commentsSection = $('section.comments-section');
      
      if (!commentsSection.length) {
        console.log(`No comments section found on page ${page}`);
        break;
      }
      
      const commentElements = commentsSection.find('li.comment');
      
      if (!commentElements.length) {
        console.log(`No more comments found on page ${page}`);
        break;
      }
      
      commentElements.each((_, element) => {
        try {
          const comment = $(element);
          const id = comment.attr('id')?.replace('comment-', '') || '';
          const author = comment.find('div.comment__author a').text().trim();
          
          // Get the date
          const dateElement = comment.find('time');
          const date = dateElement.attr('datetime') || 'Unknown Date';
          
          // Get the content
          const contentElement = comment.find('div.comment__body');
          const content = contentElement.text().trim();
          
          if (id) {
            allComments.push({
              id,
              author,
              date,
              content
            });
          }
        } catch (error) {
          console.error(`Error parsing comment: ${error instanceof Error ? error.message : String(error)}`);
        }
      });
      
      // Check if there's a next page
      const nextPage = $('a.pagination-item').filter((_, el) => $(el).text() === 'Next');
      
      if (!nextPage.length) {
        break;
      }
      
      page++;
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error(`Error fetching comments from CurseForge: ${error.message}`);
      if (error.response) {
        console.error(`Status: ${error.response.status}`);
      }
    } else {
      console.error(`Unexpected error: ${error instanceof Error ? error.message : String(error)}`);
    }
  }
  
  return allComments;
} 