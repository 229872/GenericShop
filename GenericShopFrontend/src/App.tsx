import './App.css'
import NavigationBar from './components/singleuse/NavigationBar';
import './utils/i18n'; 
import { Toaster } from 'sonner'
import RoutingWithSessionDialogs from './components/singleuse/RoutingWithSessionDialogs';
import { useState } from 'react';
import Progress from './components/singleuse/Progress';
import { isUserSignIn } from './services/sessionService';
import { Role } from './utils/types';
import { getActiveRole, getJwtToken, getRoles, saveActiveRole, saveLastActiveRole } from './services/tokenService';
import { getTotalAmountOfProducts } from './services/cartService';



function App() {
  const [ loading, setLoading ] = useState<boolean>(false)
  const [ isAuthenticated, setIsAuthenticated ] = useState<boolean>(isUserSignIn())
  const [ activeRole, setCurrentRole ] = useState<Role>(getActiveRole(getJwtToken()))
  const [ numberOfProductsInCart, setNumberOfProductsInCart ] = useState<number>(getTotalAmountOfProducts())

  const setActiveRole = (role: Role): void => {
    setCurrentRole(role)
    saveActiveRole(role)
    const roles: Role[] = getRoles(getJwtToken())
    if (roles.length > 1 && roles.includes(role)) {
      saveLastActiveRole(role)
    }
  }

  return <>
    <Toaster position='top-left' richColors closeButton expand style={{ marginTop: '70px' }}  offset={'20px'} />
    
    <NavigationBar 
      numberOfProductsInCart={numberOfProductsInCart}
      setIsAuthenticated={setIsAuthenticated}
      activeRole={activeRole}
      setActiveRole={setActiveRole}
    />

    <Progress loading={loading} />

    <RoutingWithSessionDialogs 
      setLoading={setLoading}
      isAuthenticated={isAuthenticated}
      setIsAuthenticated={setIsAuthenticated}
      activeRole={activeRole}
      setActiveRole={setActiveRole}
      setNumberOfProductsInCart={setNumberOfProductsInCart}
    />
  </> 
}

export default App;
