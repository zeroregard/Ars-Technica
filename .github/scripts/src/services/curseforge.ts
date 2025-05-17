import axios from 'axios';
import * as cheerio from 'cheerio';
import puppeteer from 'puppeteer';
import config from '../config';
import { Comment } from '../types';
import * as logger from '../utils/logger';
import * as fs from 'fs';
import * as path from 'path';

/**
 * Fetch comments from CurseForge using puppeteer to bypass Cloudflare protection
 */
async function fetchWithPuppeteer(url: string): Promise<string> {
  logger.info('Attempting to fetch page with Puppeteer to bypass Cloudflare protection...');

  let browser;
  try {
    const options: {
      headless: boolean;
      args: string[];
      executablePath?: string;
    } = {
      headless: true, // Use headless mode
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--disable-accelerated-2d-canvas',
        '--no-first-run',
        '--no-zygote',
        '--disable-gpu'
      ]
    };

    // Check if we're in GitHub Actions
    if (process.env.GITHUB_ACTIONS) {
      logger.debug('Running in GitHub Actions, using pre-installed Chrome');
      logger.debug(`GITHUB_ACTIONS=${process.env.GITHUB_ACTIONS}`);
      logger.debug(`PUPPETEER_EXECUTABLE_PATH=${process.env.PUPPETEER_EXECUTABLE_PATH || 'not set'}`);
      logger.debug(`Runner Environment: Node ${process.version}`);
      
      // Try to check if Chrome is available
      try {
        const { execSync } = require('child_process');
        const chromePath = '/usr/bin/google-chrome';
        const result = execSync(`ls -la ${chromePath} 2>&1 || echo "Chrome not found"`).toString();
        logger.debug(`Chrome check: ${result.trim()}`);
      } catch (error) {
        logger.debug(`Error checking Chrome: ${error}`);
      }
      
      options.executablePath = process.env.PUPPETEER_EXECUTABLE_PATH || '/usr/bin/google-chrome';
    }

    logger.debug(`Puppeteer options: ${JSON.stringify(options)}`);
    browser = await puppeteer.launch(options);
    logger.debug('Puppeteer browser launched successfully');
    
    const page = await browser.newPage();
    logger.debug('New page created');
    
    // Set a realistic user agent
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36');
    logger.debug('User agent set');
    
    logger.debug(`Navigating to ${url}...`);
    await page.goto(url, { waitUntil: 'networkidle2', timeout: 60000 });
    logger.debug('Page navigation completed');
    
    // Wait for any protection to clear
    await page.waitForTimeout(3000);
    logger.debug('Waited for protection to clear');
    
    // Wait for comments to load
    logger.debug('Waiting for comments to load...');
    try {
      // Try various comment container selectors
      const selectors = [
        '.comments-list',
        '.comment',
        '[data-comments-container]',
        '.commentList',
        '.comments'
      ];
      
      // Wait for at least one of the selectors to appear
      let commentsFound = false;
      for (const selector of selectors) {
        try {
          logger.debug(`Looking for comments with selector: ${selector}`);
          
          // Wait for a maximum of 10 seconds for the comments to load
          await page.waitForSelector(selector, { timeout: 10000 });
          logger.debug(`Found comments with selector: ${selector}`);
          commentsFound = true;
          break;
        } catch (e) {
          logger.debug(`Selector ${selector} not found on page`);
        }
      }
      
      if (!commentsFound) {
        logger.warn('Could not find comments container with any selector, proceeding anyway');
        // Try scrolling to reveal lazy-loaded content
        logger.debug('Scrolling page to reveal any lazy-loaded content...');
        await page.evaluate(() => {
          window.scrollBy(0, 1000);
        });
        await page.waitForTimeout(2000);
      } else {
        logger.info('Comments container found on page');
      }
    } catch (error) {
      logger.warn(`Error waiting for comments: ${error}. Will continue with page extraction.`);
    }
    
    // Take a screenshot for debugging
    if (process.env.DEBUG) {
      const debugDir = path.join(process.cwd(), 'debug');
      if (!fs.existsSync(debugDir)) {
        fs.mkdirSync(debugDir, { recursive: true });
      }
      await page.screenshot({ path: path.join(debugDir, 'page_screenshot.png'), fullPage: true });
      logger.debug('Saved page screenshot to debug/page_screenshot.png');
    }
    
    // Get the page content
    const content = await page.content();
    logger.debug(`Retrieved page content (length: ${content.length})`);
    
    // Save the HTML for debugging purposes
    if (process.env.DEBUG) {
      const debugDir = path.join(process.cwd(), 'debug');
      if (!fs.existsSync(debugDir)) {
        fs.mkdirSync(debugDir, { recursive: true });
      }
      fs.writeFileSync(path.join(debugDir, 'puppeteer_page.html'), content);
      logger.debug('Saved puppeteer page HTML to debug/puppeteer_page.html');
      
      // Save response headers
      try {
        const client = await page.target().createCDPSession();
        const responseHeaders = await client.send('Network.getAllCookies');
        fs.writeFileSync(path.join(debugDir, 'puppeteer_cookies.json'), JSON.stringify(responseHeaders, null, 2));
        logger.debug('Saved puppeteer cookies to debug/puppeteer_cookies.json');
      } catch (error) {
        logger.debug(`Error saving headers: ${error}`);
      }
    }
    
    return content;
  } catch (error) {
    logger.error(`Error fetching page with Puppeteer: ${error}`);
    logger.error(`Error details: ${error instanceof Error ? error.stack : 'Unknown error'}`);
    throw error;
  } finally {
    if (browser) {
      await browser.close();
      logger.debug('Browser closed');
    }
  }
}

