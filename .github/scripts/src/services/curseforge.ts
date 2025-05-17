import axios from 'axios';
import * as cheerio from 'cheerio';
import puppeteer from 'puppeteer';
import type { Page, LaunchOptions } from 'puppeteer';
import config from '../config';
import { Comment } from '../types';
import * as logger from '../utils/logger';
import * as fs from 'fs';
import * as path from 'path';

/**
 * Browser interaction utilities
 */
class Browser {
  /**
   * Configure browser options
   */
  static configurePuppeteer(): LaunchOptions {
    // Define options with any to bypass strict typing issues
    const options: any = {
      headless: true,
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

    if (process.env.GITHUB_ACTIONS) {
      logger.debug('Running in GitHub Actions, using pre-installed Chrome');
      logger.debug(`GITHUB_ACTIONS=${process.env.GITHUB_ACTIONS}`);
      logger.debug(`PUPPETEER_EXECUTABLE_PATH=${process.env.PUPPETEER_EXECUTABLE_PATH || 'not set'}`);
      
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
    return options;
  }

  /**
   * Setup page configuration
   */
  static async setupPage(page: Page): Promise<void> {
    // Set user agent
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36');
    
    // Set up console logging
    page.on('console', (msg: any) => logger.debug(`BROWSER CONSOLE: ${msg.text()}`));
    page.on('pageerror', (error: Error) => logger.debug(`BROWSER PAGE ERROR: ${error.message}`));
    
    // Set longer timeouts
    page.setDefaultTimeout(120000); // 2 minutes
    page.setDefaultNavigationTimeout(120000); // 2 minutes
  }

  /**
   * Navigate to the target URL with precautions for Cloudflare
   */
  static async navigateToUrl(page: Page, url: string): Promise<void> {
    // First navigate to the main site to set cookies
    await page.goto('https://www.curseforge.com/', { waitUntil: 'networkidle2', timeout: 60000 });
    logger.debug('Loaded main page first to set cookies');
    
    // Wait before navigating to the actual page
    await page.waitForTimeout(3000);
    
    // Navigate to the target URL
    await page.goto(url, { waitUntil: 'networkidle2', timeout: 60000 });
    logger.debug('Page navigation completed');
    
    // Wait for protection to clear
    await page.waitForTimeout(5000);
    logger.debug('Waited for protection to clear');
  }

  /**
   * Take screenshots for debugging
   */
  static async captureDebugScreenshots(page: Page): Promise<void> {
    if (!process.env.DEBUG) return;
    
    const debugDir = Debug.ensureDebugDir();
    
    // Take full page screenshot
    await page.screenshot({ path: path.join(debugDir, 'page_screenshot.png'), fullPage: true });
    logger.debug('Saved page screenshot');
    
    // Take screenshots at different scroll positions
    for (let i = 1; i <= 3; i++) {
      await page.evaluate((scrollIndex: number) => {
        window.scrollTo(0, scrollIndex * document.body.scrollHeight / 4);
      }, i);
      await page.waitForTimeout(1000);
      await page.screenshot({ path: path.join(debugDir, `page_section${i}_screenshot.png`), fullPage: false });
      logger.debug(`Saved section ${i} screenshot`);
    }
  }
}

/**
 * Debug utilities
 */
class Debug {
  /**
   * Ensure debug directory exists
   */
  static ensureDebugDir(): string {
    const debugDir = path.join(process.cwd(), 'debug');
    if (!fs.existsSync(debugDir)) {
      fs.mkdirSync(debugDir, { recursive: true });
    }
    return debugDir;
  }

  /**
   * Save HTML for debugging
   */
  static saveHtml(html: string, filename: string): void {
    if (!process.env.DEBUG) return;
    
    const debugDir = Debug.ensureDebugDir();
    fs.writeFileSync(path.join(debugDir, filename), html);
    logger.debug(`Saved HTML to ${filename}`);
  }

  /**
   * Save JSON data for debugging
   */
  static saveJson(data: any, filename: string): void {
    if (!process.env.DEBUG) return;
    
    const debugDir = Debug.ensureDebugDir();
    fs.writeFileSync(path.join(debugDir, filename), JSON.stringify(data, null, 2));
    logger.debug(`Saved JSON to ${filename}`);
  }

  /**
   * Save text for debugging
   */
  static saveText(text: string, filename: string): void {
    if (!process.env.DEBUG) return;
    
    const debugDir = Debug.ensureDebugDir();
    fs.writeFileSync(path.join(debugDir, filename), text);
    logger.debug(`Saved text to ${filename}`);
  }
}

/**
 * Comment finding and extraction utilities
 */
class CommentExtraction {
  /**
   * Comment selectors to try in order
   */
  static readonly COMMENT_SELECTORS = [
    '.comment',
    '.comments-list li',
    '[data-comment]',
    '.comment-body',
    '.commentList .comment-item',
    '.commentThread .comment',
    '.userComments .commentBody',
    '.project-comments .comment',
    '.project-comments-list li',
    '.c-comment',
    '.card.comment',
    '.comment-card',
    '.comment-container',
    '[data-commentid]'
  ];

  /**
   * Extract ID from a comment element
   */
  static extractId($: cheerio.CheerioAPI, el: any): string {
    return (
      el.find('.num').text().trim() || 
      el.attr('data-comment-id') || 
      el.attr('id')?.replace('comment-', '') || 
      el.attr('data-commentid') || 
      ''
    ).replace('#', '');
  }

  /**
   * Extract author from a comment element
   */
  static extractAuthor($: cheerio.CheerioAPI, el: any): string {
    return (
      el.find('.author-name .ellipsis').text().trim() || 
      el.find('.username').text().trim() || 
      el.find('.comment-author').text().trim() || 
      el.find('.author').text().trim() || 
      el.find('[data-author]').text().trim() || 
      ''
    );
  }

  /**
   * Extract date from a comment element
   */
  static extractDate($: cheerio.CheerioAPI, el: any): string {
    return (
      el.find('.date span').text().trim() || 
      el.find('.comment-date').text().trim() || 
      el.find('time').text().trim() || 
      el.find('.timestamp').text().trim() || 
      ''
    );
  }

  /**
   * Extract content from a comment element
   */
  static extractContent($: cheerio.CheerioAPI, el: any): string {
    return (
      el.find('.text div p').text().trim() || 
      el.find('.comment-content').text().trim() || 
      el.find('.text').text().trim() || 
      el.find('.message').text().trim() || 
      el.find('.content').text().trim() || 
      ''
    );
  }
}

/**
 * Wait for comment elements to appear and interact with them if needed
 */
async function waitForComments(page: Page): Promise<boolean> {
  logger.debug('Waiting for comments to load...');
  
  try {
    // First, scroll down to trigger lazy loading
    logger.debug('Scrolling to trigger lazy loading...');
    for (let i = 0; i < 5; i++) {
      await page.evaluate((scrollIndex: number) => window.scrollTo(0, scrollIndex * 500), i);
      await page.waitForTimeout(1000);
    }
    
    // Try to locate comments with different selectors
    for (const selector of CommentExtraction.COMMENT_SELECTORS) {
      try {
        logger.debug(`Looking for comments with selector: ${selector}`);
        const element = await page.waitForSelector(selector, { timeout: 15000 });
        
        if (element) {
          logger.debug(`Found comments with selector: ${selector}`);
          await element.click({ offset: { x: 5, y: 5 } }).catch((e: Error) => logger.debug(`Click error: ${e}`));
          
          const count = await page.$$eval(selector, (elements: Element[]) => elements.length);
          logger.debug(`Found ${count} elements with selector ${selector}`);
          return true;
        }
      } catch (e) {
        logger.debug(`Selector ${selector} not found: ${e}`);
      }
    }
    
    // Try clicking comment-related buttons
    if (!(await tryClickingCommentButtons(page))) {
      // Try scrolling to reveal comments
      return await tryScrollInteraction(page);
    }
    
    return false;
  } catch (error) {
    logger.warn(`Error waiting for comments: ${error}`);
    return false;
  }
}

/**
 * Try clicking on buttons that might load comments
 */
async function tryClickingCommentButtons(page: Page): Promise<boolean> {
  const commentButtonSelectors = [
    'button:contains("Comments")', 
    'a:contains("Comments")', 
    '.comments-toggle',
    '.toggle-comments',
    '[data-tab="comments"]'
  ];
  
  for (const btnSelector of commentButtonSelectors) {
    try {
      logger.debug(`Looking for comment button: ${btnSelector}`);
      const button = await page.$(btnSelector);
      if (button) {
        logger.debug(`Found and clicking comment button: ${btnSelector}`);
        await button.click();
        await page.waitForTimeout(5000);
        
        // Check if any comments appeared after clicking
        for (const selector of CommentExtraction.COMMENT_SELECTORS) {
          const count = await page.$$eval(selector, (elements: Element[]) => elements.length);
          if (count > 0) {
            logger.debug(`After clicking button, found ${count} elements with selector ${selector}`);
            return true;
          }
        }
      }
    } catch (e) {
      logger.debug(`Button selector ${btnSelector} error: ${e}`);
    }
  }
  
  return false;
}

/**
 * Try scrolling to reveal comments
 */
async function tryScrollInteraction(page: Page): Promise<boolean> {
  logger.debug('Trying aggressive scroll interaction');
  
  // Scroll to bottom
  await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight));
  await page.waitForTimeout(3000);
  
