# Curseforge Comments Processor

This script processes comments from Curseforge and creates GitHub issues for bug reports.

## Structure

The codebase is organized into a modular structure:

```
.
├── process_comments.js   # Main entry point
├── package.json          # NPM package configuration
├── src
│   ├── config.js         # Configuration values
│   ├── index.js          # Main orchestration logic
│   └── services
│       ├── analyzer.js   # Bug report detection service
│       ├── curseforge.js # Curseforge API service
│       └── github.js     # GitHub API service
└── tests
    ├── analyzer.test.js  # Tests for analyzer service
    ├── curseforge.test.js # Tests for curseforge service
    └── github.test.js    # Tests for github service
```

## Setup

1. Clone the repository
2. Install dependencies:
   ```
   cd .github/scripts
   npm ci
   ```
3. Optionally install OpenAI (if you have an API key):
   ```
   npm install openai
   ```

## Usage

Run the script:
```
node process_comments.js
```

Or use it via GitHub workflow (automatically runs daily).

## Development

Run tests:
```
npm test
```

Watch mode for development:
```
npm run test:watch
```

## Configuration

The script uses the following environment variables:

- `GITHUB_REPOSITORY`: The GitHub repository in the format `owner/repo`
- `GITHUB_TOKEN`: GitHub API token with permissions to create issues
- `OPENAI_API_KEY`: (Optional) OpenAI API key for better bug detection

## How It Works

1. Fetches all existing GitHub issues to extract previously processed comment IDs
2. Scrapes comments from the Curseforge page
3. For each new comment:
   - Checks if it sounds like a bug report (using OpenAI or keyword detection)
   - If it's a bug report, creates a GitHub issue with the comment details 