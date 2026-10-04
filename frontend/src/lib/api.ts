import axios from 'axios';

// NEXT_PUBLIC_API_URL is set in .env.local for local development (http://localhost:8080).
// In production (Kubernetes/Ingress), .env.local is excluded from the Docker build via
// .dockerignore, so this falls back to '' — making Axios use same-origin relative URLs.
// The browser then resolves /api/... against the Ingress host (e.g. http://url-platform.local).
const API_URL = process.env.NEXT_PUBLIC_API_URL || '';

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