  // Scroll back up incrementally
  for (let i = 10; i > 0; i--) {
    await page.evaluate((scrollPos: number) => window.scrollTo(0, document.body.scrollHeight * (scrollPos/10)), i);
    await page.waitForTimeout(1000);
    
    // Check if comments appeared
    for (const selector of CommentExtraction.COMMENT_SELECTORS) {
      const count = await page.$$eval(selector, (elements: Element[]) => elements.length);
      if (count > 0) {
        logger.debug(`After scrolling, found ${count} elements with selector ${selector}`);
        return true;
      }
    }
  }
  
  return false;
}

/**
 * Log page structure information to help with debugging
 */
function logPageStructure($: cheerio.CheerioAPI): void {
  const bodyClasses = $('body').attr('class') || 'none';
  logger.debug(`Body classes: ${bodyClasses}`);
  
  // Log common page elements
  const pageHeader = $('header.site-header').length;
  const pageFooter = $('footer.site-footer').length;
  logger.debug(`Found site header: ${pageHeader > 0}, Found site footer: ${pageFooter > 0}`);
  
  // Log content areas
  const mainContent = $('main').length;
  const contentAreas = $('.content-container, .content-area, .main-content').length;
  logger.debug(`Found main element: ${mainContent > 0}, Found content containers: ${contentAreas > 0}`);
  
  // Check for comment containers
  const commentContainers = $('.comments-container, .comments-wrapper, #comments, .comments-area').length;
  logger.debug(`Found comment containers: ${commentContainers > 0}`);
  
  // Look for comment-related divs
  if (commentContainers === 0) {
    const commentRelatedDivs = $('div[class*="comment"], div[id*="comment"]');
    logger.debug(`Found ${commentRelatedDivs.length} divs with "comment" in class/id`);
    
    if (commentRelatedDivs.length > 0) {
      const classes = new Set<string>();
      commentRelatedDivs.each((_, el) => {
        const className = $(el).attr('class') || '';
        className.split(/\s+/).forEach(c => {
          if (c) classes.add(c);
        });
      });
      logger.debug(`Classes of comment-related divs: ${Array.from(classes).join(', ')}`);
    }
  }
  
  // Check if likely a SPA
  const scriptCount = $('script').length;
  const hasReactOrVue = $.html().includes('reactjs') || $.html().includes('vue.js') || $.html().includes('__NEXT_DATA__');
  logger.debug(`Script count: ${scriptCount}, Likely SPA: ${hasReactOrVue}`);
}