/**
 * Extract comments from HTML content
 */
function extractComments(html: string): Comment[] {
  const $ = cheerio.load(html);
  const comments: Comment[] = [];

  try {
    // Look for the comments section in the page
    // Based on the new HTML structure we observed in puppeteer_page.html
    logger.debug('Looking for comments in the HTML...');
    
    // Log the HTML structure for debugging
    logger.debug(`HTML length: ${html.length}`);
    
    // Check for common elements to see if the page loaded correctly
    const pageTitle = $('title').text().trim();
    logger.debug(`Page title: ${pageTitle}`);
    
    // Define possible selectors to try
    const possibleSelectors = [
      '.comment',                 // Original selector
      '.comments-list li',        // Alternative list format
      '[data-comment]',           // Data attribute format
      '.comment-body',            // Common comment class
      '.commentList .comment-item', // Nested format
      '.commentThread .comment',   // Threaded comments
      '.userComments .commentBody' // Another common pattern
    ];
    
    let foundComments = false;
    
    // Try each selector until we find comments
    for (const selector of possibleSelectors) {
      logger.debug(`Trying selector: ${selector}`);
      const elements = $(selector);
      logger.debug(`Found ${elements.length} elements with selector "${selector}"`);
      
      if (elements.length > 0) {
        // Found some elements, try to extract comments
        elements.each((index, element) => {
          try {
            const el = $(element);
            
            // Try different patterns for extracting data
            // Strategy 1: Standard structure
            let id = el.find('.num').text().trim() || 
                     el.attr('data-comment-id') || 
                     el.attr('id')?.replace('comment-', '') || '';
            
            let author = el.find('.author-name .ellipsis').text().trim() || 
                         el.find('.username').text().trim() || 
                         el.find('.comment-author').text().trim() || '';
            
            let date = el.find('.date span').text().trim() || 
                       el.find('.comment-date').text().trim() || 
                       el.find('time').text().trim() || '';
            
            let content = el.find('.text div p').text().trim() || 
                          el.find('.comment-content').text().trim() || 
                          el.find('.text').text().trim() || '';
            
            // Clean up IDs
            id = id.replace('#', '');
            
            // Log what we found
            logger.debug(`Element ${index} using "${selector}": ID=${id}, Author=${author}, Content length=${content.length}`);
            
            if (id && author && content) {
              logger.debug(`Found comment from ${author} with ID ${id}: ${content.substring(0, 50)}...`);
              comments.push({
                id,
                author,
                date,
                content
              });
              foundComments = true;
            } else {
              // If we found elements but couldn't extract data, log the HTML for debugging
              if (process.env.DEBUG) {
                logger.debug(`Element HTML: ${el.html()?.substring(0, 300)}...`);
              }
            }
          } catch (error) {
            logger.error(`Error extracting comment details with selector "${selector}": ${error}`);
          }
        });
        
        // If we found comments with this selector, break the loop
        if (comments.length > 0) {
          logger.info(`Successfully extracted ${comments.length} comments using selector "${selector}"`);
          break;
        }
      }
    }
    
    if (comments.length === 0) {
      logger.warn('No comments found in the HTML using any of the selectors');
      
      // When no comments found, save the HTML structure for deeper debugging
      if (process.env.DEBUG) {
        const debugDir = path.join(process.cwd(), 'debug');
        if (!fs.existsSync(debugDir)) {
          fs.mkdirSync(debugDir, { recursive: true });
        }
        
        // Save the HTML for more detailed inspection
        fs.writeFileSync(path.join(debugDir, 'no_comments_found.html'), html);
        logger.debug('Saved full HTML to debug/no_comments_found.html for inspection');
        
        // Save a summary of the HTML structure
        const bodyContent = $('body').html();
        const structureSummary = bodyContent ? bodyContent.substring(0, 5000) : 'No body content found';
        fs.writeFileSync(path.join(debugDir, 'html_structure_summary.txt'), structureSummary);
        logger.debug('Saved HTML structure summary to debug/html_structure_summary.txt');
      }
    }
  } catch (error) {
    logger.error(`Error extracting comments: ${error}`);
  }

  return comments;
}

