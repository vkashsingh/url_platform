'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import { getUsers, getUrls } from '@/lib/api';

export default function DashboardPage() {
  const [userCount, setUserCount] = useState<number | null>(null);
  const [urlCount, setUrlCount] = useState<number | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([getUsers(), getUrls()])
      .then(([users, urls]) => {
        setUserCount(users.length);
        setUrlCount(urls.length);
      })
      .catch(() => setError('Could not connect to the API. Make sure the gateway is running.'));
  }, []);

  return (
    <div className="fade-in">
      {/* Header */}
      <div className="text-center mb-5">
        <h1 className="display-5 fw-bold mb-2" style={{ color: '#a5b4fc' }}>
          ⚡ URL Platform
        </h1>
        <p className="text-muted fs-6">
          A production-style URL shortening service
        </p>
      </div>

      {error && (
        <div className="alert alert-danger" role="alert" id="dashboard-error">
          {error}
        </div>
      )}

      {/* Stat Cards */}
      <div className="row g-4 mb-5">
        <div className="col-md-6">
          <div className="stat-card text-center">
            <div className="stat-number" id="stat-users">
              {userCount === null ? '—' : userCount}
            </div>
            <div className="stat-label mt-1">Total Users</div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="stat-card text-center">
            <div className="stat-number" id="stat-urls">
              {urlCount === null ? '—' : urlCount}
            </div>
            <div className="stat-label mt-1">Shortened URLs</div>
          </div>
        </div>
      </div>

      {/* Quick Links */}
      <div className="row g-4">
        <div className="col-md-6">
          <div className="card h-100 p-4">
            <div className="card-body">
              <h5 className="card-title mb-2">👤 Manage Users</h5>
              <p className="text-muted small mb-3">
                Create, view and delete users of the platform.
              </p>
              <Link
                href="/users"
                className="btn btn-primary"
                id="btn-go-users"
              >
                Go to Users →
              </Link>
            </div>
          </div>
        </div>

        <div className="col-md-6">
          <div className="card h-100 p-4">
            <div className="card-body">
              <h5 className="card-title mb-2">🔗 Shorten URLs</h5>
              <p className="text-muted small mb-3">
                Shorten long URLs, manage links, and test redirects.
              </p>
              <Link
                href="/urls"
                className="btn btn-primary"
                id="btn-go-urls"
              >
                Go to URLs →
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