/**
 * Extract comments from an HTML string
 */
function extractComments(html: string): Comment[] {
  const $ = cheerio.load(html);
  const comments: Comment[] = [];

  try {
    logger.debug('Looking for comments in the HTML...');
    logger.debug(`HTML length: ${html.length}`);
    
    const pageTitle = $('title').text().trim();
    logger.debug(`Page title: ${pageTitle}`);
    
    // Log page structure for debugging
    logPageStructure($);
    
    // Try all comment selectors
    for (const selector of CommentExtraction.COMMENT_SELECTORS) {
      logger.debug(`Trying selector: ${selector}`);
      const elements = $(selector);
      logger.debug(`Found ${elements.length} elements with selector "${selector}"`);
      
      if (elements.length === 0) continue;
      
      // Process each comment element
      elements.each((index, element) => {
        try {
          const el = $(element);
          
          // Log sample HTML for debugging
          if (index < 3) {
            const elHtml = el.html();
            const truncatedHtml = elHtml ? elHtml.substring(0, 200) + '...' : 'null';
            logger.debug(`Element ${index} HTML: ${truncatedHtml}`);
          }
          
          // Extract comment data
          const id = CommentExtraction.extractId($, el);
          const author = CommentExtraction.extractAuthor($, el);
          const date = CommentExtraction.extractDate($, el);
          const content = CommentExtraction.extractContent($, el);
          
          logger.debug(`Element ${index}: ID=${id}, Author=${author}, Content length=${content.length}`);
          
          if (id && author && content) {
            logger.debug(`Found comment from ${author} with ID ${id}: ${content.substring(0, 50)}...`);
            comments.push({ id, author, date, content });
          } else if (process.env.DEBUG) {
            // Debug missing fields
            const elHtml = el.html();
            const truncatedHtml = elHtml ? elHtml.substring(0, 300) + '...' : 'null';
            logger.debug(`Element HTML (missing fields): ${truncatedHtml}`);
            
            const allText = el.text().trim();
            if (allText) {
              logger.debug(`Element text content: ${allText.substring(0, 100)}...`);
            }
          }
        } catch (error) {
          logger.error(`Error extracting comment: ${error}`);
        }
      });
      
      // If we found comments, stop trying selectors
      if (comments.length > 0) {
        logger.info(`Successfully extracted ${comments.length} comments using selector "${selector}"`);
        break;
      }
    }
    
    // If no comments found, save debug info
    if (comments.length === 0) {
      logger.warn('No comments found in the HTML using any of the selectors');
      saveDebugInfo($, html);
    }
  } catch (error) {
    logger.error(`Error extracting comments: ${error}`);
  }

  return comments;
}

