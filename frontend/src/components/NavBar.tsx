'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';

export default function NavBar() {
  const pathname = usePathname();

  const isActive = (href: string) =>
    pathname === href ? 'nav-link active fw-semibold' : 'nav-link text-light';

  return (
    <nav
      className="navbar navbar-expand-lg navbar-dark"
      style={{
        background: 'linear-gradient(90deg, #0f172a 0%, #1e1b4b 100%)',
        borderBottom: '1px solid #334155',
        boxShadow: '0 2px 16px rgba(0,0,0,0.5)',
      }}
    >
      <div className="container">
        <Link className="navbar-brand" href="/">
          <span style={{ color: '#818cf8' }}>⚡</span> URL Platform
        </Link>

        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          id="navbar-toggler"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon" />
        </button>

        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto gap-1">
            <li className="nav-item">
              <Link id="nav-home" className={isActive('/')} href="/">
                Dashboard
              </Link>
            </li>
            <li className="nav-item">
              <Link id="nav-users" className={isActive('/users')} href="/users">
                Users
              </Link>
            </li>
            <li className="nav-item">
              <Link id="nav-urls" className={isActive('/urls')} href="/urls">
                URLs
              </Link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}
