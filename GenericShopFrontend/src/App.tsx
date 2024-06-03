import './App.css'
import NavigationBar from './components/singleuse/NavigationBar';
import './utils/i18n'; 
import { Toaster } from 'sonner'
import RoutingWithSessionDialogs from './components/singleuse/RoutingWithSessionDialogs';
import { useState } from 'react';
import Progress from './components/singleuse/Progress';
import { isUserSignIn } from './services/sessionService';



function App() {
  const [ loading, setLoading ] = useState<boolean>(false)
  const [ isAuthenticated, setIsAuthenticated ] = useState<boolean>(isUserSignIn())

  return <>
    <Toaster position='top-left' richColors closeButton expand style={{ marginTop: '60px' }}  offset={'40px'} />
    <NavigationBar setIsAuthenticated={setIsAuthenticated} />
    <Progress loading={loading} />
    <RoutingWithSessionDialogs setLoading={setLoading} isAuthenticated={isAuthenticated} setIsAuthenticated={setIsAuthenticated} />
  </> 
}

export default App;