/**
 * Save debug information when no comments are found
 */
function saveDebugInfo($: cheerio.CheerioAPI, html: string): void {
  if (!process.env.DEBUG) return;
  
  // Save the full HTML
  Debug.saveHtml(html, 'no_comments_found.html');
  
  // Save HTML structure summary
  const bodyContent = $('body').html();
  const structureSummary = bodyContent ? bodyContent.substring(0, 5000) : 'No body content found';
  Debug.saveText(structureSummary, 'html_structure_summary.txt');
  
  // Save DOM structure analysis
  let domSummary = '';
  const bodyElement = $('body').get(0);
  if (bodyElement) {
    analyzeStructure(bodyElement, 0);
  }
  Debug.saveText(domSummary, 'dom_structure.txt');
  
  /**
   * Recursively analyze DOM structure
   */
  function analyzeStructure(element: cheerio.Element, depth = 0, maxDepth = 3, maxChildren = 5): void {
    if (!element || depth > maxDepth) return;
    
    const children = $(element).children();
    const childrenCount = children.length;
    
    const tagName = element.tagName?.toLowerCase() || 'unknown';
    const id = $(element).attr('id') || '';
    const className = $(element).attr('class') || '';
    
    const indentation = ' '.repeat(depth * 2);
    domSummary += `${indentation}<${tagName}${id ? ` id="${id}"` : ''}${className ? ` class="${className}"` : ''}>\n`;
    
    if (childrenCount > 0) {
      const displayCount = Math.min(childrenCount, maxChildren);
      
      for (let i = 0; i < displayCount; i++) {
        const child = children.get(i);
        if (child) {
          analyzeStructure(child, depth + 1, maxDepth, maxChildren);
        }
      }
      
      if (childrenCount > displayCount) {
        domSummary += `${indentation}  ... and ${childrenCount - displayCount} more children\n`;
      }
    }
  }
}

