'use client';

import { useEffect, useState } from 'react';
import { getUsers, createUser, deleteUser, User } from '@/lib/api';
import axios from 'axios';

export default function UsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Form state
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [formErrors, setFormErrors] = useState<{ name?: string; email?: string }>({});

  const fetchUsers = async () => {
    try {
      const data = await getUsers();
      setUsers(data);
    } catch {
      setError('Failed to load users.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const validate = () => {
    const errs: { name?: string; email?: string } = {};
    if (!name.trim()) errs.name = 'Name is required';
    if (!email.trim()) errs.email = 'Email is required';
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
      errs.email = 'Enter a valid email address';
    setFormErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (!validate()) return;

    setSubmitting(true);
    try {
      await createUser({ name, email });
      setName('');
      setEmail('');
      setFormErrors({});
      setSuccess('User created successfully!');
      fetchUsers();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        setError(err.response?.data?.message || 'Failed to create user.');
      } else {
        setError('Failed to create user.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this user? Their shortened URLs will also be removed.')) return;
    setError('');
    try {
      await deleteUser(id);
      setSuccess('User deleted.');
      fetchUsers();
    } catch {
      setError('Failed to delete user.');
    }
  };

  return (
    <div className="fade-in">
      <div className="page-header d-flex align-items-center gap-2">
        <h1>👤 Users</h1>
      </div>

      {error && <div className="alert alert-danger" id="users-error">{error}</div>}
      {success && <div className="alert alert-success" id="users-success">{success}</div>}

      <div className="row g-4">
        {/* Create User Form */}
        <div className="col-lg-4">
          <div className="card p-4">
            <h5 className="card-title mb-4">Create User</h5>
            <form onSubmit={handleCreate} id="create-user-form" noValidate>
              <div className="mb-3">
                <label htmlFor="user-name" className="form-label">Name</label>
                <input
                  id="user-name"
                  type="text"
                  className={`form-control ${formErrors.name ? 'is-invalid' : ''}`}
                  placeholder="Vikash Singh"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                />
                {formErrors.name && (
                  <div className="invalid-feedback">{formErrors.name}</div>
                )}
              </div>

              <div className="mb-4">
                <label htmlFor="user-email" className="form-label">Email</label>
                <input
                  id="user-email"
                  type="email"
                  className={`form-control ${formErrors.email ? 'is-invalid' : ''}`}
                  placeholder="vikash@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
                {formErrors.email && (
                  <div className="invalid-feedback">{formErrors.email}</div>
                )}
              </div>

              <button
                id="btn-create-user"
                type="submit"
                className="btn btn-primary w-100"
                disabled={submitting}
              >
                {submitting ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" />
                    Creating…
                  </>
                ) : (
                  'Create User'
                )}
              </button>
            </form>
          </div>
        </div>

        {/* Users Table */}
        <div className="col-lg-8">
          <div className="card p-4">
            <h5 className="card-title mb-4">
              All Users{' '}
              <span className="badge rounded-pill" style={{ background: '#4f46e5', fontSize: '0.8rem' }}>
                {users.length}
              </span>
            </h5>

            {loading ? (
              <div className="loading-overlay">
                <div className="spinner-border text-primary" role="status">
                  <span className="visually-hidden">Loading…</span>
                </div>
              </div>
            ) : users.length === 0 ? (
              <div className="empty-state">
                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="currentColor" viewBox="0 0 24 24"><path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/></svg>
                <p>No users yet. Create your first user!</p>
              </div>
            ) : (
              <div className="table-responsive">
                <table className="table table-hover align-middle" id="users-table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Created</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    {users.map((u) => (
                      <tr key={u.id} className="fade-in">
                        <td className="text-muted small">{u.id}</td>
                        <td className="fw-semibold">{u.name}</td>
                        <td className="text-muted">{u.email}</td>
                        <td className="text-muted small">
                          {new Date(u.createdAt).toLocaleDateString()}
                        </td>
                        <td>
                          <button
                            id={`btn-delete-user-${u.id}`}
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
