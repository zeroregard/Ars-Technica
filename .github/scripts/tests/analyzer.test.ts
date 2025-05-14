import { describe, expect, test } from '@jest/globals';
import { containsBugKeywords } from '../src/services/analyzer';

describe('Analyzer Service', () => {
  describe('containsBugKeywords', () => {
    test('should identify bug keywords', () => {
      const bugTexts: string[] = [
        'I found a bug where the game crashes',
        'There is an issue with recipe display',
        'Nothing is working for me',
        'The mod is broken',
        'I cant see any recipes in REI',
        'Recipes not showing up in JEI',
        'How do I craft the items? They are not in REI',
        "Hello! For some reason the recipes to make the different things in Ars Technica doesnt show up in REI or anywhere? Meaning I can't find how to make them :/"
      ];
      
      bugTexts.forEach(text => {
        expect(containsBugKeywords(text)).toBe(true);
      });
    });
    
    test('should not flag non-bug comments', () => {
      const nonBugTexts: string[] = [
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