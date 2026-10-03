import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// ---- Types ----

export interface User {
  id: number;
  name: string;
  email: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  name: string;
  email: string;
}

export interface ShortUrl {
  id: number;
  originalUrl: string;
  shortCode: string;
  userId: number;
  shortUrl: string;
  createdAt: string;
}

export interface CreateUrlRequest {
  originalUrl: string;
  userId: number;
}

// ---- User APIs ----

export const getUsers = (): Promise<User[]> =>
  apiClient.get<User[]>('/api/users').then((r) => r.data);

export const createUser = (data: CreateUserRequest): Promise<User> =>
  apiClient.post<User>('/api/users', data).then((r) => r.data);

export const deleteUser = (id: number): Promise<void> =>
  apiClient.delete(`/api/users/${id}`).then(() => undefined);

// ---- URL APIs ----

export const getUrls = (): Promise<ShortUrl[]> =>
  apiClient.get<ShortUrl[]>('/api/urls').then((r) => r.data);

export const createUrl = (data: CreateUrlRequest): Promise<ShortUrl> =>
  apiClient.post<ShortUrl>('/api/urls', data).then((r) => r.data);

export const deleteUrl = (id: number): Promise<void> =>
  apiClient.delete(`/api/urls/${id}`).then(() => undefined);
