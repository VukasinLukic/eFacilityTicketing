import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ROLE_LABELS } from '../utils/labels';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav className="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
      <div className="flex items-center gap-6">
        <span className="font-bold text-blue-600 text-lg">eFacility</span>
        <div className="flex items-center gap-4 text-sm">
          <NavLink
            to="/dashboard"
            className={({ isActive }) =>
              isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-gray-900'
            }
          >
            Kontrolna tabla
          </NavLink>
          <NavLink
            to="/tickets"
            className={({ isActive }) =>
              isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-gray-900'
            }
          >
            Tiketi
          </NavLink>
          {user?.role === 'TENANT' && (
            <NavLink
              to="/tickets/create"
              className={({ isActive }) =>
                isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-gray-900'
              }
            >
              Novi tiket
            </NavLink>
          )}
          {user?.role === 'MANAGER' && (
            <>
              <NavLink
                to="/buildings"
                end
                className={({ isActive }) =>
                  isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-gray-900'
                }
              >
                Zgrade
              </NavLink>
              <NavLink
                to="/buildings/table"
                className={({ isActive }) =>
                  isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-gray-900'
                }
              >
                Zgrade (tabela)
              </NavLink>
            </>
          )}
        </div>
      </div>
      <div className="flex items-center gap-3 text-sm">
        <span className="text-gray-500">
          {user?.firstName} {user?.lastName}
          <span className="ml-1 text-xs text-gray-400">
            ({user?.role ? ROLE_LABELS[user.role] : ''})
          </span>
        </span>
        <button
          onClick={handleLogout}
          className="text-red-500 hover:text-red-700 font-medium transition-colors"
        >
          Odjava
        </button>
      </div>
    </nav>
  );
}
