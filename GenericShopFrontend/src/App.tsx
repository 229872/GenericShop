import './App.css'
import NavigationBar from './components/singleuse/NavigationBar';
import './utils/i18n'; 
import { Toaster } from 'sonner'
import RoutingWithSessionDialogs from './components/singleuse/RoutingWithSessionDialogs';
import { useState } from 'react';
import Progress from './components/singleuse/Progress';
import { isUserSignIn } from './services/sessionService';
import { Role } from './utils/types';
import { getActiveRole, getJwtToken } from './services/tokenService';



function App() {
  const [ loading, setLoading ] = useState<boolean>(false)
  const [ isAuthenticated, setIsAuthenticated ] = useState<boolean>(isUserSignIn())
  const [ activeRole, setActiveRole ] = useState<Role>(getActiveRole(getJwtToken()))

  return <>
    <Toaster position='top-left' richColors closeButton expand style={{ marginTop: '70px' }}  offset={'20px'} />
    <NavigationBar setIsAuthenticated={setIsAuthenticated} setActiveRole={setActiveRole} />
    <Progress loading={loading} />
    <RoutingWithSessionDialogs 
      setLoading={setLoading}
      isAuthenticated={isAuthenticated}
      setIsAuthenticated={setIsAuthenticated}
      activeRole={activeRole}
      setActiveRole={setActiveRole}
    />
  </> 
}

export default App;