/**
 * Fetch a page using Puppeteer to bypass Cloudflare protection
 */
async function fetchWithPuppeteer(url: string): Promise<string> {
  logger.info('Attempting to fetch page with Puppeteer to bypass Cloudflare protection...');

  let browser;
  try {
    // Configure and launch browser
    const options = Browser.configurePuppeteer();
    browser = await puppeteer.launch(options);
    logger.debug('Browser launched successfully');
    
    // Setup page
    const page = await browser.newPage();
    logger.debug('New page created');
    await Browser.setupPage(page);
    
    // Navigate to the page
    await Browser.navigateToUrl(page, url);
    
    // Wait for comments to load and interact with the page
    await waitForComments(page);
    
    // Capture debug screenshots
    await Browser.captureDebugScreenshots(page);
    
    // Get and save page content
    const content = await page.content();
    logger.debug(`Retrieved page content (length: ${content.length})`);
    
    if (process.env.DEBUG) {
      Debug.saveHtml(content, 'puppeteer_page.html');
      
      try {
        const client = await page.target().createCDPSession();
        const cookies = await client.send('Network.getAllCookies');
        Debug.saveJson(cookies, 'puppeteer_cookies.json');
      } catch (error) {
        logger.debug(`Error saving cookies: ${error}`);
      }
    }
    
    return content;
  } catch (error) {
    logger.error(`Error fetching with Puppeteer: ${error}`);
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
 * Fetch comments from CurseForge
 */
export async function fetchCurseforgeComments(): Promise<Comment[]> {
  const allComments: Comment[] = [];
  
  try {
    // Ensure debug directory exists
    if (process.env.DEBUG) {
      Debug.ensureDebugDir();
    }

    logger.info('Fetching comments from CurseForge...');
    
    // Try to fetch the page, falling back to Puppeteer if needed
    let html: string;
    try {
      // First attempt: direct HTTP request
      logger.info('Attempting direct HTTP request...');
      const response = await axios.get(config.CURSEFORGE_URL, {
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
      
      // Check if we hit Cloudflare protection
      if (html.includes('cf-browser-verification') || html.includes('Just a moment...')) {
        logger.warn('Cloudflare protection detected, falling back to Puppeteer');
        html = await fetchWithPuppeteer(config.CURSEFORGE_URL);
      }
    } catch (error) {
      logger.warn(`Error with direct request: ${error}, falling back to Puppeteer`);
      html = await fetchWithPuppeteer(config.CURSEFORGE_URL);
    }
    
    // Save the HTML for debugging
    if (process.env.DEBUG) {
      Debug.saveHtml(html, 'curseforge_page.html');
    }

    // Extract comments from the HTML
    const comments = extractComments(html);
    
    if (comments.length === 0) {
      logger.warn('No comments found on page');
    } else {
      logger.info(`Found ${comments.length} comments`);
      allComments.push(...comments);
    }

    logger.info(`Found a total of ${allComments.length} comments from CurseForge`);
  } catch (error) {
    logger.error(`Error fetching comments: ${error}`);
  }

  return allComments;
} 