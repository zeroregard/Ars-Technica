const { containsBugKeywords } = require('../src/services/analyzer');

describe('Analyzer Service', () => {
  describe('containsBugKeywords', () => {
    test('should identify bug keywords', () => {
      const bugTexts = [
        'I found a bug where the game crashes',
        'There is an issue with recipe display',
        'The game crashes when I use this mod',
        'This mod is broken',
        'I cant see recipes in REI',
        'Recipes not showing in JEI',
        'Crash on server startup',
        'Error when loading'
      ];
      
      bugTexts.forEach(text => {
        const result = containsBugKeywords(text);
        expect(result).toBe(true);
      });
    });
    
    test('should not flag non-bug comments', () => {
      const nonBugTexts = [
        'I love this mod',
        'Thank you for creating this',
        'Looking forward to more updates',
        'This is my favorite mod',
        'Great job on the latest release'
      ];
      
      nonBugTexts.forEach(text => {
        expect(containsBugKeywords(text)).toBe(false);
      });
    });
  });
}); 