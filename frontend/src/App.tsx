import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import PrivateRoute from './routes/PrivateRoute';
import RoleRoute from './routes/RoleRoute';
import Layout from './components/Layout';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import NotFoundPage from './pages/NotFoundPage';
import DashboardPage from './pages/DashboardPage';
import TiketListPage from './pages/TiketListPage';
import TiketDetailPage from './pages/TiketDetailPage';
import CreateTiketPage from './pages/CreateTiketPage';
import ZgradePage from './pages/ZgradePage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route element={<PrivateRoute />}>
              <Route element={<Layout />}>
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/tickets" element={<TiketListPage />} />
                <Route path="/tickets/:id" element={<TiketDetailPage />} />

                <Route element={<RoleRoute allowedUlogas={['TENANT']} />}>
                  <Route path="/tickets/create" element={<CreateTiketPage />} />
                </Route>

                <Route element={<RoleRoute allowedUlogas={['MANAGER']} />}>
                  <Route path="/buildings" element={<ZgradePage />} />
                </Route>
              </Route>
            </Route>

            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="*" element={<NotFoundPage />} />
          </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}
