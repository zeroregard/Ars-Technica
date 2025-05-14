import { AxiosResponse } from 'axios';
import { jest } from '@jest/globals';

export interface MockResponse {
  data: any;
  status: number;
}

export interface MockedAxios {
  get: jest.Mock;
  post: jest.Mock;
}

export interface IssueParamMock {
  title: string;
  body: string;
  labels: string[];
}

export type MockedFunction<T> = T & { mock: jest.Mock['mock'] };
export type MockedPromiseResponse = Promise<MockResponse>; 