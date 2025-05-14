import config from '../config';
import { ChatMessage } from '../types';
import * as logger from '../utils/logger';

// Define the minimal OpenAI interface we need
interface OpenAIInstance {
  chat: {
    completions: {
      create: (params: {
        model: string;
        messages: ChatMessage[];
        max_tokens: number;
      }) => Promise<{
        choices: Array<{
          message: {
            content: string;
          };
        }>;
      }>;
    };
  };
}

let openai: OpenAIInstance | null = null;

if (config.OPENAI_API_KEY) {
  try {
    // Dynamic import for OpenAI
    // We'll be careful not to call this if it's not available
    const { OpenAI } = require('openai') as { OpenAI: new (options: { apiKey: string }) => OpenAIInstance };
    openai = new OpenAI({
      apiKey: config.OPENAI_API_KEY
    });
    logger.info('OpenAI client initialized successfully');
  } catch (error) {
    logger.warn(`OpenAI module not available: ${error instanceof Error ? error.message : String(error)}`);
    logger.info('Will use keyword-based detection only');
  }
} else {
  logger.info('No OpenAI API key provided, using keyword-based detection only');
}

/**
 * Check if text contains any Minecraft mod bug report keywords
 */
export function containsBugKeywords(text: string): boolean {
  const lowercaseText = text.toLowerCase();
  
  // Common bug report keywords
  const bugKeywords = [
    'bug', 'issue', 'problem', 'broken', 'not working', "doesn't work", "don't work",
    'crash', 'error', 'failed', 'exception', 'fix', 'glitch', 'help',
    "doesn't show", "can't find", 'not showing up', 'missing', 'doesn\'t appear',
    'not loading', 'cant craft', 'not showing in', 'recipe', 'no recipe', 'REI', 'JEI',
    'incompatible', 'conflict', 'weird behavior', 'unexpected', 'invisible'
  ];
  
  // Context-specific keywords for Minecraft mods
  const minecraftBugPhrases = [
    'cant see recipe', 'recipe not showing', 'items not appearing', 'mod conflict',
    'server crash', 'client crash', 'game freeze', 'screen freeze',
    'not showing up in rei', 'not showing in jei', 'recipe missing',
    'cant find how to make', 'how do i craft', 'how to craft',
    'doesnt load with', 'compatibility issue', 'mod compatibility'
  ];
  
  // Check for single keywords
  for (const keyword of bugKeywords) {
    if (lowercaseText.includes(keyword)) {
      logger.debug(`Comment contains bug keyword: "${keyword}"`);
      return true;
    }
  }
  
  // Check for specific phrases
  for (const phrase of minecraftBugPhrases) {
    if (lowercaseText.includes(phrase)) {
      logger.debug(`Comment contains Minecraft bug phrase: "${phrase}"`);
      return true;
    }
  }
  
  logger.debug('No bug keywords or phrases found in comment');
  return false;
}

/**
 * Check if a comment sounds like a bug report
 */
export async function isBugReport(commentContent: string): Promise<boolean> {
  // Try OpenAI if API key is available
  if (openai) {
    try {
      logger.info('Using OpenAI to classify comment as bug report');
      const response = await openai.chat.completions.create({
        model: 'gpt-3.5-turbo',
        messages: [
          {
            role: 'system',
            content: 'You are a bug report classifier for a Minecraft mod. Determine if the following comment sounds like a bug report or issue. Answer \'Yes\' or \'No\' only.'
          },
          {
            role: 'user',
            content: `Is this comment a bug report? Comment: ${commentContent}`
          }
        ],
        max_tokens: 5
      });
      
      const answer = response.choices[0].message.content.trim().toLowerCase();
      const isBug = answer.includes('yes');
      
      logger.info(`OpenAI classification result: ${isBug ? 'IS a bug report' : 'NOT a bug report'}`);
      return isBug;
    } catch (error) {
      logger.error('Error calling OpenAI API', error instanceof Error ? error : new Error(String(error)));
      logger.warn('Falling back to keyword detection for bug classification');
    }
  }
  
  // If OpenAI failed or is not available, use keyword detection
  logger.info('Using keyword detection to classify comment as bug report');
  const result = containsBugKeywords(commentContent);
  logger.info(`Keyword detection result: ${result ? 'IS a bug report' : 'NOT a bug report'}`);
  return result;
} 