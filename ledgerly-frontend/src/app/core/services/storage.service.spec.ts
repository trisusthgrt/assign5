import { TestBed } from '@angular/core/testing';
import { StorageService } from './storage.service';

describe('StorageService', () => {
  let service: StorageService;
  let mockLocalStorage: { [key: string]: string };

  beforeEach(() => {
    // Create a fresh mock localStorage for each test
    mockLocalStorage = {};
    
    // Mock localStorage methods
    spyOn(localStorage, 'getItem').and.callFake((key: string) => {
      return mockLocalStorage[key] || null;
    });
    
    spyOn(localStorage, 'setItem').and.callFake((key: string, value: string) => {
      mockLocalStorage[key] = value;
    });
    
    spyOn(localStorage, 'removeItem').and.callFake((key: string) => {
      delete mockLocalStorage[key];
    });
    
    spyOn(localStorage, 'clear').and.callFake(() => {
      mockLocalStorage = {};
    });

    TestBed.configureTestingModule({
      providers: [StorageService]
    });
    service = TestBed.inject(StorageService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('setItem', () => {
    it('should store string value in localStorage', () => {
      service.setItem('testKey', 'testValue');
      expect(localStorage.setItem).toHaveBeenCalledWith('testKey', '"testValue"');
    });

    it('should store number value in localStorage', () => {
      service.setItem('testKey', 123);
      expect(localStorage.setItem).toHaveBeenCalledWith('testKey', '123');
    });

    it('should store boolean value in localStorage', () => {
      service.setItem('testKey', true);
      expect(localStorage.setItem).toHaveBeenCalledWith('testKey', 'true');
    });

    it('should store object value in localStorage', () => {
      const testObj = { name: 'test', value: 123 };
      service.setItem('testKey', testObj);
      expect(localStorage.setItem).toHaveBeenCalledWith('testKey', JSON.stringify(testObj));
    });
  });

  describe('getItem', () => {
    it('should retrieve string value from localStorage', () => {
      service.setItem('testKey', 'testValue');
      const result = service.getItem('testKey');
      expect(result).toBe('testValue');
    });

    it('should retrieve number value from localStorage', () => {
      service.setItem('testKey', 123);
      const result = service.getItem('testKey');
      expect(result).toBe(123);
    });

    it('should retrieve boolean value from localStorage', () => {
      service.setItem('testKey', true);
      const result = service.getItem('testKey');
      expect(result).toBe(true);
    });

    it('should retrieve object value from localStorage', () => {
      const testObj = { name: 'test', value: 123 };
      service.setItem('testKey', testObj);
      const result = service.getItem('testKey');
      expect(result).toEqual(testObj);
    });

    it('should return null when key does not exist', () => {
      const result = service.getItem('nonexistentKey');
      expect(result).toBeNull();
    });

    it('should return null when localStorage is not available', () => {
      // Temporarily disable localStorage by making getItem throw
      const originalGetItem = localStorage.getItem;
      localStorage.getItem = jasmine.createSpy('getItem').and.throwError('localStorage not available');
      
      const result = service.getItem('testKey');
      expect(result).toBeNull();
      
      // Restore original
      localStorage.getItem = originalGetItem;
    });
  });

  describe('removeItem', () => {
    it('should remove item from localStorage', () => {
      service.setItem('testKey', 'testValue');
      service.removeItem('testKey');
      expect(localStorage.removeItem).toHaveBeenCalledWith('testKey');
      
      const result = service.getItem('testKey');
      expect(result).toBeNull();
    });

    it('should not throw error when removing non-existent key', () => {
      expect(() => service.removeItem('nonexistentKey')).not.toThrow();
    });
  });

  describe('clear', () => {
    it('should clear all items from localStorage', () => {
      service.setItem('key1', 'value1');
      service.setItem('key2', 'value2');
      
      service.clear();
      expect(localStorage.clear).toHaveBeenCalled();
      
      expect(service.getItem('key1')).toBeNull();
      expect(service.getItem('key2')).toBeNull();
    });
  });

  describe('browser detection', () => {
    it('should detect browser environment correctly', () => {
      // localStorage should be available in browser environment
      expect(localStorage.getItem).toBeDefined();
      expect(localStorage.setItem).toBeDefined();
      expect(localStorage.removeItem).toBeDefined();
      expect(localStorage.clear).toBeDefined();
    });
  });
});