/**
 * Fetch comments from CurseForge
 */
export async function fetchCurseforgeComments(): Promise<Comment[]> {
  const allComments: Comment[] = [];
  let page = 1;
  
  try {
    // Save the page content for debugging
    if (process.env.DEBUG) {
      const debugDir = path.join(process.cwd(), 'debug');
      if (!fs.existsSync(debugDir)) {
        fs.mkdirSync(debugDir);
      }
    }

    logger.info(`Fetching page ${page} of comments from CurseForge...`);
    
    // First attempt direct access - this will likely fail with Cloudflare protection
    let html: string;
    try {
      const response = await axios.get(`${config.CURSEFORGE_URL}`, {
        headers: {
          'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
          'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
          'Accept-Language': 'en-US,en;q=0.5',
          'Referer': 'https://www.curseforge.com/',
          'DNT': '1',
          'Connection': 'keep-alive',
          'Upgrade-Insecure-Requests': '1',
          'Cache-Control': 'max-age=0'
        },
        timeout: 30000
      });
      
      html = response.data;
      
      // Check if we received a Cloudflare challenge
      if (html.includes('cf-browser-verification') || html.includes('Just a moment...')) {
        logger.warn('Cloudflare protection detected, falling back to Puppeteer');
        html = await fetchWithPuppeteer(`${config.CURSEFORGE_URL}`);
      }
    } catch (error) {
      logger.warn(`Error fetching with axios: ${error}, falling back to Puppeteer`);
      html = await fetchWithPuppeteer(`${config.CURSEFORGE_URL}`);
    }
    
    // Save the HTML for debugging purposes
    if (process.env.DEBUG) {
      const debugDir = path.join(process.cwd(), 'debug');
      fs.writeFileSync(path.join(debugDir, 'curseforge_page.html'), html);
      logger.debug('Saved page HTML to debug/curseforge_page.html');
    }

    // Extract comments from the page
    const comments = extractComments(html);
    
    if (comments.length === 0) {
      logger.warn(`No comments section found on page ${page}`);
    } else {
      logger.info(`Found ${comments.length} comments on page ${page}`);
      allComments.push(...comments);
    }

    // For now, we'll only process the first page
    // In the future, we could implement pagination if needed
    // by looking for pagination buttons and fetching additional pages

    logger.info(`Found a total of ${allComments.length} comments from CurseForge`);
  } catch (error) {
    logger.error(`Error fetching comments from CurseForge: ${error}`);
  }

  return allComments;
} 