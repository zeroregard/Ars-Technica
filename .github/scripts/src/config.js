/**
 * Configuration for the Curseforge comment processor
 */
module.exports = {
  GITHUB_REPOSITORY: process.env.GITHUB_REPOSITORY || '',
  REPO_OWNER: process.env.GITHUB_REPOSITORY ? process.env.GITHUB_REPOSITORY.split('/')[0] : '',
  REPO_NAME: process.env.GITHUB_REPOSITORY ? process.env.GITHUB_REPOSITORY.split('/')[1] : '',
  GITHUB_TOKEN: process.env.GITHUB_TOKEN,
  OPENAI_API_KEY: process.env.OPENAI_API_KEY,
  CURSEFORGE_URL: 'https://www.curseforge.com/minecraft/mc-mods/ars-technica/comments'
}; 