import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import type { Uloga } from '../types/korisnik.types';

interface RoleRouteProps {
  allowedUlogas: Uloga[];
}

export default function RoleRoute({ allowedUlogas }: RoleRouteProps) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (!allowedUlogas.includes(user.role)) return <Navigate to="/dashboard" replace />;
  return <Outlet />;
}
