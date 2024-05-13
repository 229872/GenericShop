import './App.css'
import NavigationBar from './pages/NavigationBar';
import './utils/i18n'; 
import { Toaster } from 'sonner'
import RoutingWithSessionDialogs from './components/singleuse/RoutingWithSessionDialogs';
import { useState } from 'react';
import Progress from './components/singleuse/Progress';



function App() {
  const [ loading, setLoading ] = useState<boolean>(false)

  return <>
    <Toaster position='top-right' richColors style={{marginTop: '45px'}} />
    <NavigationBar />
    <Progress loading={loading} />
    <RoutingWithSessionDialogs setLoading={setLoading} />
  </> 
}

export default App;
