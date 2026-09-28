'use client';

import { useEffect, useState } from 'react';
import {
  getUrls,
  createUrl,
  deleteUrl,
  getUsers,
  User,
  ShortUrl,
} from '@/lib/api';
import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export default function UrlsPage() {
  const [urls, setUrls] = useState<ShortUrl[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [createdUrl, setCreatedUrl] = useState<ShortUrl | null>(null);

  // Form state
  const [originalUrl, setOriginalUrl] = useState('');
  const [userId, setUserId] = useState('');
  const [formErrors, setFormErrors] = useState<{ originalUrl?: string; userId?: string }>({});

  const fetchData = async () => {
    try {
      const [urlData, userData] = await Promise.all([getUrls(), getUsers()]);
      setUrls(urlData);
      setUsers(userData);
    } catch {
      setError('Failed to load data. Is the gateway running?');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const validate = () => {
    const errs: { originalUrl?: string; userId?: string } = {};
    if (!originalUrl.trim()) {
      errs.originalUrl = 'URL is required';
    } else {
      try {
        new URL(originalUrl);
      } catch {
        errs.originalUrl = 'Enter a valid URL (including http:// or https://)';
      }
    }
    if (!userId) errs.userId = 'Please select a user';
    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setCreatedUrl(null);
    if (!validate()) return;

    setSubmitting(true);
    try {
      const result = await createUrl({ originalUrl, userId: Number(userId) });
      setCreatedUrl(result);
      setOriginalUrl('');
      setUserId('');
      setFormErrors({});
      setSuccess('Short URL created!');
      fetchData();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message || 'Failed to create URL.');
      } else {
        setError('Failed to create URL.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this shortened URL?')) return;
    setError('');
    try {
      await deleteUrl(id);
      setSuccess('URL deleted.');
      if (createdUrl?.id === id) setCreatedUrl(null);
      fetchData();
    } catch {
      setError('Failed to delete URL.');
    }
  };

  const shortUrlFull = (shortCode: string) => `${API_URL}/${shortCode}`;

  return (
    <div className="fade-in">
      <div className="page-header">
        <h1>🔗 URLs</h1>
      </div>

      {error && <div className="alert alert-danger" id="urls-error">{error}</div>}
      {success && !createdUrl && (
        <div className="alert alert-success" id="urls-success">{success}</div>
      )}

      <div className="row g-4">
        {/* Create URL Form */}
        <div className="col-lg-5">
          <div className="card p-4">
            <h5 className="card-title mb-4">Shorten a URL</h5>
            <form onSubmit={handleCreate} id="create-url-form" noValidate>
              <div className="mb-3">
                <label htmlFor="original-url" className="form-label">Long URL</label>
                <input
                  id="original-url"
                  type="url"
                  className={`form-control ${formErrors.originalUrl ? 'is-invalid' : ''}`}
                  placeholder="https://example.com/very/long/url"
                  value={originalUrl}
                  onChange={(e) => setOriginalUrl(e.target.value)}
                />
                {formErrors.originalUrl && (
                  <div className="invalid-feedback">{formErrors.originalUrl}</div>
                )}
              </div>

              <div className="mb-4">
                <label htmlFor="user-select" className="form-label">User</label>
                <select
                  id="user-select"
                  className={`form-select ${formErrors.userId ? 'is-invalid' : ''}`}
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                >
                  <option value="">Select a user…</option>
                  {users.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.name} ({u.email})
                    </option>
                  ))}
                </select>
                {formErrors.userId && (
                  <div className="invalid-feedback">{formErrors.userId}</div>
                )}
                {users.length === 0 && !loading && (
                  <div className="form-text text-warning small mt-1">
                    No users found. <a href="/users" className="text-warning">Create a user first.</a>
                  </div>
                )}
              </div>

              <button
                id="btn-shorten-url"
                type="submit"
                className="btn btn-primary w-100"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" />
                    Shortening…
                  </>
                ) : (
                  '⚡ Shorten URL'
                )}
              </button>
            </form>

            {/* Result */}
            {createdUrl && (
              <div className="mt-4 fade-in">
                <div className="form-label mb-2 text-success">✅ Short URL Created</div>
                <div className="short-url-display" id="short-url-result">
                  <a
                    href={shortUrlFull(createdUrl.shortCode)}
                    target="_blank"
                    rel="noopener noreferrer"
                    style={{ color: '#a5b4fc', textDecoration: 'none' }}
                  >
                    {shortUrlFull(createdUrl.shortCode)}
                  </a>
                </div>
                <div className="mt-2 d-flex gap-2">
                  <span className="badge-short-code">{createdUrl.shortCode}</span>
                  <button
                    className="btn btn-sm btn-outline-secondary"
                    onClick={() => navigator.clipboard.writeText(shortUrlFull(createdUrl.shortCode))}
                    id="btn-copy-url"
                  >
                    Copy
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* URLs Table */}
        <div className="col-lg-7">
          <div className="card p-4">
            <h5 className="card-title mb-4">
              All Shortened URLs{' '}
              <span className="badge rounded-pill" style={{ background: '#4f46e5', fontSize: '0.8rem' }}>
                {urls.length}
              </span>
            </h5>

            {loading ? (
              <div className="loading-overlay">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Loading…</span>
                </div>
              </div>
            ) : urls.length === 0 ? (
              <div className="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 24 24"><path d="M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z"/></svg>
                <p>No URLs shortened yet.</p>
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle" id="urls-table">
                  <thead>
                    <tr>
                      <th>Original URL</th>
                      <th>Short Code</th>
                      <th>Created</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {urls.map((u) => (
                      <tr key={u.id} className="fade-in">
                        <td
                          style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
                          title={u.originalUrl}
                        >
                          <a
                            href={u.originalUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-muted small"
                            style={{ textDecoration: 'none' }}
                          >
                            {u.originalUrl}
                          </a>
                        </td>
                        <td>
                          <a
                            href={shortUrlFull(u.shortCode)}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="badge-short-code"
                            style={{ textDecoration: 'none' }}
                            id={`short-code-${u.shortCode}`}
                          >
                            {u.shortCode}
                          </a>
                        </td>
                        <td className="text-muted small">
                          {new Date(u.createdAt).toLocaleDateString()}
                        </td>
                        <td>
                          <button
                            id={`btn-delete-url-${u.id}`}
                            className="btn btn-sm btn-danger"
                            onClick={() => handleDelete(u.id)}
                          >
                            Delete
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
