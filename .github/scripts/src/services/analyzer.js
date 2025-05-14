const config = require('../config');

let openai = null;
if (config.OPENAI_API_KEY) {
  try {
    const { OpenAI } = require('openai');
    openai = new OpenAI({
      apiKey: config.OPENAI_API_KEY
    });
    console.log('OpenAI client initialized successfully');
  } catch (error) {
    console.warn(`OpenAI module not available: ${error.message}`);
    console.log('Will use keyword-based detection only');
  }
} else {
  console.log('No OpenAI API key provided, using keyword-based detection only');
}

/**
 * Check if text contains any Minecraft mod bug report keywords
 */
function containsBugKeywords(text) {
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
      return true;
    }
  }
  
  // Check for specific phrases
  for (const phrase of minecraftBugPhrases) {
    if (lowercaseText.includes(phrase)) {
      return true;
    }
  }
  
  return false;
}

/**
 * Check if a comment sounds like a bug report
 */
async function isBugReport(commentContent) {
  // Try OpenAI if API key is available
  if (openai) {
    try {
      console.log('Using OpenAI to classify comment');
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
      return answer.includes('yes');
    } catch (error) {
      console.error(`Error calling OpenAI API: ${error.message}`);
      console.log('Falling back to keyword detection');
    }
  }
  
  // If OpenAI failed or is not available, use keyword detection
  console.log('Using keyword detection to classify comment');
  return containsBugKeywords(commentContent);
}

module.exports = {
  isBugReport,
  containsBugKeywords
}; 