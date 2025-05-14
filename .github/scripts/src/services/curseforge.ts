import axios from 'axios';
import * as cheerio from 'cheerio';
import config from '../config';
import { Comment } from '../types';
import * as logger from '../utils/logger';

/**
 * Fetch comments from CurseForge
 */
export async function fetchCurseforgeComments(): Promise<Comment[]> {
  const allComments: Comment[] = [];
  let page = 1;
  
  try {
    while (true) {
      logger.info(`Fetching page ${page} of comments from CurseForge...`);
      
      const response = await axios.get(`${config.CURSEFORGE_URL}?page=${page}`);
      const $ = cheerio.load(response.data);
      
      const commentsSection = $('section.comments-section');
      
      if (!commentsSection.length) {
        logger.warn(`No comments section found on page ${page}`);
        break;
      }
      
      const commentElements = commentsSection.find('li.comment');
      
      if (!commentElements.length) {
        logger.info(`No more comments found on page ${page}`);
        break;
      }
      
      let pageCommentsCount = 0;
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
            pageCommentsCount++;
            logger.debug(`Found comment ${id} by ${author} from ${date}`);
          } else {
            logger.warn('Found comment without ID, skipping');
          }
        } catch (error) {
          logger.error(`Error parsing comment`, error instanceof Error ? error : new Error(String(error)));
        }
      });
      
      logger.info(`Processed ${pageCommentsCount} comments on page ${page}`);
      
      // Check if there's a next page
      const nextPage = $('a.pagination-item').filter((_, el) => $(el).text() === 'Next');
      
      if (!nextPage.length) {
        logger.debug('No next page link found, ending pagination');
        break;
      }
      
      page++;
    }
    
    logger.info(`Found a total of ${allComments.length} comments from CurseForge`);
  } catch (error) {
    if (axios.isAxiosError(error)) {
      logger.error(`Error fetching comments from CurseForge: ${error.message}`, error);
      if (error.response) {
        logger.error(`API Response: ${error.response.status}`, error);
      }
    } else {
      logger.error('Unexpected error fetching comments', error instanceof Error ? error : new Error(String(error)));
    }
  }
  
  return allComments;
} 